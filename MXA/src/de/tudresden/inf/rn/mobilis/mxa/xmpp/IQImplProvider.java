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

import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.mxa.util.XmlBuilder;

/**
 * An IQImplProvider parses an incoming IQ stanzas and returns an IQImpl object
 * containing the child XML as String.
 * 
 * @author Benjamin Söllner
 * 
 */
public class IQImplProvider implements IQProvider {

	public String namespace;
	public String elementName;

	public IQImplProvider(String namespace, String elementName) {
		this.namespace = namespace;
		this.elementName = elementName;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getElementName() {
		return this.elementName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.jivesoftware.smack.provider.IQProvider#parseIQ(org.xmlpull.v1.
	 * XmlPullParser)
	 */
	@Override
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		List<String> stack = new LinkedList<String>();
		XmlBuilder xml = new XmlBuilder();
		boolean done = false;
		// boolean feature =
		// parser.getFeature(XmlPullParser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES);
		// parser.setFeature(XmlPullParser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES,
		// true);
		String prefix, elementName;
		do {
			switch (parser.nextToken()) {
			case XmlPullParser.END_DOCUMENT:
				done = true;
				break;
			case XmlPullParser.COMMENT:
				xml.writeComment(parser.getText());
				break;
			case XmlPullParser.IGNORABLE_WHITESPACE:
			case XmlPullParser.TEXT:
				xml.writeText(parser.getText());
				break;
			case XmlPullParser.ENTITY_REF:
				xml.writeEntityRef(parser.getText());
				break;
			case XmlPullParser.CDSECT:
				xml.writeCdSect(parser.getText());
				break;
			case XmlPullParser.DOCDECL:
				xml.writeDocDecl(parser.getText());
				break;
			case XmlPullParser.PROCESSING_INSTRUCTION:
				xml.writeProcessingInstruction(parser.getText());
				break;
			case XmlPullParser.END_TAG:
				if (stack.isEmpty())
					done = true;
				else {
					prefix = parser.getPrefix();
					elementName = parser.getName();
					xml.writeEndTag(prefix, elementName);
					for (int i = stack.size() - 1; i >= 0; i--)
						if (stack.get(i).equals(
								(prefix != null ? prefix + ":" : "")
										+ elementName)) {
							for (int j = stack.size() - 1; j >= i; j--)
								stack.remove(j);
							break;
						}
				}
				break;
			case XmlPullParser.START_TAG:
				prefix = parser.getPrefix();
				elementName = parser.getName();
				int attributeCount = parser.getAttributeCount();
				String[] attributePrefixes = new String[attributeCount];
				String[] attributeNames = new String[attributeCount];
				String[] attributeValues = new String[attributeCount];
				for (int i = 0; i < attributeCount; i++) {
					attributePrefixes[i] = parser.getAttributePrefix(i);
					attributeNames[i] = parser.getAttributeName(i);
					attributeValues[i] = parser.getAttributeValue(i);
				}
				stack.add((prefix != null ? prefix + ":" : "") + elementName);
				xml.writeStartTag(prefix, elementName, attributePrefixes,
						attributeNames, attributeValues);
				break;
			}
		} while (!done);
		// parser.setFeature(XmlPullParser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES,
		// feature);
		return new IQImpl(this.elementName, this.namespace, xml.toString());
	}

}
