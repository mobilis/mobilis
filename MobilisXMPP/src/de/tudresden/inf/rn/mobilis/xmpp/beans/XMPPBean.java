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

package de.tudresden.inf.rn.mobilis.xmpp.beans;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * @author Benjamin Söllner, Robert Lübke
 */
public abstract class XMPPBean implements Cloneable, XMPPInfo {
	
	private static final long serialVersionUID = 1L;
	public static final int TYPE_SET = 0;
	public static final int TYPE_GET = 1;
	public static final int TYPE_RESULT = 2;
	public static final int TYPE_ERROR  = 3;
	
	public static int currentId = 0;
	
	protected int type;
	protected String id;
	protected String from;
	protected String to;
	
	public String errorType, errorCondition, errorText;
	
	public void setType(int type) { this.type = type; }
	public void setId(String id) { this.id = id; }
	public void setFrom(String from) { this.from = from; }
	public void setTo(String to) { this.to = to; }
	
	public int getType() { return this.type; }
	public String getId() { return this.id; }
	public String getFrom() { return this.from; }
	public String getTo() { return this.to; }

	public XMPPBean() {
		this.id = "mobilis_"+XMPPBean.currentId; 
		XMPPBean.currentId++; 
	}
	
	/**
	 * Constructor for type=ERROR. For more information about the parameters
	 * of an error IQ see http://xmpp.org/rfcs/rfc3920.html#stanzas.
	 * @param errorType Error type
	 * @param errorCondition Error condition
	 * @param errorText descriptive error text
	 */
	public XMPPBean(String errorType, String errorCondition, String errorText) {
		this.id = "mobilis_"+XMPPBean.currentId; 
		XMPPBean.currentId++; 
		
		this.errorType=errorType;
		this.errorCondition=errorCondition;
		this.errorText=errorText;
		this.type=XMPPBean.TYPE_ERROR;
	}

	/**
	 * Appends XML Payload information about an error to the given StringBuilder
	 * @param sb
	 * @return the changed StringBuilder
	 */
	public StringBuilder appendErrorPayload(StringBuilder sb) {
		//Error element:
		if (this.errorCondition!=null && this.errorText!=null && this.errorType!=null) {
			sb.append("<error type=\""+errorType+"\">")
				  	.append("<"+errorCondition+" xmlns=\""+Mobilis.NAMESPACE_ERROR_STANZA+"\" />")
					.append("<text xmlns=\""+Mobilis.NAMESPACE_ERROR_STANZA+"\">")
						.append(errorText)
					.append("</text>")
				.append("</error>");	  
		}		
		return sb;		
	}
	
	public XMPPBean cloneBasicAttributes(XMPPBean twin) {
		twin.errorCondition=this.errorCondition;
		twin.errorText=this.errorText;
		twin.errorType=this.errorType;
		
		twin.id = this.id;
		twin.from = this.from;
		twin.to = this.to;
		twin.type = this.type;
		return twin;
	}
	
	/**
	 * Parses and saves the error attributes (type, condition and text).
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public XmlPullParser parseErrorAttributes(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getAttributeName(0).equals("type"))
			errorType = parser.getAttributeValue(0);
		parser.next();
		//Now the parser is at START_TAG of error condition
		errorCondition=parser.getName();
		parser.next(); parser.next();
		//Now the parser is at START_TAG of error text
		errorText=parser.nextText();
		return parser;
	}

	public String toXML() {
		String childElement = this.getChildElement();
		String namespace = this.getNamespace(); 
		return new StringBuilder()
				.append("<")
				.append(childElement)
				.append(" xmlns=\"")
				.append(namespace)
				.append("\">")
				.append(this.payloadToXML())
				.append("</")
				.append(childElement)
				.append(">")
				.toString();
	}
	
	public abstract XMPPBean clone();
	
	/**
	 * Converts all payload information into XML format.
	 * @return XML representation of the payload.
	 */
	public abstract String payloadToXML();
	
	public String toString() {
		String type="no type";
		switch (this.type) {
		case XMPPBean.TYPE_GET : type="GET"; break;
		case XMPPBean.TYPE_SET : type="SET"; break;
		case XMPPBean.TYPE_RESULT : type="RESULT"; break;
		case XMPPBean.TYPE_ERROR : type="ERROR"; break;
		}		
		return "packetID:"+id+" type:"+type+ "childelement:"+this.getChildElement();
	}
}
