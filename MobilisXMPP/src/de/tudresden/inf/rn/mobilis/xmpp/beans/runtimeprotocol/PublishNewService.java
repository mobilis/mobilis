package de.tudresden.inf.rn.mobilis.xmpp.beans.runtimeprotocol;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.coordination.MobilisServiceInfo;

public class PublishNewService extends XMPPBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String NAMESPACE = Mobilis.NAMESPACE + "#XMPPBean:deployment:publishNewService";
	public static final String CHILD_ELEMENT = "publishNewService";
	
	public String newServiceJID;
	
	/*
	 * Constructor to send a Result
	 */
	public PublishNewService() {
		super();
		this.type=XMPPBean.TYPE_RESULT;
	}
	
	/*
	 * Constructor for publish a new Service to another Runtimes Discovery
	 */
	public PublishNewService(String newServiceJID){
		super();
		this.newServiceJID = newServiceJID;
		this.type=XMPPBean.TYPE_SET;
	}
	
	/*
	 * Constructor for occuring Errors while trying to publish a new Service 
	 */
	public PublishNewService(String errorType, String errorCondition,
			String errorText) {
		super(errorType, errorCondition, errorText);
	}

	@Override
	public void fromXML(XmlPullParser parser) throws Exception {
		String childElement = MobilisServiceInfo.CHILD_ELEMENT;
		
		boolean done = false;

		do {
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				if (tagName.equals(childElement)) {
					for (int i = 0; i < parser.getAttributeCount(); i++)
						if (parser.getAttributeName(i).equals("newservicejid"))
							this.newServiceJID = parser.getAttributeValue(i);	
					parser.next();				
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
		return PublishNewService.CHILD_ELEMENT;
	}

	@Override
	public String getNamespace() {
		return PublishNewService.NAMESPACE;
	}

	@Override
	public PublishNewService clone() {
		PublishNewService twin = new PublishNewService(newServiceJID);		
				
		twin = (PublishNewService) cloneBasicAttributes(twin);
		return twin;
	}

	@Override
	public String payloadToXML() {
		String childElement = MobilisServiceInfo.CHILD_ELEMENT;
		
		StringBuilder sb = new StringBuilder()
				.append("<").append(childElement).append(" ");
				
		if (this.newServiceJID!=null) sb.append("newservicejid=\"").append(this.newServiceJID).append("\" ");
						
		sb.append(" />");				
		
		return sb.toString();
	}

	public String getNewServiceJID() {
		return newServiceJID;
	}

	public void setNewServiceJID(String newServiceJID) {
		this.newServiceJID = newServiceJID;
	}

}
