package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class DepartureDataResponse extends XMPPBean {

	private List< DepartureInfo > Departures = new ArrayList< DepartureInfo >();


	public DepartureDataResponse( List< DepartureInfo > Departures ) {
		super();
		for ( DepartureInfo entity : Departures ) {
			this.Departures.add( entity );
		}

		this.setType( XMPPBean.TYPE_RESULT );
	}

	public DepartureDataResponse(){
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
				else if (tagName.equals( DepartureInfo.CHILD_ELEMENT ) ) {
					DepartureInfo entity = new DepartureInfo();

					entity.fromXML( parser );
					this.Departures.add( entity );
					
					parser.next();
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

	public static final String CHILD_ELEMENT = "DepartureDataResponse";

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
		DepartureDataResponse clone = new DepartureDataResponse( Departures );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		for( DepartureInfo entry : this.Departures ) {
			sb.append( "<" + DepartureInfo.CHILD_ELEMENT + ">" );
			sb.append( entry.toXML() );
			sb.append( "</" + DepartureInfo.CHILD_ELEMENT + ">" );
		}

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public List< DepartureInfo > getDepartures() {
		return this.Departures;
	}

	public void setDepartures( List< DepartureInfo > Departures ) {
		this.Departures = Departures;
	}

}