package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class PlayersRequest extends XMPPBean {

	private List< PlayerInfo > Players = new ArrayList< PlayerInfo >();
	private String Info = null;


	public PlayersRequest( List< PlayerInfo > Players, String Info ) {
		super();
		for ( PlayerInfo entity : Players ) {
			this.Players.add( entity );
		}
		this.Info = Info;

		this.setType( XMPPBean.TYPE_SET );
	}

	public PlayersRequest(){
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
					PlayerInfo entity = new PlayerInfo();

					entity.fromXML( parser );
					this.Players.add( entity );
					
					parser.next();
				}
				else if (tagName.equals( "Info" ) ) {
					this.Info = parser.nextText();
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

	public static final String CHILD_ELEMENT = "PlayersRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:players";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		PlayersRequest clone = new PlayersRequest( Players, Info );
		this.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		for( PlayerInfo entry : Players ) {
			sb.append( "<" + PlayerInfo.CHILD_ELEMENT + ">" );
			sb.append( entry.toXML() );
			sb.append( "</" + PlayerInfo.CHILD_ELEMENT + ">" );
		}

		sb.append( "<Info>" )
			.append( this.Info )
			.append( "</Info>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public List< PlayerInfo > getPlayers() {
		return this.Players;
	}

	public void setPlayers( List< PlayerInfo > Players ) {
		this.Players = Players;
	}

	public String getInfo() {
		return this.Info;
	}

	public void setInfo( String Info ) {
		this.Info = Info;
	}

}