package de.tudresden.inf.rn.mobilis.mxa.services.sessionmobility;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;

public class SessionTransferIQ extends IQ {

	public static final String elementName = "query";
	public static final String namespace = "mobilis:iq:sessionmobility#sessiontransfer";

	private String mAppURI;
	private List<String> mMechanisms;

	public SessionTransferIQ() {
	}
	
	public SessionTransferIQ(String appURI) {
		this();
		
		mAppURI = appURI;
	}

	@Override
	public String getChildElementXML() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("<" + elementName + " xmlns=\"" + namespace + "\">\n");

		buf.append("<appuri>").append(this.mAppURI).append("</appuri>\n");

		buf.append("<mechanisms>\n");
		if (mMechanisms != null) {
			for (String mechanism : mMechanisms) {
				buf.append("<mechanism>");
				buf.append(mechanism);
				buf.append("</mechanism>\n");
			}
		}
		buf.append("</mechanisms>\n");

		buf.append("</" + elementName + ">");
		return buf.toString();
	}

	public String getAppURI() {
		return mAppURI;
	}

	public void setAppURI(String appURI) {
		this.mAppURI = appURI;
	}
	
	public List<String> getMechanisms() {
		return mMechanisms;
	}
	
	public void setMechanisms(List<String> mechanisms) {
		mMechanisms = mechanisms;
	}
	
	public void addMechanism(String mechanism) {
		if (mMechanisms == null) {
			mMechanisms = new ArrayList<String>();
		}
		mMechanisms.add(mechanism);
	}

}
