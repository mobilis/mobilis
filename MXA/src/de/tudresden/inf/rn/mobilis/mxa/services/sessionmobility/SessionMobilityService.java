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

package de.tudresden.inf.rn.mobilis.mxa.services.sessionmobility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.MessageItems;
import de.tudresden.inf.rn.mobilis.mxa.XMPPRemoteService;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IResourcesCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.ITransferCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.parcelable.DiscoverItem;
import de.tudresden.inf.rn.mobilis.mxa.services.xmpp.ForwardedStateExtensionProvider;
import de.tudresden.inf.rn.mobilis.mxa.services.xmpp.SessionInvitationProvider;
import de.tudresden.inf.rn.mobilis.mxa.services.xmpp.SessionTransferProvider;

public class SessionMobilityService extends Service {

	// TAG for logging
	private static final String TAG = "SessionMobilityService";

	// members
	private final XMPPRemoteService mXMPPService;
	private ReaderThread mXMPPReadWorker;
	private HashMap<String, String> mTransferListener;
	private ConcurrentHashMap<String, ITransferCallback> mWaitingTransfers = new ConcurrentHashMap<String, ITransferCallback>();

	public SessionMobilityService(XMPPRemoteService service) {
		mXMPPService = service;
		mXMPPReadWorker = new ReaderThread();

		// TODO: mockup code
		mTransferListener = new HashMap<String, String>();
		mTransferListener.put("ChatMigration",
				"de.tudresden.inf.rn.mobilis.chatmigration");

		// add providers
		ProviderManager pm = ProviderManager.getInstance();
		SessionInvitationProvider sip = new SessionInvitationProvider();
		pm.addIQProvider(SessionInvitationIQ.elementName,
				SessionInvitationIQ.namespace, sip);
		SessionTransferProvider stp = new SessionTransferProvider();
		pm.addIQProvider(SessionTransferIQ.elementName,
				SessionTransferIQ.namespace, stp);
		// message forwards
		pm.addExtensionProvider(ForwardedStateExtension.elementName,
				ForwardedStateExtension.namespace,
				new ForwardedStateExtensionProvider());

		// add listeners for invitation and transfer intents
		PacketTypeFilter filterInvitation = new PacketTypeFilter(
				SessionInvitationIQ.class);
		PacketTypeFilter filterTransfer = new PacketTypeFilter(
				SessionTransferIQ.class);
		// message forwards
		PacketExtensionFilter pef = new PacketExtensionFilter(
				ForwardedStateExtension.elementName,
				ForwardedStateExtension.namespace);

		mXMPPService.getXMPPConnection().addPacketListener(
				mXMPPReadWorker,
				new OrFilter(new OrFilter(filterInvitation, filterTransfer),
						pef));
	}

	@Override
	public IBinder onBind(Intent i) {
		return mBinder;
	}

