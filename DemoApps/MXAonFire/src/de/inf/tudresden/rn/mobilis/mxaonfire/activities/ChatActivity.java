package de.inf.tudresden.rn.mobilis.mxaonfire.activities;

import java.util.concurrent.CopyOnWriteArraySet;

import de.inf.tudresden.rn.mobilis.mxaonfire.R;
import de.inf.tudresden.rn.mobilis.mxaonfire.util.ChatAdapter;
import de.inf.tudresden.rn.mobilis.mxaonfire.util.Const;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.MessageItems;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IChatStateCallback;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IConnectionCallback;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPMessage;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Displays the Chat Screen. 
 * Uses the @see de.inf.tudresden.rn.mxaonfire.ChatAdapter
 * @author Christian Magenheimer
 */
public class ChatActivity extends Activity implements MXAListener{

	//Tag for log information
	private static String TAG="ChatActivity";
	
	//ChatAdapter and cursor for database access
	private Cursor mChatCursor;
	private ChatAdapter mChatAdapter;
	
	//own XMPP ID
	private String mUserXMPPID;
	//the xmpp id of the other
	private String mPartnerXMPPID;
	//button which is pressed to send the message
	private Button mButtonSend;
	//textfield holding the message
	private EditText mEditText;
	//chatstate textview
	private TextView mChatState;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//get the layout and show it
		setContentView(R.layout.chatscreen_layout);
		//XMPP IDs are transfered by parcelable extras
		mPartnerXMPPID=getIntent().getExtras().getString(Const.PARTNER_XMPPID);
		mUserXMPPID=getIntent().getExtras().getString(Const.USER_XMPPID);		
	
		//show the last entries
		ListView chatListView=(ListView) findViewById(R.id.chat_screen_list_view);
		StringBuilder sb= new StringBuilder();
		//build up the filter
		sb.append("((("+MessageItems.SENDER+" LIKE '"+mPartnerXMPPID.split("/")[0]
				   +"%' AND "
				   +MessageItems.RECIPIENT+" LIKE '"+mUserXMPPID.split("/")[0]
				   +"%') OR"+
				   "("+MessageItems.SENDER+" LIKE '"+mUserXMPPID.split("/")[0]
				   +"%' AND "
				   +MessageItems.RECIPIENT+" LIKE '"+mPartnerXMPPID.split("/")[0]
				   +"%')) AND "+MessageItems.BODY+"<>'') ");
		String helper=sb.toString();
		
		//Log.i(TAG, helper);
		//open the Cursor
		mChatCursor= getContentResolver().query(MessageItems.CONTENT_URI,null,
				helper,null, MessageItems.DEFAULT_SORT_ORDER);
		startManagingCursor(mChatCursor);
		
		mChatAdapter=new ChatAdapter(this, R.layout.chat_item, 
				mChatCursor,
				new String[]{MessageItems.DATE_SENT,MessageItems.SENDER,MessageItems.BODY},
				new int[]{R.id.chat_item_text_view});
		
		mChatAdapter.setUserXMPPId(mUserXMPPID);
		chatListView.setAdapter(mChatAdapter);	
		//always scroll to the end
		chatListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		setTitle("Chat with "+mPartnerXMPPID);
		mEditText= (EditText) findViewById(R.id.chat_screen_edit_text);
		
		//set the onclicklistener
		mButtonSend= (Button) findViewById(R.id.chat_screen_send);
		mButtonSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					IXMPPService xmpp= MXAController.get().getXMPPService();
					//xmpp service can be null!
					if (xmpp!=null)
					{
						if (xmpp.isConnected())
						{
							XMPPMessage msg= new XMPPMessage(xmpp.getUsername(), mPartnerXMPPID, mEditText.getText().toString(), XMPPMessage.TYPE_CHAT);
							xmpp.sendMessage(new Messenger(new Handler()), -1, msg);
							mEditText.setText(null);
						}
						
					
					}
					
				}catch (RemoteException e)
				{
					//happens when bounding problems occur
					Log.i(TAG, e.toString());
				}
				
			}
		});
	
		MXAController.get().connectMXA(this, this);
		
		mChatState=(TextView)findViewById(R.id.chat_screen_chat_state);
		mChatState.setText("");
		mChatState.setTextColor(Color.GRAY);
	
	}

	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			
			String chatState=msg.getData().getString("CHATSTATE");
			Log.v(TAG,"got chat state");
			if (chatState!=null)
				{
				//	Toast.makeText(ChatActivity.this, "ChatEvent: "+msg.getData().getString("CHATSTATE"), Toast.LENGTH_SHORT).show();
				if (chatState.equals(ConstMXA.CHATSTATE_ACTIVE)) mChatState.setText("chat is active");
				else if(chatState.equals(ConstMXA.CHATSTATE_COMPOSING)) mChatState.setText(mPartnerXMPPID+" is currently entering a message");
				else if(chatState.equals(ConstMXA.CHATSTATE_INACTIVE)) mChatState.setText("chat is inactive");
				else if(chatState.equals(ConstMXA.CHATSTATE_GONE)) mChatState.setText(mPartnerXMPPID+" is gone");
				else if(chatState.equals(ConstMXA.CHATSTATE_PAUSED)) mChatState.setText(mPartnerXMPPID+" paused entering a message");
				else mChatState.setText("unknown chat state: "+chatState);
				}
		}
	};




	@Override
	public void onMXAConnected() {
		try {
			MXAController.get().getXMPPService().registerChatStateCallback(mChatStateCallback, mPartnerXMPPID);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			Log.e(TAG,"exception: "+e.getMessage());
		}
		
	}


	@Override
	public void onMXADisconnected() {
		try {
			MXAController.get().getXMPPService().unregisterChatStateCallback(mChatStateCallback, mPartnerXMPPID);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			Log.e(TAG,"exception: "+e.getMessage());
		}
		
	}

	
	private IChatStateCallback mChatStateCallback= new IChatStateCallback.Stub() {
		
		@Override
		public void chatEventReceived(String arg0) throws RemoteException {
			Log.v(TAG, "Chatevenet received "+ arg0);
			Message m= new Message();
			m.getData().putString("CHATSTATE", arg0);
			handler.sendMessage(m);
			
			
		}
	};
				
	
	
	@Override
	protected void onPause() {
		super.onPause();
		try {
			MXAController.get().getXMPPService().unregisterChatStateCallback(mChatStateCallback, mPartnerXMPPID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG,"exception: "+e.getMessage());
		}
	}

	

}

