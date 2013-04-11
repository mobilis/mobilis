package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class GameOverRequest extends XMPPBean {

	private String Reason = null;


	public GameOverRequest( String Reason ) {
		super();
		this.Reason = Reason;

		this.setType( XMPPBean.TYPE_SET );
	}

	public GameOverRequest(){
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
				else if (tagName.equals( "Reason" ) ) {
					this.Reason = parser.nextText();
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

	public static final String CHILD_ELEMENT = "GameOverRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:gameover";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		GameOverRequest clone = new GameOverRequest( Reason );
		this.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<Reason>" )
			.append( this.Reason )
			.append( "</Reason>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public String getReason() {
		return this.Reason;
	}

	public void setReason( String Reason ) {
		this.Reason = Reason;
	}

}