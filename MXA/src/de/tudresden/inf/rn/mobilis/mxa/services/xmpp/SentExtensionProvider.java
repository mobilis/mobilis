package de.tudresden.inf.rn.mobilis.mxa.services.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.mxa.services.messagecarbons.SentExtension;

public class SentExtensionProvider implements PacketExtensionProvider {

	@Override
	public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		SentExtension se = new SentExtension();
		return se;
	}

}
