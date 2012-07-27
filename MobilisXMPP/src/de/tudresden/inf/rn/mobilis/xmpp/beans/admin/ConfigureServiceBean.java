package de.tudresden.inf.rn.mobilis.xmpp.beans.admin;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class ConfigureServiceBean extends XMPPBean {

	private static final long serialVersionUID = 5861561620590442363L;
	
	public static final String NAMESPACE = Mobilis.NAMESPACE + "#XMPPBeans:admin:configureService";
	public static final String CHILD_ELEMENT = "configureService";
	
	// SET
	public AgentConfigInfo AgentConfig = null;
//	public ServiceConfigInfo ServiceConfig = null;
	public String ServiceNamespace = null;
	public int ServiceVersion = -1;
	
	// RESULT
	
	private String _xmlTag_AgentConfig = "agentConfig";
//	private String _xmlTag_ServiceConfig = "serviceConfig";
	private String _xmlTag_ServiceNamespace = "serviceNamespace";
	private String _xmlTag_ServiceVersion = "serviceVersion";
	
	
	public ConfigureServiceBean() {
		super();
	}
	
	// SET
	public ConfigureServiceBean(AgentConfigInfo agentConfig, //ServiceConfigInfo serviceConfig,
			String serviceNamespace, int serviceversion) {
		super();
		
		this.AgentConfig = agentConfig;
//		this.ServiceConfig = serviceConfig;
		this.ServiceNamespace = serviceNamespace;
		this.ServiceVersion = serviceversion;
	}
	
	// ERROR
	public ConfigureServiceBean(String errorType, String errorCondition, String errorText) {
		super(errorType, errorCondition, errorText);	
	}

	
	@Override
	public ConfigureServiceBean clone() {
		ConfigureServiceBean clone =
			null != this.AgentConfig
				? new ConfigureServiceBean(this.AgentConfig.clone(),
//						this.ServiceConfig.clone(),
						this.ServiceNamespace,
						this.ServiceVersion)
				: new ConfigureServiceBean();
		
		clone = (ConfigureServiceBean) cloneBasicAttributes(clone);
		
		return clone;
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
				} else if (tagName.equals(_xmlTag_AgentConfig)) {
					this.AgentConfig = new AgentConfigInfo();
					this.AgentConfig.fromXML( parser );
//				} else if (tagName.equals(_xmlTag_ServiceConfig)) {
//					this.ServiceConfig = new ServiceConfigInfo();
//					this.ServiceConfig.fromXML( parser );
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
	
	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();
		
		if (null != this.AgentConfig)
			sb.append("<" + _xmlTag_AgentConfig + ">")
				.append(this.AgentConfig.toXML())
				.append("</" + _xmlTag_AgentConfig + ">");
		
//		if (null != this.ServiceConfig)
//			sb.append("<" + _xmlTag_ServiceConfig + ">")
//				.append(this.ServiceConfig.toXML())
//				.append("</" + _xmlTag_ServiceConfig + ">");
		
		if (null != this.ServiceNamespace) {
			sb.append("<" + _xmlTag_ServiceNamespace + ">")
				.append(this.ServiceNamespace)
				.append("</" + _xmlTag_ServiceNamespace + ">");
			
			sb.append("<" + _xmlTag_ServiceVersion + ">")
				.append(this.ServiceVersion)
				.append("</" + _xmlTag_ServiceVersion + ">");
		}
		
		sb = appendErrorPayload(sb);
		
		return sb.toString();
	}
}