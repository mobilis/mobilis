package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class UsedTicketsResponse extends XMPPBean {

	private List< UsedTicketsInfo > UsedTickets = new ArrayList< UsedTicketsInfo >();


	public UsedTicketsResponse( List< UsedTicketsInfo > UsedTickets ) {
		super();
		for ( UsedTicketsInfo entity : UsedTickets ) {
			this.UsedTickets.add( entity );
		}

		this.setType( XMPPBean.TYPE_RESULT );
	}

	public UsedTicketsResponse(){
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
				else if (tagName.equals( UsedTicketsInfo.CHILD_ELEMENT ) ) {
					UsedTicketsInfo entity = new UsedTicketsInfo();

					entity.fromXML( parser );
					this.UsedTickets.add( entity );
					
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

	public static final String CHILD_ELEMENT = "UsedTicketsResponse";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:usedtickets";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		UsedTicketsResponse clone = new UsedTicketsResponse( UsedTickets );
		this.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		for( UsedTicketsInfo entry : UsedTickets ) {
			sb.append( "<" + UsedTicketsInfo.CHILD_ELEMENT + ">" );
			sb.append( entry.toXML() );
			sb.append( "</" + UsedTicketsInfo.CHILD_ELEMENT + ">" );
		}

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public List< UsedTicketsInfo > getUsedTickets() {
		return this.UsedTickets;
	}

	public void setUsedTickets( List< UsedTicketsInfo > UsedTickets ) {
		this.UsedTickets = UsedTickets;
	}

}