package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

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
		clone.cloneBasicAttributes( clone );

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


	public PlayerInfo getPlayerInfo() {
		return this.PlayerInfo;
	}

	public void setPlayerInfo( PlayerInfo PlayerInfo ) {
		this.PlayerInfo = PlayerInfo;
	}

}