package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class GameDetailsResponse extends XMPPBean {

	private String GameName = null;
	private boolean RequirePassword = false;
	private int CountRounds = Integer.MIN_VALUE;
	private int StartTimer = Integer.MIN_VALUE;
	private List< String > PlayerNames = new ArrayList< String >();
	private boolean IsOpen = false;


	public GameDetailsResponse( String GameName, boolean RequirePassword, int CountRounds, int StartTimer, List< String > PlayerNames, boolean IsOpen ) {
		super();
		this.GameName = GameName;
		this.RequirePassword = RequirePassword;
		this.CountRounds = CountRounds;
		this.StartTimer = StartTimer;
		for ( String entity : PlayerNames ) {
			this.PlayerNames.add( entity );
		}
		this.IsOpen = IsOpen;

		this.setType( XMPPBean.TYPE_RESULT );
	}

	public GameDetailsResponse(){
		this.setType( XMPPBean.TYPE_RESULT );
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
				else if (tagName.equals( "RequirePassword" ) ) {
					this.RequirePassword = Boolean.parseBoolean( parser.nextText() );
				}
				else if (tagName.equals( "CountRounds" ) ) {
					this.CountRounds = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "StartTimer" ) ) {
					this.StartTimer = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "PlayerNames" ) ) {
					PlayerNames.add( parser.nextText() );
				}
				else if (tagName.equals( "IsOpen" ) ) {
					this.IsOpen = Boolean.parseBoolean( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "GameDetailsResponse";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:gamedetails";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		GameDetailsResponse clone = new GameDetailsResponse( GameName, RequirePassword, CountRounds, StartTimer, PlayerNames, IsOpen );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<GameName>" )
			.append( this.GameName )
			.append( "</GameName>" );

		sb.append( "<RequirePassword>" )
			.append( this.RequirePassword )
			.append( "</RequirePassword>" );

		sb.append( "<CountRounds>" )
			.append( this.CountRounds )
			.append( "</CountRounds>" );

		sb.append( "<StartTimer>" )
			.append( this.StartTimer )
			.append( "</StartTimer>" );

		for( String entry : this.PlayerNames ) {
			sb.append( "<PlayerNames>" );
			sb.append( entry );
			sb.append( "</PlayerNames>" );
		}

		sb.append( "<IsOpen>" )
			.append( this.IsOpen )
			.append( "</IsOpen>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public String getGameName() {
		return this.GameName;
	}

	public void setGameName( String GameName ) {
		this.GameName = GameName;
	}

	public boolean getRequirePassword() {
		return this.RequirePassword;
	}

	public void setRequirePassword( boolean RequirePassword ) {
		this.RequirePassword = RequirePassword;
	}

	public int getCountRounds() {
		return this.CountRounds;
	}

	public void setCountRounds( int CountRounds ) {
		this.CountRounds = CountRounds;
	}

	public int getStartTimer() {
		return this.StartTimer;
	}

	public void setStartTimer( int StartTimer ) {
		this.StartTimer = StartTimer;
	}

	public List< String > getPlayerNames() {
		return this.PlayerNames;
	}

	public void setPlayerNames( List< String > PlayerNames ) {
		this.PlayerNames = PlayerNames;
	}

	public boolean getIsOpen() {
		return this.IsOpen;
	}

	public void setIsOpen( boolean IsOpen ) {
		this.IsOpen = IsOpen;
	}

}