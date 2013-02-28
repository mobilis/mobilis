package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

public class PlayerInfo implements XMPPInfo {

	private String Jid = null;
	private String PlayerName = null;
	private boolean IsModerator = false;
	private boolean IsMrX = false;
	private boolean IsReady = false;
	private int IconColorID = Integer.MIN_VALUE;


	public PlayerInfo( String Jid, String PlayerName, boolean IsModerator, boolean IsMrX, boolean IsReady, int IconColorID ) {
		super();
		this.Jid = Jid;
		this.PlayerName = PlayerName;
		this.IsModerator = IsModerator;
		this.IsMrX = IsMrX;
		this.IsReady = IsReady;
		this.IconColorID = IconColorID;
	}

	public PlayerInfo(){}



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
				else if (tagName.equals( "PlayerName" ) ) {
					this.PlayerName = parser.nextText();
				}
				else if (tagName.equals( "IsModerator" ) ) {
					this.IsModerator = Boolean.parseBoolean( parser.nextText() );
				}
				else if (tagName.equals( "IsMrX" ) ) {
					this.IsMrX = Boolean.parseBoolean( parser.nextText() );
				}
				else if (tagName.equals( "IsReady" ) ) {
					this.IsReady = Boolean.parseBoolean( parser.nextText() );
				}
				else if (tagName.equals( "IconColorID" ) ) {
					this.IconColorID = Integer.parseInt( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "PlayerInfo";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "http://mobilis.inf.tu-dresden.de#services/MobilisXHuntService#type:PlayerInfo";

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

		sb.append( "<PlayerName>" )
			.append( this.PlayerName )
			.append( "</PlayerName>" );

		sb.append( "<IsModerator>" )
			.append( this.IsModerator )
			.append( "</IsModerator>" );

		sb.append( "<IsMrX>" )
			.append( this.IsMrX )
			.append( "</IsMrX>" );

		sb.append( "<IsReady>" )
			.append( this.IsReady )
			.append( "</IsReady>" );

		sb.append( "<IconColorID>" )
			.append( this.IconColorID )
			.append( "</IconColorID>" );

		return sb.toString();
	}



	public String getJid() {
		return this.Jid;
	}

	public void setJid( String Jid ) {
		this.Jid = Jid;
	}

	public String getPlayerName() {
		return this.PlayerName;
	}

	public void setPlayerName( String PlayerName ) {
		this.PlayerName = PlayerName;
	}

	public boolean getIsModerator() {
		return this.IsModerator;
	}

	public void setIsModerator( boolean IsModerator ) {
		this.IsModerator = IsModerator;
	}

	public boolean getIsMrX() {
		return this.IsMrX;
	}

	public void setIsMrX( boolean IsMrX ) {
		this.IsMrX = IsMrX;
	}

	public boolean getIsReady() {
		return this.IsReady;
	}

	public void setIsReady( boolean IsReady ) {
		this.IsReady = IsReady;
	}

	public int getIconColorID() {
		return this.IconColorID;
	}

	public void setIconColorID( int IconColorID ) {
		this.IconColorID = IconColorID;
	}

}