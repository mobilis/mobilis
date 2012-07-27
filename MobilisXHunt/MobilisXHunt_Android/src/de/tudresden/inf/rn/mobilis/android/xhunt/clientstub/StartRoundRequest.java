package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class StartRoundRequest extends XMPPBean {

	private int Round = Integer.MIN_VALUE;
	private boolean ShowMrX = false;
	private List< TicketAmount > Tickets = new ArrayList< TicketAmount >();


	public StartRoundRequest( int Round, boolean ShowMrX, List< TicketAmount > Tickets ) {
		super();
		this.Round = Round;
		this.ShowMrX = ShowMrX;
		for ( TicketAmount entity : Tickets ) {
			this.Tickets.add( entity );
		}

		this.setType( XMPPBean.TYPE_SET );
	}

	public StartRoundRequest(){
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
				else if (tagName.equals( "Round" ) ) {
					this.Round = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "ShowMrX" ) ) {
					this.ShowMrX = Boolean.parseBoolean( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "StartRoundRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:startround";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		StartRoundRequest clone = new StartRoundRequest( Round, ShowMrX, Tickets );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<Round>" )
			.append( this.Round )
			.append( "</Round>" );

		sb.append( "<ShowMrX>" )
			.append( this.ShowMrX )
			.append( "</ShowMrX>" );

		for( TicketAmount entry : this.Tickets ) {
			sb.append( "<" + TicketAmount.CHILD_ELEMENT + ">" );
			sb.append( entry.toXML() );
			sb.append( "</" + TicketAmount.CHILD_ELEMENT + ">" );
		}

		sb = appendErrorPayload(sb);

		return sb.toString();
	}





	public int getRound() {
		return this.Round;
	}

	public void setRound( int Round ) {
		this.Round = Round;
	}

	public boolean getShowMrX() {
		return this.ShowMrX;
	}

	public void setShowMrX( boolean ShowMrX ) {
		this.ShowMrX = ShowMrX;
	}

	public List< TicketAmount > getTickets() {
		return this.Tickets;
	}

	public void setTickets( List< TicketAmount > Tickets ) {
		this.Tickets = Tickets;
	}

}