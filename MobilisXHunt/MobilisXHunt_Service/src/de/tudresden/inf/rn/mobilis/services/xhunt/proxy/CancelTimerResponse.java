package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class CancelTimerResponse extends XMPPBean {

	public CancelTimerResponse(){
		this.setType( XMPPBean.TYPE_RESULT );
	}


	@Override
	public void fromXML( XmlPullParser parser ) throws Exception {}

	public static final String CHILD_ELEMENT = "CancelTimerResponse";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:cancelstarttimer";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		CancelTimerResponse clone = new CancelTimerResponse(  );
		this.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() { return ""; }


}