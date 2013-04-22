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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import de.tudresden.inf.rn.mobilis.services.xhunt.Connection;
import de.tudresden.inf.rn.mobilis.services.xhunt.Game;
import de.tudresden.inf.rn.mobilis.services.xhunt.Settings;
import de.tudresden.inf.rn.mobilis.services.xhunt.XHunt;
import de.tudresden.inf.rn.mobilis.services.xhunt.helper.EmptyCallback;
import de.tudresden.inf.rn.mobilis.services.xhunt.model.XHuntPlayer;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.DepartureDataRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.DepartureDataResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.GameDetailsRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.LocationInfo;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.LocationResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.PlayerExitRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.PlayersResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.RoundStatusResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.SnapshotResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.StartRoundResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.TargetRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.TicketAmount;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.UsedTicketsInfo;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.UsedTicketsRequest;
import de.tudresden.inf.rn.mobilis.xmpp.beans.IXMPPCallback;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * The Class GameStatePlay is divided into two substates the GameStateRoundMrX and 
 * GameStateRoundAgents to handle the game.
 */
public class GameStatePlay extends GameState{
	
	/** The current SubGameState. */
	private SubGameState mSubState;
	
	/* True, if not all player have reached their targets. */
	//private boolean mWaitingForPlayersReachingTarget = false;
	
	/** The timer which polls the location of all players if ticks. */
	private Timer mPollingTimer;
	
	/** Identifiers for the two different Substates, needed for letting Players rejoin after connection disturbances */
	public static final int SUBSTATE_MRX = 1, SUBSTATE_AGENTS = 2;
	
	/** The class specific Logger object. */
	private final static Logger LOGGER = Logger.getLogger(GameStatePlay.class.getCanonicalName());
	
	/**
	 * Instantiates a new GameStatePlay.
	 *
	 * @param control the service controller
	 * @param game the game instance of this service
	 */
	public GameStatePlay(XHunt control, Game game){
		this.control = control;
		this.game = game;
		
		LOGGER.info("GameState = GameStatePlay");
		
		// Start GameStateRoundMrX
		mSubState = new GameStateRoundMrX();
		
		// Start polling of players locations
		startLocationPolling();
		
		// check for connection interruptions
		control.getConnection().startDelayedResultBeansTimer();
	}
	
	/**
	 * Change current GameState.
	 *
	 * @param state the new GameState
	 */
	protected void changeGameStateTo(GameState state){	
		// Stop location polling
		if(mPollingTimer != null)
			mPollingTimer.cancel();
		
		game.setGameState(state);
		LOGGER.info("Status changed to " + state.getClass().toString());
	}

