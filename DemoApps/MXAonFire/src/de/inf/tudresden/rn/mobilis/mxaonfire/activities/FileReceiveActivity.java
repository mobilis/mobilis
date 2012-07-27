package de.inf.tudresden.rn.mobilis.mxaonfire.activities;

import java.text.NumberFormat;
import java.util.logging.SimpleFormatter;

import de.inf.tudresden.rn.mobilis.mxaonfire.service.FileTransferDescriber;
import de.inf.tudresden.rn.mobilis.mxaonfire.service.FileTransferManager;
import de.inf.tudresden.rn.mobilis.mxaonfire.service.ListeningFileTransferService;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;


public class FileReceiveActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG,"blub");
		
		
		FileTransferManager tm= FileTransferManager.get();
		mFtd= tm.getFileTransfer();
		if (mFtd!=null) 
		{
			createAcceptDialog();
		}else
			finish();
		
	}
	private FileTransferDescriber mFtd;
	private static String TAG="FileReceiveActivity";
	ProgressDialog mProgressDialog;
	
	private void createProgressDialog(long size) {
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMessage("Loading...");
		mProgressDialog.setMax((int) size);
		mProgressDialog.show();
		
	}

	private Handler mHandler= new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			Log.v(TAG,msg.toString());
			switch(msg.what)
			{
			case ConstMXA.MSG_SEND_FILE:
				Log.v(TAG,"msg_send_file");
				if (msg.arg1==ConstMXA.MSG_STATUS_SUCCESS)
				{
					Bundle b= msg.getData();
					int blocks=b.getInt("BLOCKSTRANSFERRED");
					long bytes=b.getLong("BYTESTRANSFERRED");
					Log.v(TAG," receiving file: blocks: "+blocks+" bytes:"  +bytes);
					mProgressDialog.setProgress((int) blocks);
				}else if (msg.arg1==ConstMXA.MSG_STATUS_ERROR)
				{
					Bundle b= msg.getData();
					int ec=b.getInt("ERRORCODE");
					String em=b.getString("ERRORMESSAGE");
					Log.v(TAG," receiving file: ec: "+ec+" em: "+em);
				}else if (msg.arg1==ConstMXA.MSG_STATUS_DELIVERED)
				{
					Log.v(TAG,"file transferred");
					mProgressDialog.dismiss();
					finish();
				}
				break;
			}
			
		}
	};
	
	private void createAcceptDialog()
	{
		
		// void acceptFile(in Messenger acknowledgement, in int requestCode, in String streamID, in String path, in int blockSize);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage(mFtd.mFile.from+" sends you a file ("+mFtd.mFile.path+" "+mFtd.mFile.size/1000.0+"KB)")
		.setCancelable(false)
		.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				try {
					mFtd.mFileAcceptCallback.acceptFile(new Messenger(mHandler), 0 , mFtd.mStreamID	, "/mnt/sdcard/mxaonfire/"+mFtd.mFile.path,1000);
					createProgressDialog(mFtd.mFile.size/1000);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		})
		.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				try {
					mFtd.mFileAcceptCallback.denyFileTransferRequest(new Messenger(mHandler), 0, "dont want to");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
