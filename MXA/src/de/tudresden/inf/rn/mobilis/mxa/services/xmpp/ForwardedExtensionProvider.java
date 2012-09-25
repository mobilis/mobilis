package de.tudresden.inf.rn.mobilis.mxa.services.xmpp;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.mxa.services.messagecarbons.ForwardedExtension;

public class ForwardedExtensionProvider implements PacketExtensionProvider {

	@Override
	public PacketExtension parseExtension(XmlPullParser parser)
			throws Exception {
		ForwardedExtension fe = new ForwardedExtension();
		Message msg = null;

		boolean done = false;
		String lastDelay = null;
		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("delay")) {
					lastDelay = parser.getAttributeValue("", "stamp");
				} else if (parser.getName().equals("message")) {
					msg = (Message) PacketParserUtils.parseMessage(parser);
					msg.setProperty("stamp", lastDelay);
					done = true;
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("forwarded")) {
					done = true;
				}
			}
		}

		if (msg != null) {
			fe.setForwardedPacket(msg);
		}
		
		return fe;
	}

}
