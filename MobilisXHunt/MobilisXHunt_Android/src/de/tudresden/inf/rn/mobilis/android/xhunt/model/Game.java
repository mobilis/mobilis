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
package de.tudresden.inf.rn.mobilis.android.xhunt.model;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Color;
import android.util.Log;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.AreaInfo;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.LocationInfo;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.PlayerInfo;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.PlayerSnapshotInfo;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.RoundStatusInfo;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.SnapshotRequest;

/**
 * The Class Game is the main class for game handling in this application. It 
 * contains all players and relevant data like tickets etc. for a game.
 */
public class Game {
	
	/** The game id. */
	private String gameName;
	
	/** The current round. */
	private int currentRound;
	
	/** The game players. (jid, xhuntplayer) */
	private ConcurrentHashMap<String, XHuntPlayer> gamePlayers;
	
	/** The chat id. */
	private String chatID;
	
	/** The chat password. */
	private String chatPassword;
	
	/** Available tickets for this game (id, ticket). */
	private ConcurrentHashMap<Integer, Ticket> mTickets;
    
    /** The game start timer. */
    private int gameStartTimer;
    
    /** The remain player icons and colors for the agents. */
    private CopyOnWriteArrayList<IconColorPair> mAgentsIconColorPairs;

    /** The icon and color for mr.x. */
    private IconColorPair mMrXIconColorPair;
    
    /** The informations for the available areas fetched from server. */
    private CopyOnWriteArrayList<AreaInfo> Areas = new CopyOnWriteArrayList<AreaInfo>();
    
    /** The routemanagement to handle map data. */
    private RouteManagement mRoutemanagement;
    
    /** Is true if mr.x is visible in the current round. */
    private boolean showMrX;
    
    
	/**
	 * Instantiates a new game and set the default values.
	 */
	public Game(){
		gameName = null;
		currentRound = -1;
		
		mRoutemanagement = new RouteManagement();
		
		gamePlayers = new ConcurrentHashMap<String, XHuntPlayer>();		
		mTickets = new ConcurrentHashMap<Integer, Ticket>();		
		mAgentsIconColorPairs = new CopyOnWriteArrayList<Game.IconColorPair>();
		
		// Loads the available player icons and colors
		mAgentsIconColorPairs.add(new IconColorPair(R.drawable.ic_player_blue_36,
				Color.BLUE));
		mAgentsIconColorPairs.add(new IconColorPair(R.drawable.ic_player_green_36,
				Color.GREEN));
		mAgentsIconColorPairs.add(new IconColorPair(R.drawable.ic_player_orange_36,
				Color.rgb(220, 120, 30)));
		mAgentsIconColorPairs.add(new IconColorPair(R.drawable.ic_player_red_36,
				Color.RED));
		mAgentsIconColorPairs.add(new IconColorPair(R.drawable.ic_player_yellow_36,
				Color.rgb(255, 225, 0)));
		
		mMrXIconColorPair = new IconColorPair(R.drawable.ic_player_mrx_36, Color.BLACK);
		
		showMrX = false;

	}
	
	/**
	 * Add a ticket to list of tickets.
	 *
	 * @param ticket the ticket which should be added.
	 */
	public void addTicket(Ticket ticket){
		this.mTickets.put(ticket.getId(), ticket);
	}

	/**
	 * Get all tickets as @see java.util.HashMap (id, ticket).
	 *
	 * @return the all tickets
	 */
	public ConcurrentHashMap<Integer, Ticket> getAllTickets(){
		return mTickets;
	}

