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

import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.packet.Packet;

/**
 * A simple packet extension filter that checks for the correct namespace and
 * token of an mxa-ident packet extension.
 * 
 * @author Istvan Koren
 * 
 */
public class MXAIdentFilter extends PacketExtensionFilter {

	private String namespace;
	private String token;

	public MXAIdentFilter(String namespace, String token) {
		super(MXAIdentExtension.ELEMENT_NAME, MXAIdentExtension.NAMESPACE);
		this.namespace = namespace;
		this.token = token;
	}

	@Override
	public boolean accept(Packet packet) {
		if (super.accept(packet)) {
			MXAIdentExtension mie = (MXAIdentExtension) packet
					.getExtension(MXAIdentExtension.ELEMENT_NAME,
							MXAIdentExtension.NAMESPACE);

			return ((namespace.equals(mie.getConsumerNamespace())) && (token
					.equals(mie.getToken())));
		} else {
			return false;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MXAIdentFilter) {
			return ((((MXAIdentFilter) o).namespace.equals(namespace)) && (((MXAIdentFilter) o).token
					.equals(token)));
		}
		return false;
	}
}
