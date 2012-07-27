package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class UpdateTicketsRequest extends XMPPBean {

	private List< TicketAmount > Tickets = new ArrayList< TicketAmount >();


	public UpdateTicketsRequest( List< TicketAmount > Tickets ) {
		super();
		for ( TicketAmount entity : Tickets ) {
			this.Tickets.add( entity );
		}

		this.setType( XMPPBean.TYPE_SET );
	}

	public UpdateTicketsRequest(){
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
				else if (tagName.equals( TicketAmount.CHILD_ELEMENT ) ) {
					TicketAmount entity = new TicketAmount();

					entity.fromXML( parser );
					this.Tickets.add( entity );
					
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

	public static final String CHILD_ELEMENT = "UpdateTicketsRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:updatetickets";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		UpdateTicketsRequest clone = new UpdateTicketsRequest( Tickets );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		for( TicketAmount entry : this.Tickets ) {
			sb.append( "<" + TicketAmount.CHILD_ELEMENT + ">" );
			sb.append( entry.toXML() );
			sb.append( "</" + TicketAmount.CHILD_ELEMENT + ">" );
		}

		sb = appendErrorPayload(sb);

		return sb.toString();
	}





	public List< TicketAmount > getTickets() {
		return this.Tickets;
	}

	public void setTickets( List< TicketAmount > Tickets ) {
		this.Tickets = Tickets;
	}

}