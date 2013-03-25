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
/** 
 * @author Robert
 */

package de.tudresden.inf.rn.mobilis.android.xhunt.model;

import com.google.android.maps.GeoPoint;

/**
 * The Class XHuntPlayer.
 */
public class XHuntPlayer {
	
	/** The players name. */
	private String jid,name;
	
	/** Is true if player is moderator, mr x, ready or reached target. */
	private boolean moderator,mrx,ready,reachedTarget;
	
	/** The geo location of the player. */
	private GeoPoint geoLocation;
	
	/** The last station of the player. */
	private int currentTargetId, lastStationId;
	
	/** Is true if the current target is the players final decision. */
	private boolean currentTargetFinal;
	
	/** The player icon id. */
	private int playerIcon;
	
	/** The player color id. */
	private int playerColor;
	
	/** True if the player responds to IQs. */
	private boolean playerOnline;
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String str = "";
		
		str += "jid: " + jid;
		str += " name: " + name;
		str += " colorID: " + playerColor;
		str += "\n";
		str += "isMod: " + isModerator();
		str += " isMr.X: " + isMrX();
		str += " isReady: " + isReady();
		str += " isOnline: " + isOnline();
		
		return str;
	}

	/**
	 * Instantiates a new XHuntPlayer.
	 * 
	 * @param jid the jid of the player
	 * @param name the name of the player
	 * @param moderator is player moderator
	 * @param mrx is player mrx
	 * @param ready is player ready
	 */
	public XHuntPlayer(String jid, String name, boolean moderator,boolean mrx,boolean ready) {
		this.jid=jid;
		this.name=name;
		this.moderator=moderator;
		this.mrx=mrx;
		this.ready=ready;
		this.reachedTarget=false;
		this.currentTargetId=-1;
		this.currentTargetFinal=false;
		this.lastStationId=-1;
		this.playerOnline = true;
	}
	
	/**
	 * Sets the jid of the player.
	 * 
	 * @param jid the new jid
	 */
	public void setJid(String jid) {
		this.jid=jid;
	}
	
	/**
	 * Gets the jid of the player.
	 * 
	 * @return the jid of the player
	 */
	public String getJid() {
		return jid;
	}
	
	/**
	 * Gets the name of the player.
	 * 
	 * @return the name of the player
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set player to moderator.
	 * 
	 * @param moderator true, if player is moderator
	 */
	public void setModerator(boolean moderator) {
		this.moderator=moderator;
	}
	
	/**
	 * Is player moderator.
	 * 
	 * @return true, if player is moderator
	 */
	public boolean isModerator() {
		return moderator;
	}
	
	/**
	 * Sets player to mr x.
	 * 
	 * @param mrx true, if player is mr x
	 */
	public void setMrX(boolean mrx) {
		this.mrx=mrx;
	}
	
	/**
	 * Is player mr x.
	 * 
	 * @return true, if player is mr x
	 */
	public boolean isMrX() {
		return mrx;
	}
	
	/**
	 * Set player ready.
	 * 
	 * @param ready true, if player is ready
	 */
	public void setReady(boolean ready) {
		this.ready=ready;
	}
	
	/**
	 * Is player ready.
	 * 
	 * @return true, if player is ready
	 */
	public boolean isReady() {
		return ready;
	}
	
	/**
	 * Set player has reached target.
	 * 
	 * @param reachedTarget true, if player has reached target
	 */
	public void setReachedTarget(boolean reachedTarget) {
		this.reachedTarget=reachedTarget;
	}
	
	/**
	 * Has player reached target.
	 * 
	 * @return true, if player has reached target
	 */
	public boolean getReachedTarget() {
		return reachedTarget;
	}
	
	/**
	 * Sets the current target.
	 *
	 * @param stationId the new current target id
	 */
	public void setCurrentTarget(int stationId) {
		this.currentTargetId = stationId;
	}
	
	/**
	 * Sets the current target to last station store target station
	 * as current station of own player.
	 */
	public void setCurrentTargetToLastStation() {
		if(currentTargetId > 0){
			this.lastStationId = this.currentTargetId;
			this.currentTargetId = -1;
		}
	}
	
	/**
	 * Gets the current target.
	 * 
	 * @return the current target
	 */
	public int getCurrentTargetId() {
		return currentTargetId;
	}
	
	/**
	 * Sets true if the current target is final.
	 * 
	 * @param currentTargetFinal the new current target final
	 */
	public void setCurrentTargetFinal(boolean currentTargetFinal) {
		this.currentTargetFinal=currentTargetFinal;
	}
	
	/**
	 * Checks if is current target final.
	 * 
	 * @return true, if is current target final
	 */
	public boolean isCurrentTargetFinal() {
		return currentTargetFinal;
	}
	
	/**
	 * Sets the geo location of the player.
	 * 
	 * @param geo the new geo location
	 */
	public void setGeoLocation(GeoPoint geo) {
		geoLocation=geo;
	}
	
	/**
	 * Sets the geo location of the player.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	public void setGeoLocation(int latitude, int longitude) {
		geoLocation=new GeoPoint(latitude, longitude);
	}
	
	/**
	 * Gets the geo location of the player.
	 * 
	 * @return the geo location
	 */
	public GeoPoint getGeoLocation() {
		return geoLocation;
	}
	
	/**
	 * Sets the last station. This is the station where the player is 
	 * located at, or was started at to move to the target station.
	 *
	 * @param stationId the new last station
	 */
	public void setLastStation(int stationId) {
		this.lastStationId = stationId;
	}
	
	/**
	 * Gets the last station.
	 * 
	 * @return the last station
	 */
	public int getLastStationId() {
		return lastStationId;
	}

	/**
	 * Gets the player role to string.
	 * 
	 * @return the player role to string
	 */
	public String getPlayerRoleToString(){
		String role = "";
		
		if(this.moderator){
			role += "M, ";
		}
		
		if(this.mrx){
			role += "X";
		}
		else{
			role += "A";
		}
		
		return role;
	}

	/**
	 * Gets the player icon id.
	 * 
	 * @return the player icon id
	 */
	public int getPlayerIconID() {
		return playerIcon;
	}

	/**
	 * Sets the player icon id.
	 * 
	 * @param playerIconID the new player icon id
	 */
	public void setPlayerIconID(int playerIconID) {
		this.playerIcon = playerIconID;
	}
	
	/**
	 * Gets the player color id.
	 * 
	 * @return the player color id
	 */
	public int getPlayerColorID() {
		return playerColor;
	}

	/**
	 * Sets the player color id.
	 * 
	 * @param playerColorID the new player color id
	 */
	public void setPlayerColorID(int playerColorID) {
		this.playerColor = playerColorID;
	}
	
	/**
	 * Checks whether the player's online state is true or false.
	 *
	 * @return true, if the player responds to IQs
	 */
	public boolean isOnline() {
		return playerOnline;
	}

	/**
	 * Sets true, if the player's online state is true.
	 *
	 * @param playerOnline true if the player responds to IQs
	 */
	public void setOnline(boolean playerOnline) {
		this.playerOnline = playerOnline;
	}
}
