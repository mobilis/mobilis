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
/**
 * Activity that shows the Map of the city with all stations and its
 * connections as well as the actual positions of the players.
 */
package de.tudresden.inf.rn.mobilis.android.xhunt.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import de.tudresden.inf.rn.mobilis.android.xhunt.Const;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.GameState;
import de.tudresden.inf.rn.mobilis.android.xhunt.proxy.MXAProxy;
import de.tudresden.inf.rn.mobilis.android.xhunt.service.ServiceConnector;
import de.tudresden.inf.rn.mobilis.android.xhunt.ui.DialogRemoteLoading;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.activities.Setup;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.coordination.MobilisServiceDiscoveryBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.coordination.MobilisServiceInfo;

/**
 * The Class MainActivity is the entrypoint of the game.
 */
public class MainActivity extends Activity {
	
	/** Identifier for the Log outputs *. */
	public static final String TAG = "MainActivity";
	
	/** The ServiceConnector to connect to XHuntService. */
	private ServiceConnector mServiceConnector;
	
	/** The MXAProxy. */
	private MXAProxy mMxaProxy;
	
	/** Dialog that displays if client is waiting for server acks. */
	private DialogRemoteLoading mRemoteLoadingDialog;
	
	/** Is used, if Mobilis-Server supports XHunt-Service. */
	private static final int CODE_SERVICES_AVAILABLE = 1;
	
	/** Is used, if Mobilis-Server doesn't supports XHunt-Service. */
	private static final int CODE_SERVICE_XHUNT_UNAVAILABLE = -1;
	
	/** Is used, if contacting the Mobilis-Server fails. */
	private static final int CODE_SERVER_RESPONSE_ERROR = -2;
    
    /** Handler to handle MobilisServiceDiscoveryBeans. */
    private Handler mServiceDiscoveryResultHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// If XHunt-Service is available on Mobilis-Server, switch to OpenGamesActivity
			if(msg.what == CODE_SERVICES_AVAILABLE){
				Intent i = new Intent(MainActivity.this, OpenGamesActivity.class);
				startActivity(i);
			}
			// If XHunt-Service is not available on Mobilis-Server, display a Toast
			else if(msg.what == CODE_SERVICE_XHUNT_UNAVAILABLE){
				Toast.makeText(MainActivity.this, "No XHunt Service installed on Server",
						Toast.LENGTH_SHORT).show();
			}
			// If Mobilis-Server doesn't respond, display a Toast
			else if(msg.what == CODE_SERVER_RESPONSE_ERROR){
				Toast.makeText(MainActivity.this, "Server not found. Please check your settings!",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
    
    /** The handler which is called, if XMPP connection was established successfully. */
    private Handler mXmppConnectedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// Dismiss RemoteLoadingDialog
			if(mRemoteLoadingDialog != null){
				mRemoteLoadingDialog.cancel();
			}
			
			// Notify user that XMPP is connected 
			Toast.makeText(MainActivity.this, "XMPP connection established", Toast.LENGTH_SHORT).show();	
			mMxaProxy.getIQProxy().registerCallbacks();
			
			// Start game
			startGame();
		}
	};
	
	/** The handler which is called if the XHuntService was bound. */
    private Handler mXHuntServiceBoundHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//Toast.makeText(MainActivity.this, "Service Bound",
	        //        Toast.LENGTH_SHORT).show();
			mServiceConnector.getXHuntService().setGameState(new GameStateMain());
			mMxaProxy = mServiceConnector.getXHuntService().getMXAProxy();	
			
			
			boolean staticMode = mServiceConnector.getXHuntService().getSharedPrefHelper()
					.getValueAsBool(getResources().getString(R.string.bundle_key_settings_staticmode));
			if(staticMode)
				mMxaProxy.setStaticMode(true);
			else
				mMxaProxy.setStaticMode(false);
			
