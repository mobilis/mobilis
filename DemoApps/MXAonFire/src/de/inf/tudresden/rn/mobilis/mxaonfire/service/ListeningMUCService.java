package de.inf.tudresden.rn.mobilis.mxaonfire.service;

import java.util.List;

import de.inf.tudresden.rn.mobilis.mxaonfire.activities.FileReceiveActivity;
import de.inf.tudresden.rn.mobilis.mxaonfire.activities.MultiUserChatActivity;
import de.inf.tudresden.rn.mobilis.mxaonfire.util.Const;
import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPMessage;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IInvitationCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.multiuserchat.IMultiUserChatService;
import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


/**
 * Listens for MUC invitations and notifies the user about them.
 * @author Christian Magenheimer
 *
 */
public class ListeningMUCService extends Service implements MXAListener{

	private static String TAG="ListeningMUCService";
	private IXMPPService mXMPPService;
	private NotificationManager mNotificationManager;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
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

	private IInvitationCallback mInvitationCallback= new IInvitationCallback.Stub() {
		
		@Override
		public void onInvitationReceived(String room, String inviter, String reason, String password,XMPPMessage message) throws RemoteException {
			
			//now show in the status bar, that we we have  filetransfer
			Notification status = new Notification(
					R.drawable.stat_notify_chat,
					"Invitation to MUC received", System
							.currentTimeMillis());
			Intent notificationIntent = new Intent(ListeningMUCService.this, MultiUserChatActivity.class);
			notificationIntent.putExtra("ROOM", room);
			notificationIntent.putExtra("INVITER", inviter);
			notificationIntent.putExtra("REASON", reason);
			notificationIntent.putExtra("PASSWORD",password);
			PendingIntent contentIntent = PendingIntent.getActivity(ListeningMUCService.this, 0, notificationIntent, 0);

	
			status.setLatestEventInfo(
					getApplicationContext(),
					"MultiUserChat",
					"Invitation to "+room,
					contentIntent);
			
			status.icon = R.drawable.stat_notify_chat;
			mNotificationManager.notify(Const.NOTIFICATION_MUC_AVAILABLE,status);
			
		}
	};

	@Override
	public void onMXAConnected() {
		try{
			mXMPPService=MXAController.get().getXMPPService();
			mXMPPService.getMultiUserChatService().registerInvitationCallback(mInvitationCallback);
		}catch (Exception e) {
			Log.e(TAG,"error binding xmmpservice: +"+e.getMessage());
		}
		
	}

	@Override
	public void onMXADisconnected() {
		// TODO Auto-generated method stub
		
	}
}
