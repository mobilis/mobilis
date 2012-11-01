package de.tudresden.inf.rn.mobilis.emulation.serverstubs;

import org.xmlpull.v1.XmlPullParser;import java.util.List;import java.util.ArrayList;

public class ConnectAck extends XMPPBean {

	public ConnectAck(){
		this.setType( XMPPBean.TYPE_RESULT );
	}


	@Override
	public void fromXML( XmlPullParser parser ) throws Exception {}

	public static final String CHILD_ELEMENT = "ConnectAck";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "emulation:iq:connect";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		ConnectAck clone = new ConnectAck(  );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() { return ""; }


}