			boolean logging = mServiceConnector.getXHuntService().getSharedPrefHelper()
					.getValueAsBool(getResources().getString(R.string.bundle_key_settings_logging));
			if(logging)
				mServiceConnector.getXHuntService().getTools().writeLogToFile();
			else
				mServiceConnector.getXHuntService().getTools().stopWritingLogToFile();
		}
	};
		
	/**
	 * Bind XHuntService using the mXHuntServiceBoundHandler and start local XHuntService.
	 */
	private void bindXHuntService(){
    	mServiceConnector = new ServiceConnector(this);
    	mServiceConnector.startXHuntService();
    	mServiceConnector.doBindXHuntService(mXHuntServiceBoundHandler);
	}
	
	/**
	 * Establish a connection to the XMPP-Server. 
	 * If connection is already established, game will be started.
	 */
	private void connectToXMPP(){		
		if(mMxaProxy != null
				&& mMxaProxy.isConnected()){
			startGame();
		}
		else{
			mRemoteLoadingDialog.setLoadingText("Start up MXA.\n\n     Please wait...");
			mRemoteLoadingDialog.run();
		
			mMxaProxy.registerXMPPConnectHandler(mXmppConnectedHandler);
			try {
				mMxaProxy.connect();
			} catch (RemoteException e) {
				Log.e(TAG, e.getMessage());
				Toast.makeText(MainActivity.this, "Failed to connect to MXA: " + e.getLocalizedMessage(),
						Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public MXAProxy getMXAProxy() {
		return mMxaProxy;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		// If local XHuntService is up, unregister all IQ-Listeners and stop the local XHuntService
		if(mServiceConnector != null
				&& mServiceConnector.getXHuntService() != null){
			mMxaProxy.getIQProxy().unregisterCallbacks();
			
			mServiceConnector.getXHuntService().stopSelf();
			mServiceConnector.doUnbindXHuntService();
		}
		
		super.finish();
	}
	
	/**
	 * Initialize all UI elements from resources.
	 */
    private void initComponents(){
    	mRemoteLoadingDialog = new DialogRemoteLoading(this, Const.CONNECTION_TIMEOUT_DELAY + (10 * 1000));
    	
    	Button btn_Play = (Button)findViewById(R.id.main_btn_play);
    	btn_Play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!MXAController.get().checkSetupDone()) {
					Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.main_toast_xmpp_setup_needed), Toast.LENGTH_SHORT).show();
					Intent xmppSetupIntent = new Intent(MainActivity.this, Setup.class);
					startActivity(xmppSetupIntent);
				} else if (mMxaProxy != null) {
					mMxaProxy.getIQProxy().updateServerJid();
					
					if(!mMxaProxy.isConnected())
						connectToXMPP();
					else{
						startGame();
					}
				}
			}
		});
    	
    	Button btn_Settings = (Button)findViewById(R.id.main_btn_settings);
    	btn_Settings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            	startActivity(i);
			}
		});
    	
    	Button btn_Instructions = (Button)findViewById(R.id.main_btn_instructions);
    	btn_Instructions.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, GameInstructionActivity.class);
				startActivity(i);
			}
		});
    	
    	Button btn_Version = (Button)findViewById(R.id.main_btn_version);
    	btn_Version.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, VersionActivity.class);
				startActivity(i);
			}
		});
    	
    	/*Button btn_Profile = (Button)findViewById(R.id.main_btn_profile);
    	btn_Profile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO ProfileActivity
			}
		});*/
    	
    	Button btn_Exit = (Button)findViewById(R.id.main_btn_exit);
    	btn_Exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MXAController.get().setSharedPreferencesName(this,"de.tudresden.inf.rn.mobilis.android.xhunt.mxa");
        
		initComponents();
		bindXHuntService();
	}
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// If the Back-Button of the Device was pressed, finish the application 
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	this.finish();
	    }
	    
	    return super.onKeyDown(keyCode, event);	    
	}
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		mRemoteLoadingDialog = new DialogRemoteLoading(this, Const.CONNECTION_TIMEOUT_DELAY + (10 * 1000));
		
		if(mServiceConnector.getXHuntService() != null)
			mServiceConnector.getXHuntService().setGameState(new GameStateMain());
		
		super.onResume();
	}
    
    /**
     * Start the game. This will set the nickname of the player and send an empty
     * MobilisServiceDiscoveryBean to the Mobilis-Server.
     */
    private void startGame(){
		mMxaProxy.setNickname(mServiceConnector.getXHuntService().getSharedPrefHelper()
				.getValue(getResources().getString(R.string.bundle_key_settings_username)));

		mMxaProxy.getIQProxy().sendServiceDiscoveryIQ(null);
    }
    
    /**
     * The Class GameStateMain is an inner class which represents the current state of the game.
     */
    private class GameStateMain extends GameState {

		/* (non-Javadoc)
		 * @see de.tudresden.inf.rn.mobilis.android.xhunt.model.GameState#processPacket(de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean)
		 */
		@Override
		public void processPacket(XMPPBean inBean) {
			if(inBean.getType() == XMPPBean.TYPE_ERROR){
				Log.e(TAG, "IQ Type ERROR: " + inBean.toXML());
			}

			if( inBean instanceof MobilisServiceDiscoveryBean){
				MobilisServiceDiscoveryBean bean = (MobilisServiceDiscoveryBean)inBean;		

				// If responded MobilisServiceDiscoveryBean is not of kind ERROR, 
				// check Mobilis-Server response for XHunt support
				if(bean != null && bean.getType() != XMPPBean.TYPE_ERROR ){
					if(bean.getDiscoveredServices() != null
							&& bean.getDiscoveredServices().size() > 0){
						boolean serverSupportsXHunt = false;
						
						for(MobilisServiceInfo info : bean.getDiscoveredServices()){
							if(info.getServiceNamespace().toLowerCase().contains("xhunt")){
								serverSupportsXHunt = true;
								break;
							}								
						}
					
						if(serverSupportsXHunt){
							mServiceDiscoveryResultHandler.sendEmptyMessage(CODE_SERVICES_AVAILABLE);
						}
						else{
							mServiceDiscoveryResultHandler.sendEmptyMessage(CODE_SERVICE_XHUNT_UNAVAILABLE);
						}
					}
				}
				else if(bean.getType() == XMPPBean.TYPE_ERROR){
					mServiceDiscoveryResultHandler.sendEmptyMessage(CODE_SERVER_RESPONSE_ERROR);
				}
				
			}
			// Other Beans of type get or set will be responded with an ERROR
			else if(inBean.getType() == XMPPBean.TYPE_GET
					|| inBean.getType() == XMPPBean.TYPE_SET) {
				inBean.errorType = "wait";
				inBean.errorCondition = "unexpected-request";
				inBean.errorText = "This request is not supportet at this game state(main)";
				
				mMxaProxy.getIQProxy().sendXMPPBeanError(inBean);
			}		
		}
    	
    }
}