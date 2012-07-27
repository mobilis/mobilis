package de.tudresden.inf.rn.mobilis.xmpp.beans.admin;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class UpdateServiceBean extends XMPPBean {

	private static final long serialVersionUID = -6212607235234490605L;

	public static final String NAMESPACE = Mobilis.NAMESPACE + "#XMPPBeans:admin:updateService";
	public static final String CHILD_ELEMENT = "updateService";
	
	// SET
	public String FileName = null;
	public String OldServiceNamespace = null;
	public int OldServiceVersion = -1;
	
	// RESULT
	public String NewServiceNamespace = null;
	public int NewServiceVersion = -1;
	
	private String _xmlTag_FileName = "filename";
	private String _xmlTag_OldServiceNamespace = "oldservicenamespace";
	private String _xmlTag_OldServiceVersion = "oldserviceversion";
	private String _xmlTag_NewServiceNamespace = "newservicenamespace";
	private String _xmlTag_NewServiceVersion = "newserviceversion";
	
	
	public UpdateServiceBean() {
		super();
	}	
	
	// SET
	public UpdateServiceBean(String fileName, String oldServiceNamespace, int oldServiceVersion) {
		super();
		
		this.FileName = fileName;
		this.OldServiceNamespace = oldServiceNamespace;
		this.OldServiceVersion = oldServiceVersion;
	}
	
	// RESULT
	public UpdateServiceBean(String newServiceNamespace, int newServiceVersion) {
		super();
		
		this.NewServiceNamespace = newServiceNamespace;
		this.NewServiceVersion = newServiceVersion;
	}
	
	// ERROR
	public UpdateServiceBean(String errorType, String errorCondition, String errorText) {
		super(errorType, errorCondition, errorText);	
	}

	
	@Override
	public UpdateServiceBean clone() {
		UpdateServiceBean clone =
			null != this.FileName
				? new UpdateServiceBean( this.FileName, this.OldServiceNamespace, this.OldServiceVersion )
				: new UpdateServiceBean( this.NewServiceNamespace, this.NewServiceVersion );
		
		clone = (UpdateServiceBean) cloneBasicAttributes(clone);
		
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
				} else if (tagName.equals(_xmlTag_OldServiceNamespace)) {
					this.OldServiceNamespace = parser.nextText();
				} else if (tagName.equals(_xmlTag_OldServiceVersion)) {
					this.OldServiceVersion = Integer.parseInt( parser.nextText() );
				} else if (tagName.equals(_xmlTag_NewServiceNamespace)) {
					this.NewServiceNamespace = parser.nextText();
				} else if (tagName.equals(_xmlTag_NewServiceVersion)) {
					this.NewServiceVersion = Integer.parseInt( parser.nextText() );
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
			
			sb.append("<" + _xmlTag_OldServiceNamespace + ">")
				.append(this.OldServiceNamespace)
				.append("</" + _xmlTag_OldServiceNamespace + ">");
		
			sb.append("<" + _xmlTag_OldServiceVersion + ">")
				.append(this.OldServiceVersion)
				.append("</" + _xmlTag_OldServiceVersion + ">");
		}
		else{		
			sb.append("<" + _xmlTag_NewServiceNamespace + ">")
				.append(this.NewServiceNamespace)
				.append("</" + _xmlTag_NewServiceNamespace + ">");
			
			sb.append("<" + _xmlTag_NewServiceVersion + ">")
				.append(this.NewServiceVersion)
				.append("</" + _xmlTag_NewServiceVersion + ">");
		}
		
		sb = appendErrorPayload(sb);
		
		return sb.toString();
	}
}