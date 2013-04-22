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
import de.tudresden.inf.rn.mobilis.services.xhunt.XHunt;
import de.tudresden.inf.rn.mobilis.services.xhunt.helper.EmptyCallback;
import de.tudresden.inf.rn.mobilis.services.xhunt.model.XHuntPlayer;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.AreasRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.CancelTimerRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.CreateGameRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.DepartureDataRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.DepartureDataResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.GameDetailsRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.LocationInfo;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.LocationRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.LocationResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.PlayerExitRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.PlayersResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.RoundStatusResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.SnapshotResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.StartRoundResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.TicketAmount;
import de.tudresden.inf.rn.mobilis.xmpp.beans.IXMPPCallback;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * First round of the game. The start positions for the players 
 * are send. If all players are at the start positions, the state 
 * changes to GameStateRoundMrX.
 */
public class GameStateRoundInitial extends GameState{
	
	/** True, if checking for all players locations. */
	private boolean mIsFirstLocationRequest = true;
	
	/** The timer which polls the location of all players if ticks. */
	private Timer mPollingTimer = null;
	
	/** The start round timer for Mr.X. */
	private Timer mStartTimerMrX;
	
	/** The start round timer for the agents. */
	private Timer mStartTimerAgents;
	
	/** True, if start round timer for Mr.X is running. */
	private boolean mIsStartTimerMrxRunning;
	
	/** True, if start round timer for the agents is running. */
	private boolean mIsStartTimerAgentsRunning;
	
	/** The class specific Logger object. */
	private final static Logger LOGGER = Logger.getLogger(GameStateRoundInitial.class.getCanonicalName());

	/**
	 * Instantiates a new GameStateRoundInitial.
	 *
	 * @param control the service controller
	 * @param game the game instance of this service
	 */
	public GameStateRoundInitial(XHunt control, Game game){
		this.control = control;
		this.game = game;		
		
		startInitialRound();
	}
	
