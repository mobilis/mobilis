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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;

import de.tudresden.inf.rn.mobilis.services.xhunt.model.Ticket;
import de.tudresden.inf.rn.mobilis.services.xhunt.model.XHuntPlayer;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.LocationInfo;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.PlayerInfo;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.PlayerSnapshotInfo;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.SnapshotRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.TicketAmount;
import de.tudresden.inf.rn.mobilis.services.xhunt.state.GameState;
import de.tudresden.inf.rn.mobilis.services.xhunt.state.GameStateUninitialized;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * The game class represents a whole game. 
 * It organizes locations, targets, players und the state of the game.
 * @author elmar, Daniel Esser
 *
 */

public class Game {
	
	//Components
	/** The routemanagement. */
	private RouteManagement mRoutemanagement;
	
	/** The muc. */
	private MultiUserChat muc;
	
	/** The control. */
	private XHunt control;
	
	/** The game is open. */
	private boolean gameIsOpen;
	
	/** The game players. (jid, xhuntplayer) */
	private HashMap<String, XHuntPlayer> gamePlayers;	
	
	/** id, ticket. */
	private HashMap<Integer, Ticket> mTickets;
	
	/** The state. */
	private GameState state;
    
	/** The round. */
	private int round;
	
	/** The class specific Logger object. */
	private final static Logger LOGGER = Logger.getLogger(Game.class.getCanonicalName());

	/**
	 * Initilizes the Game Component. All private members are initialized. The Statemachine gets
	 * the initial state and the XML with stations and routes is parsed.
	 *
	 * @param control XHuntController, who administrates the whole life cycle
	 * @throws XMPPException the xMPP exception
	 * @throws FileNotFoundException the file not found exception
	 * @throws XMLStreamException the xML stream exception
	 * @throws FactoryConfigurationError the factory configuration error
	 * @throws Exception the exception
	 */
	public Game(XHunt control) throws XMPPException, FileNotFoundException, XMLStreamException, FactoryConfigurationError, Exception{
		this.control = control;
		
		this.round = 0;
		this.gameIsOpen = false;
		
		gamePlayers = new HashMap<String, XHuntPlayer>();
		mTickets = new HashMap<Integer, Ticket>();
		mRoutemanagement = new RouteManagement();
        
		openMultiUserChat();
		
        System.out.println("Game created (PlayerMin: " + control.getSettings().getMinPlayers() + ", PlayerMax: " + control.getSettings().getMaxPlayers() +")");
        
        //set status
		setGameState(new GameStateUninitialized(control, this));
				
	}
	
	/**
	 * Adds the ticket.
	 *
	 * @param ticket the ticket
	 */
	public void addTicket(Ticket ticket){
		this.mTickets.put(ticket.getId(), ticket);
	}
	
	/**
	 * Checks if is round start.
	 *
	 * @param playerJid the player jid
	 * @return true, if is round start
	 */
	public boolean isRoundStart(String playerJid){
		boolean isRoundStart = false;
		
		XHuntPlayer player = getPlayerByJid(playerJid);
		//check round start conditions
		if(player.getLastStationId() > 0
				&& player.getCurrentTargetId() < 1
				&& !player.getReachedTarget())
			isRoundStart = true;

		return isRoundStart;
	}
	
