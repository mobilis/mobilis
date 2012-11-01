package de.tudresden.inf.rn.mobilis.emulation.serverstubs;

import org.xmlpull.v1.XmlPullParser;import java.util.List;import java.util.ArrayList;

public class ExecutionResultRequest extends XMPPBean {

	private int command_id = Integer.MIN_VALUE;
	private String message = null;


	public ExecutionResultRequest( int command_id, String message ) {
		super();
		this.command_id = command_id;
		this.message = message;

		this.setType( XMPPBean.TYPE_SET );
	}

	public ExecutionResultRequest(){
		this.setType( XMPPBean.TYPE_SET );
	}


	@Override
	public void fromXML( XmlPullParser parser ) throws Exception {
		boolean done = false;
			
		do {
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				
				if (tagName.equals(getChildElement())) {
					parser.next();
				}
				else if (tagName.equals( "command_id" ) ) {
					this.command_id = Integer.parseInt( parser.nextText() );
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

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "emulation:iq:connect";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		ExecutionResultRequest clone = new ExecutionResultRequest( command_id, message );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<command_id>" )
			.append( this.command_id )
			.append( "</command_id>" );

		sb.append( "<message>" )
			.append( this.message )
			.append( "</message>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}





	public int getCommand_id() {
		return this.command_id;
	}

	public void setCommand_id( int command_id ) {
		this.command_id = command_id;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage( String message ) {
		this.message = message;
	}

}