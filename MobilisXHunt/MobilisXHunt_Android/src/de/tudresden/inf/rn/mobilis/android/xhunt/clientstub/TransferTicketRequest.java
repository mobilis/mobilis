package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class TransferTicketRequest extends XMPPBean {

	private String FromPlayerJid = null;
	private String ToPlayerJid = null;
	private TicketAmount Ticket = new TicketAmount();


	public TransferTicketRequest( String FromPlayerJid, String ToPlayerJid, TicketAmount Ticket ) {
		super();
		this.FromPlayerJid = FromPlayerJid;
		this.ToPlayerJid = ToPlayerJid;
		this.Ticket = Ticket;

		this.setType( XMPPBean.TYPE_SET );
	}

	public TransferTicketRequest(){
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
				else if (tagName.equals( "FromPlayerJid" ) ) {
					this.FromPlayerJid = parser.nextText();
				}
				else if (tagName.equals( "ToPlayerJid" ) ) {
					this.ToPlayerJid = parser.nextText();
				}
				else if (tagName.equals( TicketAmount.CHILD_ELEMENT ) ) {
					this.Ticket.fromXML( parser );
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

	public static final String CHILD_ELEMENT = "TransferTicketRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:transferticket";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		TransferTicketRequest clone = new TransferTicketRequest( FromPlayerJid, ToPlayerJid, Ticket );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<FromPlayerJid>" )
			.append( this.FromPlayerJid )
			.append( "</FromPlayerJid>" );

		sb.append( "<ToPlayerJid>" )
			.append( this.ToPlayerJid )
			.append( "</ToPlayerJid>" );

		sb.append( "<" + this.Ticket.getChildElement() + ">" )
			.append( this.Ticket.toXML() )
			.append( "</" + this.Ticket.getChildElement() + ">" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public String getFromPlayerJid() {
		return this.FromPlayerJid;
	}

	public void setFromPlayerJid( String FromPlayerJid ) {
		this.FromPlayerJid = FromPlayerJid;
	}

	public String getToPlayerJid() {
		return this.ToPlayerJid;
	}

	public void setToPlayerJid( String ToPlayerJid ) {
		this.ToPlayerJid = ToPlayerJid;
	}

	public TicketAmount getTicket() {
		return this.Ticket;
	}

	public void setTicket( TicketAmount Ticket ) {
		this.Ticket = Ticket;
	}

}