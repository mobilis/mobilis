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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import de.tudresden.inf.rn.mobilis.android.xhunt.Const;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.CancelTimerResponse;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.DepartureDataResponse;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.DepartureInfo;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.GameOverRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.IXMPPCallback;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.LocationInfo;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.LocationRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.PlayerExitResponse;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.PlayerInfo;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.PlayersRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.RoundStatusRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.SnapshotRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.StartRoundRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.TargetResponse;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.UsedTicketsInfo;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.UsedTicketsResponse;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Game;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.GameState;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Route;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.RouteManagement;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Station;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Ticket;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.XHuntPlayer;
import de.tudresden.inf.rn.mobilis.android.xhunt.proxy.GPSProxy;
import de.tudresden.inf.rn.mobilis.android.xhunt.proxy.MXAProxy;
import de.tudresden.inf.rn.mobilis.android.xhunt.service.ServiceConnector;
import de.tudresden.inf.rn.mobilis.android.xhunt.ui.DialogDepartures;
import de.tudresden.inf.rn.mobilis.android.xhunt.ui.DialogRemoteLoading;
import de.tudresden.inf.rn.mobilis.android.xhunt.ui.DialogUsedTickets;
import de.tudresden.inf.rn.mobilis.android.xhunt.ui.PanelInfo;
import de.tudresden.inf.rn.mobilis.android.xhunt.ui.PanelTickets;
import de.tudresden.inf.rn.mobilis.android.xhunt.ui.PanelTimer;
import de.tudresden.inf.rn.mobilis.android.xhunt.ui.overlay.PlayerIconOverlay;
import de.tudresden.inf.rn.mobilis.android.xhunt.ui.overlay.RoutesOverlay;
import de.tudresden.inf.rn.mobilis.android.xhunt.ui.overlay.StationSignOverlay;
import de.tudresden.inf.rn.mobilis.android.xhunt.ui.overlay.TargetRouteOverlay;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPIQ;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * The Class XHuntMapActivity is the most important class in this game. This 
 * class provides a map to navigate and several game items to interact with.
 */
public class XHuntMapActivity extends MapActivity {
	
	/** Identifier for the Log outputs. */
	public static final String TAG = "XHuntMapActivity";
	
	/** The MapView to display game elements. */
	private MapView mapView;
	
	/** The overlays are the visual game elements like players. */
	private List<Overlay> mapOverlays;
	
	/** The overlay for all routes which the players can use to move forward. */
	private RoutesOverlay routeOverlay;
	
	/** The overlay for all stations which are connected by the routes. */
	private StationSignOverlay stationSignOverlay;
	
	/** The overlay of all players in the current game. */
	private PlayerIconOverlay playerOverlay;
	
	/** The overlay of all players targets to see the current target station of 
	 * the players. */
	private TargetRouteOverlay targetRouteOverlay;
	
	/** The current tapped reachable station of the own player if he can choose the 
	 * next target. */
	private Station tappedReachableStation;
	
	/** The possible routes from the current station. */
	ArrayList<Route> possibleRoutes;
	
	/** The MXAProxy. */
	private MXAProxy mMxaProxy;
	
	/** The Game class which contains the gaming information. */
	private Game mGame;
	
	/** The info panel to display several information at the bottom of the display. */
	private PanelInfo mPanelInfo;
	
	/** The timer panel to display the start timer if it is active. */
	private PanelTimer mPanelTimer;
	
	/** The tickets panel to display the current tickets and round number of the own 
	 * player. */
	private PanelTickets mPanelTickets;
	
	/** The ServiceConnector to connect to XHuntService. */
	private ServiceConnector mServiceConnector;
	
	/** The RouteManagement class to provide several helper functions concerning the 
	 * routes. */
	private RouteManagement mRouteManagement;
	
	/** The GPSProxy to fetch current position of the own player. */
	private GPSProxy mGpsProxy;
	
	/** Dialog that displays if client is waiting for server acks. */
	private DialogRemoteLoading mRemoteLoadingDialog;
	
	/** The dialog for departures from the current station, reachable via menu. 
	 * !Warning: this function is not available on server in the current version. */
	private DialogDepartures mDialogDepartures;
	
	/** The dialog for the used tickets o all players, reachable via menu. */
	private DialogUsedTickets mDialogUsedTickets;
	
	//TODO: XHuntMapActivity: add-on focus a player, focus map center
	
