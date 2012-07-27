/**
 * Copyright (C) 2009 Technische Universität Dresden
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

package de.tudresden.inf.rn.mobilis.mxa.xmpp;

import org.jivesoftware.smack.packet.IQ;

import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPIQ;

/**
 * An IQImpl object contains all the information to describe an incoming/sent IQ
 * stanza, including its XML content.
 * @author Benjamin Söllner
 */
public class IQImpl extends IQ {

	// members
	private String childElementXML;
	private String childElementName;
	private String childNamespace;

	public IQImpl() {
		super();
	}

	public IQImpl(String childElementXML) {
		this();
		this.childElementXML = childElementXML;
	}

	public IQImpl(String childElementName, String childNamespace,
			String childElementXML) {
		this(childElementXML);
		this.childElementName = childElementName;
		this.childNamespace = childNamespace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jivesoftware.smack.packet.IQ#getChildElementXML()
	 */
	@Override
	public String getChildElementXML() {
		StringBuilder sb = new StringBuilder();
		if (this.childElementName != null) {
			sb.append("<").append(this.childElementName);
			if (this.childNamespace != null)
				sb.append(" xmlns=\"").append(this.childNamespace).append("\"");
			sb.append(">");
		}
		sb.append(this.childElementXML);
		if (this.childElementName != null)
			sb.append("</").append(this.childElementName).append(">");
		return sb.toString();
	}

	public String getChildNamespace() {
		return this.childNamespace;
	}

	public String getChildElementName() {
		return this.childElementName;
	}

	public XMPPIQ toXMPPIQ() {
		IQ.Type typeFrom = this.getType();
		int typeTo = XMPPIQ.TYPE_SET;
		if (typeFrom == IQ.Type.SET)
			typeTo = XMPPIQ.TYPE_SET;
		else if (typeFrom == IQ.Type.GET)
			typeTo = XMPPIQ.TYPE_GET;
		else if (typeFrom == IQ.Type.ERROR)
			typeTo = XMPPIQ.TYPE_ERROR;
		else if (typeFrom == IQ.Type.RESULT)
			typeTo = XMPPIQ.TYPE_RESULT;
		XMPPIQ iq = new XMPPIQ(this.getFrom(), this.getTo(), typeTo,
				this.childElementName, this.childNamespace,
				this.childElementXML);
		iq.packetID = this.getPacketID();
		return iq;
	}

	public void fromXMPPIQ(XMPPIQ iq) {
		switch (iq.type) {
		case XMPPIQ.TYPE_GET:
			this.setType(IQ.Type.GET);
			break;
		case XMPPIQ.TYPE_SET:
			this.setType(IQ.Type.SET);
			break;
		case XMPPIQ.TYPE_ERROR:
			this.setType(IQ.Type.ERROR);
			break;
		case XMPPIQ.TYPE_RESULT:
			this.setType(IQ.Type.RESULT);
			break;
		}
		if (iq.from != null)
			this.setFrom(iq.from);
		if (iq.to != null)
			this.setTo(iq.to);
		if (iq.packetID != null)
			this.setPacketID(iq.packetID);
		if (iq.namespace != null)
			this.childNamespace = iq.namespace;
		if (iq.element != null)
			this.childElementName = iq.element;
		if (iq.payload != null)
			this.childElementXML = iq.payload;
	}
}
