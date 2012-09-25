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

package de.tudresden.inf.rn.mobilis.migration.chat.beans;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class MigrationRequest extends XMPPBean {

	public MigrationRequest() {
		this.setType(XMPPBean.TYPE_RESULT);
	}

	@Override
	public void fromXML(XmlPullParser parser) throws Exception {
	}

	public static final String CHILD_ELEMENT = "MigrationRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilismigration:iq:request";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		MigrationRequest clone = new MigrationRequest();
		clone.cloneBasicAttributes(clone);

		return clone;
	}

	@Override
	public String payloadToXML() {
		return "";
	}

}