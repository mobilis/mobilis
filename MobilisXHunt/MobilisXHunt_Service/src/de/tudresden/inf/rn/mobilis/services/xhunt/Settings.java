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
package de.tudresden.inf.rn.mobilis.services.xhunt;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent;

/**
 * The Class Settings contains global setting for the game.
 */
public class Settings {
	
	/** The amount of max players. */
	private int maxPlayers;
	
	/** The amount of min players. */
	private int minPlayers;
	
	/** The area id of the games used area. */
	private int areaId;
	
	/** The amount of rounds for this game. */
	private int rounds;
	
	/** The chat id. */
	private String chatID;
	
	/** The chat password. */
	private String chatPW;
	
	/** The service resource. */
	private String serviceResource;
	
	/** The game name. */
	private String gameName;
	
	/** The game password. */
	private String gamePassword;
	
	/** The start timer in milliseconds for Mr.X. */
	private int startTimer;
	
	/** The ticket ids and amount for Mr.X (id, amount). */
	private HashMap<Integer, Integer> ticketsMrX;
	
	/** The ticket ids and amount for the agents (id, amount). */
	private HashMap<Integer, Integer> ticketsAgents;
	
	/** The location polling interval in milliseconds.
	 * This delay is used as a kind of ping as well. */
	private int locationPollingIntervalMillis = 10000;
	
	/** The Constant TICKET_ID_SUGGESTION used to identify a
	 * players suggestion. */
	public static final int TICKET_ID_SUGGESTION = -1;
	
	/** The Constant TICKET_ID_UNMOVABLE used to identify if 
	 * a player is unmovable for this game (for the left rounds 
	 * of the game he's sending TargetBeans with a ticket id of 0). */
	public static final int TICKET_ID_UNMOVABLE = 0;
	
	/** The distance in which a target is reached (in km). */
	private double distanceTargetReached = 0.1;
	
	/** The resource folder path for the XHunt files (root is project root). */
	private String resXhuntFolderPath = "resource" + File.separator;
	
	/** The Constant DB_SERVER_ADDRESS. */
	public final static String DB_SERVER_ADDRESS = "127.0.0.1";
	
	/** The Constant DB_SERVER_PORT. */
	public final static String DB_SERVER_PORT = "3306";
	
	/** The Constant DB_NAME. */
	public final static String DB_NAME = "mobilis_server";
	
	/** The Constant DB_USERNAME. */
	public final static String DB_USERNAME = "mobilis";
	
	/** The Constant DB_PASSWORD. */
	public final static String DB_PASSWORD = "mobilis";
	
	/**
	 * Initializes the Settings with predefined values.
	 *
	 * @param agent the MobilisAgent which contains XMPP specific attributes.
	 */
	public Settings(MobilisAgent agent){
		serviceResource = agent.getResource();
		
		initDefaultValues(agent.getConnection().getServiceName());
	}
	
	/**
	 * Inits the default values.
	 *
	 * @param serverIdent the server identity
	 */
	private void initDefaultValues(String serverIdent){
		gameName = serverIdent;
		
		// in milliseconds (default is 15 minutes)
		startTimer = 15 * 60 * 1000;
		
		this.areaId = -1;
		rounds = 10;
		
		maxPlayers = 6;
		minPlayers = 1;
		
		// default chatroom data, replaces all '/' and ':' because they would produce an error in chat name
		chatID = serviceResource.replaceAll( "[/:]", "" ) + "@conference." + serverIdent;
		chatPW = "tnuhx";
		
		ticketsMrX = new HashMap<Integer, Integer>();
		ticketsAgents = new HashMap<Integer, Integer>();
	}
	
	

	/**
	 * Gets the distance in which a target is reached (in km).
	 *
	 * @return the distance
	 */
	public double getDistanceTargetReached() {
		return distanceTargetReached;
	}

	/**
	 * Sets the distance in which a target is reached (in km).
	 *
	 * @param distanceTargetReached the new distance
	 */
	public void setDistanceTargetReached(double distanceTargetReached) {
		this.distanceTargetReached = distanceTargetReached;
	}

	/**
	 * Gets the location polling interval in milliseconds.
	 *
	 * @return the location polling interval in milliseconds
	 */
	public int getLocationPollingIntervalMillis() {
		return locationPollingIntervalMillis;
	}

	/**
	 * Sets the location polling interval in milliseconds.
	 *
	 * @param locationPollingIntervalMillis the new location polling 
	 * interval in milliseconds
	 */
	public void setLocationPollingIntervalMillis(int locationPollingIntervalMillis) {
		this.locationPollingIntervalMillis = locationPollingIntervalMillis;
	}

