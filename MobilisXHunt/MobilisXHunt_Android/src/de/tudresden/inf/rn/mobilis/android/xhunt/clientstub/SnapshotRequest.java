package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class SnapshotRequest extends XMPPBean {

	private String GameName = null;
	private int Round = Integer.MIN_VALUE;
	private boolean IsRoundStart = false;
	private boolean ShowMrX = false;
	private int StartTimer = Integer.MIN_VALUE;
	private List< TicketAmount > Tickets = new ArrayList< TicketAmount >();
	private List< PlayerSnapshotInfo > PlayerSnapshots = new ArrayList< PlayerSnapshotInfo >();


	public SnapshotRequest( String GameName, int Round, boolean IsRoundStart, boolean ShowMrX, int StartTimer, List< TicketAmount > Tickets, List< PlayerSnapshotInfo > PlayerSnapshots ) {
		super();
		this.GameName = GameName;
		this.Round = Round;
		this.IsRoundStart = IsRoundStart;
		this.ShowMrX = ShowMrX;
		this.StartTimer = StartTimer;
		for ( TicketAmount entity : Tickets ) {
			this.Tickets.add( entity );
		}
		for ( PlayerSnapshotInfo entity : PlayerSnapshots ) {
			this.PlayerSnapshots.add( entity );
		}

		this.setType( XMPPBean.TYPE_SET );
	}

	public SnapshotRequest(){
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
				else if (tagName.equals( "GameName" ) ) {
					this.GameName = parser.nextText();
				}
				else if (tagName.equals( "Round" ) ) {
					this.Round = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "IsRoundStart" ) ) {
					this.IsRoundStart = Boolean.parseBoolean( parser.nextText() );
				}
				else if (tagName.equals( "ShowMrX" ) ) {
					this.ShowMrX = Boolean.parseBoolean( parser.nextText() );
				}
				else if (tagName.equals( "StartTimer" ) ) {
					this.StartTimer = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( TicketAmount.CHILD_ELEMENT ) ) {
					TicketAmount entity = new TicketAmount();

					entity.fromXML( parser );
					this.Tickets.add( entity );
					
					parser.next();
				}
				else if (tagName.equals( PlayerSnapshotInfo.CHILD_ELEMENT ) ) {
					PlayerSnapshotInfo entity = new PlayerSnapshotInfo();

					entity.fromXML( parser );
					this.PlayerSnapshots.add( entity );
					
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

	public static final String CHILD_ELEMENT = "SnapshotRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:snapshot";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		SnapshotRequest clone = new SnapshotRequest( GameName, Round, IsRoundStart, ShowMrX, StartTimer, Tickets, PlayerSnapshots );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<GameName>" )
			.append( this.GameName )
			.append( "</GameName>" );

		sb.append( "<Round>" )
			.append( this.Round )
			.append( "</Round>" );

		sb.append( "<IsRoundStart>" )
			.append( this.IsRoundStart )
			.append( "</IsRoundStart>" );

		sb.append( "<ShowMrX>" )
			.append( this.ShowMrX )
			.append( "</ShowMrX>" );

		sb.append( "<StartTimer>" )
			.append( this.StartTimer )
			.append( "</StartTimer>" );

		for( TicketAmount entry : this.Tickets ) {
			sb.append( "<" + TicketAmount.CHILD_ELEMENT + ">" );
			sb.append( entry.toXML() );
			sb.append( "</" + TicketAmount.CHILD_ELEMENT + ">" );
		}

		for( PlayerSnapshotInfo entry : this.PlayerSnapshots ) {
			sb.append( "<" + PlayerSnapshotInfo.CHILD_ELEMENT + ">" );
			sb.append( entry.toXML() );
			sb.append( "</" + PlayerSnapshotInfo.CHILD_ELEMENT + ">" );
		}

		sb = appendErrorPayload(sb);

		return sb.toString();
	}





	public String getGameName() {
		return this.GameName;
	}

	public void setGameName( String GameName ) {
		this.GameName = GameName;
	}

	public int getRound() {
		return this.Round;
	}

	public void setRound( int Round ) {
		this.Round = Round;
	}

	public boolean getIsRoundStart() {
		return this.IsRoundStart;
	}

	public void setIsRoundStart( boolean IsRoundStart ) {
		this.IsRoundStart = IsRoundStart;
	}

	public boolean getShowMrX() {
		return this.ShowMrX;
	}

	public void setShowMrX( boolean ShowMrX ) {
		this.ShowMrX = ShowMrX;
	}

	public int getStartTimer() {
		return this.StartTimer;
	}

	public void setStartTimer( int StartTimer ) {
		this.StartTimer = StartTimer;
	}

	public List< TicketAmount > getTickets() {
		return this.Tickets;
	}

	public void setTickets( List< TicketAmount > Tickets ) {
		this.Tickets = Tickets;
	}

	public List< PlayerSnapshotInfo > getPlayerSnapshots() {
		return this.PlayerSnapshots;
	}

	public void setPlayerSnapshots( List< PlayerSnapshotInfo > PlayerSnapshots ) {
		this.PlayerSnapshots = PlayerSnapshots;
	}

}