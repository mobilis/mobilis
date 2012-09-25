package de.tudresden.inf.rn.mobilis.mxa.services.messagecarbons;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;

public class ForwardedExtension implements PacketExtension {

	public static final String elementName = "forwarded";
	public static final String namespace = "urn:xmpp:forward:0";

	// members
	private Packet mForwardedPacket;

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
		buf.append("\">");

		// add sent_date
		buf.append("<delay xmlns=\"urn:xmpp:delay\" stamp=\"");
		buf.append(mForwardedPacket.getProperty("stamp"));
		buf.append("\"/>");
		
		// add packet
		buf.append(mForwardedPacket.toXML());

		buf.append("</");
		buf.append(elementName);
		buf.append(">");

		return buf.toString();
	}

	// ==========================================================
	// Private methods
	// ==========================================================

	public void setForwardedPacket(Packet packet) {
		mForwardedPacket = packet;
	}

	public Packet getForwardedPacket() {
		return mForwardedPacket;
	}
}
