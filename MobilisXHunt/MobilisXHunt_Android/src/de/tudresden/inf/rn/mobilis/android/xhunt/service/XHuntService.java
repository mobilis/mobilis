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
package de.tudresden.inf.rn.mobilis.android.xhunt.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import de.tudresden.inf.rn.mobilis.android.xhunt.helper.SharedPrefHelper;
import de.tudresden.inf.rn.mobilis.android.xhunt.helper.Tools;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Game;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.GameState;
import de.tudresden.inf.rn.mobilis.android.xhunt.proxy.GPSProxy;
import de.tudresden.inf.rn.mobilis.android.xhunt.proxy.MXAProxy;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * The Class XHuntService.
 */
public class XHuntService extends Service{
	
	/** The Constant TAG for logging. */
	public final static String TAG = "XHuntService";
	
	/** The GPSProxy instance. */
	private GPSProxy mGpsProxy;
	
	/** The MXAProxy instance. */
	private MXAProxy mMxaProxy;
	
	/** The Game instance. */
	private Game mGame;
    
    /** The current GameState. */
    private GameState mGameState;
	
	/** The SharedPrefHelper. */
	private SharedPrefHelper mSharedPrefHelper;
	
	/** The Tools. */
	private Tools mTools;	
	
	/** The local binder to bind this service. */
	private LocalBinder mBinder = new LocalBinder();

    /**
     * The Class LocalBinder.
     */
    public class LocalBinder extends Binder {
    	
	    /**
	     * Gets the XHuntService.
	     *
	     * @return the XHuntService
	     */
	    public XHuntService getService() {
            return XHuntService.this;
        }
    }

	
	/**
	 * Creates a new game instance.
	 *
	 * @return the new game
	 */
	public Game createNewGame(){
		this.mGame = new Game();
		//this.mGpsProxy.startGps();
		
		return getCurrentGame();
	}
    
	/**
	 * Gets the current game instance.
	 *
	 * @return the current game
	 */
	public Game getCurrentGame() {
		if(this.mGame == null)
			this.mGame = new Game();
		
		return mGame;
	}
	
	/**
	 * Gets the current GameState.
	 *
	 * @return the current GameState
	 */
	public GameState getGameState(){
		return mGameState;
	}
	
    /**
     * Gets the GPSProxy.
     *
     * @return the GPSProxy
     */
    public GPSProxy getGPSProxy(){
    	return mGpsProxy;
    }
    
    /**
     * Gets the MXAProxy.
     *
     * @return the MXAProxy
     */
    public MXAProxy getMXAProxy(){
    	return mMxaProxy;
    }
	
	/**
	 * Gets the SharedPrefHelper.
	 *
	 * @return the SharedPrefHelper
	 */
	public SharedPrefHelper getSharedPrefHelper(){
		return mSharedPrefHelper;
	}
	
	/**
	 * Gets the Tools.
	 *
	 * @return the Tools
	 */
	public Tools getTools(){
		return mTools;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		startService();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
	  super.onDestroy();

	  stopService();
	  mTools.stopWritingLogToFile();
	}	
	
	/**
	 * Sets the current GameState.
	 *
	 * @param newState the current GameState
	 */
	public void setGameState(GameState newState) {
		mGameState = newState;
	}
	
	/**
	 * Start service and init the default attributes.
	 */
	private void startService() {
		Log.v(TAG, TAG + " started");
		
		mGpsProxy = new GPSProxy(this);
		mMxaProxy = new MXAProxy(this);
		
		mSharedPrefHelper = new SharedPrefHelper(getApplicationContext());
		mTools = new Tools(getApplicationContext());
		
		mGameState = new GameStateDummy();
	}
	
	/**
	 * Stop service and disconnect MXAProxy and unsubscribe GPS updates.
	 */
	private void stopService(){
		mGpsProxy.stopGps();
		mMxaProxy.disconnect();
		Log.v(TAG, TAG + " stopped");
	}
	
	
	/**
	 * The Class GameStateDummy is the first GameState when no Game 
	 * instance exists.
	 */
	private class GameStateDummy extends GameState {

		/* (non-Javadoc)
		 * @see de.tudresden.inf.rn.mobilis.android.xhunt.model.GameState#processPacket(de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean)
		 */
		@Override
		public void processPacket(XMPPBean bean) {}		
	}

}
