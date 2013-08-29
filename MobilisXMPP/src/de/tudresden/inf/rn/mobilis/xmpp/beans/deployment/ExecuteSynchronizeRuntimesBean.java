package de.tudresden.inf.rn.mobilis.xmpp.beans.deployment;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * This IQ Bean is needed for sending a service update request to the server (by remote client for example). If the user is allowed to do
 * this, the server should update all his remote services, running on other known runtimes.
 * @author Philipp
 *
 */
public class ExecuteSynchronizeRuntimesBean extends XMPPBean {

	private static final long serialVersionUID = 1L;

	public static final String NAMESPACE = Mobilis.NAMESPACE + "#XMPPBeans:deployment:executeSynchronizeRuntimes";
	public static final String CHILD_ELEMENT = "executeSynchronizeRuntimes";
	
	/**
	 * General Constructor creates a RESULT BEAN. It must manually set to SET for sending an update request.
	 */
	public ExecuteSynchronizeRuntimesBean() {
		super();
		this.setType(XMPPBean.TYPE_RESULT);
	}
	
	// ERROR
	public ExecuteSynchronizeRuntimesBean(String errorType, String errorCondition, String errorText) {
		super(errorType, errorCondition, errorText);	
	}

	
	@Override
	public ExecuteSynchronizeRuntimesBean clone() {
		ExecuteSynchronizeRuntimesBean clone = new ExecuteSynchronizeRuntimesBean();
		clone = (ExecuteSynchronizeRuntimesBean) cloneBasicAttributes(clone);
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
		sb = appendErrorPayload(sb);
		
		return sb.toString();
	}

}