	private final ISessionMobilityService.Stub mBinder = new ISessionMobilityService.Stub() {

		@Override
		public void queryResources(IResourcesCallback resourcesCallback)
				throws RemoteException {
			ResourcesQueryThread rqt = new ResourcesQueryThread(
					resourcesCallback);

			rqt.start();
		}

		@Override
		public void inviteToSession(String jid, String namespace,
				List<String> params) throws RemoteException {
			SessionInvitationIQ sii = new SessionInvitationIQ(namespace);
			sii.setType(IQ.Type.SET);
			sii.setTo(jid);
			sii.setParams(params);

			mXMPPService.getXMPPConnection().sendPacket(sii);
		}

		@Override
		public void requestTransfer(String jid, String appuri,
				List<String> mechanisms, ITransferCallback transferCallback)
				throws RemoteException {
			mWaitingTransfers.put(appuri, transferCallback);
			SessionTransferIQ sti = new SessionTransferIQ(appuri);
			sti.setType(IQ.Type.GET);
			sti.setTo(jid);
			sti.setMechanisms(mechanisms);

			mXMPPService.getXMPPConnection().sendPacket(sti);
		}

		@Override
		public void acceptTransfer(String jid, String appuri,
				List<String> mechanisms) throws RemoteException {
			SessionTransferIQ sti = new SessionTransferIQ(appuri);
			sti.setType(IQ.Type.SET);
			sti.setTo(jid);
			sti.setMechanisms(mechanisms);

			mXMPPService.getXMPPConnection().sendPacket(sti);
		}

		@Override
		public void registerInvitationIntent(String appuri, String action)
				throws RemoteException {
		}

		@Override
		public void registerTransferIntent(String appuri, String action)
				throws RemoteException {
			mTransferListener.put(appuri, action);

			// TODO: persistently store intents
		}

		@Override
		public void sendMessageHistory(String jid, long startFrom) throws RemoteException {
			ForwardedStateExtension fse = new ForwardedStateExtension();
			Cursor c = mXMPPService.getContentResolver().query(
					MessageItems.contentUri, null,
					MessageItems.DATE_SENT + ">=" + startFrom, null,
					MessageItems.DEFAULT_SORT_ORDER);
			while (c.moveToNext()) {
				org.jivesoftware.smack.packet.Message msg = new org.jivesoftware.smack.packet.Message();
				msg.setFrom(c.getString(c.getColumnIndex(MessageItems.SENDER)));
				msg.setTo(c.getString(c.getColumnIndex(MessageItems.RECIPIENT)));
				msg.setBody(c.getString(c.getColumnIndex(MessageItems.BODY)));
				msg.setSubject(c.getString(c.getColumnIndex(MessageItems.SUBJECT)));
				msg.setType(org.jivesoftware.smack.packet.Message.Type.chat);
				msg.setProperty("stamp", c.getString(c.getColumnIndex(MessageItems.DATE_SENT)));
				fse.addForwardedPacket(msg);
			}
			c.close();
			
			org.jivesoftware.smack.packet.Message forwardMessage = new org.jivesoftware.smack.packet.Message();
			forwardMessage.setType(org.jivesoftware.smack.packet.Message.Type.chat);
			forwardMessage.setTo(jid);
			forwardMessage.addExtension(fse);
			mXMPPService.getXMPPConnection().sendPacket(forwardMessage);
			Log.i(TAG, "sent " + fse.getForwardedPackets().size() + " messages");
		}

	};

	// ==========================================================
	// Inner classes
	// ==========================================================

	private class ResourcesQueryThread extends Thread {

		private static final String TAG = "SessionMobilityService_ResourcesQueryThread";

		private IResourcesCallback mCallback;
		private String mUsername;

		private Handler mServiceDiscoveryHandler;

