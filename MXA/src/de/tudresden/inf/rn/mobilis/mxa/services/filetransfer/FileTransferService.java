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

package de.tudresden.inf.rn.mobilis.mxa.services.filetransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.XMPPRemoteService;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IFileAcceptCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IFileCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.parcelable.FileTransfer;

/**
 * @author Benjamin Söllner, Istvan Koren
 */
public class FileTransferService extends Service {

	private static final String TAG = "FileTransferService";

	private final XMPPRemoteService mXMPPService;
	FileTransferManager mFileTransferManager;
	private ReaderThread mXMPPReadWorker;
	private HashSet<HangingFileTransfer> mHangingFileTransfers = new HashSet<HangingFileTransfer>();
	private SparseArray<FileTransferRequest> mXMPPWaitingIncomingFileTransfers = new SparseArray<FileTransferRequest>();
	/**
	 * Remote callback list for file listeners.
	 */
	private final RemoteCallbackList<IFileCallback> mFileCallbacks = new RemoteCallbackList<IFileCallback>();

	// private Map<Integer, OutgoingFileTransferThread>
	// xmppOutgoingFileTransferThreads = Collections
	// .synchronizedMap(new HashMap<Integer, OutgoingFileTransferThread>());

	public FileTransferService(XMPPRemoteService service) {
		mXMPPService = service;

		mXMPPReadWorker = new ReaderThread();

		mFileTransferManager = new FileTransferManager(mXMPPService
				.getXMPPConnection());
		FileTransferNegotiator.setServiceEnabled(mXMPPService
				.getXMPPConnection(), true);
		mFileTransferManager.addFileTransferListener(mXMPPReadWorker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent i) {
		return mBinder;
	}

	private final IFileTransferService.Stub mBinder = new IFileTransferService.Stub() {

		@Override
		public void sendFile(Messenger messenger, int requestCode,
				FileTransfer file) throws RemoteException {
			FileTransferRunner ftr = new FileTransferRunner(file, messenger, requestCode);
			mXMPPService.getWriteExecutor().execute(ftr);
		}

		@Override
		public void registerFileCallback(IFileCallback cb)
				throws RemoteException {
			if (cb != null) {
				mFileCallbacks.register(cb);
			}
		}

		@Override
		public void unregisterFileCallback(IFileCallback cb)
				throws RemoteException {
			if (cb != null) {
				mFileCallbacks.unregister(cb);
			}
		}
	};

	// ==========================================================
	// Private methods
	// ==========================================================

	// ==========================================================
	// Inner classes
	// ==========================================================

	private static class HangingFileTransfer {
		public String streamID;
		public FileTransferRequest request;
		public long requestTime;

		public HangingFileTransfer(FileTransferRequest request) {
			streamID = request.getStreamID();
			requestTime = System.currentTimeMillis();
			this.request = request;
		}

		/**
		 * When adding the HangingFileTransfer to the HashSet, the HashSet will
		 * check if the entry is already present. This equals implementation
		 * avoids reusing existing streamIDs for illegally infiltrating the
		 * accept.
		 */
		@Override
		public boolean equals(Object o) {
			if (o instanceof HangingFileTransfer) {
				return ((HangingFileTransfer) o).streamID.equals(streamID);
			}
			return false;
		}
	}

	private IFileAcceptCallback mFileAcceptCallback = new IFileAcceptCallback.Stub() {

		@Override
		public void acceptFile(Messenger acknowledgement, int requestCode,
				String streamID, String path, int blockSize)
				throws RemoteException {
			synchronized (mHangingFileTransfers) {
				for (HangingFileTransfer hft : mHangingFileTransfers) {
					if (hft.streamID.equals(streamID)) {
						FileAcceptRunner far = new FileAcceptRunner(
								acknowledgement, requestCode, streamID, path,
								blockSize);
						mXMPPService.getWriteExecutor().execute(far);
						break;
					}
				}
			}
		}

		@Override
		public void denyFileTransferRequest(Messenger acknowledgement,
				int requestCode, String streamID) throws RemoteException {
			synchronized (mHangingFileTransfers) {
				for (HangingFileTransfer hft : mHangingFileTransfers) {
					if (hft.streamID.equals(streamID)) {
						FileDenyRunner fdr = new FileDenyRunner(
								acknowledgement, requestCode, streamID);
						mXMPPService.getWriteExecutor().execute(fdr);
						break;
					}
				}
			}
		}
	};

	private class ReaderThread extends Thread implements FileTransferListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.jivesoftware.smackx.filetransfer.FileTransferListener#
		 * fileTransferRequest
		 * (org.jivesoftware.smackx.filetransfer.FileTransferRequest)
		 */
		@Override
		public void fileTransferRequest(FileTransferRequest ftr) {
			// create a HangingFileTransfer object and add it to the HashSet
			HangingFileTransfer hft = new HangingFileTransfer(ftr);
			boolean added = mHangingFileTransfers.add(hft);

			// don't notify callback listeners if the streamID was already used
			if (added) {
				FileTransfer file = new FileTransfer(ftr.getRequestor(), "",
						ftr.getDescription(), ftr.getFileName(), ftr
								.getMimeType(), -1, ftr.getFileSize());

				int i = mFileCallbacks.beginBroadcast();
				while (i > 0) {
					i--;
					try {
						IFileCallback ifc = mFileCallbacks.getBroadcastItem(i);
						ifc.processFile(mFileAcceptCallback, file, ftr.getStreamID());
					} catch (RemoteException e) {
						// The RemoteCallbackList will take care of
						// removing the dead object for us.
					}
				}
				mFileCallbacks.finishBroadcast();
			}
		}

	};

