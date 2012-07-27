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
package de.tudresden.inf.rn.mobilis.services.xhunt.state;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;


/**
 * The Class SubGameState is used to declare an inner state of the 
 * abstract state @see de.tudresden.inf.rn.mobilis.server.services.xhunt.state#GameState.
 */
public abstract class SubGameState {

	/**
	 * Process an XMPP packet.
	 *
	 * @param bean the bean which should be processed
	 */
	public abstract void processPacket(XMPPBean bean);
	
}
