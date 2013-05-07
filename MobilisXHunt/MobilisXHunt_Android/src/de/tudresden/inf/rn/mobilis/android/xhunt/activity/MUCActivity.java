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
package de.tudresden.inf.rn.mobilis.android.xhunt.activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;
import de.tudresden.inf.rn.mobilis.android.xhunt.proxy.MXAProxy;
import de.tudresden.inf.rn.mobilis.android.xhunt.service.ServiceConnector;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.MessageItems;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPMessage;

/**
 * The Class MUCActivity is used to chat with the other players.
 */
public class MUCActivity extends Activity{
	
	/** Identifier for the Log outputs *. */
	private static final String TAG = "MUCActivity";

	/** Components of the pre-defined layout/activity_muc.xml **/
	private ListView lvMsgHistory;
	
	/** The editor for text input. */
	private EditText etEditor;
	
	/** The send button. */
	private Button btnSend;

	/** The MXAProxy. */
	private MXAProxy mMxaProxy;
	
	/** Cursor to point on the history messages *. */
	private Cursor msgCursor;
	
	/** Delivered RoomID from the server. */
	private String mucRoomID;
	
	/** The ServiceConnector to connect to XHuntService. */
	ServiceConnector mServiceConnector;
	
    /** The handler which is called if the XHuntService was bound. */
    private Handler mXHuntServiceBoundHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {			
			mMxaProxy = mServiceConnector.getXHuntService().getMXAProxy();
			mucRoomID = mServiceConnector.getXHuntService().getCurrentGame().getChatID();
			
			initMessageHistory();
		}
	};
	
	
	/**
	 * Bind XHuntService using the mXHuntServiceBoundHandler.
	 */
	private void bindXHuntService(){
    	mServiceConnector = new ServiceConnector(this);
    	mServiceConnector.doBindXHuntService(mXHuntServiceBoundHandler);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		// unbind XHuntService to release used resources
		mServiceConnector.doUnbindXHuntService();
		
		super.finish();
	}
	
	/**
	 * Constructor for the Activity.
	 *
	 * @param savedInstanceState the saved instance state
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_muc);
		
		// init the GUI elements and its controls
		initComponents();
		
		// bind the XHuntSerice
		bindXHuntService();		
	}	
	
	
	/**
	 * *********************************	XMPP functions	 *********************************.
	 */
	
	/**
	 * Send the Message to the XMPP-Service
	 */
	private void sendMessage() {
		// Create a new XMPPMessage
		XMPPMessage xMsg = new XMPPMessage();
		
		// Set the type of the message to GROUPCHAT
		xMsg.type = XMPPMessage.TYPE_GROUPCHAT;
		
		// Fill the body of the message with the text from the EditorText
		xMsg.body = etEditor.getText().toString();

		if(mMxaProxy.isConnected()){
			try {
				// Send the Message to the MXAController
				mMxaProxy.getMultiUserChatService().sendGroupMessage(mucRoomID, xMsg);
			} catch (RemoteException e) {
				Log.e(TAG, "sendGroupMessage failed. Code: " + e.getMessage());
			}
	
			// Clear the EditText
			etEditor.setText("");
		}
		else {
			// If XMPP isn't connected notify the user
			Toast toast = Toast.makeText(MUCActivity.this,
					"Currently no connection to server. Please try again in a moment",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, 0);
	    	toast.show();
		}
	}
	
	/**
	 * ***************************************************************************************.
	 */
	
	
	/*********************************	Resource functions	***********************************/
	
	/**
	 * Initialize all UI elements from resources.
	 */
	private void initComponents() {
		lvMsgHistory = (ListView) findViewById(R.id.list_view_history);
		
		etEditor = (EditText) findViewById(R.id.edit_text_editor);
		etEditor.setOnKeyListener(editTextKeyListener);
		etEditor.addTextChangedListener(editorWatcher);
		etEditor.requestFocus();
		
		btnSend = (Button) findViewById(R.id.button_send);
		btnSend.setEnabled(false);
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					sendMessage();
			}
		});
	}
	
	/**
	 * Inits the message history to display old messages.
	 */
	private void initMessageHistory() {
		// Get a cursor to all messages to the user
		msgCursor = getContentResolver().query(MessageItems.contentUri,
				null, null, null, MessageItems.DEFAULT_SORT_ORDER);
		// Start to manage the cursor
		startManagingCursor(msgCursor);
		
		// Set up a Listadapter to handle the history messages
		MUCListAdapter adapter = new MUCListAdapter(this,
				android.R.layout.simple_list_item_2,
				msgCursor,
				new String[] { MessageItems.BODY, MessageItems.SENDER },
				new int[] { android.R.id.text1, android.R.id.text2 });

		
		lvMsgHistory.setAdapter(adapter);
	}
	
	/***************************************************************************************. */
	
	
	/***********************************	Listeners	***************************************/
	
	/** The TextWatcher to recognize if text in editor changes. */
	private final TextWatcher editorWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// Update the Send-Button, so that we only send a message, 
			// when there is something to send in the EditText
			if(etEditor.getText().length() > 0){
				btnSend.setEnabled(true);
			}
			else {
				btnSend.setEnabled(false);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {}
	};
	
	/** KeyListener for listening on the Enter-Key. */
	private final OnKeyListener editTextKeyListener = new OnKeyListener() {
		
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if ((event.getAction() == KeyEvent.ACTION_DOWN)
					&& (keyCode == KeyEvent.KEYCODE_ENTER)
					&& !event.isShiftPressed()) {
				if (btnSend.isEnabled()) {
					sendMessage();
				}
				
				return true;
			} else {
				return false;
			}
		}
	};
	
	/**
	 * ***************************************************************************************.
	 */

	
	/***********************************	InnerClasses	*********************************/
	
	private class MUCListAdapter extends SimpleCursorAdapter{

		/**
		 * Constructor for the ListAdapter.
		 *
		 * @param context Context to bind
		 * @param layout List style
		 * @param c Cursor which points on the data
		 * @param from Points on the columns in the database
		 * @param to Points on the views in the list
		 */
		public MUCListAdapter(Context context, int layout, Cursor c, String[] from,
				int[] to) {
			super(context, layout, c, from, to);
		}
		
		/**
		 * Binds the view and the cursors data and represent it to the context
		 * 
		 * We override the standard SimpleCursorAdapter so that we can modify the sender
		 * to the nickname of the player.
		 *
		 * @param view View to bind on
		 * @param context Context for representation
		 * @param cursor the cursor
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			// Get the current body-entry
			String body = cursor.getString(cursor.getColumnIndex(MessageItems.BODY));
			// Get the current sender-entry and cut out the chatID
			String sender = cursor.getString(cursor.getColumnIndex(MessageItems.SENDER))
					.replace(mucRoomID, "");
			
			// If the sender wasn't the chat itself, we show the nickname of the sender
			// else we let the sender-text empty
			if(sender.length() > 0){
				sender = sender.substring(1);
			}
			
			// Catch the TextView's which represents the sender and the body
			TextView tv_body = (TextView)view.findViewById(android.R.id.text1);
			TextView tv_sender = (TextView)view.findViewById(android.R.id.text2);
			
			// Bind the text to the corresponding cursor-entry
			tv_body.setText(body);
			tv_sender.setText(sender);
		}

	}
	
	/******************************************************************************************/
	
}