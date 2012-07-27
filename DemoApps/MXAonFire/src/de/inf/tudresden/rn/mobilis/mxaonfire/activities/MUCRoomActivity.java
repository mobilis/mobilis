package de.inf.tudresden.rn.mobilis.mxaonfire.activities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.inf.tudresden.rn.mobilis.mxaonfire.R;
import de.inf.tudresden.rn.mobilis.mxaonfire.util.Const;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.MessageItems;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.RosterItems;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPMessage;
import de.tudresden.inf.rn.mobilis.mxa.services.multiuserchat.IMultiUserChatService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Shows the message of ONE multiuserchat.
 * Builds up possiblities in the menu.
 * @author Christian Magenheimer
 *
 */
public class MUCRoomActivity extends Activity implements MXAListener {

	private IMultiUserChatService mMUCService;
	private IXMPPService mXMPPService;
	private final static String TAG="MUCRoomActivity";
	private String mRoom;
	private ListView mListView;
	
	private Button mSendButton;
	private EditText mSendText;
	private TextView mRoomTextView;
	private String mNick=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i=getIntent();
		if (i.hasExtra("ROOM"))
		{
			mRoom=i.getStringExtra("ROOM");
			mNick=i.getStringExtra("NICK");
			Log.v(TAG,"alread set nick "+mNick);
		}
		else {
			finish();
			return;
		}
		MXAController.get().connectMXA(this,this);
		
