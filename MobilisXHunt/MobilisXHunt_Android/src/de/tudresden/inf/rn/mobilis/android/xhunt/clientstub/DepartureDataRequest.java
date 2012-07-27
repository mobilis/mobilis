package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class DepartureDataRequest extends XMPPBean {

	private int StationId = Integer.MIN_VALUE;


	public DepartureDataRequest( int StationId ) {
		super();
		this.StationId = StationId;

		this.setType( XMPPBean.TYPE_GET );
	}

	public DepartureDataRequest(){
		this.setType( XMPPBean.TYPE_GET );
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
				else if (tagName.equals( "StationId" ) ) {
					this.StationId = Integer.parseInt( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "DepartureDataRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:departure";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		DepartureDataRequest clone = new DepartureDataRequest( StationId );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<StationId>" )
			.append( this.StationId )
			.append( "</StationId>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public int getStationId() {
		return this.StationId;
	}

	public void setStationId( int StationId ) {
		this.StationId = StationId;
	}

}