    /** The handler to send a cancel start timer command to server using 
     * CancelStartTimerBean. */
	private Handler mCancelTimerRequestHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mMxaProxy.getIQProxy().getProxy().CancelStartTimer( 
					mMxaProxy.getIQProxy().getGameServiceJid(), 
					_cancelTimerCallback );
		}
	};
	
    /** The handler for requests of the server if timer was canceled by Mr.X. */
    private Handler mCancelTimerResponseHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(mPanelTimer != null){
				mPanelTimer.cancelTimer();
				mPanelInfo.setInfoText("Timer canceled. Please wait for location fix from server!");
			}
		}
	};
	
    /** This handler is used to react on exit game messages from server. */
    private Handler mExitGameHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// Dismiss loading dialog
			if(mRemoteLoadingDialog != null){
				mRemoteLoadingDialog.cancel();
			}
			
			// If server has confirmed own exit of the game
			if(msg.what == 0){
				XHuntMapActivity.this.finish();
			}
			
			// Notify the player if he was kicked by the moderator and exit the game
			if(msg.what == 1){
				AlertDialog.Builder ad = new AlertDialog.Builder(XHuntMapActivity.this);
		    	
				ad.setTitle("Exit Game.");
		    	ad.setMessage("Sorry, but the Moderator has kicked you!")
		    	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	                XHuntMapActivity.this.finish();
		    	           }
		    	       });
		    	ad.setIcon(R.drawable.ic_warning_48);
		    	
		    	ad.show();
			}
			// Notify the player if he can not remove the player from current game
			else if(msg.what == -1 && msg.obj != null){
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(XHuntMapActivity.this);
				
				alertBuilder.setTitle("Removing player failed.");
				alertBuilder.setMessage(msg.obj.toString());
				alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
//			        	   XHuntMapActivity.this.finish();
			           }
				});
				alertBuilder.setIcon(R.drawable.ic_error_48);
				
				alertBuilder.show();
			}
		}
	};
	
    /** This handler is used to react on game over messages from server. */
    private Handler mGameOverHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// Display Mr.X on map
			playerOverlay.updateMrX(true);
			
			// Redraw map data
			updateMapData();			
			
			// If there were a reason, show a dialog why game is over
			if(msg.obj != null){
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(XHuntMapActivity.this);
				
				alertBuilder.setTitle("Game Over.");
				alertBuilder.setMessage(msg.obj.toString());
				alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   XHuntMapActivity.this.finish();
			           }
				});
				alertBuilder.setIcon(R.drawable.ic_warning_48);
				
				alertBuilder.show();
			}
		}
	};
	
    /** The multiuserchat handler which is called when a new chat message arrives. */
    private Handler mMucHandler = new Handler(){
	    @Override
		public void handleMessage(Message msg) {
	    	if(msg.obj != null){
	    		// Display a toast on top of the screen
		    	Toast toast = Toast.makeText(XHuntMapActivity.this, msg.obj.toString(), Toast.LENGTH_LONG);
		    	toast.setGravity(Gravity.TOP, 0, 50);
		    	toast.show();
		    	
		    	// Let the device vibrate
		    	mServiceConnector.getXHuntService().getTools().vibrateOnChatMessage();
	    	}
	    }
    };
    
    /** This handler is used to react on round status messages from server. */
    private Handler mRoundStatusHandler = new Handler(){
	    @Override
		public void handleMessage(Message msg) {
	    	// Get own player
	    	XHuntPlayer player = getMyPlayer();
	    	// Update the info panel about the new target reached status (toggle flag)
    		mPanelInfo.setTargetReached(player.getReachedTarget());
    		
    		// Update the text at the info panel
    		if(player.getReachedTarget())
    			mPanelInfo.setInfoText("Some players are still on the move.");
    		else if(player.getCurrentTargetId() > 0)    			
    			mPanelInfo.setInfoText("Follow the arrow to your target.");
    		
    		// Redraw the map data
	    	updateMapData();
	    }
    };
    
    /** This handler is used to react on different messages which needs a dialog to show. */
    private Handler mShowDialogHandler = new Handler(){
	    @Override
		public void handleMessage(Message msg) {
	    	// Dismiss loading dialog
			if(mRemoteLoadingDialog != null){
				mRemoteLoadingDialog.cancel();
			}
			
	    	switch(msg.what){
	    		// If an error happens, display an toast
		    	case -1:
		    		Toast.makeText(XHuntMapActivity.this,
		    				"Failed to load data: " + msg.obj.toString(), Toast.LENGTH_LONG).show();
		    		break;
		    	// If departures dialog should be shown, display this dialog if there is any 
		    	// content in it
		    	case DialogDepartures.DIALOG_ID:
		    		if(mDialogDepartures.hasContent())
		    			mDialogDepartures.show();
		    		else
		    			Toast.makeText(XHuntMapActivity.this, "No data available!",
		    					Toast.LENGTH_LONG).show();
		    			
		    		break;
		    	// If the used ticket dialog should be shown, display this dialog if there is any
		    	// content in it
		    	case DialogUsedTickets.DIALOG_ID:
		    		if(mDialogUsedTickets.hasContent())
		    			mDialogUsedTickets.show();
		    		else
		    			Toast.makeText(XHuntMapActivity.this, "No data available!",
		    					Toast.LENGTH_LONG).show();
		    		
		    		break;
	    	}
	    }
    };
    
    /** This handler is used to react on start round messages from server. */
    private Handler mStartRoundHandler = new Handler(){
	    @Override
		public void handleMessage(Message msg) {
	    	// Update the ticket panel with the new amount of tickets
	    	mPanelTickets.update();
	    	
	    	// Update the info panel
	    	mPanelInfo.setInfoText("Choose your next target!");
	    	mPanelInfo.setTargetReached(false);
	    	
	    	Log.v(TAG, "what: " + msg.what + " lastStaion: " 
	    			+ mGame.getPlayerByJID(mMxaProxy.getXmppJid()).getLastStationId());
	    	
	    	// Clear all reachable stations (this will color all stations as usal)
	    	mRouteManagement.resetReachableStations();
	    	mRouteManagement.updateReachableStations(msg.what);
	    	
	    	// Notify the player about the start of the new round
	    	AlertDialog.Builder builder = new AlertDialog.Builder(XHuntMapActivity.this);
			builder.setTitle("New Round")
 	       		.setMessage("Round " + mGame.getCurrentRound() + " has just begun.")
 	       		.setCancelable(false)    	     
 	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
 	           public void onClick(DialogInterface dialog, int id) {	
 	        	   // If own player is unmovable, he cannot choose the next target, 
 	        	   // so request the TargetBean with ticket id=0 to the server
 	        	   if(mGame.getRouteManagement().isMyPlayerUnmovable(getMyPlayer())){
 	        		   Log.v(TAG, "My Player is unmovable");
 	        		   mMxaProxy.getIQProxy().getProxy().Target( 
 	        				   mMxaProxy.getIQProxy().getGameServiceJid(), 
 	        				  getMyPlayer().getLastStationId(), 
 	        				  mGame.getCurrentRound(),
 	        				  0,
 	        				  true, 
 	        				  _targetCallback );
 	        	   }
 	        	   // If player can choose a next target, update the map data
 	        	   else{
 	        		   updateMapData();
 	        	   }
 	           }
 	       })
 	       .show();
	    }
    };
    
    /** This handler is used to react on target messages from server. */
    private Handler mTargetHandler = new Handler(){
	    @Override
		public void handleMessage(Message msg) {
	    	// If target was invalid, notify the player about the error
	    	if(msg.what == -1){
	    		Toast.makeText(XHuntMapActivity.this, 
	    				"Failed to set target: " + msg.obj.toString(), Toast.LENGTH_LONG).show();
	    	}
	    	else{
	    		// Update the ticket panel
	    		mPanelTickets.update();
	    		
	    		// Update the info panel
	    		mPanelInfo.setTargetReached(getMyPlayer().getReachedTarget());
	    		mPanelInfo.setInfoText("Follow the arrow to your target.");
	    		
	    		// Update tha map data
	    		updateMapData();
	    		
	    		// If Mr.X is visible in current round, notify the player by a toast about this
	    		if(mGame.isShowMrX() && !getMyPlayer().isMrX())
	    			Toast.makeText(XHuntMapActivity.this, "Mr.X is visible right now. Watch out!",
	    					Toast.LENGTH_LONG).show();
	    	}	    	
	    }
    };
    
    /** This handler is used to react on update players messages from server. */
    private Handler mUpdatePlayersHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// Update map data
			updateMapData();
			
			// If there were an info available about the update, display it to the player
			if(msg.obj != null){
				Toast.makeText(XHuntMapActivity.this, msg.obj.toString(), 
						Toast.LENGTH_LONG).show();
			}
		}
	};
	
    /** This handler is used to update the map data if something changed out of the gui thread. */
    private Handler mUpdateMapDataHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// Update the map data
			updateMapData();
		}
	};
	
	/** The handler which is called if the XHuntService was bound. */
    private Handler mXHuntServiceBoundHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mServiceConnector.getXHuntService().setGameState(new GameStatePlay());
			mMxaProxy = mServiceConnector.getXHuntService().getMXAProxy();
			mGame = mServiceConnector.getXHuntService().getCurrentGame();
			mRouteManagement = mGame.getRouteManagement();
			mGpsProxy = mServiceConnector.getXHuntService().getGPSProxy();
			
	        init();
	        initComponents();
	        initOverlays();
	        
	        // Init the panel which contains the start timer
	        mPanelTimer.startTimer(mGame.getGameStartTimer(), mCancelTimerRequestHandler);
	        
	        Bundle extras = getIntent().getExtras();
	        String beanId = "unknown";
	        
			if (extras != null)
				beanId = extras.getString(Const.BUNDLE_KEY_STARTROUNDID);
			
	        mMxaProxy.getIQProxy().getProxy().StartRound( 
	        		mMxaProxy.getIQProxy().getGameServiceJid(),
	        		beanId);
	        
	        mGpsProxy.restartGps(XHuntMapActivity.this);
		}
	};
	
	
	/** The receiver for changed location updates, which were send by 
	 * @see GPSProxy#sendLocationChangedBroadcast(). */
	private BroadcastReceiver mLocationChangedReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			int lat = intent.getIntExtra(Const.BUNDLE_KEY_LOCATION_CHANGED_LAT, -1);
			int lon = intent.getIntExtra(Const.BUNDLE_KEY_LOCATION_CHANGED_LON, -1);
			
			if(lat != -1 && lon != -1){
				getMyPlayer().setGeoLocation(lat, lon);
				updateMapData();
			}
		}
	};
	
	
	/**
	 * Bind XHuntService using the mXHuntServiceBoundHandler.
	 */
	private void bindXHuntService(){
    	mServiceConnector = new ServiceConnector(this);
    	mServiceConnector.doBindXHuntService(mXHuntServiceBoundHandler);
	}
	
    /* (non-Javadoc)
     * @see android.app.Activity#finish()
     */
    public void finish() {
    	// Stop receiving gps-updates
    	unregisterReceiver(mLocationChangedReceiver);
    	// Unbind @see XHuntService
    	mServiceConnector.doUnbindXHuntService();
    	super.finish();
    };
	
	/**
	 * Gets the current zoom level.
	 * 
	 * @return the current zoom level of the map.
	 */
	public int getCurrentZoomLevel() {
		if (mapView==null) return -1;
		return mapView.getZoomLevel();
	}	
	
	/**
	 * Gets the own player object.
	 *
	 * @return the own player
	 */
	private XHuntPlayer getMyPlayer(){
		// TODO: XHuntMapActivity#getMyPlayer: can be null
		return mGame.getPlayerByJID(mMxaProxy.getXmppJid());
	}
	
	/**
	 * Gets the Game object.
	 * 
	 * @return the Game object
	 */
	public Game getGame() {
		return mGame;
	}
	
    /**
     * Initializes the components.
     */
	private void init(){
		mRemoteLoadingDialog = new DialogRemoteLoading(this, Const.CONNECTION_TIMEOUT_DELAY);
        
        // Gets the MapView of this MapActivity
        mapView = (MapView) findViewById(R.id.mapview);
        
        // Inits the MapView required components
        initMapView();
        
        // if the own player object doesn't exist, finish the game
        if(getMyPlayer() == null){
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(XHuntMapActivity.this);
			
			alertBuilder.setTitle("Unexpected Failure.");
			alertBuilder.setMessage("You're player was kicked from server.");
			alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   XHuntMapActivity.this.finish();
		           }
			});
			alertBuilder.setIcon(R.drawable.ic_warning_48);
			
			alertBuilder.show();
        }

        // Defines a cursor which listens to the incoming chat messages and fires a toast message
        // on the map if there comes a new chat message in
        mMxaProxy.registerIncomingMessageObserver(this, mMucHandler, mGame.getChatID());
        
        // Subscribe to location change events in @see GPSProxy
        registerReceiver(mLocationChangedReceiver, new IntentFilter(GPSProxy.INTENT_LOCATION_CHANGED));
	}
	
	/**
	 * Initialize all UI elements from resources.
	 */
	private void initComponents() {
		// Init information panel on bottom
		mPanelInfo = (PanelInfo)findViewById(R.id.info_panel);
		mPanelInfo.setInfoText("Distribute while Timer is running!");
		
		// Init the ticket and round panel on top
		mPanelTickets = (PanelTickets) findViewById(R.id.ticket_panel);
		mPanelTickets.create(mGame);
		
		// Init the timer panel for start timer and enable timer for Mr.X or not
		// Just Mr.X can cancel the timer.
		mPanelTimer = (PanelTimer) findViewById(R.id.timer_panel);
		mPanelTimer.enableMrXTimer(getMyPlayer().isMrX());
		
		// Init the departures dialog with current device display size
		mDialogDepartures = new DialogDepartures(XHuntMapActivity.this,
				getWindowManager().getDefaultDisplay());
		
		// Init the used tickets dialog with current device display size
		mDialogUsedTickets = new DialogUsedTickets(XHuntMapActivity.this,
				getWindowManager().getDefaultDisplay());

		//Init all overlays aka game elements for this game.
		stationSignOverlay = new StationSignOverlay(mGame.getRouteManagement().getStationsAsList(), this);
		playerOverlay = new PlayerIconOverlay(this, mapView, mServiceConnector.getXHuntService());
		routeOverlay = new RoutesOverlay(this, mapView, mServiceConnector.getXHuntService());
		targetRouteOverlay = new TargetRouteOverlay(mServiceConnector.getXHuntService());
	}
	
	/**
	 * Inits the MapView of this MapActivity.
	 */
	private void initMapView(){
		// Enable the zoom controls
		mapView.setBuiltInZoomControls(true);
		
		// Registers a context menu to the MapView for displaying the draw possibilities
        registerForContextMenu(mapView);
        
        // Sets average of all station known my the area as center of the map
        mapView.getController().setCenter(mRouteManagement.getMapCenter());
        
        // Sets the zoom level of the map
        mapView.getController().setZoom(Const.MAP_MAX_ZOOM_LEVEL);
        
        // Saves the map overlays for adding the game required overlays while playing
        mapOverlays = mapView.getOverlays();
        
        // Sets an OnZoomListener to redraw the game elements on map
        mapView.getZoomButtonsController().setOnZoomListener(new ZoomButtonsController.OnZoomListener() {				
			@Override
			public void onZoom(boolean zoomIn) {
				if(zoomIn)
					mapView.getController().zoomIn();
				else 
					mapView.getController().zoomOut();
				
				updateMapData();
			}

			@Override
			public void onVisibilityChanged(boolean visible) {}
		});
	}
	
	/**
	 * Inits the overlays aka game elements of the map.
	 */
	private void initOverlays(){
		mapOverlays.add(routeOverlay);
		mapOverlays.add(stationSignOverlay);
		mapOverlays.add(playerOverlay);
		mapOverlays.add(targetRouteOverlay);
		
		mapView.invalidate();
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
    protected boolean isRouteDisplayed() {
        return false;
    }
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	public boolean onContextItemSelected(MenuItem item) {
		// If the item 'Select as next target' was tapped and there were routes
		// available for the own player to move to this target (has a SubMenu)
		if(item.getItemId() == R.id.menu_context_map_target
				&& item.hasSubMenu()){
			Menu subMenuTarget = item.getSubMenu();
			
			Log.v(TAG, "tapped stationId: " + tappedReachableStation.getId());
			Log.v(TAG, "my Tickets: " + mRouteManagement.getMyTickets().toString());
			Log.v(TAG, "ticketsforstation: " +  mRouteManagement
					.getMyRouteTicketsForTarget(getMyPlayer().getLastStationId(),
							tappedReachableStation.getId()).toString());
			
			int subItemId = 1;
			// Following information is require for an entry to move to the next target:
			// - the id of the ticket to use to move on
			// - the name of the ticket for simple identification for the player (like bus ticket)
			// - the name of the endstation of the route to identify the direction of the transport vehicle
			// - the name of the route to identify the transport vehicle itself
			//
			// At first, query all routes from own players current station to the target station 
			// and the id of available ticket for this route
			for(Map.Entry<Route, Integer> entry 
					: mRouteManagement.getMyRouteTicketsForTarget(getMyPlayer().getLastStationId(),
							tappedReachableStation.getId()).entrySet()){
				
				// Add a menu entry, goupId is the id of the ticket type
				// subItemId is increase for each loop by 1
				// and the CharSequence is a composition of the name of the route, name of the endstation
				// and the name of the ticket type
				subMenuTarget.add(
						entry.getValue(),
						subItemId, 
						0, 
						entry.getKey().getName() 
							+ " " + (entry.getKey().getPositionOfStation(getMyPlayer().getLastStationId()) 
									< entry.getKey().getPositionOfStation(tappedReachableStation.getId())
								? entry.getKey().getEnd()
								: entry.getKey().getStart())
							+ " (" 
							+ mRouteManagement.getAreaTickets().get(entry.getValue()).getName() + ")");
				
				subItemId++;
			}
			
			// Query all special tickets which are not route related and add them to the menu as well 
			for(Map.Entry<Integer, Integer> entry : mRouteManagement.getMyTickets().entrySet()){
				Ticket superiorTicket = mRouteManagement.getAreaTickets().get(entry.getKey());
				
				if(entry.getValue() > 0
						&& superiorTicket.isSuperior()){
					
					subMenuTarget.add(
							entry.getKey(),
							subItemId, 
							0, 
							"Use " + superiorTicket.getName() + " Ticket");
					
					subItemId++;
				}
			}
			
			// Add a cancel item to the menu to abort choosing the target
			subMenuTarget.add(-1, -1, 0, "Cancel");
		}
		// If the item "Select as suggestion" was tapped, send a TargetBean to the server 
		// using the id of the station, the current round number, ticket id as -1 and 
		// isFinal selection as true
		else if(item.getItemId() == R.id.menu_context_map_suggestion){
			mMxaProxy.getIQProxy().getProxy().Target( 
					mMxaProxy.getIQProxy().getGameServiceJid(), 
					tappedReachableStation.getId(),
					mGame.getCurrentRound(),
					Const.TICKET_ID_SUGGESTION, 
					false,
					_targetCallback );
		}
		// If ticket was tapped (see first if-case) and not the "Cancel" item
		else if(item.getGroupId() > -1 && item.getItemId() > -1
				&& item.getItemId() != R.id.menu_context_map_cancel){
			// If player isn't unmovable
			if(!mGame.getRouteManagement().isMyPlayerUnmovable(getMyPlayer())){
				Log.v("", "ticket: " 
						+ mGame.getRouteManagement().getAreaTickets().get(item.getGroupId()).getName());
				// Request new target to server using TargetBean
				mMxaProxy.getIQProxy().getProxy().Target(
						mMxaProxy.getIQProxy().getGameServiceJid(),
						tappedReachableStation.getId(),
						mGame.getCurrentRound(),
						item.getGroupId(), 
						true,
						_targetCallback);
			}
			else{
				//send last station and ticket id=0 if unmovable
				mMxaProxy.getIQProxy().getProxy().Target(
						mMxaProxy.getIQProxy().getGameServiceJid(),
						getMyPlayer().getLastStationId(),
						mGame.getCurrentRound(),
						Const.TICKET_ID_UNMOVABLE,
						true,
						_targetCallback);
			}
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onContextMenuClosed(android.view.Menu)
	 */
	public void onContextMenuClosed (Menu menu){
		// Reset the tappedReachableStation
		tappedReachableStation = null;
	}
	
	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        bindXHuntService();
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_map, menu);
		
        // Add station symbol and name as title of the context menu
		menu.setHeaderIcon(R.drawable.station_50px);
		menu.setHeaderTitle(tappedReachableStation.getName());

		// If the current round isn't the initial round
		if (mGame.getCurrentRound() > 0){
			menu.findItem(R.id.menu_context_map_target).setVisible(true);
			
			// Mr. X don't need the suggestion option, because he's playing alone
			if(!getMyPlayer().isMrX()){
				menu.findItem(R.id.menu_context_map_suggestion).setVisible(true);
			}
		}
	}
	
    /**
     * Overrides the onCreateOptionsmenu.
     *
     * @param menu the menu
     * @return true, if successful
     */
	public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map, menu);
        
		return true;		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// retain tapping of the back button and hint player that he's trying to leave the game
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	showExitDialog();
	        return true;
	    }
	    
	    return super.onKeyDown(keyCode, event);	    
	}
	
	/**
	 * Fires when a menu item was clicked.
	 * 
	 * @param item the item which was clicked
	 * 
	 * @return true, if on options item selected
	 */
	public boolean onOptionsItemSelected(MenuItem item){
		// Switch the item and find out the specific item object
		switch(item.getItemId()){
		// Menu 'chat' was tapped
		case R.id.menu_map_muc:
			
			// If chat is available, start chat activity
			if(mGame.getChatID() != null){
				Intent i = new Intent(XHuntMapActivity.this, MUCActivity.class);
	 			startActivity(i);
			}
			// Else retain the player of not connected chat
			else {
				AlertDialog.Builder ad = new AlertDialog.Builder(this);
		    	
		    	ad.setMessage("Sorry, but there is something wrong with the chat!")   	     
		    	       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	           }
		    	       });
		    	
		    	ad.show();
			}
			
 			return true;
 		// Menu 'game info' was tapped
		case R.id.menu_map_ingameinfo:
			// Start loading dialog
			mRemoteLoadingDialog.setLoadingText("Requesting ticket data.\n\n     Please wait...");
			mRemoteLoadingDialog.run();
			
			// Request list of used tickets from server
			mMxaProxy.getIQProxy().getProxy().UsedTickets( 
					mMxaProxy.getIQProxy().getGameServiceJid(), 
					_usedTicketsCallback );
			
			return true;
		// Menu 'departure monitor' was tapped
		case R.id.menu_map_departuremonitor:
			// Start loading dialog
			mRemoteLoadingDialog.setLoadingText("Requesting departure data.\n\n     Please wait...");
			mRemoteLoadingDialog.run();
			
			// Request departures from server
			mMxaProxy.getIQProxy().getProxy().DeprtureData( 
					mMxaProxy.getIQProxy().getGameServiceJid(), 
					getMyPlayer().getLastStationId(),
					_departureDataCallback );
			
			return true;
		// Menu 'quit game' was tapped
		case R.id.menu_map_quit:
			// Open an exit dialog
			showExitDialog();

			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// Each time the player is resuming from another activity, update the map
		updateMapData();
		
		super.onResume();
	}
	
	/**
	 * Shows an exit dialog.
	 */
	private void showExitDialog(){
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
    	ad.setTitle("Leave Game?");
    	ad.setMessage("So you really want to leave this game?");
    	ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
        	   // Start loading dialog
	       		mRemoteLoadingDialog.setLoadingText("Exit game and notify game server.\n\n     Please wait...");
	       		mRemoteLoadingDialog.setTimeOutHandler(mExitGameHandler);
	    		mRemoteLoadingDialog.run();

	    		// Notify the server about the leaving of the player
	       		mMxaProxy.getIQProxy().getProxy().PlayerExit( 
	       				mMxaProxy.getIQProxy().getGameServiceJid(), 
	       				mMxaProxy.getXmppJid(),
	       				false, 
	       				_playerExitCallback );
           }
       });
    	ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int id) {}
		});
    	
    	ad.show();
	}
	
	/**
	 * If a reachable station was tapped we save the station and pop up a context menu.
	 * 
	 * @param s the station
	 */
	public void reachableStationTapped(Station s) {
		tappedReachableStation = s;
		mapView.showContextMenu();
	}
    
    /**
     * This function will redraw and refresh all map components and it's data
     */
    private void updateMapData(){
		try{
			playerOverlay.updateMrX(mGame.isShowMrX());
			playerOverlay.updatePlayers();
			stationSignOverlay.update();			
			
			mapView.invalidate();
		}
		catch(NullPointerException e){}
    }
    
    
    private IXMPPCallback< CancelTimerResponse > _cancelTimerCallback = new IXMPPCallback< CancelTimerResponse >() {
		
		@Override
		public void invoke( CancelTimerResponse bean ) {
			if( bean.getType() != XMPPBean.TYPE_ERROR ){
				// Notify the gui to dismiss the start timer
				mCancelTimerResponseHandler.sendEmptyMessage(0);
			}
		}
	};
	
	private IXMPPCallback< TargetResponse > _targetCallback = new IXMPPCallback< TargetResponse >() {
		
		@Override
		public void invoke( TargetResponse bean ) {
			if(bean.getType() == XMPPIQ.TYPE_ERROR){
				// If target was invalid, notify the gui handler
				Message msg = new Message();
				msg.what = -1;
				msg.obj = bean.errorText;
				mTargetHandler.sendMessage(msg);
			}
			else {
				// If the new target wasn't a suggestion
				if(bean.getTicketId() != Const.TICKET_ID_SUGGESTION){
					// Decrease the amount of the used ticket by 1
					mGame.getRouteManagement().decreaseTicket(bean.getTicketId());
					// Display all stations as usual (no colored reachable stations)
					mGame.getRouteManagement().resetReachableStations();
					
					// Notify the gui handler about the new target
					mTargetHandler.sendEmptyMessage(0);
				}
			}
		}
	};
	
	private IXMPPCallback< UsedTicketsResponse > _usedTicketsCallback = new IXMPPCallback< UsedTicketsResponse >() {
		
		@Override
		public void invoke( UsedTicketsResponse bean ) {
			if(bean.getType() == XMPPIQ.TYPE_ERROR){
				// Notify the gui handler if an error happens
				Message msg = new Message();
				msg.what = -1;
				msg.obj = bean.errorText;
				mShowDialogHandler.sendMessage(msg);
			}
			else if( bean != null && bean.getType() != XMPPBean.TYPE_ERROR ){
				// Remove old list of used tickets
				mDialogUsedTickets.clearContent();
				
				// Add the new used tickets to the list of used tickets
				for(UsedTicketsInfo entry : bean.getUsedTickets()){
					ArrayList<Bitmap> tickets = new ArrayList<Bitmap>();
					
					// Get the icons of the tickets and add them to the used ticket list
					for(Integer ticketId : entry.getTicketIds()){
						tickets.add(mRouteManagement.getAreaTickets().get(ticketId).getIcon());
					}
					
					// add player icon, name and the used tickets as icons to the used ticket dialog
					mDialogUsedTickets.addPlayer(
							mGame.getPlayerByJID(entry.getJid()).getPlayerIconID(),
							mGame.getPlayerByJID(entry.getJid()).getName(),
							tickets);
				}
				
				// Notify the gui handler to display the used ticket dialog
				mShowDialogHandler.sendEmptyMessage(DialogUsedTickets.DIALOG_ID);
			}
		}
	};
	
	private IXMPPCallback< DepartureDataResponse > _departureDataCallback = new IXMPPCallback< DepartureDataResponse >() {
		
		@Override
		public void invoke( DepartureDataResponse bean ) {
			if(bean.getType() != XMPPBean.TYPE_ERROR ){
				// Remove old departures
				mDialogDepartures.clearContent();
				
				// Store new departures in mDialogDepartures
				for(DepartureInfo departure : bean.getDepartures()){
					mDialogDepartures.addDeparture(
							mRouteManagement.getAreaTickets().get(departure.getVehicleId()).getIcon(),
							departure.getVehicleName(),
							departure.getDirection(),
							departure.getTimeLeft());
				}
				
				// Notify the gui to open the dialog
				mShowDialogHandler.sendEmptyMessage(DialogDepartures.DIALOG_ID);
			}
		}
	};
	
	private IXMPPCallback< PlayerExitResponse > _playerExitCallback = new IXMPPCallback< PlayerExitResponse >() {
		
		@Override
		public void invoke( PlayerExitResponse bean ) {
			if(bean.getType() == XMPPIQ.TYPE_ERROR){
				Message msg = new Message();
				msg.what = -1;
				msg.obj = bean.errorText;
				
				// Notify the gui handler about the error
				mExitGameHandler.sendMessage(msg);
			}
			else {
				// If the exit bean is assigned to us act, else ignore
				/*if(bean.Jid.equals(mMxaProxy.getXmppJid())){
					// If the type is a RESULT, we send a request before for leaving the game
					if(bean.getType() == XMPPBean.TYPE_RESULT)
							mExitGameHandler.sendEmptyMessage(0);
					// Else the moderator has kicked own player
					else if(bean.getType() == XMPPBean.TYPE_SET){
						mExitGameHandler.sendEmptyMessage(1);
						
						// Confirm the exit player
						mMxaProxy.getIQProxy().sendPlayerExitIQResult(bean.getId());
					}
				}	*/ //TODO		
			}
		}
	};


    /**
     * The Class GameStatePlay represents a state of the game.
     */
    private class GameStatePlay extends GameState {

		/* (non-Javadoc)
		 * @see de.tudresden.inf.rn.mobilis.android.xhunt.model.GameState#processPacket(de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean)
		 */
		@Override
		public void processPacket(XMPPBean inBean) {
			if(inBean.getType() == XMPPBean.TYPE_ERROR){
				Log.e(TAG, "IQ Type ERROR: " + inBean.toXML());
			}
			

			else if( inBean instanceof GameOverRequest){
				handleGameOverBean((GameOverRequest)inBean);
			}
			else if( inBean instanceof LocationRequest){
				handleLocationBean((LocationRequest)inBean);
			}
			else if( inBean instanceof PlayersRequest){
				handlePlayersBean((PlayersRequest)inBean);
			}
			else if( inBean instanceof RoundStatusRequest){
				handleRoundStatusBean((RoundStatusRequest)inBean);
			}
			else if( inBean instanceof SnapshotRequest){
				handleSnapshotBean((SnapshotRequest)inBean);
			}
			else if( inBean instanceof StartRoundRequest){
				handleStartRoundBean((StartRoundRequest)inBean);
			}
			// Other Beans of type get or set will be responded with an ERROR
			else if(inBean.getType() == XMPPBean.TYPE_GET
					|| inBean.getType() == XMPPBean.TYPE_SET) {
				inBean.errorType = "wait";
				inBean.errorCondition = "unexpected-request";
				inBean.errorText = "This request is not supportet at this game state(Play)";
				
				mMxaProxy.getIQProxy().sendXMPPBeanError(inBean);
			}		
		}

		
		/**
		 * Handle GameOverBean.
		 *
		 * @param bean the XMPPBean
		 */
		private void handleGameOverBean(GameOverRequest bean){
			if( bean != null && bean.getType() != XMPPBean.TYPE_ERROR ){
				// Store the reason for game over in a Message object
				Message msg = new Message();
				msg.obj = bean.getReason();
				
				// Tell the gui handler to display a dialog with the game over message
				mGameOverHandler.sendMessage(msg);
				
				// Confirm the receiving of the game over message to server
				mMxaProxy.getIQProxy().getProxy().GameOver( mMxaProxy.getIQProxy().getGameServiceJid(), bean.getId());
			}
		}
		
		/**
		 * Handle LocationBean.
		 *
		 * @param bean the XMPPBean
		 */
		private void handleLocationBean(LocationRequest bean){
			if( bean != null && bean.getType() != XMPPBean.TYPE_ERROR ){
				// Update locations of all palyers
				mGame.updatePlayerLocations(bean.getLocationInfos());

				// Notify the gui handler to redraw all map elements (to refresh the locations)
				mUpdateMapDataHandler.sendEmptyMessage(0);
				Log.v(TAG, "gpsLoc: " + mGpsProxy.getCurrentLocationAsGeoPoint());
				
				GeoPoint geoPoint = mGpsProxy.getCurrentLocationAsGeoPoint();
				// Confirm the receiving of the locations and transmit own current location to server
				mMxaProxy.getIQProxy().getProxy().Location( 
						mMxaProxy.getIQProxy().getGameServiceJid(), 
						bean.getId(),
						new LocationInfo(
								mMxaProxy.getXmppJid(), 
								geoPoint.getLatitudeE6(), 
								geoPoint.getLongitudeE6() ));
			}
		}
		
		/**
		 * Handle PlayersBean.
		 *
		 * @param bean the XMPPBean
		 */
		private void handlePlayersBean(PlayersRequest bean){
			if( bean != null && bean.getType() != XMPPBean.TYPE_ERROR ){
				Log.v(TAG, "players updated: " + mGame.synchronizePlayers(bean.getPlayers()));
				
				for(PlayerInfo info : bean.getPlayers()) {
					System.out.println("playerinfo: " + info.toString());
				}
				
				// Pack information in a Message object
				Message msg = new Message();
				msg.obj = bean.getInfo();
				
				// Notify the gui handler of the update
				mUpdatePlayersHandler.sendMessage(msg);
				
				// Confirm the receiving of the PlayersBean
				mMxaProxy.getIQProxy().getProxy().Players( 
						mMxaProxy.getIQProxy().getGameServiceJid(),
						bean.getId());
			}
		}
		
		/**
		 * Handle RoundStatusBean.
		 *
		 * @param bean the XMPPBean
		 */
		private void handleRoundStatusBean(RoundStatusRequest bean){
			if( bean != null && bean.getType() != XMPPBean.TYPE_ERROR ){
				// Update all player states
				mGame.updatePlayerStates(bean.getRoundStatusInfos());
				
				// Notify the gui handler about the update
				mRoundStatusHandler.sendEmptyMessage(0);
				
				// Confirm the receiving of the update
				mMxaProxy.getIQProxy().getProxy().RoundStatus( 
						mMxaProxy.getIQProxy().getGameServiceJid(),
						bean.getId());
			}
		}
		
		/**
		 * Handle SnapshotBean.
		 *
		 * @param bean the XMPPBean
		 */
		private void handleSnapshotBean(SnapshotRequest bean){
			if( bean != null && bean.getType() != XMPPBean.TYPE_ERROR ){
				// Process the snapshot
				mGame.processSnapshot((SnapshotRequest)bean.clone());
				
				// If snapshot is from round start, we have to choose our next target
				if(bean.getIsRoundStart())
					mStartRoundHandler.sendEmptyMessage(mGame.getPlayerByJID(mMxaProxy.getXmppJid()).getLastStationId());
				// else just notify the gui handler about the current target
				else
					mTargetHandler.sendEmptyMessage(0);

				// Confirm the receiving of the update
				mMxaProxy.getIQProxy().getProxy().Snapshot( 
						mMxaProxy.getIQProxy().getGameServiceJid(),
						bean.getId());
			}
		}
		
		/**
		 * Handle StartRoundBean.
		 *
		 * @param bean the XMPPBean
		 */
		private void handleStartRoundBean(StartRoundRequest bean){
			if( bean != null && bean.getType() != XMPPBean.TYPE_ERROR ){
				// Update current round
				mGame.setCurrentRound(bean.getRound());
				// Update own amount of tickets
				mGame.getRouteManagement().setMyTickets(bean.getTickets());
				// Maybe display Mr.X on map
				mGame.setShowMrX(bean.getShowMrX());
				
				Log.v(TAG,"startRound target: " + mGame.getPlayerByJID(mMxaProxy.getXmppJid()).getCurrentTargetId());
				Log.v(TAG,"startRound last: " + mGame.getPlayerByJID(mMxaProxy.getXmppJid()).getLastStationId());
		    	
				// Update the new target and the last station for each player
		    	for(XHuntPlayer player : mGame.getGamePlayers().values()){
		    		player.setCurrentTargetToLastStation();
		    		player.setCurrentTarget(-1);	 
		    		player.setReachedTarget(false);
		    	}
		    	
		    	// If the first round starts, cancel start timer if it's still active
				if(bean.getRound() == 1)
					mCancelTimerResponseHandler.sendEmptyMessage(0);
				
				// Notify the gui handler about the round start
				mStartRoundHandler.sendEmptyMessage(mGame.getPlayerByJID(mMxaProxy.getXmppJid()).getLastStationId());

				// Confirm the receiving of the round start to server
				mMxaProxy.getIQProxy().getProxy().StartRound( 
						mMxaProxy.getIQProxy().getGameServiceJid(), 
						bean.getId());
			}
		}
    	
    }
}