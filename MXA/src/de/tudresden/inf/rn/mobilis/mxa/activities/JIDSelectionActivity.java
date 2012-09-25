package de.tudresden.inf.rn.mobilis.mxa.activities;

import java.util.List;

import org.jivesoftware.smack.util.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IResourcesCallback;

public class JIDSelectionActivity extends Activity implements OnCancelListener,
		MXAListener {

	// Tag for log information
	private static String TAG = "JIDSelectionActivity";

	// views
	private AlertDialog mDialog;

	// members
	private MXAController mMXAController;
	private String mJID;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// show dialog
		showMethodDialog();

		mMXAController = MXAController.get();
		mMXAController.connectMXA(getApplicationContext(), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// check scan result
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, data);
		if (scanResult != null) {
			// send invite request to scanned jid
			// TODO: check if a fully qualified JID was returned

			setResultJID(scanResult.getContents());
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		mDialog.dismiss();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		setResult(RESULT_CANCELED);
		finish();
	}

	// ==========================================================
	// Interface methods MXAListener
	// ==========================================================

	public void onMXAConnected() {
		Log.i(TAG, "MXA connected");
	}

	public void onMXADisconnected() {
		Log.i(TAG, "MXA disconnected");
	}

	// ==========================================================
	// Inner classes
	// ==========================================================

	class ResourcesCallback extends IResourcesCallback.Stub {

		@Override
		public void onResourcesResult(List<String> resources)
				throws RemoteException {

			CharSequence[] resourcesChars = new CharSequence[resources.size()];
			resources.toArray(resourcesChars);
			showResourceDialog(resourcesChars);
		}
	}

	// ==========================================================
	// Private methods
	// ==========================================================

	/**
	 * Shows a dialog to let the user choose what method to use to get the JID.
	 */
	private void showMethodDialog() {
		final CharSequence[] items = { "Manually", "Scan QR Code" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select a target device");
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					// resource
					try {
						ResourcesCallback resourcesCallback = new ResourcesCallback();

						mJID = mMXAController.getXMPPService().getUsername();
						mMXAController.getXMPPService()
								.getSessionMobilityService()
								.queryResources(resourcesCallback);
						mDialog.dismiss();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// QR code
					IntentIntegrator integrator = new IntentIntegrator(
							JIDSelectionActivity.this);
					integrator.initiateScan();
				}
			}
		});
		mDialog = builder.create();
		mDialog.setOnCancelListener(this);
		mDialog.show();
	}

	/**
	 * Shows a dialog that lets the user choose an alternative resource.
	 */
	private void showResourceDialog(final CharSequence[] items) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (items.length > 0) {
			builder.setTitle("Select a resource");
			builder.setItems(items, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String username = StringUtils.parseBareAddress(mJID);

					setResultJID(username + "/" + items[which].toString());
				}
			});
		} else {
			builder.setMessage("No other resource available.");
			builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					setResult(RESULT_CANCELED);
					finish();
				}
			});
		}
		mDialog = builder.create();
		mDialog.setOnCancelListener(this);
		mDialog.show();
	}

	private void setResultJID(String jid) {
		// send invite request to scanned jid
		// TODO: check if a fully qualified JID was returned

		Intent i = new Intent();
		i.putExtra("mobilis:iq:sessionmobility#jid", jid);
		setResult(RESULT_OK, i);
		finish();
	}
}