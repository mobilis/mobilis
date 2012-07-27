package de.inf.tudresden.rn.mobilis.mxaonfire.activities;

import java.math.BigInteger;
import java.util.Date;
import java.util.zip.Inflater;

import de.inf.tudresden.rn.mobilis.mxaonfire.R;
import de.inf.tudresden.rn.mobilis.mxaonfire.R.id;
import de.inf.tudresden.rn.mobilis.mxaonfire.R.layout;
import de.inf.tudresden.rn.mobilis.mxaonfire.util.Const;
import de.inf.tudresden.rn.mobilis.mxaonfire.util.RosterAdapter;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.RosterItems;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPPresence;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class RosterActivity extends Activity implements MXAListener{

	
	private Cursor cursor;
	private String mXMPPID;
	
	private ListView mListView;
	private int mMode=-1;
	private String mStatus;
	private ImageView mStatusImage;
	private Spinner mSpinner;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.roster_layout);
		mListView=(ListView)findViewById(R.id.roster_layout_listview);
		mStatusImage=(ImageView)findViewById(R.id.roster_layout_status_icon);
		
		
		
		
		setPresence();
		
		
		
		//get the own xmpp id
		mXMPPID=getIntent().getExtras().getString(Const.USER_XMPPID);
		//get the roster list, and show the presence of the entries
		cursor= getContentResolver().query(RosterItems.CONTENT_URI,null,
				null,null, RosterItems.XMPP_ID+" ASC, "+RosterItems.RESSOURCE+" ASC");
		startManagingCursor(cursor);
	/*	ListAdapter adapter  = new SimpleCursorAdapter(this,
									R.layout.roster_item,
									cursor,
									new String []{RosterItems.NAME,RosterItems.PRESENCE_MODE,RosterItems.UPDATED_DATE,RosterItems.PRESENCE_MODE,RosterItems.XMPP_ID},
									new int []{R.id.roster_item_name,R.id.roster_item_status,R.id.roster_item_info,R.id.roster_item_mode,R.id.roster_item_xmppid});
					
		*/
		RosterAdapter adapter= new RosterAdapter(this, R.layout.roster_item, cursor,
				new String []{RosterItems.NAME,RosterItems.PRESENCE_MODE,RosterItems.UPDATED_DATE,RosterItems.PRESENCE_STATUS,RosterItems.XMPP_ID},
				new int []{R.id.roster_item_name,R.id.roster_item_image});

		mListView.setAdapter(adapter);
		mListView.setBackgroundColor(Color.WHITE);
//		getListView().setSelector(R.drawable.roster_selector);
		registerForContextMenu(mListView);
		
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,	long id) {
				v.setBackgroundColor(Color.GRAY);
				String xmppid=cursor.getString(cursor.getColumnIndex(ConstMXA.RosterItems.XMPP_ID));
				String res=cursor.getString(cursor.getColumnIndex(ConstMXA.RosterItems.RESSOURCE));
				if (res!=null) xmppid+="/"+res;
				Intent i= new Intent(RosterActivity.this, ChatActivity.class);
				i.putExtra(Const.PARTNER_XMPPID, xmppid);
				i.putExtra(Const.USER_XMPPID, mXMPPID);
				startActivity(i);
				
			}
			
		});
		
		
		mSpinner=(Spinner)findViewById(R.id.roster_layout_spinner);
		String[] spinnerList= new String[]{ "available","chat","away","extended away","do not disturb"};
		ArrayAdapter<String> sAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,spinnerList);
		sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(sAdapter);
		
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				XMPPPresence presence= new XMPPPresence();
				switch (position)
				{
				case 0: mMode=XMPPPresence.MODE_AVAILABLE;
						break;
				case 1: mMode=XMPPPresence.MODE_CHAT;
					break;
				case 2: mMode=XMPPPresence.MODE_AWAY;
					break;
				case 3: mMode=XMPPPresence.MODE_XA;
					break;
				case 4: mMode=XMPPPresence.MODE_DND;
					break;
				default:mMode=XMPPPresence.MODE_AVAILABLE;
					break;
				}
				try {
					presence.mode=mMode;
					setPresence();
					MXAController.get().getXMPPService().sendPresence(new Messenger(mHandler), 0, presence);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
			
		});
		
		MXAController.get().connectMXA(this, this);
		
	}
	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Choose an action:");
		menu.add(ContextMenu.NONE, 1, ContextMenu.NONE, "Send file...");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		if (item.getItemId()==1)
		{
			Intent i= new Intent(RosterActivity.this,FileTransferActivity.class);
		
			
			String partner=cursor.getString(cursor.getColumnIndex(ConstMXA.RosterItems.XMPP_ID));
			String ressource=cursor.getString(cursor.getColumnIndex(ConstMXA.RosterItems.RESSOURCE));
			if (ressource!=null)partner+="/"+ressource;
			i.putExtra(Const.PARTNER_XMPPID, partner);
			i.putExtra(Const.USER_XMPPID, mXMPPID);
			startActivity(i);
			return true;
		}
		return super.onContextItemSelected(item);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater= getMenuInflater();
		inflater.inflate(R.menu.main_menu,menu);
		return true;
		
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId())
		{
		case R.id.menu_item_service_discovery: // start service discovery app
			Intent i;
			i = new Intent(this, ServiceDiscoveryActivity.class);
			startActivity(i);
			break;
		case R.id.menu_item_muc: // start multiuserchat
			i = new Intent(this,MultiUserChatActivity.class);
			startActivity(i);
			break;
		case R.id.menu_item_pubsub: //Start pubusb
			i = new Intent(this,PubSubActivity.class);
			startActivity(i);
		default: break;
		}
		return true;
	}


	@Override
	public void onMXAConnected() {
		if (mMode==-1) 
		{
			try {
				if (MXAController.get().getXMPPService().isConnected())
				{
					mMode=XMPPPresence.MODE_AVAILABLE;
					setPresence();
				}
			} catch (RemoteException e) {
			}
		}
		
	}


	@Override
	public void onMXADisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	private void setPresence()
	{
		switch(mMode)
		{
		case -1: mStatusImage.setImageResource(R.drawable.offline);
				 break;
		case XMPPPresence.MODE_AVAILABLE: mStatusImage.setImageResource(R.drawable.available);
		 	break;
		case XMPPPresence.MODE_AWAY: mStatusImage.setImageResource(R.drawable.away);
	 	break;
		case XMPPPresence.MODE_CHAT: mStatusImage.setImageResource(R.drawable.chat);
	 	break;
		case XMPPPresence.MODE_DND: mStatusImage.setImageResource(R.drawable.busy);
	 	break;
		case XMPPPresence.MODE_XA: mStatusImage.setImageResource(R.drawable.extended_away);
	 	break;
		} 
	}
	
	private Handler mHandler= new Handler()
	{
		public void handleMessage(android.os.Message msg) {};
	};
}


