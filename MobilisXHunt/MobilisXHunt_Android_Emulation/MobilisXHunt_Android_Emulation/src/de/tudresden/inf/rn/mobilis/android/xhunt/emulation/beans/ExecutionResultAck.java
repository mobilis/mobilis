package de.tudresden.inf.rn.mobilis.android.xhunt.emulation.beans;

import org.xmlpull.v1.XmlPullParser;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

import java.util.List;import java.util.ArrayList;

public class ExecutionResultAck extends XMPPBean {

	public ExecutionResultAck(){
		this.setType( XMPPBean.TYPE_RESULT );
	}


	public void fromXML( XmlPullParser parser ) throws Exception {}

	public static final String CHILD_ELEMENT = "ExecutionResultAck";

	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "emulation:iq:executionresult";

	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		ExecutionResultAck clone = new ExecutionResultAck(  );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() { return ""; }


}