package de.inf.tudresden.rn.mobilis.mxaonfire.service;

import de.inf.tudresden.rn.mobilis.mxaonfire.activities.FileReceiveActivity;
import de.inf.tudresden.rn.mobilis.mxaonfire.util.Const;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IFileAcceptCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IFileCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.parcelable.FileTransfer;
import android.R;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class ListeningFileTransferService extends Service implements MXAListener{

	
	private static final String TAG="ListeningFileTransferService";
	private IXMPPService mXMPPService;
	ProgressDialog mProgressDialog;
	private NotificationManager mNotificationManager;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG,"started service");
		MXAController.get().connectMXA(this, this);
		 mNotificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public void onMXAConnected() {
		try{
			mXMPPService=MXAController.get().getXMPPService();
			mXMPPService.getFileTransferService().registerFileCallback(mFileCallback);
			Log.e(TAG,"registered file callback");
		}catch (Exception e) {
			Log.e(TAG,"error binding xmpp service"+e.getMessage());
		}
		
	}



	@Override
	public void onMXADisconnected() {
		// TODO Auto-generated method stub
		
	}

	
	private IFileCallback mFileCallback = new IFileCallback.Stub() {
	
		@Override
		public void processFile(IFileAcceptCallback  fileAcceptCallback, FileTransfer file,
				String streamId) throws RemoteException {
			
			
			//now show in the status bar, that we we have  filetransfer
			Notification status = new Notification(
					R.drawable.stat_notify_chat,
					"file available", System
							.currentTimeMillis());
			Intent notificationIntent = new Intent(ListeningFileTransferService.this, FileReceiveActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(ListeningFileTransferService.this, 0, notificationIntent, 0);
			
	
			status.setLatestEventInfo(
					getApplicationContext(),
					"FileTransfer",
					"File receive",
					contentIntent);
			
			
			status.icon = R.drawable.stat_notify_chat;
			mNotificationManager.notify(Const.NOTIFICATION_FILE_TRANSFER,status);

			FileTransferManager.get().insert(fileAcceptCallback, file, streamId);
			
		}
	};
	
	
}