	/**
	 * Creates the snapshot bean.
	 *
	 * @param playerJid the player jid
	 * @return the snapshot bean
	 */
	public SnapshotRequest createSnapshotBean(String playerJid){
		SnapshotRequest bean = new SnapshotRequest();
		XHuntPlayer toPlayer = getPlayerByJid(playerJid);
		
		if(toPlayer != null){
			bean.setGameName( control.getSettings().getGameName() );
			bean.setRound( round );
			//bean.setIsRoundStart( isRoundStart(playerJid) );
			/* The one above leads to multiple calls of mStartRoundHandler in the client */
			bean.setIsRoundStart(false);
			bean.setShowMrX( showMisterX() );
			bean.setStartTimer( control.getSettings().getStartTimer() );
			
			List<TicketAmount> tickets = new ArrayList< TicketAmount >();
			
			for ( Map.Entry< Integer, Integer > entry : toPlayer.getTicketsAmount().entrySet() ) {
				tickets.add( new TicketAmount( entry.getKey(), entry.getValue() ) );
			}
			
			bean.setTickets( tickets );
			
			ArrayList<PlayerSnapshotInfo> playersSnapshot = new ArrayList<PlayerSnapshotInfo>();
			for(XHuntPlayer player : getAgents()){
				playersSnapshot.add(
						new PlayerSnapshotInfo(
								new PlayerInfo(
										player.getJid(),
										player.getName(), 
										player.isModerator(), 
										player.isMrx(),
										player.isReady(),
										player.getPlayerIconID()
								),
								new LocationInfo( 
										player.getJid(),									
										null != player.getGeoLocation()
											? player.getGeoLocation().getLatitudeE6()
											: -1,
										null != player.getGeoLocation()
											? player.getGeoLocation().getLongitudeE6()
											: -1,
										player.isOnline()
								),
								player.isCurrentTargetFinal(),
								player.getCurrentTargetId(),								
								player.getReachedTarget(),
								player.getLastStationId()
						)
				);
			}
			
			XHuntPlayer mrxPlayer = getMisterX();
			if(mrxPlayer != null){
				PlayerSnapshotInfo mrxSnapshotInfo = null;
				if(toPlayer.isMrx())
					mrxSnapshotInfo = new PlayerSnapshotInfo(
							new PlayerInfo(
									mrxPlayer.getJid(),
									mrxPlayer.getName(), 
									mrxPlayer.isModerator(), 
									mrxPlayer.isMrx(),
									mrxPlayer.isReady(),
									mrxPlayer.getPlayerIconID()
							),
							new LocationInfo(
								mrxPlayer.getJid(),
								null != mrxPlayer.getGeoLocation()
									? mrxPlayer.getGeoLocation().getLatitudeE6()
									: -1,
								null != mrxPlayer.getGeoLocation()
									? mrxPlayer.getGeoLocation().getLongitudeE6()
									: -1,
								mrxPlayer.isOnline()
							),
							mrxPlayer.isCurrentTargetFinal(),
							mrxPlayer.getCurrentTargetId(),
							
							mrxPlayer.getReachedTarget(),
							mrxPlayer.getLastStationId());
				else
					mrxSnapshotInfo = new PlayerSnapshotInfo(
							new PlayerInfo(
									mrxPlayer.getJid(),
									mrxPlayer.getName(), 
									mrxPlayer.isModerator(), 
									mrxPlayer.isMrx(),
									mrxPlayer.isReady(),
									mrxPlayer.getPlayerIconID()
							),
							new LocationInfo( mrxPlayer.getJid(), -1, -1, mrxPlayer.isOnline() ),
							mrxPlayer.isCurrentTargetFinal(),
							mrxPlayer.getCurrentTargetId(),							
							mrxPlayer.getReachedTarget(),
							mrxPlayer.getLastStationId());
				playersSnapshot.add(mrxSnapshotInfo);
			}
			
			bean.setPlayerSnapshots( playersSnapshot );
		}
		
		return bean;
	}

	/**
	 * Gets the all tickets.
	 *
	 * @return the all tickets
	 */
	public HashMap<Integer, Ticket> getAllTickets(){
		return mTickets;
	}

	/**
	 * Gets the players.
	 *
	 * @return The list of players.
	 */
	public HashMap<String, XHuntPlayer> getPlayers() {
		return gamePlayers;
	}
		
	/**
	 * Checks if is game open.
	 *
	 * @return true, if is game open
	 */
	public boolean isGameOpen() {
		return gameIsOpen;
	}


	/**
	 * Sets the game is open.
	 *
	 * @param gameIsOpen the new game is open
	 */
	public void setGameIsOpen(boolean gameIsOpen) {
		this.gameIsOpen = gameIsOpen;
	}

	/**
	 * Gets the mister x.
	 *
	 * @return The player that is MisterX.
	 */
	public XHuntPlayer getMisterX() {
		for(XHuntPlayer player : gamePlayers.values()){
			if(player.isMrx())
				return player;
		}
		
		return null;
	}
		
	/**
	 * Gets the agents.
	 *
	 * @return List of the agents.
	 */
	public ArrayList<XHuntPlayer> getAgents(){
		ArrayList<XHuntPlayer> agents = new ArrayList<XHuntPlayer>();

		for(XHuntPlayer player : gamePlayers.values()){
			if(!player.isMrx())
				agents.add(player);
		}
		
		return agents;
	}
	
	/**
	 * Gets the agents jids.
	 *
	 * @return List of the agents.
	 */
	public Set<String> getAgentsJids(){
		Set<String> jids = new HashSet<String>();

		for(XHuntPlayer player : gamePlayers.values()){
			if(!player.isMrx())
				jids.add(player.getJid());
		}
		
		return jids;
	}
	
