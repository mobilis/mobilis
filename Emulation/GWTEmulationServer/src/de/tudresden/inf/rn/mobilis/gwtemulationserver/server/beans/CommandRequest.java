package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class CommandRequest extends XMPPBean {

	private String methodName = null;
	private List< String > parameters = new ArrayList< String >();
	private List< String > parameterTypes = new ArrayList< String >();
	private int commandId = Integer.MIN_VALUE;
	private int instanceId = Integer.MIN_VALUE;
	private String appNamespace = null;


	public CommandRequest( String methodName, List< String > parameters, List< String > parameterTypes, int commandId, int instanceId, String appNamespace ) {
		super();
		this.methodName = methodName;
		for ( String entity : parameters ) {
			this.parameters.add( entity );
		}
		for ( String entity : parameterTypes ) {
			this.parameterTypes.add( entity );
		}
		this.commandId = commandId;
		this.instanceId = instanceId;
		this.appNamespace = appNamespace;

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
				else if (tagName.equals( "methodName" ) ) {
					this.methodName = parser.nextText();
				}
				else if (tagName.equals( "parameters" ) ) {
					parameters.add( parser.nextText() );
				}
				else if (tagName.equals( "parameterTypes" ) ) {
					parameterTypes.add( parser.nextText() );
				}
				else if (tagName.equals( "commandId" ) ) {
					this.commandId = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "instanceId" ) ) {
					this.instanceId = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "appNamespace" ) ) {
					this.appNamespace = parser.nextText();
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
		CommandRequest clone = new CommandRequest( methodName, parameters, parameterTypes, commandId, instanceId, appNamespace );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<methodName>" )
			.append( this.methodName )
			.append( "</methodName>" );

		for( String entry : parameters ) {
			sb.append( "<parameters>" );
			sb.append( entry );
			sb.append( "</parameters>" );
		}

		for( String entry : parameterTypes ) {
			sb.append( "<parameterTypes>" );
			sb.append( entry );
			sb.append( "</parameterTypes>" );
		}

		sb.append( "<commandId>" )
			.append( this.commandId )
			.append( "</commandId>" );

		sb.append( "<instanceId>" )
			.append( this.instanceId )
			.append( "</instanceId>" );

		sb.append( "<appNamespace>" )
			.append( this.appNamespace )
			.append( "</appNamespace>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public String getMethodName() {
		return this.methodName;
	}

	public void setMethodName( String methodName ) {
		this.methodName = methodName;
	}

	public List< String > getParameters() {
		return this.parameters;
	}

	public void setParameters( List< String > parameters ) {
		this.parameters = parameters;
	}

	public List< String > getParameterTypes() {
		return this.parameterTypes;
	}

	public void setParameterTypes( List< String > parameterTypes ) {
		this.parameterTypes = parameterTypes;
	}

	public int getCommandId() {
		return this.commandId;
	}

	public void setCommandId( int commandId ) {
		this.commandId = commandId;
	}

	public int getInstanceId() {
		return this.instanceId;
	}

	public void setInstanceId( int instanceId ) {
		this.instanceId = instanceId;
	}

	public String getAppNamespace() {
		return this.appNamespace;
	}

	public void setAppNamespace( String appNamespace ) {
		this.appNamespace = appNamespace;
	}

}