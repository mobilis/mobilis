package de.inf.tudresden.rn.mobilis.mxaonfire.activities;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

import de.inf.tudresden.rn.mobilis.mxaonfire.R;
import de.inf.tudresden.rn.mobilis.mxaonfire.util.ServiceDiscoveryAdapter;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.services.parcelable.DiscoverItem;
import de.tudresden.inf.rn.mobilis.mxa.services.servicediscovery.IServiceDiscoveryService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Queries the mobilis server for instances  of running services, so called
 * ServiceDiscovery. An user can select the query jid and browse the results
 * 
 * @author Christian Magenheimer
 *
 */
public class ServiceDiscoveryActivity extends Activity implements MXAListener{

	private IXMPPService mXMPPService;
	private IServiceDiscoveryService mServiceDiscoveryService;
	private boolean mMXAConnected;
	private ProgressDialog mProgressDialog;
	private String mSearchFor=null;
	private Stack<String> mStack= new Stack<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MXAController.get().connectMXA(this, this);
		
	}

	
	private void createProgressDialog()
	{
		mProgressDialog= new ProgressDialog(this);
		mProgressDialog.setTitle("Fetching data...");
		mProgressDialog.setMessage("ServiceDiscovery for "+mStack.peek());
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.show();
	}
	private void selectJabberID(String title)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText editText= new EditText(this);
		editText.setText("mobilis.inf.tu-dresden.de");
		builder.setTitle(title)
		.setCancelable(false)
		.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String text= editText.getText().toString();
				Toast.makeText(ServiceDiscoveryActivity.this,text, Toast.LENGTH_SHORT).show();
				Messenger m= new Messenger(mXMPPHandler);
				try {
					mStack.push(text);
					mServiceDiscoveryService.discoverItem(m, m, 0, text, null);
					createProgressDialog();
				} catch (RemoteException e) {
					sendError("Bounding to ServiceDiscoveryService not possible "+e.getMessage());
					mProgressDialog.dismiss();
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.setView(editText);
		alert.show();
	}
	
	
	

	
	@Override
	public void onMXAConnected() {
		mMXAConnected=true;
		try{
			mXMPPService= MXAController.get().getXMPPService();
			if (mXMPPService!=null)mServiceDiscoveryService=mXMPPService.getServiceDiscoveryService();
			else 
			{
				//no service discovery service means no sense for the activity
				sendError("Bounding to ServiceDiscoveryService not possible");
			}
			selectJabberID("Enter a JabberID");
		}catch (Exception e) {
			sendError(e.getMessage());
		}
	}

	@Override
	public void onMXADisconnected() {
		mMXAConnected=false;
		
	}
	
	
	
	private void setServiceDiscoveryList(ArrayList<DiscoverItem> list)
	{
		final ListView lv= new ListView(this);
		TextView header= new TextView(this);
		header.setBackgroundColor(Color.parseColor("#333333"));
		header.setText("Results for "+mStack.peek()+":");
		lv.addHeaderView(header);
		final ServiceDiscoveryAdapter adapter= new ServiceDiscoveryAdapter(list,this,R.layout.service_discovery_item);
		lv.setAdapter(adapter);
		setContentView(lv);
		
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String jid=((DiscoverItem)adapter.getItem(position)).jid;
				String node=((DiscoverItem)adapter.getItem(position)).node;
				if (jid.equals(mStack.peek()) && node!=null)
				{
					createPubSubDialog(jid,node);
					return;
				}
				Messenger m= new Messenger(mXMPPHandler);
				try {					
					mServiceDiscoveryService.discoverItem(m, m, 0, jid, null);
					createProgressDialog();
				} catch (RemoteException e) {
					sendError("Bounding to ServiceDiscoveryService not possible "+e.getMessage());
					mProgressDialog.dismiss();
				}
				
			}

		});
	}
	/**
	 * Handler for receiving the service discovery events.
	 */
	private Handler mXMPPHandler= new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what == ConstMXA.MSG_DISCOVER_ITEMS &&	msg.arg1 == ConstMXA.MSG_STATUS_SUCCESS && msg.getData()!=null)
			{
				msg.getData().setClassLoader(getClassLoader());
				
				if (!mStack.peek().equals(msg.getData().getString("JID")))mStack.push(msg.getData().getString("JID"));
				ArrayList<DiscoverItem> list=msg.getData().getParcelableArrayList("DISCOVER_ITEMS");
				setServiceDiscoveryList(list);
				mProgressDialog.dismiss();
			}else if (msg.what == ConstMXA.MSG_DISCOVER_ITEMS &&	msg.arg1 == ConstMXA.MSG_STATUS_ERROR)
			{
				selectJabberID("JID not found, try a new one");
			}
			
		};
	};
	
	private void sendError(String e)
	{
		Message msg= Message.obtain();
		msg.getData().putString("ERROR", e);
		mErrorHandler.sendMessage(msg);
	}
	
	/**Handler responsible for forwarding an error message
	 * from outside the ui thread
	 */
	private Handler mErrorHandler= new Handler(){
		public void handleMessage(android.os.Message msg) {
			showErrorMessageAndExit(msg.getData().getString("ERROR"));
		}
	};
	
	/**
	 * Uses a dialog to show the user the current error message
	 * and quits the acitivity
	 * @param message Message to be shown
	 */
	private void showErrorMessageAndExit(String message)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String errorMessage="Encountering an error";
		if (message!=null) errorMessage+=": "+errorMessage;
		builder.setMessage(errorMessage)
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mStack.pop();
			String jid;
			try{
				jid=mStack.peek();
			}catch(EmptyStackException e) {jid=null;}
			if (jid!=null)
			{	
				Messenger m= new Messenger(mXMPPHandler);
				try {
				
					mServiceDiscoveryService.discoverItem(m, m, 0, jid, null);
					createProgressDialog();
				} catch (RemoteException e) {
					sendError("Bounding to ServiceDiscoveryService not possible "+e.getMessage());
					mProgressDialog.dismiss();
				}
				return true;
				}
			}else 
			{
				finish();
				return true;
			}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * Creates a dialog where the user can select if he wants to subscribe to the node
	 * @param jid the target jid
	 * @param node the target node
	 */
	private void createPubSubDialog(final String jid, final String node) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Publish/Subscribe")
		.setMessage("Do you want to subscribe to "+jid+"/"+node)
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//subscribe(ISubscribeCallback callback, String target, String node);
				Intent i= new Intent(ServiceDiscoveryActivity.this,PubSubActivity.class);
				i.putExtra("JID",jid);
				i.putExtra("NODE",node);
				startActivity(i);
				dialog.dismiss();
				finish();
			}
		})
		.setNegativeButton("No", null);
		
		AlertDialog alert = builder.create();
		alert.show();
		
	}
}
