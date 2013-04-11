package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class JoinGameRequest extends XMPPBean {

	private String GamePassword = null;
	private String PlayerName = null;
	private boolean IsSpectator = false;


	public JoinGameRequest( String GamePassword, String PlayerName, boolean IsSpectator ) {
		super();
		this.GamePassword = GamePassword;
		this.PlayerName = PlayerName;
		this.IsSpectator = IsSpectator;

		this.setType( XMPPBean.TYPE_SET );
	}

	public JoinGameRequest(){
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
				else if (tagName.equals( "GamePassword" ) ) {
					this.GamePassword = parser.nextText();
				}
				else if (tagName.equals( "PlayerName" ) ) {
					this.PlayerName = parser.nextText();
				}
				else if (tagName.equals( "IsSpectator" ) ) {
					this.IsSpectator = Boolean.parseBoolean( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "JoinGameRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:joingame";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		JoinGameRequest clone = new JoinGameRequest( GamePassword, PlayerName, IsSpectator );
		this.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<GamePassword>" )
			.append( this.GamePassword )
			.append( "</GamePassword>" );

		sb.append( "<PlayerName>" )
			.append( this.PlayerName )
			.append( "</PlayerName>" );

		sb.append( "<IsSpectator>" )
			.append( this.IsSpectator )
			.append( "</IsSpectator>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public JoinGameRequest buildClosedGameFault(String detailedErrorText){
		JoinGameRequest fault = ( JoinGameRequest )this.clone();

		fault.setTo( this.getFrom() );
    	fault.setId(this.getId());
		fault.setType( XMPPBean.TYPE_ERROR );
		fault.errorType = "cancel";
		fault.errorCondition = "not-allowed";
		fault.errorText = "Maximum of Players Reached or Game is already running.";

		if(null != detailedErrorText && detailedErrorText.length() > 0)
			fault.errorText += " Detail: " + detailedErrorText;
		
		return fault;
	}





	public String getGamePassword() {
		return this.GamePassword;
	}

	public void setGamePassword( String GamePassword ) {
		this.GamePassword = GamePassword;
	}

	public String getPlayerName() {
		return this.PlayerName;
	}

	public void setPlayerName( String PlayerName ) {
		this.PlayerName = PlayerName;
	}

	public boolean getIsSpectator() {
		return this.IsSpectator;
	}

	public void setIsSpectator( boolean IsSpectator ) {
		this.IsSpectator = IsSpectator;
	}

}