	private class FileTransferRunner implements Runnable {

		private static final String TAG = "FileTransferRunner";

		private FileTransfer file;
		private Messenger messenger;
		private int requestCode;

		/**
		 * 
		 * @param msg
		 */
		public FileTransferRunner(FileTransfer file, Messenger messenger, int requestCode) {
			this.file = file;
			this.messenger = messenger;
			this.requestCode = requestCode;
		}

		private void sendSuccessMessage(int arg1, int blocksTransferred, long bytesTransferred) {
			Message m = Message.obtain();
			m.what = ConstMXA.MSG_SEND_FILE;
			m.arg1 = arg1;
			m.arg2 = this.requestCode;
			Bundle b = new Bundle();
			b.putLong("BYTESTRANSFERRED", bytesTransferred);
			b.putInt("BLOCKSTRANSFERRED", blocksTransferred);
			m.setData(b);
			try { this.messenger.send(m); } catch (RemoteException e) {}
		}

		private void sendErrorMessage(int errorCode, String errorMessage) {
			Log.e(TAG, errorMessage);
			Message m = Message.obtain();
			m.what = ConstMXA.MSG_SEND_FILE;
			m.arg1 = ConstMXA.MSG_STATUS_ERROR;
			m.arg2 = this.requestCode;
			Bundle b = new Bundle();
			b.putInt("ERRORCODE", errorCode);
			b.putString("ERRORMESSAGE", errorMessage);
			b.putParcelable("MSN_ACK", this.messenger);
			b.putParcelable("MSN_RESULT", this.messenger);
			m.setData(b);
			try { this.messenger.send(m); } catch (RemoteException e) {}
		}

