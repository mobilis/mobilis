package de.tudresden.inf.rn.mobilis.xmpp.beans.admin;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * 
 */
public class PingBean extends XMPPBean {

	private static final long serialVersionUID = 1L;
	public static final String NAMESPACE = Mobilis.NAMESPACE + "#XMPPBeans:general:Ping";
	public static final String CHILD_ELEMENT = "ping";
	
	public long RequestTime = Long.MIN_VALUE;
	public long ResponseTime = Long.MIN_VALUE;
	public String Message = null;
	
	
	public PingBean(long requestTime) {
		super();
		
		this.RequestTime = requestTime;
		this.type=XMPPBean.TYPE_GET;
	}
	
	public PingBean(long responseTime, String message) {
		super();
		
		this.ResponseTime = responseTime;
		this.Message = message;
		this.type=XMPPBean.TYPE_RESULT;
	}
		
	/** Constructor for type=ERROR */
	public PingBean(String errorType, String errorCondition, String errorText) {
		super(errorType, errorCondition, errorText);	
	}
	
	public PingBean() {
		super();
	}

	@Override
	public PingBean clone() {
		PingBean clone = this.RequestTime > Long.MIN_VALUE
				? new PingBean(this.RequestTime)
				: new PingBean(this.ResponseTime, this.Message);
		
		clone = (PingBean) cloneBasicAttributes(clone);
		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();
		
		if (this.RequestTime > Long.MIN_VALUE)
			sb.append("<requestTime>").append(this.RequestTime).append("</requestTime>");
		if (this.ResponseTime > Long.MIN_VALUE)
			sb.append("<responseTime>").append(this.ResponseTime).append("</responseTime>");
		if (null != this.Message)
			sb.append("<message>").append(this.Message).append("</message>");	
		
		sb = appendErrorPayload(sb);		
		return sb.toString();
	}

	@Override
	public void fromXML(XmlPullParser parser) throws Exception {
		String childElement = PingBean.CHILD_ELEMENT;
		
		boolean done = false;
		do {
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				if (tagName.equals(childElement)) {
					parser.next();
				} else if (tagName.equals("requestTime")) {
					this.RequestTime = Long.parseLong(parser.nextText());		
				} else if (tagName.equals("responseTime")) {
					this.ResponseTime = Long.parseLong(parser.nextText());	
				} else if (tagName.equals("message")) {
					this.Message = parser.nextText();
				} else if (tagName.equals("error")) {
					parser = parseErrorAttributes(parser);
				} else
					parser.next();
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals(childElement))
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

	@Override
	public String getChildElement() {
		return PingBean.CHILD_ELEMENT;
	}

	@Override
	public String getNamespace() {
		return PingBean.NAMESPACE;
	}
	
}
