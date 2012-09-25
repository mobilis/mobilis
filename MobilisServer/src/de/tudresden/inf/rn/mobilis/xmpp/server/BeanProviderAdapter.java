/*******************************************************************************
 * Copyright (C) 2010 Technische Universit�t Dresden
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 ******************************************************************************/
package de.tudresden.inf.rn.mobilis.xmpp.server;

import java.util.logging.Level;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.server.MobilisManager;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class BeanProviderAdapter implements IQProvider {
	
	private XMPPBean prototype;
	
	public BeanProviderAdapter(XMPPBean prototype) {
		this.prototype = prototype;
	}
	
	@Override
	public IQ parseIQ(XmlPullParser parser) throws Exception {		
		XMPPBean bean = this.prototype.clone();
		
		try{
			bean.fromXML(parser);
		} catch (Exception e) {
			MobilisManager.getLogger().log( Level.WARNING, String.format( "Error while parsing bean prototype=%s error=%s",
					bean.toString(),
					e.getMessage()) );
		}
		
		return new BeanIQAdapter(bean);
	}

	public void addToProviderManager() {		
		ProviderManager.getInstance().addIQProvider(
				this.prototype.getChildElement(),
				this.prototype.getNamespace(),
				this);
	}
	
	
}
