package de.tudresden.inf.rn.mobilis.mxa.xmpp;

import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.mxa.util.XmlBuilder;

public class MessageExtensionProvider implements PacketExtensionProvider{

	public String namespace;
	public String elementName;

	public MessageExtensionProvider(String namespace, String elementName) {
		this.namespace = namespace;
		this.elementName = elementName;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getElementName() {
		return this.elementName;
	}

	
	
	@Override
	public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		List<String> stack = new LinkedList<String>();
		XmlBuilder xml = new XmlBuilder();
		boolean done = false;
		
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
				break;
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
		return new MessageExtension(this.elementName, this.namespace, xml.toString());
	}

}
