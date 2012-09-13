package de.tudresden.inf.rn.mobilis.mxa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
/**
 * This class waits for changes in the connectivity of android,
 * if a connection is reestablished, we have to reconnect the mxa manager.
 * The connection state is listened to but not actively managed.
 * @author Christian Magenheimer
 *
 */
public class NetworkMonitor extends BroadcastReceiver{

	private boolean connectScheduled = false;
	
	//holds the xmpp service
	private XMPPRemoteService mXMPPRemoteService;
	private static String TAG="NetworkMonitor";
	
	//currently connected or not the internet
	private boolean mConnected=false;
	
	
	
	/**
	 * Constructs a new NetworkMonitor that listens to connectivity changes
	 * @param service XMPPRemoteService that should be called on reconnection
	 */
	public NetworkMonitor(XMPPRemoteService service)
	{
		mXMPPRemoteService=service;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//we have received a broadcast event
		String action= intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
		{
			//thats the correct action, now check if the connectivity is broken
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			//NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			
			if (activeNetworkInfo != null && activeNetworkInfo.getReason() == null) {
				return;
			}
			
			Log.v(TAG, "Connectivity change");
			
			if (activeNetworkInfo == null || !activeNetworkInfo.isConnected())
			{
				//disconnected
				mConnected=false;				
			} else {
				//connected, test if we did failover from mobile
				mConnected=true;
				
				if (connectScheduled) {
					connectScheduled = false;
					mXMPPRemoteService.connect();
				} else {
//				if (intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false))
					mXMPPRemoteService.reconnect();
				}
				
			}
			
			if (activeNetworkInfo != null) {
				Log.v(TAG,activeNetworkInfo.toString());
				Log.v(TAG,activeNetworkInfo.getDetailedState().toString());
			}
			Log.v(TAG,"isfailover: "+intent.getExtras().getBoolean(ConnectivityManager.EXTRA_IS_FAILOVER));
			if (intent.getExtras().getParcelable(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO)!=null)
				Log.v(TAG,"oni: "+intent.getExtras().getParcelable(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO).toString());
		}
		
	}

	/**
	 * Returns whether or not there is an internet connection 
	 * @return true if there is one, false otherwise
	 */
	public boolean isConnected()
	{
		return mConnected;
	}

	public void scheduleConnect() {
		connectScheduled = true;
	}

}