	/**
	 * Gets the initial tickets of Mr.X (ticketId, amount).
	 *
	 * @return the tickets of Mr.X
	 */
	public HashMap<Integer, Integer> getTicketsMrX() {
		HashMap<Integer, Integer> tickets = new HashMap<Integer, Integer>();
		
		for(Map.Entry<Integer, Integer> entry : ticketsMrX.entrySet()){
			tickets.put(entry.getKey(), entry.getValue());
		}
		
		return tickets;
	}

	/**
	 * Sets the initial tickets for Mr.X.
	 *
	 * @param ticketsMrX the tickets for Mr.X
	 */	
	public void setTicketsMrX(HashMap<Integer, Integer> ticketsMrX) {
		this.ticketsMrX = ticketsMrX;
	}
	
	public void putTicketMrX(int ticketId, int amount) {
		this.ticketsMrX.put( ticketId, amount );
	}

	/**
	 * Gets the initial tickets of the agents.
	 *
	 * @return the tickets of the agents
	 */
	public HashMap<Integer, Integer> getTicketsAgents() {
		HashMap<Integer, Integer> tickets = new HashMap<Integer, Integer>();
		
		for(Map.Entry<Integer, Integer> entry : ticketsAgents.entrySet()){
			tickets.put(entry.getKey(), entry.getValue());
		}
		
		return tickets;
	}

	/**
	 * Sets the initial tickets for agents.
	 *
	 * @param ticketsAgents the tickets for agents
	 */
	public void setTicketsAgents(HashMap<Integer, Integer> ticketsAgents) {
		this.ticketsAgents = ticketsAgents;
	}
	
	public void putTicketsAgents(int ticketId, int amount) {
		this.ticketsAgents.put( ticketId, amount );
	}

	/**
	 * Gets the folder path to the XHunt resources.
	 *
	 * @return the resource folder path
	 */
	public String getResXhuntFolderPath() {
		return resXhuntFolderPath;
	}

	/**
	 * Gets the game password.
	 *
	 * @return the game password
	 */
	public String getGamePassword() {
		return gamePassword;
	}

	/**
	 * Sets the game password.
	 *
	 * @param gamePassword the new game password
	 */
	public void setGamePassword(String gamePassword) {
		this.gamePassword = gamePassword;
	}

	/**
	 * Gets the area id.
	 *
	 * @return the area id
	 */
	public int getAreaId() {
		return areaId;
	}

	/**
	 * Sets the area id.
	 *
	 * @param areaId the new area id
	 */
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	/**
	 * Gets the game name.
	 *
	 * @return the game name
	 */
	public String getGameName() {
		return gameName;
	}

	/**
	 * Sets the game name.
	 *
	 * @param gameName the new game name
	 */
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	/**
	 * Gets the start timer.
	 *
	 * @return the start timer
	 */
	public int getStartTimer() {
		return startTimer;
	}

	/**
	 * Sets the start timer.
	 *
	 * @param startTimer the new start timer
	 */
	public void setStartTimer(int startTimer) {
		this.startTimer = startTimer;
	}

	/**
	 * Gets the service resource.
	 *
	 * @return the service resource
	 */
	public String getServiceResource() {
		return serviceResource;
	}

	/**
	 * Sets the max players.
	 *
	 * @param maxPlayer the new max players
	 */
	public void setMaxPlayers(int maxPlayer) {
		this.maxPlayers = maxPlayer;
	}

	/**
	 * Gets the max players.
	 *
	 * @return the max players
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}

	/**
	 * Sets the min players.
	 *
	 * @param minPlayer the new min players
	 */
	public void setMinPlayers(int minPlayer) {
		this.minPlayers = minPlayer;
	}

	/**
	 * Gets the min players.
	 *
	 * @return the min players
	 */
	public int getMinPlayers() {
		return minPlayers;
	}

	/**
	 * Sets the chat id.
	 *
	 * @param chatID the new chat id
	 */
	public void setChatID(String chatID) {
		this.chatID = chatID;
	}

	/**
	 * Gets the chat id.
	 *
	 * @return the chat id
	 */
	public String getChatID() {
		return chatID;
	}

	/**
	 * Sets the chat password.
	 *
	 * @param chatPW the new chat password
	 */
	public void setChatPW(String chatPW) {
		this.chatPW = chatPW;
	}

	/**
	 * Gets the chat password.
	 *
	 * @return the chat password
	 */
	public String getChatPW() {
		return chatPW;
	}


	/**
	 * Sets the rounds.
	 *
	 * @param rounds the new rounds
	 */
	public void setRounds(int rounds) {
		this.rounds = rounds;
	}


	/**
	 * Gets the rounds.
	 *
	 * @return the rounds
	 */
	public int getRounds() {
		return rounds;
	}

}
