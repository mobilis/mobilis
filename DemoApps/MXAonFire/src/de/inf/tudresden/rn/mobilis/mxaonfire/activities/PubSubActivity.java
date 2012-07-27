package de.inf.tudresden.rn.mobilis.mxaonfire.activities;

import de.inf.tudresden.rn.mobilis.mxaonfire.R;
import de.inf.tudresden.rn.mobilis.mxaonfire.service.ListeningSubscribeService;
import de.inf.tudresden.rn.mobilis.mxaonfire.service.ListeningSubscribeService.ListeningSubscribeServiceBinder;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.inputmethodservice.Keyboard.Key;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Displays current subcripted nodes.
 * Possibility to create new subscriptions.
 * @author elmar
 *
 */
public class PubSubActivity extends ListActivity {

	
	private ListeningSubscribeService mService;
	private String mTarget=null;
	private  ArrayAdapter<String> mAdapter;
	private String mNode=null;
	
	private String mSubscribeNode=null;
	private String mSubScribeTarget=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b=getIntent().getExtras();
		if (b!=null && b.containsKey("JID"))
		{
			mSubScribeTarget=b.getString("JID");
			mSubscribeNode=b.getString("NODE");
		}
		Intent i= new Intent(PubSubActivity.this, ListeningSubscribeService.class);
		bindService(i,mServiceConncetion,Context.BIND_AUTO_CREATE);
			
	}
	
	
	
	private void buildGUI()
	{
		mAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mService.getTargets());
		setListAdapter(mAdapter);
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (mTarget==null)
		{
			mTarget=mAdapter.getItem(position);
			mAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mService.getNodes(mTarget));
			
		}else if (mNode==null)
		{
			mNode=mAdapter.getItem(position);
			mAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mService.getItems(mTarget, mNode));
		}
		setListAdapter(mAdapter);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mNode!=null)
			{
				mNode=null;
				mAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mService.getNodes(mTarget));
				setListAdapter(mAdapter);
			}
			else if (mTarget!=null){
				mTarget=null;
				mAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mService.getTargets());
				setListAdapter(mAdapter);
			}
			else 
			{
				unbindService(mServiceConncetion);
				finish();
			}
			return true;
		}
		else return super.onKeyDown(keyCode, event);
	}
	
	private ServiceConnection mServiceConncetion = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((ListeningSubscribeService.ListeningSubscribeServiceBinder)service).getService();
			//check if we were started from outside
			if (mSubScribeTarget!=null)
			{
				mService.subscribe(mSubScribeTarget, mSubscribeNode);
				mSubScribeTarget=null;
			}
			buildGUI();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
			
		}

	};
	
}
