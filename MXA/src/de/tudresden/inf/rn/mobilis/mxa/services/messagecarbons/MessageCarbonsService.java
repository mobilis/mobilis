package de.tudresden.inf.rn.mobilis.mxa.services.messagecarbons;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.MessageItems;
import de.tudresden.inf.rn.mobilis.mxa.XMPPRemoteService;
import de.tudresden.inf.rn.mobilis.mxa.services.xmpp.ForwardedExtensionProvider;
import de.tudresden.inf.rn.mobilis.mxa.services.xmpp.SentExtensionProvider;
import de.tudresden.inf.rn.mobilis.mxa.xmpp.IQImpl;

public class MessageCarbonsService extends Service {

	// TAG for logging
	private static final String TAG = "MessageCarbonsService";

	// members
	private final XMPPRemoteService mXMPPService;
	private ReaderThread mXMPPReadWorker;
	private boolean mCarbonsServerSupported = false;

	public MessageCarbonsService(XMPPRemoteService service) {
		mXMPPService = service;
		mXMPPReadWorker = new ReaderThread();

		// register provider for message forwards
		ProviderManager pm = ProviderManager.getInstance();
		pm.addExtensionProvider(SentExtension.elementName,
				SentExtension.namespace, new SentExtensionProvider());
		pm.addExtensionProvider(ForwardedExtension.elementName,
				ForwardedExtension.namespace, new ForwardedExtensionProvider());

		// add listener
		PacketFilter pf = new AndFilter(new PacketExtensionFilter(
				SentExtension.elementName, SentExtension.namespace),
				new PacketExtensionFilter(ForwardedExtension.namespace));
		mXMPPService.getXMPPConnection().addPacketListener(mXMPPReadWorker, pf);
	}

	@Override
	public IBinder onBind(Intent i) {
		return mBinder;
	}

	private final IMessageCarbonsService.Stub mBinder = new IMessageCarbonsService.Stub() {

		@Override
		public void enableCarbons() throws RemoteException {
			// send enable IQ
			IQImpl enableIQ = new IQImpl("<enable xmlns='urn:xmpp:carbons:1'/>");
			enableIQ.setType(IQ.Type.SET);
			mXMPPService.getXMPPConnection().sendPacket(enableIQ);
		}
	};

	// ==========================================================
	// Public methods
	// ==========================================================

	/**
	 * Returns whether the server supports the carbons XEP.
	 * 
	 * @return true if the server supports carbons, false if not
	 */
	public boolean hasServerCarbonsSupport() {
		return mCarbonsServerSupported;
	}

	// ==========================================================
	// Inner classes
	// ==========================================================

	private class ReaderThread extends Thread implements PacketListener {

		@Override
		public void processPacket(Packet packet) {
			Log.i(TAG, "received forward");
			ForwardedExtension forward = (ForwardedExtension) packet
					.getExtension(ForwardedExtension.elementName,
							ForwardedExtension.namespace);
			if (forward != null) {
				Packet forwardedPacket = forward.getForwardedPacket();
				if (forwardedPacket != null) {
					if (forwardedPacket instanceof Message) {
						Message msg = (Message) forwardedPacket;
						// save msg in database
						if (msg.getType().equals(Message.Type.chat)) {
							// save in database, the content provider takes care
							// of notifying interested parties
							ContentValues values = new ContentValues();
							values.put(MessageItems.SENDER, msg.getFrom());
							values.put(MessageItems.RECIPIENT, msg.getTo());
							if (msg.getSubject() != null)
								values.put(MessageItems.SUBJECT,
										msg.getSubject());
							if (msg.getBody() != null)
								values.put(MessageItems.BODY, msg.getBody());
							values.put(MessageItems.DATE_SENT,
									(String) msg.getProperty("stamp"));
							values.put(MessageItems.READ, 0);
							values.put(MessageItems.TYPE, "chat");
							values.put(MessageItems.STATUS, "received");

							// just save non empty messages
							if (msg.getBody() != null) {
								Uri uri = mXMPPService.getContentResolver()
										.insert(MessageItems.contentUri,
												values);
								Log.i(TAG, "saved forwarded message to URI "
										+ uri.getPath());
							}
						}
					}
				}
			}
		}
	}
}
