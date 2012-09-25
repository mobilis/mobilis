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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.XMPPRemoteService;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPMessage;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IInvitationCallback;

/**
 * The MultiUserChat service is based on XEP-0045.
 * @author Istvan Koren
 */
public class MultiUserChatService extends Service {

	private static final String TAG = "MultiUserChatService";

	private final XMPPRemoteService mXMPPService;
	private ReaderThread mXMPPReadWorker;
	/**
	 * HashMap with key roomID and value the associated MultiUserChat object.
	 */
	private HashMap<String, MultiUserChat> mMUCRooms;
	/**
	 * Contains the roomID and the associated MUC for all invations, we haven't joined them yet
	 */
	private HashMap<String, MultiUserChat> mMUCInvations;
	
	private final RemoteCallbackList<IInvitationCallback> mInvationCallbacks = new RemoteCallbackList<IInvitationCallback>();

	public MultiUserChatService(XMPPRemoteService service) {
		mXMPPService = service;

		mXMPPReadWorker = new ReaderThread();
		mMUCRooms = new HashMap<String, MultiUserChat>();
		mMUCInvations = new HashMap<String, MultiUserChat>();

		MultiUserChat.addInvitationListener(mXMPPService.getXMPPConnection(),
				mXMPPReadWorker);
	}

	public static String jidWithoutRessource(String jid) {
		int slashIndex = jid.indexOf('/');
		if (slashIndex == -1)
			return jid;
		else
			return jid.substring(0, slashIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent i) {
		return mBinder;
	}

	private final IMultiUserChatService.Stub mBinder = new IMultiUserChatService.Stub() {

		@Override
		public List<String> getJoinedRooms() throws RemoteException {
//			Iterator<String> i = MultiUserChat.getJoinedRooms(
//					mXMPPService.getXMPPConnection(),
//					mXMPPService.getXMPPConnection().getUser());
			
			List<String> list = new ArrayList<String>();				
//			while (i.hasNext())
//				list.add(i.next());	
			for (MultiUserChat muc : mMUCRooms.values())
				if (muc.isJoined())
					list.add(muc.getRoom());
			return list;
		}
		
		@Override
		public boolean kickParticipant(String roomID, String nickname, String reason) throws RemoteException {
			if (mMUCRooms.containsKey(roomID)) {				
				try {
					mMUCRooms.get(roomID).kickParticipant(nickname, reason);
					return true;
				} catch (XMPPException e) {					
					Log.e(TAG, "Could not kick user '"+nickname+"' from room '"+roomID+"' with reason '"+reason+"'");
					e.printStackTrace();					
				}				
			}
			return false;			
		}
		
		@Override
		public Bundle getRoomInfo(String roomID) throws RemoteException {
			Bundle result = null;
			try {				
				RoomInfo ri = MultiUserChat.getRoomInfo(mXMPPService.getXMPPConnection(), roomID);
				if (ri!=null) {					
					result = new Bundle();
					result.putBoolean(ConstMXA.MUC_IS_MODERATED, ri.isModerated());
					result.putBoolean(ConstMXA.MUC_IS_MEMEBERSONLY, ri.isMembersOnly());
					result.putBoolean(ConstMXA.MUC_IS_PASSWORDPROTECTED, ri.isPasswordProtected());
					result.putBoolean(ConstMXA.MUC_IS_PERSISTENT, ri.isPersistent());
					result.putString(ConstMXA.MUC_DESCRIPTION, ri.getDescription());
					result.putString(ConstMXA.MUC_SUBJECT, ri.getSubject());
					result.putInt(ConstMXA.MUC_OCCUPANTSCOUNT, ri.getOccupantsCount());
				}
			} catch (XMPPException e) {
				Log.e(TAG, "Error during getting information for room: " + roomID);
				e.printStackTrace();
			}	
			return result;
		}
		
		@Override
		public boolean createRoom(String roomID, String nickname) throws RemoteException {
			//nickname and roomID must not be true
			if (roomID==null || nickname==null || roomID.equals("") || nickname.equals(""))
				return false;
			
			// Create a MultiUserChat using an XMPPConnection for a room
		    MultiUserChat muc = new MultiUserChat(mXMPPService.getXMPPConnection(), roomID);
		    
		    try {
		    // Create the room
		    muc.create(nickname);
		    // Get the the room's configuration form
		    Form form = muc.getConfigurationForm();
		    // Create a new form to submit based on the original form
		    Form submitForm = form.createAnswerForm();
		    // Add default answers to the form to submit
		    for (Iterator fields = form.getFields(); fields.hasNext();) {
		    	FormField field = (FormField) fields.next();
		    	if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
		    		// Sets the default value as the answer
		    		submitForm.setDefaultAnswer(field.getVariable());
		        }
		    }
		    // Sets the new owner of the room
		    List<String> owners = new ArrayList<String>();
		    owners.add(jidWithoutRessource(mXMPPService.getXMPPConnection().getUser()));
		    submitForm.setAnswer("muc#roomconfig_roomowners", owners);
		    // Send the completed form (with default values) to the server to configure the room
		    muc.sendConfigurationForm(submitForm);
		    mMUCRooms.put(muc.getRoom(), muc);
		    } catch (XMPPException e) {
		    	Log.e(TAG, "Exception during creation of room: " + roomID + "with nickname: "+ nickname);
				e.printStackTrace();
				return false;
		    }
		    return true;
				
		}
		
		
		@Override
		public boolean changeNickname(String roomID, String nickname)
				throws RemoteException {
			
			try {
				if(mMUCRooms.get(roomID).isJoined()){
					mMUCRooms.get(roomID).changeNickname(nickname);
					return true;
				}
				return false;
			} catch (XMPPException e) {
				Log.e(TAG, "Can't change the nickname to: " + nickname + " in room: " + roomID);
				//e.printStackTrace();
				return false;
			}			
			
		}

		@SuppressWarnings("static-access")
		@Override
		public void declineInvitation(String roomID, String inviter,
				String reason) throws RemoteException {
			if(mMUCInvations.containsKey(roomID)){
				mMUCInvations.get(roomID).decline(mXMPPService.getXMPPConnection(), roomID, inviter, reason);
				mMUCInvations.remove(roomID);
			}
		}

		@Override
		public List<String> getMembers(String roomID) throws RemoteException {
			List<String> users = new ArrayList<String>();
			Iterator<String> occIter = mMUCRooms.get(roomID).getOccupants();
			
			while (occIter.hasNext()){
				users.add(occIter.next());
			}
			
			return users;
		}

		@Override
		public void registerInvitationCallback(IInvitationCallback cb)
				throws RemoteException {
			if (cb != null) {
				mInvationCallbacks.register(cb);
			}
		}

		@Override
		public void sendGroupMessage(String roomID, XMPPMessage msg)
				throws RemoteException {
			Log.v(TAG, "sendGroupMsg roomID: " + roomID);
			Message message = new Message(roomID, Message.Type.groupchat);
			message.setBody(msg.body);
			mXMPPService.getXMPPConnection().sendPacket(message);
		}

		@Override
		public void unregisterInvitationCallback(IInvitationCallback cb)
				throws RemoteException {
			if (cb != null) {
				mInvationCallbacks.unregister(cb);
			}
		}
		
		@Override
		public boolean acceptInvation(String roomID, String password)
				throws RemoteException {
			
			boolean roomJoined = false;
			
			try {
				if(mMUCInvations.containsKey(roomID)){
					MultiUserChat muc = mMUCInvations.get(roomID);
					
					muc.join(mXMPPService.getXMPPConnection().getUser(), password);
					
					mMUCInvations.remove(roomID);
					mMUCRooms.put(roomID, muc);
					
					roomJoined = true;
				}
			} catch (XMPPException e) {
				Log.e(TAG, "ERROR while joining the room via invation: " + roomID);
				Log.e(TAG, "Message: " + e.getMessage());
				//e.printStackTrace();
			}
			
			return roomJoined;
		}
		
		@Override
		public boolean joinRoom(String roomID, String password)
				throws RemoteException {
			
			boolean roomJoined = false;
			
			MultiUserChat.isServiceEnabled(mXMPPService.getXMPPConnection(), "");
			
			try {
				if(mMUCRooms.containsKey(roomID)){
					mMUCRooms.get(roomID).join(mXMPPService.getXMPPConnection().getUser(), password);
					roomJoined = true;
				}
				else {
					MultiUserChat muc = new MultiUserChat(mXMPPService.getXMPPConnection(), roomID);
					
					mMUCRooms.put(roomID, muc);
					
					muc.join(mXMPPService.getXMPPConnection().getUser(), password);

				}
				Log.i(TAG, "joined MUC: " + roomID);
			} catch (XMPPException e) {
				Log.e(TAG, "ERROR while joining the room: " + roomID);
				Log.e(TAG, "Message: " + e.getMessage());
				e.printStackTrace();
			}
			
			return roomJoined;
		}
		
		@Override
		public void leaveRoom(String roomID) throws RemoteException {
			if(mMUCRooms.containsKey(roomID)){
				mMUCRooms.get(roomID).leave();
			}
		}
		

		@Override
		public boolean inviteUser(String roomID, String userJID, String reason)
				throws RemoteException {
			if (mMUCRooms.containsKey(roomID)) {				
				mMUCRooms.get(roomID).invite(userJID, reason);
				return true;				
			}
			return false;
		}

	};

