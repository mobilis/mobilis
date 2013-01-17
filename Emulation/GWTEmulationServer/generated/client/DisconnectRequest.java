package de.tudresden.inf.rn.mobilis.emulation.clientstub;

import org.xmlpull.v1.XmlPullParser;import java.util.List;import java.util.ArrayList;

public class DisconnectRequest extends XMPPBean {

	public DisconnectRequest(){
		this.setType( XMPPBean.TYPE_GET );
	}


	@Override
	public void fromXML( XmlPullParser parser ) throws Exception {}

	public static final String CHILD_ELEMENT = "DisconnectRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "emulation:iq:disconnect";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		DisconnectRequest clone = new DisconnectRequest(  );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() { return ""; }


}