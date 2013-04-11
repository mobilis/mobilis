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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.tudresden.inf.rn.mobilis.android.xhunt.Const;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.GameDetailsResponse;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.IXMPPCallback;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.GameState;
import de.tudresden.inf.rn.mobilis.android.xhunt.proxy.MXAProxy;
import de.tudresden.inf.rn.mobilis.android.xhunt.service.ServiceConnector;
import de.tudresden.inf.rn.mobilis.android.xhunt.ui.DialogGameDetails;
import de.tudresden.inf.rn.mobilis.android.xhunt.ui.DialogRemoteLoading;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.coordination.MobilisServiceDiscoveryBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.coordination.MobilisServiceInfo;

/**
 * The Class OpenGamesActivity gives an overview of all running and open games on the current 
 * Mobilis-Server.
 */
public class OpenGamesActivity extends Activity {
	
	/** Identifier for the Log outputs *. */
	public final static String TAG = "OpenGamesActivity";
	
	/** The list adapter for the list of the open games. */
	private OpenGamesListAdapter mOpenGamesListAdapter;
	
	/** The m layout inflater. */
	private LayoutInflater mLayoutInflater;
	
	/** The ServiceConnector to connect to XHuntService. */
	private ServiceConnector mServiceConnector;
	
	/** The MXAProxy. */
	private MXAProxy mMxaProxy;
	
	/** Dialog that displays if client is waiting for server acks. */
	private DialogRemoteLoading mRemoteLoadingDialog;
	
	/** Dialog that shows some details of an open game. */
	private DialogGameDetails mDialogGameDetails;
	
	/** Is used, if Mobilis-Server supports XHunt-Service. */
	private static final int CODE_SERVICE_GAMES_AVAILABLE = 1;
	
	/** Is used, if Mobilis-Server doesn't supports XHunt-Service. */
	private static final int CODE_SERVICE_NO_GAMES_AVAILABLE = 0;
	
	/** Is used, if contacting the Mobilis-Server fails. */
	private static final int CODE_SERVICE_GAMES_FAILURE = -1;
	
    /** The handler for response of the MobilisServiceDiscoveryBean. */
    private Handler mDiscoverGamesHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(mRemoteLoadingDialog != null){
				mRemoteLoadingDialog.cancel();
			}
			
