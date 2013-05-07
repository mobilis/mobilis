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

package de.tudresden.inf.rn.mobilis.mxa;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.NotFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ChatState;
import org.jivesoftware.smackx.ChatStateManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bytestreams.ibb.provider.CloseIQProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.DataPacketProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.OpenIQProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.search.UserSearch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.MessageItems;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.RosterItems;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IChatStateCallback;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IConnectionCallback;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IXMPPIQCallback;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IXMPPMessageCallback;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPIQ;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPMessage;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPPresence;
import de.tudresden.inf.rn.mobilis.mxa.provider.DynamicContentProvider;
import de.tudresden.inf.rn.mobilis.mxa.provider.ProviderRegistry;
import de.tudresden.inf.rn.mobilis.mxa.services.filetransfer.FileTransferService;
import de.tudresden.inf.rn.mobilis.mxa.services.filetransfer.IFileTransferService;
import de.tudresden.inf.rn.mobilis.mxa.services.messagecarbons.ForwardedExtension;
import de.tudresden.inf.rn.mobilis.mxa.services.messagecarbons.IMessageCarbonsService;
import de.tudresden.inf.rn.mobilis.mxa.services.messagecarbons.MessageCarbonsService;
import de.tudresden.inf.rn.mobilis.mxa.services.messagecarbons.SentExtension;
import de.tudresden.inf.rn.mobilis.mxa.services.multiuserchat.IMultiUserChatService;
import de.tudresden.inf.rn.mobilis.mxa.services.multiuserchat.MultiUserChatService;
import de.tudresden.inf.rn.mobilis.mxa.services.pubsub.IPubSubService;
import de.tudresden.inf.rn.mobilis.mxa.services.pubsub.PubSubService;
import de.tudresden.inf.rn.mobilis.mxa.services.serverlessmessaging.IServerlessMessagingService;
import de.tudresden.inf.rn.mobilis.mxa.services.serverlessmessaging.ServerlessMessagingService;
import de.tudresden.inf.rn.mobilis.mxa.services.servicediscovery.IServiceDiscoveryService;
import de.tudresden.inf.rn.mobilis.mxa.services.servicediscovery.ServiceDiscoveryService;
import de.tudresden.inf.rn.mobilis.mxa.services.sessionmobility.ForwardedStateExtension;
import de.tudresden.inf.rn.mobilis.mxa.services.sessionmobility.ISessionMobilityService;
import de.tudresden.inf.rn.mobilis.mxa.services.sessionmobility.SessionMobilityService;
import de.tudresden.inf.rn.mobilis.mxa.util.FilteredCallbackList;
import de.tudresden.inf.rn.mobilis.mxa.util.JIDFilteredCallbackList;
import de.tudresden.inf.rn.mobilis.mxa.xmpp.IQImpl;
import de.tudresden.inf.rn.mobilis.mxa.xmpp.IQImplFilter;
import de.tudresden.inf.rn.mobilis.mxa.xmpp.IQImplProvider;
import de.tudresden.inf.rn.mobilis.mxa.xmpp.MXAIdentExtension;
import de.tudresden.inf.rn.mobilis.mxa.xmpp.MXAIdentExtensionProvider;
import de.tudresden.inf.rn.mobilis.mxa.xmpp.MessageExtension;
import de.tudresden.inf.rn.mobilis.mxa.xmpp.MessageExtensionProvider;

/**
 * @author Istvan Koren, Christian Magenheimer
 */
public class XMPPRemoteService extends Service {

	private static final String TAG = "XMPPRemoteService";

	private static final int XMPPSERVICE_STATUS = 1;

	private static final int VERSION_CODE_ICE_CREAM_SANDWICH = 14;

	private static final boolean RUN_IN_FOREGROUND = false;

	private static final int NOTIFICATION_ID = 1101;

	private SharedPreferences mPreferences;
	private XMPPConnection mConn;
	private WriterThread xmppWriteWorker;
	private ReaderThread xmppReadWorker;
	ExecutorService mWriteExecutor;

	// Additional MXA Services
	FileTransferService mFileTransferService;
	MultiUserChatService mMultiUserChatService;
	PubSubService mPubSubService;
	ServiceDiscoveryService mServiceDiscoveryService;
	SessionMobilityService mSessionMobilityService;
	ServerlessMessagingService mServerlessMessagingService;
	MessageCarbonsService mMessageCarbonsService;

	private XMPPRemoteService instance;

	// service for listening to connectivity changes
	private NetworkMonitor mNetworkMonitor;
	private LostIQQueue mIQQueue;
	private Timer mIQQueueTimer;
	private boolean mReconnect = false;

	// other members
	private List<String> mOwnResources;

	final private JIDFilteredCallbackList<IChatStateCallback> mChatStateCallbacks = new JIDFilteredCallbackList<IChatStateCallback>();

	/*
	 * Remote callback list for message listeners.
	 */
	final FilteredCallbackList<IXMPPMessageCallback> mMsgCallbacks = new FilteredCallbackList<IXMPPMessageCallback>();
	/*
	 * Remote callback list for connection listeners.
	 */
	final RemoteCallbackList<IConnectionCallback> mConnectionCallbacks = new RemoteCallbackList<IConnectionCallback>();

	// Remote callback list for iq listeners.
	final FilteredCallbackList<IXMPPIQCallback> mIQCallbacks = new FilteredCallbackList<IXMPPIQCallback>();

	// Listener for data from messages
	// final FilteredCallbackList<IXMPPMessageCallback> mMsgCallbacks = new
	// FilteredCallbackList<IXMPPMessageCallback>();
	private static long PACKET_TIMEOUT = 5000;

	static {
//		System.setProperty("smack.debuggerClass",
//		"de.tudresden.inf.rn.mobilis.mxa.util.Debugger");
		
		Connection.DEBUG_ENABLED = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// Workaround for ServiceDiscoveryManager: this class has a static
		// initializer which is not called in the right moment. That is why
		// we repeat its code here.
		XMPPConnection
				.addConnectionCreationListener(new ConnectionCreationListener() {
					@Override
					public void connectionCreated(Connection connection) {
						new ServiceDiscoveryManager(connection);
					}
				});
		// initialize and start worker threads
		xmppWriteWorker = new WriterThread();
		xmppWriteWorker.start();
		xmppReadWorker = new ReaderThread();
		xmppReadWorker.start();

		// read in preferences
		mPreferences = MXAController.get().getSharedPreferences();
		// initialize IQ executor
		mWriteExecutor = Executors.newCachedThreadPool();

		// build the monitor and register the events
		mNetworkMonitor = new NetworkMonitor(this);
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mNetworkMonitor, filter);

		mIQQueue = new LostIQQueue();
		mIQQueueTimer = new Timer();

		Log.v(TAG, "count: " + mIQQueue.getCount());

		// initialize other members
		mOwnResources = new ArrayList<String>();

		// testIQQueue();
		// testIQ();
		// testHandler();
		// mIQDatabase.printAllEntries();
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		prepareContentProviderUris();
		
