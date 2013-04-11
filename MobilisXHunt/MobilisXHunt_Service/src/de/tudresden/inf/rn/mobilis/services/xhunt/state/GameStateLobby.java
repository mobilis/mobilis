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
package de.tudresden.inf.rn.mobilis.services.xhunt.state;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import de.tudresden.inf.rn.mobilis.services.xhunt.Game;
import de.tudresden.inf.rn.mobilis.services.xhunt.XHunt;
import de.tudresden.inf.rn.mobilis.services.xhunt.helper.EmptyCallback;
import de.tudresden.inf.rn.mobilis.services.xhunt.model.Ticket;
import de.tudresden.inf.rn.mobilis.services.xhunt.model.XHuntPlayer;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.GameDetailsRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.JoinGameRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.PlayerExitRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.PlayersResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.SnapshotResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.TransferTicketRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.UpdatePlayerRequest;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * The class GameStateLobby is a kind of room where the players meet each other.
 */
class GameStateLobby extends GameState /*implements IMobilisXHuntIncoming*/ {
	
	/** The File which contains the game data like stations, routes and tickets. */
	private File mGameDataFile = null;
	private String _tmpFolderPath = "tmp" + File.separator + "xhunt";
	
	/** The class specific Logger object. */
	private final static Logger LOGGER = Logger.getLogger(GameStateLobby.class.getCanonicalName());
	
	/** This Map contains the IDs of Colors which were already assigned to Players. */
	private static Map<Integer, String> mOccupiedColorIDs;
	
	/**
	 * Instantiates a new GameStateLobby.
	 *
	 * @param control the service controller
	 * @param game the game instance of this service
	 */
	public GameStateLobby(XHunt control, Game game){
		this.control = control;
		this.game = game;
		
		if(mOccupiedColorIDs == null)
			mOccupiedColorIDs = new ConcurrentHashMap<Integer, String>();
		
		// create tmp and parent dirs if necessary
		File tmpDir = new File( _tmpFolderPath );
		if(!tmpDir.exists()){
			tmpDir.mkdirs();
		}
		
		loadGameData();
		
		//control.getConnection().startDelayedResultBeansTimer();
	}
	
	/**
	 * Load game data. This function will store the chosen area of the game creator and 
	 * load the data from database using the SqlHelper class.
	 */
	private void loadGameData(){
		game.getRouteManagement().setAreaId(control.getSettings().getAreaId());
		/*game.getRouteManagement().setAreaName();
		game.getRouteManagement().setAreaDescription();*/
		
		game.getRouteManagement().setAreaRoutes(
				control.getSqlHelper().queryAreaRoutesMap(control.getSettings().getAreaId()));
		game.getRouteManagement().setAreaStations(
				control.getSqlHelper().queryAreaStationsMap(control.getSettings().getAreaId()));
		game.getRouteManagement().setAreaTickets(
				control.getSqlHelper().queryAreaTicketsMap(control.getSettings().getAreaId()));
		
		// This will export the loaded data into a file on the server for using it statically, so that 
		// changes on the map doesn't influence the current game. This file will be also transmitted 
		// to the clients
		mGameDataFile = control.getSqlHelper().exportAreaData(
				control.getSettings().getAreaId(), _tmpFolderPath);
	}
	
	/* (non-Javadoc)
	 * @see de.tudresden.inf.rn.mobilis.server.services.xhunt.state.GameState#processPacket(de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean)
	 */
	@Override
	public void processPacket(XMPPBean inBean) {
		
		if( inBean instanceof GameDetailsRequest){
			handleGameDetailsBean((GameDetailsRequest)inBean);
		}
		else if( inBean instanceof JoinGameRequest){
			onJoinGame((JoinGameRequest) inBean);
		}
		else if(inBean instanceof PlayerExitRequest){
			onPlayerExit((PlayerExitRequest) inBean);
		}
		else if(inBean instanceof UpdatePlayerRequest){
			onUpdatePlayer((UpdatePlayerRequest) inBean);
		}
		else if( inBean instanceof PlayersResponse){
			// Just result of the player
			onPlayers( (PlayersResponse)inBean );
		}
		else if(inBean instanceof TransferTicketRequest){
			onTransferTicket((TransferTicketRequest) inBean);
		}
		else if(inBean instanceof SnapshotResponse) {}
		// If no defined Bean was matched, respond an error to the requester
		else {
			inBean.errorType = "wait";
			inBean.errorCondition = "unexpected-request";
			inBean.errorText = "This request is not supportet at this game state";
			
			control.getConnection().sendXMPPBeanError(
					inBean,
					inBean
			);			
		}
	}
	
