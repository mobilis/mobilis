/**
 * Copyright (C) 2010 Technische Universität Dresden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 */

package de.tudresden.inf.rn.mobilis.xmpp.beans.coordination;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

/**
 * 
 * @author Robert Lübke
 */
public class MobilisServiceInfo implements XMPPInfo, Cloneable {

	private static final long serialVersionUID = 1L;
	public static final String CHILD_ELEMENT = "mobilisService";
	public static final String NAMESPACE = Mobilis.NAMESPACE;

	private String namespace, version, mode, jid, serviceName;
	private int instances;
		
	
	public MobilisServiceInfo() {
		this.instances=Integer.MIN_VALUE;
	}	
	
	/**
	 * Constructor for an InfoItem about a MobilisService with type="single".
	 * @param namespace Namespace of the MobilisService
	 * @param version version of the MobilisService
	 * @param jid XMPP-ID of the MobilisService
	 */
	public MobilisServiceInfo(String namespace, String version, String jid) {
		this.namespace=namespace;
		this.version=version;
		this.mode="single";
		this.jid=jid;
		this.instances=Integer.MIN_VALUE;
	}
	
	/**
	 * Constructor for an InfoItem about a MobilisService with type="multi".
	 * @param namespace  Namespace of the MobilisService
	 * @param instances Number of currently running instances of this MobilisService
	 */
	public MobilisServiceInfo(String namespace, int instances) {
		this.namespace=namespace;
		this.mode="multi";
		this.instances=instances;
	}	
	
	@Override
	public MobilisServiceInfo clone() {		
		MobilisServiceInfo twin = new MobilisServiceInfo(this.namespace, this.version, this.jid);
		twin.serviceName=this.serviceName;
		twin.mode=this.mode;
		twin.instances=this.instances;
		return twin;
	}
	
	@Override
	public void fromXML(XmlPullParser parser) throws Exception {
		String childElement = MobilisServiceInfo.CHILD_ELEMENT;
		
		boolean done = false;

		do {
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				if (tagName.equals(childElement)) {
					for (int i = 0; i < parser.getAttributeCount(); i++)
						if (parser.getAttributeName(i).equals("namespace"))
							this.namespace = parser.getAttributeValue(i);	
						else if (parser.getAttributeName(i).equals("version"))
							this.version = parser.getAttributeValue(i);	
						else if (parser.getAttributeName(i).equals("serviceName"))
							this.serviceName = parser.getAttributeValue(i);	
						else if (parser.getAttributeName(i).equals("mode"))
							this.mode = parser.getAttributeValue(i);	
						else if (parser.getAttributeName(i).equals("instances"))
							this.instances = Integer.parseInt(parser.getAttributeValue(i));	
						else if (parser.getAttributeName(i).equals("jid"))
							this.jid = parser.getAttributeValue(i);	
					parser.next();				
				} else
					parser.next();
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals(childElement))
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
		String childElement = MobilisServiceInfo.CHILD_ELEMENT;
		
		StringBuilder sb = new StringBuilder()
				.append("<").append(childElement).append(" ");
				
		if (this.namespace!=null) sb.append("namespace=\"").append(this.namespace).append("\" ");
		if (this.version!=null) sb.append("version=\"").append(this.version).append("\" ");
		if (this.mode!=null) sb.append("mode=\"").append(this.mode).append("\" ");
		if (this.instances>Integer.MIN_VALUE) sb.append("instances=\"").append(this.instances).append("\" ");
		if (this.jid!=null) sb.append("jid=\"").append(this.jid).append("\" ");
		if (this.serviceName!=null) sb.append("serviceName=\"").append(this.serviceName).append("\" ");
						
		sb.append(" />");				
		
		return sb.toString();
	}

	@Override
	public String getChildElement() {
		return MobilisServiceInfo.CHILD_ELEMENT;
	}

	@Override
	public String getNamespace() {
		return MobilisServiceInfo.NAMESPACE;
	}

	/**
	 * @return the version of the MobilisService
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version of the MobilisService to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the mode of the MobilisService
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode the mode of the MobilisService to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * @return the XMPP-ID of the MobilisService
	 */
	public String getJid() {
		return jid;
	}

	/**
	 * @param jid the XMPP-ID of the MobilisService to set
	 */
	public void setJid(String jid) {
		this.jid = jid;
	}

	/**
	 * @return the number of currently active instances of this MobilisService
	 */
	public int getInstances() {
		return instances;
	}

	/**
	 * @param instances the number of currently active instances to set
	 */
	public void setInstances(int instances) {
		this.instances = instances;
	}

	/**
	 * @param namespace the namespace of the MobilisService to set
	 */
	public void setServiceNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	/**
	 * @return the namespace of the MobilisService
	 */
	public String getServiceNamespace() {
		return this.namespace;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}
		
}
