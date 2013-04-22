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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.MultiUserChat;

import de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent;
import de.tudresden.inf.rn.mobilis.services.xhunt.helper.EmptyCallback;
import de.tudresden.inf.rn.mobilis.services.xhunt.model.XHuntPlayer;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.AreasRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.AreasResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.CancelTimerRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.CancelTimerResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.CreateGameRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.CreateGameResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.DepartureDataRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.DepartureDataResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.GameDetailsRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.GameDetailsResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.GameOverRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.GameOverResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.IMobilisXHuntOutgoing;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.JoinGameRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.JoinGameResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.LocationRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.LocationResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.MobilisXHuntProxy;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.PlayerExitRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.PlayerExitResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.PlayersRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.PlayersResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.RoundStatusRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.RoundStatusResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.SnapshotRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.SnapshotResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.StartRoundRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.StartRoundResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.TargetRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.TargetResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.TicketAmount;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.TransferTicketRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.TransferTicketResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.UpdatePlayerRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.UpdatePlayerResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.UpdateTicketsRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.UpdateTicketsResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.UsedTicketsRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.UsedTicketsResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.state.GameStateGameOver;
import de.tudresden.inf.rn.mobilis.services.xhunt.state.GameStatePlay;
import de.tudresden.inf.rn.mobilis.xmpp.beans.IXMPPCallback;
import de.tudresden.inf.rn.mobilis.xmpp.beans.ProxyBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanIQAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanProviderAdapter;

/**
 * The Class Connection.
 */
public class Connection {
	
	/** The SMACK FileTransferManager. */
	private FileTransferManager mFileTransferManager;
	
	/** The current MobilisAgent of the Service. */
	private MobilisAgent mMobilisAgent;
	
	/** The XHunt service. */
	private XHunt mController;
	
	/** The Timer for checking for delayed XMPPBeans of type result. */
	private Timer mDelayedResultBeansTimer;
	
	/** The Timer for periodically sending Snapshots to unavailable Players. */
	private Timer mSendSnapshotsTimer;
	
	/** The limit for delayed result-XMPPBeans. This number determines, how often 
	 * a result-XMPPBean can miss the mResultBeansTimeoutMillis before the 
	 * XMPP-user is declared as not replying. */
	private int mLimitForDelayedPeriods = 2;
	
	/** The interval for sending Snapshots to unavailable Players. */
	private int mSendSnapshotsInterval = 5 * 1000;
	
	/** The timeout in seconds for a FileTransfer. */
	private int mFiletransferTimeout = 15;
	
	/** True if a FileTransfer is active. */
	private boolean mIsFiletransferActive = false;
	
	/** The class specific Logger object. */
	private final static Logger LOGGER = Logger.getLogger(Connection.class.getCanonicalName());
	
	/** The prototypes of registered XMPPBeans used for this service. */
	// namespace, childelement, xmppbean
	private Map<String,Map<String,XMPPBean>> beanPrototypes
		= Collections.synchronizedMap(new HashMap<String,Map<String,XMPPBean>>());
	
	/** The list for result-XMPPBeans for which this service is waiting for. 
	 * This contains of the id of the XMPPBean as key and a BeanTimePair for 
	 * further information about delaying. */
	private ConcurrentHashMap<String, BeanTimePair> mWaitingForResultBeans 
		= new ConcurrentHashMap<String, Connection.BeanTimePair>();
	
	/** Players which are waiting for the beginning of the next round to return. */
	private Set<XHuntPlayer> playersWaitingForReturn;
	
	private MobilisXHuntProxy _proxy;
	
	private Map< String, IXMPPCallback< ? extends XMPPBean >> _waitingCallbacks 
		= new HashMap< String, IXMPPCallback< ? extends XMPPBean > >();
	
	
	/**
	 * Instantiates a new Connection.
	 *
	 * @param controller the XHunt service
	 */
	public Connection(XHunt controller) {
		this.mMobilisAgent = controller.getAgent();
		this.mController = controller;
		
		_proxy = new MobilisXHuntProxy( _proxyOutgoingMapper );
		
		// instantiate and enable FileTransfer
		mFileTransferManager = new FileTransferManager(mMobilisAgent.getConnection());
		FileTransferNegotiator.setServiceEnabled(mMobilisAgent.getConnection(), true);
		
		// register all XMPPBeans labeled as XMPP-Extensions
		registerXMPPExtensions();
	}
	
