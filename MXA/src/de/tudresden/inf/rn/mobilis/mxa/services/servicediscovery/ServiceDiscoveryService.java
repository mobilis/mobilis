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

package de.tudresden.inf.rn.mobilis.mxa.services.servicediscovery;

import java.util.ArrayList;
import java.util.Iterator;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverItems;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.XMPPRemoteService;
import de.tudresden.inf.rn.mobilis.mxa.services.parcelable.DiscoverItem;

/**
 * The ServiceDiscovery service is based on XEP-0030.
 * @author Benjamin Söllner, Istvan Koren
 */
public class ServiceDiscoveryService extends Service {

	private static final String TAG = "ServiceDiscoveryService";

	private final XMPPRemoteService mXMPPService;
	private ServiceDiscoveryManager mServiceDiscoveryManager;

	public ServiceDiscoveryService(XMPPRemoteService service) {
		mXMPPService = service;

		mServiceDiscoveryManager = ServiceDiscoveryManager
				.getInstanceFor(mXMPPService.getXMPPConnection());
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

	private final IServiceDiscoveryService.Stub mBinder = new IServiceDiscoveryService.Stub() {

		@Override
		public void discoverItem(Messenger acknowledgement, Messenger result,
				int requestCode, String jid, String node)
				throws RemoteException {
			// send runner to the write executor
			DiscoverItemRunner dir = new DiscoverItemRunner(acknowledgement,
					result, requestCode, jid, node);
			mXMPPService.getWriteExecutor().execute(dir);
		}
	};

	// ==========================================================
	// Inner classes
	// ==========================================================

	private class DiscoverItemRunner implements Runnable {

		private Messenger acknowledgement;
		private Messenger result;
		private int requestCode;
		private String jid;
		private String node;

		public DiscoverItemRunner(Messenger acknowledgement, Messenger result,
				int requestCode, String jid, String node) {
			this.requestCode = requestCode;
			this.acknowledgement = acknowledgement;
			this.result = result;
			this.jid = jid;
			this.node = node;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			Message msg = Message.obtain();
			msg.what = ConstMXA.MSG_DISCOVER_ITEMS;
			msg.arg2 = requestCode;
			Bundle data = new Bundle();
			data.putString("JID", jid);
			data.putString("NODE", node);
			data.putParcelable("MSN_ACK", acknowledgement);
			data.putParcelable("MSN_RESULT", result);
			msg.setData(data);

			Iterator<DiscoverItems.Item> diIterator = null;
			try {
				diIterator = mServiceDiscoveryManager.discoverItems(jid, node)
						.getItems();
			} catch (XMPPException e) {
				msg.arg1 = ConstMXA.MSG_STATUS_ERROR;
				mXMPPService.getXMPPResultsHandler().sendMessage(msg);
				return;
			}
			
			msg.arg1 = ConstMXA.MSG_STATUS_DELIVERED;
			mXMPPService.getXMPPResultsHandler().sendMessage(msg);			
			
			ArrayList<DiscoverItem> diList = new ArrayList<DiscoverItem>();
			while (diIterator.hasNext()) {
				DiscoverItems.Item discoverItem = diIterator.next();
				diList.add(new DiscoverItem(discoverItem.getName(),
						discoverItem.getEntityID(), discoverItem.getNode()));
			}
			// TODO send ack and result
			Message msg2 = Message.obtain(msg);
			msg2.what = ConstMXA.MSG_DISCOVER_ITEMS;
			msg2.arg1 = ConstMXA.MSG_STATUS_SUCCESS;
			msg2.arg2 = requestCode;
			data = new Bundle();
			data.putString("JID", jid);
			data.putString("NODE", node);
			data.putParcelable("MSN_ACK", acknowledgement);
			data.putParcelable("MSN_RESULT", result);
			data.putParcelableArrayList("DISCOVER_ITEMS", diList);
			msg2.setData(data);
			mXMPPService.getXMPPResultsHandler().sendMessage(msg2);
		}
	}
}
