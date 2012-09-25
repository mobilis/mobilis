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
package de.tudresden.inf.rn.mobilis.server.services;

import de.tudresden.inf.rn.mobilis.server.MobilisManager;

/**
 * Abstract class which represents an Application-specific MobilisService.
 * @author Robert Lübke
 */
public abstract class AppSpecificService extends MobilisService {

	private String password;
	private String serviceName;
	private CoordinatorService coordinator;
	
	public AppSpecificService(CoordinatorService coordinator, String password, String serviceName) {
		super();
		System.out.println("Constructor AppSpecificService");
		this.password=password;
		this.coordinator=coordinator;
		this.serviceName=serviceName;
	}

	public boolean isPasswordProtected() {
		return password!=null;
	}	
	
	public void shutdown() throws Exception {		
		if (!coordinator.removeAppSpecificService(this))
			MobilisManager.getLogger().warning("Could not remove service ("+this.getIdent()+") from coordinators list.");
		super.shutdown();
    }
	
	protected abstract void registerPacketListener();
	
	/**
	 * @return the name of this App Specific Service or null if no name was set.
	 */
	public String getServiceName() {
		return serviceName;
	}
	
}
