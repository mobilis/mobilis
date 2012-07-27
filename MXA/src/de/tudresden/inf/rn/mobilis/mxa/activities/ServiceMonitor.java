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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.R;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IConnectionCallback;

/**
 * A simple activity for toggling the XMPP connection state.
 * @author Istvan Koren
 */
public class ServiceMonitor extends Activity implements MXAListener,
		OnCheckedChangeListener {

	// views
	ToggleButton mTBtnConnection;

	// members
	private Messenger mStdAckMessenger;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.servicemonitor);

		mStdAckMessenger = new Messenger(mAckHandler);

		// initialize members for UI elements.
		initResourceRefs();

		// bind XMPP service
		MXAController.get().connectMXA(getApplicationContext(), this);

		toggleTBtnConnection();
	}

	// ==========================================================
	// Private methods
	// ==========================================================

	/**
	 * Initialize all UI elements from resources.
	 */
	private void initResourceRefs() {
		mTBtnConnection = (ToggleButton) findViewById(R.id.sm_tbtn_connection);
		mTBtnConnection.setEnabled(false);
		toggleTBtnConnection();
		mTBtnConnection.setOnCheckedChangeListener(this);
	}

	// private boolean isServiceRunning() {
	// ActivityManager a = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	// List<RunningServiceInfo> servicesInfo = a.getRunningServices(15);
	// // TODO check if XMPPRemoteService is included in servicesInfo
	// return false;
	// }

	private void toggleTBtnConnection() {
		if (MXAController.get().getXMPPService() != null) {
			// check if XMPP is online
			try {
				if (MXAController.get().getXMPPService().isConnected()) {
					mTBtnConnection.setChecked(true);
				} else {
					mTBtnConnection.setChecked(false);
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mTBtnConnection.setEnabled(true);
		} else {
			// service is not yet connected
			mTBtnConnection.setChecked(false);
		}
	}

	// ==========================================================
	// Inner classes
	// ==========================================================

	/**
	 * Receives the IQ results from the XMPP service and incoming IQs from the
	 * XMPP IQ callback.
	 */
	private Handler mConnectionHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ConstMXA.MSG_CONN_CHANGED:
				mTBtnConnection.setEnabled(true);
				toggleTBtnConnection();
				break;
			}
		}
	};

	/**
	 * This implementation is used to receive Connection callbacks from the
	 * remote service.
	 */
	private IConnectionCallback mConnectionCallback = new IConnectionCallback.Stub() {

		@Override
		public void onConnectionChanged(boolean connected)
				throws RemoteException {
			Message msg = Message.obtain(mConnectionHandler,
					ConstMXA.MSG_CONN_CHANGED);
			msg.arg1 = connected ? 1 : 0;
			msg.sendToTarget();
		}
	};

	/**
	 * Receives the acknowledgements from the XMPP service.
	 */
	private Handler mAckHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mTBtnConnection.setEnabled(true);
		}
	};

	// ==========================================================
	// Interface methods
	// ==========================================================

	@Override
	public void onMXAConnected() {
		// register listeners
		try {
			MXAController.get().getXMPPService().registerConnectionCallback(
					mConnectionCallback);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mTBtnConnection.setEnabled(true);
		toggleTBtnConnection();
	}

	@Override
	public void onMXADisconnected() {
		// service's context crashed

		mTBtnConnection.setEnabled(false);
		toggleTBtnConnection();
	}

	@Override
	public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
		if (btn == mTBtnConnection) {
			// check new checked status
			if (isChecked) {
				// connect
				try {
					MXAController.get().getXMPPService().connect(
							mStdAckMessenger);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// disconnect
				try {
					MXAController.get().getXMPPService().disconnect(
							mStdAckMessenger);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mTBtnConnection.setEnabled(false);
		}	
	}
}
