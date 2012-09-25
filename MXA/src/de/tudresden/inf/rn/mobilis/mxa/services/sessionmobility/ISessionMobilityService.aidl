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

package de.tudresden.inf.rn.mobilis.mxa.services.sessionmobility;

import android.os.Messenger;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IResourcesCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.ITransferCallback;

interface ISessionMobilityService {

	/**
	 * Gets a list of all resource-qualified endpoints currently connected with
	 * the same JID.
	 * @param resourcesCallback the callback to be called after the list of resources
	 * has been retrieved
	 */
	void queryResources(IResourcesCallback resourcesCallback);
	
	/**
	 * Sends a SessionInvite-IQ to the specified JID with the given app URI and
	 * parameters.
	 * @param jid the invitee the invitation is sent to
	 * @param appuri a nonambiguous application URI
	 * @param params a list of parameters that should enable the invitee to identify the
	 * current session
	 */
	void inviteToSession(String jid, String appuri, in List<String> params);
	
	/**
	 * Sends a MigrateSession-IQ to the specified JID with the given app URI.
	 * @param jid the session transfer target
	 * @param appuri a nonambiguous application URI
	 * @param mechanisms a list of supported mechanisms. Can be INBAND-XMPP, FILES and
	 * BUNDLE-COMPRESSED-ZIP
	 * @param transferCallback after the transfer negotation is finished, this callback gets notified
	 */
	void requestTransfer(String jid, String appuri, in List<String> mechanisms, ITransferCallback transferCallback);
	
	/**
	 * Accepts a session transfer request.
	 * @param jid the session transfer target
	 * @param appuri a nonambiguous application URI
	 * @param mechanisms a list of accepted mechanisms. Can be INBAND-XMPP, FILES and
	 * BUNDLE-COMPRESSED-ZIP
	 */
	void acceptTransfer(String jid, String appuri, in List<String> mechanisms);
	
	/**
	 * Called to register an intent action that gets called when a session invitation request
	 * reaches this client.
	 * @param a nonambiguous application URI
	 * @param the action of the broadcast intent that gets sent when an invitation arrives
	 */
	void registerInvitationIntent(String appuri, String action);
	
	/**
	 * Called to register an intent action that gets called when a session transfer request
	 * reaches this client.
	 * @param a nonambiguous application URI
	 * @param the action of the broadcast intent that gets sent when a transfer request arrives
	 */
	void registerTransferIntent(String appuri, String intent);
	
	/**
	 * This is an auxiliary method to send all messages sent and received after the supplied time.
	 * @param jid the session state transfer target
	 * @param startFrom the time in milliseconds
	 */
	void sendMessageHistory(String jid, long startFrom);
}