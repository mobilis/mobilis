package de.tudresden.inf.rn.mobilis.xmpp.beans.deployment;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class PrepareServiceUploadBean extends XMPPBean {

	private static final long serialVersionUID = -6549621360788268631L;

	public static final String NAMESPACE = Mobilis.NAMESPACE + "#XMPPBeans:admin:prepareServiceUpload";
	public static final String CHILD_ELEMENT = "prepareServiceUpload";
	
	// RESULT
	public boolean AcceptServiceUpload = false;
	
	// SET
	public String Filename = null;
	public boolean autoDeploy = false;
	public boolean singleMode = false;
	
	private String _xmlTag_AcceptServiceUpload = "acceptServiceUpload";
	private String _xmlTag_Filename = "filename";
	private String _xmlTag_AutoDeploy = "autoDeploy";
	private String _xmlTag_SingleMode = "singleMode";
	
	
	public PrepareServiceUploadBean() {
		super();
	}
	
	// RESULT
	public PrepareServiceUploadBean(boolean acceptServiceUpload) {
		super();
		
		this.AcceptServiceUpload = acceptServiceUpload;
	}
	
	// SET
	public PrepareServiceUploadBean(String fileName) {
		super();
		
		this.Filename = fileName;
	}
	
	public PrepareServiceUploadBean(String fileName, boolean autoDeploy, boolean singleMode) {
		super();
		
		this.Filename = fileName;
		this.autoDeploy = autoDeploy;
		this.singleMode = singleMode;
	}
	
	// ERROR
	public PrepareServiceUploadBean(String errorType, String errorCondition, String errorText) {
		super(errorType, errorCondition, errorText);	
	}

	
	@Override
	public PrepareServiceUploadBean clone() {
		PrepareServiceUploadBean clone = new PrepareServiceUploadBean();
		clone.Filename = this.Filename;
		clone.AcceptServiceUpload = this.AcceptServiceUpload;
		
		clone = (PrepareServiceUploadBean) cloneBasicAttributes(clone);
		
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
				} else if (tagName.equals(_xmlTag_Filename)) {
					this.Filename = parser.nextText();		
				} else if (tagName.equals(_xmlTag_AutoDeploy)) {
					this.autoDeploy = Boolean.parseBoolean(parser.nextText());
				} else if (tagName.equals(_xmlTag_SingleMode)) {
					this.singleMode = Boolean.parseBoolean(parser.nextText());
				} else if (tagName.equals(_xmlTag_AcceptServiceUpload)) {
					this.AcceptServiceUpload = Boolean.parseBoolean( parser.nextText() );
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

		if (getType() == XMPPBean.TYPE_SET) {
			if (null != this.Filename)
				sb.append("<" + _xmlTag_Filename + ">")
				.append(this.Filename)
				.append("</" + _xmlTag_Filename + ">");
			sb.append("<" + _xmlTag_AutoDeploy + ">")
				.append(this.autoDeploy)
				.append("</" + _xmlTag_AutoDeploy + ">");
			sb.append("<" + _xmlTag_SingleMode + ">")
				.append(this.singleMode)
				.append("</" + _xmlTag_SingleMode + ">");
		}
		
		if (getType() == XMPPBean.TYPE_RESULT) {
			sb.append("<" + _xmlTag_AcceptServiceUpload + ">")
			.append(this.AcceptServiceUpload)
			.append("</" + _xmlTag_AcceptServiceUpload + ">");	
		}
		
		sb = appendErrorPayload(sb);
		
		return sb.toString();
	}

}