package de.tudresden.inf.rn.mobilis.android.xhunt.emulation.clientstub;

import org.xmlpull.v1.XmlPullParser;import java.util.List;import java.util.ArrayList;

public class LogRequest extends XMPPBean {

	public LogRequest(){
		this.setType( XMPPBean.TYPE_GET );
	}


	@Override
	public void fromXML( XmlPullParser parser ) throws Exception {}

	public static final String CHILD_ELEMENT = "LogRequest";

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
		LogRequest clone = new LogRequest(  );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() { return ""; }






}