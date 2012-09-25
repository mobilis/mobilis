package de.tudresden.inf.rn.mobilis.mxa.services.messagecarbons;

import org.jivesoftware.smack.packet.PacketExtension;

public class SentExtension implements PacketExtension {

	public static final String elementName = "sent";
	public static final String namespace = "urn:xmpp:carbons:1";
	
	@Override
	public String getElementName() {
		return elementName;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String toXML() {
		StringBuffer buf = new StringBuffer();
		buf.append("<");
		buf.append(elementName);
		buf.append(" xmlns=\"");
		buf.append(namespace);
		buf.append("\"/>");
		return buf.toString();
	}

}
