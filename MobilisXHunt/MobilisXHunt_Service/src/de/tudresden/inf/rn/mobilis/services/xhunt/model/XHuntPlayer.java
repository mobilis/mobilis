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

package de.tudresden.inf.rn.mobilis.services.xhunt.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.RoundStatusInfo;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.TicketAmount;


/**
 * The Class XHuntPlayer.
 */
public class XHuntPlayer {
	
	/** The players jid and name. */
	private String jid, name;
	
	/** Is true if player is moderator, mr x, ready or reached target. */
	private boolean moderator, mrx, ready, reachedTarget;
	
	/** The geographic location of the player. */
	private GeoPoint geoLocation;
	
	/** The last station of the player. */
	private int currentTargetId, lastStationId;
	
	/** Is true if the current target is the players final decision. */
	private boolean currentTargetFinal;
	
	/** The players icon id. */
	private int playerIcon;
	
	/** The players color id. */
	private int playerColor;
	
	/** True if the player responds to IQs. */
	private boolean playerOnline;
	
	/** The amount of players tickets. */
	private HashMap<Integer, Integer> ticketsAmount;
	
	/** The list of the used tickets of the player. */
	private ArrayList<Integer> usedTickets;
	
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
		str += " isMr.X: " + isMrx();
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
		this.ticketsAmount = new HashMap<Integer, Integer>();
		this.usedTickets = new ArrayList<Integer>();
	}
	
	/**
	 * Decrease the amount of a ticket.
	 *
	 * @param ticketId the ticket id which should be decreased
	 */
	public void decreaseTicket(int ticketId){
		int amount = (ticketsAmount.get(ticketId) != null
				? ticketsAmount.get(ticketId)
				: -1);
		
		// if player has more than 0 tickets left, the 
		// amount of the ticket will be decreased and the 
		// decreased ticket will be stored as an used ticket
		if(amount > 0){
			ticketsAmount.put(ticketId, amount - 1);
			usedTickets.add(ticketId);
		}
	}
	
	/**
	 * Increase the amount of a ticket. Is just for Mr.X.
	 *
	 * @param ticketId the ticket id which should be increased
	 */
	public void increaseTicket(int ticketId){
		int amount = (ticketsAmount.get(ticketId) != null
				? ticketsAmount.get(ticketId)
				: -1);
		
		// Tickets with an ID of -1 are not assigned to this player 
		// or this ticket doesn't exist
		if(amount > -1)
			ticketsAmount.put(ticketId, amount + 1);
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

	/**
	 * Gets the amount of the players tickets.
	 *
	 * @return the tickets amount
	 */
	public HashMap<Integer, Integer> getTicketsAmount() {
		return ticketsAmount;
	}
	
	public List<TicketAmount> getTicketsAmountAsList() {
		List<TicketAmount> tickets = new ArrayList< TicketAmount >();
		
		for ( Map.Entry< Integer, Integer > amount : ticketsAmount.entrySet() ) {
			tickets.add( new TicketAmount( amount.getKey(), amount.getValue() ) );
		}
		
		return tickets;
	}

	/**
	 * Sets the amount of the players tickets.
	 *
	 * @param ticketsAmount the tickets amount
	 */
	public void setTicketsAmount(HashMap<Integer, Integer> ticketsAmount) {
		this.ticketsAmount = ticketsAmount;
	}
	
	/**
	 * Gets the used tickets.
	 *
	 * @return the used tickets
	 */
	public ArrayList<Integer> getUsedTickets(){
		return this.usedTickets;
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
	 * Sets the name of the player.
	 * 
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name=name;
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
	public void setMrx(boolean mrx) {
		this.mrx=mrx;
	}
	
	/**
	 * Is player mr x.
	 * 
	 * @return true, if player is mr x
	 */
	public boolean isMrx() {
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
	 * Sets the current target of the player.
	 *
	 * @param stationId the new current target of the player
	 */
	public void setCurrentTarget(int stationId) {
		this.currentTargetId = stationId;
	}
	
	/**
	 * Sets the current target to last station. This happens if the player 
	 * has chosen a new target. To display a visual line we need to store the 
	 * start station.
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
	 * Sets the current target as final. If a target is not set as final, 
	 * it is only a decision of the player (just available for the agents).
	 * 
	 * @param currentTargetFinal true, if the current target is the final target
	 */
	public void setCurrentTargetFinal(boolean currentTargetFinal) {
		this.currentTargetFinal=currentTargetFinal;
	}
	
	/**
	 * Checks if the current target is final.
	 * 
	 * @return true, if current target is final
	 */
	public boolean isCurrentTargetFinal() {
		return currentTargetFinal;
	}
	
	/**
	 * Sets the geographic location of the player.
	 * 
	 * @param geo the new geographic location
	 */
	public void setGeoLocation(GeoPoint geo) {
		geoLocation=geo;
	}
	
	/**
	 * Sets the geographic location of the player.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	public void setGeoLocation(int latitude, int longitude) {
		geoLocation = new GeoPoint(latitude, longitude);
	}
	
	/**
	 * Gets the geographic location of the player.
	 * 
	 * @return the geographic location
	 */
	public GeoPoint getGeoLocation() {
		return geoLocation;
	}
	
	/**
	 * Sets the id of the last station.
	 *
	 * @param stationId the new last stations id
	 */
	public void setLastStation(int stationId) {
		this.lastStationId = stationId;
	}
	
	/**
	 * Gets the id of the last station.
	 * 
	 * @return the last stations id
	 */
	public int getLastStationId() {
		return lastStationId;
	}

	/**
	 * Gets the players role as string. This is used to display the role 
	 * like 'Agent', 'Mr.X' or 'Moderator' in Lobby or IngameInfo.
	 * 
	 * @return the players role as string
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
	 * Gets the players icon id.
	 * 
	 * @return the players icon id
	 */
	public int getPlayerIconID() {
		return playerIcon;
	}

	/**
	 * Sets the players icon id.
	 * 
	 * @param playerIconID the new players icon id
	 */
	public void setPlayerIconID(int playerIconID) {
		this.playerIcon = playerIconID;
	}
	
	/**
	 * Gets the players color id.
	 * 
	 * @return the players color id
	 */
	public int getPlayerColorID() {
		return playerColor;
	}

	/**
	 * Sets the players color id.
	 * 
	 * @param playerColorID the new players color id
	 */
	public void setPlayerColorID(int playerColorID) {
		this.playerColor = playerColorID;
	}
	
	/**
	 * Gets the RoundStatusInfo of the player.
	 *
	 * @return the RoundStatusInfo which contains the 
	 * JID, the current target id, if the current target is 
	 * final decision or not and if the player has reached his 
	 * current target 
	 */
	public RoundStatusInfo getRoundStatusInfo(){
		return new RoundStatusInfo(jid,				
				isCurrentTargetFinal(), 
				currentTargetId,
				reachedTarget);
	}
	
}
