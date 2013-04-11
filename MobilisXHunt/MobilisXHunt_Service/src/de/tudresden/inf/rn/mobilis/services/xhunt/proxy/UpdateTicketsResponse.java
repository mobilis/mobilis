package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class UpdateTicketsResponse extends XMPPBean {

	public UpdateTicketsResponse(){
		this.setType( XMPPBean.TYPE_RESULT );
	}


	@Override
	public void fromXML( XmlPullParser parser ) throws Exception {}

	public static final String CHILD_ELEMENT = "UpdateTicketsResponse";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:updatetickets";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		UpdateTicketsResponse clone = new UpdateTicketsResponse(  );
		this.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() { return ""; }


}