	/**
	 * Gets the moderator.
	 *
	 * @return The player that is MisterX.
	 */
	public XHuntPlayer getModerator() {
		for(XHuntPlayer player : gamePlayers.values()){
			if(player.isModerator())
				return player;
		}
		
		return null;
	}
	
	/**
	 * Gets the player infos.
	 *
	 * @return the player infos
	 */
	public ArrayList<PlayerInfo> getPlayerInfos(){
		ArrayList<PlayerInfo> playerInfos = new ArrayList<PlayerInfo>();
		
		for(XHuntPlayer player : gamePlayers.values()){
			playerInfos.add(new PlayerInfo(
					player.getJid(),
					player.getName(),
					player.isModerator(),
					player.isMrx(),
					player.isReady(),
					player.getPlayerIconID())
			);
		}
		
		return playerInfos;
	}

	/**Returns the player corresponding to the JabberID that is used. Necessary for indirect packages (XHuntLocation).
	 * @param jid The JabberID of the player.
	 * @return Object XHuntPlayer that matches the JabberID.
	 */
	public XHuntPlayer getPlayerByJid(String jid)
	{
		return gamePlayers.get(jid);
	}
	
	/**
	 * Gets the used tickets.
	 *
	 * @return the used tickets
	 */
	public HashMap<String, ArrayList<Integer>> getUsedTickets(){
		HashMap<String, ArrayList<Integer>> usedTickets =
			new HashMap<String, ArrayList<Integer>>();
		
		for(XHuntPlayer player : gamePlayers.values()){
			usedTickets.put(player.getJid(), player.getUsedTickets());
		}
		
		return usedTickets;
	}
	
	/**
	 * Adds a new player and updates all player lists.
	 *
	 * @param p the p
	 */
	public void addPlayer(XHuntPlayer p){
		this.gamePlayers.put(p.getJid(), p);
	}
	
	/**Removes the players and updates all player lists. Kicks him out of the chat. 
	 * @param p Player object that should be removed.
	 */
	public void removePlayer(XHuntPlayer p){
		this.gamePlayers.remove(p.getJid());
		
		kickPlayerFromChat(p.getJid());
	}

	/**
	 * Removes the player by jid.
	 *
	 * @param jid the jid
	 */
	public void removePlayerByJid(String jid){
		this.gamePlayers.remove(jid);
		
		kickPlayerFromChat(jid);
	}
	
	/**
	 * Clear moderator status.
	 */
	public void clearModeratorStatus(){
		for(XHuntPlayer player : gamePlayers.values()){
			player.setModerator(false);
		}
	}
	
	/**
	 * Clear mr x status.
	 */
	public void clearMrXStatus(){
		for(XHuntPlayer player : gamePlayers.values()){
			player.setMrx(false);
		}
	}
	
	/**Deletes all the targets of the players and saves them in oldTargetLocations.
	 * 
	 */
	public void clearAgentTargets(){
		for(XHuntPlayer player : gamePlayers.values()){
			if(!player.isMrx()){
				player.setCurrentTargetToLastStation();
				player.setReachedTarget(false);
			}
		}
	}
	
	/**Deletes the target of MisterX and saves it in oldTargetLocations.
	 * 
	 */
	public void clearMisterXTarget(){	
		for(XHuntPlayer player : gamePlayers.values()){
			if(player.isMrx()){
				player.setCurrentTargetToLastStation();
				player.setReachedTarget(false);
				break;
			}
		}
	}	
	
	/**Set the current game state to a new one.
	 * @param newState The next state for the state machine.
	 */
	public void setGameState(GameState newState)
	{
		state = newState;
	}
	
	
	
	/**
	 * Gets the game state.
	 *
	 * @return the game state
	 */
	public GameState getGameState() {
		return state;
	}

	/**
	 * Forwards the IQ packet to the state machine.
	 *
	 * @param bean the bean
	 */
	public void processPacket(XMPPBean bean)
	{
		state.processPacket(bean);
	}
	
	/**Computes the distance between the current and the target location.
	 * @param p Player object.
	 * @return True, if distance is smaller than 50 meters, else false.
	 */
	public boolean isPlayerAtTarget(XHuntPlayer p) {
		if(p.getGeoLocation() != null && p.getCurrentTargetId() > 0){
			return (mRoutemanagement.computeDistance(p.getGeoLocation(),
						mRoutemanagement.getStation(p.getCurrentTargetId()).getGeoPoint())
					< control.getSettings().getDistanceTargetReached());
		} 
		
		return false;
	}
	