	/* (non-Javadoc)
	 * @see de.tudresden.inf.rn.mobilis.server.services.xhunt.state.GameState#processPacket(de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean)
	 */
	//@Override
	public void processPacket(XMPPBean inBean) {
		// At first it will be checked if that bean is global for all SubGameStates, 
		// If not refer the bean to the current SubGameState
		if( inBean instanceof DepartureDataRequest){
			onDeprtureData((DepartureDataRequest)inBean);
		}
		else if( inBean instanceof GameDetailsRequest){
			handleGameDetailsBean((GameDetailsRequest)inBean);
		}
		else  if( inBean instanceof LocationResponse ){
			onLocation((LocationResponse) inBean);
		}
		else if( inBean instanceof RoundStatusResponse){
			// Just result of the player
			onRoundStatus( (RoundStatusResponse)inBean );
		}
		else if( inBean instanceof PlayerExitRequest ){
			onPlayerExit((PlayerExitRequest) inBean);
		}
		else if( inBean instanceof PlayersResponse){
			// Just result of the player
			onPlayers( (PlayersResponse)inBean );
		}
		else if( inBean instanceof StartRoundResponse){
			// Just result of the player
			onStartRound( (StartRoundResponse)inBean );
		}
		else if( inBean instanceof UsedTicketsRequest){
			onUsedTickets((UsedTicketsRequest)inBean);
		}
		else if(inBean instanceof TargetRequest){
			// If the bean was not a global bean for the GameStatePlay
			// refer it to the current SubGameState
			mSubState.processPacket(inBean);
		}
		else if(inBean instanceof SnapshotResponse) {}
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
	
	/**
	 * Handle handleLocationBean. This will update the location of a player. 
	 * If a player has reached his target, notify all players.
	 * If an agent and Mr.X are at the same position, game is over.
	 * If all players reached their station, the GameState will switch to GameStateRoundMrX.
	 *
	 * @param inBean the LocationBean which contains the location information of a player
	 */
	private IXMPPCallback< LocationResponse > LocationCallback = new IXMPPCallback< LocationResponse >() {
		
		@Override
		public void invoke( LocationResponse inBean ) {
			XHuntPlayer updatePlayer = null;
			
			if(null != inBean.getLocationInfo()){
				updatePlayer = game.getPlayerByJid(inBean.getLocationInfo().getJid());
				updatePlayer.setGeoLocation(inBean.getLocationInfo().getLatitude(), inBean.getLocationInfo().getLongitude());
			}
			else{
				// no empty iq allowed
				return;
			}
			
			boolean isPlayerAtTarget = game.isPlayerAtTarget(updatePlayer);
			// If a player has reached or left his target position
			if(isPlayerAtTarget || updatePlayer.getReachedTarget()){
				updatePlayer.setReachedTarget(isPlayerAtTarget);
	
				// If Mr.X reached or left his target, only notify him, else notify all players
				if(updatePlayer.isMrx())
					sendRoundStatusBeanForMrX(null);
				else
					sendRoundStatusBean();
			}
			/*// Else if player diverge from target but was at target before, set target reached to false
			else if(!isPlayerAtTarget && updatePlayer.getReachedTarget()){			
				updatePlayer.setReachedTarget(false);
				sendRoundStatusBean();
			}*/
			
			// If an agent and Mr.X at the same position, switch to GameStateGameOver
			if(game.isMrXAtSamePositionLikeAgent()){
				setGameOver("Mr.X was caught by agent " + game.getPlayerByJid(inBean.getFrom()).getName() + ".");			
				return;
			}
			
			// If all players have reached their target and MrX is online, switch to GameStateRoundMrX
			if(game.areAllPlayersAtTarget() && game.getMisterX().isOnline())
				mSubState = new GameStateRoundMrX();
		}
	};
	
	/**
	 * Sets the game over.
	 *
	 * @param reason the game over reason
	 */
	@SuppressWarnings("unchecked")
	private void setGameOver(String reason){
		game.setGameIsOpen(false);
		
		// Stop polling locations
		stopPollingLocations();
		
		// Switch to GameStateGameOver
		game.setGameState(new GameStateGameOver(control, game));
		
		// Notify each player about the end of the game
		for ( String toJid : game.getPlayers().keySet() ) {
			control.getConnection().getProxy().GameOver( 
					toJid, 
					reason, 
					new EmptyCallback());
		}
	}
	
	/**
	 * Start location polling. This will poll the location of all players periodically. 
	 * This will also update the locations on clientside of the players.
	 */
	private void startLocationPolling(){
		LOGGER.info("Start polling locations. Interval = " + control.getSettings().getLocationPollingIntervalMillis() /1000 + "sec");
		// Define and start the polling timer
		mPollingTimer = new Timer();
		mPollingTimer.schedule(
			new TimerTask() {
				public void run() {
					if(game.getGameState() instanceof GameStateGameOver)
						mPollingTimer.cancel();
					
					// If there are not enough players available or Mr.X is gone, switch to 
					// GameStateGameOver
					List<XHuntPlayer> availablePlayers = new ArrayList<XHuntPlayer>();
					for(XHuntPlayer plr : game.getPlayers().values())
						if (plr.isOnline())
							availablePlayers.add(plr);
					
					if(availablePlayers.size() < control.getSettings().getMinPlayers()
							|| game.getMisterX() == null){						
						
						setGameOver("Game over. Not enough players or Mr.X not available.");
						return;
					}
					
					ArrayList<LocationInfo> infos = new ArrayList<LocationInfo>();
					XHuntPlayer playerMrX = null;
					
					// Collect the locations of all agents
					for(XHuntPlayer player : game.getPlayers().values()){
						if(!player.isMrx()){
							infos.add(new LocationInfo(player.getJid(),
									player.getGeoLocation().getLatitudeE6(),
									player.getGeoLocation().getLongitudeE6(),
									player.isOnline()));
						}
						else{
							playerMrX = player;
						}
					}
					
					// Send the locations of all agents to each agent
					boolean mrXOnline = playerMrX != null ? playerMrX.isOnline() : false;
					for ( String toJid : game.getAgentsJids() ) {
						control.getConnection().getProxy().Location( 
								toJid, 
								infos,
								mrXOnline,
								LocationCallback );
					}
					
					// Add the location of Mr.X to the list of locations of the agents 
					// and send this list to Mr.X
					if(playerMrX != null){
						infos.add(new LocationInfo(playerMrX.getJid(),
								playerMrX.getGeoLocation().getLatitudeE6(),
								playerMrX.getGeoLocation().getLongitudeE6(),
								playerMrX.isOnline()));
						
						control.getConnection().getProxy().Location( 
								game.getMisterX().getJid(), 
								infos,
								playerMrX.isOnline(),
								LocationCallback );
					}
					
		        }
				// Timer will be start immediately
		}, 0, control.getSettings().getLocationPollingIntervalMillis());
	}
	
	/**
	 * Stop polling locations.
	 */
	private void stopPollingLocations(){
		if(mPollingTimer != null)
			mPollingTimer.cancel();
	}


	/**
	 * The Class GameStateRoundMrX is used to handle the target for Mr.X. The agents can only request the 
	 * global beans for the GameStatePlay, but cannot set their next target.
	 */
	private class GameStateRoundMrX extends SubGameState {

		/**
		 * Instantiates a new GameStateRoundMrX.
		 */
		@SuppressWarnings("unchecked")
		public GameStateRoundMrX(){
			LOGGER.info("SubGameState: GameStateRoundMrX");
			
			// Reset the last target of Mr.X
			game.clearMisterXTarget();
			// Increase the round number
			game.setRound(game.getRound() + 1);
			
			// If the maximum of game rounds has been reached --> mrx won
			// Switch to GameStateGameOver
			if(game.getRound() > control.getSettings().getRounds()){
				setGameOver("End of game reached. Mr.X was not caught by the agents.");
			}
			// If Mr.X cannot move anymore, the game is also over
			else if(game.getRouteManagement().isPlayerUnmovable(game.getMisterX())){
				setGameOver("End of game reached. Mr.X is out of tickets.");
			}
			// Else send StartRoundIQ to Mr.X
			else {
				List<TicketAmount> ticketsMrX = new ArrayList< TicketAmount >();
				for ( Map.Entry< Integer, Integer > entry : control.getSettings().getTicketsMrX().entrySet() ) {
					ticketsMrX.add( new TicketAmount(entry.getKey(), entry.getValue()) );
				}
				
				control.getConnection().getProxy().StartRound( 
						game.getMisterX().getJid(), 
						game.getRound(), 
						true, 
						ticketsMrX, 
						new EmptyCallback());
			}
		}
		
		/* (non-Javadoc)
		 * @see de.tudresden.inf.rn.mobilis.server.services.xhunt.state.SubGameState#processPacket(de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean)
		 */
		@Override
		public void processPacket(XMPPBean inBean) {
			// players shouldn't send target requests if they are still marked as offline
			XHuntPlayer sender = game.getPlayerByJid(inBean.getFrom());
			if(sender != null && !sender.isOnline()) {
				inBean.errorType = "wait";
				inBean.errorCondition = "unexpected-request";
				inBean.errorText = "Player is still marked as offline";
				
				control.getConnection().sendXMPPBeanError(
						inBean,
						inBean
				);	
			}
			
			// normal case
			else if( inBean instanceof TargetRequest ) {
				handleTargetBean((TargetRequest) inBean);
			}
			
			// Only TargetBean of Mr.X is supposed in here
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
		
		/**
		 * Handle TargetBean just for Mr.X.
		 *
		 * @param inBean the TargetBean which contains the new target information
		 */
		private void handleTargetBean(TargetRequest inBean){
			XHuntPlayer playerMrX = game.getPlayerByJid(inBean.getFrom());
			
			if(playerMrX != null && playerMrX.isMrx()){
				// If target is final decision (Mr.X cannot make suggestions)
				if(inBean.getIsFinal()){
					// If Mr.X has enough tickets available to use this route
					if(playerMrX.getTicketsAmount().get(inBean.getTicketId()) != null
							&& playerMrX.getTicketsAmount().get(inBean.getTicketId()) > 0){			
						LOGGER.info("TargetId: " + inBean.getStationId());
						
						// If Mr.X can reach his target from his current station
						if(game.getRouteManagement().isTargetReachable(inBean.getStationId(), playerMrX)){
							// If Mr.X is synch with server round
							if(inBean.getRound() == game.getRound()){
								// Store old target into last station for navigation
								playerMrX.setCurrentTargetToLastStation();
								// Replace old target with new one
								playerMrX.setCurrentTarget(inBean.getStationId());
								// Decrease amount of used ticket by 1
								playerMrX.decreaseTicket(inBean.getTicketId());
								
								// Confirm new target to Mr.X
								control.getConnection().getProxy().Target( 
										inBean.getFrom(), 
										inBean.getId(), 
										inBean.getTicketId() );
								
								// Notify Mr.X about his new target to synch client and server
								sendRoundStatusBeanForMrX( null );
								
								// Switch to GameStateRoundAgents
								mSubState = new GameStateRoundAgents();
							}
							// Mr.X is not synch with server, respond an error
							else{
								control.getConnection().getProxy().getBindingStub().sendXMPPBean( inBean.buildPlayerSynchronizationFault( null ) );
								
								// Try to synchronize Mr.X with actual server data (sends Snapshots to him)
								control.getConnection().disableNotRespondingPlayer(playerMrX.getJid());
							}
						}
						// Target station is not reachable from Mr.Xs current station, respond an error
						else{
							control.getConnection().getProxy().getBindingStub()
								.sendXMPPBean( inBean.buildInvalidTargetFault( "Target station is not reachable from current." ));
						}
					}
					// If Mr.Xs amount of tickets is to low to use this route
					else {
						// If player is unmovable
						if(game.getRouteManagement().isPlayerUnmovable(playerMrX)){
							// test if he knows it (ticketId == 0)
							if(inBean.getTicketId() == Settings.TICKET_ID_UNMOVABLE){
								control.getConnection().getProxy().Target( 
										inBean.getFrom(), 
										inBean.getId(),
										inBean.getTicketId() );
							}
							else{
								control.getConnection().getProxy().getBindingStub()
									.sendXMPPBean( inBean.buildInvalidTargetFault( "You're unmovable at this position." ));
							}
						}
						// everything else is not accepted
						else {
							control.getConnection().getProxy().getBindingStub()
								.sendXMPPBean( inBean.buildInvalidTargetFault( "Not enough tickets available." ));
						}
					}
				}
				else{
					control.getConnection().getProxy().getBindingStub()
						.sendXMPPBean( inBean.buildInvalidTargetFault( "Target has to be final." ));
				}
			}
			else{
				inBean.errorType = "wait";
				inBean.errorCondition = "unexpected-request";
				inBean.errorText = "This request is not supportet at this game state";
				
				control.getConnection().sendXMPPBeanError(
						inBean,
						inBean
				);
			}
		}
	}
	
	
	/**
	 * The Class GameStateRoundAgents is used to handle the targets for the agents. Mr.X can only request the 
	 * global beans for the GameStatePlay, but cannot set his next target. 
	 * as long as not all players reached and stay at their target, this GameState will be 
	 * exist, else it will be switched toGameStateMrX.
	 */
	private class GameStateRoundAgents extends SubGameState{	

		/**
		 * Instantiates a new GameStateRoundAgents.
		 */
		@SuppressWarnings("unchecked")
		public GameStateRoundAgents(){
			LOGGER.info("SubGameState: GameStateRoundAgents");
			
			// set previously unavailable Agents to online if they returned
			control.getConnection().setReturneesToOnline();
			
			// Reset targets of the agents
			game.clearAgentTargets();
			
			// Notify agents about the start of the new round, containing the current number of round, 
			// if Mr.X is visible and the amount of tickets for each agent
			for(XHuntPlayer playerAgent : game.getAgents()){
				List<TicketAmount> tickets = new ArrayList< TicketAmount >();
				for ( Map.Entry< Integer, Integer > entry : playerAgent.getTicketsAmount().entrySet() ) {
					tickets.add( new TicketAmount(entry.getKey(), entry.getValue()) );
				}
				
				control.getConnection().getProxy().StartRound( 
						playerAgent.getJid(), 
						game.getRound(), 
						game.showMisterX(),
						tickets,
						new EmptyCallback());
			}
		}
		
		/* (non-Javadoc)
		 * @see de.tudresden.inf.rn.mobilis.server.services.xhunt.state.SubGameState#processPacket(de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean)
		 */
		@Override
		public void processPacket(XMPPBean inBean) {
			// players shouldn't send target requests if they are still marked as offline
			XHuntPlayer sender = game.getPlayerByJid(inBean.getFrom());
			if(sender != null && !sender.isOnline()) {
				inBean.errorType = "wait";
				inBean.errorCondition = "unexpected-request";
				inBean.errorText = "Player is marked as offline until start of next round";
				
				control.getConnection().sendXMPPBeanError(
						inBean,
						inBean
				);	
			}
			
			// normal case
			else if( inBean instanceof TargetRequest ) {
				handleTargetBean((TargetRequest) inBean);
			}
			
			// only TargetRequests allowed here
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
		
		/**
		 * Handle TargetBean just for the agents.
		 *
		 * @param inBean the TargetBean which contains the new target information
		 */
		private void handleTargetBean(TargetRequest inBean){		
			XHuntPlayer player = game.getPlayerByJid(inBean.getFrom());
			
			// Mr.X cannot set his next target in this GameState
			if(player != null && !player.isMrx()){
				// If agent can reach his target from his current station
				if(game.getRouteManagement().isTargetReachable(inBean.getStationId(), player)){
					// If agent has enough tickets available to use this route
					if(player.getTicketsAmount().get(inBean.getTicketId()) != null
							&& player.getTicketsAmount().get(inBean.getTicketId()) > 0){
						// If Mr.X is synch with server round
						if(inBean.getRound() == game.getRound()){
							// If target is final decision, not a suggestion
							if(inBean.getIsFinal()){
								// Store old target into last station for navigation
								player.setCurrentTargetToLastStation();	
								// Decrease amount of used ticket by 1
								player.decreaseTicket(inBean.getTicketId());
								// Increase amount of used ticket for Mr.X by 1
								game.getMisterX().increaseTicket(inBean.getTicketId());
							}
							
							// Replace old target with new one
							player.setCurrentTarget(inBean.getStationId());
							// Store final state of chosen station
							player.setCurrentTargetFinal(inBean.getIsFinal());
							
							// Confirm selected target to the agent
							control.getConnection().getProxy().Target( 
									inBean.getFrom(), 
									inBean.getId(), 
									inBean.getTicketId() );
							
							// Notify all players about the new (suggested)target
							sendRoundStatusBean();
						}
						// Else agent is not sync with the server, respond an error
						else{
							control.getConnection().getProxy().getBindingStub().sendXMPPBean( inBean.buildPlayerSynchronizationFault( null ) );
							
							// Try to synchronize agent with actual server data (sends Snapshots to him)
							control.getConnection().disableNotRespondingPlayer(player.getJid());
						}
					}
					else{
						// If player is unmovable
						if(game.getRouteManagement().isPlayerUnmovable(player)){
							// test if he knows it (ticketId == 0)
							if(inBean.getTicketId() == Settings.TICKET_ID_UNMOVABLE){
								control.getConnection().getProxy().Target( 
										inBean.getFrom(), 
										inBean.getId(), 
										inBean.getTicketId() );
							}
							else{
								control.getConnection().getProxy().getBindingStub()
									.sendXMPPBean( inBean.buildInvalidTargetFault( "You're unmovable at this position." ));
							}
						}
						// If ticketId == -1, its a suggestion
						else if(inBean.getTicketId() == Settings.TICKET_ID_SUGGESTION){
							player.setCurrentTarget(inBean.getStationId());
							player.setCurrentTargetFinal(inBean.getIsFinal());
							
							control.getConnection().getProxy().Target( 
									inBean.getFrom(), 
									inBean.getId(), 
									inBean.getTicketId() );
							
							sendRoundStatusBean();
						}
						else if(inBean.getTicketId() > 0){
							control.getConnection().getProxy().getBindingStub()
								.sendXMPPBean( inBean.buildInvalidTargetFault( "Not enough tickets available." ));
						}
					}
				}
				else{
					control.getConnection().getProxy().getBindingStub()
						.sendXMPPBean( inBean.buildInvalidTargetFault( "Target station is not reachable from current." ));
				}
				
			}
			else{
				inBean.errorType = "wait";
				inBean.errorCondition = "unexpected-request";
				inBean.errorText = "This request is not supportet at this game state";
				
				control.getConnection().sendXMPPBeanError(
						inBean,
						inBean
				);
			}
		}
	}


	public XMPPBean onDeprtureData( DepartureDataRequest in ) {
		XMPPBean out = Connection.createXMPPBeanResult( new DepartureDataResponse(), in );
		control.getConnection().getProxy().getBindingStub().sendXMPPBean( out );
		
		return out;
	}

	
	public void onLocation( LocationResponse in ) {
		control.getConnection().handleCallback( in );
	}


	/**
	 * Handle PlayerExitBean. This will handle if a players exit the game by himself.
	 * If there not enough players left for 
	 * the game, or if Mr.X left the game, the game will be shutdown.
	 *
	 * @param inBean the PlayerExitBean which contain the jid of the exit player
	 */
	public XMPPBean onPlayerExit( PlayerExitRequest inBean ) {
		XMPPBean out = null;
		XHuntPlayer exitPlayer = game.getPlayerByJid(inBean.getJid());
		
		if(exitPlayer != null){
			//player can only leave the current game
			if(exitPlayer.getJid().equals(inBean.getFrom())){
				
				game.removePlayer(exitPlayer);
				
				// Confirm exit player
				out = control.getConnection().getProxy().PlayerExit( 
						inBean.getFrom(), inBean.getId() );
				
				String gameOverReason = null;
				
				// check for game over conditions
				List<XHuntPlayer> availablePlayers = new ArrayList<XHuntPlayer>();
				for(XHuntPlayer plr : game.getPlayers().values())
					if (plr.isOnline())
						availablePlayers.add(plr);
				
				if(exitPlayer.isMrx()) gameOverReason = "Mr.X has left!";
				else if(availablePlayers.size() < control.getSettings().getMinPlayers())
					gameOverReason = "Not enough players to carry on with this game!";
				
				// If game over happens notify players
				if(gameOverReason != null){
					setGameOver(gameOverReason);
				}
				else{					
					// Notify rest of players about the exit player
					sendPlayersBean("Player " + exitPlayer.getName() + " has left the game.", null);
					sendRoundStatusBean();
				}
			}
			else{
				out = inBean.buildPermissionFault( null );
				control.getConnection().getProxy().getBindingStub().sendXMPPBean( out );
			}
		}
		else{
			out = inBean.buildPermissionFault( "Player not found." );
			control.getConnection().getProxy().getBindingStub().sendXMPPBean( out );
		}
		
		return out;
	}

	public void onPlayers( PlayersResponse in ) {
		control.getConnection().handleCallback( in );
	}

	public void onRoundStatus( RoundStatusResponse in ) {
		control.getConnection().handleCallback( in );
	}

	public void onStartRound( StartRoundResponse in ) {
		control.getConnection().handleCallback( in );
	}

	/**
	 * Handle UsedTicketsBean to respond a list with used tickets for all players.
	 *
	 * @param inBean the UsedTicketsBean which is empty
	 */
	//@Override
	public XMPPBean onUsedTickets( UsedTicketsRequest in ) {
		List<UsedTicketsInfo> usedTickets = new ArrayList< UsedTicketsInfo >();
		
		for ( Map.Entry< String, ArrayList< Integer > > entry : game.getUsedTickets().entrySet() ) {
			usedTickets.add( new UsedTicketsInfo( entry.getKey(), entry.getValue() ) );
		}
		
		return control.getConnection().getProxy().UsedTickets( 
				in.getFrom(), 
				in.getId(), 
				usedTickets );
	}
	
	/**
	 * Returns either {@link SUBSTATE_MRX} or {@link SUBSTATE_AGENTS}.
	 * @return a final Integer value identifying the SubGameState.
	 */
	public int getSubGameStateID() {
		return (mSubState instanceof GameStateRoundMrX) ? SUBSTATE_MRX : SUBSTATE_AGENTS;
	}
}