		public ResourcesQueryThread(IResourcesCallback callback) {
			mCallback = callback;
			try {
				mUsername = mXMPPService.getXMPPService().getUsername();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			super.run();
			Looper.prepare();

			try {
				mServiceDiscoveryHandler = new Handler() {
					public void handleMessage(Message msg) {
						if (msg.what == ConstMXA.MSG_DISCOVER_ITEMS
								&& msg.arg1 == ConstMXA.MSG_STATUS_SUCCESS
								&& msg.getData() != null) {
							try {
								ArrayList<DiscoverItem> items = msg.getData()
										.getParcelableArrayList(
												"DISCOVER_ITEMS");
								String username = mXMPPService.getXMPPService()
										.getUsername();

								ArrayList<String> resourcesList = new ArrayList<String>();
								for (DiscoverItem di : items) {
									if ((di.node == null)
											&& !(di.jid.equals(username))) {
										resourcesList.add(StringUtils
												.parseResource(di.jid));
									}
								}

								// notify callback listener
								mCallback.onResourcesResult(resourcesList);

								// stop thread
								ResourcesQueryThread.this.interrupt();

							} catch (RemoteException e) {
								Log.e(TAG, e.getLocalizedMessage());
							}
						}
					};
				};

				Messenger m = new Messenger(mServiceDiscoveryHandler);

				mXMPPService
						.getXMPPService()
						.getServiceDiscoveryService()
						.discoverItem(m, m, 0,
								StringUtils.parseBareAddress(mUsername), null);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Looper.loop();
		}
	}

	private class ReaderThread extends Thread implements PacketListener {

		@Override
		public void processPacket(Packet packet) {
			if (packet instanceof SessionInvitationIQ) {
				Log.i(TAG, "SessionInvitationIQ from " + packet.getFrom());
				SessionInvitationIQ invitationIQ = (SessionInvitationIQ) packet;
			} else if (packet instanceof SessionTransferIQ) {
				SessionTransferIQ transferIQ = (SessionTransferIQ) packet;

				// check type of IQ
				// GET: send receipt, notify app that needs to send supported
				// mechanisms
				// SET: notify app about chosen mechanism
				// RESULT: either receipt of GET or SET
				// content
				if (transferIQ.getType() == IQ.Type.GET) {
					Log.i(TAG, "SessionTransferIQ GET received from "
							+ transferIQ.getFrom());
					// send receipt
					transferIQ.setType(IQ.Type.RESULT);
					transferIQ.setTo(transferIQ.getFrom());
					mXMPPService.getXMPPConnection().sendPacket(transferIQ);

					// send notification intents
					String appURI = transferIQ.getAppURI();
					if (mTransferListener.containsKey(appURI)) {
						Intent i = new Intent(mTransferListener.get(appURI));
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.putExtra("mobilis:iq:sessionmobility#from",
								transferIQ.getFrom());
						i.putExtra("mobilis:iq:sessionmobility#mechanisms",
								transferIQ.getMechanisms().toArray());

						mXMPPService.sendOrderedBroadcast(i, null);
					}
				} else if (transferIQ.getType() == IQ.Type.SET) {
					// notify app about the supported mechanisms
					Log.i(TAG, "SessionTransferIQ SET received from "
							+ transferIQ.getFrom());

					// send receipt
					transferIQ.setType(IQ.Type.RESULT);
					transferIQ.setTo(transferIQ.getFrom());
					mXMPPService.getXMPPConnection().sendPacket(transferIQ);

					// notify application
					if (mWaitingTransfers.containsKey(transferIQ.getAppURI())) {
						ITransferCallback cb = mWaitingTransfers.get(transferIQ
								.getAppURI());
						try {
							cb.onTransferResult(transferIQ.getFrom(),
									transferIQ.getMechanisms());
						} catch (RemoteException e) {
							e.printStackTrace();
						}

						mWaitingTransfers.remove(transferIQ.getAppURI());
					}
				} else if (transferIQ.getType() == IQ.Type.RESULT) {
					// receipt of GET or SET
					Log.i(TAG, "SessionInvitationIQ RESULT received from "
							+ transferIQ.getFrom());
				}
			} else if (packet instanceof org.jivesoftware.smack.packet.Message) {
				Log.i(TAG, "Received forward Message");
				ForwardedStateExtension fse = (ForwardedStateExtension) packet
						.getExtension(ForwardedStateExtension.namespace);
				if (fse != null) {
					// add all packets to database, if not yet added
					for (Packet forwardedPacket : fse.getForwardedPackets()) {
						if (forwardedPacket instanceof org.jivesoftware.smack.packet.Message) {
							org.jivesoftware.smack.packet.Message msg = (org.jivesoftware.smack.packet.Message) forwardedPacket;

							Log.i(TAG,
									"Received forwarded message with stamp: "
											+ msg.getProperty("stamp"));
							// check if this message is already in database
							StringBuilder sb = new StringBuilder();
							sb.append(MessageItems.SENDER);
							sb.append("='");
							sb.append(msg.getFrom());
							sb.append("' AND ");
							sb.append(MessageItems.RECIPIENT);
							sb.append("='");
							sb.append(msg.getTo());
							sb.append("' AND ");
							sb.append(MessageItems.BODY);
							sb.append("='");
							sb.append(msg.getBody());
							sb.append("' AND ");
							sb.append(MessageItems.DATE_SENT);
							sb.append("='");
							sb.append(msg.getProperty("stamp"));
							sb.append("'");
							Cursor c = mXMPPService.getContentResolver().query(
									MessageItems.contentUri, null,
									sb.toString(), null,
									MessageItems.DEFAULT_SORT_ORDER);
							if (!c.moveToFirst()) {
								// insert
								ContentValues values = new ContentValues();
								Long date_sent = Long.parseLong((String) msg
										.getProperty("stamp"));
								values.put(MessageItems.SENDER, msg.getFrom());
								values.put(MessageItems.RECIPIENT, msg.getTo());
								if (msg.getSubject() != null)
									values.put(MessageItems.SUBJECT,
											msg.getSubject());
								if (msg.getBody() != null)
									values.put(MessageItems.BODY, msg.getBody());
								values.put(MessageItems.DATE_SENT, date_sent);
								values.put(MessageItems.READ, 0);
								values.put(MessageItems.TYPE, "chat");
								values.put(MessageItems.STATUS, "forwarded");

								// just save non empty messages
								if (msg.getBody() != null) {
									Uri uri = mXMPPService.getContentResolver()
											.insert(MessageItems.contentUri,
													values);
									Log.i(TAG,
											"saved state forwarded message to URI "
													+ uri.getPath());
								}
							}
							c.close();
						}
					}
				}
			}
		}

	};
}
