package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class LocationRequest extends XMPPBean {

	private List< LocationInfo > LocationInfos = new ArrayList< LocationInfo >();
	private boolean MrXOnline = false;


	public LocationRequest( List< LocationInfo > LocationInfos, boolean MrXOnline ) {
		super();
		for ( LocationInfo entity : LocationInfos ) {
			this.LocationInfos.add( entity );
		}
		this.MrXOnline = MrXOnline;

		this.setType( XMPPBean.TYPE_SET );
	}

	public LocationRequest(){
		this.setType( XMPPBean.TYPE_SET );
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
					LocationInfo entity = new LocationInfo();

					entity.fromXML( parser );
					this.LocationInfos.add( entity );
					
					parser.next();
				}
				else if (tagName.equals( "MrXOnline" ) ) {
					this.MrXOnline = Boolean.parseBoolean( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "LocationRequest";

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
		LocationRequest clone = new LocationRequest( LocationInfos, MrXOnline );
		this.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		for( LocationInfo entry : LocationInfos ) {
			sb.append( "<" + LocationInfo.CHILD_ELEMENT + ">" );
			sb.append( entry.toXML() );
			sb.append( "</" + LocationInfo.CHILD_ELEMENT + ">" );
		}

		sb.append( "<MrXOnline>" )
			.append( this.MrXOnline )
			.append( "</MrXOnline>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public List< LocationInfo > getLocationInfos() {
		return this.LocationInfos;
	}

	public void setLocationInfos( List< LocationInfo > LocationInfos ) {
		this.LocationInfos = LocationInfos;
	}

	public boolean getMrXOnline() {
		return this.MrXOnline;
	}

	public void setMrXOnline( boolean MrXOnline ) {
		this.MrXOnline = MrXOnline;
	}

}