package de.tudresden.inf.rn.mobilis.xmpp.beans.runtimeprotocol;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * 
 * @author Philipp Grubitzsch
 *
 */
public class PublishNewServiceBean extends XMPPBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String NAMESPACE = Mobilis.NAMESPACE + "#XMPPBean:deployment:publishNewService";
	public static final String CHILD_ELEMENT = "publishNewService";
	
	public List<String> newServiceJIDs;
	public boolean successfullyAddedService;
	private String _xmlTag_ServiceJID = "serviceJID";
	
	/*
	 * Constructor to send a Result
	 */
	public PublishNewServiceBean() {
		super();
		this.type=XMPPBean.TYPE_RESULT;
	}
	
	/*
	 * Constructor for publish a new Service to another Runtimes Discovery
	 */
	public PublishNewServiceBean(List<String> newServiceJIDs){
		super();
		this.newServiceJIDs = newServiceJIDs;
		this.type=XMPPBean.TYPE_SET;
	}
	
	/**
	 * Constructor for a Get Request. Needed for Pulling remote services to a local runtime roster.
	 */
	public PublishNewServiceBean(String targetRuntimeJID){
		super();
		this.setTo(targetRuntimeJID);
		this.type = XMPPBean.TYPE_GET;
	}
	
	/*
	 * Constructor for occuring Errors while trying to publish a new Service 
	 */
	public PublishNewServiceBean(String errorType, String errorCondition,
			String errorText) {
		super(errorType, errorCondition, errorText);
	}

	@Override
	public void fromXML(XmlPullParser parser) throws Exception {
		//String childElement = MobilisServiceInfo.CHILD_ELEMENT;
		boolean done = false;
		newServiceJIDs = new ArrayList<String>();
		do {
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				if (tagName.equals(CHILD_ELEMENT)) {
					parser.next();
				} else if (tagName.equals(_xmlTag_ServiceJID)) {
					newServiceJIDs.add(parser.nextText());
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
//		parser.next();
//		System.out.println(parser.getName());
//		if(parser.getName().equals("error")){
//			this.errorType = parser.getAttributeValue(1);
//			parser.next();
//			this.errorCondition = parser.getName();
//			this.errorText = parser.getName();
//		}
	}

	@Override
	public String getChildElement() {
		return PublishNewServiceBean.CHILD_ELEMENT;
	}

	@Override
	public String getNamespace() {
		return PublishNewServiceBean.NAMESPACE;
	}

	@Override
	public PublishNewServiceBean clone() {
		PublishNewServiceBean twin = new PublishNewServiceBean(this.newServiceJIDs);		
				
		twin = (PublishNewServiceBean) cloneBasicAttributes(twin);
		return twin;
	}

	@Override
	public String payloadToXML() {
		
		StringBuilder sb = new StringBuilder();
				
		if (getType() == XMPPBean.TYPE_SET) {
			
			if(this.newServiceJIDs!=null){
				for(String jid : newServiceJIDs){
					sb.append("<" + _xmlTag_ServiceJID + ">")
					.append(jid)
					.append("</" + _xmlTag_ServiceJID + ">");	
				}
			}
		}
		
		if (getType() == XMPPBean.TYPE_RESULT) {
			
			if((this.newServiceJIDs!=null) && (newServiceJIDs.size()>0)){
				for(String jid : newServiceJIDs){
					sb.append("<" + _xmlTag_ServiceJID + ">")
					.append(jid)
					.append("</" + _xmlTag_ServiceJID + ">");	
				}
			}
		}
		
		if(getType() == XMPPBean.TYPE_ERROR){
			if(this.newServiceJIDs!=null){
				sb.append("<" + _xmlTag_ServiceJID + ">")
				.append(this.newServiceJIDs)
				.append("</" + _xmlTag_ServiceJID + ">");	
				}
		}
		
		sb = appendErrorPayload(sb);
		
		return sb.toString();
	}

	public List<String> getNewServiceJIDs() {
		return newServiceJIDs;
	}

	public void setNewServiceJID(List<String> newServiceJID) {
		this.newServiceJIDs = newServiceJID;
	}

}
