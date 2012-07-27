package de.inf.tudresden.rn.mobilis.mxaonfire.activities;

import java.util.ArrayList;
import java.util.List;

import de.inf.tudresden.rn.mobilis.mxaonfire.R;
import de.inf.tudresden.rn.mobilis.mxaonfire.util.Const;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.services.multiuserchat.IMultiUserChatService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Show the chat message from the multiuserchat.
 * @author Christian Magenheimer
 *
 */
public class MultiUserChatActivity extends Activity implements MXAListener{

	private boolean mJoinRoom=false;
	private static final String TAG="MUC";
	private class MUCDescriber{
		public String room;
		public String inviter;
		public String password;
		public String reason;
	}
	
	private MUCDescriber mDescriber;
	private IXMPPService mXMPPService;
	private IMultiUserChatService mMUCService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent i=getIntent();
		if (i!=null && i.hasExtra("ROOM"))
		{
			//there  was an invitation
			mDescriber= new MUCDescriber();
			mDescriber.room=i.getStringExtra("ROOM");
			mDescriber.inviter=i.getStringExtra("INVITER");
			mDescriber.password=i.getStringExtra("PASSWORD");
			mDescriber.reason=i.getStringExtra("REASON");
			mJoinRoom=true;
		}
		MXAController.get().connectMXA(this, this);
		Log.v(TAG,"started muc acitvity");
	}
	@Override
	public void onMXAConnected() {
		try{
			mXMPPService=MXAController.get().getXMPPService();
			mMUCService=mXMPPService.getMultiUserChatService();
		}catch (Exception e) {
			Log.e(TAG,"error binding xmpp "+e.getMessage());
		}
		Message msg= new Message();
		msg.what=1;
		guiHandler.sendMessage(msg);
	}
		
	@Override
	public void onMXADisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	private Handler guiHandler= new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			if (msg.what==1)
			{
				//build up ui
				if (mJoinRoom) createJoinRoomDialog();
				else createGUI();
			}
		}
	};
	
	
	private void createJoinRoomDialog()
	{
		Log.v(TAG,"create dialog");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage(mDescriber.inviter+" invites you to the room \""+mDescriber.room+"\", the reason is: "+mDescriber.reason)
		.setCancelable(false)
		.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				try {
					mMUCService.acceptInvation(mDescriber.room, mDescriber.password);
					createGUI();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		})
		.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				try {
					mMUCService.declineInvitation(mDescriber.room, mDescriber.inviter, "Im sorry, but i dont want to take part in this MUC");
					createGUI();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void createGUI()
	{
		ListView lv= new ListView(this);
		try{
			List<String> rooms=mMUCService.getJoinedRooms();
			final ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,rooms);
			lv.setAdapter(adapter);
			setContentView(lv);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void  onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent i= new Intent(MultiUserChatActivity.this,MUCRoomActivity.class);
					i.putExtra("ROOM", adapter.getItem(position));
					startActivity(i);
					
				}
				
			});
			lv.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					createRoomInfoDialog(adapter.getItem(position));
					return true;
				}
				
				
			});
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater= getMenuInflater();
		inflater.inflate(R.menu.muc_menu,menu);
		return true;
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
		case R.id.menu_item_muc_refresh_list:
			createGUI();
			break;
		case R.id.menu_item_muc_create:
			createMUC();
			break;
			
		}
		return true;
	}
	
	private void createMUC()
	{
		final Dialog dialog= new Dialog(this);
		dialog.setTitle("Create MultiUserChat");
		dialog.setContentView(R.layout.create_muc_dialog);
		final EditText roomName= (EditText)dialog.findViewById(R.id.muc_create_room);
		try {
			roomName.setText("test@conference."+mXMPPService.getUsername().split("@")[1].split("/")[0]);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		final EditText roomNick= (EditText)dialog.findViewById(R.id.muc_create_nick);
		final Button createButton=(Button)dialog.findViewById(R.id.muc_create_button);
		
		
		
		createButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					if (mMUCService.createRoom(roomName.getText().toString(), roomNick.getText().toString()))
					{
						Intent i= new Intent(MultiUserChatActivity.this,MUCRoomActivity.class);
						i.putExtra("ROOM",roomName.getText().toString());
						i.putExtra("NICK",roomNick.getText().toString());
						startActivity(i);
						dialog.dismiss();
						finish();
					}else
					{
						Toast.makeText(MultiUserChatActivity.this, "Chatroom could not be created, try a new one", Toast.LENGTH_SHORT).show();
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		dialog.show();
	}
	
	private void createRoomInfoDialog(String room)
	{
		StringBuilder sb= new StringBuilder();
		Bundle bundle;
		try {
			bundle=mMUCService.getRoomInfo(room);
			if (bundle==null) return;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		final Dialog dialog= new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.muc_info);
		
		TextView left= (TextView)dialog.findViewById(R.id.muc_info_left);
		TextView right= (TextView)dialog.findViewById(R.id.muc_info_right);
		TextView bottom= (TextView)dialog.findViewById(R.id.muc_info_bottom);
		TextView title= (TextView)dialog.findViewById(R.id.muc_info_title);
		Button close= (Button) dialog.findViewById(R.id.muc_info_button);
		
		sb.append("Moderated:").append("\n");
		sb.append("Members only:").append("\n");
		sb.append("Password:").append("\n");
		sb.append("Persistent:").append("\n");
		sb.append("Count of occupants:").append("\n");
		left.setText(sb.toString());
		sb=new StringBuilder();
		
		sb.append(bundle.getBoolean(ConstMXA.MUC_IS_MODERATED)).append("\n");
		sb.append(bundle.getBoolean(ConstMXA.MUC_IS_MEMEBERSONLY)).append("\n");
		sb.append(bundle.getBoolean(ConstMXA.MUC_IS_PASSWORDPROTECTED)).append("\n");
		sb.append(bundle.getBoolean(ConstMXA.MUC_IS_PERSISTENT)).append("\n");
		
		sb.append(bundle.getInt(ConstMXA.MUC_OCCUPANTSCOUNT)).append("\n");
		right.setText(sb.toString());
		sb=new StringBuilder();
		sb.append("Subject:").append(bundle.getString(ConstMXA.MUC_SUBJECT)).append("\n");
		sb.append("Description:").append(bundle.getString(ConstMXA.MUC_DESCRIPTION)).append("\n");
		bottom.setText(sb.toString());
		
		title.setText("Room Info for \n"+room);
		close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});
		dialog.show();
	}
}
 