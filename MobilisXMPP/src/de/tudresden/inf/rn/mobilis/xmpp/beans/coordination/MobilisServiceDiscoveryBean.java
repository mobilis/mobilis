package de.tudresden.inf.rn.mobilis.xmpp.beans.coordination;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * This bean is sent to discover running MobilisServices 
 * on the MobilisServer.
 * @author Robert L�bke
 */
public class MobilisServiceDiscoveryBean extends XMPPBean {

	private static final long serialVersionUID = 1L;
	public static final String NAMESPACE = Mobilis.NAMESPACE + "#services/CoordinatorService";
	public static final String CHILD_ELEMENT = "serviceDiscovery";
	
	public String serviceNamespace;
	public int serviceVersion = Integer.MIN_VALUE;
	public boolean requestMSDL = false;
	private List<MobilisServiceInfo> discoveredServices;
	
	/** Constructor for discovering all services on the MobilisServer; type=GET;
	 * also used for empty beans */
	public MobilisServiceDiscoveryBean() {
		super();		
		this.type=XMPPBean.TYPE_GET;
	}
	
	/** Constructor for discovering only the services with the given namespace
	 * and the correct version. type=GET */
	public MobilisServiceDiscoveryBean(String serviceNamespace, int serviceVersion, boolean requestMsdl) {
		super();
		this.serviceNamespace=serviceNamespace;
		this.serviceVersion = serviceVersion;
		this.requestMSDL = requestMsdl;
		this.type=XMPPBean.TYPE_GET;
	}
		
	/** Constructor for type=ERROR */
	public MobilisServiceDiscoveryBean(String errorType, String errorCondition, String errorText) {
		super(errorType, errorCondition, errorText);	
	}
	
	/** Constructor for type=RESULT */
	public MobilisServiceDiscoveryBean(List<MobilisServiceInfo> discoveredServices) {
		super();
		this.discoveredServices = discoveredServices;
		this.type=XMPPBean.TYPE_RESULT;
	}
	
	@Override
	public MobilisServiceDiscoveryBean clone() {
		MobilisServiceDiscoveryBean twin = new MobilisServiceDiscoveryBean(serviceNamespace, serviceVersion, requestMSDL);		
		twin.discoveredServices = this.discoveredServices;	
		
		twin = (MobilisServiceDiscoveryBean) cloneBasicAttributes(twin);
		return twin;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();
		
		if (this.serviceNamespace!=null)
			sb.append("<serviceNamespace>").append(serviceNamespace).append("</serviceNamespace>");
		if (this.serviceVersion > 0)
			sb.append("<serviceVersion>").append(serviceVersion).append("</serviceVersion>");
		if (this.requestMSDL)
			sb.append("<requestMSDL>").append(requestMSDL).append("</requestMSDL>");
		if (this.discoveredServices!=null)
			for (MobilisServiceInfo service : discoveredServices) {
				sb.append(service.toXML());
			}
		sb = appendErrorPayload(sb);		
		return sb.toString();
	}

	@Override
	public void fromXML(XmlPullParser parser) throws Exception {
		String childElement = MobilisServiceDiscoveryBean.CHILD_ELEMENT;
		
		discoveredServices = new ArrayList<MobilisServiceInfo>();
		
		boolean done = false;
		do {
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				if (tagName.equals(childElement)) {
					parser.next();
				} else if (tagName.equals("serviceNamespace")) {
					this.serviceNamespace = parser.nextText();		
				} else if (tagName.equals("serviceVersion")) {
					this.serviceVersion = Integer.parseInt( parser.nextText() );
				} else if (tagName.equals("requestMSDL")) {
					this.requestMSDL = Boolean.parseBoolean( parser.nextText() );
				} else if (tagName.equals(MobilisServiceInfo.CHILD_ELEMENT)) {
					MobilisServiceInfo service = new MobilisServiceInfo();
					
					service.fromXML(parser);
					
					discoveredServices.add(service);
					parser.next();
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
		
		if (discoveredServices.size()<=0)
			discoveredServices=null;
		
	}
	
	/**
	 * Adds an InfoItem about a MobilisService with type="single".
	 * @param namespace Namespace of the MobilisService
	 * @param version version of the MobilisService
	 * @param jid XMPP-ID of the MobilisService
	 */
	public void addDiscoveredService(String namespace, String version, String jid) {
		if (discoveredServices==null)
			discoveredServices = new ArrayList<MobilisServiceInfo>();
		
		MobilisServiceInfo newService = new MobilisServiceInfo(namespace, version, jid);
				
		discoveredServices.add(newService);	
	}
	
	/**
	 * Adds an InfoItem about a MobilisService with type="multi".
	 * @param namespace  Namespace of the MobilisService
	 * @param instances Number of currently running instances of this MobilisService
	 */
	public void addDiscoveredService(String namespace, int instances) {
		if (discoveredServices==null)
			discoveredServices = new ArrayList<MobilisServiceInfo>();
		
		MobilisServiceInfo newService = new MobilisServiceInfo(namespace, instances);
		
		discoveredServices.add(newService);		
	}
	
	/**
	 * Adds a MobilisServiceInfo to the list of discovered services.
	 * @param serviceInfo the MobilisServiceInfo to add
	 */
	public void addDiscoveredService(MobilisServiceInfo serviceInfo) {
		if (discoveredServices==null)
			discoveredServices = new ArrayList<MobilisServiceInfo>();						
		discoveredServices.add(serviceInfo);		
	}
	
	/**
	 * Gets the list of discovered services.
	 * @return a list of MobilisServiceInfo objects which describe the discovered services.
	 */
	public List<MobilisServiceInfo> getDiscoveredServices() {
		return discoveredServices;
	}

	@Override
	public String getChildElement() {
		return MobilisServiceDiscoveryBean.CHILD_ELEMENT;
	}

	@Override
	public String getNamespace() {
		return MobilisServiceDiscoveryBean.NAMESPACE;
	}
	
}
