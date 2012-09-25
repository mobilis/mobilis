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

package de.tudresden.inf.rn.mobilis.mxa.services.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.mxa.services.parcelable.GeolocItem;

/**
 * Parses incoming XEP-0080 User Location events.
 * 
 * @author Istvan Koren
 * 
 */
public class GeolocItemProvider implements PacketExtensionProvider {

	/** Installs the provider. */
	public static void install(ProviderManager manager) {
		manager.addExtensionProvider(GeolocItem.ELEMENT_NAME,
				GeolocItem.NAMESPACE, new GeolocItemProvider());
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
		GeolocItem gi = new GeolocItem();
		
		boolean done = false;
		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("accuracy")) {
					gi.setAccuracy(Double.parseDouble(parser.nextText()));
				} else if (parser.getName().equals("alt")) {
					gi.setAlt(Double.parseDouble(parser.nextText()));
				} else if (parser.getName().equals("area")) {
					gi.setArea(parser.nextText());
				} else if (parser.getName().equals("bearing")) {
					gi.setBearing(Double.parseDouble(parser.nextText()));
				} else if (parser.getName().equals("building")) {
					gi.setBuilding(parser.nextText());
				} else if (parser.getName().equals("country")) {
					gi.setCountry(parser.nextText());
				} else if (parser.getName().equals("countrycode")) {
					gi.setCountrycode(parser.nextText());
				} else if (parser.getName().equals("datum")) {
					gi.setDatum(parser.nextText());
				} else if (parser.getName().equals("description")) {
					gi.setDescription(parser.nextText());
				} else if (parser.getName().equals("error")) {
					gi.setError(Double.parseDouble(parser.nextText()));
				} else if (parser.getName().equals("floor")) {
					gi.setFloor(parser.nextText());
				} else if (parser.getName().equals("lat")) {
					gi.setLat(Double.parseDouble(parser.nextText()));
				} else if (parser.getName().equals("locality")) {
					gi.setLocality(parser.nextText());
				} else if (parser.getName().equals("long")) {
					gi.setLon(Double.parseDouble(parser.nextText()));
				} else if (parser.getName().equals("postalcode")) {
					gi.setPostalcode(parser.nextText());
				} else if (parser.getName().equals("region")) {
					gi.setRegion(parser.nextText());
				} else if (parser.getName().equals("room")) {
					gi.setRoom(parser.nextText());
				} else if (parser.getName().equals("speed")) {
					gi.setSpeed(Double.parseDouble(parser.nextText()));
				} else if (parser.getName().equals("street")) {
					gi.setStreet(parser.nextText());
				} else if (parser.getName().equals("text")) {
					gi.setText(parser.nextText());
				} else if (parser.getName().equals("timestamp")) {
					gi.setTimestamp(parser.nextText());
				} else if (parser.getName().equals("uri")) {
					gi.setUri(parser.nextText());
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals(gi.getElementName())) {
					done = true;
				}
			}
		}
		
		return gi;
	}

}
