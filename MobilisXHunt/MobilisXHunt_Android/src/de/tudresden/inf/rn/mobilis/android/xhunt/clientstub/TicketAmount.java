package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

public class TicketAmount implements XMPPInfo {

	private int ID = Integer.MIN_VALUE;
	private int Amount = Integer.MIN_VALUE;


	public TicketAmount( int ID, int Amount ) {
		super();
		this.ID = ID;
		this.Amount = Amount;
	}

	public TicketAmount(){}



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
				else if (tagName.equals( "ID" ) ) {
					this.ID = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "Amount" ) ) {
					this.Amount = Integer.parseInt( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "TicketAmount";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "http://mobilis.inf.tu-dresden.de#services/MobilisXHuntService#type:TicketAmount";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<ID>" )
			.append( this.ID )
			.append( "</ID>" );

		sb.append( "<Amount>" )
			.append( this.Amount )
			.append( "</Amount>" );

		return sb.toString();
	}



	public int getID() {
		return this.ID;
	}

	public void setID( int ID ) {
		this.ID = ID;
	}

	public int getAmount() {
		return this.Amount;
	}

	public void setAmount( int Amount ) {
		this.Amount = Amount;
	}

}