package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

public class AreaInfo implements XMPPInfo {

	private int AreaId = Integer.MIN_VALUE;
	private String AreaName = null;
	private String AreaDescription = null;
	private int Version = Integer.MIN_VALUE;
	private List< Ticket > Tickets = new ArrayList< Ticket >();


	public AreaInfo( int AreaId, String AreaName, String AreaDescription, int Version, List< Ticket > Tickets ) {
		super();
		this.AreaId = AreaId;
		this.AreaName = AreaName;
		this.AreaDescription = AreaDescription;
		this.Version = Version;
		for ( Ticket entity : Tickets ) {
			this.Tickets.add( entity );
		}
	}

	public AreaInfo(){}



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
				else if (tagName.equals( "AreaId" ) ) {
					this.AreaId = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "AreaName" ) ) {
					this.AreaName = parser.nextText();
				}
				else if (tagName.equals( "AreaDescription" ) ) {
					this.AreaDescription = parser.nextText();
				}
				else if (tagName.equals( "Version" ) ) {
					this.Version = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( Ticket.CHILD_ELEMENT ) ) {
					Ticket entity = new Ticket();

					entity.fromXML( parser );
					this.Tickets.add( entity );
					
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

	public static final String CHILD_ELEMENT = "AreaInfo";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "http://mobilis.inf.tu-dresden.de#services/MobilisXHuntService#type:AreaInfo";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<AreaId>" )
			.append( this.AreaId )
			.append( "</AreaId>" );

		sb.append( "<AreaName>" )
			.append( this.AreaName )
			.append( "</AreaName>" );

		sb.append( "<AreaDescription>" )
			.append( this.AreaDescription )
			.append( "</AreaDescription>" );

		sb.append( "<Version>" )
			.append( this.Version )
			.append( "</Version>" );

		for( Ticket entry : this.Tickets ) {
			sb.append( "<" + Ticket.CHILD_ELEMENT + ">" );
			sb.append( entry.toXML() );
			sb.append( "</" + Ticket.CHILD_ELEMENT + ">" );
		}

		return sb.toString();
	}



	public int getAreaId() {
		return this.AreaId;
	}

	public void setAreaId( int AreaId ) {
		this.AreaId = AreaId;
	}

	public String getAreaName() {
		return this.AreaName;
	}

	public void setAreaName( String AreaName ) {
		this.AreaName = AreaName;
	}

	public String getAreaDescription() {
		return this.AreaDescription;
	}

	public void setAreaDescription( String AreaDescription ) {
		this.AreaDescription = AreaDescription;
	}

	public int getVersion() {
		return this.Version;
	}

	public void setVersion( int Version ) {
		this.Version = Version;
	}

	public List< Ticket > getTickets() {
		return this.Tickets;
	}

	public void setTickets( List< Ticket > Tickets ) {
		this.Tickets = Tickets;
	}

}