	/**Computes if all online players have already reached their target. Uses isPlayerAtTarget().
	 * @return True, if the distance of all players between them and their targets is smaller that 50 meters.
	 */
	public boolean areAllPlayersAtTarget() {
		boolean result = true;
		
		for(XHuntPlayer player : gamePlayers.values()) {
			if(player.isOnline())
				result = result && isPlayerAtTarget(player);
		}
		
		return result;
	}
	
	/**
	 * Are all players ready.
	 *
	 * @return true, if successful
	 */
	public boolean areAllPlayersReady(){
		boolean result = true;
		
		for(XHuntPlayer player : gamePlayers.values()){
			result = result && player.isReady();
		}
		
		return result;
	}
	
	
	/**If we have a special round, show MisterX.
	 * @return True, if MisterX should be shown, else false.
	 */
	public boolean showMisterX() {
		if((round % 3) == 0 && round > 0){
			return true;
		}		
		return false;
	}

	/**
	 * Gets the route management.
	 *
	 * @return RouteManagement object. Responsible for stations and routes.
	 */
	public RouteManagement getRouteManagement() {
		return mRoutemanagement;
	}

	
	
	//MultiUserChat	
	/**
	 * Opens the MultiUserChat with initialized members.
	 *
	 * @throws XMPPException the xMPP exception
	 */
	public void openMultiUserChat() throws XMPPException{
		
		muc = control.getConnection().createMultiUserChat(control.getSettings().getChatID());

		muc.create("Server");
		
		Form oldForm = muc.getConfigurationForm();
		Form newForm = oldForm.createAnswerForm();
		
		for (Iterator<FormField> fields = oldForm.getFields(); fields.hasNext();) {
		    FormField field = (FormField) fields.next();
		    if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
		    	newForm.setDefaultAnswer(field.getVariable());
		    }
		}
		
		newForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
		newForm.setAnswer("muc#roomconfig_roomsecret", control.getSettings().getChatPW());
		
		muc.sendConfigurationForm(newForm);
		
		System.out.println("Chat created (ID: " + control.getSettings().getChatID() + ", Pw: " + control.getSettings().getChatPW() +")");

	}
	
	/**Kicks a player from the chat.
	 * @param jid The JabberID of the player.
	 */
	public void kickPlayerFromChat(String jid){
		
		try {
			muc.kickParticipant(jid, "No reason");
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		
		
	}

	/**
	 * Closes the MultiUserChat.
	 *
	 * @throws XMPPException the xMPP exception
	 */
	public void closeMultiUserChat() throws XMPPException{
		
		if(muc.isJoined()){
			muc.destroy("", "");
		}		
	}


	/**
	 * Gets the round.
	 *
	 * @return the round
	 */
	public int getRound() {
		return round;
	}


	/**
	 * Sets the round.
	 *
	 * @param round the new round
	 */
	public void setRound(int round) {
		this.round = round;
	}

	/**
	 * Sets the initial target.
	 *
	 * @param player the player
	 * @return true, if successful
	 */
	public boolean setInitialTarget(XHuntPlayer player){
		if(player != null 
				&& player.getGeoLocation() != null){			
			LOGGER.info("initial target for " + player.getJid() + " is " + mRoutemanagement.getNearestStation(player).getId());
			player.setCurrentTarget(mRoutemanagement.getNearestStation(player).getId());
			player.setCurrentTargetFinal(true);
			
			return true;
		}
		else
			System.err.println("Cannot set intial target, geoloc of player " + player.getJid() + " is null!");
		
		return false;
	}

	/**
	 * Checks if is mr x at same position like agent.
	 *
	 * @return true, if is mr x at same position like agent
	 */
	public boolean isMrXAtSamePositionLikeAgent(){
		XHuntPlayer playerMrX = getMisterX();
		boolean isSame = false;
		
		for(XHuntPlayer player : getAgents()){
			if(player.getCurrentTargetId() == playerMrX.getCurrentTargetId()
					&& player.getCurrentTargetId() > 0
					&& mRoutemanagement.computeDistance(player.getGeoLocation()
							, playerMrX.getGeoLocation()) 
						< control.getSettings().getDistanceTargetReached())
				return true;
		}
		
		return isSame;
	}

}
