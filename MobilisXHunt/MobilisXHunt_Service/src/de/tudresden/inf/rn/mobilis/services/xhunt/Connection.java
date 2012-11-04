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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.MultiUserChat;

import de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent;
import de.tudresden.inf.rn.mobilis.server.deployment.helper.ProxyBean;
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
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.IXMPPCallback;
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
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.TransferTicketRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.TransferTicketResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.UpdatePlayerRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.UpdatePlayerResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.UpdateTicketsRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.UpdateTicketsResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.UsedTicketsRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.UsedTicketsResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.state.GameStateGameOver;
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
	
	/** The waiting timeout in milliseconds for a XMPPBean of type result. */
	private long mResultBeansTimeoutMillis = 15 * 1000;
	
	/** The Timer for checking for delayed XMPPBeans of type result. */
	private Timer mDelayedResultBeansTimer;
	
	/** The limit for delayed result-XMPPBeans. This number determines, how often 
	 * a result-XMPPBean can miss the mResultBeansTimeoutMillis before the 
	 * XMPP-user is declared as not replying. */
	private int mLimitForDelayedPeriods = 3;
	
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
	private ConcurrentHashMap<String, BeanTimePair> mWatingForResultBeans 
		= new ConcurrentHashMap<String, Connection.BeanTimePair>();
	
	/** The list of players which are unavailable. This players will not get any normal XMPPBean 
	 * until the players are respond to the SnapshotBean. */
	private ArrayList<String> mUnavailablePlayers;
	
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
		this.mUnavailablePlayers = new ArrayList<String>();
		
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
	 * This method is observing the result-XMPPBeans of each request to the game players. 
	 * Each XMPPBean can be in one of the three states:
	 * 
	 * a) XMPPBean has not respond in mLimitForDelayedPeriods or less periods
	 * b) XMPPBean is of type SnapshotBean and wasn't responded in less than mLimitForDelayedPeriods
	 * c) XMPPBean is of type SnapshotBean and wasn't responded in mLimitForDelayedPeriods
	 * 
	 * This leads to the following actions:
	 * 
	 * ad a) increase delayed periods by 1
	 * ad b) do not send any normal XMPPBeans beside the SnapshotBeans to synchronize the player
	 * ad c) remove player from current game
	 */
	public void checkForDelayedResultBeans(){
		// if the current game is not open for any players, stop observing the delayed result-XMPPBeans
		if(!mController.getActGame().isGameOpen())
			stopDelayedResultBeansTimer();
		
		// get the current timestamp in milliseconds
		long currentTime = System.currentTimeMillis();
		LOGGER.info("check WaitingBeans; size: " + mWatingForResultBeans.size());
		
		// if we are waiting for result-XMPPBeans, print out information of the mWatingForResultBeans
		if(mWatingForResultBeans.size() > 0){
			printWaitingBeanMap();
		}
		
		// XMPPBeans to remove from mWatingForResultBeans
		ArrayList<String> removeWaitingBeansIds = new ArrayList<String>(); 
		
		// check each XMPPBean in mWatingForResultBeans for response condition
		for(Map.Entry<String, BeanTimePair> entry : mWatingForResultBeans.entrySet()){
			// if player is unavailable or no more in game, do not send XMPPBeans anymore
			if(entry.getValue().DeleteFromWaitings
					|| mController.getActGame()
						.getPlayerByJid(entry.getValue().Bean.getTo()) == null){ 
				removeWaitingBeansIds.add(entry.getKey());
				
				// skip rest of condition checking, because this player is no more part of this game
				continue;
			}
			
			// check if a result-XMPPBean has reached the timeout mResultBeansTimeoutMillis
			if((entry.getValue().TimeStamp + mResultBeansTimeoutMillis) < currentTime){		
				// check if a result-XMPPBean has not reached the maximum count of delayed timeout periods
				// in mLimitForDelayedPeriods
				if(entry.getValue().DelayedPeriods < mLimitForDelayedPeriods){
					// if no FileTransfer is active, increase delayed period of the XMPP-Bean
					if(!mIsFiletransferActive)
						entry.getValue().DelayedPeriods++;
					
					/*if(entry.getValue().Bean.getNamespace().equals(SnapshotBean.NAMESPACE))
						sendSnapshot(entry.getValue());
					else
						sendDelayedBean(entry.getValue());*/
					
					LOGGER.info(entry.getValue().DelayedPeriods + ". delay of " + entry.getKey());
				}
				// else if the result-XMPPBean has not replayed in mLimitForDelayedPeriods
				else{
					// if the result-XMPPBean was of type SnapshotBean, the player seems to be no more 
					// available for this game
					if(entry.getValue().Bean.getNamespace().equals(SnapshotRequest.NAMESPACE)){
//						if(mUnavailablePlayers.contains(entry.getValue().Bean.getFrom()))
							handlePlayerUnavailable(entry.getValue().Bean.getTo());
						/*else{
							entry.getValue().DelayedPeriods++;
						}*/
					}
					// else if the result-XMPPBean was not a SnapshotBean, handle the not replying player
					else
						handlePlayerNotReplies(entry.getValue().Bean.getTo());
				}
			}
		}
		
		// remove all result-XMPPBeans from mWatingForResultBeans which were marked before
		for(String waitingBeanId : removeWaitingBeansIds)
			mWatingForResultBeans.remove(waitingBeanId);
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
	 * Handle if a player doesn't reply.
	 *
	 * @param playerJid the jid of the player
	 */
	public void handlePlayerNotReplies(String playerJid){
		LOGGER.warning("Buddy doesn't reply: " + playerJid);
		
		// store the player in the list of unavailable players
		mUnavailablePlayers.add(playerJid);
		
		// Mark each result-XMPPBean which we are waiting for for deletion
		for(BeanTimePair pair : mWatingForResultBeans.values()){
			if(pair.Bean.getTo().equals(playerJid))
				pair.DeleteFromWaitings = true;
		}
		
		// try to send a current SnapshotBean to the player
		sendSnapshot(playerJid);		
	}
	
	/**
	 * Handle if a player is unavailable.
	 *
	 * @param playerJid the jid of the player
	 */
	private void handlePlayerUnavailable(String playerJid){
		LOGGER.warning("Buddy unavailable: " + playerJid);
		
		// try to send at least an PlayerExitBean to the unavailable player, but 
		// do not wait for result
		GameOverRequest bean = new GameOverRequest( "You were kicked because of missing response messages." );
		bean.setTo( playerJid );
		sendBean(bean);
		
		// Mark each result-XMPPBean which we are waiting for for deletion
		for(BeanTimePair pair : mWatingForResultBeans.values()){
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
		// id unavailable player is not Mr.X
		else{
			// remove player from game
			mController.getActGame().removePlayerByJid(playerJid);
			
			// notify all other players about removing of the unavailable player
			mController.getActGame().getGameState().sendPlayersBean("Player " + unavailablePlayer.getName()
					+ " is no more available", new IXMPPCallback< PlayersResponse >() {
						
						@Override
						public void invoke( PlayersResponse xmppBean ) {
							// Do nothing
						}
					});
		}		

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
	 * Prints detailed information about the  waiting XMPPBeans.
	 */
	public void printWaitingBeanMap(){
		LOGGER.info("            WaitingBeans: " + mWatingForResultBeans.size());
		
		for(Map.Entry<String, BeanTimePair> entry : mWatingForResultBeans.entrySet()){
			LOGGER.info("WaitingBean: [" 
					+ " id=" + entry.getKey()
					+ " timestamp=" + entry.getValue().TimeStamp
					+ " delayedPeriod=" + entry.getValue().DelayedPeriods
					+ " delete=" + entry.getValue().DeleteFromWaitings
					+ " " + beanToString(entry.getValue().Bean));
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
	
	/**
	 * This method is used to send a delayed XMPPBean.
	 *
	 * @param beanTimePair the BeanTimePair containing the XMPPBEan and further information
	 */
	private void sendDelayedBean(BeanTimePair beanTimePair){
		beanTimePair.TimeStamp = System.currentTimeMillis();		
		
		mWatingForResultBeans.put(beanTimePair.Bean.getId(), beanTimePair);
		sendBean(beanTimePair.Bean.clone());			
	}
	
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
		
		LOGGER.info("sendIQ: " + beanToString(bean));
		
		// if the player is not available, do not send any XMPPBEan beside a SnapshotBEan
		if(!mUnavailablePlayers.contains(bean.getTo())
				|| bean.getNamespace().equals(SnapshotRequest.NAMESPACE)){
			// just wait for XMPPBeans of type get or set
			if(bean.getType() == XMPPBean.TYPE_SET
					|| bean.getType() == XMPPBean.TYPE_GET){
				
				// add a copy of the XMPPBean to the list of waiting XMPPBeans mWatingForResultBeans
				XMPPBean clone = bean.clone();
				mWatingForResultBeans.put(bean.getId(), new BeanTimePair(clone, System.currentTimeMillis()));
			}
			
			// send XMPPBean
			sendBean(bean);
			
			return true;
		}
		else
			return false;		
	}
	
	/**
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
	private boolean sendXMPPBean(XMPPBean bean, String to, int type){
		bean.setTo(to);
		bean.setType(type);
		
		return this.sendXMPPBean(bean);
	}
	
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
	
	/**
	 * Send a XMPPBean of type result using the original XMPPBean for routing information.
	 *
	 * @param resultBean the result XMPPBean
	 * @param fromBean the original XMPPBean
	 * @return true, if sending successful
	 */
	private boolean sendXMPPBeanResult(XMPPBean resultBean, XMPPBean fromBean){
		resultBean.setTo(fromBean.getFrom());
		resultBean.setType(XMPPBean.TYPE_RESULT);
		resultBean.setId(fromBean.getId());
		
		return this.sendXMPPBean(resultBean);
	}
	
	/**
	 * Start the mDelayedResultBeansTimer which will call the checking function 
	 * {@link checkForDelayedResultBean} each mResultBeansTimeoutMillis.
	 */
	public void startDelayedResultBeansTimer(){
		mDelayedResultBeansTimer = new Timer();
		mDelayedResultBeansTimer.schedule(
			new TimerTask() {
				public void run() {
					checkForDelayedResultBeans();
		        }
		}, mResultBeansTimeoutMillis, mResultBeansTimeoutMillis);
	}
	
	/**
	 * Stops the mDelayedResultBeansTimer and the result checking.
	 */
	public void stopDelayedResultBeansTimer(){
		if(mDelayedResultBeansTimer != null)
			mDelayedResultBeansTimer.cancel();
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
		
		LOGGER.info("incomingIQ: " + beanToString(inBean));
		
		// just handle the XMPPBeans of type result, each other XMPPBeans automatically accepted 
		// so that the current GameState can handle this XMPPBean
		if(inBean.getType() == XMPPBean.TYPE_RESULT){
			// if we are waiting for this result, accept the XMPPBean, else not
			isBeanAccepted = (mWatingForResultBeans.remove(inBean.getId()) != null);
			
			// if this result XMPPBean is coming from a unavailable player 
			// remove this player from the unavailable list of players, accept the XMPPBean 
			// and deal with the player like normal
			if(mUnavailablePlayers.contains(inBean.getFrom())){
					//&& inBean.getNamespace() == SnapshotBean.NAMESPACE)
				mUnavailablePlayers.remove(inBean.getFrom());
				isBeanAccepted = true;
			}
		}
		else if (inBean.getType() == XMPPBean.TYPE_GET || inBean.getType() == XMPPBean.TYPE_SET){
			isBeanAccepted = true;
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
