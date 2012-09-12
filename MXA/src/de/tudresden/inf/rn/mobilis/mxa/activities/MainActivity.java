/**
 * Copyright (C) 2009 Technische Universität Dresden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 */

package de.tudresden.inf.rn.mobilis.mxa.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.R;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IConnectionCallback;

/**
 * Shows the main view that enables the user to connect or disconnect from the
 * XMPP server. Also buttons to open the preferences and statistics activity are
 * provided.
 * 
 * @author Christian Magenheimer
 */
public class MainActivity extends Activity implements MXAListener {

	private Button mLoginButton;
	private Button mLogoffButton;
	private Button mPreferencesButton;
	private Button mSetupButton;
	private Button mStatisticsButton;
	private Button mDeleteMessagesButton;

	private boolean mMxaConnected = false;
	private IXMPPService mXMPPService;
	private boolean mXMPPConnected = false;

	private static final String TAG = "MXA:MainActivity";
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		mLoginButton = (Button) findViewById(R.id.main_activity_login_button);
		mLoginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mProgressDialog = new ProgressDialog(MainActivity.this);
				mProgressDialog.setIndeterminate(true);
				mProgressDialog.setTitle("Connecting XMPP");
				mProgressDialog.setMessage("please wait...");
				mProgressDialog.show();
				if (!mMxaConnected) {
					MXAController.get().connectMXA(MainActivity.this,
							MainActivity.this);
				} else {
					try {
						mXMPPService
								.registerConnectionCallback(mXMPPConnectionListener);
						mXMPPService.connect(new Messenger(mMxaHandler));
					} catch (RemoteException e) {
						Log.e(TAG,
								"Remote Exception while fetching the xmppservice "
										+ e.getMessage());
						mProgressDialog.dismiss();
					}
				}

			}
		});
		mLoginButton.setEnabled(false);

		mLogoffButton = (Button) findViewById(R.id.main_activity_logout_button);
		mLogoffButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mXMPPConnected) {
					mProgressDialog = new ProgressDialog(MainActivity.this);
					mProgressDialog.setIndeterminate(true);
					mProgressDialog.setTitle("Disconnecting XMPP");
					mProgressDialog.setMessage("please wait...");
					mProgressDialog.show();
					try {
						mXMPPService.disconnect(new Messenger(mMxaHandler));
					} catch (RemoteException e) {
						Log.e(TAG,
								"Remote Exception while fetching the xmppservice "
										+ e.getMessage());
						mProgressDialog.dismiss();
					}
				}
			}
		});
		mLogoffButton.setEnabled(false);

		mPreferencesButton = (Button) findViewById(R.id.main_activity_preferences_button);
		mPreferencesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this,
						PreferencesClient.class);
				startActivity(i);

			}
		});

		mStatisticsButton = (Button) findViewById(R.id.main_activity_statistics_button);
		mStatisticsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this,
						StatisticsActivity.class);
				startActivity(i);

			}
		});

		mSetupButton = (Button) findViewById(R.id.main_activity_setup_button);
		mSetupButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, SetupBasics.class);
				startActivity(i);

			}
		});
		
		mDeleteMessagesButton = (Button) findViewById(R.id.main_activity_deletemessages_button);
		mDeleteMessagesButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deleteDatabase("messages.db");
			}
		});

		// connect to MXA
		if (!mMxaConnected) {
			MXAController.get()
					.connectMXA(MainActivity.this, MainActivity.this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			if (mXMPPConnected) {
				mXMPPConnected = MXAController.get().getXMPPService()
						.isConnected();
			}
		} catch (RemoteException e) {
			mXMPPConnected = false;
		}

		updateUI();
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			if (mXMPPService != null) {
				mXMPPService
						.unregisterConnectionCallback(mXMPPConnectionListener);
				if (isFinishing()) {
					try {
						unbindService(MXAController.get());
					} catch (IllegalArgumentException e) {
						// do nothing, service simply has not been registered yet
					}
				}
			}
		} catch (RemoteException e) {
			showMessage("Error pausing activity: " + e.getMessage());
		}
	}

	private Handler mGuiHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ConstMXA.MSG_CONNECT:
				if (msg.arg1 == ConstMXA.MSG_STATUS_SUCCESS) {
					mXMPPConnected = true;
					if (mProgressDialog != null)
						mProgressDialog.dismiss();
					Toast.makeText(MainActivity.this, "XMPP connected",
							Toast.LENGTH_SHORT).show();
				} else if (msg.arg1 == ConstMXA.MSG_STATUS_SUCCESS) {
					Bundle data = msg.getData();
					String reason = data
							.getString(ConstMXA.EXTRA_ERROR_MESSAGE);
					String errorMessage;
					if (reason == null)
						errorMessage = "Failure during connection";
					else
						errorMessage = "Failure: " + reason;
					// Toast.makeText(MainActivity.this, errorMessage,
					// Toast.LENGTH_SHORT).show();
					if (mProgressDialog != null)
						mProgressDialog.dismiss();
					Toast.makeText(MainActivity.this, "XMPP disconnected",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case ConstMXA.MSG_DISCONNECT:
				mXMPPConnected = false;

				if (mProgressDialog != null)
					mProgressDialog.dismiss();
				Toast.makeText(MainActivity.this, "XMPP disconnected",
						Toast.LENGTH_SHORT).show();
			}
			// since this is an ui handler, we must update the user interface
			updateUI();
		}

	};

	private Handler mMxaHandler = new Handler() {
		public void handleMessage(Message msg) {

			Message m = Message.obtain(msg);
			mGuiHandler.sendMessage(m);
		}
	};

	@Override
	public void onMXAConnected() {
		mMxaConnected = true;
		mXMPPService = MXAController.get().getXMPPService();
		try {
			mXMPPService.registerConnectionCallback(mXMPPConnectionListener);

			mXMPPConnected = mXMPPService.isConnected();
		} catch (RemoteException e) {
			Log.e(TAG, "Remote Exception while fetching the xmppservice");
		}

		updateUI();
	}

	@Override
	public void onMXADisconnected() {
		mMxaConnected = true;

		updateUI();
	}

	private void showMessage(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();

	}

	/**
	 * Updates the buttons according to the mxa status.
	 */
	private void updateUI() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mLoginButton.setEnabled(!mXMPPConnected);
				mLogoffButton.setEnabled(mXMPPConnected);
			}
		});
	}

	private IConnectionCallback mXMPPConnectionListener = new IConnectionCallback.Stub() {

		@Override
		public void onConnectionChanged(boolean connected)
				throws RemoteException {
			mXMPPConnected = connected;
			mGuiHandler.sendEmptyMessage(0);

			updateUI();
		}
	};
}
