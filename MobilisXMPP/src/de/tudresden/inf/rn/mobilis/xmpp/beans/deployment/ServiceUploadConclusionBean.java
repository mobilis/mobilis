package de.tudresden.inf.rn.mobilis.xmpp.beans.deployment;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class ServiceUploadConclusionBean extends XMPPBean {

	private static final long serialVersionUID = -6369084011702721904L;
	
	public static final String NAMESPACE = Mobilis.NAMESPACE + "#XMPPBean:deployment:serviceUploadConclusion";
	public static final String CHILD_ELEMENT = "serviceUploadConclusion";
	
	// SET
	public boolean UploadSuccessful = false;
	public String Message = null;
	public String FileName = null;
	
	// RESULT
	
	private String _xmlTag_UploadSuccessful = "uploadSuccessful";
	private String _xmlTag_Message = "message";
	private String _xmlTag_FileName = "filename";
	
	
	// RESULT
	public ServiceUploadConclusionBean() {
		super();
	}
	
	// SET
	public ServiceUploadConclusionBean(boolean successful, String fileName) {		
		super();
		
		this.UploadSuccessful = successful;
		this.FileName = fileName;
	}
	
	// SET with optional message (e.g. on upload failure)
	public ServiceUploadConclusionBean(boolean successful, String fileName, String message) {
		super();
		
		this.UploadSuccessful = successful;
		this.FileName = fileName;
		this.Message = message;		
	}
		
	// ERROR
	public ServiceUploadConclusionBean(String errorType, String errorCondition, String errorText) {
		super(errorType, errorCondition, errorText);	
	}

	
	@Override
	public ServiceUploadConclusionBean clone() {
		ServiceUploadConclusionBean clone = 
			null == this.Message
				? new ServiceUploadConclusionBean(this.UploadSuccessful, this.FileName)
				: new ServiceUploadConclusionBean(this.UploadSuccessful, this.FileName, this.Message);
		
		clone = (ServiceUploadConclusionBean) cloneBasicAttributes(clone);
		
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
				} else if (tagName.equals(_xmlTag_Message)) {
					this.Message = parser.nextText();		
				} else if (tagName.equals(_xmlTag_UploadSuccessful)) {
					this.UploadSuccessful = Boolean.parseBoolean(parser.nextText());	
				} else if (tagName.equals(_xmlTag_FileName)) {
					this.FileName = parser.nextText();	
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
		
		sb.append("<" + _xmlTag_UploadSuccessful + ">")
			.append(this.UploadSuccessful)
			.append("</" + _xmlTag_UploadSuccessful + ">");
		
		if (null != this.Message)
			sb.append("<" + _xmlTag_Message + ">")
				.append(this.Message)
				.append("</" + _xmlTag_Message + ">");
		
		if (null != this.FileName)
			sb.append("<" + _xmlTag_FileName + ">")
				.append(this.FileName)
				.append("</" + _xmlTag_FileName + ">");
		
		sb = appendErrorPayload(sb);
		
		return sb.toString();
	}
}
