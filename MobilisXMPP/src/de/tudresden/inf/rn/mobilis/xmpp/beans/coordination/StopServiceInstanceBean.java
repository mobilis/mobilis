package de.tudresden.inf.rn.mobilis.xmpp.beans.coordination;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class StopServiceInstanceBean extends XMPPBean {

	private static final long serialVersionUID = -1498478496597937673L;

	public static final String NAMESPACE = Mobilis.NAMESPACE + "#XMPPBeans:coordination:stopService";
	public static final String CHILD_ELEMENT = "stopServiceInstance";

	public String ServiceJid;

	private String _xmlTag_ServiceJid = "servicejid";

	
	public StopServiceInstanceBean() {}
	
	// SET
	public StopServiceInstanceBean(String serviceJid) {
		super();
		this.ServiceJid = serviceJid;
	}

	public StopServiceInstanceBean(String errorType, String errorCondition, String errorText) {
		super( errorType, errorCondition, errorText );
	}

	@Override
	public StopServiceInstanceBean clone() {
		StopServiceInstanceBean clone = new StopServiceInstanceBean( ServiceJid );

		clone = (StopServiceInstanceBean)cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		if ( this.ServiceJid != null )
			sb.append( "<" + _xmlTag_ServiceJid + ">" ).append( ServiceJid )
					.append( "</" + _xmlTag_ServiceJid + ">" );

		sb = appendErrorPayload( sb );
		return sb.toString();
	}

	@Override
	public void fromXML( XmlPullParser parser ) throws Exception {
		String childElement = StopServiceInstanceBean.CHILD_ELEMENT;

		boolean done = false;
		do {
			switch ( parser.getEventType() ) {
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				if ( tagName.equals( childElement ) ) {
					parser.next();
				} else if ( tagName.equals( _xmlTag_ServiceJid ) ) {
					this.ServiceJid = parser.nextText();
				} else if ( tagName.equals( "error" ) ) {
					parser = parseErrorAttributes( parser );
				} else
					parser.next();
				break;
			case XmlPullParser.END_TAG:
				if ( parser.getName().equals( childElement ) )
					done = true;
				else
					parser.next();
				break;
			case XmlPullParser.END_DOCUMENT:
				done = true;
				break;
			default:
				parser.next();
			}
		} while ( !done );

	}

	@Override
	public String getChildElement() {
		return StopServiceInstanceBean.CHILD_ELEMENT;
	}

	@Override
	public String getNamespace() {
		return StopServiceInstanceBean.NAMESPACE;
	}

}