		if(savedInstanceState!=null)
		{
			mNick=savedInstanceState.getString("NICK");
			Log.v(TAG, "nick restored: +"+mNick);
		}
		
	}
	
	/*
	 * Creates the listview and shows the muc messages.
	 */
	private void create()
	{
		setContentView(R.layout.muc_room_layout);
		mListView=(ListView) findViewById(R.id.muc_room_list_view);
		Cursor cursor=  getContentResolver().query(ConstMXA.MessageItems.CONTENT_URI,
				null, 
				//new String[]{ConstMXA.MessageItems.SENDER,ConstMXA.MessageItems.BODY}, 
				ConstMXA.MessageItems.TYPE+"='groupchat' AND "+ConstMXA.MessageItems.SENDER+" LIKE '"+mRoom+"%'" ,null,null);
		startManagingCursor(cursor);
		SimpleCursorAdapter adapter= new MUCAdapter(this,
				android.R.layout.simple_list_item_2,
				cursor, 
				new String[]{ConstMXA.MessageItems.BODY,ConstMXA.MessageItems.SENDER},
				//new int[]{R.layout.simple_list_item_1,R.layout.simple_list_item_2});
				new int[] { android.R.id.text1, android.R.id.text2 });
		mListView.setAdapter(adapter);
		mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		mListView.setStackFromBottom(true);
		
		mSendText=(EditText)findViewById(R.id.muc_room_send_text);
		
		mSendText.setText("");
		mSendText.setHint("Enter a message...");

		mSendButton= (Button)findViewById(R.id.muc_room_send_button);
		mSendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				XMPPMessage m= new XMPPMessage();
				m.body=mSendText.getText().toString();
				if (m.body.length()==0)return;
				try {
					mMUCService.sendGroupMessage(mRoom, m);
				} catch (RemoteException e) {
					Log.e(TAG,"error sending groupchat message "+e.getMessage());
				}
				mSendText.setText("");
				mListView.requestFocus();
				
			}
		});
		
		mRoomTextView=(TextView) findViewById(R.id.muc_room_headline);
		mRoomTextView.setText(mRoom.split("@")[0]);
		
		
		
		
	}
		
	
	/**
	 * Adapts the information from the database to the needs.
	 * The chatid has to be parsed.
	 * @author elmar
	 *
	 */
	private class MUCAdapter extends SimpleCursorAdapter
	{

		public MUCAdapter(Context context, int layout, Cursor c, String[] from,
				int[] to) {
			super(context, layout, c, from, to);
		}
	
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			String sender =cursor.getString(cursor.getColumnIndex(ConstMXA.MessageItems.SENDER));
			String[] parts=sender.split("/");
			if (parts.length>1)
			{
				sender=parts[1]; 
			}
			else{
				sender="Room "+sender;
				
			}
			
			TextView tvSender= (TextView) view.findViewById(android.R.id.text2);
			tvSender.setText(sender);
			
			TextView tvBody= (TextView) view.findViewById(android.R.id.text1);
			tvBody.setText(cursor.getString(cursor.getColumnIndex(ConstMXA.MessageItems.BODY)));
		
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Create the options menu according to the xml file
		MenuInflater inflater= getMenuInflater();
		inflater.inflate(R.menu.muc_room_menu,menu);
		return true;	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case R.id.menu_item_muc_leave: //leave the room
			try {
				mMUCService.leaveRoom(mRoom);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				Log.v(TAG,"error binding MUC Service");
			}
			finish();
			return true;
		case R.id.menu_item_muc_change_nick: //change nick
			changeNickDialog();
			return true;
		case R.id.menu_item_muc_kick: //kick member
			kickDialog();
			return true;
		case R.id.menu_item_muc_invite: //invite user
			inviteUserDialog();
			return true;			
		
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onMXAConnected() {
		try
		{
			mMUCService=MXAController.get().getXMPPService().getMultiUserChatService();
			guiHandler.sendMessage(Message.obtain());
			mXMPPService=MXAController.get().getXMPPService();
			if (mNick==null)mXMPPService.getUsername();
		}catch(RemoteException e)
		{
			Log.e(TAG,"error binding xmpp: "+e.getMessage() );
		}
		
	}

	@Override
	public void onMXADisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	private Handler guiHandler= new Handler(){
		public void handleMessage(android.os.Message msg) {
			create();
		};
	};
	
	private void changeNickDialog()
	{
		final String result;
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText editText= new EditText(this);
		editText.setHint("new nick...");
		editText.setText("");
		builder.setTitle("Enter a new nickname")
		.setCancelable(false)
		.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String text= editText.getText().toString();
				if (text.length()>0) 
				{
					try {
						boolean success=mMUCService.changeNickname(mRoom, text);
						if (!success) builder.setTitle("Nickname not possible, try a new one");
						else 
							{
								mNick=text;
								dialog.dismiss();
								
							}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						Log.v(TAG,"error binding MUC Service");
					}
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.setView(editText);
		alert.show();
	}
	
	private void kickDialog()
	{
		final String[] list;
		try{
			//remove own nick, we dont want to kick ourselves
			
			List<String> l=mMUCService.getMembers(mRoom);
			
			l.remove(mRoom+"/"+mNick);
			Log.v(TAG,mRoom+"/"+mNick);
			list= new String[l.size()];
			for (int i=0;i<l.size();i++)list[i]=l.get(i).split(mRoom+"/")[1];
		}catch (Exception e) {
			Log.e(TAG,"exception getting members of the room");
			return;
		}


		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose a member");
		builder.setItems(list, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        Log.v(TAG,"select "+list[item]+" to kick");
		        try {
					boolean success=mMUCService.kickParticipant(mRoom, list[item], "Es kann nur einen geben");
					if(!success)
					{
						Toast.makeText(MUCRoomActivity.this, "Kicking was not successful",Toast.LENGTH_SHORT);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					Log.e(TAG,"exception during kicking");
					dialog.dismiss();
					return;
				}
		 
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString("NICK", mNick);
		super.onSaveInstanceState(outState);
	}
	
	void inviteUserDialog()
	{
		//do a simple selection on the roster to the online users
		Cursor c=getContentResolver().query(RosterItems.CONTENT_URI, null, RosterItems.PRESENCE_MODE+"<>'"+RosterItems.MODE_UNAVAILABLE+"'",null,null);
		ArrayList<String> list= new ArrayList<String>();
		while (c.moveToNext())
		{
			String xmppid=c.getString(c.getColumnIndex((RosterItems.XMPP_ID)));
			String res=c.getString(c.getColumnIndex((RosterItems.RESSOURCE)));
			list.add(xmppid+"/"+res);
		}
		if (list.size()==0)return;
		final String[] array=(String[])list.toArray(new String[list.size()]);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose a contact");
		builder.setItems(array, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        Log.v(TAG,"select "+array[item]+" to invite");
		        try {
		            //boolean inviteUser(String roomID, String userJID, String reason);
		        	boolean success=mMUCService.inviteUser(mRoom, array[item], "MultiUserChat");
		        	dialog.dismiss();
		        	if (!success)
		        	{
		        		Toast.makeText(MUCRoomActivity.this,"Invitation failed!",Toast.LENGTH_SHORT);
		        	}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					Log.e(TAG,"exception during kicking");
					dialog.dismiss();
					return;
				}
		 
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**Reads information about the room and shows them to the user
	 * 
	 */
	void roomInfoDialog()
	{
		Bundle bundle=null;
		try {
			 bundle=mMUCService.getRoomInfo(mRoom);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bundle!=null)
		{
			
		}
	}
}
