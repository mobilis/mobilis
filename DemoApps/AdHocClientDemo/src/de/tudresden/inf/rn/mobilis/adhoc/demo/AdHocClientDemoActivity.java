/**
 * Copyright (C) 2009 Technische Universit�t Dresden
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

package de.tudresden.inf.rn.mobilis.adhoc.demo;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import de.tudresden.inf.rn.mobilis.adhoc.demo.ColorPickerDialog.OnColorChangedListener;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPPresence;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IServerlessMessageCallback;

public class AdHocClientDemoActivity extends Activity implements MXAListener {

	// Tag for log information
	private static String TAG = "AdHocClientDemoActivity";

	// views
	private LinearLayout mLytBackground;
	private Button mBtnPublish;
	private Button mBtnUnregister;
	private Button mBtnSelectColor;

	// members
	private MXAController mMXAController;
	private Handler mHandler = new Handler();
	ServerlessMessageCallback mCallback = new ServerlessMessageCallback();
	private SetColorMessage mColorMessage = new SetColorMessage(0xFFFF0000);

	/** Called when the activity is first created. */
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mLytBackground = (LinearLayout) findViewById(R.id.main_lyt_background);
		mBtnPublish = (Button) findViewById(R.id.main_btn_register);
		mBtnPublish.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				XMPPPresence presence = new XMPPPresence();
				try {
					mMXAController.getXMPPService()
							.getServerlessMessagingService()
							.registerPresence(presence);

					mMXAController.getXMPPService()
							.getServerlessMessagingService()
							.registerMessageCallback(mCallback);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});

		mBtnUnregister = (Button) findViewById(R.id.main_btn_unregister);
		mBtnUnregister.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				try {
					mMXAController.getXMPPService()
							.getServerlessMessagingService()
							.unregisterMessageCallback(mCallback);

					mMXAController.getXMPPService()
							.getServerlessMessagingService()
							.unregisterPresence();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});

		mBtnSelectColor = (Button) findViewById(R.id.main_btn_selectcolor);
		mBtnSelectColor.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				ColorPickerDialog dlg = new ColorPickerDialog(v.getContext(),
						new OnColorChangedListener() {
							public void colorChanged(int color) {
								final int currentColor = color;
								mHandler.post(new Runnable() {

									@Override
									public void run() {
										mLytBackground
												.setBackgroundColor(currentColor);
										mColorMessage.setColor(currentColor);

										ArrayList<String> peers = new ArrayList<String>();
										try {
											mMXAController
													.getXMPPService()
													.getServerlessMessagingService()
													.getPeers(peers);

											for (String peer : peers) {
												mMXAController
														.getXMPPService()
														.getServerlessMessagingService()
														.sendMessage(
																peer,
																mColorMessage
																		.toXML());
											}
										} catch (RemoteException e) {
											e.printStackTrace();
										}
									}
								});
							}
						}, mColorMessage.getColor());
				dlg.show();
			}
		});

		mMXAController = MXAController.get();
		mMXAController.connectMXA(getApplicationContext(), this);
	}

	// ==========================================================
	// Interface methods MXAListener
	// ==========================================================

	public void onMXAConnected() {
		Log.i(TAG, "MXA connected");
		Toast.makeText(this, "MXA connected", Toast.LENGTH_LONG).show();
	}

	public void onMXADisconnected() {
		Log.i(TAG, "MXA disconnected");
		Toast.makeText(this, "MXA disconnected", Toast.LENGTH_LONG).show();
	}

	// ==========================================================
	// Inner classes
	// ==========================================================

	class ServerlessMessageCallback extends IServerlessMessageCallback.Stub {

		public void processMessage(String message) throws RemoteException {
			mColorMessage.setPayload(message);
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					mLytBackground.setBackgroundColor(mColorMessage.getColor());
				}
			});
		}

	}

}