	// TO DO: Failure handling if a file doesn't exists
	/*
	 * Transmit game data to player.
	 *
	 * @param playerJid the jid of the player
	 */
	/*private void transmitGameData(String playerJid){		
		// Transmit route, station and ticket information via file transfer
		if(mGameDataFile != null) {
			System.out.println("Transmitting GameDataFile ("+mGameDataFile.getAbsolutePath()+") to "+playerJid);		
			control.getConnection().transmitFile(
					new File(mGameDataFile.getAbsolutePath()),
					"gameDataIQ.Namespace",
					playerJid);
		}
		
		// Transmit ticket icons
		for(Ticket ticket : game.getRouteManagement().getAreaTickets().values()){
			
			File iconFile = new File(_tmpFolderPath + File.separator + ticket.getIcon());
			System.out.println("check if file " + iconFile.getAbsoluteFile() + " exist: " + !iconFile.exists());
			
			if(!iconFile.exists()){
				InputStream is = GameStateLobby.class.getClassLoader().getResourceAsStream( 
						control.getSettings().getResXhuntFolderPath() + ticket.getIcon() );
				
				System.out.println("is: " + is);
				
				iconFile = FileHelper.createFileFromInputStream( 
					is,
					_tmpFolderPath + File.separator + ticket.getIcon() );
				
				System.out.println("file exist now ?: " + iconFile.exists());
			}
			
			//File iconFile = new File(control.getSettings().getResXhuntFolderPath() + ticket.getIcon());
			System.out.println("Transmitting icon ("+iconFile.getAbsolutePath()+") to "+playerJid);
			if(iconFile.exists())
				control.getConnection().transmitFile(
						new File(iconFile.getAbsolutePath()),
						"icon",
						playerJid);
		}
	}*/
	
	
	/**
	 * This method searches for the lowest Color ID which has not been assigned to a player,
	 * and also checks for already assigned Color IDs which can be freed because the corresponding player left.
	 * 
	 * @param playerJID the JID of the Player to which the returned Color ID will be assigned.
	 * @return a Color ID which will be sent to the Client App
	 */
	private int getUnoccupiedColorId(String playerJID) {
		// delete ID from mOccupiedColorIDs if corresponding Player left Game
		for(Map.Entry<Integer, String> entry : mOccupiedColorIDs.entrySet()) {
			if(!game.getPlayers().containsKey(entry.getValue()))
				mOccupiedColorIDs.remove(entry.getKey());
		}
		
		// find lowest ID which is not occupied
		Integer clrId = new Integer(0);
		while(mOccupiedColorIDs.keySet().contains(clrId)) {
			clrId++;
		}
		
		// remember this Color ID as occupied
		mOccupiedColorIDs.put(clrId, playerJID);
		return clrId;
	}

