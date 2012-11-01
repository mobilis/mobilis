package de.tudresden.inf.rn.mobilis.emulation.serverstubs;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class CommandRequest extends XMPPBean {

	private String method_name = null;
	private List< String > parameters = new ArrayList< String >();
	private List< String > parameter_types = new ArrayList< String >();
	private int command_id = Integer.MIN_VALUE;


	public CommandRequest( String method_name, List< String > parameters, List< String > parameter_types, int command_id ) {
		super();
		this.method_name = method_name;
		for ( String entity : parameters ) {
			this.parameters.add( entity );
		}
		for ( String entity : parameter_types ) {
			this.parameter_types.add( entity );
		}
		this.command_id = command_id;

		this.setType( XMPPBean.TYPE_SET );
	}

	public CommandRequest(){
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
				else if (tagName.equals( "method_name" ) ) {
					this.method_name = parser.nextText();
				}
				else if (tagName.equals( "parameters" ) ) {
					parameters.add( parser.nextText() );
				}
				else if (tagName.equals( "parameter_types" ) ) {
					parameter_types.add( parser.nextText() );
				}
				else if (tagName.equals( "command_id" ) ) {
					this.command_id = Integer.parseInt( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "CommandRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "emulation:iq:command";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		CommandRequest clone = new CommandRequest( method_name, parameters, parameter_types, command_id );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<method_name>" )
			.append( this.method_name )
			.append( "</method_name>" );

		for( String entry : parameters ) {
			sb.append( "<parameters>" );
			sb.append( entry );
			sb.append( "</parameters>" );
		}

		for( String entry : parameter_types ) {
			sb.append( "<parameter_types>" );
			sb.append( entry );
			sb.append( "</parameter_types>" );
		}

		sb.append( "<command_id>" )
			.append( this.command_id )
			.append( "</command_id>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public String getMethod_name() {
		return this.method_name;
	}

	public void setMethod_name( String method_name ) {
		this.method_name = method_name;
	}

	public List< String > getParameters() {
		return this.parameters;
	}

	public void setParameters( List< String > parameters ) {
		this.parameters = parameters;
	}

	public List< String > getParameter_types() {
		return this.parameter_types;
	}

	public void setParameter_types( List< String > parameter_types ) {
		this.parameter_types = parameter_types;
	}

	public int getCommand_id() {
		return this.command_id;
	}

	public void setCommand_id( int command_id ) {
		this.command_id = command_id;
	}

}