	// ==========================================================
	// Private methods
	// ==========================================================

	// ==========================================================
	// Inner classes
	// ==========================================================

	private class ReaderThread extends Thread implements InvitationListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jivesoftware.smackx.muc.InvitationListener#invitationReceived
		 * (org.jivesoftware.smack.XMPPConnection, java.lang.String,
		 * java.lang.String, java.lang.String, java.lang.String,
		 * org.jivesoftware.smack.packet.Message)
		 */
		@Override
		public void invitationReceived(Connection conn, String room,
				String inviter, String reason, String password, Message message) {
			Log.i(TAG, "MUC -> invitationReceived");
			Log.v(TAG, "MUC conn: " + conn);
			Log.v(TAG, "room: " + room + " inviter: " + inviter + " reason: " + reason + " password: " + password);

			MultiUserChat muc = new MultiUserChat(conn, room);
			mMUCInvations.put(room, muc);
			
			int i = mInvationCallbacks.beginBroadcast();
			while (i > 0) {
				i--;
				try {
					IInvitationCallback icb = mInvationCallbacks.getBroadcastItem(i);
					
					XMPPMessage msg = new XMPPMessage(message.getFrom(), message.getTo(), message.getBody(), XMPPMessage.TYPE_GROUPCHAT);
					
					icb.onInvitationReceived(room, inviter, reason, password, msg);
					
					Log.v(TAG, "sending IInvationCallback");
					
				} catch (RemoteException e) {
					Log.e(TAG, "sending IInvationCallback failed!");
				}
			}
			mInvationCallbacks.finishBroadcast();
		}
	}
}
