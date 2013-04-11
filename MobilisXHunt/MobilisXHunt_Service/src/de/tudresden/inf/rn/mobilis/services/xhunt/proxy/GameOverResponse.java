package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class GameOverResponse extends XMPPBean {

	public GameOverResponse(){
		this.setType( XMPPBean.TYPE_RESULT );
	}


	@Override
	public void fromXML( XmlPullParser parser ) throws Exception {}

	public static final String CHILD_ELEMENT = "GameOverResponse";

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
		GameOverResponse clone = new GameOverResponse(  );
		this.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() { return ""; }


}