	/**
	 * Handle JoinGameBean. This game is needed of each player to play this game, also the creator 
	 * of this game has to send a JoinGameBean.
	 *
	 * @param inBean the JoinGameBean which contains the player information
	 */
	//@Override
	@SuppressWarnings("unchecked")
	public XMPPBean onJoinGame( JoinGameRequest inBean ) {
		if(inBean.getIsSpectator())
			return null;
		
		XMPPBean out = null;
		LOGGER.info("game is open: " + game.isGameOpen());
		
		// The game can only be joined if it is open(configured by the creator) or has not reached its
		// maximum of game players
		if(game.isGameOpen()){
			XHuntPlayer player;
			
			// If player already joined this game, don't add him again
			if(game.getPlayers().containsKey(inBean.getFrom())){
				player = game.getPlayerByJid(inBean.getFrom());
			}
			// If the player is the first one, set his role to moderator and to Mr.X 
			// (mostly it is the creator of the game)
			else if(game.getPlayers().size() == 0){
				player = new XHuntPlayer(inBean.getFrom(), inBean.getPlayerName(), true, true, false);
				
				player.setPlayerIconID(-1);
				player.setPlayerColorID(-1);
			}
			// Else the player will be an agent and doesn't own rights as moderator
			else{
				player = new XHuntPlayer(inBean.getFrom(), inBean.getPlayerName(), false, false, false);
				
				int iconColorId = getUnoccupiedColorId(inBean.getFrom());
				player.setPlayerIconID(iconColorId);
				player.setPlayerColorID(iconColorId);
			}
			
			// Add/replace new player
			game.addPlayer(player);
			
			// If maximum of players is reached, no more JoinGameBeans will be allowed
			if(game.getPlayers().size() == control.getSettings().getMaxPlayers())
				game.setGameIsOpen(false);
			
			// Add the game data file to the list of incoming files, which will be responded to the player
			// so that he knows which files will be transfered
			ArrayList<String> incomingFileNames = new ArrayList<String>();
			if(mGameDataFile != null)
				incomingFileNames.add(mGameDataFile.getName());
			
			// Add each icon of ticket to the list of incoming files
			for(Ticket ticket : game.getRouteManagement().getAreaTickets().values()){
				incomingFileNames.add(ticket.getIcon());
			}
			
			// Confirm the join of the game and send the chat information, the start timer 
			// and the list of incoming files to the client
			out = control.getConnection().getProxy().JoinGame( 
					inBean.getFrom(), 
					inBean.getId(),
					control.getSettings().getChatID(),
					control.getSettings().getChatPW(),
					control.getSettings().getStartTimer(),
					incomingFileNames );
			
			// Notify each player of the new joined player
			sendPlayersBean("Player " + player.getName() + " has joined.", new EmptyCallback());
			
			if(player.isMrx()){
				player.setTicketsAmount(control.getSettings().getTicketsMrX());
			}
			else{
				player.setTicketsAmount(control.getSettings().getTicketsAgents());
			}
			
			control.getConnection().getProxy().UpdateTickets( inBean.getFrom(),
					player.getTicketsAmountAsList(),
					new EmptyCallback());
			
			
			
			
			// Start to transmitting the game files like game data and ticket icons
			//transmitGameData(player.getJid());
		}
		// If game is not open, it is not configured yet or the maximum of players has been reached
		else {
			control.getConnection().getProxy().getBindingStub().sendXMPPBean( inBean.buildClosedGameFault( "Maximum of players reached." ) );
		}
		
		return out;
	}

	/**
	 * Handle PlayerExitBean. This will handle if a players exit the game by himself or he will 
	 * be kicked by the moderator. If moderator leaves the game, it will be switch to GameStateGameOver. 
	 * If Mr.X leaves the game and he's not moderator, moderator becomes Mr.X.
	 *
	 * @param inBean the PlayerExitBean which contain the jid of the exit player
	 */
	//@Override
	@SuppressWarnings("unchecked")
	public XMPPBean onPlayerExit( PlayerExitRequest inBean ) {
		XMPPBean out = null;
		XHuntPlayer exitPlayer = game.getPlayerByJid(inBean.getJid());
		String updateInfo = null;
		
		if(exitPlayer != null){
			XHuntPlayer fromPlayer = game.getPlayerByJid(inBean.getFrom());
			
			//player leaves game or moderator kicks player
			if(exitPlayer.getJid().equals(inBean.getFrom())
					|| (fromPlayer != null && fromPlayer.isModerator())){
				
				game.removePlayer(exitPlayer);
				// default info
				updateInfo = "Player " + exitPlayer.getName() + " has left the game.";
				
				// Confirm exit player
				control.getConnection().getProxy().PlayerExit( 
						inBean.getFrom(),
						inBean.getId() );
				
				// If moderator leaves game send GameOver
				if(exitPlayer.isModerator()){
					game.setGameIsOpen(false);
					
					// Switch to GameOver
					game.setGameState(new GameStateGameOver(control, game));
					LOGGER.info("Status changed to GameStateGameOver");
					
					for ( String toJid : game.getPlayers().keySet() ) {
						control.getConnection().getProxy().GameOver( 
								toJid, 
								"Moderator has left!", new EmptyCallback() );
					}
				}
				else{
					// if exit player is mr.x, moderator becomes mr.x
					if(exitPlayer.isMrx()){
						XHuntPlayer moderator = fromPlayer.isModerator()
							? fromPlayer
							: game.getModerator();
						
						if(moderator != null){
							moderator.setMrx(true);
							moderator.setReady(false);
							
							updateInfo = "Mr.X(" + exitPlayer.getName() +") has left the game. Moderator("
								+ moderator.getName() + ") is now Mr.X.";
						}
						else{
							// Switch to GameOver
							game.setGameState(new GameStateGameOver(control, game));
							LOGGER.info("Status changed to GameStateGameOver");

							for ( String toJid : game.getPlayers().keySet() ) {
								control.getConnection().getProxy().GameOver( 
										toJid, 
										"No Moderator available!", new EmptyCallback() );
							}
						}
					}
					
					// Player was kicked by moderator
					if(fromPlayer.isModerator()){
						control.getConnection().getProxy().GameOver( 
								inBean.getJid(), 
								"Moderator has kicked you!",
								new EmptyCallback());
						
						updateInfo = "Player " + exitPlayer.getName() + " was kicked by Moderator.";
					}
					
					// Notify rest of players about the exited player
					sendPlayersBean(updateInfo, new EmptyCallback());
					
					// check if rest of players are ready to play and minimum of players is reached
					if(game.areAllPlayersReady()
							&& game.getPlayers().size() >= control.getSettings().getMinPlayers()) {
						game.setGameState(new GameStateRoundInitial(control, game));
						LOGGER.info("Status changed to GameStateRoundInitial");
					}
					
					// else check if new players can join now that one left
					else if((game.getPlayers().size() >= control.getSettings().getMinPlayers())
							&& (game.getPlayers().size() < control.getSettings().getMaxPlayers()))
						game.setGameIsOpen(true);
				}
			}
			// If player wants to kick another player but isn't moderator, respond an error
			else {
				out = inBean.buildPermissionFault( null );
				control.getConnection().getProxy().getBindingStub().sendXMPPBean( out );
			}
		}
		else if(inBean.getType() == XMPPBean.TYPE_RESULT){
			// answer from kicked player	
		}
		// Exit player is unknown, respond an error
		else {
			out = inBean.buildPermissionFault( "Player not found" );
			control.getConnection().getProxy().getBindingStub().sendXMPPBean( out );
		}
		
		return out;
	}

