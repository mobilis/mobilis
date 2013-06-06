/*******************************************************************************
 * Copyright (C) 2010 Technische Universität Dresden
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

package de.tudresden.inf.rn.mobilis.consoleclient.bean;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.admin.AgentConfigInfo;
import de.tudresden.inf.rn.mobilis.xmpp.beans.admin.ConfigureServiceBean;

public class TemplateBean {
	
	public static String TEMP_DIR = "tmp";
	
	
	public static XMPPBean createConfigureServiceBeanTreasureHuntService(String toJID) {
		ConfigureServiceBean bean = new ConfigureServiceBean();
		
		bean.AgentConfig = new AgentConfigInfo("TreasureHuntService",
				"localhost", "multi", "TreasureHuntService", "WwmSdzZWf.", "5222", "TreasureHuntService", "xhunt",
				"ondemand", "de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent",
				"mobilis");		
		
		bean.ServiceNamespace = "http://mobilis.inf.tu-dresden.de#services/TreasureHuntService";
		bean.ServiceVersion = 1;
		
		bean.setTo( toJID );
		bean.setType( XMPPBean.TYPE_SET );
		
		return bean;
	}
	
	public static XMPPBean createConfigureServiceBeanTreasureHunt2Service(String toJID) {
		ConfigureServiceBean bean = new ConfigureServiceBean();
		
		bean.AgentConfig = new AgentConfigInfo("TreasureHuntService",
				"localhost", "multi", "TreasureHuntService", "WwmSdzZWf.", "5222", "TreasureHuntService", "xhunt",
				"ondemand", "de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent",
				"mobilis");		
		
		bean.ServiceNamespace = "http://mobilis.inf.tu-dresden.de#services/TreasureHuntService";
		bean.ServiceVersion = 2;
		
		bean.setTo( toJID );
		bean.setType( XMPPBean.TYPE_SET );
		
		return bean;
	}
	
	public static XMPPBean createConfigureServiceBeanXHuntService(String toJID, int version) {
		ConfigureServiceBean bean = new ConfigureServiceBean();
		
		if(version == 2){
			bean.AgentConfig = new AgentConfigInfo("XHuntService",
					"localhost", "multi", "XHuntService", "WwmSdzZWf.", "5222", "XHuntService", "xhunt",
					"ondemand", "de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent",
					"mobilis");		
			
			bean.ServiceVersion = 2;
		}
		else if(version == 3){
			bean.AgentConfig = new AgentConfigInfo("XHuntService",
					"localhost", "multi", "XHuntService3", "WwmSdzZWf.", "5222", "XHuntService", "xhunt",
					"ondemand", "de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent",
					"mobilis");
			
			// mode
			
			bean.ServiceVersion = 3;
		}
		
		bean.ServiceNamespace = "http://mobilis.inf.tu-dresden.de#services/MobilisXHuntService";
		
		
		bean.setTo( toJID );
		bean.setType( XMPPBean.TYPE_SET );
		
		return bean;
	}

}