		public void run() {
			Log.i(TAG, "sending file");
			FileTransferManager manager = mFileTransferManager;
			OutgoingFileTransfer transfer = manager
					.createOutgoingFileTransfer(this.file.to);
			// access file object and get file input stream
			File fileObject;
			FileInputStream fileStream;
			try {
				fileObject = new File(this.file.path);
				fileStream = new FileInputStream(fileObject);
			} catch (IllegalArgumentException e) {
				this.sendErrorMessage(10, "File '" + this.file.path
						+ "' not accessible.");
				return;
			} catch (FileNotFoundException e) {
				this.sendErrorMessage(11, "File '" + this.file.path
						+ "' not found.");
				return;
			}
			// negotiate transfer and get transfer output stream
			OutputStream transferStream = null;
			this.sendSuccessMessage(ConstMXA.MSG_STATUS_SUCCESS, -1, 0);
			boolean error = false;
			try {
				transferStream = transfer.sendFile(fileObject.getName(),
						fileObject.length(), this.file.description);
			} catch (XMPPException e) {
				// recipient doesn't support file transfers or didn't accept
				error = true;
			}
			if (error || transferStream == null) {
				this.sendErrorMessage(20,
						"File transfer negotiation failed for '"
								+ fileObject.getName() + "'.");
				try {
					fileStream.close();
				} catch (IOException e) {
					Log
							.e(OutgoingFileTransfer.class.getName(),
									"Unexpected exception thrown after exception cleanup.");
				}
				return;
			}
			// negotiation successful, inform about success
			this.sendSuccessMessage(ConstMXA.MSG_STATUS_SUCCESS, 0, 0);
			// transfer block-by-block, inform about success after each block
			byte[] buffer = new byte[file.blockSize];
			int actuallyRead = 0;
			int blockNumber = 0;
			do {
				try {
					actuallyRead = fileStream.read(buffer, 0, file.blockSize);
					if (actuallyRead != -1)
						transferStream.write(buffer, 0, actuallyRead);
				} catch (IOException e) {
					this.sendErrorMessage(30,
							"There was an error during the file transfer of '"
									+ fileObject.getName() + "'.");
					try {
						fileStream.close();
						transferStream.close();
					} catch (IOException e1) {
						Log
								.e(OutgoingFileTransfer.class.getName(),
										"Unexpected exception thrown after exception cleanup.");
					}
					return;
				}
				this.sendSuccessMessage(ConstMXA.MSG_STATUS_SUCCESS, blockNumber + 1,
						(actuallyRead != -1 ? actuallyRead : 0));
				blockNumber++;
			} while (actuallyRead != -1);
			// close stream
			try {
				transferStream.close();
				fileStream.close();
			} catch (IOException e) {
				Log.e(OutgoingFileTransfer.class.getName(),
						"Couldn't close streams properly.");
			}
			// file transferred, inform about successful delivery.
			this.sendSuccessMessage(ConstMXA.MSG_STATUS_DELIVERED, blockNumber, actuallyRead);
		}
	}

	private class FileAcceptRunner implements Runnable {

		private Messenger messenger;
		private int requestCode;
		private String streamID;
		private String path;
		private int blockSize;

		public FileAcceptRunner(Messenger messenger, int requestCode,
				String streamID, String path, int blockSize) {
			this.messenger = messenger;
			this.requestCode = requestCode;
			this.streamID = streamID;
			this.path = path;
			this.blockSize = blockSize;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// get FileTransferRequest for streamID
			HangingFileTransfer hft = null;
			synchronized (mHangingFileTransfers) {
				for (HangingFileTransfer h : mHangingFileTransfers) {
					if (h.streamID.equals(streamID)) {
						hft = h;
						break;
					}
				}
			}
			if (hft != null) {
				FileTransferRequest ftr = hft.request;
				IncomingFileTransfer transfer = ftr.accept();
				File fileObject;
				FileOutputStream fileStream;
				try {
					fileObject = new File(this.path);
					fileStream = new FileOutputStream(fileObject);
				} catch (IOException e) {
					this.sendErrorMessage(12, "File '" + this.path
							+ "' cannot be opened for writing.");
					return;
				}
				// negotiate transfer and get transfer output stream
				InputStream transferStream = null;
				Log.i("MMedia measurement", "MXAin\tinitiated\t"+System.currentTimeMillis());
				this.sendSuccessMessage(ConstMXA.MSG_STATUS_SUCCESS, -1, 0);
				boolean error = false;
				try {
					transferStream = transfer.recieveFile();
				} catch (XMPPException e) {
					error = true;
				}
				if (error || transferStream == null) {
					this.sendErrorMessage(20,
							"File transfer negotiation failed for '"
									+ ftr.getFileName() + "'.");
					try {
						fileStream.close();
					} catch (IOException e) {
						Log
								.e(OutgoingFileTransfer.class.getName(),
										"Unexpected exception thrown after exception cleanup.");
					}
					return;
				}
				// negotiation successful, inform about success
				Log.i("MMedia measurement", "MXAin\tnegotiated\t"+System.currentTimeMillis());
				this.sendSuccessMessage(ConstMXA.MSG_STATUS_SUCCESS, 0, 0);
				// transfer block-by-block, inform about success after each
				// block
				byte[] buffer = new byte[this.blockSize];
				int actuallyRead = 0;
				int blockNumber = 0;
				do {
					try {
						actuallyRead = transferStream
								.read(buffer, 0, blockSize);
						if (actuallyRead != -1)
							fileStream.write(buffer, 0, actuallyRead);
					} catch (IOException e) {
						this.sendErrorMessage(30,
								"There was an error during the file transfer of '"
										+ fileObject.getName() + "'.");
						try {
							transferStream.close();
							fileStream.close();
						} catch (IOException e1) {
							Log
									.e(OutgoingFileTransfer.class.getName(),
											"Unexpected exception thrown after exception cleanup.");
						}
						return;
					}
					Log.i("MMedia measurement", "MXAin\t"+String.format("%05d", blockNumber+1)+"\t"+System.currentTimeMillis()+"\t"+actuallyRead);
					this.sendSuccessMessage(ConstMXA.MSG_STATUS_SUCCESS,
							blockNumber + 1, (actuallyRead != -1 ? actuallyRead
									: 0));
					blockNumber++;
				} while (actuallyRead != -1);
				// close stream
				try {
					fileStream.close();
					transferStream.close();
				} catch (IOException e) {
					Log.e(OutgoingFileTransfer.class.getName(),
							"Couldn't close streams properly.");
				}
				// file transferred, inform about successful delivery.
				this.sendSuccessMessage(ConstMXA.MSG_STATUS_DELIVERED,
						blockNumber, 0);
			}
			synchronized (mHangingFileTransfers) {
				mHangingFileTransfers.remove(hft);
			}
		}

