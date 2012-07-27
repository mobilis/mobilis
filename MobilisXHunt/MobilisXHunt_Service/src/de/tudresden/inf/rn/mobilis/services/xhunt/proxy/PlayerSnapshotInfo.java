package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

public class PlayerSnapshotInfo implements XMPPInfo {

	private PlayerInfo PlayerInfo = new PlayerInfo();
	private LocationInfo Location = new LocationInfo();
	private boolean IsTargetFinal = false;
	private int TargetId = Integer.MIN_VALUE;
	private boolean TargetReached = false;
	private int LastStationId = Integer.MIN_VALUE;


	public PlayerSnapshotInfo( PlayerInfo PlayerInfo, LocationInfo Location, boolean IsTargetFinal, int TargetId, boolean TargetReached, int LastStationId ) {
		super();
		this.PlayerInfo = PlayerInfo;
		this.Location = Location;
		this.IsTargetFinal = IsTargetFinal;
		this.TargetId = TargetId;
		this.TargetReached = TargetReached;
		this.LastStationId = LastStationId;
	}

	public PlayerSnapshotInfo(){}



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
				else if (tagName.equals( PlayerInfo.CHILD_ELEMENT ) ) {
					this.PlayerInfo.fromXML( parser );
				}
				else if (tagName.equals( LocationInfo.CHILD_ELEMENT ) ) {
					this.Location.fromXML( parser );
				}
				else if (tagName.equals( "IsTargetFinal" ) ) {
					this.IsTargetFinal = Boolean.parseBoolean( parser.nextText() );
				}
				else if (tagName.equals( "TargetId" ) ) {
					this.TargetId = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "TargetReached" ) ) {
					this.TargetReached = Boolean.parseBoolean( parser.nextText() );
				}
				else if (tagName.equals( "LastStationId" ) ) {
					this.LastStationId = Integer.parseInt( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "PlayerSnapshotInfo";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "http://mobilis.inf.tu-dresden.de#services/MobilisXHuntService#type:PlayerSnapshotInfo";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<" + this.PlayerInfo.getChildElement() + ">" )
			.append( this.PlayerInfo.toXML() )
			.append( "</" + this.PlayerInfo.getChildElement() + ">" );

		sb.append( "<" + this.Location.getChildElement() + ">" )
			.append( this.Location.toXML() )
			.append( "</" + this.Location.getChildElement() + ">" );

		sb.append( "<IsTargetFinal>" )
			.append( this.IsTargetFinal )
			.append( "</IsTargetFinal>" );

		sb.append( "<TargetId>" )
			.append( this.TargetId )
			.append( "</TargetId>" );

		sb.append( "<TargetReached>" )
			.append( this.TargetReached )
			.append( "</TargetReached>" );

		sb.append( "<LastStationId>" )
			.append( this.LastStationId )
			.append( "</LastStationId>" );

		return sb.toString();
	}



	public PlayerInfo getPlayerInfo() {
		return this.PlayerInfo;
	}

	public void setPlayerInfo( PlayerInfo PlayerInfo ) {
		this.PlayerInfo = PlayerInfo;
	}

	public LocationInfo getLocation() {
		return this.Location;
	}

	public void setLocation( LocationInfo Location ) {
		this.Location = Location;
	}

	public boolean getIsTargetFinal() {
		return this.IsTargetFinal;
	}

	public void setIsTargetFinal( boolean IsTargetFinal ) {
		this.IsTargetFinal = IsTargetFinal;
	}

	public int getTargetId() {
		return this.TargetId;
	}

	public void setTargetId( int TargetId ) {
		this.TargetId = TargetId;
	}

	public boolean getTargetReached() {
		return this.TargetReached;
	}

	public void setTargetReached( boolean TargetReached ) {
		this.TargetReached = TargetReached;
	}

	public int getLastStationId() {
		return this.LastStationId;
	}

	public void setLastStationId( int LastStationId ) {
		this.LastStationId = LastStationId;
	}

}