	/* (non-Javadoc)
	 * @see de.tudresden.inf.rn.mobilis.server.services.xhunt.state.GameState#processPacket(de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean)
	 */
	@Override
	public void processPacket(XMPPBean inBean) {
		
		if(inBean instanceof CancelTimerRequest){
			onCancelStartTimer((CancelTimerRequest)inBean);
		}
		else if( inBean instanceof GameDetailsRequest){
			handleGameDetailsBean((GameDetailsRequest)inBean);
		}
		else if( inBean instanceof DepartureDataRequest){
			onDeprtureData( (DepartureDataRequest)inBean);
		}
		else if( inBean instanceof LocationResponse){
			onLocation((LocationResponse) inBean);
		}
		else if( inBean instanceof LocationRequest && inBean.getType() == XMPPBean.TYPE_ERROR){
			
		}
		else if( inBean instanceof PlayerExitRequest){
			onPlayerExit((PlayerExitRequest) inBean);
		}
		else if( inBean instanceof StartRoundResponse){
			onStartRound((StartRoundResponse) inBean);
		}
		else if( inBean instanceof PlayersResponse){
			// Just result of the player
			onPlayers( (PlayersResponse)inBean);
		}
		else if( inBean instanceof RoundStatusResponse){
			// Just result of the player
			control.getConnection().handleCallback( inBean );
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
	
	/**
	 * Dismiss the start timer for the agents.
	 */
	@SuppressWarnings("unchecked")
	private void dismissStartTimerAgents(){
		for(XHuntPlayer player : game.getAgents())
			// If a player doesn't provide a location after timer dismissed, 
			// the player will be removed from game
			if(!game.setInitialTarget(player)){
				game.removePlayer(player);
				
				// If not enough players left to continue, notify players
				if(game.getPlayers().size() < control.getSettings().getMinPlayers()){
					setGameOver("Not enough players to carry on with this game!");
				}
				else{
					sendPlayersBean("Player " + player.getName() + " can not provide a geo location and was kicked.", new EmptyCallback());
				}
			}
		
		// Tell the agents their nearest station to start at
		sendRoundStatusBeanForAgents(new EmptyCallback());
		mIsStartTimerAgentsRunning = false;
	}
	
	/**
	 * Dismiss start timer for Mr.X.
	 */
	@SuppressWarnings("unchecked")
	private void dismissStartTimerMrX(){
		// If Mr.X doesn't provide a location after timer dismissed, 
		// the game will be shutdown
		if(!game.setInitialTarget(game.getMisterX())){
			setGameOver("Mr.X can not provide a geo location.");
		}
		// Else tell Mr.X his nearest station to start at
		else{			
			sendRoundStatusBeanForMrX(new EmptyCallback());
		}
		
		mIsStartTimerMrxRunning = false;		
	}

	
	/**
	 * Handle handleLocationBean. This will update the location of a player. 
	 * If all players reached their start station, the GameState will switch to 
	 * GameStatePlay.
	 *
	 * @param inBean the LocationBean which contains the location information of a player
	 */
	private IXMPPCallback< LocationResponse > LocationCallback = new IXMPPCallback< LocationResponse >() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void invoke( LocationResponse inBean ) {
			XHuntPlayer updatePlayer = null;
			
			LOGGER.info("Location received: player: " + inBean.getFrom() 
					+ " loc: " + inBean.getLocationInfo().getLatitude() + ";" + inBean.getLocationInfo().getLongitude()
					+ " time: " + System.currentTimeMillis());
			
			// Update the location of a player
			if(null != inBean.getLocationInfo()){
				updatePlayer = game.getPlayerByJid(inBean.getLocationInfo().getJid());
				updatePlayer.setGeoLocation(inBean.getLocationInfo().getLatitude(), inBean.getLocationInfo().getLongitude());
			}
			else{
				// no empty iq allowed
				return;
			}
			
			// In initial round we need the locations of all players. If each player 
			// provides his location, mIsFirstLocationRequest will be true and periodically 
			// polling of players location will be start
			if(mIsFirstLocationRequest){
				boolean allLocationsAvailable = true;
				
				for(XHuntPlayer player : game.getPlayers().values()){
					allLocationsAvailable = allLocationsAvailable && ( player.getGeoLocation() != null);
				}
				
				if(allLocationsAvailable){
					mIsFirstLocationRequest = false;
					startLocationPolling(5000);
				}
			}

			// If Mr.X reached his target, inform just Mr.X. 
			// If an agent reaches his target inform all agents
			if(game.isPlayerAtTarget(updatePlayer)){
				updatePlayer.setReachedTarget(true);

				if(updatePlayer.isMrx())
					sendRoundStatusBeanForMrX(new EmptyCallback());
				else
					sendRoundStatusBeanForAgents(new EmptyCallback());
			}
			
			// If each player has reach his target, stop polling locations in this GameState 
			// and switch to GameStatePlay.
			if(game.areAllPlayersAtTarget() && game.getMisterX().isOnline()){
				stopPollingLocations();
				game.setGameState(new GameStatePlay(control, game));
			}
		}
	};
	
	@SuppressWarnings("unchecked")
	private void setGameOver(String reason){
		game.setGameIsOpen(false);
		
		// Cancel start timers if they are running
		if(mStartTimerAgents != null)
			mStartTimerAgents.cancel();
		
		if(mStartTimerMrX != null)
			mStartTimerMrX.cancel();
		
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
	 * Start initial round. This will send the StartRoundBean, which contains the 
	 * the current round number, if Mr.X is visible and the amount of tickets.
	 */
	@SuppressWarnings("unchecked")
	private void startInitialRound(){
		// init and send tickets to mrx
		if(game.getMisterX().getTicketsAmount().size() < 1)
			game.getMisterX().setTicketsAmount(control.getSettings().getTicketsMrX());
		
		List<TicketAmount> ticketsMrX = new ArrayList< TicketAmount >();
		for ( Map.Entry< Integer, Integer > entry : game.getMisterX().getTicketsAmount().entrySet() ) {
			ticketsMrX.add( new TicketAmount(entry.getKey(), entry.getValue()) );
		}
		
		control.getConnection().getProxy().StartRound( 
				game.getMisterX().getJid(), 
				game.getRound(), 
				true, 
				ticketsMrX, 
				new EmptyCallback());
		
		
		// init and send tickets to agents
		for(XHuntPlayer player : game.getAgents()){
			if( player.getTicketsAmount().size() < 1 )
				player.setTicketsAmount(control.getSettings().getTicketsAgents());
			
			List<TicketAmount> ticketsAgents = new ArrayList< TicketAmount >();
			for ( Map.Entry< Integer, Integer > entry : player.getTicketsAmount().entrySet() ) {
				ticketsAgents.add( new TicketAmount(entry.getKey(), entry.getValue()) );
			}
			
			control.getConnection().getProxy().StartRound( 
					player.getJid(), 
					game.getRound(), 
					true, 
					ticketsAgents, 
					new EmptyCallback());
		}
		
		
		// Start the start round timers for all players
		mStartTimerMrX = new Timer();
		mStartTimerMrX.schedule(
			new TimerTask() {
				public void run() {
					dismissStartTimerMrX();
		        }
		}, control.getSettings().getStartTimer());
		mIsStartTimerMrxRunning = true;
    	
		mStartTimerAgents = new Timer();
		mStartTimerAgents.schedule(
			new TimerTask() {
				public void run() {
					dismissStartTimerAgents();
		        }
		}, control.getSettings().getStartTimer() / 2);
		mIsStartTimerAgentsRunning = true;
    	
	}
	
	/**
	 * Start location polling. This will poll the location of all players periodically. 
	 * This will also update the locations on clientside of the players.
	 */
	private void startLocationPolling(long delay){
		LOGGER.info("Start polling locations. Interval = " + control.getSettings().getLocationPollingIntervalMillis() /1000
				+ "sec; Delay = " + delay/1000 + "sec");
		// Define and start the polling timer
		if (mPollingTimer!=null) {
			mPollingTimer.cancel();
		}
		
		mPollingTimer = new Timer();
		mPollingTimer.schedule(
			new TimerTask() {
				public void run() {
					System.out.println("GameStateRoundInitial Sending LocationRequests");
					ArrayList<LocationInfo> infos = new ArrayList<LocationInfo>();
					XHuntPlayer playerMrX = null;
					
					// Collect the locations of all agents
					for(XHuntPlayer player : game.getPlayers().values()){
						if(!player.isMrx() && player.getGeoLocation()!=null){
							infos.add(new LocationInfo(
									player.getJid(),
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
						if (playerMrX.getGeoLocation()!=null) {
							infos.add(new LocationInfo(
									playerMrX.getJid(),
									playerMrX.getGeoLocation().getLatitudeE6(),
									playerMrX.getGeoLocation().getLongitudeE6(),
									playerMrX.isOnline()));
						}
						
						control.getConnection().getProxy().Location( 
								game.getMisterX().getJid(), 
								infos,
								playerMrX.isOnline(),
								LocationCallback );
						
						LOGGER.info("Sending LocationRequests done");
					}
		        }
				// Timer will be start after 5000 ms
		}, delay, control.getSettings().getLocationPollingIntervalMillis());
	}
	
	/**
	 * Stop polling locations.
	 */
	private void stopPollingLocations(){
		if(mPollingTimer != null)
			mPollingTimer.cancel();
	}

	public XMPPBean onAreas( AreasRequest in ) {
		return null;
	}

	/**
	 * Handle CancelStartTimerBean to cancel the start timer. Just Mr.X is permitted 
	 * to use this functionality. All players will directed to their nearest station 
	 * to start at.
	 *
	 * @param inBean the CancelStartTimerBean to cancel the start timer
	 */
	public XMPPBean onCancelStartTimer( CancelTimerRequest in ) {
		// If start timer for agents is still running, it will be dismissed
		if( mIsStartTimerAgentsRunning
				&& mStartTimerAgents != null ){
			mStartTimerAgents.cancel();
			dismissStartTimerAgents();
		}
		
		// If start timer of Mr.X is still running, it will be dismissed
		if( mIsStartTimerMrxRunning
				&& mStartTimerMrX != null ){
			mStartTimerMrX.cancel();
			dismissStartTimerMrX();
		}
		
		// Confirm the cancel of the start timer to Mr.X
		return control.getConnection().getProxy().CancelStartTimer(in.getFrom(), in.getId());
	}

	public XMPPBean onCreateGame( CreateGameRequest in ) {return null;}

	public XMPPBean onDeprtureData( DepartureDataRequest in ) {
		//TODO: handle departures, there is currently no solution implemented
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
			// If a player wants to leave the game
			if(exitPlayer.getJid().equals(inBean.getFrom())){
				
				game.removePlayer(exitPlayer);
				
				// Confirm exit player
				out = control.getConnection().getProxy().PlayerExit( 
						inBean.getFrom(), inBean.getId() );
				
				String gameOverReason = null;
				
				// Check for game over conditions
				if(exitPlayer.isMrx()) gameOverReason = "Mr.X has left!";
				else if(game.getPlayers().size() < control.getSettings().getMinPlayers())
					gameOverReason = "Not enough players to carry on with this game!";
				
				// If game over happens notify all players
				if(gameOverReason != null){
					setGameOver(gameOverReason);
				}
				else {					
					// Notify rest of players about the exit player
					sendPlayersBean("Player " + exitPlayer.getName() + " has left the game.", null);
					
					// Test again if all players reached their targets
					boolean allPlayersReachedTargets = true;
					for(XHuntPlayer player : game.getPlayers().values()){
						allPlayersReachedTargets = allPlayersReachedTargets && game.isPlayerAtTarget(player);
					}
					
					// If all players reached their targets, switch to GameStatePlay
					if(allPlayersReachedTargets) {
						stopPollingLocations();
						game.setGameState(new GameStatePlay(control, game));
					}
				}
			}
			// If player name in Bean and the sender are not equal, respond an error
			else {
				out = inBean.buildPermissionFault( null );
				control.getConnection().getProxy().getBindingStub().sendXMPPBean( out );
			}
		}
		// If player was not found, respond an error
		else{
			out = inBean.buildPermissionFault( "Player not found." );
			control.getConnection().getProxy().getBindingStub().sendXMPPBean( out );
		}
		
		return out;
	}

	
	public void onPlayers( PlayersResponse in ) {
		control.getConnection().handleCallback( in );
	}


	/**
	 * Handle StartRoundBean.
	 *
	 * @param inBean the StartRoundBean
	 */
	
	public void onStartRound( StartRoundResponse in ) {
		// Confirm the start of the round
		/*for ( String toJid : game.getPlayers().keySet() ) {
			control.getConnection().getProxy().Location( 
					toJid, 
					new ArrayList< LocationInfo >(),
					LocationCallback);		
		}*/
		startLocationPolling(0);
	}
}
