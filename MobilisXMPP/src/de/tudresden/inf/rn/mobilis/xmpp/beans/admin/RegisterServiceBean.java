package de.tudresden.inf.rn.mobilis.xmpp.beans.admin;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class RegisterServiceBean extends XMPPBean {

	private static final long serialVersionUID = 1L;
	public static final String NAMESPACE = Mobilis.NAMESPACE + "#XMPPBeans:admin:RegisterService";
	public static final String CHILD_ELEMENT = "register";
	
	// SET
	public String ServiceNamespace = null;
	public int ServiceVersion = -1;
	
	// RESULT
	public boolean RegistrationSuccessful = false;
	
	
	private String _xmlTag_RegistrationSuccessful = "success";
	private String _xmlTag_ServiceNamespace = "servicenamespace";
	private String _xmlTag_ServiceVersion = "serviceversion";
		
	
	public RegisterServiceBean() {
		super();
	}
	
	public RegisterServiceBean(String serviceNamespace, int serviceVersion) {
		super();
		
		this.ServiceNamespace = serviceNamespace;
		this.ServiceVersion = serviceVersion;
		
		this.type = XMPPBean.TYPE_SET;
	}
	
	public RegisterServiceBean(boolean registrationSuccessful) {
		super();
		
		this.RegistrationSuccessful = registrationSuccessful;
		this.type = XMPPBean.TYPE_RESULT;
	}
		
	/** Constructor for type=ERROR */
	public RegisterServiceBean(String errorType, String errorCondition, String errorText) {
		super(errorType, errorCondition, errorText);	
	}
	

	@Override
	public RegisterServiceBean clone() {
		RegisterServiceBean clone = new RegisterServiceBean(this.ServiceNamespace, this.ServiceVersion);
		clone.RegistrationSuccessful = this.RegistrationSuccessful;
		
		clone = (RegisterServiceBean) cloneBasicAttributes(clone);
		
		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();
		
		if(null != this.ServiceNamespace){
			sb.append("<" + _xmlTag_ServiceNamespace + ">")
				.append(this.ServiceNamespace)
				.append("</" + _xmlTag_ServiceNamespace + ">");
		
			sb.append("<" + _xmlTag_ServiceVersion + ">")
				.append(this.ServiceVersion)
				.append("</" + _xmlTag_ServiceVersion + ">");
		}
		else {
			sb.append("<" + _xmlTag_RegistrationSuccessful + ">")
				.append(this.RegistrationSuccessful)
				.append("</" + _xmlTag_RegistrationSuccessful + ">");
		}
		
		sb = appendErrorPayload(sb);
		
		return sb.toString();
	}

	@Override
	public void fromXML(XmlPullParser parser) throws Exception {		
		boolean done = false;
		do {
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				if (tagName.equals(CHILD_ELEMENT)) {
					parser.next();
				} else if (tagName.equals(_xmlTag_RegistrationSuccessful)) {
					this.RegistrationSuccessful = Boolean.parseBoolean(parser.nextText());	
				} else if (tagName.equals(_xmlTag_ServiceNamespace)) {
					this.ServiceNamespace = parser.nextText();
				} else if (tagName.equals(_xmlTag_ServiceVersion)) {
					this.ServiceVersion = Integer.parseInt( parser.nextText() );
				} else if (tagName.equals("error")) {
					parser = parseErrorAttributes(parser);
				} else
					parser.next();
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals(CHILD_ELEMENT))
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
		return CHILD_ELEMENT;
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}
	
}