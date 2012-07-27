package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

public class RoundStatusInfo implements XMPPInfo {

	private String PlayerJid = null;
	private boolean IsTargetFinal = false;
	private int TargetId = Integer.MIN_VALUE;
	private boolean TargetReached = false;


	public RoundStatusInfo( String PlayerJid, boolean IsTargetFinal, int TargetId, boolean TargetReached ) {
		super();
		this.PlayerJid = PlayerJid;
		this.IsTargetFinal = IsTargetFinal;
		this.TargetId = TargetId;
		this.TargetReached = TargetReached;
	}

	public RoundStatusInfo(){}



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
				else if (tagName.equals( "PlayerJid" ) ) {
					this.PlayerJid = parser.nextText();
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

	public static final String CHILD_ELEMENT = "RoundStatusInfo";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "http://mobilis.inf.tu-dresden.de#services/MobilisXHuntService#type:RoundStatusInfo";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<PlayerJid>" )
			.append( this.PlayerJid )
			.append( "</PlayerJid>" );

		sb.append( "<IsTargetFinal>" )
			.append( this.IsTargetFinal )
			.append( "</IsTargetFinal>" );

		sb.append( "<TargetId>" )
			.append( this.TargetId )
			.append( "</TargetId>" );

		sb.append( "<TargetReached>" )
			.append( this.TargetReached )
			.append( "</TargetReached>" );

		return sb.toString();
	}



	public String getPlayerJid() {
		return this.PlayerJid;
	}

	public void setPlayerJid( String PlayerJid ) {
		this.PlayerJid = PlayerJid;
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

}