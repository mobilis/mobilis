/**
 * Copyright (C) 2009 Technische Universität Dresden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 */

package de.tudresden.inf.rn.mobilis.migration.chat.activities;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import de.tudresden.inf.rn.mobilis.migration.chat.R;
import de.tudresden.inf.rn.mobilis.migration.chat.util.ChatAdapter;
import de.tudresden.inf.rn.mobilis.migration.chat.util.Const;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.MessageItems;
import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPMessage;

public class ChatActivity extends Activity {

	// Tag for log information
	private static String TAG = "ChatActivity";

	// CONST
	public final static String XMPPID_USER = "xmppiduser";
	public final static String XMPPID_PARTNER = "xmppidpartner";

	// views
	private ListView mLstMessages;
	private TextView mTxtState;
	private EditText mEdtText;
	private Button mBtnSend;

	// members
	private MXAController mMXAController;
	private Cursor mCursorChat;
	private String mOwnXMPPID;
	private String mPartnerXMPPID;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);

		// link all views to members
		findAllViewsById();

		// init members
		mMXAController = MXAController.get();
		try {
			mOwnXMPPID = mMXAController.getXMPPService().getUsername();
		} catch (RemoteException e) {
			// TODO make sure that MXA is connected before.
			e.printStackTrace();
		}
		mPartnerXMPPID = getIntent().getExtras().getString(Const.EXTRA_XMPP_ID);

		initChat();
	}

	private void findAllViewsById() {
		mLstMessages = (ListView) findViewById(R.id.chat_lst_messages);
		mTxtState = (TextView) findViewById(R.id.chat_txt_state);
		mEdtText = (EditText) findViewById(R.id.chat_edt_text);
		mBtnSend = (Button) findViewById(R.id.chat_btn_send);

		mBtnSend.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				sendMessage();
			}
		});
	}

	private void initChat() {
		StringBuilder sb = new StringBuilder();
		// build up the filter
		sb.append("(((" + MessageItems.SENDER + " LIKE '"
				+ mPartnerXMPPID.split("/")[0] + "%' AND "
				+ MessageItems.RECIPIENT + " LIKE '" + mOwnXMPPID.split("/")[0]
				+ "%') OR " + "(" + MessageItems.SENDER + " LIKE '"
				+ mOwnXMPPID.split("/")[0] + "%' AND " + MessageItems.RECIPIENT
				+ " LIKE '" + mPartnerXMPPID.split("/")[0] + "%')) AND "
				+ MessageItems.BODY + "<>'') ");
		String helper = sb.toString();

		// Log.i(TAG, helper);
		// open the Cursor
		mCursorChat = getContentResolver().query(MessageItems.CONTENT_URI,
				null, helper, null, MessageItems.DATE_SENT + " ASC");
		startManagingCursor(mCursorChat);

		ChatAdapter chatAdapter = new ChatAdapter(this, R.layout.chat_item, mCursorChat,
				new String[] { MessageItems.DATE_SENT, MessageItems.SENDER,
						MessageItems.BODY },
				new int[] { R.id.chat_item_txt_message });

		chatAdapter.setUserXMPPId(mOwnXMPPID);
		mLstMessages.setAdapter(chatAdapter);
		// always scroll to the end
		mLstMessages.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		setTitle("Chat with " + mPartnerXMPPID);

		mTxtState.setText(null);
		mTxtState.setTextColor(Color.GRAY);
	}

	private void sendMessage() {
		try {
			IXMPPService xmpp = MXAController.get().getXMPPService();
			// xmpp service can be null
			if (xmpp != null) {
				XMPPMessage msg = new XMPPMessage(xmpp.getUsername(),
						mPartnerXMPPID, mEdtText.getText().toString(),
						XMPPMessage.TYPE_CHAT);
				xmpp.sendMessage(new Messenger(new Handler()), -1, msg);
				mEdtText.setText(null);
			}

		} catch (RemoteException e) {
			// happens when bounding problems occur
			Log.i(TAG, e.toString());
		}
	}
}
