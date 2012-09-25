/*******************************************************************************
 * Copyright (C) 2010 Technische Universität Dresden
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 ******************************************************************************/

/**
 * contains code from com.android.mms.ui
 */
package de.tudresden.inf.rn.mobilis.magnetizer.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.Html.TagHandler;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import de.tudresden.inf.rn.mobilis.magnetizer.Const;
import de.tudresden.inf.rn.mobilis.magnetizer.R;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.MessageItems;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.RosterItems;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPMessage;

/**
 * Displays a Chat Screen that is capable of handling one conversation to a
 * single chat partner.
 * 
 * @author Istvan Koren
 */
public class ChatScreen extends Activity implements OnClickListener {

	private static final String TAG = "ChatScreen";

	private String mChatPartner;

	// views
	private ListView mLstMessages;
	private EditText mEdtEditor;
	private Button mBtnSend;

	// MXA members
	private MXAController mMXAController;
	private Messenger mAckMessenger;
	private Cursor mMessagesCursor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatscreen);

		// read properties
		Intent i = getIntent();
		// TODO as soon as we have Message DB, use Message.RECIPIENT instead
		mChatPartner = i.getStringExtra(Const.SELECTED_XMPPID);
		setTitle("Conversation with " + mChatPartner);

		// initialize members for UI elements.
		initResourceRefs();

		updateBtnSendState();

		// intialize MXA members
		mMXAController = MXAController.get();
		mAckMessenger = new Messenger(xmppResultHandler);

		initMessageList();
	}

	// ==========================================================
	// Interface methods
	// ==========================================================

	@Override
	public void onClick(View v) {
		if ((v == mBtnSend) && isReadyForSending()) {
			sendMessage();
		}
	}

	private final TextWatcher mEdtEditorWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			updateBtnSendState();
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	private final OnKeyListener mEdtEditorKeyListener = new OnKeyListener() {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if ((event.getAction() == KeyEvent.ACTION_DOWN)
					&& (keyCode == KeyEvent.KEYCODE_ENTER)
					&& !event.isShiftPressed()) {
				if (isReadyForSending()) {
					sendMessage();
				}
				return true;
			} else {
				return false;
			}
		}
	};

	// ==========================================================
	// Private methods
	// ==========================================================

	/**
	 * Initialize all UI elements from resources.
	 */
	private void initResourceRefs() {
		mLstMessages = (ListView) findViewById(R.id.cs_list_history);
		mEdtEditor = (EditText) findViewById(R.id.cs_edt_editor);
		mEdtEditor.setOnKeyListener(mEdtEditorKeyListener);
		mEdtEditor.addTextChangedListener(mEdtEditorWatcher);
		mEdtEditor.requestFocus();
		mBtnSend = (Button) findViewById(R.id.cs_btn_send);
		mBtnSend.setOnClickListener(this);
	}

	/**
	 * Sends message to chat partner.
	 */
	private void sendMessage() {
		XMPPMessage xMsg = new XMPPMessage();
		xMsg.type = XMPPMessage.TYPE_CHAT;
		xMsg.to = mChatPartner;
		xMsg.body = mEdtEditor.getText().toString();

		// send Message
		try {
			mMXAController.getXMPPService().sendMessage(mAckMessenger, 0, xMsg);
		} catch (RemoteException e) {
			sendFailed(0);
		}

		// Clear the text box.
		TextKeyListener.clear(mEdtEditor.getText());
	}

	private void sendFailed(int requestCode) {
		// TODO error handling
		Log.e("ChatScreen", "send failed with requestCode " + requestCode);
	}

	private boolean hasText() {
		return mEdtEditor.length() > 0;
	}

	private boolean isReadyForSending() {
		return hasText();
	}

	private void updateBtnSendState() {
		boolean enabled = false;
		if (isReadyForSending()) {
			enabled = true;
		}

		mBtnSend.setEnabled(enabled);
		mBtnSend.setFocusable(enabled);
	}

	private void initMessageList() {
		Log.i(TAG, "initializing message list");
		// Get a cursor with all messages to this user
		mMessagesCursor = getContentResolver().query(MessageItems.CONTENT_URI,
				null, null, null, MessageItems.DEFAULT_SORT_ORDER);
		startManagingCursor(mMessagesCursor);

		ListAdapter adapter = new SimpleCursorAdapter(this,
		// Use a template that displays a text view
				android.R.layout.simple_list_item_2,
				// Give the cursor to the list adapter
				mMessagesCursor,
				// Map the BODY and SENDER columns in the message database to...
				new String[] { MessageItems.BODY, MessageItems.SENDER },
				// The "text1" view defined in the XML template
				new int[] { android.R.id.text1, android.R.id.text2 });
		mLstMessages.setAdapter(adapter);
	}

	// ==========================================================
	// Inner classes
	// ==========================================================

	/**
	 * Receives the acknowledgements from the XMPP service.
	 */
	private Handler xmppResultHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// sendFailed(0);
		}
	};
}
