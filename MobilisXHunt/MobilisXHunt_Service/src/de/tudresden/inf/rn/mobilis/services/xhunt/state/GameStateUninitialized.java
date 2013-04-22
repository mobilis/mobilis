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
import java.util.logging.Logger;

import de.tudresden.inf.rn.mobilis.services.xhunt.Game;
import de.tudresden.inf.rn.mobilis.services.xhunt.XHunt;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.AreaInfo;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.AreasRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.CancelTimerRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.CreateGameRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.JoinGameRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.TicketAmount;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * The Class GameStateUninitialized is the first state when this service starts.
 */
public class GameStateUninitialized extends GameState {
	
	/** The class specific Logger object. */
	private final static Logger LOGGER = Logger.getLogger(GameStateUninitialized.class.getCanonicalName());
	
	/**
	 * Instantiates a new GameStateUninitialized.
	 *
	 * @param control the service controller
	 * @param game the game instance of this service
	 */
	public GameStateUninitialized(XHunt control, Game game)
	{
		this.control = control;
		this.game = game;
	}
	
	/* (non-Javadoc)
	 * @see de.tudresden.inf.rn.mobilis.server.services.xhunt.state.GameState#processPacket(de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean)
	 */
	@Override
	public void processPacket(XMPPBean inBean) {
		// If a needed Bean for this game was matched, it will be 
		// converted to the special Bean and will be handled in a 
		// separate function
		if( inBean instanceof AreasRequest){
			onAreas((AreasRequest) inBean);
		}
		else if( inBean instanceof CreateGameRequest){
			onCreateGame((CreateGameRequest) inBean);
		}
		/*else if( inBean instanceof GameDetailsRequest){
			onGameDetails((GameDetailsRequest)inBean);
		}*/
		else if( inBean instanceof JoinGameRequest){
			onJoinGame((JoinGameRequest) inBean);
		}
		else {
			// If there will be no expected Bean match, we send 
			// an ERROR Bean as result back that this Bean was not 
			// expected at this time
			inBean.errorType = "wait";
			inBean.errorCondition = "unexpected-request";
			inBean.errorText = "This request is not supportet at this game state";
			
			// Send ERROR Bean (copies original Bean an append an error tag)
			control.getConnection().sendXMPPBeanError(
					inBean,
					inBean
			);			
		}
	}
	
	/**
	 * Handle JoinGameBean. In this GameState there is no JoinGameBean permitted. 
	 * It's only possible to configure the game properties and start the game. 
	 * JoinGameBean is only allowed in GameState @see GameStateLobby 
	 *
	 * @param inBean the KoinGameBean
	 */
	//@Override
	public XMPPBean onJoinGame( JoinGameRequest in ) {
		XMPPBean out = in.buildClosedGameFault( "This GameService is not yet configured properly." );
		
		control.getConnection().getProxy().getBindingStub().sendXMPPBean( out );
		
		return out;
	}
	
	
	/**
	 * Handle CreateGameBean. This function will be called if a client 
	 * sets up a new game for this service. At this point the service is 
	 * already started but the Game property 'isOpen' is false until the 
	 * game was configured successfully.
	 *
	 * @param inBean the CreateGameBean which contains the set up configuration
	 */
	//@Override
	public XMPPBean onCreateGame( CreateGameRequest inBean ) {
		String errorText = "";
		
		control.getSettings().setAreaId(inBean.getAreaId());
		control.getSettings().setGameName(inBean.getGameName());
		control.getSettings().setGamePassword(inBean.getGamePassword());
		
		if(inBean.getCountRounds() < 21)
			control.getSettings().setRounds(inBean.getCountRounds());
		else 
			errorText += " max round: 21";
		
		if(inBean.getMinPlayers() > 0)
			control.getSettings().setMinPlayers(inBean.getMinPlayers());
		else 
			errorText += " min players: 1";
		
		if(inBean.getMaxPlayers() < 7)
			control.getSettings().setMaxPlayers(inBean.getMaxPlayers());
		else 
			errorText += " max players: 7";
		
		if(inBean.getStartTimer() > 0)
			control.getSettings().setStartTimer(inBean.getStartTimer());
		else 
			errorText += " starttimer to low";
		
		if(inBean.getLocPollingInterval() > 0)
			control.getSettings().setLocationPollingIntervalMillis(inBean.getLocPollingInterval());
		else if(inBean.getLocPollingInterval() != Integer.MIN_VALUE)
			errorText += " location update interval too low";
		else if(inBean.getLocPollingInterval() == Integer.MIN_VALUE)
			LOGGER.info("Client didn't send location polling interval (old version?); using default value");
		
		if(errorText.length() == 0){
			for ( TicketAmount ticketAmount : inBean.getTicketsMrX().getTicketsMrX() ) {
				control.getSettings().putTicketMrX(ticketAmount.getID(), ticketAmount.getAmount());
			}
			
			for ( TicketAmount ticketAmount : inBean.getTicketsAgents().getTicketsAgents() ) {
				control.getSettings().putTicketsAgents( ticketAmount.getID(), ticketAmount.getAmount() );
			}
			
			// Make game joinable for other players
			game.setGameIsOpen(true);
			
			// Switch GameState to GameStateLobby
			game.setGameState(new GameStateLobby(control, game));
			LOGGER.info("Status changed to GameStateLobby");			
			
			// Confirm that the game was configured successfully and is open
			return control.getConnection().getProxy().CreateGame( inBean.getFrom(), inBean.getId() );
		}
		else {
			// Send an ERROR with the related error text defined before 
			// (depends on first mismatched configuration attribute)
			XMPPBean out = inBean.buildInputDataFault( "Errors: " + errorText );			
			control.getConnection().getProxy().getBindingStub().sendXMPPBean( out );
			
			return out;
		}
	}
	
	//@Override
	public XMPPBean onCancelStartTimer( CancelTimerRequest in ) {return null;}
	
	/**
	 * Handle AreasBean. This function will be send back a list of available 
	 * areas.
	 *
	 * @param inBean the AreasBean request
	 */
	//@Override
	public XMPPBean onAreas( AreasRequest in ) {
		// Query all areas from database
		ArrayList<AreaInfo> areas = control.getSqlHelper().queryAreas();
		
		return control.getConnection().getProxy().Areas( 
				in.getFrom(), 
				in.getId(), 
				areas );
	}
}

