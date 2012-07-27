package de.tudresden.inf.rn.mobilis.mxa.services.serverlessmessaging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import org.jivesoftware.smack.util.StringUtils;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import de.tudresden.inf.rn.mobilis.mxa.XMPPRemoteService;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPPresence;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IServerlessMessageCallback;

public class ServerlessMessagingService extends Service {

	// TAG for logging
	private static final String TAG = "ServerlessMessagingService";

	// members
	private final XMPPRemoteService mXMPPService;
	private ListenerThread mServiceListener;
	private boolean registered = false;
	private String mHost;
	private String mResource;
	final RemoteCallbackList<IServerlessMessageCallback> mMessageCallbacks = new RemoteCallbackList<IServerlessMessageCallback>();
	private ConcurrentHashMap<String, ServiceInfo> mAdhocClients = new ConcurrentHashMap<String, ServiceInfo>();
	private ConcurrentHashMap<String, ConnectionHandler> mServerConnections = new ConcurrentHashMap<String, ConnectionHandler>();
	private ConcurrentHashMap<String, ClientThread> mClientConnections = new ConcurrentHashMap<String, ClientThread>();

	// JmDNS members
	private android.net.wifi.WifiManager.MulticastLock lock;
	private String type = "_presence._tcp.local.";
	private JmDNS jmdns = null;
	private ServiceInfo serviceInfo;
	private int port = 5562;

	public ServerlessMessagingService(XMPPRemoteService service) {
		mXMPPService = service;

		// read user data
		mHost = StringUtils.parseName(mXMPPService.getPreferences().getString(
				"pref_xmpp_user", ""));
		mResource = mXMPPService.getPreferences().getString("pref_resource",
				"mxa");
	}

	@Override
	public IBinder onBind(Intent i) {
		return mBinder;
	}

	private final IServerlessMessagingService.Stub mBinder = new IServerlessMessagingService.Stub() {

		@Override
		public void registerPresence(XMPPPresence presence)
				throws RemoteException {
			// register mDNS service
			Log.i(TAG, "publishing presence on .local");

			if (mServiceListener == null) {
				mServiceListener = new ListenerThread();
				// mServerThread = new ServerThread();
			}

			if (!registered) {

				android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) mXMPPService
						.getSystemService(android.content.Context.WIFI_SERVICE);
				lock = wifi.createMulticastLock("serverlessmessaginglock");
				lock.setReferenceCounted(true);
				lock.acquire();
				try {
					jmdns = JmDNS.create(getLocalIpAddress(), mHost);
					jmdns.addServiceListener(type, mServiceListener);
					if (serviceInfo == null) {
						serviceInfo = ServiceInfo
								.create("_presence._tcp.local.", mResource
										+ "@" + mHost, port,
										"Peer-to-peer messaging / Link-Local Messaging");
						HashMap<String, String> txtRecord = new HashMap<String, String>();
						txtRecord.put("1st", mResource);
						txtRecord.put("last", mHost);
						txtRecord.put("port.p2pj", String.valueOf(port));
						txtRecord.put("status", "avail");
						txtRecord.put("nick", mHost + mResource);
						serviceInfo.setText(txtRecord);
					}
					jmdns.registerService(serviceInfo);
					registered = true;

					// start open socket
					SERVERIP = getLocalIpAddress().getHostAddress().toString();
					Thread fst = new Thread(new ServerThread());
					fst.start();

				} catch (IOException e) {
					e.printStackTrace();
					return;
				}

			}
		}

		@Override
		public void unregisterPresence() throws RemoteException {
			if (registered) {
				// close all connections

				// unregister services and listener
				jmdns.unregisterAllServices();
				jmdns.removeServiceListener(type, mServiceListener);
				try {
					jmdns.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				jmdns = null;
				lock.release();
				serviceInfo = null;
				registered = false;
			}
		}

		@Override
		public void registerMessageCallback(IServerlessMessageCallback callback)
				throws RemoteException {
			mMessageCallbacks.register(callback);
		}

		@Override
		public void unregisterMessageCallback(
				IServerlessMessageCallback callback) throws RemoteException {
			mMessageCallbacks.unregister(callback);
		}

		@Override
		public void sendMessage(String to, String message)
				throws RemoteException {
			if (mAdhocClients.containsKey(to)) {
				String ip = mAdhocClients.get(to).getInet4Addresses()[0]
						.toString();
				int port = mAdhocClients.get(to).getPort();

				// check if incoming connection is available
				if (mServerConnections.contains(ip)) {
					ConnectionHandler con = mServerConnections.get(ip);
					con.sendMessage(message);
				} else if (mClientConnections.containsKey(ip)) {
					ClientThread ct = mClientConnections.get(ip);
					ct.sendMessage(message);
				} else {
					// start new connection
					ClientThread client = new ClientThread(ip.substring(1),
							port, mResource + "@" + mHost, to, message);
					new Thread(client).start();
					mClientConnections.put(ip, client);
				}
			} else {
				Log.e(TAG, "no such user " + to);
			}
		}

		@Override
		public void getPeers(List<String> peers) throws RemoteException {
			for (String peer : mAdhocClients.keySet()) {
				peers.add(peer);
			}
		}
	};

	// ==========================================================
	// Inner classes
	// ==========================================================

	private class ListenerThread extends Thread implements ServiceListener {

		public void run() {
			setName("ServerlessMessaging ListenerThread");
			Looper.prepare();
			Looper.loop();
		}

		@Override
		public void serviceAdded(ServiceEvent event) {
			// Required to force serviceResolved to be
			// called again (after the first search)
			jmdns.requestServiceInfo(event.getType(), event.getName(), 1);
		}

