package de.treasurehunt.proxy;
import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class GetLocationResponse extends XMPPBean {

	private Location PlayerLocation = new Location();


	public GetLocationResponse( Location PlayerLocation ) {
		super();
		this.PlayerLocation = PlayerLocation;

		this.setType( XMPPBean.TYPE_RESULT );
	}

	public GetLocationResponse(){
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
				else if (tagName.equals( Location.CHILD_ELEMENT ) ) {
					this.PlayerLocation.fromXML( parser );
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

	public static final String CHILD_ELEMENT = "GetLocationResponse";

	@Override
	public String getChildElement() {
		return this.CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "treasurehunt:iq:getlocation";

	@Override
	public String getNamespace() {
		return this.NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		GetLocationResponse clone = new GetLocationResponse( PlayerLocation );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<" + this.PlayerLocation.getChildElement() + ">" )
			.append( this.PlayerLocation.toXML() )
			.append( "</" + this.PlayerLocation.getChildElement() + ">" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public Location getPlayerLocation() {
		return this.PlayerLocation;
	}

	public void setPlayerLocation( Location PlayerLocation ) {
		this.PlayerLocation = PlayerLocation;
	}

}