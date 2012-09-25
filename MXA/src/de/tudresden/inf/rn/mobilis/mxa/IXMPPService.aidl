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

package de.tudresden.inf.rn.mobilis.mxa;

import android.os.Messenger;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPMessage;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPIQ;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPPresence;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IXMPPMessageCallback;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IXMPPIQCallback;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IConnectionCallback;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IChatStateCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.servicediscovery.IServiceDiscoveryService;
import de.tudresden.inf.rn.mobilis.mxa.services.filetransfer.IFileTransferService;
import de.tudresden.inf.rn.mobilis.mxa.services.multiuserchat.IMultiUserChatService;
import de.tudresden.inf.rn.mobilis.mxa.services.pubsub.IPubSubService;
import de.tudresden.inf.rn.mobilis.mxa.services.sessionmobility.ISessionMobilityService;
import de.tudresden.inf.rn.mobilis.mxa.services.serverlessmessaging.IServerlessMessagingService;
import de.tudresden.inf.rn.mobilis.mxa.services.messagecarbons.IMessageCarbonsService;

interface IXMPPService {

    /**
     * Connect to the XMPP server.
     */
    void connect(in Messenger acknowledgement);
	
	/**
	 * Only disconnects if all binders have been removed.
	 */
    void disconnect(in Messenger acknowledgement);
	
	/**
	 * Returns whether the service is connected or not.
	 */
	boolean isConnected();
	
	/**
	 * Returns the JID of the logged in XMPP user.
	 */
	String getUsername();
	
	/**
	 * param requestCode: Reply request code. < 0 if reply is not requested.
	 */
	void sendMessage(in Messenger acknowledgement, in int requestCode, in XMPPMessage message);

    /**
     * Sends an XMPP Info/Query packet. 
     * param acknowledgement: Messenger which is notified upon the delivery of the IQ
     * param result: Messenger which is notified upon the arrival of the correspondent result IQ
     * param requestCode: ...
     * param iq: the IQ to be sent
     */
    void sendIQ(in Messenger acknowledgement, in Messenger result, in int requestCode, in XMPPIQ iq);
    
    /**
     * Sends an XMPP Info/Query packet in fire-and-forget semantics, the type is ignored.
     * No information will be received for this packet. 
     * param acknowledgement: Messenger which is notified upon the delivery of the IQ, means if the iq could be sent trough
     * the library and left the device
     * param iq: the IQ to be sent
     */
    void sendIQInMessage(in Messenger acknowledgement, in XMPPIQ iq);
    
    /**
     * Sends a Presence stanza to the XMPP server including Presence mode (e.g. 'online' or 'do not
     * disturb' and status (e.g. 'on the beach').
     */
    void sendPresence(in Messenger acknowledgement, in int requestCode, in XMPPPresence presence);

    /**
     * Registers a callback interface with the service that will be notified
     * upon connection changes.
     */
    void registerConnectionCallback(IConnectionCallback cb);
    
    /**
     * Remove a previously registered connection callback interface.
     */
    void unregisterConnectionCallback(IConnectionCallback cb);
    
    /**
     * Registers a callback interface with the service that will be notified
     * upon new data messages (i.e. those who have an mxa-ident packet extension).
     */
    void registerDataMessageCallback(IXMPPMessageCallback cb, in String namespace, in String token);
    
    /**
     * Removes a previously registered data message callback interface.
     */
    void unregisterDataMessageCallback(IXMPPMessageCallback cb, in String namespace, in String token);
    
    /**
     * Registers a callback interface with the service that will be notified
     * upon new IQ messages.
     *
     * @param namespace: The namespace for filtering incoming IQ.
     * @param token: The token for filtering incoming IQ. // isn't it elementName?!? -> query 
     */
    void registerIQCallback(IXMPPIQCallback cb, in String elementName, in String namespace);
    
    /**
     * Removes a previously registered IQ callback interface.
     */
    void unregisterIQCallback(IXMPPIQCallback cb, in String elementName, in String namespace);
    
    /**
     * Gets the parameters of the XMPP Connection, that were given by the user in MXA's XMPP preferences.
     */
    Bundle getXMPPConnectionParameters();
    
    
    // ==========================================================
    // Chat state
    // ==========================================================
    
    /**
     * Register a callback for chatstates corresponding to a fully qualified JID.
     */
    void registerChatStateCallback(IChatStateCallback cb, in String jid);
    
    /**
     * Unregisters a previously registered chatstate callback
     */
    void unregisterChatStateCallback(IChatStateCallback cb, in String jid);
    
    // ==========================================================
	// Additional Services
	// ==========================================================
	
	/**
    * A service used for XMPP Service Discovery.
    */
    IServiceDiscoveryService getServiceDiscoveryService();
	
	/**
	 * A basic File-Transfer service based on XMPP.
	 */
	IFileTransferService getFileTransferService();
	
	/**
	 * The MultiUserChat service conforms to XEP-0045.
	 */
	IMultiUserChatService getMultiUserChatService();
	
	/**
	 * A basic publish-subscribe service for general purposes.
	 */
	IPubSubService getPubSubService();
	
	/**
	 * A service used for Session Mobility.
	 */
	ISessionMobilityService getSessionMobilityService();
	
	/**
	 * A service used for serverless ad-hoc messaging.
	 */
	IServerlessMessagingService getServerlessMessagingService();
	
	/**
	 * A service for XEP-280 Message Carbons.
	 */
	IMessageCarbonsService getMessageCarbonsService();

}