	/**
	 * Converts an XMPPBean to a string.
	 *
	 * @param bean the XMPPBean
	 * @return the XMPPBean as string
	 */
	public String beanToString(XMPPBean bean){
		String str = "XMPPBean: [NS="
			+ bean.getNamespace()
			+ " id=" + bean.getId()
			+ " from=" + bean.getFrom()
			+ " to=" + bean.getTo()
			+ " type=" + bean.getType()
			+ " payload=" + bean.payloadToXML();
		
		if(bean.errorCondition != null)
			str += " errorCondition=" + bean.errorCondition;
		if(bean.errorText != null)
			str += " errorText=" + bean.errorText;
		if(bean.errorType != null)
			str += " errorType=" + bean.errorType;
		
		str += "]";
		
		return str;
	}
	
	/**
	 * Sends Snapshots to all Players which are marked as offline.
	 * The Interval is defined by {@link mSendSnapshotsInterval}.
	 */
	private void startSendingSnapshots() {
		// restart to avoid errors
		if(mSendSnapshotsTimer != null)
			mSendSnapshotsTimer.cancel();

		mSendSnapshotsTimer = new Timer();
		mSendSnapshotsTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Set<XHuntPlayer> offlinePlayers = new HashSet<XHuntPlayer>();
				for(Map.Entry<String, XHuntPlayer> entry : mController.getActGame().getPlayers().entrySet()) {
					if(!entry.getValue().isOnline())
						offlinePlayers.add(entry.getValue());
				}
				
				//if game was closed, cancel timer
				if(mController.getActGame().getGameState() instanceof GameStateGameOver)
					mSendSnapshotsTimer.cancel();
				
				// if all players are back online, write log message and cancel timer
				if(offlinePlayers.size() == 0) {
					LOGGER.info("no more offline players, stopping snapshot timer");
					mSendSnapshotsTimer.cancel();
				}
				
				else {
					for(XHuntPlayer player : offlinePlayers) {
						LOGGER.info("sending Snapshot to " + player.getJid());
						sendSnapshot(player.getJid());
					}
				}
			}
		}, 0, mSendSnapshotsInterval);
	}
	
	/**
	 * Handles the return of a previously unavailable Player.
	 * 
	 * Connection can break down in three states:
	 * 1) Player has neither reached nor chosen target
	 *    -> Mr.X can rejoin immediately, he just has to send a TargetIQ
	 *    -> Agents should receive a StartRoundIQ, therefore should only rejoin at the beginning of GameStateRoundAgents
	 * 2) Player chose target, but hasn't reached it yet
	 *    -> Mr.X can rejoin immediately, Game waits with changing to GameStateRoundMrX until he reached his target
	 *    -> Agents can only rejoin in GameStateRoundAgents, which means that other Players are still on the move
	 * 3) Player has chosen and reached target
	 *    -> Mr.X can rejoin immediately, Game only changes to GameStateRoundMrX when he's online
	 *    -> Agents can rejoin at the beginning of GameStateRoundAgents (it's cleaner this way)
	 *    
	 * Note: - 'Agent.currentTargetId=-1' and 'setReachedTarget(false)' are called at the beginning of GameStateRoundAgents
	 *       - for Mr.X it's the same in GameStateRoundMrX
	 *       - Resetting is being followed by a StartRoundIQ
	 * 
	 * @param jid The JID of the returning Player
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void handleReturningPlayer(String jid) {
		
		// Players can only return in GameStatePlay
		if(mController.getActGame().getGameState() instanceof GameStatePlay) {
			// If Player was removed from Game, ignore him
			XHuntPlayer returnee = mController.getActGame().getPlayerByJid(jid);
			if(returnee == null) {
				LOGGER.warning("Player " + jid + " was not found in the Game's Player List!");
				return;
			}

			int subState = ((GameStatePlay) mController.getActGame().getGameState()).getSubGameStateID();
			
			// Mr.X can rejoin anytime
			if(returnee.isMrx()) {
				LOGGER.info("Setting Mr.X back to online");
				returnee.setOnline(true);
				
				// if it's GameStateRoundMrX, he could have missed the StartRoundIQ, just send another one
				if(subState == GameStatePlay.SUBSTATE_MRX) {
					List<TicketAmount> ticketsMrX = new ArrayList< TicketAmount >();
					for ( Map.Entry< Integer, Integer > entry : mController.getSettings().getTicketsMrX().entrySet() ) {
						ticketsMrX.add( new TicketAmount(entry.getKey(), entry.getValue()) );
					}
					
					mController.getConnection().getProxy().StartRound( 
							mController.getActGame().getMisterX().getJid(), 
							mController.getActGame().getRound(), 
							true, 
							ticketsMrX, 
							new EmptyCallback());
				}
			}

			// Agents have to rejoin depending on their state
			else if(!returnee.isMrx()) {
				if(playersWaitingForReturn == null)
					playersWaitingForReturn = new CopyOnWriteArraySet<XHuntPlayer>();
				
				if(!playersWaitingForReturn.contains(returnee)) {
					
					// if Agent didn't choose a target, he has to wait until the beginning of the next round 
					if(returnee.getCurrentTargetId() == -1) {
						LOGGER.info("Agent " + jid + " didn't choose a target, adding him to playersWaitingForReturn");
						playersWaitingForReturn.add(returnee);
					}
					
					// if Agent chose a target, but hasn't reached it yet, he can return as long as it's GameStateRoundAgents
					else if((returnee.getCurrentTargetId() != -1) && (!returnee.getReachedTarget())) {
						if(subState == GameStatePlay.SUBSTATE_AGENTS) {
							LOGGER.info("Setting Agent " + jid + " back to online, he has to move to his previously chosen target");
							returnee.setOnline(true);
						} else {
							LOGGER.info("Agent " + jid + " can't rejoin in GameStateRoundMrX, adding him to playersWaitingForReturn");
							playersWaitingForReturn.add(returnee);
						}
					}
					
					// if Agent has reached his target, he can rejoin at the beginning of the next round
					else if(returnee.getReachedTarget()) {
						LOGGER.info("Agent " + jid + " already reached his target, adding him to playersWaitingForReturn");
						playersWaitingForReturn.add(returnee);
					}
					
					else
						LOGGER.warning("Unhandled State of returning Player");
				}
			}
			
		} else {
			LOGGER.info("Players can only return in GameStatePlay!");
		}
	}
	
	/**
	 * sets all players in {@link playersWaitingForReturn} to online and removes them from the List
	 */
	public void setReturneesToOnline() {
		if(playersWaitingForReturn != null) {
			for(XHuntPlayer player : playersWaitingForReturn) {
				LOGGER.info("Setting Agent " + player.getJid() + " back to online");
				player.setOnline(true);
				playersWaitingForReturn.remove(player);
			}
		}
	}
	
	/**
	 * This method is observing the result-XMPPBeans of each IQ to the game players. 
	 * If a result bean doesn't arrive in a defined amount of time, the player is marked as offline
	 * and snapshots beans are sent to him periodically until he responds again.
	 */
	public void checkForDelayedResultBeans() {
		
		long currentTime = System.currentTimeMillis();
		
		ArrayList<String> removableWaitingBeanIds = new ArrayList<String>(); 
		for(Map.Entry<String, BeanTimePair> entry : mWaitingForResultBeans.entrySet()){

			// if the player left the game or the bean was marked for deletion, don't wait for a response to this bean any more
			if(entry.getValue().DeleteFromWaitings || mController.getActGame().getPlayerByJid(entry.getValue().Bean.getTo()) == null) {
				removableWaitingBeanIds.add(entry.getKey());
				
				// skip rest of condition checking
				continue;
			}
			
			// check if a result-XMPPBean has exceeded the timeout for one waiting period
			if(currentTime > (entry.getValue().TimeStamp + mController.getSettings().getLocationPollingIntervalMillis())){		
				
				// check if a result-XMPPBean has reached the maximum number of delay periods
				if(entry.getValue().DelayedPeriods >= mLimitForDelayedPeriods) {
					
					// check whether player really is offline or if he already sent newer beans
					if(!entry.getValue().playerGaveSignOfLive) {

						// kick player if still in GameStateLobby, GameStateRoundInitial etc
						if(!(mController.getActGame().getGameState() instanceof GameStatePlay))
							kickNotRespondingPlayer(entry.getValue().Bean.getTo());
						
						// else mark him as offline
						else
							disableNotRespondingPlayer(entry.getValue().Bean.getTo());
					}
					
					// if he already sent newer beans, just don't wait for a response to this one any more
					else {
						LOGGER.info("marking old waiting bean for deletion, player still seems to be alive");
						entry.getValue().DeleteFromWaitings = true;
					}
				}
				
				// if maximum number of delay periods is not reached yet, increment delayed periods value
				else {
					if(!mIsFiletransferActive)
						entry.getValue().DelayedPeriods++;
				}
			}
		}
		
		// remove all result-XMPPBeans from mWatingForResultBeans which were marked before
		StringBuilder strBuilder = new StringBuilder();
		for(String waitingBeanId : removableWaitingBeanIds) {
			strBuilder.append(System.getProperty("line.separator") + "- " + mWaitingForResultBeans.get(waitingBeanId).Bean.toXML());
			mWaitingForResultBeans.remove(waitingBeanId);
		}
		if(strBuilder.toString().length() > 0)
			LOGGER.info("removed following Beans from mWaitingForResultBeans: " + strBuilder.toString());
		
		printWaitingBeanMap();
	}
	
	/**
	 * Handle if a player doesn't reply.
	 *
	 * @param playerJid the jid of the player
	 */
	public void disableNotRespondingPlayer(String playerJid){
		LOGGER.warning("Player " + playerJid + "  doesn't reply. Marking him as offline");
		
		XHuntPlayer player = mController.getActGame().getPlayerByJid(playerJid);
		
		if(player != null)
			player.setOnline(false);
		
		if((playersWaitingForReturn != null) && (playersWaitingForReturn.contains(player)))
			playersWaitingForReturn.remove(player);
		
		// Mark each result-XMPPBean which we are waiting for for deletion
		for(BeanTimePair pair : mWaitingForResultBeans.values()) {
			if(pair.Bean.getTo().equals(playerJid))
				pair.DeleteFromWaitings = true;
		}
		
		// try to send a current SnapshotBean to the player
		startSendingSnapshots();		
	}
	
	/**
	 * Kick player, close game if it was Mr.X
	 * 
	 * @param playerJid the jid of the player
	 */
	@SuppressWarnings("unchecked")
	private void kickNotRespondingPlayer(String playerJid){
		LOGGER.warning("Kicking not responding Player " + playerJid);
		
		// try to send at least an PlayerExitBean to the unavailable player, but do not wait for result
		GameOverRequest bean = new GameOverRequest( "You were kicked because of missing response messages." );
		bean.setTo( playerJid );
		sendBean(bean);
		
		// Mark each result-XMPPBean which we are waiting for for deletion
		for(BeanTimePair pair : mWaitingForResultBeans.values()){
			if(pair.Bean.getTo().equals(playerJid))
				pair.DeleteFromWaitings = true;
		}
		
		XHuntPlayer unavailablePlayer = mController.getActGame().getPlayerByJid(playerJid);
		
		// if the unavailable player is Mr.X
		if(unavailablePlayer.isMrx()){
			// mark game as closed
			mController.getActGame().setGameIsOpen(false);
			// remove unavailable player from game
			mController.getActGame().removePlayerByJid(playerJid);
			
			// Switch to GameStateGameOver
			mController.getActGame().setGameState(new GameStateGameOver(mController, mController.getActGame()));
			LOGGER.info("Status changed to GameStateGameOver");
			
			// send GameOverBean to all agents
			sendXMPPBean(
					new GameOverRequest("MrX is no more available!"),
					mController.getActGame().getAgentsJids(),
					XMPPBean.TYPE_SET
			);
		}
		// if unavailable player is not Mr.X
		else{
			// remove player from game
			mController.getActGame().removePlayerByJid(playerJid);
			
			// notify all other players about removing of the unavailable player
			mController.getActGame().getGameState().sendPlayersBean("Player " + unavailablePlayer.getName()
					+ " is no more available", new EmptyCallback());
		}		
	}
	
	public static XMPPBean createXMPPBeanResult(XMPPBean resultBean, XMPPBean fromBean){
		resultBean.setTo(fromBean.getFrom());
		resultBean.setType(XMPPBean.TYPE_RESULT);
		resultBean.setId(fromBean.getId());
		
		return resultBean;
	}
	
	/**
	 * Creates a MultiUserChat.
	 *
	 * @param roomName the name of the room
	 * @return the created MultiUserChat
	 */
	public MultiUserChat createMultiUserChat(String roomName){
		return new MultiUserChat(mMobilisAgent.getConnection(), roomName);
	}
	
	/**
	 * Gets the FileTransferManager.
	 *
	 * @return the FileTransferManager
	 */
	public FileTransferManager getFileTransferManager(){
		return this.mFileTransferManager;
	}
	
	public MobilisXHuntProxy getProxy(){
		return _proxy;
	}
	
	/**
	 * Gets a registered XMPPBean by its namespace. This XMPPBean is listed in the beynPrototypes.
	 *
	 * @param namespace the namespace of the XMPPBean
	 * @return the registered XMPPBean
	 */
	public XMPPBean getRegisteredBeanByNamespace(String namespace){
		try{
			return this.beanPrototypes.get(namespace).values().iterator().hasNext()
				? this.beanPrototypes.get(namespace).values().iterator().next() : null;
		}
		catch(NullPointerException e){
			LOGGER.severe("ERROR: Cannot find namespace '" + namespace + "' in list of bean prototypes!");
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean handleCallback(XMPPBean inBean){
		@SuppressWarnings("rawtypes")
		IXMPPCallback callback = _waitingCallbacks.get( inBean.getId() );

		if ( null != callback )
			callback.invoke( inBean );
		
		return null != callback;
	}
	
	/**
	 * Checks if XHUnt service is connected to XMPP server.
	 *
	 * @return true, if is connected
	 */
	public boolean isConnected(){
		return mMobilisAgent.getConnection() != null
			? mMobilisAgent.getConnection().isConnected()
			: false;
	}
	
	/**
	 * Prints detailed information about the waiting XMPPBeans, if there are any.
	 */
	private void printWaitingBeanMap() {
		if(mWaitingForResultBeans.size() > 0) {
			
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("Waiting for " + mWaitingForResultBeans.size() + " result beans:");
			
			int count = 1;
			for(Map.Entry<String, BeanTimePair> entry : mWaitingForResultBeans.entrySet()) {
				strBuilder.append(System.getProperty("line.separator")
						+ "- Waiting Bean #" + count++ + ": ["
						+ " id=" + entry.getKey()
						+ " timestamp=" + entry.getValue().TimeStamp
						+ " delayedPeriod=" + entry.getValue().DelayedPeriods
						+ " playerGaveOtherSignsOfLife=" + entry.getValue().playerGaveSignOfLive
						+ " delete=" + entry.getValue().DeleteFromWaitings + "] "
						+ System.getProperty("line.separator")
						+ beanToString(entry.getValue().Bean)
						);
			}
			LOGGER.info(strBuilder.toString());
		}
	}
	
	/**
	 * Register all XMBBBeabs labeled as XMPP extensions.
	 */
	private void registerXMPPExtensions(){		
		registerXMPPBean(new AreasRequest());
		registerXMPPBean(new AreasResponse());
		
		registerXMPPBean(new CancelTimerRequest());
		registerXMPPBean(new CancelTimerResponse());
		
		registerXMPPBean(new CreateGameRequest());
		registerXMPPBean(new CreateGameResponse());
		
		registerXMPPBean(new DepartureDataRequest());
		registerXMPPBean(new DepartureDataResponse());
		
		registerXMPPBean(new GameDetailsRequest());
		registerXMPPBean(new GameDetailsResponse());
		
		registerXMPPBean(new GameOverRequest());
		registerXMPPBean(new GameOverResponse());
		
		registerXMPPBean(new JoinGameRequest());
		registerXMPPBean(new JoinGameResponse());
		
		registerXMPPBean(new AreasRequest());
		registerXMPPBean(new AreasResponse());
		
		registerXMPPBean(new LocationRequest());
		registerXMPPBean(new LocationResponse());
		
//		registerXMPPBean(new OpenGamesBean());
		
		registerXMPPBean(new PlayerExitRequest());
		registerXMPPBean(new PlayerExitResponse());
		
		registerXMPPBean(new PlayersRequest());
		registerXMPPBean(new PlayersResponse());
		
		registerXMPPBean(new RoundStatusRequest());
		registerXMPPBean(new RoundStatusResponse());
		
		registerXMPPBean(new SnapshotRequest());
		registerXMPPBean(new SnapshotResponse());
		
		registerXMPPBean(new StartRoundRequest());
		registerXMPPBean(new StartRoundResponse());
		
		registerXMPPBean(new TargetRequest());
		registerXMPPBean(new TargetResponse());
		
		registerXMPPBean(new TransferTicketRequest());
		registerXMPPBean(new TransferTicketResponse());
		
		registerXMPPBean(new UpdatePlayerRequest());
		registerXMPPBean(new UpdatePlayerResponse());
		
		registerXMPPBean(new UpdateTicketsRequest());
		registerXMPPBean(new UpdateTicketsResponse());
		
		registerXMPPBean(new UsedTicketsRequest());
		registerXMPPBean(new UsedTicketsResponse());
	}
	
	/**
	 * Register an XMPPBean as prototype.
	 *
	 * @param prototype a basic instance of the XMPPBean
	 */
	private void registerXMPPBean(XMPPBean prototype) {
		
		// add XMPPBean to service provider to enable it in XMPP
		(new BeanProviderAdapter(new ProxyBean( prototype.getNamespace(), prototype.getChildElement() ))).addToProviderManager();
		
		// add the prototype of the XMPPBean to the managed list of prototypes
		synchronized (this.beanPrototypes) {
			if (!this.beanPrototypes.keySet().contains(prototype.getNamespace()))
				this.beanPrototypes.put(prototype.getNamespace(), 
						Collections.synchronizedMap( new HashMap<String,XMPPBean>() ));
			
			this.beanPrototypes.get(prototype.getNamespace())
				.put(prototype.getChildElement(), prototype);
		}
	}
	
	/**
	 * Send a single XMPPBean using the routing information determined in the XMPPBean itself. 
	 * This function doesn't store the XMPPBean to send in the list of waiting XMPPBeans 
	 * mWatingForResultBeans, so there will be no check for response. This function can 
	 * only be used by this Connection class is not qualified for normal usage of sending 
	 * XMPPBeans by other classes like the GameState classes.
	 *
	 * @param bean the XMPPBean to send
	 */
	private void sendBean(XMPPBean bean){
		// just send the XMPPBean if XMPP connection is established and no FileTransfer is active
		if(!mIsFiletransferActive
				&& mMobilisAgent != null
				&& mMobilisAgent.getConnection() != null
				&& mMobilisAgent.getConnection().isConnected()){
			// send XMPPBean
			mMobilisAgent.getConnection().sendPacket(new BeanIQAdapter(bean));
			
			// if the receiver of the XMPPBean is no spectator
			if(!mController.getSpectators().contains(bean.getTo())){
				// using counter to extend the id of the packet
				int counter = 0;
				
				// forward the XMPPBean to all spectators
				for(String jid : mController.getSpectators()){
					XMPPBean clone = bean.clone();
					clone.setTo(jid);
					clone.setId(bean.getId() + "_" + counter);
					
					LOGGER.info(clone.toXML());
					mMobilisAgent.getConnection().sendPacket(new BeanIQAdapter(clone));
				}
			}
		}
	}
	
	/*
	 * This method is used to send a delayed XMPPBean.
	 *
	 * @param beanTimePair the BeanTimePair containing the XMPPBEan and further information
	 */
	/*private void sendDelayedBean(BeanTimePair beanTimePair){
		beanTimePair.TimeStamp = System.currentTimeMillis();		
		
		mWaitingForResultBeans.put(beanTimePair.Bean.getId(), beanTimePair);
		sendBean(beanTimePair.Bean.clone());			
	}*/
	
	/**
	 * Send a SnapshotBean to a player.
	 *
	 * @param toJid the jid of the player
	 */
	private void sendSnapshot(String toJid){
		// create a new SnapshotBean with current game information
		SnapshotRequest snapshotBean = mController.getActGame().createSnapshotBean(toJid);
		snapshotBean.setTo(toJid);
		snapshotBean.setType(XMPPBean.TYPE_SET);
		
		LOGGER.info("Snapshot: " + beanToString(snapshotBean));
		
		sendXMPPBean(snapshotBean);
	}
	
	/**
	 * Send a single XMPPBean using the routing information determined in the XMPPBean itself. 
	 * This function stores the XMPPBean to send in the list of waiting XMPPBeans 
	 * mWatingForResultBeans, to check for response. This function should be used for all 
	 * classes to send a XMPPBean.
	 *
	 * @param bean the XMPPBean to send
	 * @return true, if sending was successful
	 */
	private boolean sendXMPPBean(XMPPBean bean){
		bean.setFrom(mMobilisAgent.getFullJid());
		
		// if the player is not available, do not send any XMPPBEan beside a SnapshotBean
		// Player can be null in GameStateUninitialized and beginning of GameStateLobby
		XHuntPlayer plr = mController.getActGame().getPlayerByJid(bean.getTo());
		if((plr == null) || (plr.isOnline() || bean.getNamespace().equals(SnapshotRequest.NAMESPACE))) {
				// just wait for XMPPBeans of type get or set (Snapshots are 'Set')
				// and don't wait for responses to UpdateTicketsRequests, the client doesn't send any
				if((bean.getType() == XMPPBean.TYPE_SET || bean.getType() == XMPPBean.TYPE_GET)
						&& !(bean instanceof UpdateTicketsRequest)) {
					// add a copy of the XMPPBean to the list of waiting XMPPBeans mWaitingForResultBeans
					XMPPBean clone = bean.clone();
					mWaitingForResultBeans.put(bean.getId(), new BeanTimePair(clone, System.currentTimeMillis()));
				}
				// send XMPPBean
				LOGGER.info("sendIQ: " + beanToString(bean));
				sendBean(bean);
				return true;
		}
		else {
			return false;
		}
	}
	
	/*
	 * Send a single XMPPBean using the to attribute and the type of the XMPPBEan provided. 
	 * This function stores the XMPPBean to send in the list of waiting XMPPBeans 
	 * mWatingForResultBeans, to check for response. This function should be used for all 
	 * classes to send a XMPPBean.
	 *
	 * @param bean the XMPPBean to send
	 * @param to the receivers jid
	 * @param type the type of the XMPPBean
	 * @return true, if sending was successful
	 */
	/*private boolean sendXMPPBean(XMPPBean bean, String to, int type){
		bean.setTo(to);
		bean.setType(type);
		
		return this.sendXMPPBean(bean);
	}*/
	
	/**
	 * Send a XMPPBean to a list of players using the routing information determined in the XMPPBean itself. 
	 * This function stores the XMPPBean to send in the list of waiting XMPPBeans 
	 * mWatingForResultBeans, to check for response. This function should be used for all 
	 * classes to send a XMPPBean.
	 *
	 * @param bean the XMPPBean to send
	 * @param players the list of receiving players of the XMPPBEan
	 * @param type the type of the XMPPBean
	 * @return true, if sending was successful
	 */
	private boolean sendXMPPBean(XMPPBean bean, Set<String> players, int type){
		
		bean.setType(type);
		// counter for extending the id of the packet
		int counter = 0;
		
		// for each player in the list of players, clone the original XMPPBean,
		// extend the id of the packet and do a normal send with storing the XMPPBean in 
		// the list of waiting XMPPBEans
		for(String playerJid : players){
			XMPPBean clone = bean.clone();
			clone.setTo(playerJid);
			clone.setId(bean.getId() + "_" + counter);
			
			this.sendXMPPBean(clone);
			counter++;
		}
		
		return true;
	}
	
	/**
	 * Send a XMPPBean of type error using the original XMPPBean for routing information.
	 *
	 * @param resultBean the error XMPPBean with the specific error information
	 * @param fromBean the original XMPPBean
	 * @return true, if sending successful
	 */
	public boolean sendXMPPBeanError(XMPPBean resultBean, XMPPBean fromBean){
		resultBean.setTo(fromBean.getFrom());
		resultBean.setType(XMPPBean.TYPE_ERROR);
		resultBean.setId(fromBean.getId());
		
		return this.sendXMPPBean(resultBean);
	}
	
	/*
	 * Send a XMPPBean of type result using the original XMPPBean for routing information.
	 *
	 * @param resultBean the result XMPPBean
	 * @param fromBean the original XMPPBean
	 * @return true, if sending successful
	 */
	/*private boolean sendXMPPBeanResult(XMPPBean resultBean, XMPPBean fromBean){
		resultBean.setTo(fromBean.getFrom());
		resultBean.setType(XMPPBean.TYPE_RESULT);
		resultBean.setId(fromBean.getId());
		
		return this.sendXMPPBean(resultBean);
	}*/
	
	/**
	 * Start the mDelayedResultBeansTimer which will call the checking function 
	 * {@link checkForDelayedResultBean} each mResultBeansTimeoutMillis.
	 */
	public void startDelayedResultBeansTimer(){
		if(mDelayedResultBeansTimer != null)
			mDelayedResultBeansTimer.cancel();
		
		mDelayedResultBeansTimer = new Timer();
		mDelayedResultBeansTimer.schedule(
			new TimerTask() {
				public void run() {
					if(mController.getActGame().getGameState() instanceof GameStateGameOver)
						mDelayedResultBeansTimer.cancel();
					else
						checkForDelayedResultBeans();
		        }
		},
		// use same Interval as the location poller
		mController.getSettings().getLocationPollingIntervalMillis(),
		mController.getSettings().getLocationPollingIntervalMillis());
	}
	
	/**
	 * Transmit a File using the SMACK FileTransfer. This function is used to send the 
	 * information of the area and the icons of the tickets to each player.
	 *
	 * @param file the file to send
	 * @param fileDesc the description of the file can be used to identify the file
	 * @param toJid the jid of the player
	 * @return true, if sending was successful
	 */
	public boolean transmitFile(File file, String fileDesc, String toJid){
		boolean transferSuccessful = false;
		mIsFiletransferActive = true;
		OutgoingFileTransfer transfer = mFileTransferManager.createOutgoingFileTransfer(toJid);
        
		// check if file exists
		if(file.exists()) {
			LOGGER.info("Start transmitting file: " + file.getAbsolutePath()
					+ " to: " + toJid);
	        try {
	        	// counter for sending tries
	        	int counter = 0;
	        	
	        	// start sending file
	        	transfer.sendFile(file, fileDesc);
	        	
	        	// while file is sending
	        	while(!transfer.isDone()) {
	        		// if counter of maximum tries has reached, cancel transmission
	        		if(counter == mFiletransferTimeout){
	        			LOGGER.warning("ERROR: Filetransfer canceled. No Response!");
	        			break;
	        		}
	        		// increase try counter of sending tries
	        		counter++;
	        		
	        		// wait for 1000 ms and try sending the file again
	        		try {
	        			Thread.sleep(1000);
	        		} catch (InterruptedException e1) {
	        			LOGGER.warning("ERROR: Thread interrupted while transmitting file: " + file.getName());
	        		}
	        	}
	        	
	        	transferSuccessful = transfer.isDone();
	        } catch (XMPPException e) {
	        	LOGGER.severe("FileTransfer throws XMPPException: " + e.getMessage().toString());
	        }
		}
		mIsFiletransferActive = false;
		LOGGER.info("FileTransfer successful?: " + transferSuccessful);
		
		return transferSuccessful;
	}
	
	public XMPPBean unpackBeanIQAdapter(BeanIQAdapter adapter){
		XMPPBean unpackedBean = null;
		
		if( beanPrototypes.containsKey( adapter.getNamespace() )
				&& beanPrototypes.get( adapter.getNamespace() )
					.containsKey( adapter.getChildElement() )){
			unpackedBean = adapter.unpackProxyBean( 
					beanPrototypes.get( adapter.getNamespace() )
						.get( adapter.getChildElement() ).clone() );
		}
		
		return unpackedBean;
	}
	
	/**
	 * Verify an incoming XMPPBean.
	 *
	 * @param inBean the incoming XMPPBean
	 * @return true, if verification was successful
	 */
	public boolean verifyIncomingBean(XMPPBean inBean){
		boolean isBeanAccepted = false;
		String resultMsg = "";
		
		// just handle the XMPPBeans of type result, each other XMPPBeans automatically accepted 
		// so that the current GameState can handle this XMPPBean
		if(inBean.getType() == XMPPBean.TYPE_RESULT){
			// if we are waiting for this result, accept the XMPPBean
			BeanTimePair btp = mWaitingForResultBeans.remove(inBean.getId());
			isBeanAccepted = ((btp != null) && (!btp.DeleteFromWaitings));
			if(isBeanAccepted) resultMsg += " Bean was expected Result";
			
			// if it's coming from a unavailable player, accept the XMPPBean and let the player rejoin
			XHuntPlayer plr = mController.getActGame().getPlayerByJid(inBean.getFrom());
			if((plr == null) || (!plr.isOnline())) {
				if(!plr.isOnline()) resultMsg += " Bean was accepted because it was sent by a Player marked as offline";
				isBeanAccepted = true;
				handleReturningPlayer(inBean.getFrom());
			}
		}
		else if (inBean.getType() == XMPPBean.TYPE_GET || inBean.getType() == XMPPBean.TYPE_SET){
			resultMsg += " Bean was accepted because it was of type GET or SET";
			isBeanAccepted = true;
		}
		
		if(!isBeanAccepted && (inBean.getType() != XMPPBean.TYPE_ERROR))
			resultMsg += " Bean was rejected because it neither was an expected result, nor did it come from an offline Player";
		
		if(inBean.getType() == XMPPBean.TYPE_ERROR)
			resultMsg += " Bean was of Type Error, maybe addressee is offline";
		
		LOGGER.info("Verifying incoming IQ: " + beanToString(inBean)
				+ System.getProperty("line.separator") + "-->" + resultMsg);
		
		// if we are waiting for other responses from this player, set a boolean in the corresponding BeanTimePairs
		// so that checkForDelayedResultBeans() knows that this Player shouldn't be marked as offline
		if(isBeanAccepted) {
			int cnt = 0;
			for(Map.Entry<String, BeanTimePair> entry : mWaitingForResultBeans.entrySet()) {
				if(entry.getValue().Bean.getTo().equals(inBean.getFrom())) {
					entry.getValue().playerGaveSignOfLive = true;
					cnt++;
				}
			}
			if(cnt > 0)
				LOGGER.info("received newer bean from player who still owes " + cnt + " response(s), prevent marking him as offline");
		}

		return isBeanAccepted;
	}
	
	
	private IMobilisXHuntOutgoing _proxyOutgoingMapper = new IMobilisXHuntOutgoing() {
		
		@Override
		public void sendXMPPBean( XMPPBean out ) {
			Connection.this.sendXMPPBean( out );
		}
		
		@Override
		public void sendXMPPBean( XMPPBean out, IXMPPCallback< ? extends XMPPBean > callback ) {
			if(!(callback instanceof EmptyCallback))
				_waitingCallbacks.put( out.getId(), callback );
			
			sendXMPPBean( out );
		}
	};
	
	
	/**
	 * The Class BeanTimePair is used to store further information about the delay status 
	 * of a XMPPBean.
	 */
	private class BeanTimePair {
		
		/** The XMPPBean. */
		public XMPPBean Bean;
		
		/** The timestamp when the XMPPBean was send. */
		public long TimeStamp;
		
		/** The count of delayed periods of this XMPPBean. */
		public int DelayedPeriods = 0;
		
		/** True if this XMPPBean should be removed from the list of waiting XMPPBeans 
		 * while next check for result XMPPBeans happens. */
		public boolean DeleteFromWaitings = false;
		
		/** Is set to true if the player sent other beans although he didn't respond to this one.
		 *  Prevents player from being set to offline if he just didn't respond to a single bean. */
		public boolean playerGaveSignOfLive = false;
		
		/**
		 * Instantiates a new BeanTimePair.
		 *
		 * @param bean the XMPPBean
		 * @param timeStamp the timestamp when this XMPPBean was send
		 */
		public BeanTimePair(XMPPBean bean, long timeStamp){
			this.Bean = bean;
			this.TimeStamp = timeStamp;
		}
	}

}
