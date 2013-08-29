package de.tudresden.inf.rn.mobilis.xmpp.beans.coordination;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * This bean is sent to create a new instance of
 * a service on the MobilisServer.
 * @author Robert L�bke, Philipp Grubitzsch
 */
public class SendNewServiceInstanceBean extends XMPPBean {

	private static final long serialVersionUID = 1L;
	public static final String NAMESPACE = Mobilis.NAMESPACE + "#services/CoordinatorService";
	public static final String CHILD_ELEMENT = "sendNewServiceInstance";
	
	public String serviceNamespace;
	public int serviceVersion = -1;
	public int minVersion = -1;
	public int maxVersion = -1;
	public String jidOfNewService;
	public String jidOfOriginalRequestor;
	
	/** Constructor for sending new Service Instance Information back to original Requestor; type=SET */
	public SendNewServiceInstanceBean(String jidOfNewService, int serviceVersion) {
		super();
		this.jidOfNewService=jidOfNewService;
		this.serviceVersion = serviceVersion;
		this.type=XMPPBean.TYPE_SET;
	}
		
	/** Constructor for type=ERROR */
	public SendNewServiceInstanceBean(String errorType, String errorCondition, String errorText) {
		super(errorType, errorCondition, errorText);	
	}
	
	/** Constructor for type=RESULT */
	public SendNewServiceInstanceBean() {
		super();
		this.type=XMPPBean.TYPE_RESULT;
	}
	
	@Override
	public SendNewServiceInstanceBean clone() {
		SendNewServiceInstanceBean twin = new SendNewServiceInstanceBean(jidOfNewService, serviceVersion);		
		twin.jidOfNewService = this.jidOfNewService;
		twin.serviceVersion = this.serviceVersion;
		twin.minVersion = this.minVersion;
		twin.maxVersion = this.maxVersion;
		
		twin = (SendNewServiceInstanceBean) cloneBasicAttributes(twin);
		return twin;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();
		
		if (this.serviceNamespace!=null)
			sb.append("<serviceNamespace>").append(serviceNamespace).append("</serviceNamespace>");
		if (this.jidOfNewService!=null)
			sb.append("<jidOfNewService>").append(jidOfNewService).append("</jidOfNewService>");
		if (this.jidOfOriginalRequestor!=null)
			sb.append("<jidOfOriginalRequestor>").append(jidOfOriginalRequestor).append("</jidOfOriginalRequestor>");
		if (this.serviceVersion!=-1)
			sb.append("<serviceVersion>").append(this.serviceVersion).append("</serviceVersion>");
		if (this.minVersion!=-1)
			sb.append("<minVersion>").append(this.minVersion).append("</minVersion>");
		if (this.maxVersion!=-1)
			sb.append("<maxVersion>").append(this.maxVersion).append("</maxVersion>");
		
		sb = appendErrorPayload(sb);		
		return sb.toString();
	}

	@Override
	public void fromXML(XmlPullParser parser) throws Exception {
		String childElement = SendNewServiceInstanceBean.CHILD_ELEMENT;
		
		boolean done = false;
		do {
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				if (tagName.equals(childElement)) {
					parser.next();
				} else if (tagName.equals("serviceNamespace")) {
					this.serviceNamespace = parser.nextText();		
				} else if (tagName.equals("jidOfNewService")) {
					this.jidOfNewService = parser.nextText();
				} else if (tagName.equals("jidOfOriginalRequestor")) {
					this.jidOfOriginalRequestor = parser.nextText();	
				} else if (tagName.equals("serviceVersion")) {
					this.serviceVersion = Integer.parseInt( parser.nextText() );	
				} else if (tagName.equals("minVersion")) {
					this.minVersion = Integer.parseInt( parser.nextText() );
				} else if (tagName.equals("maxVersion")) {
					this.maxVersion = Integer.parseInt( parser.nextText() );
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
		return SendNewServiceInstanceBean.CHILD_ELEMENT;
	}

	@Override
	public String getNamespace() {
		return SendNewServiceInstanceBean.NAMESPACE;
	}

	public String getServiceNamespace() {
		return serviceNamespace;
	}

	public void setServiceNamespace(String serviceNamespace) {
		this.serviceNamespace = serviceNamespace;
	}

	public String getJidOfNewService() {
		return jidOfNewService;
	}

	public void setJidOfNewService(String jidOfNewService) {
		this.jidOfNewService = jidOfNewService;
	}

	/**
	 * @return the serviceVersion
	 */
	public int getServiceVersion() {
		return serviceVersion;
	}

	/**
	 * @param serviceVersion the serviceVersion to set
	 */
	public void setServiceVersion( int serviceVersion ) {
		this.serviceVersion = serviceVersion;
	}

	/**
	 * @return the minVersion
	 */
	public int getMinVersion() {
		return minVersion;
	}

	/**
	 * @param minVersion the minVersion to set
	 */
	public void setMinVersion( int minVersion ) {
		this.minVersion = minVersion;
	}

	/**
	 * @return the maxVersion
	 */
	public int getMaxVersion() {
		return maxVersion;
	}

	/**
	 * @param maxVersion the maxVersion to set
	 */
	public void setMaxVersion( int maxVersion ) {
		this.maxVersion = maxVersion;
	}
	
	
	
}