		@Override
		public void serviceRemoved(ServiceEvent event) {
			Log.i(TAG, "Service removed: " + event.getName());
			String name = event.getInfo().getName();
			if (mAdhocClients.containsKey(name)) {
				mAdhocClients.remove(name);
				Log.i(TAG, "removed " + name + " from ad-hoc users.");
			}
		}

		@Override
		public void serviceResolved(ServiceEvent event) {
			Log.i(TAG, "Service resolved: "
					+ event.getInfo().getQualifiedName() + " port:"
					+ event.getInfo().getPort());
			if (!event.getInfo().getPropertyString("nick")
					.equals(mHost + mResource)) {
				String name = event.getInfo().getName();
				if (!mAdhocClients.containsKey(name)) {
					mAdhocClients.put(name, event.getInfo());
					Log.i(TAG, "added " + name + " as new ad-hoc user.");
				} else {
					mAdhocClients.replace(name, event.getInfo());
					Log.i(TAG, "updated " + name + " info.");
				}
			}
		}

	}

	public Handler mHandler;

	// default ip
	public static String SERVERIP = "10.0.2.15";
	private ServerSocket serverSocket;

	public class ServerThread implements Runnable {

		private final LinkedList<ConnectionHandler> connectionList;

		public ServerThread() {
			connectionList = new LinkedList<ConnectionHandler>();
		}

		public void onMessageReceived(String message) {
			notifyMessageCallbacks(message);
		}

		public void run() {

			ConnectionHandler temp;
			try {
				if (SERVERIP != null) {
					Log.i(TAG, "Listening on IP: " + SERVERIP);

					serverSocket = new ServerSocket(port, 5,
							getLocalIpAddress());
					serverSocket.setReuseAddress(true);
					serverSocket.setSoTimeout(0);

					while (true) {
						// listen for incoming clients
						Socket client = serverSocket.accept();
						Log.i(TAG, "Connected.");

						temp = new ConnectionHandler(client, this);
						connectionList.add(temp);
						mServerConnections.put(client.getInetAddress()
								.toString(), temp);
						new Thread(temp).start();
					}
				} else {
					Log.i(TAG, "Couldn't detect internet connection.");
				}
			} catch (Exception e) {
				Log.i(TAG, "Error");
				e.printStackTrace();
			}
		}
	}

	public class ClientThread implements Runnable {

		// TAG for logging
		private static final String TAG = "ClientThread";

		private String serverIpAddress;
		private int serverPort;
		private boolean connected;
		private PrintWriter clientOut;
		private BufferedReader clientIn;
		private String mLocalName;
		private String mRemoteName;
		private String mInitialMessage;

		public ClientThread(String ip, int port, String localName,
				String remoteName, String initialMessage) {
			serverIpAddress = ip;
			serverPort = port;
			mLocalName = localName;
			mRemoteName = remoteName;
			mInitialMessage = initialMessage;
		}

		@Override
		public void run() {
			try {
				InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
				Log.d(TAG, "C: Connecting...");
				Socket socket = new Socket(serverAddr, serverPort);
				connected = true;
				while (connected) {
					try {
						Log.d(TAG, "C: Sending command.");
						clientOut = new PrintWriter(
								new BufferedWriter(new OutputStreamWriter(
										socket.getOutputStream())));
						clientIn = new BufferedReader(new InputStreamReader(
								socket.getInputStream(), "UTF-8"));

						clientOut
								.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><stream:stream to=\""
										+ mRemoteName
										+ "\" from=\""
										+ mLocalName
										+ "\" xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\">");
						clientOut.flush();

						String inputLine = "";
						while (!(inputLine.endsWith("/streams\">"))) {
							inputLine += (char) clientIn.read();
						}
						// stream opened
						Log.i(TAG, "Stream opened: " + inputLine);

						sendMessage(mInitialMessage);

						// wait for next message
						inputLine = "";
						while (true) {
							// keep stream open
							while (!(inputLine.endsWith("</message>"))) {
								inputLine += (char) clientIn.read();
							}
							Log.i(TAG, "Message received: " + inputLine);
							ServerlessMessagingService.this
									.notifyMessageCallbacks(inputLine);
							inputLine = "";
						}

					} catch (Exception e) {
						Log.e(TAG, "Error ", e);
					}
				}
				socket.close();
				Log.d(TAG, "Socket to " + serverIpAddress + " closed.");
			} catch (Exception e) {
				Log.e(TAG, "Error with " + serverIpAddress, e);
				connected = false;
			}
		}

		public void sendMessage(String content) {
			StringBuffer buf = new StringBuffer();
			buf.append("<message from=\"");
			buf.append(mLocalName);
			buf.append("\" type=\"chat\" to=\"");
			buf.append(mRemoteName);
			buf.append("\">");
			buf.append(content);
			buf.append("</message>");
			String message = buf.toString();
			Log.i(TAG, "Sending message: " + message);
			clientOut.write(message);
			clientOut.flush();
		}

	}

	// ==========================================================
	// Private methods
	// ==========================================================

	private InetAddress getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = (InetAddress) enumIpAddr
							.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						if (inetAddress.getAddress().length == 4) {
							return inetAddress;
						}
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("ListDevices", ex.toString());
		}
		return null;
	}

	public void notifyMessageCallbacks(String message) {
		int i = mMessageCallbacks.beginBroadcast();
		while (i > 0) {
			i--;
			try {
				mMessageCallbacks.getBroadcastItem(i).processMessage(message);
			} catch (RemoteException e) {
				// The RemoteCallbackList will take care of
				// removing the dead object for us.
			}
		}
		mMessageCallbacks.finishBroadcast();
	}
}
