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

import de.tudresden.inf.rn.mobilis.services.xhunt.Connection;
import de.tudresden.inf.rn.mobilis.services.xhunt.Game;
import de.tudresden.inf.rn.mobilis.services.xhunt.XHunt;
import de.tudresden.inf.rn.mobilis.services.xhunt.helper.EmptyCallback;
import de.tudresden.inf.rn.mobilis.services.xhunt.model.XHuntPlayer;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.GameDetailsRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.GameDetailsResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.PlayersResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.RoundStatusInfo;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.RoundStatusResponse;
import de.tudresden.inf.rn.mobilis.xmpp.beans.IXMPPCallback;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * The GameState Class which provides a skeleton and global 
 * functions for a GameState instance.
 */
public abstract class GameState {

	/** The service controller. */
	protected XHunt control;
	
	/** The Game class which provides all necessary game data. */
	protected Game game;
	
	/**
	 * Handles the IQ packet in the corresponding game state. 
	 * Has to be overwritten by all subclasses.
	 *
	 * @param bean the bean
	 */
	public abstract void processPacket(XMPPBean bean);
	
	/**
	 * Handles the GameDetailsBean which requests detailed information 
	 * about the current game. This function is available in each GameState instance.
	 *
	 * @param inBean the GameDetailsBean requested packet
	 */
	protected void handleGameDetailsBean(GameDetailsRequest inBean){
		// If there is currently no game available, answer with an empty GameDetailsBean
		if(game != null){
			ArrayList<String> playernames = new ArrayList<String>();
			
			// Collect names of current game players
			for(XHuntPlayer player : game.getPlayers().values()){
				playernames.add(player.getName() + "; ");
			}
			
			// Send a result GameDetailsBean which provides
			// name of the game, if game is protected by a password, 
			// configured amount of rounds, the start timer for Mr.X, 
			// all player names and the information if the game is open 
			// for players to join it
			control.getConnection().getProxy().GameDetails( 
					inBean.getFrom(),
					inBean.getId(),
					control.getSettings().getGameName(),
					(control.getSettings().getGamePassword() != null
						&& control.getSettings().getGamePassword().length() > 0),
						control.getSettings().getRounds(),
						control.getSettings().getStartTimer(),
						playernames,
						game.isGameOpen()
					);
			
			
			/*sendXMPPBeanResult(
					new GameDetailsBean(control.getServiceName(),
							(control.getSettings().getGamePassword() != null
									&& control.getSettings().getGamePassword().length() > 0),
							control.getSettings().getRounds(),
							control.getSettings().getStartTimer(),
							playernames,
							game.isGameOpen()
					),
					inBean
			);*/
		}
		else{
			// Send an empty result of GameDetailsBean
			control.getConnection().getProxy().getBindingStub().sendXMPPBean(  
					Connection.createXMPPBeanResult( new GameDetailsResponse(), inBean ) );
		}
	}
	
	/**
	 * Notifies all players about the player states status (even sender). 
	 * This Bean contains all attributes about all players like 'isReady' or 
	 * 'isMrX' and an optional information text which will be displayed if this 
	 * Bean arrives on client side.
	 *
	 * @param info an optional information text which will be displayed on client side
	 */
	@SuppressWarnings("unchecked")
	public void sendPlayersBean(String info, IXMPPCallback< PlayersResponse > callback){
		if(game != null && control != null){
			System.out.println("gameplayers: " + game.getPlayers().size());
			
			// Send a result PlayersBean to _ALL_ players
			for ( String toJid : game.getPlayers().keySet() ) {
				control.getConnection().getProxy().Players( 
						toJid, 
						game.getPlayerInfos(), 
						info,
						callback != null
							? callback
							: new EmptyCallback() );
			}			
			
			/*sendXMPPBean(
					new PlayersBean(game.getPlayerInfos(), info),
					// JIDs of all players
					game.getPlayers().keySet(),
					XMPPBean.TYPE_SET
			);*/
		}
	}
	
	/**
	 * Send RoundStatusBean to inform all players about the state of the current round.
	 */
	@SuppressWarnings("unchecked")
	protected void sendRoundStatusBean(){
		if(game != null && control != null){
			ArrayList<RoundStatusInfo> info = new ArrayList<RoundStatusInfo>();
			
			// Collect status info for each player
			for(XHuntPlayer player : game.getPlayers().values())
				info.add(player.getRoundStatusInfo());
			
			// Send a result RoundStatusBean to _ALL_ players
			for ( String toJid : game.getPlayers().keySet() ) {
				control.getConnection().getProxy().RoundStatus( toJid, game.getRound(), info, new EmptyCallback());
			}
			
			
			/*sendXMPPBean(
					new RoundStatusBean(game.getRound(), info),
					// JIDs of all players
					game.getPlayers().keySet(),
					XMPPBean.TYPE_SET);*/
		}
	}
	
	/**
	 * Send RoundStatusBean to inform Mr.X about his own state of the current round.
	 */
	@SuppressWarnings("unchecked")
	protected void sendRoundStatusBeanForMrX(IXMPPCallback< RoundStatusResponse > callback){
		System.out.println("game=" + game + " control=" + control);
		if(game != null && control != null){
			ArrayList<RoundStatusInfo> info = new ArrayList<RoundStatusInfo>();
			
			// Collect status info for Mr.X
			info.add(game.getMisterX().getRoundStatusInfo());
			
			control.getConnection().getProxy().RoundStatus( 
					game.getMisterX().getJid(), 
					game.getRound(), 
					info, 
					callback != null
						? callback
						: new EmptyCallback());
			
			/*sendXMPPBean(
					new RoundStatusBean(game.getRound(), info),
					game.getMisterX().getJid(), 
					XMPPBean.TYPE_SET);*/
		}
	}
	
	/**
	 * Send RoundStatusBean to inform all players about the state of the agents 
	 * of the current round.
	 */
	@SuppressWarnings("unchecked")
	protected void sendRoundStatusBeanForAgents(IXMPPCallback< RoundStatusResponse > callback){
		if(game != null && control != null){	
			ArrayList<RoundStatusInfo> info = new ArrayList<RoundStatusInfo>();
			
			// Collect status info for all agents
			for(XHuntPlayer player : game.getAgents())
				info.add(player.getRoundStatusInfo());
			
			for ( String toJid : game.getPlayers().keySet() ) {
				control.getConnection().getProxy().RoundStatus( 
						toJid, 
						game.getRound(), 
						info, 
						callback != null
						? callback
						: new EmptyCallback());
			}
			
			
			/*sendXMPPBean(
					new RoundStatusBean(game.getRound(), info),
					// JIDs of all players
					game.getPlayers().keySet(), 
					XMPPBean.TYPE_SET);*/
		}
	}
}
