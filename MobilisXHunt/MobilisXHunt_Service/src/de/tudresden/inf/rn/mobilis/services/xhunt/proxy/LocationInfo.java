package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

public class LocationInfo implements XMPPInfo {

	private String Jid = null;
	private int Latitude = Integer.MIN_VALUE;
	private int Longitude = Integer.MIN_VALUE;
	private boolean PlayerOnline = false;


	public LocationInfo( String Jid, int Latitude, int Longitude, boolean PlayerOnline ) {
		super();
		this.Jid = Jid;
		this.Latitude = Latitude;
		this.Longitude = Longitude;
		this.PlayerOnline = PlayerOnline;
	}

	public LocationInfo(){}



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
				else if (tagName.equals( "Jid" ) ) {
					this.Jid = parser.nextText();
				}
				else if (tagName.equals( "Latitude" ) ) {
					this.Latitude = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "Longitude" ) ) {
					this.Longitude = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "PlayerOnline" ) ) {
					this.PlayerOnline = Boolean.parseBoolean( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "LocationInfo";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "http://mobilis.inf.tu-dresden.de#services/MobilisXHuntService#type:LocationInfo";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<Jid>" )
			.append( this.Jid )
			.append( "</Jid>" );

		sb.append( "<Latitude>" )
			.append( this.Latitude )
			.append( "</Latitude>" );

		sb.append( "<Longitude>" )
			.append( this.Longitude )
			.append( "</Longitude>" );

		sb.append( "<PlayerOnline>" )
			.append( this.PlayerOnline )
			.append( "</PlayerOnline>" );

		return sb.toString();
	}



	public String getJid() {
		return this.Jid;
	}

	public void setJid( String Jid ) {
		this.Jid = Jid;
	}

	public int getLatitude() {
		return this.Latitude;
	}

	public void setLatitude( int Latitude ) {
		this.Latitude = Latitude;
	}

	public int getLongitude() {
		return this.Longitude;
	}

	public void setLongitude( int Longitude ) {
		this.Longitude = Longitude;
	}

	public boolean getPlayerOnline() {
		return this.PlayerOnline;
	}

	public void setPlayerOnline( boolean PlayerOnline ) {
		this.PlayerOnline = PlayerOnline;
	}

}