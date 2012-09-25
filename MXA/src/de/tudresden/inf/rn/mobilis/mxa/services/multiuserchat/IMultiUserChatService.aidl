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

package de.tudresden.inf.rn.mobilis.mxa.services.multiuserchat;

import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPMessage;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IInvitationCallback;

interface IMultiUserChatService {

    /**
     * Sends a message to the whole group.
     */
    void sendGroupMessage(in String roomID, in XMPPMessage msg);
    
    /**
     * Gets all members of the provided group.
     */
    List<String> getMembers(in String roomID);
    
    /**
     * Registers a callback for invitations.
     */
    void registerInvitationCallback(IInvitationCallback cb);
    
    /**
     * Unregisters a previously registered invitation callback.
     */
    void unregisterInvitationCallback(IInvitationCallback cb);
    
    /**
     * Changes the nickname within the room.
     */
    boolean changeNickname(String roomID, String nickname);
    
    /**
     * Declines a previously received invitation.
     */
    void declineInvitation(String roomID, String inviter, String reason);
    
    /**
     * Accept Invation and join a room.
     */
    boolean acceptInvation(String roomID, String password);
    
    /**
     * Join a room.
     */
    boolean joinRoom(String roomID, String password);
    
     /**
     * Leave a room.
     */
    void leaveRoom(String roomID);
    
    /**
     * Get Information about a room. 
     * boolean ConstMXA.MUC_IS_MODERATED
     * boolean ConstMXA.MUC_IS_MEMEBERSONLY
	 * boolean ConstMXA.MUC_IS_PASSWORDPROTECTED
	 * boolean ConstMXA.MUC_IS_PERSISTENT
	 * String ConstMXA.MUC_DESCRIPTION
	 * String ConstMXA.MUC_SUBJECT
	 * int ConstMXA.MUC_OCCUPANTSCOUNT
     */
    Bundle getRoomInfo(String roomID);
    
    /**
     * Create a new chat room. 
     */
    boolean createRoom(String roomID, String nickname);
    
    /**
     * Returns a list of all chat rooms that the user joined.
     */
    List<String> getJoinedRooms();
    
    /**
     * Kicks a visitor or participant from the room.
     */  
    boolean kickParticipant(String roomID, String nickname, String reason);
     
    /**
     * Invites another user to the room in which one is an occupant. The invitation
     * will be sent to the room which in turn will forward the invitation to the invitee.
	 * If the room is password-protected, the invitee will receive a password to use to
	 * join the room. If the room is members-only, the the invitee may be added to
	 * the member list.
     */  
    boolean inviteUser(String roomID, String userJID, String reason);
    
}