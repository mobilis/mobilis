package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

public class TicketsAgents implements XMPPInfo {

	private List< TicketAmount > TicketsAgents = new ArrayList< TicketAmount >();


	public TicketsAgents( List< TicketAmount > TicketsAgents ) {
		super();
		for ( TicketAmount entity : TicketsAgents ) {
			this.TicketsAgents.add( entity );
		}
	}

	public TicketsAgents(){}



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
				else if (tagName.equals( TicketAmount.CHILD_ELEMENT ) ) {
					TicketAmount entity = new TicketAmount();

					entity.fromXML( parser );
					this.TicketsAgents.add( entity );
					
					parser.next();
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

	public static final String CHILD_ELEMENT = "TicketsAgents";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "http://mobilis.inf.tu-dresden.de#services/MobilisXHuntService#type:TicketsAgents";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder sb = new StringBuilder();

		for( TicketAmount entry : TicketsAgents ) {
			sb.append( "<" + TicketAmount.CHILD_ELEMENT + ">" );
			sb.append( entry.toXML() );
			sb.append( "</" + TicketAmount.CHILD_ELEMENT + ">" );
		}

		return sb.toString();
	}



	public List< TicketAmount > getTicketsAgents() {
		return this.TicketsAgents;
	}

	public void setTicketsAgents( List< TicketAmount > TicketsAgents ) {
		this.TicketsAgents = TicketsAgents;
	}

}