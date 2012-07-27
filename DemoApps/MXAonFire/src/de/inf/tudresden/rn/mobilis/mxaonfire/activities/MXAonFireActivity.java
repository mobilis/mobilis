package de.inf.tudresden.rn.mobilis.mxaonfire.activities;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Set;

import de.inf.tudresden.rn.mobilis.mxaonfire.R;
import de.inf.tudresden.rn.mobilis.mxaonfire.R.id;
import de.inf.tudresden.rn.mobilis.mxaonfire.R.menu;
import de.inf.tudresden.rn.mobilis.mxaonfire.service.ListeningFileTransferService;
import de.inf.tudresden.rn.mobilis.mxaonfire.service.ListeningMUCService;
import de.inf.tudresden.rn.mobilis.mxaonfire.service.ListeningSubscribeService;
import de.inf.tudresden.rn.mobilis.mxaonfire.util.Const;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IConnectionCallback;
import android.R.layout;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

/**
 * Demonstrates all of the features of MXA, also used for testing the client
 * itself.
 * 
 * @author Christian Magenheimer
 * 
 */
public class MXAonFireActivity extends Activity implements MXAListener {

	private static String TAG = "MXAOF";
	// private MXAController controller;

	private IXMPPService mXMPP;
	private String mXMPPID = null;
	TextView connectionInformation;
	Button connectMXAButton;
	ToggleButton connectButton;

	// private IPInformationBroadcastReceiver broadcastReceiver;

	Button updateUIButton;
	Button updateIPButton;
	TextView ipInformationTextiew;

	private ListeningFileTransferService mFileTransferService;

	private IXMPPService mXMPPService;
	private boolean mMXAConnected = false;
	private ProgressBar mProgressBar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);

		// Intent i= new Intent(ConstMXA.INTENT_PREFERENCES);
		// this.startActivity(Intent.createChooser(i,
		// "MXA not installed on device"));

		setContentView(R.layout.main_menu);
		mProgressBar = (ProgressBar) findViewById(R.id.main_menu_progress_bar);
		mProgressBar.setIndeterminate(true);

		if (!mMXAConnected) {
			MXAController.get().connectMXA(getApplicationContext(), this);
		}

	}

	@Override
	public void onMXAConnected() {
		mMXAConnected = true;
		try {
			// start the background services
			Intent i = new Intent(MXAonFireActivity.this,
					ListeningFileTransferService.class);
			startService(i);
			i = new Intent(MXAonFireActivity.this, ListeningMUCService.class);
			startService(i);
			i = new Intent(MXAonFireActivity.this, ListeningSubscribeService.class);
			startService(i);
			
			mXMPPService = MXAController.get().getXMPPService();

			if (mXMPPService.isConnected()) {
				// XMPP is already connected
				mXMPPID = mXMPPService.getUsername();
				startRosterActivity();
			} else {
				mXMPPService.connect(new Messenger(mConnectHandler));
				mXMPPID = mXMPPService.getUsername();
			}
		} catch (RemoteException e) {

		}

	}

	@Override
	public void onMXADisconnected() {
		mMXAConnected = false;

	}

	private Handler mConnectHandler = new Handler() {
		public void handleMessage(Message msg) {
			// start the roster if succesfully connected, and stop the activity
			if (msg.what == ConstMXA.MSG_CONNECT
					&& msg.arg1 == ConstMXA.MSG_STATUS_SUCCESS) {
				startRosterActivity();
			}
		};
	};

	private void startRosterActivity() {
		Intent i = new Intent(MXAonFireActivity.this, RosterActivity.class);
		i.putExtra(Const.USER_XMPPID, mXMPPID);
		startActivity(i);
		finish();
	}
}