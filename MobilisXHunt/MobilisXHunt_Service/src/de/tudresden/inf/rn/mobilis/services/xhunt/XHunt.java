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
package de.tudresden.inf.rn.mobilis.services.xhunt;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;

import de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent;
import de.tudresden.inf.rn.mobilis.server.services.MobilisService;
import de.tudresden.inf.rn.mobilis.services.xhunt.helper.EmptyCallback;
import de.tudresden.inf.rn.mobilis.services.xhunt.helper.LogClass;
import de.tudresden.inf.rn.mobilis.services.xhunt.helper.SqlHelper;
import de.tudresden.inf.rn.mobilis.services.xhunt.services.IQListener;
import de.tudresden.inf.rn.mobilis.services.xhunt.services.MessageService;


/**
 * The Class XHunt is the main class for this service.
 */
public class XHunt extends MobilisService {
	
	/** The connection wrapper for this service. */
	private Connection mConnection;
	
	/** The actual game instance. */
	private Game mGame;
	
	/** The Settings which contains game specific confoguration. */
	private Settings mSettings;
	
	/** The SqlHelper to use the database. */
	private SqlHelper mSqlHelper;
	
	/** The list of participants, who do not play, but spectate at the game. */
	private Set<String> spectators = new HashSet<String>();
	
	/** The Class responsible for creating and writing a log file for this XHuntService instance.
	 *  Instantiates a top level Logger, all class specific Loggers inherit configuration and FileHandler. */
	private LogClass mLogClass;
	
	/** The class specific Logger object. */
	private final static Logger LOGGER = Logger.getLogger(XHunt.class.getCanonicalName());
	
	
	/**
	 * Creates a new XHunt instance.
	 */
	public XHunt() {
		// Instantiate new parent Logger which defines rules for all inheriting Loggers
		mLogClass = new LogClass();
	}
	
	
	/* (non-Javadoc)
	 * @see de.tudresden.inf.rn.mobilis.server.services.MobilisService#startup(de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent)
	 */
	public void startup(MobilisAgent agent) throws Exception {
		super.startup(agent);
		
		mConnection = new Connection(this);
		LOGGER.info("XHunt#startUp: Start new XHunt service (connection = " + mConnection.toString() + ")");
		
		mSettings = new Settings(getAgent());
		
		mSqlHelper = new SqlHelper();
		
		//TODO: read data from hibernate.xml
		mSqlHelper.setSqlConnectionData(Settings.DB_SERVER_ADDRESS,
				Settings.DB_SERVER_PORT,
				Settings.DB_NAME,
				Settings.DB_USERNAME,
				Settings.DB_PASSWORD);
		
		startGame();
	}
	

	/**
	 * Starts/Restarts the game.
	 */
	public void startGame(){
		LOGGER.info("XHunt#startGame: Start a new XHunt Game");
		if(mConnection.isConnected()){
			try {
				mGame = new Game(this);
			} catch (XMPPException e) {
				if(e.getXMPPError() != null){
					int errorcode = e.getXMPPError().getCode();
					String errormessage = e.getXMPPError().getMessage();
					LOGGER.severe(errorcode + " - " + errormessage + " - " + e.getMessage());
				}else{
					LOGGER.severe("XHunt#start: Unknown Error while connecting to the XMPP-Server");
				}
			} catch (Exception e) {
				LOGGER.severe("XHunt#start: " + e.getMessage());
			}			
		}else{
			try {
				LOGGER.warning("not connected; XHunt instance shutting down");
				this.shutdown();
			} catch (Exception e) {
				LOGGER.warning(e.getMessage().toString());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.tudresden.inf.rn.mobilis.server.services.AppSpecificService#shutdown()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void shutdown() throws Exception {
		// If a game is open, notify all players while using a GameOverBean
		// that the service is shutting down.
		if (mGame != null){
			try {
				for ( String toJid : mGame.getPlayers().keySet() ) {
					mConnection.getProxy().GameOver( 
							toJid, 
							"Server was shut down", 
							new EmptyCallback());
				}
				/*GameOverRequest bean = 
					new GameOverRequest("Server was shut down");
				mConnection.sendXMPPBean(bean,
						mGame.getPlayers().keySet(),
						XMPPBean.TYPE_SET);*/
				
				mSqlHelper.disconnect();
				mGame.closeMultiUserChat();
			} catch (XMPPException e) {
				if(e.getXMPPError() != null){
					int errorcode = e.getXMPPError().getCode();
					String errormessage = e.getXMPPError().getMessage();
					LOGGER.severe(errorcode + " - " + errormessage + " - " + e.getMessage());
				} else {
					LOGGER.severe("XHunt#shutdown: Unknown Error while shut down XHunt Service: " 
							+ getAgent().getFullJid());
				}
			}
		}
		

		LOGGER.info(getAgent().getFullJid() + " is shutting down.");
		mLogClass.closeLogFile();
		super.shutdown();
	}

	/**
	 * Gets the connection for this service. This is a wrapper 
	 * for the raw XMPP connection.
	 *
	 * @return the connection
	 */
	public Connection getConnection() {
		return mConnection;
	}

	/**
	 * Sets the actual game instance.
	 *
	 * @param actGame the new actual game instance
	 */
	public void setActGame(Game actGame) {
		this.mGame = actGame;
	}

	/**
	 * Gets the actual game instance.
	 *
	 * @return the actual game instance
	 */
	public Game getActGame() {
		return mGame;
	}

	/**
	 * Gets the Settings.
	 *
	 * @return the Settings
	 */
	public Settings getSettings() {
		return mSettings;
	}
	
	/**
	 * Gets the SqlHelper.
	 *
	 * @return the SqlHelper for this Service
	 */
	public SqlHelper getSqlHelper(){
		return this.mSqlHelper;
	}


	/**
	 * Sets the Settings.
	 *
	 * @param mSettings the new Settings configuration class
	 */
	public void setSettings(Settings mSettings) {
		this.mSettings = mSettings;
	}
	
	/**
	 * Get all participants, who do not play, but spectate at the game.
	 * @return the set of spectator JIDs
	 */	
	public Set<String> getSpectators() {
		return spectators;
	}
	
	/**
	 * Add a new entry to the list of participants, who do not play,
	 * but spectate at the game.
	 * @param spectatorJID the full XMPP-ID (JID; with resource) of the spectator
	 * @return true if the set of spectators did not already contain the specified element
	 */			
	public boolean addSpectator(String spectatorJID) {
		return spectators.add(spectatorJID);
	}
	
	/**
	 * Remove an entry from the list of participants, who do not play,
	 * but spectate at the game.
	 * @param spectatorJID the full XMPP-ID (JID; with resource) of the spectator
	 * @return true if this set of spectators contained the specified element
	 */
	public boolean removeSpectator(String spectatorJID) {
		return spectators.remove(spectatorJID);
	}
	
	/* (non-Javadoc)
	 * @see de.tudresden.inf.rn.mobilis.server.services.AppSpecificService#registerPacketListener()
	 */
	@Override
	protected void registerPacketListener() {
		MessageService mesServ = new MessageService();
		PacketTypeFilter mesFil = new PacketTypeFilter(Message.class);		
		getAgent().getConnection().addPacketListener(mesServ, mesFil);		
		
		IQListener iqServ = new IQListener(this);
		PacketTypeFilter locFil = new PacketTypeFilter(IQ.class);		
		getAgent().getConnection().addPacketListener(iqServ, locFil);	
		
		LOGGER.info("XHunt#registerPacketListener successfully registered "
				+ "IQListener and MessageListener");
	}
}