	/**
	 * Get all area informations provided by server.
	 *
	 * @return the area infos.
	 */
	public CopyOnWriteArrayList<AreaInfo> getAreas() {
		return Areas;
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
	 * Gets the chat password.
	 * 
	 * @return the chat password
	 */
	public String getChatPassword() {
		return chatPassword;
	}
	
	/**
	 * Gets the current round.
	 * 
	 * @return the current round
	 */
	public int getCurrentRound(){
		return currentRound;
	}
	
	/**
	 * Gets the game id.
	 * 
	 * @return the game id
	 */
	public String getGameName(){
		return gameName;
	}
	
	/**
	 * Gets a game player by jid.
	 * 
	 * @param jid the jid of the player
	 * 
	 * @return a player
	 */
	public XHuntPlayer getPlayerByJID(String jid){		
		return gamePlayers.get(jid);
	}
	
	/**
	 * Gets the game players.
	 * 
	 * @return the game players
	 */
	public ConcurrentHashMap<String, XHuntPlayer> getGamePlayers() {
		return gamePlayers;
	}
	
	/**
	 * Gets the game start timer.
	 *
	 * @return the game start timer
	 */
	public int getGameStartTimer() {
		return gameStartTimer;
	}
	
	/**
	 * Gets the player mr x.
	 *
	 * @return player mr x
	 */
	public XHuntPlayer getMrX(){
		XHuntPlayer mrx = null;
		
		for(XHuntPlayer player : gamePlayers.values()){
			if(player.isMrX()){
				mrx = player;
				break;
			}
		}
		
		return mrx;
	}
	
	/**
	 * Gets the RouteManagement or this game.
	 *
	 * @return the RouteManagement
	 */
	public RouteManagement getRouteManagement(){
		return this.mRoutemanagement;
	}
	
	/**
	 * Get a specific ticket.
	 *
	 * @param id the id of the ticket
	 * @return the ticket
	 */
	public Ticket getTicket(int id){
		return mTickets.get(id);
	}	
	
	/**
	 * Handle a snapshot provided by server if client is not synchrony with server.
	 *
	 * @param bean the SnapshotBean to process
	 */
	public void processSnapshot(SnapshotRequest bean){	
		gameName = bean.getGameName();
		currentRound = bean.getRound();
		showMrX = bean.getShowMrX();
		gameStartTimer = bean.getStartTimer();
		
		mRoutemanagement.setMyTickets(bean.getTickets());
		
		gamePlayers.clear();		

		// Assign icons and colors to players
		try {
			// new Service version with Color Ids contained in PlayerInfo
			for(PlayerSnapshotInfo info : bean.getPlayerSnapshots()){
				XHuntPlayer player = new XHuntPlayer(info.getPlayerInfo().getJid(), info.getPlayerInfo().getPlayerName(),
						info.getPlayerInfo().getIsModerator(), info.getPlayerInfo().getIsMrX(), info.getPlayerInfo().getIsReady());
				
				player.setCurrentTargetFinal(info.getIsTargetFinal());
				player.setCurrentTarget(info.getTargetId());
				player.setReachedTarget(info.getTargetReached());
				player.setLastStation(info.getLastStationId());
				
				if(player.isMrX()){
					player.setPlayerIconID(mMrXIconColorPair.IconId);
					player.setPlayerColorID(mMrXIconColorPair.Color);
				}
				else{
					player.setGeoLocation(info.getLocation().getLatitude(), info.getLocation().getLongitude());
					
					int iconColorId = info.getPlayerInfo().getIconColorID();
					player.setPlayerIconID(mAgentsIconColorPairs.get(iconColorId).IconId);
					player.setPlayerColorID(mAgentsIconColorPairs.get(iconColorId).Color);
				}
				gamePlayers.put(player.getJid(), player);			
			}
		} catch(Exception e) {
			// old Service version without Color Ids contained in PlayerInfo
			int iconCounter = 0;
			for(PlayerSnapshotInfo info : bean.getPlayerSnapshots()){
				XHuntPlayer player = new XHuntPlayer(info.getPlayerInfo().getJid(), info.getPlayerInfo().getPlayerName(),
						info.getPlayerInfo().getIsModerator(), info.getPlayerInfo().getIsMrX(), info.getPlayerInfo().getIsReady());
				
				player.setCurrentTargetFinal(info.getIsTargetFinal());
				player.setCurrentTarget(info.getTargetId());
				player.setReachedTarget(info.getTargetReached());
				player.setLastStation(info.getLastStationId());
				
				if(player.isMrX()){
					player.setPlayerIconID(mMrXIconColorPair.IconId);
					player.setPlayerColorID(mMrXIconColorPair.Color);
				}
				else{
					player.setGeoLocation(info.getLocation().getLatitude(), info.getLocation().getLongitude());
					player.setPlayerIconID(mAgentsIconColorPairs.get(iconCounter).IconId);				
					player.setPlayerColorID(mAgentsIconColorPairs.get(iconCounter).Color);
					iconCounter++;
				}
				gamePlayers.put(player.getJid(), player);			
			}
		}
	}
	

	/**
	 * Sets the areas.
	 *
	 * @param areas the new areas
	 */
	public void setAreas(CopyOnWriteArrayList<AreaInfo> areas) {
		Areas = areas;
	}
	
	/**
	 * Sets the chat id.
	 * 
	 * @param id the new chat id
	 */
	public void setChatID(String id){
		this.chatID = id;
	}
	
	/**
	 * Sets the chat password.
	 * 
	 * @param pass the new chat password
	 */
	public void setChatPassword(String pass){
		this.chatPassword = pass;
	}
	
	/**
	 * Sets the current round.
	 * 
	 * @param round the new current round
	 */
	public void setCurrentRound(int round){
		this.currentRound = round;
	}
	
	/**
	 * Sets the game id.
	 * 
	 * @param id the new game id
	 */
	public void setGameID(String id){
		this.gameName = id;
	}
	
	/**
	 * Sets the game start timer.
	 *
	 * @param gameStartTimer the new game start timer
	 */
	public void setGameStartTimer(int gameStartTimer) {
		this.gameStartTimer = gameStartTimer;
	}

	/**
	 * Checks if mr.x is visible.
	 *
	 * @return true, if mr.x is visible
	 */
	public boolean isShowMrX() {
		return showMrX;
	}

	/**
	 * Sets the showMrX attribute.
	 *
	 * @param showMrX true if mr.x is visible
	 */
	public void setShowMrX(boolean showMrX) {
		this.showMrX = showMrX;
	}

	/**
	 * Removes a game player.
	 * 
	 * @param player a player
	 */
	public void removeGamePlayer(XHuntPlayer player){
		gamePlayers.remove(player.getJid());
	}
	
	/**
	 * Synchronize players.
	 *
	 * @param playerInfos infos of all players
	 * @return true, if successful
	 */
	public boolean synchronizePlayers(List<PlayerInfo> playerInfos){
		boolean isSync = true;
		
		for(String jid : gamePlayers.keySet()){
			boolean found = false;
			
			for(PlayerInfo info : playerInfos){
				if(jid.equals(info.getJid())){
					found = true;
					break;
				}
			}
			
			if(!found){
				gamePlayers.remove(jid);
			}
		}
		
		for(PlayerInfo info : playerInfos){
			if(!gamePlayers.containsKey(info.getJid())){
				isSync = false;
				break;
			}
		}
		
		return isSync;
	}

	/**
	 * update the game players.
	 *
	 * @param playerInfos list of players
	 * @return true, if successful
	 */
	//TODO: handle if more than 6 players try to play (normally its forbidden)
	public boolean updateGamePlayers(List<PlayerInfo> playerInfos){
		Log.v("Game", "playerinfo size: " + playerInfos.size() + " agentIconPairs size: " + mAgentsIconColorPairs.size());
		if(playerInfos.size() > mAgentsIconColorPairs.size() + 1) //+1 cause of Mr.X
			return false;
			
		gamePlayers.clear();
		
		
		try{
			for(PlayerInfo info : playerInfos){
				if( !gamePlayers.containsKey(info.getJid()) ){
					XHuntPlayer player = new XHuntPlayer(info.getJid(), info.getPlayerName(),
							info.getIsModerator(), info.getIsMrX(), info.getIsReady());
				
					if(player.isMrX()){
						player.setPlayerIconID(mMrXIconColorPair.IconId);
						player.setPlayerColorID(mMrXIconColorPair.Color);
					}
					else{
						int iconColorId = info.getIconColorID();
						player.setPlayerIconID(mAgentsIconColorPairs.get(iconColorId).IconId);
						player.setPlayerColorID(mAgentsIconColorPairs.get(iconColorId).Color);
					}		
					gamePlayers.put(player.getJid(), player);		
				}
			}
			
			Log.i("Game", "PlayerInfos contained color IDs (new Service version)");
			
		} catch(Exception e) {
			int iconCounter = 0;
			for(PlayerInfo info : playerInfos){
				if( !gamePlayers.containsKey(info.getJid()) ){
					XHuntPlayer player = new XHuntPlayer(info.getJid(), info.getPlayerName(),
							info.getIsModerator(), info.getIsMrX(), info.getIsReady());
				
					if(player.isMrX()){
						player.setPlayerIconID(mMrXIconColorPair.IconId);
						player.setPlayerColorID(mMrXIconColorPair.Color);
					}
					else{
						player.setPlayerIconID(mAgentsIconColorPairs.get(iconCounter).IconId);				
						player.setPlayerColorID(mAgentsIconColorPairs.get(iconCounter).Color);		
						iconCounter++;
					}
					
					gamePlayers.put(player.getJid(), player);		
				}
			}
			
			Log.w(getClass().getName(), "PlayerInfos didn't contain color IDs (old Service version)", e);
		}

		return true;
	}
	
	/**
	 * Update the locations of the players.
	 *
	 * @param locationInfos the infos about the location of all players
	 */
	public void updatePlayerLocations(List<LocationInfo> locationInfos) {
		XHuntPlayer player;
		for(LocationInfo info : locationInfos) {
			player = gamePlayers.get(info.getJid());
			player.setGeoLocation(info.getLatitude(), info.getLongitude());
			try {
				player.setOnline(info.getPlayerOnline());				
			} catch(Exception e) {
				Log.w("Game", "XHuntService doesn't support info about player's online state", e);
				player.setOnline(true);				
			}
			
		}
	}
	
	/**
	 * Update the states of the players like target reached etc..
	 *
	 * @param playerStates the states of all players
	 */
	public void updatePlayerStates(List<RoundStatusInfo> playerStates){
		for(RoundStatusInfo info : playerStates){
			XHuntPlayer player = gamePlayers.get(info.getPlayerJid());
			
			player.setCurrentTarget(info.getTargetId());
			player.setCurrentTargetFinal(info.getIsTargetFinal());
			player.setReachedTarget(info.getTargetReached());
		}
	}


	/**
	 * The Class IconColorPair to define a relation between an icon and a color for a player.
	 */
	private class IconColorPair {
		
		/** The constant of a color. */
		public int Color;
		
		/** The id of an icon (reference to the icons in resource folder). */
		public int IconId;
		
		/**
		 * Instantiates a new icon color pair.
		 *
		 * @param iconId the id of an icon
		 * @param color the constant of a color
		 */
		public IconColorPair(int iconId, int color) {
			this.Color = color;
			this.IconId = iconId;
		}
	}
	
}
