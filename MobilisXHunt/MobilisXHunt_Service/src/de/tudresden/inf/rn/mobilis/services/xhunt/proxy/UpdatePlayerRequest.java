package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class UpdatePlayerRequest extends XMPPBean {

	private PlayerInfo PlayerInfo = new PlayerInfo();


	public UpdatePlayerRequest( PlayerInfo PlayerInfo ) {
		super();
		this.PlayerInfo = PlayerInfo;

		this.setType( XMPPBean.TYPE_SET );
	}

	public UpdatePlayerRequest(){
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
				else if (tagName.equals( PlayerInfo.CHILD_ELEMENT ) ) {
					this.PlayerInfo.fromXML( parser );
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

	public static final String CHILD_ELEMENT = "UpdatePlayerRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:updateplayer";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		UpdatePlayerRequest clone = new UpdatePlayerRequest( PlayerInfo );
		this.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<" + this.PlayerInfo.getChildElement() + ">" )
			.append( this.PlayerInfo.toXML() )
			.append( "</" + this.PlayerInfo.getChildElement() + ">" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public UpdatePlayerRequest buildPermissionFault(String detailedErrorText){
		UpdatePlayerRequest fault = ( UpdatePlayerRequest )this.clone();

		fault.setTo( this.getFrom() );
    	fault.setId(this.getId());
		fault.setType( XMPPBean.TYPE_ERROR );
		fault.errorType = "cancel";
		fault.errorCondition = "not-allowed";
		fault.errorText = "You do not have the permission for this action.";

		if(null != detailedErrorText && detailedErrorText.length() > 0)
			fault.errorText += " Detail: " + detailedErrorText;
		
		return fault;
	}


	public UpdatePlayerRequest buildInputDataFault(String detailedErrorText){
		UpdatePlayerRequest fault = ( UpdatePlayerRequest )this.clone();

		fault.setTo( this.getFrom() );
    	fault.setId(this.getId());
		fault.setType( XMPPBean.TYPE_ERROR );
		fault.errorType = "modify";
		fault.errorCondition = "not-acceptable";
		fault.errorText = "Unaccepted data input.";

		if(null != detailedErrorText && detailedErrorText.length() > 0)
			fault.errorText += " Detail: " + detailedErrorText;
		
		return fault;
	}





	public PlayerInfo getPlayerInfo() {
		return this.PlayerInfo;
	}

	public void setPlayerInfo( PlayerInfo PlayerInfo ) {
		this.PlayerInfo = PlayerInfo;
	}

}