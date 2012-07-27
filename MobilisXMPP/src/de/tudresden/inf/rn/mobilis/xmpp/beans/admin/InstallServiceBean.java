package de.tudresden.inf.rn.mobilis.xmpp.beans.admin;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class InstallServiceBean extends XMPPBean {

	private static final long serialVersionUID = -3517043825474566387L;
	
	public static final String NAMESPACE = Mobilis.NAMESPACE + "#XMPPBeans:admin:installService";
	public static final String CHILD_ELEMENT = "installService";
	
	// SET
	public String FileName = null;
	
	// RESULT
	public boolean InstallationSucessful = false;
	public String Message = null;
	public String ServiceNamespace = null;
	public int ServiceVersion = -1;
	
	private String _xmlTag_FileName = "filename";
	private String _xmlTag_InstallationSuccessful = "installationsuccessful";
	private String _xmlTag_Message = "message";
	private String _xmlTag_ServiceNamespace = "servicenamespace";
	private String _xmlTag_ServiceVersion = "serviceversion";
	
	
	public InstallServiceBean() {
		super();
	}	
	
	// SET
	public InstallServiceBean(String fileName) {
		super();
		
		this.FileName = fileName;
	}
	
	// RESULT
	public InstallServiceBean(boolean installationSuccessful, String serviceNamespace, int serviceVersion, String message) {
		super();
		
		this.InstallationSucessful = installationSuccessful;
		this.Message = message;
		this.ServiceNamespace = serviceNamespace;
		this.ServiceVersion = serviceVersion;
	}
	
	// ERROR
	public InstallServiceBean(String errorType, String errorCondition, String errorText) {
		super(errorType, errorCondition, errorText);	
	}

	
	@Override
	public InstallServiceBean clone() {
		InstallServiceBean clone =
			null != this.FileName
				? new InstallServiceBean( this.FileName )
				: new InstallServiceBean( this.InstallationSucessful, this.ServiceNamespace, this.ServiceVersion, this.Message );
		
		clone = (InstallServiceBean) cloneBasicAttributes(clone);
		
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
				} else if (tagName.equals(_xmlTag_FileName)) {
					this.FileName = parser.nextText();
				} else if (tagName.equals(_xmlTag_InstallationSuccessful)) {
					this.InstallationSucessful = Boolean.parseBoolean( parser.nextText() );
				} else if (tagName.equals(_xmlTag_Message)) {
					this.Message = parser.nextText();
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
		
		if (null != this.FileName){
			sb.append("<" + _xmlTag_FileName + ">")
				.append(this.FileName)
				.append("</" + _xmlTag_FileName + ">");
		}
		else{		
			sb.append("<" + _xmlTag_InstallationSuccessful + ">")
				.append(this.InstallationSucessful)
				.append("</" + _xmlTag_InstallationSuccessful + ">");
			
			sb.append("<" + _xmlTag_ServiceNamespace + ">")
				.append(this.ServiceNamespace)
				.append("</" + _xmlTag_ServiceNamespace + ">");
			
			sb.append("<" + _xmlTag_ServiceVersion + ">")
				.append(this.ServiceVersion)
				.append("</" + _xmlTag_ServiceVersion + ">");
			
			if (null != this.Message) {
				sb.append("<" + _xmlTag_Message + ">")
					.append(this.Message)
					.append("</" + _xmlTag_Message + ">");
			}
		}
		
		sb = appendErrorPayload(sb);
		
		return sb.toString();
	}
}