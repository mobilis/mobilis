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
package de.tudresden.inf.rn.mobilis.services.xhunt.state;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.tudresden.inf.rn.mobilis.services.xhunt.Game;
import de.tudresden.inf.rn.mobilis.services.xhunt.XHunt;
import de.tudresden.inf.rn.mobilis.services.xhunt.model.XHuntPlayer;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.GameDetailsRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.GameOverResponse;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * The Class GameStateGameOver is the last GameState in this service. 
 * After all players confirms the game over the server will shutdown.
 */
public class GameStateGameOver extends GameState{
	
	/** The class specific Logger object. */
	private final static Logger LOGGER = Logger.getLogger(GameStateGameOver.class.getCanonicalName());

	/**
	 * Instantiates a new GameStateGameOver.
	 *
	 * @param control the service controller
	 * @param game the current game instance
	 */
	public GameStateGameOver(XHunt control, Game game){
		this.control = control;
		this.game = game;
		
		// declare game as closed
		game.setGameIsOpen(false);
		
		// check if service is ready to shutdown
		checkShutdownCondition();
	}
	
	/**
	 * Checks the shutdown conditions of the service. 
	 * If each player has confirmed the GameOverBean and left 
	 * the game, the service will shutdown and is no longer 
	 * available.
	 */
	private void checkShutdownCondition(){
		Set<XHuntPlayer> avlblPlayers = new HashSet<XHuntPlayer>();
		for(Map.Entry<String, XHuntPlayer> entry : game.getPlayers().entrySet())
			if(entry.getValue().isOnline())
				avlblPlayers.add(entry.getValue());
		
		LOGGER.info("GameStateGameOver#checkShutdownCondition: "
				+ "shutdown in gameOverState?: " + (avlblPlayers.size() == 0));
		
		if(avlblPlayers.size() == 0){
			try {
				control.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.tudresden.inf.rn.mobilis.server.services.xhunt.state.GameState#processPacket(de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean)
	 */
	@Override
	public void processPacket(XMPPBean inBean) {

		if( inBean instanceof GameDetailsRequest){
			handleGameDetailsBean((GameDetailsRequest)inBean);
		}
		else if( inBean instanceof GameOverResponse){
			// remove player if GameOverBean was confirmed
			game.getPlayers().remove(inBean.getFrom());
		}
		else{
			inBean.errorType = "modify";
			inBean.errorCondition = "not-acceptable";
			inBean.errorText = "Just GameOver is accepted in this state.";
			
			control.getConnection().sendXMPPBeanError(
					inBean,
					inBean
			);			
		}
		
		// check if service is ready to shutdown
		checkShutdownCondition();
	}
}
