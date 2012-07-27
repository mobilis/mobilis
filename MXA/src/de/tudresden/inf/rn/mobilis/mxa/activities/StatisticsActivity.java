package de.tudresden.inf.rn.mobilis.mxa.activities;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.R;
import de.tudresden.inf.rn.mobilis.mxa.XMPPRemoteService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.widget.TextView;

/**
 * Displays information about the current connection.
 * @author Christian Magenheimer
 *
 */
public class StatisticsActivity extends Activity implements MXAListener{

	private TextView mXMPPStatus;
	private TextView mServer;
	private TextView mPort;
	private TextView mUser;
	private TextView mQueueSize;
	private TextView mCurrentNetwork;
	private TextView mIPAdress;
	
	private ConnectivityManager mConnectivityManager; 
	private IXMPPService mXMPPService;
	private Timer mTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistic);
		
		mXMPPStatus=(TextView)findViewById(R.id.statistics_status);
		mServer=(TextView) findViewById(R.id.statistics_server);
		mPort=(TextView) findViewById(R.id.statistics_port);
		mUser=(TextView)findViewById(R.id.statistics_user);
		mQueueSize=(TextView)findViewById(R.id.statistics_count_queue);
		mCurrentNetwork=(TextView)findViewById(R.id.statistics_active_network);
		mIPAdress=(TextView)findViewById(R.id.statistics_ip);
		
		mConnectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		mTimer= new Timer();
		mTimer.scheduleAtFixedRate(new UpdateUITask(), 1000,1000);
		MXAController.get().connectMXA(this, this);
		updateUI();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mTimer.cancel();
	}
	
	
	private void updateUI(){
		if (mXMPPService!=null)
		{
			//read the infos 
			try
			{
				Bundle b=mXMPPService.getXMPPConnectionParameters();
				String host=b.getString("xmpp_host");
				int port=b.getInt("xmpp_port");
				String user=b.getString("xmpp_user")+"/"+b.getString("xmpp_resource");
				int count=b.getInt("lostiqqueue_count");
				
				mXMPPStatus.setText(String.valueOf(mXMPPService.isConnected()));
				mServer.setText(host);
				mPort.setText(String.valueOf(port));
				mUser.setText(user);
				mQueueSize.setText(String.valueOf(count));
				
			}catch(RemoteException e)
			{
				mXMPPStatus.setText("N/A");
				mServer.setText("N/A");
				mPort.setText("N/A");
				mUser.setText("N/A");
				mQueueSize.setText("N/A");
			}
		}else
		{
			mXMPPStatus.setText("N/A");
			mServer.setText("N/A");
			mPort.setText("N/A");
			mUser.setText("N/A");
			mQueueSize.setText("N/A");
		}
		
		NetworkInfo networkInfo= mConnectivityManager.getActiveNetworkInfo();
		if (networkInfo!=null)
		{
			String current=networkInfo.getTypeName();
			if (networkInfo.getSubtypeName()!=null && !networkInfo.getTypeName().equals("WIFI")) 
				current+=" - "+networkInfo.getSubtypeName();
			mCurrentNetwork.setText(current);
			String ip=getLocalIpAddress();
			if (ip!=null)
			{
				mIPAdress.setText(ip);
			}
		}else
		{
			mCurrentNetwork.setText("N/A");
			mIPAdress.setText("N/A");
		}
	}
	
	/**
	 * Handler for updating the user interface
	 */
	private Handler mGuiHandler= new Handler()
	{
		public void handleMessage(Message msg)
		{
			updateUI();
		}
	};
	
	private class UpdateUITask extends TimerTask
	{

		@Override
		public void run() {
			mGuiHandler.sendEmptyMessage(0);
		}
		
	}

	@Override
	public void onMXAConnected() {
		mXMPPService= MXAController.get().getXMPPService();
	}

	@Override
	public void onMXADisconnected() {
	}
	
	/**
	 * Returns the current IP Adress, either a global one, or local like 192.168.*
	 * 
	 * @return IP oder null on Error
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();

					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {

		}
		return null;
	}
}
