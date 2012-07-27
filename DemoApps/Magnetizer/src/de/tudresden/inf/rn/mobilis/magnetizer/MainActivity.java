/*******************************************************************************
 * Copyright (C) 2010 Technische Universität Dresden
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 ******************************************************************************/

package de.tudresden.inf.rn.mobilis.magnetizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import de.tudresden.inf.rn.mobilis.magnetizer.activities.FriendsList;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.services.parcelable.FileTransfer;
/**
 * 
 * @author Istvan Koren
 *
 */
public class MainActivity extends Activity implements MXAListener {

	// views
	private Button mBtnConnect;
	private Button mBtnFriends;
	private Button mBtnSendFile;
	private Button mBtnPreferences;

	// members
	private Messenger mStdAckMessenger;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mStdAckMessenger = new Messenger(xmppResultHandler);

		// bind XMPP service
		MXAController.get().connectMXA(getApplicationContext(), this);

		// link views
		mBtnConnect = (Button) findViewById(R.id.main_btn_connect);
		mBtnConnect.setOnClickListener(new OnClickListener() {
			// connect to the service and go online
			@Override
			public void onClick(View view) {
				// connect online
				try {
					MXAController.get().getXMPPService().connect(
							mStdAckMessenger);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		mBtnFriends = (Button) findViewById(R.id.main_btn_friends);
		mBtnFriends.setOnClickListener(new OnClickListener() {
			// show FriendsList Activity
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this, FriendsList.class);
				startActivity(i);
			}
		});
		mBtnSendFile = (Button) findViewById(R.id.main_btn_sendfile);
		mBtnSendFile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, FriendsList.class);
				startActivityForResult(i, 0);
			}
		});
		mBtnPreferences = (Button) findViewById(R.id.main_btn_preferences);
		mBtnPreferences.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(ConstMXA.INTENT_PREFERENCES);
				i.addCategory(Intent.CATEGORY_PREFERENCE);
				startActivity(i);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		// if (isServiceConnected)
		// unbindService(MXAController.get());
	}

	private Handler xmppResultHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(MainActivity.this, "XMPP connected",
					Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			String id = data.getStringExtra(Const.SELECTED_XMPPID);
			FileTransfer ft = new FileTransfer();
			ft.to = id + "/MATLAB";
			ft.description = "Test file";
			ft.path = "/sdcard/testfile.txt";
			ft.blockSize = 4096;
			try {
				MXAController.get().getXMPPService().getFileTransferService()
						.sendFile(mStdAckMessenger, 0, ft);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onMXAConnected() {

	}

	@Override
	public void onMXADisconnected() {

	}
}