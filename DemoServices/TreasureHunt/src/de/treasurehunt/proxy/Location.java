package de.treasurehunt.proxy;
import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

public class Location implements XMPPInfo {

	private long Latitude = Long.MIN_VALUE;
	private long Longitude = Long.MIN_VALUE;


	public Location( long Latitude, long Longitude ) {
		super();
		this.Latitude = Latitude;
		this.Longitude = Longitude;
	}

	public Location(){}



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
				else if (tagName.equals( "Latitude" ) ) {
					this.Latitude = Long.parseLong( parser.nextText() );
				}
				else if (tagName.equals( "Longitude" ) ) {
					this.Longitude = Long.parseLong( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "Location";

	@Override
	public String getChildElement() {
		return this.CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "http://mobilis.inf.tu-dresden.de#services/TreasureHuntService#type:Location";

	@Override
	public String getNamespace() {
		return this.NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<Latitude>" )
			.append( this.Latitude )
			.append( "</Latitude>" );

		sb.append( "<Longitude>" )
			.append( this.Longitude )
			.append( "</Longitude>" );

		return sb.toString();
	}



	public long getLatitude() {
		return this.Latitude;
	}

	public void setLatitude( long Latitude ) {
		this.Latitude = Latitude;
	}

	public long getLongitude() {
		return this.Longitude;
	}

	public void setLongitude( long Longitude ) {
		this.Longitude = Longitude;
	}

}