	//@Override
	public void onPlayers( PlayersResponse in ) {
		control.getConnection().handleCallback( in );
	}

	/**
	 * Handle UpdatePlayerBean. This will happen, if a player changes his status like he's 
	 * becoming Mr.X, moderator or is ready to play.
	 *
	 * @param inBean the UpdatePlayerBean which contains the new player information
	 */
	//@Override
	@SuppressWarnings("unchecked")
	public XMPPBean onUpdatePlayer( UpdatePlayerRequest inBean ) {
		XMPPBean out = null;
		XHuntPlayer fromPlayer = game.getPlayerByJid(inBean.getFrom());
		String errorText = null;
		// For update info
		boolean updatePlayers = false;
		String updateInfo = null;
		
		if(fromPlayer != null){
			// Player likes to change his own properties
			if(fromPlayer.getJid().equals(inBean.getPlayerInfo().getJid())
					&& !fromPlayer.isModerator()){
				// If player is not moderator, he can only change his ready status
				if(inBean.getPlayerInfo().getIsReady()){
					fromPlayer.setReady(true);
					
					updatePlayers = true;
					if (fromPlayer.isReady()) {
						updateInfo = "Player " + fromPlayer.getName() 
								+ " is ready now. ";
					} else {
						updateInfo = "Player " + fromPlayer.getName() 
								+ " is not ready. ";
					}
				}
				else {
					out = inBean.buildPermissionFault( "You can only change your ready status." );
					control.getConnection().getProxy().getBindingStub().sendXMPPBean( out );
				}
			}
			else if(fromPlayer.isModerator()){
				// If player is moderator, he can change everything
				XHuntPlayer updatePlayer = game.getPlayerByJid(inBean.getPlayerInfo().getJid());
				
				if(updatePlayer != null){
					// If a role state changes, the player has to confirm the ready status again
					boolean statusChanged = false;
					
					// Set ready status of player
					updatePlayer.setReady(inBean.getPlayerInfo().getIsReady());
					if (fromPlayer.isReady()) {
						updateInfo = "Player " + fromPlayer.getName() 
								+ " is ready now. ";
					} else {
						updateInfo = "Player " + fromPlayer.getName() 
								+ " is not ready. ";
					}
					
					// Set moderator status for player
					if(inBean.getPlayerInfo().getIsModerator()
							&& !updatePlayer.isModerator()){
						
						game.clearModeratorStatus();
						updatePlayer.setModerator(true);
						
						statusChanged = true;
						updateInfo = "Player " + updatePlayer.getName() 
							+ " is now Moderator";
					}
					
					// Set Mr.X status for player
					if(inBean.getPlayerInfo().getIsMrX()
							&& !updatePlayer.isMrx()){
						
						game.clearMrXStatus();
						updatePlayer.setMrx(true);
						
						// exchange icon and path colors
						fromPlayer.setPlayerIconID(updatePlayer.getPlayerIconID());
						fromPlayer.setPlayerColorID(updatePlayer.getPlayerColorID());
						updatePlayer.setPlayerIconID(-1);
						updatePlayer.setPlayerColorID(-1);
						statusChanged = true;
						updateInfo = "Player " + updatePlayer.getName() 
							+ " is now Mr.X";
					}
					
					// If role of player changed, he's not ready
					if(statusChanged){
						updatePlayer.setReady(false);
						fromPlayer.setReady(false);
						
						updatePlayers = true;
					}
					else if(inBean.getPlayerInfo().getIsReady())
						updatePlayer.setReady(inBean.getPlayerInfo().getIsReady());
						updatePlayers = true;
				}
				else{
					errorText = "Player not found.";
				}
			}
			else{
				errorText = "You don't have moderator status.";
			}
			
			if(errorText != null){
				out = inBean.buildPermissionFault( errorText );
				control.getConnection().getProxy().getBindingStub().sendXMPPBean( out );
			}
			else {
				String info = null;
				
				//check if all players are ready, minimum of players and if there is a mrx available
				if(game.areAllPlayersReady()){
					System.out.println("players: " + game.getPlayers().size() + " min:" + control.getSettings().getMinPlayers());
					if(game.getPlayers().size() >= control.getSettings().getMinPlayers()){
						if(game.getMisterX() != null){
							info = "All players are ready!";
							
							// If everthing is valid, change to GameStateInitial
							game.setGameState(new GameStateRoundInitial(control, game));
							LOGGER.info("Status changed to GameStateRoundInitial");
						}
						else{
							info = "Please set Mr.X again.";
						}
					}
					else{
						info = "Current count of players is "
							+ game.getPlayers().size()
							+ " but this game requires a minimum of "
							+ control.getSettings().getMinPlayers()
							+ " to start.";
					}
				}
				else{
					if(fromPlayer.isReady())
						info = "There are still unready players. Please Wait!";
				}
				
				if(info == null)
					info = "Nothing has changed!";
				
				// Confirm the player update with an optional text
				control.getConnection().getProxy().UpdatePlayer( 
						inBean.getFrom(), 
						inBean.getId(), 
						info );
				
				if(updatePlayers)
					sendPlayersBean(updateInfo, new EmptyCallback());
			}
		}
		else{
			out = inBean.buildPermissionFault( "You're not a player of this game." );
			control.getConnection().getProxy().getBindingStub().sendXMPPBean( out );
		}
		
		return out;
	}

