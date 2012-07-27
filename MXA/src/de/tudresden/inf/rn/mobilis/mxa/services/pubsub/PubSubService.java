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

package de.tudresden.inf.rn.mobilis.mxa.services.pubsub;

import java.util.List;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.provider.DelayInfoProvider;
import org.jivesoftware.smackx.provider.HeaderProvider;
import org.jivesoftware.smackx.provider.HeadersProvider;
import org.jivesoftware.smackx.pubsub.EventElement;
import org.jivesoftware.smackx.pubsub.EventElementType;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemsExtension;
import org.jivesoftware.smackx.pubsub.NodeExtension;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.provider.AffiliationProvider;
import org.jivesoftware.smackx.pubsub.provider.AffiliationsProvider;
import org.jivesoftware.smackx.pubsub.provider.ConfigEventProvider;
import org.jivesoftware.smackx.pubsub.provider.EventProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemsProvider;
import org.jivesoftware.smackx.pubsub.provider.PubSubProvider;
import org.jivesoftware.smackx.pubsub.provider.RetractEventProvider;
import org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionProvider;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionsProvider;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import de.tudresden.inf.rn.mobilis.mxa.XMPPRemoteService;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.ISubscribeCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.xmpp.GeolocItemProvider;
import de.tudresden.inf.rn.mobilis.mxa.services.xmpp.SubscriptionFilter;
import de.tudresden.inf.rn.mobilis.mxa.util.FilteredCallbackList;

/**
 * The PubSub service is based on XEP-0060.
 * @author Istvan Koren
 */
public class PubSubService extends Service {

	private static final String TAG = "PubSubService";

	private final XMPPRemoteService mXMPPService;
	private final PubSubManager mPubSubManager;
	private ReaderThread mXMPPReadWorker;

	/*
	 * Remote callback list for event listeners.
	 */
	final FilteredCallbackList<ISubscribeCallback> mSubscribeCallbacks = new FilteredCallbackList<ISubscribeCallback>();

