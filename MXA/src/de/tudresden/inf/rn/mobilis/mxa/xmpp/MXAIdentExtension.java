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

import org.jivesoftware.smack.packet.PacketExtension;

/**
 * This PacketExtension is being added and removed from the MXA for identifying
 * service consumers reciprocally.
 * 
 * @author Istvan Koren
 * 
 */
public class MXAIdentExtension implements PacketExtension {

	public static final String ELEMENT_NAME = "x";
	public static final String NAMESPACE = "mxa:ident";

	/*
	 * The token to identify the consumer. E.g., token can be negotiated
	 * directly by calling app.
	 */
	private String token;
	private String consumerNamespace;

	public MXAIdentExtension() {
	}

	public MXAIdentExtension(String consumerNamespace, String token) {
		this.consumerNamespace = consumerNamespace;
		this.token = token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jivesoftware.smack.packet.PacketExtension#getElementName()
	 */
	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jivesoftware.smack.packet.PacketExtension#getNamespace()
	 */
	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jivesoftware.smack.packet.PacketExtension#toXML()
	 */
	@Override
	public String toXML() {
		final StringBuffer buf = new StringBuffer();
		buf.append("");

		buf.append("<token>");
		buf.append(token);
		buf.append("</token>");

		buf.append("");
		return buf.toString();
	}

	public String getConsumerNamespace() {
		return consumerNamespace;
	}

	public void setConsumerNamespace(String consumerNamespace) {
		this.consumerNamespace = consumerNamespace;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MXAIdentExtension) {
			MXAIdentExtension mie = (MXAIdentExtension) o;
			return ((consumerNamespace.equals(mie.getConsumerNamespace())) && (token
					.equals(mie.getToken())));
		}

		return false;
	}
}
