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
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.v1.XmlPullParser;

/**
 * An MXAIdentExtensionProvider parses the MXA ident PacketExtension of an
 * incoming DataMessage.
 * 
 * @author Istvan Koren
 * 
 */
public class MXAIdentExtensionProvider implements PacketExtensionProvider {

	/**
	 * Creates a new MXAIdentExtensionProvider. ProviderManager requires that
	 * every PacketExtensionProvider has a public, no-argument constructor
	 */
	public MXAIdentExtensionProvider() {
	}

	/** Installs the provider. */
	public static void install(ProviderManager manager) {
		manager.addExtensionProvider(MXAIdentExtension.ELEMENT_NAME,
				MXAIdentExtension.NAMESPACE, new MXAIdentExtensionProvider());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jivesoftware.smack.provider.PacketExtensionProvider#parseExtension
	 * (org.xmlpull.v1.XmlPullParser)
	 */
	@Override
	public PacketExtension parseExtension(XmlPullParser parser)
			throws Exception {
		MXAIdentExtension mie = new MXAIdentExtension();

		boolean done = false;
		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("token")) {
					mie.setToken(parser.nextText());
				} else if (parser.getName().equals("namespace")) {
					mie.setConsumerNamespace(parser.nextText());
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals(mie.getElementName())) {
					done = true;
				}
			}
		}

		return mie;
	}

}
