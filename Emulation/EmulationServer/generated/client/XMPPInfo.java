package de.tudresden.inf.rn.mobilis.android.xhunt.emulation.clientstub;


			
import java.io.Serializable;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author Benjamin Söllner, Robert Lübke
 */
public interface XMPPInfo extends Serializable {
	/**
	 * Parses the XML String of an XMPP stanza and saves all neccessary information. 
	 * @param parser XML Parser to use for parsing the XML String
	 * @throws Exception
	 */
	public void fromXML(XmlPullParser parser) throws Exception;
	/**
	 * Converts this XMPP stanza into its representation as XML string.
	 * @return XML string representation of this XMPP stanza 
	 */
	public String toXML(); 
	/**
	 * @return Child Element of this XMPP stanza
	 */
	public String getChildElement();
	/**
	 * @return Namespace of this XMPP stanza
	 */
	public String getNamespace();
}
		