		stopNotification();
		instance = this;
	}
	
	private void prepareContentProviderUris() {
		try {
			ProviderInfo[] providers = getPackageManager().getPackageInfo(XMPPRemoteService.this.getPackageName(), PackageManager.GET_PROVIDERS).providers;
			for (ProviderInfo provider : providers) {
				if (provider.name.equals("de.tudresden.inf.rn.mobilis.mxa.provider.RosterProvider")) {
					ConstMXA.rosterAuthority = provider.authority;
				} else if (provider.name.equals("de.tudresden.inf.rn.mobilis.mxa.provider.MessageProvider")) {
					ConstMXA.messageAuthority = provider.authority;
				}
			}
			
			for (DynamicContentProvider provider : ProviderRegistry.get()) {
				provider.loadUriMatcherAuthority();
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method is just a quick fix to get hold of the current XMPPConnection
	 * object. It may be removed as soon as the WMP-CollaborationService has
	 * been properly integrated into Mobilis and into this service.
	 */
	public XMPPRemoteService getInstance() {
		return instance;
	}

	public XMPPConnection getXMPPConnection() {
		return mConn;
	}

	public void setXMPPConnection(XMPPConnection mConn) {
		this.mConn = mConn;
	}

	public SharedPreferences getPreferences() {
		return mPreferences;
	}

	public ExecutorService getWriteExecutor() {
		return mWriteExecutor;
	}

	/**
	 * The WriterThread is responsible for sending XMPP stanzas to the server.
	 * 
	 * @author koren
	 * 
	 */
	private class WriterThread extends Thread {
		public Handler mHandler;

		public void run() {
			setName("MXA Writer Thread");
			Looper.prepare();

			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					// initialize response Message, as Messages cannot be reused
					// get a Message from the Message pool and copy values of
					// msg
					Message msg2 = Message.obtain(msg);
					switch (msg.what) {
					case ConstMXA.MSG_CONNECT:
						mReconnect = true;
						// initialize XMPP Connection
						// check if already connected
						if (mConn != null && mConn.isConnected()
								&& mConn.isAuthenticated()) {
							msg2.arg1 = ConstMXA.MSG_STATUS_SUCCESS;
							xmppResults.sendMessage(msg2);
							break;
						}
						if (mConn != null) mConn.disconnect();

						// read server preferences
						String host = mPreferences.getString("pref_host", null);
						int port = Integer.parseInt(mPreferences.getString(
								"pref_port", "5222"));
						String serviceName = mPreferences.getString(
								"pref_service", "");

						boolean useEncryption = mPreferences.getBoolean(
								"pref_xmpp_encryption", true);
						boolean useCompression = mPreferences.getBoolean(
								"pref_xmpp_compression", false);

						ConnectionConfiguration config = new ConnectionConfiguration(
								host, port, serviceName);
						configureTrustStore(config);
						if (!useEncryption)
							config.setSecurityMode(SecurityMode.disabled);
						else
							config.setSecurityMode(SecurityMode.enabled);
						// TODO: insert compression possiblity
						// config.setCompressionEnabled(useCompression);

						mConn = new XMPPConnection(config);

						// read user credentials
						String username = mPreferences.getString(
								"pref_xmpp_user", "");
						String password = mPreferences.getString(
								"pref_xmpp_password", "");
						String resource = mPreferences.getString(
								"pref_resource", "MXA");

						// if
						// (mPreferences.getBoolean("pref_xmpp_debug_enabled",
						// false)) {
						// Debugger.setEnabled(true);
						// Debugger.setDirectory(mPreferences.getString(
						// "pref_xmpp_debug_directory", null));
						// }

						// connect and login to XMPP server
						try {
							mConn.connect();
							mIQQueue.setMaxRetryTime(Integer
									.valueOf(mPreferences.getString(
											"pref_xmpp_lost_timeout", "60")));
							mIQQueue.setMaxRetryCount(Integer
									.valueOf(mPreferences.getString(
											"pref_xmpp_retry_count", "10")));
							mIQQueue.setRetryInterval(Integer
									.valueOf(mPreferences.getString(
											"pref_xmpp_retry_timeout", "10")));

							ServiceDiscoveryManager sdm = ServiceDiscoveryManager
									.getInstanceFor(mConn);
							sdm.addFeature("http://jabber.org/protocol/chatstates");
							PACKET_TIMEOUT = (Integer
									.valueOf(mPreferences.getString(
											"pref_xmpp_interval_packet", "5"))) * 1000;
							ChatStateManager.getInstance(mConn);
							ProviderManager pm = ProviderManager.getInstance();
							configureProviderManager(pm);

							// install MXAIdentExtension
							MXAIdentExtensionProvider.install(pm);

							// register PacketListener for all message and
							// presence stanzas

							// mConn.addPacketListener(
							// xmppReadWorker,
							// new OrFilter(
							// new OrFilter(
							// new PacketTypeFilter(
							// org.jivesoftware.smack.packet.Message.class),
							// new PacketTypeFilter(
							// Presence.class)),
							// new PacketTypeFilter(IQImpl.class)));

							PacketFilter messageFilter = new AndFilter(
									new PacketTypeFilter(
											org.jivesoftware.smack.packet.Message.class),
									new NotFilter(
											new OrFilter(
													new PacketExtensionFilter(
															ForwardedStateExtension.namespace),
													new PacketExtensionFilter(
															ForwardedExtension.namespace))));
							mConn.addPacketListener(xmppReadWorker,
									new OrFilter(
											new OrFilter(messageFilter,
													new PacketTypeFilter(
															Presence.class)),
											new PacketTypeFilter(IQImpl.class)));

							// register connection listener
							mConn.addConnectionListener(xmppReadWorker);

							mConn.login(username, password, resource);

							// TODO we have a BUG here that causes all entries
							// to be offline, this
							// happens if roster updates are sent before the
							// table is initially cleared
							// Solution: Shift the deletion upwards, remove the
							// code below, because
							// it should be done by the roster listener

							// delete all entries in the RosterProvider
							getContentResolver().delete(
									ConstMXA.RosterItems.contentUri, "1",
									new String[] {});
							// get Roster from server
							final Roster r = XMPPRemoteService.this.mConn
									.getRoster();
							r.addRosterListener(xmppReadWorker);
							Collection<RosterEntry> rosterEntries = r
									.getEntries();
							List<String> entries = new ArrayList<String>(
									rosterEntries.size());
							for (RosterEntry re : rosterEntries)
								entries.add(re.getUser());
							xmppReadWorker.entriesAdded(entries);
							
//							// Notification
							showConnectionNotification();

							// initialize services
							mFileTransferService = new FileTransferService(
									XMPPRemoteService.this);
							mMultiUserChatService = new MultiUserChatService(
									XMPPRemoteService.this);
							mPubSubService = new PubSubService(
									XMPPRemoteService.this);
							mServiceDiscoveryService = new ServiceDiscoveryService(
									XMPPRemoteService.this);
							mSessionMobilityService = new SessionMobilityService(
									XMPPRemoteService.this);
							mMessageCarbonsService = new MessageCarbonsService(
									XMPPRemoteService.this);

							msg2.arg1 = ConstMXA.MSG_STATUS_SUCCESS;
							mIQQueueTimer = new Timer();
							mIQQueueTimer.schedule(
									new IQQueueCheckBackgroundRunner(), 0);

						} catch (XMPPException e) {
							msg2.arg1 = ConstMXA.MSG_STATUS_ERROR;
							Bundle b = msg2.getData();
							String errorMessage = e.getMessage();
							if (e.getXMPPError() != null ) errorMessage+=e.getXMPPError().toString();
							if (e.getCause() != null
									&& e.getCause().getMessage() != null)
								errorMessage += e.getCause().getMessage();
							b.putString(ConstMXA.EXTRA_ERROR_MESSAGE,
									errorMessage);

							msg2.setData(b);

						} catch (IllegalStateException e) {
							msg2.arg1 = ConstMXA.MSG_STATUS_ERROR;
							Bundle b = msg2.getData();
							String errorMessage = e.getMessage();
							if (e.getCause() != null && e.getCause().getMessage() != null) {
								errorMessage += e.getCause().getMessage();
							}
							b.putString(ConstMXA.EXTRA_ERROR_MESSAGE, errorMessage);
							
							msg2.setData(b);
						}

						// // get roster, some entries may already have been
						// // inserted due to concurrent receiving of Presence
						// // stanzas
						// Log.i(TAG, "reading roster");
						// Roster r = mConn.getRoster();
						// ContentValues valuesRoster;
						// for (RosterEntry re : r.getEntries()) {
						// valuesRoster = new ContentValues();
						// valuesRoster.put(RosterItems.XMPP_ID, re.getUser());
						// valuesRoster.put(RosterItems.NAME, StringUtils
						// .parseName(re.getUser()));
						// valuesRoster.put(RosterItems.PRESENCE_MODE,
						// "offline");
						//
						// getContentResolver().insert(
						// RosterItems.CONTENT_URI, valuesRoster);
						// }

						xmppResults.sendMessage(msg2);
						break;
					case ConstMXA.MSG_RECONNECT:
						if (!mReconnect)
							break;
						Log.v(TAG, "Reconnection wish");
						// this case differs from the above by assuming we had
						// an already working connection
						try {

							if (mConn != null) {
								Log.v(TAG, "Trying to reconnect");

								// read server preferences
//								String host2 = mPreferences.getString(
//										"pref_host", null);
//								int port2 = Integer.parseInt(mPreferences
//										.getString("pref_port", "5222"));
//								String serviceName2 = mPreferences.getString(
//										"pref_service", "");
//
//								boolean useEncryption2 = mPreferences
//										.getBoolean("pref_xmpp_encryption",
//												true);
//								boolean useCompressio2n = mPreferences
//										.getBoolean("pref_xmpp_compression",
//												false);
//
//								ConnectionConfiguration config2 = new ConnectionConfiguration(
//										host2, port2, serviceName2);
//								if (!useEncryption2)
//									config2.setSecurityMode(SecurityMode.disabled);
//								else
//									config2.setSecurityMode(SecurityMode.enabled);
//								// TODO: insert compression possiblity
//								// config.setCompressionEnabled(useCompression);
//
//								mConn = new XMPPConnection(config2);
//
//								mConn.connect();

								String username2 = mPreferences.getString(
										"pref_xmpp_user", "");
								String password2 = mPreferences.getString(
										"pref_xmpp_password", "");
								String resource2 = mPreferences.getString(
										"pref_resource", "MXA");
								mConn.login(username2, password2, resource2);
							} else
								break;

						} catch (Exception e) {
							if (!(e instanceof IllegalStateException)) {
								Message reconnect = new Message();
								reconnect.what = ConstMXA.MSG_CONNECT;
								xmppWriteWorker.mHandler.sendMessage(reconnect);
								Log.e(TAG,
										"hard reconnect, reason: "
												+ e.getMessage());
							}
						}

						if (mConn != null && mConn.isAuthenticated()) {
							Log.v(TAG,
									"Connection established to "
											+ mConn.getServiceName());
							
							// Notification
							showConnectionNotification();
							
							Message msgResend = new Message();
							msgResend.what = ConstMXA.MSG_IQ_RESEND;
							xmppWriteWorker.mHandler.sendMessage(msgResend);
						} else {
							Log.v(TAG,
									"Connection still broken "
											+ mConn.getServiceName());
							if (mNetworkMonitor.isConnected()) {
								Message reconnect = new Message();
								reconnect.what = ConstMXA.MSG_CONNECT;
								xmppWriteWorker.mHandler.sendMessage(reconnect);
								Log.e(TAG, "hard reconnect because of failure");
							}
						}
						break;
					case ConstMXA.MSG_DISCONNECT:
						if (mConn == null || !mConn.isConnected()) {
							msg2.arg1 = ConstMXA.MSG_STATUS_SUCCESS;
							xmppResults.sendMessage(msg2);
							break;
						}
						mReconnect = false;
						mIQQueueTimer.cancel();
						// disconnect() deletes all listeners, use shutdown() to
						// retain
						// listeners
						// TODO only disconnect from XMPP server if all
						// service consumers disconnected before

						stopNotification();
						
						mConn.disconnect();
						msg2.arg1 = ConstMXA.MSG_STATUS_SUCCESS;
						xmppResults.sendMessage(msg2);
						break;
					case ConstMXA.MSG_SEND_IQ:
						Bundle data = msg.getData();
						XMPPIQ payloadIQ = data.getParcelable("PAYLOAD");
						IQImpl iq = new IQImpl(payloadIQ.payload);
						iq.setTo(payloadIQ.to);
						switch (payloadIQ.type) {
						case XMPPIQ.TYPE_GET:
							iq.setType(Type.GET);
							break;
						case XMPPIQ.TYPE_SET:
							iq.setType(Type.SET);
							break;
						case XMPPIQ.TYPE_RESULT:
							iq.setType(Type.RESULT);
							break;
						case XMPPIQ.TYPE_ERROR:
							iq.setType(Type.ERROR);
							break;
						default:
							iq.setType(Type.GET);
						}

						// Set the packet-ID of the iq which should be sent:
						iq.setPacketID(payloadIQ.packetID);

						// set token
						if ((payloadIQ.namespace != null)
								|| (payloadIQ.token != null)) {
							MXAIdentExtension mie = new MXAIdentExtension(
									payloadIQ.namespace, payloadIQ.token);
							iq.addExtension(mie);
						}

						mConn.sendPacket(iq);

						break;
					case ConstMXA.MSG_IQ_RESEND:
						if (mIQQueue.getCount() == 0
								|| !mConn.isAuthenticated())
							break;

						// Log.v(TAG,"Queue has "+mIQQueue.getCount()+" entries");
						mIQQueueTimer.cancel();
						mIQQueueTimer = new Timer();
						mIQQueueTimer.schedule(
								new IQQueueCheckBackgroundRunner(),
								mIQQueue.getRetryInterval() * 1000,
								mIQQueue.getRetryInterval() * 1000);
						resendIQ();
						// Log.v(TAG,"Queue has "+mIQQueue.getCount()+" entries after sending");

						break;
					case ConstMXA.MSG_SEND_PRESENCE:
						Bundle dataPresence = msg.getData();
						XMPPPresence payloadPresence = dataPresence
								.getParcelable("PAYLOAD");
						Presence presence = new Presence(
								Presence.Type.available);
						presence.setStatus(payloadPresence.status);
						presence.setPriority(payloadPresence.priority);
						switch (payloadPresence.mode) {
						case XMPPPresence.MODE_AVAILABLE:
							presence.setMode(Mode.available);
							break;
						case XMPPPresence.MODE_AWAY:
							presence.setMode(Mode.away);
							break;
						case XMPPPresence.MODE_CHAT:
							presence.setMode(Mode.chat);
							break;
						case XMPPPresence.MODE_DND:
							presence.setMode(Mode.dnd);
							break;
						case XMPPPresence.MODE_XA:
							presence.setMode(Mode.xa);
							break;
						default:
							presence.setMode(Mode.available);
						}

						// send Presence over XMPP
						try {
							mConn.sendPacket(presence);
							msg2.arg1 = ConstMXA.MSG_STATUS_DELIVERED;
						} catch (IllegalStateException e) {
							msg2.arg1 = ConstMXA.MSG_STATUS_ERROR;
						}

						// send result ack

						xmppResults.sendMessage(msg2);

						// broadcast new presence
						Intent i = new Intent(ConstMXA.BROADCAST_PRESENCE);
						i.putExtra("STATUS_TEXT", payloadPresence.status);
						sendBroadcast(i);

						break;
					}

				}



			};

			Looper.loop();
		}
	}

	/**
	 * Configure trust store for pre and post ICS API.
	 * 
	 * @param connectionConfiguration
	 */
	private void configureTrustStore(ConnectionConfiguration connectionConfiguration) {
		if (Build.VERSION.SDK_INT >= VERSION_CODE_ICE_CREAM_SANDWICH) {
			
		    connectionConfiguration.setTruststoreType("AndroidCAStore");
		    connectionConfiguration.setTruststorePassword(null);
		    connectionConfiguration.setTruststorePath(null);
		} else {
		    connectionConfiguration.setTruststoreType("BKS");
		    String path = System.getProperty("javax.net.ssl.trustStore");
		    if (path == null)
		        path = System.getProperty("java.home") + File.separator + "etc"
		            + File.separator + "security" + File.separator
		            + "cacerts.bks";
		    connectionConfiguration.setTruststorePath(path);
		}					
	}

	/**
	 * An IQ runner thread sends a GET or SET IQ Message to the XMPP server,
	 * constructs a PacketListener for the result with the specific packet ID
	 * and notifies a handler. This happens for XMPP standard compliance
	 * reasons, as GET or SET IQ Messages require a RESULT or ERROR IQ stanza
	 * from the XMPP partner. Developers not wanting the result/error to be
	 * handled by a PacketCollector should set the result messenger to null.
	 * 
	 * @author Istvan Koren
	 * 
	 */
	
	/**
	 * @author Tobias Rho
	 */
	private void showConnectionNotification() {
		if (RUN_IN_FOREGROUND) {
			Notification note = new NotificationCompat.Builder(this)
					.setContentTitle(getString(R.string.mxa_sb_txt_title))
					.setContentText(getString(R.string.mxa_sb_txt_text))
					.setSmallIcon(R.drawable.mxa_stat_notify_chat).build();

			note.flags |= Notification.FLAG_NO_CLEAR;

			startForeground(NOTIFICATION_ID, note);
		} else {
			NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

			Notification status = new Notification(R.drawable.mxa_stat_notify_chat,
					getString(R.string.mxa_sb_txt_text), System.currentTimeMillis());
			status.setLatestEventInfo(XMPPRemoteService.this,
					getString(R.string.mxa_sb_txt_title),
					getString(R.string.mxa_sb_txt_text), PendingIntent.getActivity(
							XMPPRemoteService.this, 0, new Intent(
									ConstMXA.INTENT_SERVICEMONITOR), 0));
			status.flags |= Notification.FLAG_ONGOING_EVENT;
			status.icon = R.drawable.mxa_stat_notify_chat;
			nm.notify(XMPPSERVICE_STATUS, status);
		}
	}
	
	/**
	 * 	Clear leftover notification in case this service previously got
	 *	killed while running.
	 *
	 * @author Tobias Rho
	 */
	private void stopNotification() {
		if(RUN_IN_FOREGROUND){
			stopForeground(true);
		} else {
			NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(XMPPSERVICE_STATUS);
		}
	}
	
	private class IQRunner implements Runnable {
		private Message msg;

		/**
		 * Constructs a new IQ runner.
		 * 
		 * @param result
		 *            the handler to be notified upon IQ result
		 * @param iq
		 *            the iq to be sent, must be of type GET or SET, as RESULT
		 *            and ERROR don't expect results.
		 */
		public IQRunner(Message msg) {
			this.msg = msg;
		}

		/**
		 * 
		 */
		@Override
		public void run() {
			Bundle data = msg.getData();
			XMPPIQ iq = data.getParcelable("PAYLOAD");

			IQImpl iqPacket = new IQImpl(iq.payload);
			iqPacket.fromXMPPIQ(iq);

			// create PacketCollector
			PacketCollector coll;
			iqPacket.setFrom(mConn.getUser());

			try {
				coll = mConn.createPacketCollector(new PacketIDFilter(iqPacket
						.getPacketID()));
				mConn.sendPacket(iqPacket);
			} catch (Exception error) {
				// TODO: handle the MSG_IQ_RESEND case
				// sending went wrong, possibly because of disconnection
				LostIQQueueEntry e = new LostIQQueueEntry();
				e.mMessage = Message.obtain(msg);
				e.mTime = System.currentTimeMillis();
				e.mXMPPIQ = iq;
				e.mSending = false;
				if (msg.what == ConstMXA.MSG_IQ_RESEND) {
					e.mID = data.getLong(ConstMXA.IQ_RESEND_ID);
				}
				mIQQueue.insert(e);
				// notify application about error
				Message resultMsg = Message.obtain(msg);
				resultMsg.arg1 = ConstMXA.MSG_STATUS_ERROR;
				xmppResults.sendMessage(resultMsg);
				Log.v(TAG, "iq failed:" + e.mID + " " + e.mXMPPIQ.toString());
				// Log.v(TAG,"Error: "+error.getMessage());
				return;
			}
			// send ack message
			Message msgAck = Message.obtain(msg);
			msgAck.arg1 = ConstMXA.MSG_STATUS_DELIVERED;
			xmppResults.sendMessage(msgAck);
			msgAck.getData().putParcelable("MSN_RESULT",
					msg.getData().getParcelable("MSN_RESULT"));
			if (msgAck.getData().getParcelable("MSN_RESULT") != null) {
				Packet resultPacket = coll.nextResult(PACKET_TIMEOUT);
				coll.cancel();
				// construct result Message
				Message resultMsg = Message.obtain(msg);
				
				// Timeout occured
				
				if (resultPacket == null) {
					// timeout
					resultMsg.arg1 = ConstMXA.MSG_STATUS_ERROR;

					LostIQQueueEntry e = new LostIQQueueEntry();
					e.mMessage = Message.obtain(msg);
					e.mTime = System.currentTimeMillis();
					e.mXMPPIQ = iq;
					e.mSending = false;
					if (msg.what == ConstMXA.MSG_IQ_RESEND) {
						e.mID = data.getLong(ConstMXA.IQ_RESEND_ID);
					}
					mIQQueue.insert(e);
					Log.v(TAG,
							"timeout sending iq:" + e.mID + " "
									+ e.mXMPPIQ.toString());
					return;
				} else {
					// check if any timeout error occurred, so put message into queue
					XMPPError err = resultPacket.getError();
				/*	if (err != null && (err.getCode()==504 || err.getCode()==408)) {
						resultMsg.arg1 = ConstMXA.MSG_STATUS_ERROR;

						// normal packet, a lost packet means we need to insert
						LostIQQueueEntry e = new LostIQQueueEntry();
						e.mMessage = Message.obtain(msg);
						e.mTime = System.currentTimeMillis();
						e.mXMPPIQ = iq;
						e.mSending = false;
						if (msg.what == ConstMXA.MSG_IQ_RESEND) {
							e.mID = data.getLong(ConstMXA.IQ_RESEND_ID);
						}
						mIQQueue.insert(e);
						Log.v(TAG, "error sending iq:" + e.mID + " "
								+ e.mXMPPIQ.toString());
						return;
					} else {
						Log.i(TAG, "Success IQ: " + resultPacket.toXML());
						// attach result
				*/
						// this was send by the resending mechanism
						if (msg.what == ConstMXA.MSG_IQ_RESEND) {
							// we need to delete the packet
							long id = msg.getData().getLong(ConstMXA.IQ_RESEND_ID);
							mIQQueue.delete(id);
						}
						if (err != null) resultMsg.arg1= ConstMXA.MSG_STATUS_ERROR;
						else resultMsg.arg1 = ConstMXA.MSG_STATUS_SUCCESS;
						if (resultPacket instanceof IQImpl) {
							IQImpl resultIQ = (IQImpl) resultPacket;
							XMPPIQ xmppIQ =new XMPPIQ(resultIQ.getFrom(), resultIQ
									.getTo(), XMPPIQ.TYPE_RESULT,
									resultIQ.getChildNamespace(),
									resultIQ.getChildElementName(),
									resultIQ.getChildElementXML());
							if (err != null) xmppIQ.type=XMPPIQ.TYPE_ERROR;
							data.putParcelable("PAYLOAD",xmppIQ);
									
						}
					
				}
				// notify result handler
				// resultMsg.getData().putString("ID", iq.packetID);
				xmppResults.sendMessage(resultMsg);
			} else
				Log.v(TAG, "no handler for " + iq.packetID);
		}
	}

	private class MessageRunner implements Runnable {
		private Message msg;

		public MessageRunner(Message msg) {
			this.msg = msg;
		}

		@Override
		public void run() {
			Message msgAck = Message.obtain(msg);
			Bundle dataMsg = msg.getData();
			XMPPMessage payloadMsg = dataMsg.getParcelable("PAYLOAD");
			org.jivesoftware.smack.packet.Message xmppMsg = new org.jivesoftware.smack.packet.Message();
			xmppMsg.setTo(payloadMsg.to);
			if (payloadMsg.type == XMPPMessage.TYPE_CHAT) {
				// send chat message
				xmppMsg.setBody(payloadMsg.body);
				xmppMsg.setType(org.jivesoftware.smack.packet.Message.Type.chat);
				// save to database, update status later
				ContentValues values = new ContentValues();
				Long now = Long.valueOf(System.currentTimeMillis());
				values.put(MessageItems.SENDER, mConn.getUser());
				values.put(MessageItems.RECIPIENT, xmppMsg.getTo());
				if (xmppMsg.getSubject() != null)
					values.put(MessageItems.SUBJECT, xmppMsg.getSubject());
				if (xmppMsg.getBody() != null)
					values.put(MessageItems.BODY, xmppMsg.getBody());
				values.put(MessageItems.DATE_SENT, now);
				values.put(MessageItems.READ, 0);
				values.put(MessageItems.TYPE, "chat");
				values.put(MessageItems.STATUS, "sent");

				Log.i(TAG, "saving chat message");
				getContentResolver().insert(MessageItems.contentUri, values);

				// send via XMPP
				if (mConn.isAuthenticated()) {
					mConn.sendPacket(xmppMsg);

					// also send to all connected resources, if server doesn't
					// support carbons
					if (!mMessageCarbonsService.hasServerCarbonsSupport()) {
						ForwardedExtension fe = new ForwardedExtension();
						xmppMsg.setFrom(mConn.getUser());
						xmppMsg.setProperty("stamp", String.valueOf(values.getAsString(MessageItems.DATE_SENT)));
						fe.setForwardedPacket(xmppMsg);
						org.jivesoftware.smack.packet.Message forwardMessage = new org.jivesoftware.smack.packet.Message();
						forwardMessage
								.setType(org.jivesoftware.smack.packet.Message.Type.chat);
						forwardMessage.addExtension(new SentExtension());
						forwardMessage.addExtension(fe);
						for (String resource : mOwnResources) {
							forwardMessage.setTo(resource);
							mConn.sendPacket(forwardMessage);
						}
					}
				}
				msgAck.arg1 = ConstMXA.MSG_STATUS_DELIVERED;
			} else if (payloadMsg.type == XMPPMessage.TYPE_NORMAL) {
				// send data message with mxa-ident
				xmppMsg.setType(org.jivesoftware.smack.packet.Message.Type.normal);
				// add MXA-ident extension
				/*
				 * MXAIdentExtension mie = new MXAIdentExtension(
				 * payloadMsg.namespace, payloadMsg.token);
				 */
				String xml = dataMsg.getString("PAYLOAD_XML");
				MessageExtension me = new MessageExtension(payloadMsg.token,
						payloadMsg.namespace, xml);
				xmppMsg.addExtension(me);
				if (mConn.isAuthenticated())
					mConn.sendPacket(xmppMsg);
				else
					msgAck.arg1 = ConstMXA.MSG_STATUS_ERROR;
				msgAck.arg1 = ConstMXA.MSG_STATUS_DELIVERED;
			} else if (payloadMsg.type == XMPPMessage.TYPE_GROUPCHAT) {
				// we cannot handle groupchat at this time
				Log.e(TAG, "we cannot handle groupchat at this time");
				msgAck.arg1 = ConstMXA.MSG_STATUS_ERROR;
			} else {
				msgAck.arg1 = ConstMXA.MSG_STATUS_ERROR;
			}
			// send ack message
			xmppResults.sendMessage(msgAck);
		}

	}

	private Handler xmppResults = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// handle response of worker thread
			Message msg2 = Message.obtain(msg);

			// msg2.getData().putString(ConstMXA.EXTRA_ID,
			// msg.getData().getString(ConstMXA.EXTRA_ID));
			// TODO send to IQ result if result/error, do not send if result
			// messenger null
			try {
				Bundle data = msg.getData();

				if (msg.what == ConstMXA.MSG_CONNECT
						&& msg.arg1 == ConstMXA.MSG_STATUS_SUCCESS) {
					Messenger ack = data.getParcelable("MSN_ACK");
					if (ack != null)
						ack.send(msg2);
				} else if (msg.what == ConstMXA.MSG_CONNECT
						&& msg.arg1 == ConstMXA.MSG_STATUS_ERROR) {
					Messenger ack = data.getParcelable("MSN_ACK");
					if (ack != null)
						ack.send(msg2);
				} else if (msg.what == ConstMXA.MSG_DISCONNECT
						&& msg.arg1 == ConstMXA.MSG_STATUS_SUCCESS) {
					Messenger ack = data.getParcelable("MSN_ACK");
					if (ack != null)
						ack.send(msg2);
				} else if (msg.arg1 == ConstMXA.MSG_STATUS_DELIVERED) {
					Messenger ack = data.getParcelable("MSN_ACK");
					if (ack != null)
						ack.send(msg2);
				} else if (msg.arg1 == ConstMXA.MSG_STATUS_SUCCESS) {
					Messenger result = data.getParcelable("MSN_RESULT");
					if (result != null)
						result.send(msg2);
				}
			} catch (RemoteException e) {
				// Handler doesn't exist anymore, ignore it.
				e.printStackTrace();
			}
		}
	};

	public Handler getXMPPResultsHandler() {
		return xmppResults;
	}

	private class ReaderThread extends Thread implements PacketListener,
			ConnectionListener, RosterListener {

		public void run() {
			setName("MXA Reader Thread");
			Looper.prepare();
			Looper.loop();
		}

		@Override
		public void processPacket(Packet packet) {
			Log.i(TAG, "reading packet");
			// String packetXML = packet.toXML();
			if (packet instanceof org.jivesoftware.smack.packet.Message) {
				// ===================================
				// Message packets
				// ===================================
				org.jivesoftware.smack.packet.Message m = (org.jivesoftware.smack.packet.Message) packet;

				// if message is of type normal, notify listeners
				if (m.getType().equals(
						org.jivesoftware.smack.packet.Message.Type.normal)) {
					Log.i(TAG, "--> message type=normal");
					// notify callback listeners
					// XMPPMessage xMsg = new XMPPMessage(m.getFrom(),
					// m.getTo(),
					// m.getBody(), m.getType().ordinal());

					PacketExtension pubsubExtension = m.getExtension("event",
							"http://jabber.org/protocol/pubsub#event");
					if (pubsubExtension != null) {
						// TODO Do something

						// int i = mMsgCallbacks.beginBroadcast();
						// while (i > 0) {
						// i--;
						// try {
						// IXMPPMessageCallback imc = mMsgCallbacks
						// .getBroadcastItem(i);
						// if (mMsgCallbacks.getFilters(imc).contains(
						// new
						// MXAIdentFilter("http://jabber.org/protocol/pubsub#event",
						// "event"))) {
						// // found an appropriate filter, notify
						// // callback interface
						// imc.processMessage(xMsg);
						// notifiedMsg = true;
						// }
						// } catch (RemoteException e) {
						// // The RemoteCallbackList will take care of
						// // removing the dead object for us.
						// }
						// }
						// mMsgCallbacks.finishBroadcast();

					}

					Collection<PacketExtension> pec = m.getExtensions();

					for (int i = mMsgCallbacks.beginBroadcast() - 1; i >= 0; i--)
						try {
							IXMPPMessageCallback callback = mMsgCallbacks
									.getBroadcastItem(i);
							for (PacketExtension e : pec) {
								for (PacketFilter filter : mMsgCallbacks
										.getFilters(callback)) {
									if (filter.accept(new IQImpl(e
											.getElementName(),
											e.getNamespace(), null))) {
										XMPPIQ iq = new XMPPIQ(m.getFrom(),
												m.getTo(), XMPPIQ.TYPE_RESULT,
												e.getElementName(),
												e.getNamespace(),
												m.getExtension(
														e.getElementName(),
														e.getNamespace())
														.toXML());
										if (e instanceof MessageExtension) {
											MessageExtension me = (MessageExtension) e;
											Log.v(TAG, "message Extension: "
													+ me.getElementName() + " "
													+ me.getNamespace() + " "
													+ me.getPayload());
											iq.payload = me.getPayload();
										}

										callback.processIQ(iq);
										Log.v(TAG,
												"message data received, notifies the application for  "
														+ iq.toString());
									}
								}
							}
						} catch (RemoteException error) {
							// TODO: Details zur Exception ausgeben.
							Log.e(TAG, "RemoteException!");
							error.printStackTrace();
						}
					mMsgCallbacks.finishBroadcast();

					/*
					 * try{ PacketExtension pe=m.getExtension(namespace) final
					 * XMPPIQ iq= new XMPPIQ(); iq.element=m.getExtensions();
					 * 
					 * }catch (Exception e) { e.printStackTrace(); }
					 */
					/*
					 * MXAIdentExtension mie = (MXAIdentExtension)
					 * m.getExtension( MXAIdentExtension.ELEMENT_NAME,
					 * MXAIdentExtension.NAMESPACE); if (mie != null) { String
					 * namespaceMsg = mie.getConsumerNamespace(); String
					 * tokenMsg = mie.getToken(); boolean notifiedMsg = false;
					 * 
					 * int i = mMsgCallbacks.beginBroadcast(); while (i > 0) {
					 * i--; try { IXMPPMessageCallback imc = mMsgCallbacks
					 * .getBroadcastItem(i); if
					 * (mMsgCallbacks.getFilters(imc).contains( new
					 * MXAIdentFilter(namespaceMsg, tokenMsg))) { // found an
					 * appropriate filter, notify // callback interface
					 * imc.processMessage(xMsg); notifiedMsg = true; } } catch
					 * (RemoteException e) { // The RemoteCallbackList will take
					 * care of // removing the dead object for us. } }
					 * mMsgCallbacks.finishBroadcast();
					 * 
					 * if (notifiedMsg) { // reply with error as no callback
					 * listeners // available with appropriate filter
					 * sendXMPPErrorMessage( m.getFrom(),
					 * XMPPError.Condition.feature_not_implemented,
					 * "No service available for this kind of request."); } }
					 * else { // reply with error as we do not yet understand //
					 * messages of type pubsub etc. //TODO: is disabled, because
					 * if we play with 2 android clients wich // uses mxa, they
					 * producing an endless loop of ErrorMessages to each other
					 * // tested with openfire in local network /*
					 * sendXMPPErrorMessage(m.getFrom(),
					 * XMPPError.Condition.feature_not_implemented,
					 * "No service available for this kind of request.");
					 */// }
				} else if (m.getType().equals(
						org.jivesoftware.smack.packet.Message.Type.chat)) {
					// Log.i(TAG, "message type=chat");
					// save in database, the content provider takes care of
					// notifying interested parties
					ContentValues values = new ContentValues();
					Long now = Long.valueOf(System.currentTimeMillis());
					values.put(MessageItems.SENDER, m.getFrom());
					values.put(MessageItems.RECIPIENT, m.getTo());
					if (m.getSubject() != null)
						values.put(MessageItems.SUBJECT, m.getSubject());
					if (m.getBody() != null)
						values.put(MessageItems.BODY, m.getBody());
					values.put(MessageItems.DATE_SENT, now);
					values.put(MessageItems.READ, 0);
					values.put(MessageItems.TYPE, "chat");
					values.put(MessageItems.STATUS, "received");

					// Log.i(TAG, "saving chat message");
					Log.i(TAG, "Chat Message, XML: " + m.toXML());
					// just save non empty messages
					if (m.getBody() != null) {
						Uri uri = getContentResolver().insert(
								MessageItems.contentUri, values);
						// Log.i(TAG, "saved chat message to " +
						// uri.toString());

					}

					// =======================
					// Chat State
					// ========================

					PacketExtension pe = m.getExtension(new ChatStateExtension(
							ChatState.active).getNamespace());

					// now notifiy the callbacks
					if (pe.getElementName() != null) {
						Log.v(TAG, "notify the listeners about the chatstate+ "
								+ pe.getElementName());
						for (int i = mChatStateCallbacks.beginBroadcast() - 1; i >= 0; i--)
							try {
								IChatStateCallback callback = mChatStateCallbacks
										.getBroadcastItem(i);

								String jid = m.getFrom();
								Log.v(TAG, "checking jid: " + jid);
								if (mChatStateCallbacks.appliesToJid(callback,
										jid)) {
									callback.chatEventReceived(pe
											.getElementName());
									Log.v(TAG,
											"chatStateCallback notified for "
													+ jid);
								}
							} catch (RemoteException e) {
								// TODO: Details zur Exception ausgeben.
								Log.e(TAG, "RemoteException!");
								e.printStackTrace();
							}
						mChatStateCallbacks.finishBroadcast();
					}

					// =======================
					// Chat State END
					// ========================

					/*
					 * MessageEvent me= new MessageEvent(); PacketExtension
					 * pe=m.getExtension(me.getNamespace());
					 * Log.i(TAG,"message contained: body: "
					 * +m.getBody()+" "+pe.toXML());
					 */
				} else if (m.getType().equals(
						org.jivesoftware.smack.packet.Message.Type.groupchat)) {
					Log.i(TAG, "message type=groupchat");

					ContentValues values = new ContentValues();
					Long now = Long.valueOf(System.currentTimeMillis());
					values.put(MessageItems.SENDER, m.getFrom());
					values.put(MessageItems.RECIPIENT, m.getTo());
					if (m.getSubject() != null)
						values.put(MessageItems.SUBJECT, m.getSubject());
					if (m.getBody() != null)
						values.put(MessageItems.BODY, m.getBody());
					values.put(MessageItems.DATE_SENT, now);
					values.put(MessageItems.READ, 0);
					values.put(MessageItems.TYPE, "groupchat");
					values.put(MessageItems.STATUS, "received");

					Uri uri = getContentResolver().insert(
							MessageItems.contentUri, values);
					Log.i(TAG, "saved groupchat message to " + uri.toString());
				} else if (m.getType().equals(
						org.jivesoftware.smack.packet.Message.Type.error)) {
					Log.i(TAG, "message type=error");
				} else {
					Log.i(TAG, "message type=? -->" + m.getType().toString());
					sendXMPPErrorMessage(m.getFrom(),
							XMPPError.Condition.feature_not_implemented,
							"No service available for this kind of request.");
				}

			} else if (packet instanceof IQImpl) {
				// ===================================
				// IQ packets
				// ===================================
				Log.i(TAG, "packet instance of IQImpl");
				final XMPPIQ parcelable = ((IQImpl) packet).toXMPPIQ();

				for (int i = mIQCallbacks.beginBroadcast() - 1; i >= 0; i--)
					try {
						IXMPPIQCallback callback = mIQCallbacks
								.getBroadcastItem(i);
						for (PacketFilter filter : mIQCallbacks
								.getFilters(callback))
							if (filter.accept(packet))
								callback.processIQ(parcelable);
					} catch (RemoteException e) {
						// TODO: Details zur Exception ausgeben.
						Log.e(TAG, "RemoteException!");
						e.printStackTrace();
					}
				mIQCallbacks.finishBroadcast();
			} else if (packet instanceof PubSub) {
				// ===================================
				// PubSub packets
				// ===================================
				Log.i(TAG, "packet instance of PubSub");

				PubSub pubsub = (PubSub) packet;

				IQ.Type typeFrom = pubsub.getType();
				int typeTo = XMPPIQ.TYPE_SET;
				if (typeFrom == IQ.Type.SET)
					typeTo = XMPPIQ.TYPE_SET;
				else if (typeFrom == IQ.Type.GET)
					typeTo = XMPPIQ.TYPE_GET;
				else if (typeFrom == IQ.Type.ERROR)
					typeTo = XMPPIQ.TYPE_ERROR;
				else if (typeFrom == IQ.Type.RESULT)
					typeTo = XMPPIQ.TYPE_RESULT;
				XMPPIQ parcelable = new XMPPIQ(pubsub.getFrom(),
						pubsub.getTo(), typeTo, pubsub.getElementName(),
						pubsub.getNamespace(), pubsub.getChildElementXML());
				parcelable.packetID = pubsub.getPacketID();

				for (int i = mIQCallbacks.beginBroadcast() - 1; i >= 0; i--)
					try {
						IXMPPIQCallback callback = mIQCallbacks
								.getBroadcastItem(i);
						for (PacketFilter filter : mIQCallbacks
								.getFilters(callback)) {
							filter.toString();
							if (filter.accept(packet))
								callback.processIQ(parcelable);
						}
					} catch (RemoteException e) {
						// TODO: Details zur Exception ausgeben.
						Log.e(TAG, "RemoteException!");
						e.printStackTrace();
					}
				mIQCallbacks.finishBroadcast();

			} else if (packet instanceof Presence) {
				// ===================================
				// Presence packets
				// ===================================
				// update db
				Log.i(TAG, "received presence in reader Thread: "
						+ ((Presence) packet).getFrom());

				// add to own resources list if appropriate
				Presence presence = (Presence) packet;
				String me = mConn.getUser();
				if (!presence.getFrom().equals(me)) {
					String bareAddress = StringUtils.parseBareAddress(presence
							.getFrom());
					if (bareAddress.equals(StringUtils.parseBareAddress(me))) {
						// same user, different resource
						if (presence.getType() == Presence.Type.unavailable) {
							// remove from list
							if (mOwnResources.contains(presence.getFrom())) {
								mOwnResources.remove(presence.getFrom());
							}
						} else {
							if (!mOwnResources.contains(presence.getFrom())) {
								mOwnResources.add(presence.getFrom());
							}
						}
					}
				}

				/*
				 * Presence pres = (Presence) packet; ContentValues values = new
				 * ContentValues(); String user =
				 * StringUtils.parseBareAddress(pres.getFrom());
				 * values.put(RosterItems.XMPP_ID, user); if (pres.getType() ==
				 * Presence.Type.unavailable)
				 * values.put(RosterItems.PRESENCE_MODE, "offline"); else if
				 * (pres.getMode() == null)
				 * values.put(RosterItems.PRESENCE_MODE, "online"); else
				 * values.put(RosterItems.PRESENCE_MODE, pres.getMode()
				 * .toString()); values.put(RosterItems.PRESENCE_STATUS,
				 * pres.getStatus());
				 * 
				 * Log.i(TAG, "saving presence information"); Uri uri =
				 * getContentResolver().insert(RosterItems.CONTENT_URI, values);
				 * Log.i(TAG, "saved presence to " + uri.toString());
				 */
			} else {
				Log.e(TAG, "Packet unknown. XML: " + packet.toXML());
				if (packet.getError() != null) {
					Log.e(TAG, "ERROR Message: " + packet.getError());
				}
			}
		}

		// ==========================================================
		// Interface methods
		// ==========================================================

		@Override
		public void connectionClosed() {
			notifyConnectionListeners(false);
			Log.v(TAG, "CL:connection closed");
		}

		@Override
		public void connectionClosedOnError(Exception e) {
			notifyConnectionListeners(false);
			Log.v(TAG, "CL:connection closed on error " + e.getMessage());
			if (mNetworkMonitor.isConnected())
				reconnect();
			else 
				mNetworkMonitor.scheduleConnect();
		}

		@Override
		public void reconnectingIn(int seconds) {
			notifyConnectionListeners(false);
			Log.v(TAG, "CL:reconnecting in " + seconds + " s");
		}

		@Override
		public void reconnectionFailed(Exception e) {
			notifyConnectionListeners(false);
			Log.v(TAG, "CL:reconnection failed due to: " + e.getMessage());
		}

		@Override
		public void reconnectionSuccessful() {
			notifyConnectionListeners(true);
			Log.v(TAG, "CL:reconnection succesful");
		}

		@Override
		public void entriesAdded(Collection<String> entries) {
			// Log.v(TAG,"entries added");
			final ContentResolver cr = XMPPRemoteService.this
					.getContentResolver();
			ContentValues[] cvs = getFromStrings(entries);
			cr.bulkInsert(ConstMXA.RosterItems.contentUri, cvs);
		}

		@Override
		public void entriesDeleted(Collection<String> entries) {
			// Log.v(TAG,"entries deleted");
			final ContentResolver cr = XMPPRemoteService.this
					.getContentResolver();
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (String e : entries) {
				if (!first)
					sb.append(" or ");
				else
					first = false;
				sb.append(ConstMXA.RosterItems.XMPP_ID + "='" + e + "'");
			}
			cr.delete(ConstMXA.RosterItems.contentUri, sb.toString(), null);

		}

		@Override
		public void entriesUpdated(Collection<String> entries) {
			// Log.v(TAG,"entries updated");
			final ContentResolver cr = XMPPRemoteService.this
					.getContentResolver();
			ContentValues[] cvs = getFromStrings(entries);
			int i = 0;
			for (String e : entries) {
				String whereClause = RosterItems.XMPP_ID + "='"
						+ cvs[i].getAsString(RosterItems.XMPP_ID) + "' AND "
						+ RosterItems.RESSOURCE + "='"
						+ cvs[i].getAsString(RosterItems.RESSOURCE) + "' ";
				cr.update(ConstMXA.RosterItems.contentUri, cvs[i],
						whereClause, null);
				i++;
			}
		}

		@Override
		public void presenceChanged(Presence presence) {
			// Log.v(TAG,"presence changed");
			final ContentResolver cr = XMPPRemoteService.this
					.getContentResolver();
			ContentValues cv = this.getFromPresences(presence);
			String whereClause = RosterItems.XMPP_ID + "='"
					+ cv.getAsString(RosterItems.XMPP_ID) + "' AND "
					+ RosterItems.RESSOURCE + "='"
					+ cv.getAsString(RosterItems.RESSOURCE) + "' ";
			cr.update(ConstMXA.RosterItems.contentUri, cv, whereClause, null);
			// Log.v(TAG,"presence changed: "+presence.getFrom());
		}

		// ==========================================================
		// Private methods
		// ==========================================================

		/**
		 * 
		 * @param entries
		 *            The XMPP addresses of the contacts that have been added to
		 *            the roster.
		 */
		public ContentValues[] getFromStrings(Collection<String> entries) {
			Log.v(TAG, "getFromStrings");
			final Roster r = XMPPRemoteService.this.mConn.getRoster();
			ContentValues[] cvs = new ContentValues[entries.size()];
			int i = 0;
			for (String e : entries) {
				final RosterEntry re = r.getEntry(e);
				final Presence p = r.getPresence(e);
				cvs[i] = new ContentValues();
				cvs[i].put(ConstMXA.RosterItems.XMPP_ID,
						StringUtils.parseBareAddress(e));
				cvs[i].put(ConstMXA.RosterItems.RESSOURCE,
						StringUtils.parseResource(e));
				cvs[i].put(ConstMXA.RosterItems.NAME, re.getName());
				if (p.getMode() == null && p.isAvailable())
					cvs[i].put(ConstMXA.RosterItems.PRESENCE_MODE,
							RosterItems.MODE_AVAILABLE);
				if (p.getMode() == null && !p.isAvailable())
					cvs[i].put(ConstMXA.RosterItems.PRESENCE_MODE,
							RosterItems.MODE_UNAVAILABLE);
				cvs[i].put(ConstMXA.RosterItems.PRESENCE_STATUS, p.getStatus());
				cvs[i].put(ConstMXA.RosterItems.UPDATED_DATE,
						System.currentTimeMillis());
				i++;
			}
			return cvs;
		}

		/**
		 * 
		 * @param presence
		 * @return
		 */
		public ContentValues getFromPresences(Presence presence) {
			// Log.v(TAG,"getFromPresences");
			ContentValues cv = new ContentValues();

			/*
			 * if (presence.getMode()==null) Log.v(TAG,
			 * "user went offline "+presence.getFrom()); else Log.v(TAG,
			 * "getFromPresences --> presence.mode:"
			 * +presence.getMode().toString());
			 */
			cv.put(ConstMXA.RosterItems.XMPP_ID,
					StringUtils.parseBareAddress(presence.getFrom()));
			cv.put(ConstMXA.RosterItems.RESSOURCE,
					StringUtils.parseResource(presence.getFrom()));
			if (presence.getMode() != null) {
				cv.put(ConstMXA.RosterItems.PRESENCE_MODE, presence.getMode()
						.name());
				cv.put(ConstMXA.RosterItems.PRESENCE_STATUS,
						presence.getStatus());
			} else if (presence.isAvailable())
				cv.put(ConstMXA.RosterItems.PRESENCE_MODE,
						RosterItems.MODE_AVAILABLE);
			else
				cv.put(ConstMXA.RosterItems.PRESENCE_MODE,
						RosterItems.MODE_UNAVAILABLE);
			cv.put(ConstMXA.RosterItems.UPDATED_DATE,
					System.currentTimeMillis());
			return cv;
		}

		/**
		 * Sends an XMPP Error Message to the recipient.
		 */
		private void sendXMPPErrorMessage(String to,
				XMPPError.Condition condition, String errorText) {
			org.jivesoftware.smack.packet.Message msg = new org.jivesoftware.smack.packet.Message(
					to, org.jivesoftware.smack.packet.Message.Type.error);
			msg.setError(new XMPPError(condition, errorText));
			mConn.sendPacket(msg);
		}

		/**
		 * Notifies all remote connection listeners on connection changes.
		 * 
		 * @param connected
		 *            If XMPP is connected or not.
		 */
		private void notifyConnectionListeners(boolean connected) {
			if (!connected) {
				NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				nm.cancel(XMPPSERVICE_STATUS);
			}

			int i = mConnectionCallbacks.beginBroadcast();
			while (i > 0) {
				i--;
				try {
					mConnectionCallbacks.getBroadcastItem(i)
							.onConnectionChanged(connected);
				} catch (RemoteException e) {
					// The RemoteCallbackList will take care of
					// removing the dead object for us.
				}
			}
			mConnectionCallbacks.finishBroadcast();
		}
	}

	public IXMPPService getXMPPService() {
		return mBinder;
	}

	private final IXMPPService.Stub mBinder = new IXMPPService.Stub() {

		@Override
		public void connect(Messenger acknowledgement) throws RemoteException {
			Log.i(TAG, "connect to XMPP server");
			Message msg = new Message();
			msg.what = ConstMXA.MSG_CONNECT;

			
			// set ack target
			Bundle data = new Bundle();
			data.putParcelable("MSN_ACK", acknowledgement);
			msg.setData(data);

			xmppWriteWorker.mHandler.sendMessage(msg);
		}

		@Override
		public void disconnect(Messenger acknowledgement)
				throws RemoteException {
			Log.i(TAG, "disconnect from XMPP server");
			Message msg = new Message();
			msg.what = ConstMXA.MSG_DISCONNECT;

			// set ack target
			Bundle data = new Bundle();
			data.putParcelable("MSN_ACK", acknowledgement);
			msg.setData(data);

			xmppWriteWorker.mHandler.sendMessage(msg);
		}

		@Override
		public void sendMessage(Messenger acknowledgement, int requestCode,
				XMPPMessage message) throws RemoteException {
			Message msg = Message.obtain();
			// send packet
			msg.what = ConstMXA.MSG_SEND_MESSAGE;
			msg.arg2 = requestCode;
			// create new Bundle and supply the Acknowledgement Messenger
			Bundle data = new Bundle();
			data.putParcelable("MSN_ACK", acknowledgement);
			data.putParcelable("PAYLOAD", message);
			msg.setData(data);

			// send message to a writer thread
			MessageRunner mr = new MessageRunner(msg);
			mWriteExecutor.execute(mr);
		}

		@Override
		public void sendIQ(Messenger acknowledgement, Messenger result,
				int requestCode, XMPPIQ iq) throws RemoteException {
			// if iq type is GET or SET, send iq to ThreadPool for result
			// waiting. if iq type is RESULT or ERROR, send iq to worker thread

			Message msg = new Message();
			msg.what = ConstMXA.MSG_SEND_IQ;
			msg.arg1 = ConstMXA.MSG_STATUS_REQUEST;
			msg.arg2 = requestCode;
			Bundle data = new Bundle();
			data.putParcelable("MSN_ACK", acknowledgement);
			data.putParcelable("MSN_RESULT", result);
			data.putParcelable("PAYLOAD", iq);
			msg.setData(data);

			if (iq.type == XMPPIQ.TYPE_GET || iq.type == XMPPIQ.TYPE_SET) {
				IQRunner iqRunJob = new IQRunner(msg);
				mWriteExecutor.execute(iqRunJob);
			} else {
				// send iq to worker thread
				xmppWriteWorker.mHandler.sendMessage(msg);
			}
		}

		@Override
		public void sendPresence(Messenger acknowledgement, int requestCode,
				XMPPPresence presence) throws RemoteException {
			Message msg = new Message();
			// send packet
			msg.what = ConstMXA.MSG_SEND_PRESENCE;
			msg.arg2 = requestCode;
			// create new Bundle and supply the Acknowledgement Messenger
			Bundle data = new Bundle();
			data.putParcelable("MSN_ACK", acknowledgement);
			data.putParcelable("PAYLOAD", presence);
			msg.setData(data);

			// send message to worker thread
			xmppWriteWorker.mHandler.sendMessage(msg);
		}

		@Override
		public void registerDataMessageCallback(IXMPPMessageCallback cb,
				String namespace, String token) throws RemoteException {
			if (cb != null) {

				IQImplFilter iif = new IQImplFilter(token, namespace);
				// MXAIdentFilter mif = new MXAIdentFilter(namespace, token);
				mMsgCallbacks.register(cb, iif);
				MessageExtensionProvider mep = new MessageExtensionProvider(
						namespace, token);
				ProviderManager.getInstance().addExtensionProvider(token,
						namespace, mep);

			}
		}

		@Override
		public void unregisterDataMessageCallback(IXMPPMessageCallback cb,
				String namespace, String token) throws RemoteException {
			if (cb != null) {
				// MXAIdentFilter mif = new MXAIdentFilter(namespace, token);
				IQImplFilter iif = new IQImplFilter(token, namespace);
				mMsgCallbacks.unregister(cb, iif);
			}
		}

		@Override
		public String getUsername() throws RemoteException {
			if (mConn != null) {
				return mConn.getUser();
			} else {
				return null;
			}
			// Bundle xmppConnectionParameters = getXMPPConnectionParameters();
			// return xmppConnectionParameters.getString("xmpp_user") + "@" +
			// xmppConnectionParameters.getString("xmpp_service") + "/" +
			// xmppConnectionParameters.getString("xmpp_resource");
		}

		@Override
		public boolean isConnected() throws RemoteException {
			if (mConn != null) {
				return mConn.isAuthenticated();
			} else {
				return false;
			}
		}

		@Override
		public void registerConnectionCallback(IConnectionCallback cb)
				throws RemoteException {
			mConnectionCallbacks.register(cb);
		}

		@Override
		public void unregisterConnectionCallback(IConnectionCallback cb)
				throws RemoteException {
			mConnectionCallbacks.unregister(cb);
		}

		@Override
		public IFileTransferService getFileTransferService()
				throws RemoteException {
			IBinder b = mFileTransferService.onBind(null);
			return (IFileTransferService) b;
		}

		@Override
		public IMultiUserChatService getMultiUserChatService()
				throws RemoteException {
			IBinder b = mMultiUserChatService.onBind(null);
			return (IMultiUserChatService) b;
		}

		@Override
		public IPubSubService getPubSubService() throws RemoteException {
			// TODO Auto-generated method stub
			IBinder b = mPubSubService.onBind(null);
			return (IPubSubService) b;
		}

		public IServiceDiscoveryService getServiceDiscoveryService()
				throws RemoteException {
			IBinder b = mServiceDiscoveryService.onBind(null);
			return (IServiceDiscoveryService) b;
		}

		@Override
		public ISessionMobilityService getSessionMobilityService()
				throws RemoteException {
			IBinder b = mSessionMobilityService.onBind(null);
			return (ISessionMobilityService) b;
		}

		@Override
		public IServerlessMessagingService getServerlessMessagingService()
				throws RemoteException {
			if (mServerlessMessagingService == null) {
				mServerlessMessagingService = new ServerlessMessagingService(
						XMPPRemoteService.this);
			}
			IBinder b = mServerlessMessagingService.onBind(null);
			return (IServerlessMessagingService) b;
		}

		@Override
		public IMessageCarbonsService getMessageCarbonsService()
				throws RemoteException {
			IBinder b = mMessageCarbonsService.onBind(null);
			return (IMessageCarbonsService) b;
		}

		@Override
		public void registerIQCallback(IXMPPIQCallback cb, String elementName,
				String namespace) throws RemoteException {
			if (cb != null) {
				IQImplProvider iqProvider = new IQImplProvider(namespace,
						elementName);
				ProviderManager.getInstance().addIQProvider(elementName,
						namespace, iqProvider);
				IQImplFilter iqFilter = new IQImplFilter(elementName, namespace);
				boolean result = mIQCallbacks.register(cb, iqFilter);
				Log.i(TAG, "registerIQCallback(). elementName=" + elementName
						+ " namespace=" + namespace + ". result=" + result);
			}
		}

		@Override
		public void unregisterIQCallback(IXMPPIQCallback cb,
				String elementName, String namespace) throws RemoteException {
			if (cb != null) {
				// TODO remove IQ Provider if all callbacks for this
				// elementName/namespace combination have been unregistered.
				IQImplFilter iqFilter = new IQImplFilter(elementName, namespace);
				mIQCallbacks.unregister(cb, iqFilter);
			}
		}

		@Override
		public Bundle getXMPPConnectionParameters() throws RemoteException {
			// TODO Auto-generated method stub

			// read server preferences
			String host = mPreferences.getString("pref_host", null);
			int port = Integer.parseInt(mPreferences.getString("pref_port",
					null));
			String serviceName = mPreferences.getString("pref_service", null);
			// read user credentials
			String username = mPreferences.getString("pref_xmpp_user", null);
			String password = mPreferences
					.getString("pref_xmpp_password", null);
			String resource = mPreferences.getString("pref_resource", null);

			/*
			 * if ((host == null) || (serviceName == null) || (username == null)
			 * || (password == null) || (resource==null)) { return null; }
			 */

			Bundle connectionParams = new Bundle();
			connectionParams.putString("xmpp_host", host);
			connectionParams.putInt("xmpp_port", port);
			connectionParams.putString("xmpp_service", serviceName);
			connectionParams.putString("xmpp_user", username);
			connectionParams.putString("xmpp_password", password);
			connectionParams.putString("xmpp_resource", resource);
			connectionParams.putInt("lostiqqueue_count", mIQQueue.getCount());

			return connectionParams;
		}

		@Override
		public void registerChatStateCallback(IChatStateCallback cb, String jid)
				throws RemoteException {
			if (cb != null && jid != null) {
				if (mChatStateCallbacks.register(cb, jid))
					Log.v(TAG,
							"registered callback for chatstate notification for jid:"
									+ jid);
			}

		}

		@Override
		public void unregisterChatStateCallback(IChatStateCallback cb,
				String jid) throws RemoteException {
			if (cb != null && jid != null) {
				if (mChatStateCallbacks.unregister(cb, jid))
					Log.v(TAG,
							"unregistered callback for chatstate notification for jid:"
									+ jid);
			}
		}

		@Override
		public void sendIQInMessage(Messenger acknowledgement, XMPPIQ iq)
				throws RemoteException {
			Message msg = Message.obtain();
			// send packet
			msg.what = ConstMXA.MSG_SEND_MESSAGE;
			// create new Bundle and supply the Acknowledgement Messenger
			Bundle data = new Bundle();
			data.putParcelable("MSN_ACK", acknowledgement);

			XMPPMessage m = new XMPPMessage(iq.from, iq.to, null,
					XMPPMessage.TYPE_NORMAL);
			m.namespace = iq.namespace;
			m.token = iq.element;
			data.putParcelable("PAYLOAD", m);
			data.putString("PAYLOAD_MSG", iq.payload);

			msg.setData(data);
			// send message to a writer thread
			MessageRunner mr = new MessageRunner(msg);
			mWriteExecutor.execute(mr);

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

		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",
				new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",
					Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",
				new RosterExchangeProvider());

		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",
				new MessageEventProvider());

		// Chat State
		pm.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
				new XHTMLExtensionProvider());

		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());

		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());

		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());

		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
				new MUCUserProvider());

		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
				new MUCAdminProvider());

		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
				new MUCOwnerProvider());

		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",
				new DelayInformationProvider());

		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}

		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());

		// Offline Message Indicator
		pm.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());

		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());

		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());

		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());
		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());
		pm.addIQProvider("open", "http://jabber.org/protocol/ibb",
				new OpenIQProvider());
		pm.addIQProvider("close", "http://jabber.org/protocol/ibb",
				new CloseIQProvider());
		pm.addExtensionProvider("data", "http://jabber.org/protocol/ibb",
				new DataPacketProvider());

		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
	}

	/**
	 * Prints all information about roster items to the log
	 */
	/*
	 * private void printRosterToLog() { final ContentResolver cr =
	 * XMPPRemoteService.this .getContentResolver();
	 * 
	 * Cursor c=cr.query(ConstMXA.RosterItems.CONTENT_URI, null, null,null,
	 * null); if (!c.moveToFirst()) { Log.v(TAG, "Roster is empty"); return;
	 * }else { Log.v(TAG,c.getString(0)+" "+ c.getString(1)+" "+
	 * c.getString(2)+" "+ c.getString(3)+" "+ c.getString(4)+" "+
	 * c.getString(5)); while(c.moveToNext()) { Log.v(TAG,c.getString(0)+" "+
	 * c.getString(1)+" "+ c.getString(2)+" "+ c.getString(3)+" "+
	 * c.getString(4)+" "+ c.getString(5)); } c.close(); }
	 * 
	 * }
	 */

	/**
	 * Forwards the MSG_RECONNECT to the XMPPRemoteService.
	 */
	protected void reconnect() {
		Message msg = new Message();
		msg.what = ConstMXA.MSG_RECONNECT;
		xmppWriteWorker.mHandler.sendMessage(msg);
	}
	
	/**
	 * Forwards the MSG_CONNECT to the XMPPRemoteService.
	 */
	protected void connect() {
		Message msg = new Message();
		msg.what = ConstMXA.MSG_CONNECT;
		xmppWriteWorker.mHandler.sendMessage(msg);
	}
	
	/**
	 * Get all packets that are in the LostIQQueue and sends them. Assumes that
	 * for every packet there is a corresponding thread.
	 */
	public void resendIQ() {
		// to the same as sendIQ, but we take the values from the list
		Log.v(TAG, "start resending iqs");
		LostIQQueueEntry e;
		int size = mIQQueue.getCount();
		// try to send each packet only once
		for (int i = 0; i < size; i++) {
			e = mIQQueue.getOldestEntry();
			if (e == null)
				return;
			if (e.mSending)
				continue;
			e.mSending = true;
			Message msg = Message.obtain(e.mMessage);
			msg.what = ConstMXA.MSG_IQ_RESEND;
			msg.getData().putLong(ConstMXA.IQ_RESEND_ID, e.mID);
			IQRunner iqRunJob = new IQRunner(msg);
			mWriteExecutor.execute(iqRunJob);

		}
		Log.v(TAG, "end resending iqs");

	}

	/**
	 * Just forwards a call to the write worker that handles that message
	 * 
	 * @author Christian Magenheimer
	 * 
	 */
	private class IQQueueCheckBackgroundRunner extends TimerTask {

		@Override
		public void run() {
			Message msg = new Message();
			msg.what = ConstMXA.MSG_IQ_RESEND;
			xmppWriteWorker.mHandler.sendMessage(msg);
		}

	}

}
