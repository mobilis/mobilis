package de.tudresden.inf.rn.mobilis.xmpp.beans.admin;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

public class AgentConfigInfo implements XMPPInfo{

	private static final long serialVersionUID = -8998689946541998742L;
	public static final String NAMESPACE = Mobilis.NAMESPACE + "#services/AdminService/AgentConfigInfo";
	public static final String CHILD_ELEMENT = "agentConfig";
	
	private String _xmlTag_Description = "description";
	private String _xmlTag_Host = "host";
	private String _xmlTag_Mode = "mode";
	private String _xmlTag_Name = "name";
	private String _xmlTag_Password = "password";
	private String _xmlTag_Port = "port";
	private String _xmlTag_Resource = "resource";
	private String _xmlTag_Service = "service";
	private String _xmlTag_Start = "start";
	private String _xmlTag_Type = "type";
	private String _xmlTag_Username = "username";
	
	public String Description = null;
	public String Host = null;
	public String Mode = null;
	public String Name = null;
	public String Password = null;
	public String Port = null;
	public String Resource = null;
	public String Service = null;
	public String Start = null;
	public String Type = null;
	public String Username = null;
	
	
	public AgentConfigInfo() {
		super();
	}
	
	public AgentConfigInfo(String description, String host, String mode, String name, 
			String password, String port, String resource,
			String service, String start,String type, 
			String username) {
		super();
		
		this.Description = description;
		this.Host = host;
		this.Mode = mode;
		this.Name = name;
		this.Password = password;
		this.Port = port;
		this.Resource = resource;
		this.Service = service;
		this.Start = start;
		this.Type = type;
		this.Username = username;
	}
	

	@Override
	public AgentConfigInfo clone() {
		return Service != null
				? new AgentConfigInfo(Description, Host, Mode, Name, Password,
						Port, Resource, Service, Start, Type, Username)
				: new AgentConfigInfo();
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
				} else if (tagName.equals(_xmlTag_Description))
					this.Description = parser.nextText();
				else if (tagName.equals(_xmlTag_Host))
					this.Host = parser.nextText();
				else if (tagName.equals(_xmlTag_Mode))
					this.Mode = parser.nextText();
				else if (tagName.equals(_xmlTag_Name))
					this.Name = parser.nextText();
				else if (tagName.equals(_xmlTag_Password))
					this.Password = parser.nextText();
				else if (tagName.equals(_xmlTag_Port))
					this.Port = parser.nextText();
				else if (tagName.equals(_xmlTag_Resource))
					this.Resource = parser.nextText();
				else if (tagName.equals(_xmlTag_Service))
					this.Service = parser.nextText();
				else if (tagName.equals(_xmlTag_Start))
					this.Start = parser.nextText();
				else if (tagName.equals(_xmlTag_Type))
					this.Type = parser.nextText();
				else if (tagName.equals(_xmlTag_Username))
					this.Username = parser.nextText();
				else
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
	public String toXML() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<").append(CHILD_ELEMENT).append(">");		
		if(null != this.Description){
			sb.append("<" + _xmlTag_Description + ">")
				.append(this.Description)
				.append("</" + _xmlTag_Description + ">");
		}
			
		if(null != this.Host){
			sb.append("<" + _xmlTag_Host + ">")
				.append(this.Host)
				.append("</" + _xmlTag_Host + ">");
		}
			
		if(null != this.Mode){
			sb.append("<" + _xmlTag_Mode + ">")
				.append(this.Mode)
				.append("</" + _xmlTag_Mode + ">");
		}
			
		if(null != this.Name){
			sb.append("<" + _xmlTag_Name + ">")
				.append(this.Name)
				.append("</" + _xmlTag_Name + ">");
		}
		
		if(null != this.Password){
			sb.append("<" + _xmlTag_Password + ">")
				.append(this.Password)
				.append("</" + _xmlTag_Password + ">");
		}
		
		if(null != this.Port){
			sb.append("<" + _xmlTag_Port + ">")
				.append(this.Port)
				.append("</" + _xmlTag_Port + ">");
		}
		
		if(null != this.Resource){
			sb.append("<" + _xmlTag_Resource+ ">")
				.append(this.Resource)
				.append("</" + _xmlTag_Resource + ">");
		}
		
		if(null != this.Service){
			sb.append("<" + _xmlTag_Service + ">")
				.append(this.Service)
				.append("</" + _xmlTag_Service + ">");
		}
		
		if(null != this.Start){
			sb.append("<" + _xmlTag_Start+ ">")
				.append(this.Start)
				.append("</" + _xmlTag_Start + ">");
		}
		
		if(null != this.Type){
			sb.append("<" + _xmlTag_Type + ">")
				.append(this.Type)
				.append("</" + _xmlTag_Type + ">");
		}
		
		if(null != this.Username){
			sb.append("<" + _xmlTag_Username + ">")
				.append(this.Username)
				.append("</" + _xmlTag_Username + ">");
		}		
		sb.append("</").append(CHILD_ELEMENT).append(">");
		
		return sb.toString();
	}

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

}