			// Updates list of the open games
			mOpenGamesListAdapter.notifyDataSetChanged();
			switch(msg.what){
				case CODE_SERVICE_GAMES_AVAILABLE:					
					break;
				case CODE_SERVICE_NO_GAMES_AVAILABLE:
					Toast.makeText(OpenGamesActivity.this,
							"There are no games available right now!", Toast.LENGTH_LONG).show();
					break;
				case CODE_SERVICE_GAMES_FAILURE:
					Toast.makeText(OpenGamesActivity.this,
							"Failed to load open games!", Toast.LENGTH_LONG).show();
					break;
			}
		}
	};
	
    /** The handler for displaying game details in Dialog or a message if fails to request this details. */
    private Handler mShowDialogHandler = new Handler(){
	    @Override
		public void handleMessage(Message msg) {
			if(mRemoteLoadingDialog != null){
				mRemoteLoadingDialog.cancel();
			}
			
	    	switch(msg.what){
		    	case -1:
		    		Toast.makeText(OpenGamesActivity.this,
		    				"Failed to load game details: " + msg.obj.toString(), Toast.LENGTH_LONG).show();
		    		break;
		    	case DialogGameDetails.DIALOG_ID:
		    		if(mDialogGameDetails.hasContent())
		    			mDialogGameDetails.show();
		    		else
		    			Toast.makeText(OpenGamesActivity.this, "No game data available!", Toast.LENGTH_LONG).show();
		    			
		    		break;
	    	}
	    }
    };
	
    /** The handler which is called if the XHuntService was bound. */
    private Handler mXHuntServiceBoundHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mServiceConnector.getXHuntService().setGameState(new GameStateOpengames());
			mMxaProxy = mServiceConnector.getXHuntService().getMXAProxy();
			
			mRemoteLoadingDialog = new DialogRemoteLoading(OpenGamesActivity.this, Const.CONNECTION_TIMEOUT_DELAY);
			
			discoverOpenGames();
		}
	};
	
	
	/**
	 * Bind XHuntService using the mXHuntServiceBoundHandler.
	 */
	private void bindXHuntService(){
    	mServiceConnector = new ServiceConnector(this);
    	mServiceConnector.doBindXHuntService(mXHuntServiceBoundHandler);
	}
    
    /**
     * Discover open games.
     */
    private void discoverOpenGames(){
    	// Start RemoteLoadingDialog while fetching the data
		mRemoteLoadingDialog.setLoadingText("Requesting open games.\n\n     Please wait...");
		mRemoteLoadingDialog.run();
		
		// Clear game list 
		if(mOpenGamesListAdapter != null)
			mOpenGamesListAdapter.List.clear();
    	
		// Send a ServiceDiscovery to the Mobilis-Server an asking for all running XHunt-Services
    	mMxaProxy.getIQProxy().sendServiceDiscoveryIQ("http://mobilis.inf.tu-dresden.de#services/MobilisXHuntService");
    }
	
	/* (non-Javadoc)
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		if(mRemoteLoadingDialog != null)
			mRemoteLoadingDialog.cancel();
		
		mServiceConnector.doUnbindXHuntService();
		
		super.finish();
	}
	
	/**
	 * Initialize all UI elements from resources.
	 */
    private void initComponents(){
    	mOpenGamesListAdapter = new OpenGamesListAdapter();    	
    	
    	ListView lv_Games = (ListView)findViewById(R.id.opengames_list);
    	lv_Games.setEmptyView(findViewById(R.id.opengames_list_empty));
    	lv_Games.setAdapter(mOpenGamesListAdapter);
    	
    	lv_Games.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				// If user click on a game in the list, load the game details
				OpenGameItem openGameItem = mOpenGamesListAdapter.getItem(position);
				Log.v(TAG, "itemId: " + openGameItem.GameId);
				
				showGameDialog(openGameItem);
			}
    		
		});
    	
    	Button btn_CreateGame = (Button)findViewById(R.id.opengames_btn_newgame);
    	btn_CreateGame.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(OpenGamesActivity.this, CreateGameActivity.class));
			}
		});
    	
    	Button btn_ReloadGames = (Button)findViewById(R.id.opengames_btn_reloadgames);
    	btn_ReloadGames.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				discoverOpenGames();
			}
		});
    	
		mDialogGameDetails = new DialogGameDetails(OpenGamesActivity.this,
				getWindowManager().getDefaultDisplay());
    }
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opengames);
		
		// For dynamic layout of the games list
		mLayoutInflater = getLayoutInflater();
		
		bindXHuntService();
		initComponents();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// Load open games from server if this activity is build first. It also change the current 
		// state of the game to GameStateOpengames to handle different Beans
		if(mMxaProxy != null){
			mServiceConnector.getXHuntService().setGameState(new GameStateOpengames());
			mRemoteLoadingDialog = new DialogRemoteLoading(OpenGamesActivity.this, Const.CONNECTION_TIMEOUT_DELAY);

			discoverOpenGames();
		}
		
		super.onResume();
	}
    
    /**
     * Displays a dialog with further information about a game.
     *
     * @param openGameItem the OpenGameItem which should be displayed
     */
    private void showGameDialog(final OpenGameItem openGameItem){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Game: " + openGameItem.Name);
    	builder.setItems(R.array.opengame_options, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {    	        
    	        switch(item){
    	        case 0:
    	        	mMxaProxy.getIQProxy().setGameServiceJid(openGameItem.Jid);
    	        	mMxaProxy.getIQProxy().setGameName(openGameItem.Name);
    	        	
    	        	startActivity(new Intent(OpenGamesActivity.this, LobbyActivity.class));
    	        	mMxaProxy.getIQProxy().setServiceVersion( openGameItem.ServiceVersion );
    	        	break;
    	        case 1:
    	        	Toast.makeText(getApplicationContext(),"Not possible right now!",
    	        			Toast.LENGTH_SHORT).show();
    	        	break;
    	        case 2:
    	    		mRemoteLoadingDialog.setLoadingText("Requesting game details.\n\n     Please wait...");
    	    		mRemoteLoadingDialog.run();
    	    		
    	    		// Requesting detailed information for a selected game from server 
    	    		// using the JID of the game
    	        	mMxaProxy.getIQProxy().getProxy().GameDetails( openGameItem.Jid, _gameDetailsCallback );
    	        	  	        	
    	        	break;
    	        }
    	    }

    	});
    	AlertDialog alert = builder.create();
    	
    	alert.show();
    }
    
    private IXMPPCallback< GameDetailsResponse > _gameDetailsCallback = new IXMPPCallback< GameDetailsResponse >() {
		
		@Override
		public void invoke( GameDetailsResponse bean ) {
			mDialogGameDetails.clearContent();
			mDialogGameDetails.addDetails(bean.getGameName(), bean.getRequirePassword(),
					bean.getCountRounds(), bean.getStartTimer(), bean.getPlayerNames());
			
			mShowDialogHandler.sendEmptyMessage(DialogGameDetails.DIALOG_ID);
		}
	};
    
    
    /**
     * The Class OpenGamesListAdapter used to display and manage the list of games.
     */
    private class OpenGamesListAdapter extends BaseAdapter {
    	
    	/** The List of games. */
	    public List<OpenGameItem> List;
    	
    	/**
	     * Instantiates a new OpenGamesListAdapter.
	     */
	    public OpenGamesListAdapter() {
			this.List = new ArrayList<OpenGamesActivity.OpenGameItem>();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		public int getCount() {
			return List.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		public OpenGameItem getItem(int position) {
			return List.get(position);
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		public long getItemId(int position) {
			return List.size() > 0
				? List.get(position).GameId
				: 0;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
	        View view = null;

	        if(convertView != null){
	        	view = (LinearLayout)convertView;
	        }
	        
	        else if(convertView == null) {
	        	// Use a custom layout inside the listelement
	        	view = mLayoutInflater.inflate(R.layout.listitem_opengames, null);
	        }

	        ImageView img_image = (ImageView)view.findViewById(R.id.listitem_opengames_image);
        	img_image.setBackgroundResource(List.get(position).DrawableId);
        	view.setTag(img_image);
        	
        	TextView tv_name = (TextView)view.findViewById(R.id.listitem_opengames_name);
        	tv_name.setText(List.get(position).Name != null
        			? List.get(position).Name
        			: List.get(position).Jid);
        	view.setTag(tv_name);
        	
        	TextView tv_openGames = (TextView)view.findViewById(R.id.listitem_opengames_players);
        	tv_openGames.setText(List.get(position).Players);	        	
        	view.setTag(tv_openGames);
	        
	        return view;
		}
    }
    
	
	//TODO: Maybe ask all open games for amount of players?
    /**
	 * The Class GameStateOpengames represents a state of the game.
	 */
	private class GameStateOpengames extends GameState {

		/* (non-Javadoc)
		 * @see de.tudresden.inf.rn.mobilis.android.xhunt.model.GameState#processPacket(de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean)
		 */
		@Override
		public void processPacket(XMPPBean inBean) {
			if(inBean.getType() == XMPPBean.TYPE_ERROR){
				Log.e(TAG, "IQ Type ERROR: " + inBean.toXML());
			}
			
			// Handle MobilisServiceDiscoveryBean and the containing services
			if( inBean instanceof MobilisServiceDiscoveryBean){
				MobilisServiceDiscoveryBean bean = (MobilisServiceDiscoveryBean)inBean;

				if(inBean.getType() == XMPPBean.TYPE_ERROR){
					Log.e(TAG, "IQ Type ERROR: " + inBean.toXML());
				}
				
				else {				
					if(bean != null && bean.getType() != XMPPBean.TYPE_ERROR ){
						if( bean.getDiscoveredServices() != null
								&& bean.getDiscoveredServices().size() > 0 ){
							
							// check if ServiceDiscoveryBean contains game instances or just admin-/coordinator-/etc services
							List<MobilisServiceInfo> gameInstances = new ArrayList<MobilisServiceInfo>();
							for(MobilisServiceInfo info : bean.getDiscoveredServices()) {
								if((info.getJid() != null)
										&& (info.getJid().toLowerCase().contains("xhunt"))
										&& (!info.getJid().toLowerCase().contains("coordinator"))
										&& (!info.getJid().toLowerCase().contains("admin"))
										&& (!info.getJid().toLowerCase().contains("deployment"))) {
									gameInstances.add(info);
								}
							}
							
							// if ServiceDiscoveryBean contained game instances, refresh list
							if(gameInstances.size() > 0) {
								mOpenGamesListAdapter.List.clear();
								for(MobilisServiceInfo info : gameInstances) {
									mOpenGamesListAdapter.List.add(new OpenGameItem(info.hashCode(), R.drawable.ic_game,
											info.getJid(), Integer.parseInt(info.getVersion()), info.getServiceName(), ""));
								}
								
								mDiscoverGamesHandler.sendEmptyMessage(CODE_SERVICE_GAMES_AVAILABLE);
							}
							
							else {
								mDiscoverGamesHandler.sendEmptyMessage(CODE_SERVICE_NO_GAMES_AVAILABLE);
							}

						} else {
							mDiscoverGamesHandler.sendEmptyMessage(CODE_SERVICE_NO_GAMES_AVAILABLE);
						}
					}
				}
			}
			// Other Beans of type get or set will be responded with an ERROR
			else if(inBean.getType() == XMPPBean.TYPE_GET || inBean.getType() == XMPPBean.TYPE_SET) {
				inBean.errorType = "wait";
				inBean.errorCondition = "unexpected-request";
				inBean.errorText = "This request is not supportet at this game state(opengames)";
				
				mMxaProxy.getIQProxy().sendXMPPBeanError(inBean);
			}		
		}
    	
    }
    
    
    /**
     * The Class OpenGameItem is used to handle complex structure in list adapter for games.
     */
    private class OpenGameItem {
    	
    	/** The Game id. */
	    public long GameId;
    	
	    /** The Drawable id. */
	    public int DrawableId;
    	
	    /** The Name. */
	    public String Name;
    	
	    /** The Jid. */
	    public String Jid;
	    
	    public int ServiceVersion;
    	
	    /** The Players as string. */
	    public String Players;
    	
    	/**
	     * Instantiates a new OpenGameItem.
	     *
	     * @param gameId the id of the game
	     * @param drawableId the drawable id for the icon of the listelement
	     * @param jid the jid of the game service
	     * @param name the name of the game
	     * @param players the players which are currently in the game
	     */
	    public OpenGameItem(long gameId, int drawableId, String jid, int serviceVersion, String name, String players) {
    		this.GameId = gameId;
			this.DrawableId = drawableId;
			this.Jid = jid;
			this.ServiceVersion = serviceVersion;
			this.Name = name;
			this.Players = players;
		}
    }

}
