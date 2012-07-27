package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class LocationResponse extends XMPPBean {

	private LocationInfo LocationInfo = new LocationInfo();


	public LocationResponse( LocationInfo LocationInfo ) {
		super();
		this.LocationInfo = LocationInfo;

		this.setType( XMPPBean.TYPE_RESULT );
	}

	public LocationResponse(){
		this.setType( XMPPBean.TYPE_RESULT );
	}


	@Override
	public void fromXML( XmlPullParser parser ) throws Exception {
		boolean done = false;
			
		do {
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				
				if (tagName.equals(getChildElement())) {
					parser.next();
				}
				else if (tagName.equals( LocationInfo.CHILD_ELEMENT ) ) {
					this.LocationInfo.fromXML( parser );
				}
				else if (tagName.equals("error")) {
					parser = parseErrorAttributes(parser);
				}
				else
					parser.next();
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals(getChildElement()))
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
		} while (!done);
	}

	public static final String CHILD_ELEMENT = "LocationResponse";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:location";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		LocationResponse clone = new LocationResponse( LocationInfo );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<" + this.LocationInfo.getChildElement() + ">" )
			.append( this.LocationInfo.toXML() )
			.append( "</" + this.LocationInfo.getChildElement() + ">" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public LocationInfo getLocationInfo() {
		return this.LocationInfo;
	}

	public void setLocationInfo( LocationInfo LocationInfo ) {
		this.LocationInfo = LocationInfo;
	}

}