package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class PlayerExitRequest extends XMPPBean {

	private String Jid = null;
	private boolean IsSpectator = false;


	public PlayerExitRequest( String Jid, boolean IsSpectator ) {
		super();
		this.Jid = Jid;
		this.IsSpectator = IsSpectator;

		this.setType( XMPPBean.TYPE_SET );
	}

	public PlayerExitRequest(){
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
				else if (tagName.equals( "Jid" ) ) {
					this.Jid = parser.nextText();
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

	public static final String CHILD_ELEMENT = "PlayerExitRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:playerexit";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		PlayerExitRequest clone = new PlayerExitRequest( Jid, IsSpectator );
		this.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<Jid>" )
			.append( this.Jid )
			.append( "</Jid>" );

		sb.append( "<IsSpectator>" )
			.append( this.IsSpectator )
			.append( "</IsSpectator>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public PlayerExitRequest buildPermissionFault(String detailedErrorText){
		PlayerExitRequest fault = ( PlayerExitRequest )this.clone();

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





	public String getJid() {
		return this.Jid;
	}

	public void setJid( String Jid ) {
		this.Jid = Jid;
	}

	public boolean getIsSpectator() {
		return this.IsSpectator;
	}

	public void setIsSpectator( boolean IsSpectator ) {
		this.IsSpectator = IsSpectator;
	}

}