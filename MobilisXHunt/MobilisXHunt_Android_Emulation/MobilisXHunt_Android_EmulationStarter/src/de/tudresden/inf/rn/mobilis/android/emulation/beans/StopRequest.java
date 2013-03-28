package de.tudresden.inf.rn.mobilis.android.emulation.beans;

import org.xmlpull.v1.XmlPullParser;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

import java.util.List;import java.util.ArrayList;

public class StopRequest extends XMPPBean {

	private String appNamespace = null;
	private int instanceId = Integer.MIN_VALUE;


	public StopRequest( String appNamespace, int instanceId ) {
		super();
		this.appNamespace = appNamespace;
		this.instanceId = instanceId;

		this.setType( XMPPBean.TYPE_SET );
	}

	public StopRequest(){
		this.setType( XMPPBean.TYPE_SET );
	}


	public void fromXML( XmlPullParser parser ) throws Exception {
		boolean done = false;
			
		do {
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				
				if (tagName.equals(getChildElement())) {
					parser.next();
				}
				else if (tagName.equals( "appNamespace" ) ) {
					this.appNamespace = parser.nextText();
				}
				else if (tagName.equals( "instanceId" ) ) {
					this.instanceId = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals("error")) {
					parser = parseErrorAttributes(parser);
				}
				else
					parser.next();
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals(getChildElement()))
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

	public static final String CHILD_ELEMENT = "StopRequest";

	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "emulation:iq:stop";

	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		StopRequest clone = new StopRequest( appNamespace, instanceId );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<appNamespace>" )
			.append( this.appNamespace )
			.append( "</appNamespace>" );

		sb.append( "<instanceId>" )
			.append( this.instanceId )
			.append( "</instanceId>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public StopRequest buildStopError(String detailedErrorText){
		StopRequest fault = ( StopRequest )this.clone();

		fault.setTo( this.getFrom() );
    	fault.setId(this.getId());
		fault.setType( XMPPBean.TYPE_ERROR );
		fault.errorType = "modify";
		fault.errorCondition = "not-acceptable";
		fault.errorText = "Couldn't stop application instance! Either the application namespace isn't known by the TestNodeModule or the instance was already stopped.";

		if(null != detailedErrorText && detailedErrorText.length() > 0)
			fault.errorText += " Detail: " + detailedErrorText;
		
		return fault;
	}





	public String getAppNamespace() {
		return this.appNamespace;
	}

	public void setAppNamespace( String appNamespace ) {
		this.appNamespace = appNamespace;
	}

	public int getInstanceId() {
		return this.instanceId;
	}

	public void setInstanceId( int instanceId ) {
		this.instanceId = instanceId;
	}

}