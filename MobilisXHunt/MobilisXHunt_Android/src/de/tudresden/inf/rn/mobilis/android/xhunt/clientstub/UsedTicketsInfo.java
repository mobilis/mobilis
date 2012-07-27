package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

public class UsedTicketsInfo implements XMPPInfo {

	private String Jid = null;
	private List< Integer > TicketIds = new ArrayList< Integer >();


	public UsedTicketsInfo( String Jid, List< Integer > TicketIds ) {
		super();
		this.Jid = Jid;
		for ( int entity : TicketIds ) {
			this.TicketIds.add( entity );
		}
	}

	public UsedTicketsInfo(){}



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
				else if (tagName.equals( "Jid" ) ) {
					this.Jid = parser.nextText();
				}
				else if (tagName.equals( "TicketIds" ) ) {
					TicketIds.add( Integer.parseInt( parser.nextText() ) );
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

	public static final String CHILD_ELEMENT = "UsedTicketsInfo";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "http://mobilis.inf.tu-dresden.de#services/MobilisXHuntService#type:UsedTicketsInfo";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<Jid>" )
			.append( this.Jid )
			.append( "</Jid>" );

		for( int entry : this.TicketIds ) {
			sb.append( "<TicketIds>" );
			sb.append( entry );
			sb.append( "</TicketIds>" );
		}

		return sb.toString();
	}



	public String getJid() {
		return this.Jid;
	}

	public void setJid( String Jid ) {
		this.Jid = Jid;
	}

	public List< Integer > getTicketIds() {
		return this.TicketIds;
	}

	public void setTicketIds( List< Integer > TicketIds ) {
		this.TicketIds = TicketIds;
	}

}