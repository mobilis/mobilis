package de.inf.tudresden.rn.mobilis.mxaonfire.activities;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import de.inf.tudresden.rn.mobilis.mxaonfire.R;
import de.inf.tudresden.rn.mobilis.mxaonfire.util.Const;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IFileAcceptCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IFileCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.filetransfer.IFileTransferService;
import de.tudresden.inf.rn.mobilis.mxa.services.parcelable.ByteStream;
import de.tudresden.inf.rn.mobilis.mxa.services.parcelable.FileTransfer;


/**
 * This activity gets the target and the current user to transfer
 * the selected file to it. 
 * @author Christian Magenheimer
 *
 */
public class FileTransferActivity extends Activity {
 
	private final static String TAG = "FileTransferActivity";
	private String mUserXMPPID;
	private String mPartnerXMPPID;
	private	TextView mTv;
	private ProgressBar mBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_transfer_layout);
		mTv= (TextView)findViewById(R.id.file_transfer_info_tv);
		Bundle extras=getIntent().getExtras();
		
		//tv.setText("FileTransferActivity");
		mUserXMPPID=extras.getString(Const.USER_XMPPID);
		mPartnerXMPPID=extras.getString(Const.PARTNER_XMPPID);
		mTv.append("\nPartner: "+mPartnerXMPPID);
		mTv.append("\nUser: "+mUserXMPPID);
		
				
		
		FileTransferAcceptor acceptor= new FileTransferAcceptor();
		acceptor.registerHandler(mAcceptFileHandler);
		try {
			//register the callback
			MXAController.get().getXMPPService().getFileTransferService().registerFileCallback(acceptor);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		
		//start selecting the file
		Intent i= new Intent(FileTransferActivity.this,FileChooserActivity.class);
		startActivityForResult(i, 1);
		
		//do a progress menu
		mBar= (ProgressBar) findViewById(R.id.file_transfer_progress_bar);
		
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//we have successfully selected a file
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				File f = new File(data.getExtras().getString(Const.FILE_NAME));
				Toast.makeText(FileTransferActivity.this, f.getPath(),
						Toast.LENGTH_SHORT).show();
				if (f.exists() && f.isFile()) {
					try {
						//try to send it
						mTv.append("\nFile: "+f.getAbsolutePath());
						Log.v(TAG,"L��nge: "+String.valueOf(f.length()));
						//count of blocks is length/1000
						mBar.setMax((int)(f.length()/1000.0));
						IFileTransferService fts = MXAController.get()
								.getXMPPService().getFileTransferService();
						FileTransfer ft = new FileTransfer(mUserXMPPID,
								mPartnerXMPPID, "a file",
								f.getAbsolutePath(), "text/plain", 1000,
								f.length());

						fts.sendFile(new Messenger(xmppFileHandler), 0, ft);
					} catch (RemoteException e) {
						Log.i(TAG, e.toString());
					}
				}
			}
		}
	}

	private Handler xmppFileHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//every block is acked here
			switch (msg.arg1) {
			case ConstMXA.MSG_STATUS_ERROR:
				Bundle b = msg.getData();
				String reason = b.getInt("ERRORCODE") + " "
						+ b.getString("ERRORMESSAGE");
				Toast.makeText(FileTransferActivity.this, reason,
						Toast.LENGTH_LONG).show();
			case ConstMXA.MSG_STATUS_DELIVERED:
				mTv.append("\n File succesfully transfered!");
				break;
			case ConstMXA.MSG_STATUS_SUCCESS:
				//means there were blocks acked
				Bundle b2 = msg.getData();
				Log.v(TAG,"transferiert bytes"+String.valueOf((int)b2.getLong("BYTESTRANSFERRED")));
				Log.v(TAG,"transferiert bl��cke"+String.valueOf((int)b2.getInt("BLOCKSTRANSFERRED")));
				if (b2.getInt("BLOCKSTRANSFERRED")>0)mBar.setProgress((int)b2.getInt("BLOCKSTRANSFERRED"));
				break;
			}
		}
	};

	
	private Handler mAcceptFileHandler=new Handler()
	{
		public void handleMessage(Message msg) {
			//since everything in android is asynchronous, this handler is registered with the inner class FileTransferAccepter
			Toast.makeText(FileTransferActivity.this, msg.toString(), Toast.LENGTH_SHORT).show();
			mTv.append(msg.toString());
		}
	};
	
	class FileTransferAcceptor extends IFileCallback.Stub{

		private Handler handler;
		
		@Override
		public void processFile(IFileAcceptCallback acceptCallback,
				ByteStream file, String streamID) throws RemoteException {
			Message msg= Message.obtain();
			msg.what=ConstMXA.MSG_SEND_FILE;
			
			if (file instanceof FileTransfer) {
				FileTransfer fileTransfer = (FileTransfer) file;
				//register a file transfer service with mxa, so incoming packets are treated here
				Log.e("FileTransferActivity", "XEP-0096 File Transfer: " + file.from+" "+file.to+" "+fileTransfer.size+" "+fileTransfer.description+" "+fileTransfer.mimeType+" "+fileTransfer.path);
				Log.e("FileTransferActivity", streamID+" "+acceptCallback.toString());
				acceptCallback.acceptFile(new Messenger(acceptFileHandler), 0, streamID, "/mnt/sdcard/download/"+fileTransfer.path,  1); 
//			Toast.makeText(FileTransferActivity.this, file.toString(),Toast.LENGTH_SHORT).show();
			} else {
				//register a file transfer service with mxa, so incoming packets are treated here
				Log.e("FileTransferActivity", "XEP-0065 File Transfer: " + file.from+" "+file.to);
				Log.e("FileTransferActivity", streamID+" "+acceptCallback.toString());
				acceptCallback.acceptFile(new Messenger(acceptFileHandler), 0, streamID, "/mnt/sdcard/download/"+System.currentTimeMillis(),  1); 
			}
			 
		}
		
		public void registerHandler(Handler h)
		{
			this.handler=h;
		}
		
		private Handler acceptFileHandler= new Handler(){
			
			public void handleMessage(Message msg) {
				Log.e("FileTransferActivity",msg.toString());
				Message newmsg= Message.obtain(msg);
				handler.sendMessage(newmsg);
			};
		};
		
	}
}
