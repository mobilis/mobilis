package de.tudresden.inf.rn.mobilis.mxa.services.sessionmobility;

import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;

public class ForwardedStateExtension implements PacketExtension {

	public static final String elementName = "forwarded";
	public static final String namespace = "urn:xmpp:forward:state";

	// members
	private LinkedList<Packet> mForwardedPackets;

	public ForwardedStateExtension() {
		mForwardedPackets = new LinkedList<Packet>();
	}

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

		for (Packet packet : mForwardedPackets) {
			buf.append("<delay xmlns=\"urn:xmpp:delay\" stamp=\"");
			buf.append(packet.getProperty("stamp"));
			buf.append("\"/>");
			buf.append(packet.toXML());
		}

		buf.append("</");
		buf.append(elementName);
		buf.append(">");

		return buf.toString();
	}

	// ==========================================================
	// Private methods
	// ==========================================================

	public void addForwardedPacket(Packet packet) {
		mForwardedPackets.add(packet);
	}

	public List<Packet> getForwardedPackets() {
		return mForwardedPackets;
	}
}