	public PubSubService(XMPPRemoteService service) {
		mXMPPService = service;

		mPubSubManager = new PubSubManager(mXMPPService.getXMPPConnection());

		ProviderManager pm = ProviderManager.getInstance();
		configureProviderManager(pm);

		// install GeolocItem
		GeolocItemProvider.install(pm);

		mXMPPReadWorker = new ReaderThread();

		// register for Personal Eventing (XEP-0163)
		AndFilter af = new AndFilter(new PacketExtensionFilter("event",
				"http://jabber.org/protocol/pubsub#event"),
		// a pubsub server doesn't have an '@' included in its name
				new FromContainsFilter("@"));
		mXMPPService.getXMPPConnection().addPacketListener(mXMPPReadWorker, af);
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

	private final IPubSubService.Stub mBinder = new IPubSubService.Stub() {

		@Override
		public void subscribe(ISubscribeCallback callback, String target,
				String node) throws RemoteException {
			if (callback != null) {
				SubscriptionFilter sf = new SubscriptionFilter(target, node);
				mSubscribeCallbacks.register(callback, sf);
			}
		}

		@Override
		public void unsubscribe(ISubscribeCallback callback, String target,
				String node) throws RemoteException {
			if (callback != null) {
				SubscriptionFilter sf = new SubscriptionFilter(target, node);
				mSubscribeCallbacks.unregister(callback, sf);
			}
		}
	};

	// ==========================================================
	// Inner classes
	// ==========================================================

	private class ReaderThread extends Thread implements PacketListener {

		@Override
		public void processPacket(Packet packet) {
//			PacketExtension pe = packet.getExtension("event",
//			"http://jabber.org/protocol/pubsub#event");
//			String peXML = pe.toXML();
//			String packetXML = packet.toXML();
						
			EventElement ee = (EventElement) packet.getExtension("event",
					"http://jabber.org/protocol/pubsub#event");
			
//			String eeXML = ee.toXML();
			ItemsExtension ie = (ItemsExtension) ee.getExtensions().get(0);
//			String ieXML = ie.toXML();
//			StringBuilder buf = new StringBuilder();
//			List<PayloadItem<PacketExtension>> items = (List<PayloadItem<PacketExtension>>) ie.getItems();
//			for (PayloadItem<PacketExtension> i : items) {
//				buf.append(i.toXML());
//			}
			
//			EventElementType eventType = ee.getEventType();
//			NodeExtension ne = ee.getEvent();
			

			int i = mSubscribeCallbacks.beginBroadcast();
			while (i > 0) {
				i--;
				try {
					ISubscribeCallback isc = mSubscribeCallbacks
							.getBroadcastItem(i);
					if (mSubscribeCallbacks.getFilters(isc).contains(
							new SubscriptionFilter(packet.getFrom(), ie
									.getNode()))) {
						// found an appropriate filter, notify
						// callback interface
						//TODO: also add the items from the packet.
						StringBuffer sb= new StringBuffer();
						for (PacketExtension pe:ie.getItems()) sb.append(pe.toXML()+" ");
						isc.onPublishEvent(packet.getFrom(), ie.getNode(), sb.toString());
//						isc.onPublishEvent(packet.getFrom(), ie.getNode(), buf
//								.toString());
					}
				} catch (RemoteException e) {
					// The RemoteCallbackList will take care of
					// removing the dead object for us.
				}
			}
			mSubscribeCallbacks.finishBroadcast();
		}
	};

	/**
	 * WORKAROUND for Android only! The necessary configuration files for Smack
	 * library are not included in Android's apk-Package.
	 * 
	 * @param pm
	 *            A ProviderManager instance.
	 */
	private void configureProviderManager(ProviderManager pm) {

		pm.addExtensionProvider("headers", "http://jabber.org/protocol/shim",
				new HeadersProvider());

		pm.addExtensionProvider("delay", "urn:xmpp:delay",
				new DelayInfoProvider());

		pm.addExtensionProvider("header", "http://jabber.org/protocol/shim",
				new HeaderProvider());

		pm.addIQProvider("pubsub", "http://jabber.org/protocol/pubsub",
				new PubSubProvider());

		pm.addExtensionProvider("create", "http://jabber.org/protocol/pubsub",
				new SimpleNodeProvider());

		pm.addExtensionProvider("items", "http://jabber.org/protocol/pubsub",
				new ItemsProvider());

		pm.addExtensionProvider("item", "http://jabber.org/protocol/pubsub",
				new ItemProvider());

		pm.addExtensionProvider("subscriptions",
				"http://jabber.org/protocol/pubsub",
				new SubscriptionsProvider());

		pm
				.addExtensionProvider("subscription",
						"http://jabber.org/protocol/pubsub",
						new SubscriptionProvider());

		pm
				.addExtensionProvider("affiliations",
						"http://jabber.org/protocol/pubsub",
						new AffiliationsProvider());

		pm.addExtensionProvider("affiliation",
				"http://jabber.org/protocol/pubsub", new AffiliationProvider());

		// pm.addExtensionProvider("options",
		// "http://jabber.org/protocol/pubsub",
		// new FormNodeProvider());

		pm.addIQProvider("pubsub", "http://jabber.org/protocol/pubsub#owner",
				new PubSubProvider());

		// pm.addExtensionProvider("configure",
		// "http://jabber.org/protocol/pubsub#owner",
		// new FormNodeProvider());

		// pm.addExtensionProvider("default",
		// "http://jabber.org/protocol/pubsub#owner",
		// new FormNodeProvider());

		pm.addExtensionProvider("event",
				"http://jabber.org/protocol/pubsub#event", new EventProvider());

		pm.addExtensionProvider("configuration",
				"http://jabber.org/protocol/pubsub#event",
				new ConfigEventProvider());

		pm.addExtensionProvider("delete",
				"http://jabber.org/protocol/pubsub#event",
				new SimpleNodeProvider());

		// pm.addExtensionProvider("options",
		// "http://jabber.org/protocol/pubsub#event",
		// new FormNodeProvider());

		pm.addExtensionProvider("items",
				"http://jabber.org/protocol/pubsub#event", new ItemsProvider());

		pm.addExtensionProvider("item",
				"http://jabber.org/protocol/pubsub#event", new ItemProvider());

		pm.addExtensionProvider("retract",
				"http://jabber.org/protocol/pubsub#event",
				new RetractEventProvider());

		pm.addExtensionProvider("purge",
				"http://jabber.org/protocol/pubsub#event",
				new SimpleNodeProvider());
	}
}
