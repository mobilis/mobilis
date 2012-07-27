/**
 * Copyright (C) 2009 Technische Universit�t Dresden
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

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.pubsub.packet.PubSub;

/**
 * An IQImplFilter filters an incoming IQ stanza upon its elementName and
 * namespace.
 * 
 * @author Istvan Koren
 * 
 */
public class IQImplFilter implements PacketFilter {

	// members
	private String elementName;
	private String namespace;

	public IQImplFilter(String elementName, String namespace) {
		this.elementName = elementName;
		this.namespace = namespace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jivesoftware.smack.filter.PacketFilter#accept(org.jivesoftware.smack
	 * .packet.Packet)
	 */
	@Override
	public boolean accept(Packet p) {
		if (p instanceof IQImpl) {
			if (((IQImpl) p).getChildElementName().equalsIgnoreCase(elementName)
					&& ((IQImpl) p).getChildNamespace().equalsIgnoreCase(namespace)) {
				return true;
			}
		}
		if (p instanceof PubSub) {
			if (((PubSub) p).getElementName().equalsIgnoreCase(elementName)
					&& ((PubSub) p).getNamespace().equalsIgnoreCase(namespace)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof IQImplFilter) {
			return ((((IQImplFilter) o).elementName.equals(elementName)) && (((IQImplFilter) o).namespace
					.equals(namespace)));
		}
		return false;
	}
	
	public String toString() {
		return "IQImplFilter. elementeName="+elementName+" namespace="+namespace;
	}

}
