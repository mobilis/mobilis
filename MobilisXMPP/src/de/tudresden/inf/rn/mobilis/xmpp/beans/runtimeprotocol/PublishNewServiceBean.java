package de.tudresden.inf.rn.mobilis.xmpp.beans.runtimeprotocol;

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
	
	public String newServiceJID;
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
	public PublishNewServiceBean(String newServiceJID){
		super();
		this.newServiceJID = newServiceJID;
		this.type=XMPPBean.TYPE_SET;
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

		do {
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				if (tagName.equals(CHILD_ELEMENT)) {
					parser.next();
				} else if (tagName.equals(_xmlTag_ServiceJID)) {
					this.newServiceJID = parser.nextText();
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
		PublishNewServiceBean twin = new PublishNewServiceBean(this.newServiceJID);		
				
		twin = (PublishNewServiceBean) cloneBasicAttributes(twin);
		return twin;
	}

	@Override
	public String payloadToXML() {
		
		StringBuilder sb = new StringBuilder();
				
		if (getType() == XMPPBean.TYPE_SET) {
			if(this.newServiceJID!=null){
			sb.append("<" + _xmlTag_ServiceJID + ">")
			.append(this.newServiceJID)
			.append("</" + _xmlTag_ServiceJID + ">");	
			}
		}
		
		if(getType() == XMPPBean.TYPE_ERROR){
			if(this.newServiceJID!=null){
				sb.append("<" + _xmlTag_ServiceJID + ">")
				.append(this.newServiceJID)
				.append("</" + _xmlTag_ServiceJID + ">");	
				}
		}
		
		sb = appendErrorPayload(sb);
		
		return sb.toString();
	}

	public String getNewServiceJID() {
		return newServiceJID;
	}

	public void setNewServiceJID(String newServiceJID) {
		this.newServiceJID = newServiceJID;
	}

}