	@SuppressWarnings("unchecked")
	public XMPPBean onTransferTicket( TransferTicketRequest in ) {
		XMPPBean out = null;
		XHuntPlayer fromPlayer = game.getPlayerByJid( in.getFromPlayerJid() );
		XHuntPlayer toPlayer = game.getPlayerByJid( in.getToPlayerJid() );
		
		System.out.println("transfer ticket from=" + fromPlayer + " to=" + toPlayer);
		
		if(null != fromPlayer && null != toPlayer ){
			if(fromPlayer.getTicketsAmount().containsKey( in.getTicket().getID() )
					&& fromPlayer.getTicketsAmount().get( in.getTicket().getID() ) > in.getTicket().getAmount()
					&& toPlayer.getTicketsAmount().containsKey( in.getTicket().getID() ) ){
				
				System.out.println("ticket transfer can happen");
				
				int old = fromPlayer.getTicketsAmount().get( in.getTicket().getID() );
				fromPlayer.getTicketsAmount().put( in.getTicket().getID(), old - in.getTicket().getAmount() );
				
				System.out.println("fromOld=" + old + "fromNew=" + fromPlayer.getTicketsAmount().get( in.getTicket().getID() ));
				
				toPlayer.getTicketsAmount().put( in.getTicket().getID(), 
						toPlayer.getTicketsAmount().get( in.getTicket().getID() ) + in.getTicket().getAmount());
				
				System.out.println("toNew=" + toPlayer.getTicketsAmount().get( in.getTicket().getID() ));
				
				out = control.getConnection().getProxy().TransferTicket( in.getFrom(), in.getId() );
				
				control.getConnection().getProxy().UpdateTickets( in.getFromPlayerJid(),
						fromPlayer.getTicketsAmountAsList(),
						new EmptyCallback());
				
				control.getConnection().getProxy().UpdateTickets( in.getToPlayerJid(),
						toPlayer.getTicketsAmountAsList(),
						new EmptyCallback());
			}
			else{
				out = in.buildInputDataFault( "Not enough tickets available or unavailable type of ticket." );
			}
		}
		else{
			out = in.buildInputDataFault( "Player unknown." );
		}
		
		control.getConnection().getProxy().getBindingStub().sendXMPPBean( out );
		
		return out;
	}
	
}
