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
 * 
 * Contains parts of Android Email App (C) 2008 The Android Open Source Project
 */

/**
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 */

package de.tudresden.inf.rn.mobilis.mxa.util;

/**
 * A utility class that builds a String upon XML types.
 * @author Benjamin Söllner
 */
public class XmlBuilder {

	private StringBuilder xmlBuffer = new StringBuilder();

	@Override
	public String toString() {
		return xmlBuffer.toString();
	}

	public XmlBuilder writeComment(String text) {
		this.xmlBuffer.append("<!--").append(text).append("-->");
		return this;
	}

	public XmlBuilder writeText(String text) {
		this.xmlBuffer.append(text);
		return this;
	}

	public XmlBuilder writeEntityRef(String text) {
		this.xmlBuffer.append("&").append(text).append(";");
		return this;
	}

	public XmlBuilder writeCdSect(String text) {
		this.xmlBuffer.append("<![CDATA[").append(text).append("]]>");
		return this;
	}

	public XmlBuilder writeDocDecl(String text) {
		this.xmlBuffer.append("<!DOCTYPE").append(text).append(">");
		return this;
	}

	public XmlBuilder writeProcessingInstruction(String text) {
		this.xmlBuffer.append("<?").append(text).append("?>");
		return this;
	}

	public XmlBuilder writeStartTag(String prefix, String elementName,
			String[] attributePrefixes, String[] attributeNames,
			String[] attributeValues) {
		StringBuilder xml = this.xmlBuffer;
		xml.append("<");
		if (prefix != null)
			xml.append(prefix).append(":");
		xml.append(elementName);
		for (int i = 0; i < attributeNames.length; i++) {
			xml.append(" ");
			if (attributePrefixes[i] != null)
				xml.append(attributePrefixes[i]).append(":");
			xml.append(attributeNames[i]).append("=\"").append(
					attributeValues[i]).append("\"");
		}
		xml.append(">");
		return this;
	}

	public XmlBuilder writeEndTag(String prefix, String elementName) {
		StringBuilder xml = this.xmlBuffer;
		xml.append("</");
		if (prefix != null)
			xml.append(prefix).append(":");
		xml.append(elementName).append(">");
		return this;
	}
}