		private void sendErrorMessage(int errorCode, String errorMessage) {
			Log.e(TAG, errorMessage);
			Message m = Message.obtain();
			m.what = ConstMXA.MSG_SEND_FILE;
			m.arg1 = ConstMXA.MSG_STATUS_ERROR;
			m.arg2 = this.requestCode;
			Bundle b = new Bundle();
			b.putInt("ERRORCODE", errorCode);
			b.putString("ERRORMESSAGE", errorMessage);
			b.putParcelable("MSN_ACK", this.messenger);
			b.putParcelable("MSN_RESULT", this.messenger);
			m.setData(b);
			mXMPPService.getXMPPResultsHandler().handleMessage(m);
		}

		protected void sendSuccessMessage(int arg1, int blocksTransferred,
				long bytesTransferred) {
			Message m = Message.obtain();
			m.what = ConstMXA.MSG_SEND_FILE;
			m.arg1 = arg1;
			m.arg2 = this.requestCode;
			Bundle b = new Bundle();
			b.putInt("BLOCKSTRANSFERRED", blocksTransferred);
			b.putLong("BYTESTRANSFERRED", bytesTransferred);
			b.putParcelable("MSN_ACK", messenger);
			b.putParcelable("MSN_RESULT", messenger);
			m.setData(b);
			mXMPPService.getXMPPResultsHandler().handleMessage(m);
		}
	}

	private class FileDenyRunner implements Runnable {

		private Messenger messenger;
		private int requestCode;
		private String streamID;
		
		/**
		 * @param messenger
		 * @param requestCode
		 * @param streamID
		 */
		public FileDenyRunner(Messenger messenger, int requestCode,
				String streamID) {
			this.messenger = messenger;
			this.requestCode = requestCode;
			this.streamID = streamID;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// get FileTransferRequest for streamID
			HangingFileTransfer hft = null;
			synchronized (mHangingFileTransfers) {
				for (HangingFileTransfer h : mHangingFileTransfers) {
					if (h.streamID.equals(streamID)) {
						hft = h;
						break;
					}
				}
			}
			if (hft != null) {
				hft.request.reject();
				// notify ack
				Message m = Message.obtain();
				m.what = ConstMXA.MSG_DENY_FILE;
				m.arg1 = ConstMXA.MSG_STATUS_SUCCESS;
				m.arg2 = requestCode;
				Bundle b = new Bundle();
				b.putParcelable("MSN_ACK", messenger);
				b.putParcelable("MSN_RESULT", messenger);
				m.setData(b);
				mXMPPService.getXMPPResultsHandler().handleMessage(m);
			}
			synchronized (mHangingFileTransfers) {
				mHangingFileTransfers.remove(hft);
			}
		}

	}
}
