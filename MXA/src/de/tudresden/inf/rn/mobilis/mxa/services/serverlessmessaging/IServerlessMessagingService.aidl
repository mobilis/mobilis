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

package de.tudresden.inf.rn.mobilis.mxa.services.serverlessmessaging;

import android.os.Messenger;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPPresence;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IServerlessMessageCallback;

interface IServerlessMessagingService {

	/**
	 * Enables multicast and registers a DNS record in the local network.
	 * @param a presence object defining the status
	 */
    void registerPresence(in XMPPPresence presence);
    
    /**
     * Removes the presence DNS record and closes the multicast interface.
     */
    void unregisterPresence();
    
    /**
     * Registers a callback for getting incoming messages originating from p2p connections.
     * @param callback the message callback to register
     */ 
    void registerMessageCallback(in IServerlessMessageCallback callback);
    
    /**
     * Unregisters a previously registered message callback.
     * @param callback the message callback to unregister
     */
    void unregisterMessageCallback(in IServerlessMessageCallback callback);
    
    /**
     * Gets a list of peers that have been discovered on the local subnetwork. Please note, that
     * it may take a while until all peers are discovered.
     * @param peers a list of strings that gets filled with the peer's names
     */
    void getPeers(out List<String> peers);
    
    void sendMessage(String to, String message);

}