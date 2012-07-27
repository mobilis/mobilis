/**
 * 
 */
package de.tudresden.inf.rn.mobilis.mxa.services.sessionmobility;

import java.util.List;

import org.jivesoftware.smack.packet.IQ;

/**
 * @author koren
 * 
 */
public class SessionInvitationIQ extends IQ {

	public static final String elementName = "SessionInvitation";
	public static final String namespace = "mobilis:iq:sessionmobility";

	private String mAppURI;
	private List<String> mParams;

	public SessionInvitationIQ() {
	}

	public SessionInvitationIQ(String appURI) {
		this();

		mAppURI = appURI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jivesoftware.smack.packet.IQ#getChildElementXML()
	 */
	@Override
	public String getChildElementXML() {
		StringBuffer buf = new StringBuffer();
		buf.append("<" + elementName + " xmlns=\"" + namespace + "\">\n");

		buf.append("<appnamespace>").append(this.mAppURI)
				.append("</appnamespace>\n");

		if ((mParams != null) && !mParams.isEmpty()) {
			buf.append("<params>\n");
			for (String i : mParams) {
				buf.append("<param>");
				buf.append(i);
				buf.append("</param>\n");
			}
			buf.append("</params>\n");
		}

		buf.append("</" + elementName + ">");
		return buf.toString();
	}

	public String getAppURI() {
		return mAppURI;
	}

	public void setAppURI(String appURI) {
		this.mAppURI = appURI;
	}

	public List<String> getParams() {
		return mParams;
	}
	
	public void setParams(List<String> params) {
		mParams = params;
	}

}
