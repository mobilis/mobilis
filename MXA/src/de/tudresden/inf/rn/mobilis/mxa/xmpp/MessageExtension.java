package de.tudresden.inf.rn.mobilis.mxa.xmpp;

import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.packet.PacketExtension;

/**
 * Contains informationen about the element name, its namespace and the
 * payload, that is in the message stanza.
 * @author Christian Magenheimer
 *
 */
public class MessageExtension implements PacketExtension{

	
	private String elementName,namespace,payload;
	
	public MessageExtension(String elementName, String namespace, String payload)
	{
		this.elementName=elementName;
		this.namespace=namespace;
		this.payload=payload;
	}
	
	@Override
	public String getElementName() {
		return elementName;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}
	
	public String getPayload()
	{
		return payload;
	}

	@Override
	public String toXML() {
		StringBuilder sb=new StringBuilder();
		sb.append("<").append(elementName).append(" xmlns=\"").append(namespace).append("\"");
		if (payload == null)sb.append(" />");
		else sb.append(payload).append("</").append(elementName).append(">");
		
		return sb.toString();
	}

}
