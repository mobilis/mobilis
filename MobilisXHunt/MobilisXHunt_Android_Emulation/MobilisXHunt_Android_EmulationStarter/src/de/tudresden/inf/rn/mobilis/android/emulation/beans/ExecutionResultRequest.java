package de.tudresden.inf.rn.mobilis.android.emulation.beans;

import org.xmlpull.v1.XmlPullParser;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

import java.util.List;import java.util.ArrayList;

public class ExecutionResultRequest extends XMPPBean {

	private int commandId = Integer.MIN_VALUE;
	private String message = null;


	public ExecutionResultRequest( int commandId, String message ) {
		super();
		this.commandId = commandId;
		this.message = message;

		this.setType( XMPPBean.TYPE_SET );
	}

	public ExecutionResultRequest(){
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
				else if (tagName.equals( "commandId" ) ) {
					this.commandId = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "message" ) ) {
					this.message = parser.nextText();
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

	public static final String CHILD_ELEMENT = "ExecutionResultRequest";

	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "emulation:iq:executionresult";

	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		ExecutionResultRequest clone = new ExecutionResultRequest( commandId, message );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<commandId>" )
			.append( this.commandId )
			.append( "</commandId>" );

		sb.append( "<message>" )
			.append( this.message )
			.append( "</message>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public int getCommandId() {
		return this.commandId;
	}

	public void setCommandId( int commandId ) {
		this.commandId = commandId;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage( String message ) {
		this.message = message;
	}

}