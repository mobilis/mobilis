package de.treasurehunt.proxy;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class GetLocationRequest extends XMPPBean {

	public GetLocationRequest(){
		this.setType( XMPPBean.TYPE_GET );
	}


	@Override
	public void fromXML( XmlPullParser parser ) throws Exception {}

	public static final String CHILD_ELEMENT = "GetLocationRequest";

	@Override
	public String getChildElement() {
		return this.CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "treasurehunt:iq:getlocation";

	@Override
	public String getNamespace() {
		return this.NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		GetLocationRequest clone = new GetLocationRequest(  );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() { return ""; }


}