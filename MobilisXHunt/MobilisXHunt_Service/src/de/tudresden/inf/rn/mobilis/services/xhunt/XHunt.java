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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;

import de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent;
import de.tudresden.inf.rn.mobilis.server.services.MobilisService;
import de.tudresden.inf.rn.mobilis.services.xhunt.helper.SqlHelper;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.GameOverResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.IXMPPCallback;
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
	
	/** The Settings which contains game specifig confoguration. */
	private Settings mSettings;
	
	/** The date formatter to format the timestamps of the log. */
	private DateFormat mDateFormatter;
	
	/** The SqlHelper to use the database. */
	private SqlHelper mSqlHelper;
	
	/** The list of participants, who do not play, but spectate at the game. */
	private Set<String> spectators = new HashSet<String>();
	
	
	public XHunt() {
		mDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		log("XHunt()");
	}
	
	
	/**
	 * Log a string to console. The string to log will be expanded by a timestamp.
	 *
	 * @param str the string to log
	 */
	public void log(String str){		 
		System.out.println("[" + mDateFormatter.format(System.currentTimeMillis()) + "] " + str);
	}
	
	/**
	 * Log a string to file.
	 *
	 * @param str the string to log
	 */
	private void logToFile(String str){
		FileWriter fw;
		File file = new File("xhunt.log");
		
		try {
			fw = new FileWriter(file, true);
			
			BufferedWriter bw = new BufferedWriter(fw); 
			
			bw.write(str); 
			bw.newLine();
			
			bw.close(); 
		} catch (IOException e) {
			System.err.println("ERROR while writing to logfile: " + file.getAbsolutePath());
			e.printStackTrace();
		} 
	}
	
	/* (non-Javadoc)
	 * @see de.tudresden.inf.rn.mobilis.server.services.MobilisService#startup(de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent)
	 */
	public void startup(MobilisAgent agent) throws Exception {
		super.startup(agent);
		
		mConnection = new Connection(this);
		log("XHunt#startUp: Start new XHunt service (connection = " 
				+ mConnection.toString() + ")");
		
		mSettings = new Settings(getAgent());
		
		mSqlHelper = new SqlHelper(this);
		
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
		log("XHunt#startGame: Start a new XHunt Game");
		if(mConnection.isConnected()){
			try {
				mGame = new Game(this);
			} catch (XMPPException e) {
				if(e.getXMPPError() != null){
					int errorcode = e.getXMPPError().getCode();
					String errormessage = e.getXMPPError().getMessage();
					log(errorcode + " - " + errormessage);
				}else{
					log("XHunt#start: Unknown Error while connecting to the XMPP-Server");
				}
			} catch (Exception e) {
				log("XHunt#start: " + e.getMessage());
			}			
		}else{
			try {
				this.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.tudresden.inf.rn.mobilis.server.services.AppSpecificService#shutdown()
	 */
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
							new IXMPPCallback< GameOverResponse >() {
								
								@Override
								public void invoke( GameOverResponse xmppBean ) {
									// Do nothing
								}
							} );
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
					log(errorcode + " - " + errormessage);
				} else {
					log("XHunt#shutdown: Unknown Error while shut down XHunt Service: " 
							+ getAgent().getFullJid());
				}
			}
		}
		
		log(getAgent().getFullJid() + " is shutting down.");
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
		MessageService mesServ = new MessageService(this);
		PacketTypeFilter mesFil = new PacketTypeFilter(Message.class);		
		getAgent().getConnection().addPacketListener(mesServ, mesFil);		
		
		IQListener iqServ = new IQListener(this);
		PacketTypeFilter locFil = new PacketTypeFilter(IQ.class);		
		getAgent().getConnection().addPacketListener(iqServ, locFil);	
		
		log("XHunt#registerPacketListener successfully registered "
				+ "IQListener and MessageListener");
	}
}
