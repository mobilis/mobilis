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

package de.tudresden.inf.rn.mobilis.migration.chat.activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import de.tudresden.inf.rn.mobilis.migration.chat.R;
import de.tudresden.inf.rn.mobilis.migration.chat.util.Const;
import de.tudresden.inf.rn.mobilis.migration.chat.util.ContactsAdapter;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.RosterItems;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IXMPPIQCallback;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPIQ;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.ITransferCallback;

/**
 * @author koren
 * 
 */
public class ContactsActivity extends Activity implements MXAListener {

	// Tag for log information
	private static String TAG = "ContactsActivity";

	// views
	private ListView mLstContacts;

	// members
	private MXAController mMXAController;
	private Cursor mCursorContacts;
	private String mOwnUsername = "";
	private boolean receiverIsRegistered = false;
	private Dialog mDialog;
	private IQCallback mIQCallback;

	// static
	static final int RESOURCE_RESULT = 1;
	static final int DIALOG_QRCODE = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);

		// link all views to members
		findAllViewsByID();

		mMXAController = MXAController.get();
		mMXAController.connectMXA(getApplicationContext(), this);
	}

	private void findAllViewsByID() {
		mLstContacts = (ListView) findViewById(R.id.contacts_lst_contacts);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (getIntent() != null) {
			if (getIntent().hasExtra("mobilis:iq:sessionmobility#from")) {
				Log.i(TAG, "accepting transfer in onResume");
				acceptTransfer(getIntent());
				setIntent(null);
			}
		}

		if (!receiverIsRegistered) {
			IntentFilter filter = new IntentFilter(
					"de.tudresden.inf.rn.mobilis.chatmigration");
			filter.setPriority(4);
			registerReceiver(mReceiver, filter);
			receiverIsRegistered = true;
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contacts, menu);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_contacts_transfer:
			Intent i = new Intent(
					"de.tudresden.inf.rn.mobilis.mxa.JIDSELECTION");
			startActivityForResult(i, RESOURCE_RESULT);
			break;
		case R.id.menu_contacts_qrcode:
			showDialog(DIALOG_QRCODE);
			break;
		}

		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == RESOURCE_RESULT) {
			if (resultCode == RESULT_OK) {
				// send transferIQ
				String target = intent
						.getStringExtra("mobilis:iq:sessionmobility#jid");

				ArrayList<String> mechanisms = new ArrayList<String>();
				mechanisms.add("INBAND-XMPP");
				mechanisms.add("FILES");
				mechanisms.add("BUNDLE-COMPRESSED-ZIP");

				TransferCallback cb = new TransferCallback();

				try {
					mMXAController
							.getXMPPService()
							.getSessionMobilityService()
							.requestTransfer(target, "ChatMigration",
									mechanisms, cb);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (resultCode == RESULT_CANCELED) {
				// handle cancel
				Toast.makeText(this, "Resource selection was canceled.",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private void initContacts() {
		// get own username
		try {
			mOwnUsername = MXAController.get().getXMPPService().getUsername()
					.split("/")[0];
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		mCursorContacts = getContentResolver().query(
				RosterItems.CONTENT_URI,
				null,
				RosterItems.PRESENCE_MODE + "<>\""
						+ RosterItems.MODE_UNAVAILABLE + "\" AND "
						+ RosterItems.XMPP_ID + "<>\"" + mOwnUsername
						+ "\") GROUP BY (" + RosterItems.XMPP_ID, null,
				RosterItems.XMPP_ID + " ASC");
		startManagingCursor(mCursorContacts);

		ContactsAdapter adapter = new ContactsAdapter(this,
				R.layout.contacts_item, mCursorContacts, new String[] {
						RosterItems.NAME, RosterItems.PRESENCE_MODE,
						RosterItems.UPDATED_DATE, RosterItems.PRESENCE_STATUS,
						RosterItems.XMPP_ID }, new int[] {
						R.id.contacts_item_name, R.id.roster_item_image });

		mLstContacts.setAdapter(adapter);

		mLstContacts.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				// v.setBackgroundColor(Color.GRAY);
				String xmppid = mCursorContacts.getString(mCursorContacts
						.getColumnIndex(ConstMXA.RosterItems.XMPP_ID));
				String res = mCursorContacts.getString(mCursorContacts
						.getColumnIndex(ConstMXA.RosterItems.RESSOURCE));
				// if (res != null) {
				// xmppid += "/" + res;
				// }
				Intent i = new Intent(Const.ACTION_SEND);
				i.putExtra(Const.EXTRA_XMPP_ID, xmppid);
				startActivity(i);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (receiverIsRegistered) {
			unregisterReceiver(mReceiver);
			receiverIsRegistered = false;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_QRCODE) {
			String username = "not available";
			try {
				username = mMXAController.getXMPPService().getUsername();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			String imageURL = "https://chart.googleapis.com/chart?cht=qr&chl="
					+ username + "&chs=350x350&choe=UTF-8&chld=L|0";

			mDialog = new Dialog(this);

			mDialog.setContentView(R.layout.qrdialog);
			mDialog.setTitle("Scan this!");

			ImageView image = (ImageView) mDialog
					.findViewById(R.id.qrdialog_img_qr);
			Bitmap bitmap;
			try {
				bitmap = BitmapFactory.decodeStream((InputStream) new URL(
						imageURL).getContent());
				image.setImageBitmap(bitmap);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return mDialog;
		} else {
			return null;
		}
	}

	// ==========================================================
	// Interface methods MXAListener
	// ==========================================================

	public void onMXAConnected() {
		Log.i(TAG, "MXA connected");
		Toast.makeText(this, "MXA connected", Toast.LENGTH_SHORT).show();

		try {
			if (!mMXAController.getXMPPService().isConnected()) {
				mMXAController.getXMPPService().connect(
						new Messenger(mConnectionHandler));
			} else {
				// already connected
				// start the cursor on the Roster
				initContacts();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void onMXADisconnected() {
		Log.i(TAG, "MXA disconnected");
		Toast.makeText(this, "MXA disconnected", Toast.LENGTH_SHORT).show();
		// TODO implement disconnect behavior
	}

	// ==========================================================
	// Inner classes
	// ==========================================================

	private Handler mConnectionHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == ConstMXA.MSG_CONNECT
					&& msg.arg1 == ConstMXA.MSG_STATUS_SUCCESS) {
				// success
				// start the cursor on the Roster
				initContacts();
			}
		};
	};

	private Handler mIncomingIQHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.i(TAG, "incoming IQ");
		}
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "received intent");
			acceptTransfer(intent);
			abortBroadcast();
		}
	};

	class TransferCallback extends ITransferCallback.Stub {

		@Override
		public void onTransferResult(String from, List<String> mechanisms)
				throws RemoteException {
			// send application state
			XMPPIQ stateIQ;
			try {
				Log.i(TAG, "sending state");
				mMXAController.getXMPPService().getSessionMobilityService()
						.sendMessageHistory(from, 0);
				// stateIQ = new XMPPIQ(mMXAController.getXMPPService()
				// .getUsername(), from, XMPPIQ.TYPE_SET, "query",
				// "mobilis:iq:sessionmobility#state",
				// "<appuri>ChatMigration</appuri><state>contained</state>");
				// mMXAController.getXMPPService().sendIQ(null,
				// new Messenger(mIncomingIQHandler), 0, stateIQ);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	class IQCallback extends IXMPPIQCallback.Stub {

		@Override
		public void processIQ(XMPPIQ arg0) throws RemoteException {
			Log.i(TAG, "received application state");
		}

	};

	// ==========================================================
	// Private methods
	// ==========================================================

	private void sendAccept(String from) {
		// send accept to partner
		ArrayList<String> mechanisms = new ArrayList<String>();
		mechanisms.add("INBAND-XMPP");
		try {
			// register IQ callback for application state
			if (mIQCallback == null) {
				mIQCallback = new IQCallback();
				mMXAController.getXMPPService().registerIQCallback(mIQCallback,
						"query", "mobilis:iq:sessionmobility#state");
			}
			// send accept
			mMXAController.getXMPPService().getSessionMobilityService()
					.acceptTransfer(from, "ChatMigration", mechanisms);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void acceptTransfer(Intent intent) {
		if ((mDialog != null) && mDialog.isShowing()) {
			dismissDialog(DIALOG_QRCODE);
		}

		if (intent.hasExtra("mobilis:iq:sessionmobility#from")) {
			final String from = intent
					.getStringExtra("mobilis:iq:sessionmobility#from");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Are you sure you want to transfer your session to this device?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									ContactsActivity.this.sendAccept(from);
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

}
