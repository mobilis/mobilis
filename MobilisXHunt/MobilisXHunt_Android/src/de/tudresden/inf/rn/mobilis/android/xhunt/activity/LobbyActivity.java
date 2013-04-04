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
package de.tudresden.inf.rn.mobilis.android.xhunt.activity;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import de.tudresden.inf.rn.mobilis.android.xhunt.Const;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.GameOverRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.IXMPPCallback;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.JoinGameResponse;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.LocationInfo;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.LocationRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.PlayerExitResponse;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.PlayerInfo;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.PlayersRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.StartRoundRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.UpdatePlayerResponse;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.UpdateTicketsRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Game;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.GameState;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.XHuntPlayer;
import de.tudresden.inf.rn.mobilis.android.xhunt.proxy.MXAProxy;
import de.tudresden.inf.rn.mobilis.android.xhunt.service.ServiceConnector;
import de.tudresden.inf.rn.mobilis.android.xhunt.ui.DialogPlayerInfo;
import de.tudresden.inf.rn.mobilis.android.xhunt.ui.DialogRemoteLoading;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.MessageItems;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IFileAcceptCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IFileCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.parcelable.ByteStream;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

// TODO: The change of player icons are difficult to see, because they just changing the 
// opacity. They have to be replaced by new icons.
// TODO: default ticket icon is required if transmitting of icons failed
/**
 * The Class LobbyActivity is the place where all players meet to play the game
 * after they joining the game.
 */
public class LobbyActivity extends Activity {

	/** Identifier for the Log outputs. */
	private static final String TAG = "LobbyActivity";

	/** Represents the TableLayout of the Lobby. */
	private TableLayout tbl_lobby;

	/** For caching the selected context item id. */
	private String selectedPlayerJid;

	/** The MXAProxy. */
	private MXAProxy mMxaProxy;

	/** The game instance for game logic. */
	private Game mGame;

	/** The ServiceConnector to connect to XHuntService. */
	private ServiceConnector mServiceConnector;

	/** Dialog that displays if client is waiting for server acks. */
	private DialogRemoteLoading mRemoteLoadingDialog;

	/** The request code for FileTransfers to identify them. */
	private int mTransferRequestCode = 0;

	/** The incoming files(requestCode, filePath) like map an pictures. */
	private HashMap<Integer, String> mIncomingFiles = new HashMap<Integer, String>();

	/** The dialog that will be used for filetransmission. */
	private DialogRemoteLoading mDialogIncomingFiles;

	/**
	 * The list with announced incoming files to verify that all files were
	 * transmitted.
	 */
	private List<String> mAnnouncedIncomingFiles = new ArrayList<String>();

	/** The dummy handler for non action if a handler is called. */
	private Handler mDummyHandler = new Handler();

	/** The dummy messenger for existing files in FileTransfer. */
	private Messenger mDummyMessenger = new Messenger(mDummyHandler);

	private DialogPlayerInfo mDialogPlayerInfo;

	// views
	private Button mBtnInvite;

	/** The handler which handles PlayerExitBeans. */
	private Handler mExitGameHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mRemoteLoadingDialog != null) {
				mRemoteLoadingDialog.cancel();
			}
			// If player leaves game, finish Lobby
			if (msg.what == 0) {
				LobbyActivity.this.finish();
			}
			// If player was kicked by moderator, notify the player and finish
			// the lobby
			if (msg.what == 1) {
				AlertDialog.Builder ad = new AlertDialog.Builder(
						LobbyActivity.this);

				ad.setTitle("Exit Game.");
				ad.setMessage("Sorry, but the Moderator has kicked you!")
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										LobbyActivity.this.finish();
									}
								});
				ad.setIcon(R.drawable.ic_warning_48);

				ad.show();
			}
			// If this action was invalid or fails, notify the player
			else if (msg.what == -1 && msg.obj != null) {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
						LobbyActivity.this);

				alertBuilder.setTitle("Removing player failed.");
				alertBuilder.setMessage(msg.obj.toString());
				alertBuilder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
				alertBuilder.setIcon(R.drawable.ic_error_48);

				alertBuilder.show();
			}
		}
	};

	/** The handler for GameOverBeans notifies the player and finish the lobby. */
	private Handler mGameOverHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (msg.obj != null) {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
						LobbyActivity.this);

				alertBuilder.setTitle("Game Over.");
				alertBuilder.setMessage(msg.obj.toString());
				alertBuilder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								LobbyActivity.this.finish();
							}
						});
				alertBuilder.setIcon(R.drawable.ic_warning_48);

				alertBuilder.show();
			}
		}
	};

	/**
	 * The handler for exiting incoming files. This will skip FileTransfer for
	 * this file to reduce bandwidth.
	 */
	private Handler mIncomingFileExistsHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			handleDeliveredFile(msg.arg2);
		}
	};

	/** The handler for incoming files via FileTransfer. */
	private Handler mIncomingFileHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == ConstMXA.MSG_SEND_FILE) {
				Bundle extras = msg.getData();

				switch (msg.arg1) {
				case ConstMXA.MSG_STATUS_ERROR:
					String data = "errorCode: " + extras.getInt("ERRORCODE", 0);
					data += " errorMsg: " + extras.getString("ERRORMESSAGE");

					Log.e(TAG, "ERROR while receiving file: [id: " + msg.arg2
							+ " path: " + mIncomingFiles.get(msg.arg2)
							+ "] data: " + data);

					break;
				case ConstMXA.MSG_STATUS_DELIVERED:
					handleDeliveredFile(msg.arg2);

					break;
				}
			} else
				Log.e("Main", "fileHandler triggered with unknown identifier: "
						+ msg.what);
		}
	};

	/** The messenger for incoming files to get the packets. */
	private Messenger mIncomingFileMessenger = new Messenger(mIncomingFileHandler);

	/** The handler for JoinGameBeans. */
	private Handler mJoinGameHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mRemoteLoadingDialog != null) {
				mRemoteLoadingDialog.cancel();
			}

			// Store information about the incoming files which will be
			// transmitted
			// after this bean has arrived and start progress dialog
			/*
			 * Commented out because although the server could still send files, they are not used anymore.
			 * Instead they are integrated in the /res folder.
			 * 
			 * mDialogIncomingFiles.setMaxValue(mAnnouncedIncomingFiles.size());
			 * mDialogIncomingFiles.run();
			 */

			// If joining game was successful, enter chatroom and register popup
			// for incoming
			// chatmessages
			if (msg.what == 0) {
				try {
					mMxaProxy.connectToMUC(mGame.getChatID(),
							mGame.getChatPassword());
				} catch (RemoteException e) {
					Log.e(TAG, "ERROR while connecting to MUC");
					Toast.makeText(LobbyActivity.this,
							"ERROR while connecting to MUC", Toast.LENGTH_LONG)
							.show();
				}

				mMxaProxy.registerIncomingMessageObserver(LobbyActivity.this,
						mMucHandler, mGame.getChatID());
			}
			// If joining failed, notify user
			else if (msg.obj != null) {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
						LobbyActivity.this);

				alertBuilder.setTitle("Joining Game Failed");
				alertBuilder.setMessage(msg.obj.toString());
				alertBuilder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								LobbyActivity.this.finish();
							}
						});
				alertBuilder.setIcon(R.drawable.ic_error_48);

				alertBuilder.show();
			}
		}
	};

	/**
	 * The handler which will be called if joining a game times out. This
	 * handler will notify the user and offer to retry or to exit.
	 */
	private Handler mJoinGameTimeOutHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mRemoteLoadingDialog != null) {
				mRemoteLoadingDialog.cancel();
			}

			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
					LobbyActivity.this);

			alertBuilder.setTitle("Joining Game Failed");
			alertBuilder
					.setMessage("Timeout while requesting confirmation of joining game.");
			alertBuilder.setPositiveButton("Retry",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							unregisterXMPPCallbacks();
							registerXMPPCallbacks();
							joinGame();
						}
					});
			alertBuilder.setNegativeButton("Quit",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							LobbyActivity.this.finish();
						}
					});
			alertBuilder.setIcon(R.drawable.ic_error_48);

			alertBuilder.show();
		}
	};

	/**
	 * The handler for chatmessages. This messages will be displayed using a
	 * Toast and the device will vibrate.
	 */
	private Handler mMucHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
				Toast toast = Toast.makeText(LobbyActivity.this,
						msg.obj.toString(), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 100);
				toast.show();

				mServiceConnector.getXHuntService().getTools()
						.vibrateOnChatMessage();
			}
		}
	};

	/** The handler for StartRoundBean which is starting the XHuntMapActivity. */
	private Handler mStartInitialRoundHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Intent i = new Intent(LobbyActivity.this, XHuntMapActivity.class);
			i.putExtra(Const.BUNDLE_KEY_STARTROUNDID, msg.obj.toString());
			startActivityForResult(i, Const.MAP_ACTIVITY_ID);

			LobbyActivity.this.finish();
		}
	};

	/**
	 * The handler to update the players view if something changed like new
	 * player joined the game.
	 */
	private Handler mUpdatePlayersHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// Remove all player views
			tbl_lobby.removeAllViews();

			// For each player insert a row in the players list
			for (XHuntPlayer player : mGame.getGamePlayers().values()) {
				Log.v(TAG, "Player: " + player.toString());
				insertNewPlayerRow(player);
			}

			// Display a message shipped by the Bean
			if (msg.obj != null) {
				Toast.makeText(LobbyActivity.this, msg.obj.toString(),
						Toast.LENGTH_LONG).show();
			}
		}
	};

	/** The handler for UpdatePlayerBeans. */
	private Handler mUpdatePlayerStatusHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mRemoteLoadingDialog != null) {
				mRemoteLoadingDialog.cancel();
			}

			if (msg.obj != null) {
				if (msg.what == -1) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
							LobbyActivity.this);

					alertBuilder.setTitle("Update player failed");
					alertBuilder.setMessage(msg.obj.toString());
					alertBuilder.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});
					alertBuilder.setIcon(R.drawable.ic_error_48);

					alertBuilder.show();
				} else {
					Toast.makeText(LobbyActivity.this, msg.obj.toString(),
							Toast.LENGTH_LONG).show();
				}
			}
		}
	};

	/** The handler which is called if the XHuntService was bound. */
	private Handler mXHuntServiceBoundHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mServiceConnector.getXHuntService().setGameState(
					new GameStateLobby());
			mMxaProxy = mServiceConnector.getXHuntService().getMXAProxy();
			mGame = mServiceConnector.getXHuntService().createNewGame();
			
			/*
			 * parseDataXML() was formerly called in handleDeliveredFile(), now moved to mXHuntServiceBoundHandler because
			 * the area.xml now is integrated in res/raw/ instead of being sent by the server
			 */
			Reader reader = new InputStreamReader(getResources().openRawResource(R.raw.area_1_v1));
			mGame.getRouteManagement().parseDataXML(reader);

			registerXMPPCallbacks();
			mServiceConnector.getXHuntService().getGPSProxy().startGps();
			joinGame();
		}
	};

	/**
	 * Bind XHuntService using the mXHuntServiceBoundHandler.
	 */
	private void bindXHuntService() {
		mServiceConnector = new ServiceConnector(this);
		mServiceConnector.doBindXHuntService(mXHuntServiceBoundHandler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		if (mRemoteLoadingDialog != null)
			mRemoteLoadingDialog.cancel();

		if (mDialogIncomingFiles != null)
			mDialogIncomingFiles.cancel();

		unregisterXMPPCallbacks();
		mServiceConnector.getXHuntService().getGPSProxy().stopGps();
		mServiceConnector.doUnbindXHuntService();

		super.finish();
	}

	/**
	 * Handle transmitted file by server.
	 * 
	 * @param requestCode
	 *            the request code of the transmitted file
	 */
	private void handleDeliveredFile(int requestCode) {
		// If file contains the area information, then parse information and
		// store it in the Game class.
		/*
		 * Not needed anymore because the area.xml was integrated in res/raw/.
		 * I'll keep the code here just in case someone wants to change it back.
		 * parseDataXML() is called in onCreate() now.
		 * 
		 * if (mIncomingFiles.get(requestCode) != null
				&& mIncomingFiles.get(requestCode).contains("area")) {
			mGame.getRouteManagement().parseDataXML(mIncomingFiles.get(requestCode));
		}*/

		// Increment progress of dialog by 1 and if transmission is complete,
		// then dismiss the dialog.
		if (mDialogIncomingFiles != null) {
			mDialogIncomingFiles.incrementCurrentValueBy(1);

			if (mDialogIncomingFiles.isLoadingComplete())
				mDialogIncomingFiles.cancel();
		}
	}

	/**
	 * Initialize all UI elements from resources.
	 */
	private void initComponents() {
		mRemoteLoadingDialog = new DialogRemoteLoading(this,
				Const.CONNECTION_TIMEOUT_DELAY);

		mDialogIncomingFiles = new DialogRemoteLoading(this,
				Const.CONNECTION_TIMEOUT_DELAY * 5);
		mDialogIncomingFiles.setLoadingText("Loading Game Files...");
		mDialogIncomingFiles.setStyleHorizontal();

		mDialogPlayerInfo = new DialogPlayerInfo(LobbyActivity.this,
				getWindowManager().getDefaultDisplay());

		// Get the TableLayout of the Lobby
		tbl_lobby = (TableLayout) findViewById(R.id.tbl_lobby);

		// Get the Ready-Button of the Lobby
		Button btn_ready = (Button) findViewById(R.id.btn_go);
		btn_ready.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				final XHuntPlayer player = mGame.getPlayerByJID(mMxaProxy
						.getXmppJid());

				if (player != null)
					startGame(player);
			}
		});

		mBtnInvite = (Button) findViewById(R.id.lobby_btn_invite);
		mBtnInvite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startScanActivity();
			}
		});
	}

	private void startScanActivity() {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
	}

	/**
	 * Insert a new player row in the list of players.
	 * 
	 * @param player
	 *            the player to be insert
	 * 
	 * @return true, if successful
	 */
	private boolean insertNewPlayerRow(XHuntPlayer player) {
		ImageView icon = new ImageView(LobbyActivity.this);

		// If player is ready, use normal icon, else set an opacity
		icon.setImageResource(player.getPlayerIconID());
		if (!player.isReady())
			icon.setAlpha(150);
		icon.setPadding(3, 0, 0, 10);

		// Define name of player
		TextView tv_player = new TextView(LobbyActivity.this);
		tv_player.setText(player.getName());
		tv_player.setTextSize(20);
		tv_player.setTypeface(Typeface.DEFAULT_BOLD);
		tv_player.setPadding(3, 0, 0, 10);

		// Define role of player (Mr.X or agent)
		TextView tv_role = new TextView(LobbyActivity.this);
		tv_role.setText(player.getPlayerRoleToString());
		tv_role.setGravity(Gravity.RIGHT);
		tv_role.setPadding(3, 0, 0, 10);

		// TableRow which contains the data of the player
		TableRow tr = new TableRow(LobbyActivity.this);
		tr.setTag(player.getJid());
		tr.addView(icon);
		tr.addView(tv_player);
		tr.addView(tv_role);
		registerForContextMenu(tr);

		tbl_lobby.addView(tr);

		return true;
	}

	/**
	 * Join game while sending an JoinGameBean to the server.
	 */
	private void joinGame() {
		mRemoteLoadingDialog
				.setLoadingText("Joining game.\n\n     Please wait...");
		mRemoteLoadingDialog.setTimeOutHandler(mJoinGameTimeOutHandler);
		mRemoteLoadingDialog.run();

		mMxaProxy
				.getIQProxy()
				.getProxy()
				.JoinGame(mMxaProxy.getIQProxy().getGameServiceJid(), null,
						mMxaProxy.getNickname(), false, _joinGameCallback);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, data);
		if (scanResult != null) {
			// send invite request to scanned jid
			// TODO: check if it is a JID
			mMxaProxy.sendSessionInvitation(scanResult.getContents());

			Toast.makeText(LobbyActivity.this, "Invitation sent",
					Toast.LENGTH_LONG).show();
		} else if (requestCode == Const.MAP_ACTIVITY_ID) {
			this.finish();
		}
	}

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_lobby);

		initComponents();
		bindXHuntService();
		
		// delete old chat messages from internal database
		int cntMsgsDeleted = getContentResolver().delete(
				MessageItems.CONTENT_URI,
				null,
				null);
		Log.i(TAG, cntMsgsDeleted + " old chat messages deleted");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
	 * android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		XHuntPlayer player = mGame.getPlayerByJID(mMxaProxy.getXmppJid());

		Log.v(TAG, "serviceVersion: "
				+ mMxaProxy.getIQProxy().getServiceVersion());

		MenuInflater inflater;
		// Just the moderator can have a contextmenu
		if (player != null && player.isModerator()) {
			inflater = getMenuInflater();

			if (mMxaProxy.getIQProxy().getServiceVersion() > 2) {
				inflater.inflate(R.menu.context_lobby_ttmrx, menu);
			} else {
				inflater.inflate(R.menu.context_lobby, menu);
			}

			selectedPlayerJid = v.getTag().toString();
		} else if (mMxaProxy.getIQProxy().getServiceVersion() > 2) {
			inflater = getMenuInflater();
			inflater.inflate(R.menu.context_lobby_ttagent, menu);
		}
	}

	/**
	 * Overrides the onCreateOptionsmenu.
	 * 
	 * @param menu
	 *            the menu
	 * @return true, if successful
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.lobby, menu);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	public boolean onContextItemSelected(MenuItem item) {
		boolean result = false;

		final XHuntPlayer player = mGame.getPlayerByJID(selectedPlayerJid);

		// Add the option to kick users or change the status of Mr.X
		switch (item.getItemId()) {
		case R.id.menu_context_lobby_kick:
			// Moderator can not kick himself
			if (!player.getJid().equals(mMxaProxy.getXmppJid())) {
				mMxaProxy
						.getIQProxy()
						.getProxy()
						.PlayerExit(mMxaProxy.getIQProxy().getGameServiceJid(),
								player.getJid(), false, _kickPlayerCallback);
				result = true;
			} else {
				Toast.makeText(LobbyActivity.this, "Nice try :-)",
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.menu_context_lobby_tomrx:
			if (!player.isMrX()) {
				mMxaProxy
						.getIQProxy()
						.getProxy()
						.UpdatePlayer(
								mMxaProxy.getIQProxy().getGameServiceJid(),
								new PlayerInfo(player.getJid(), player
										.getName(), player.isModerator(), true,
										player.isReady(),
										player.getPlayerIconID()),
								_updatePlayerCallback);
			} else {
				Toast.makeText(LobbyActivity.this, "You're already Mr.X",
						Toast.LENGTH_LONG).show();
			}

			result = true;
			break;
		/* commented out since tickets became hidden
		case R.id.menu_context_lobby_transferTicket:
			if (selectedPlayerJid.equals(mMxaProxy.getXmppJid())) {
				Toast.makeText(getApplicationContext(),
						"Choose another player to transfer ticket!",
						Toast.LENGTH_SHORT).show();

				return true;
			}

			List<String> myTicketNames = new ArrayList<String>();

			for (Map.Entry<Integer, Integer> myTickets : mGame
					.getRouteManagement().getMyTickets().entrySet()) {
				if (myTickets.getValue() > 0) {
					myTicketNames
							.add(mGame.getRouteManagement().getAreaTickets()
									.get(myTickets.getKey()).getName());
				}
			}

			final CharSequence[] items = myTicketNames
					.toArray(new CharSequence[myTicketNames.size()]);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Choose Type");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, final int item) {
					try {
						Log.v(TAG, "item: " + items[item]);
						final DialogInput inDialog = new DialogInput(
								LobbyActivity.this, "Amount");

						inDialog.setPositiveButton("Transfer",
								new Dialog.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (inDialog.getInputText().length() > 0) {

											try {
												Integer transferAmount = Integer.parseInt(inDialog
														.getInputText());
												int selectedTicketId = -1;

												for (Map.Entry<Integer, Ticket> ticket : mGame
														.getRouteManagement()
														.getAreaTickets()
														.entrySet()) {
													if (items[item]
															.toString()
															.toLowerCase()
															.equals(ticket
																	.getValue()
																	.getName()
																	.toLowerCase())) {
														selectedTicketId = ticket
																.getKey();
														break;
													}
												}

												if (selectedTicketId > 0
														&& mGame.getRouteManagement()
																.getMyTickets()
																.get(selectedTicketId) >= transferAmount) {

													mMxaProxy
															.getIQProxy()
															.getProxy()
															.TransferTicket(
																	mMxaProxy
																			.getIQProxy()
																			.getGameServiceJid(),
																	mMxaProxy
																			.getXmppJid(),
																	player.getJid(),
																	new TicketAmount(
																			selectedTicketId,
																			transferAmount),
																	new IXMPPCallback<TransferTicketResponse>() {

																		@Override
																		public void invoke(
																				TransferTicketResponse xmppBean) {
																		}
																	});

													Toast.makeText(
															LobbyActivity.this,
															"Transfered "
																	+ inDialog
																			.getInputText()
																	+ " tickets of type "
																	+ mGame.getRouteManagement()
																			.getAreaTickets()
																			.get(selectedTicketId)
																			.getName(),
															Toast.LENGTH_SHORT)
															.show();
												} else {
													Toast.makeText(
															LobbyActivity.this,
															"Not enough tickets available!",
															Toast.LENGTH_SHORT)
															.show();
												}

											} catch (Exception e) {
												Toast.makeText(
														LobbyActivity.this,
														"Unsupported symbols!",
														Toast.LENGTH_SHORT)
														.show();
											}
										}
									}
								});

						inDialog.show();
					} catch (Exception e) {
						Toast.makeText(LobbyActivity.this,
								"Sorry, error happens: " + e.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				}
			});

			builder.show();

			break;
		 */
		}

		if (result) {
			mUpdatePlayersHandler.sendEmptyMessage(0);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onContextMenuClosed(android.view.Menu)
	 */
	public void onContextMenuClosed(Menu menu) {
		selectedPlayerJid = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// If the back button of the device was pressed, show an exit dialog
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			showExitDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Fires when a Button is clicked.
	 * 
	 * @param item
	 *            the clicked item
	 * 
	 * @return true, if on options item selected
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		// Switch the item and find out the specific item-object.
		switch (item.getItemId()) {
		case R.id.menu_lobby_muc:

			if (mMxaProxy.isMUCConnected()) {
				// Creates a new Intent to start a new MUCActivity
				Intent i = new Intent(LobbyActivity.this, MUCActivity.class);
				startActivity(i);
			} else {
				AlertDialog.Builder ad = new AlertDialog.Builder(this);

				ad.setMessage("Sorry, but the chat is currently not available!")
						.setNeutralButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});

				ad.show();
			}

			return true;
		case R.id.menu_lobby_info:
			mDialogPlayerInfo.clearContent();
			XHuntPlayer player = mGame.getPlayerByJID(mMxaProxy.getXmppJid());

			if (null == player)
				return false;

			Map<Bitmap, Integer> tickets = new HashMap<Bitmap, Integer>();

			Log.v(TAG, "mytickets: "
					+ mGame.getRouteManagement().getMyTickets().size());

			// Get the icons of the tickets and add them to the used ticket list
			for (Map.Entry<Integer, Integer> entity : mGame.getRouteManagement().getMyTickets().entrySet()) {
				tickets.put(mGame.getRouteManagement().getAreaTickets().get(entity.getKey()).getIcon(getResources()),
						entity.getValue());
			}

			mDialogPlayerInfo.addPlayer(player.getPlayerIconID(),
					player.getName(), tickets);

			mDialogPlayerInfo.show();

			return true;
		case R.id.menu_lobby_quit:
			showExitDialog();
			return true;
		}
		return false;
	}

	/**
	 * Register the XMPP FileTransfer callback.
	 */
	private void registerXMPPCallbacks() {
		if (mMxaProxy != null) {
			try {
				mMxaProxy.registerFileCallback(FileReceiveCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "MXAProxy is null");
		}
	}

	/**
	 * Show exit dialog. The user has to confirm the exit.
	 */
	private void showExitDialog() {
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setTitle("Leave Game?");
		ad.setMessage("So you really want to leave this game?");
		ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			// If user wants to leave the game, the server will be notified with
			// a PlayerExitBean.
			public void onClick(DialogInterface dialog, int id) {
				mRemoteLoadingDialog
						.setLoadingText("Exit game and notify game server.\n\n     Please wait...");
				mRemoteLoadingDialog.setTimeOutHandler(mExitGameHandler);
				mRemoteLoadingDialog.run();

				mMxaProxy
						.getIQProxy()
						.getProxy()
						.PlayerExit(mMxaProxy.getIQProxy().getGameServiceJid(),
								mMxaProxy.getXmppJid(), false,
								_playerExitCallback);
			}
		});
		ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		ad.show();
	}

	/**
	 * Start game and send an UpdatePlayerIQ to the server.
	 * 
	 * @param player
	 *            the player which will start the game which should be the own player            
	 */
	private void startGame(XHuntPlayer player) {
		mRemoteLoadingDialog
				.setLoadingText("Starting game.\n\n     Please wait...");
		mRemoteLoadingDialog.run();

		player.setReady(true);

		mMxaProxy
				.getIQProxy()
				.getProxy()
				.UpdatePlayer(
						mMxaProxy.getIQProxy().getGameServiceJid(),
						new PlayerInfo(player.getJid(),
								player.getName(),
								player.isModerator(),
								player.isMrX(),
								player.isReady(),
								player.getPlayerIconID()),
						_updatePlayerCallback);
	}

	/**
	 * Unregister the XMPP FileTransfer callback.
	 */
	private void unregisterXMPPCallbacks() {
		if (mMxaProxy != null) {
			try {
				mMxaProxy.unregisterFileCallback(FileReceiveCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "MXAProxy is null");
		}
	}

	/** The callback to receive files from MXA/server. */
	private IFileCallback FileReceiveCallback = new IFileCallback.Stub() {

		@Override
		public void processFile(IFileAcceptCallback accepCallback,
				ByteStream xmppFile, String streamID) throws RemoteException {

			/*
			 * Not needed anymore because the area.xml and vehicle icons were integrated in the res folder.
			 * I'll keep the code here just in case someone wants to change it back.
			 * 
			// TODO: requires sd card
			File sdFolder = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()
					+ File.separator
					+ Const.GAME_DATA_DIR_NAME);
			// If the xhunt directory doesn't exist, create it
			if (!sdFolder.isDirectory())
				sdFolder.mkdir();

			// Create the new file which will be transmitted
			File inFile = new File(sdFolder.getAbsolutePath(), xmppFile.path);

			// Cache transfercode and the path to the file for further using
			mIncomingFiles.put(mTransferRequestCode, inFile.getAbsolutePath());

			// Accept file if its not already on our phone to save bandwidth
			if (inFile.exists()) {
				Log.v(TAG, "file exists: " + xmppFile.path);
				
			*/

				mIncomingFileExistsHandler.sendEmptyMessage(0);
				accepCallback.denyFileTransferRequest(mDummyMessenger, mTransferRequestCode, streamID);
				
			/*
			 * Not needed anymore because the area.xml and vehicle icons were integrated in the res folder.
			 * I'll keep the code here just in case someone wants to change it back.
			 * 
			} else {
				accepCallback.acceptFile(mIncomingFileMessenger,
						mTransferRequestCode, streamID,
						inFile.getAbsolutePath(), 256);

				Log.v(TAG, "File received: " + xmppFile.path + "(" + streamID
						+ ")");
			}
			
			*/

			mTransferRequestCode++;
		}
	};

	private IXMPPCallback<JoinGameResponse> _joinGameCallback = new IXMPPCallback<JoinGameResponse>() {

		@Override
		public void invoke(JoinGameResponse bean) {
			if (bean.getType() == XMPPBean.TYPE_ERROR) {
				Log.v(TAG, "errorText: " + bean.errorText);

				Message msg = new Message();
				msg.what = -1;
				msg.obj = bean.errorText;
				mJoinGameHandler.sendMessage(msg);
			} else {
				Game game = mServiceConnector.getXHuntService()
						.getCurrentGame();

				game.setChatID(bean.getChatRoom().toLowerCase());
				game.setChatPassword(bean.getChatPassword());
				game.setGameStartTimer(bean.getStartTimer());

				Set<String> set = new HashSet<String>(
						bean.getIncomingGameFileNames());

				mAnnouncedIncomingFiles = new ArrayList<String>(set);

				mJoinGameHandler.sendEmptyMessage(0);
			}
		}
	};

	private IXMPPCallback<PlayerExitResponse> _playerExitCallback = new IXMPPCallback<PlayerExitResponse>() {

		@Override
		public void invoke(PlayerExitResponse bean) {
			if (bean.getType() == XMPPBean.TYPE_ERROR) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = bean.errorText;
				mExitGameHandler.sendMessage(msg);
			} else {
				if(bean.getType() == XMPPBean.TYPE_RESULT)
					mExitGameHandler.sendEmptyMessage(0);
			}
		}
	};
	
	private IXMPPCallback<PlayerExitResponse> _kickPlayerCallback = new IXMPPCallback<PlayerExitResponse>() {

		@Override
		public void invoke(PlayerExitResponse bean) {
			// do nothing
		}
	};

	private IXMPPCallback<UpdatePlayerResponse> _updatePlayerCallback = new IXMPPCallback<UpdatePlayerResponse>() {

		@Override
		public void invoke(UpdatePlayerResponse bean) {
			if (bean.getType() == XMPPBean.TYPE_ERROR) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = bean.errorText;
				mUpdatePlayerStatusHandler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.what = 0;
				msg.obj = bean.getInfo();
				mUpdatePlayerStatusHandler.sendMessage(msg);
			}
		}
	};

	/**
	 * The Class GameStateLobby represents a state of the game.
	 */
	private class GameStateLobby extends GameState {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * de.tudresden.inf.rn.mobilis.android.xhunt.model.GameState#processPacket
		 * (de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean)
		 */
		@Override
		public void processPacket(XMPPBean inBean) {
			boolean isError = false;
			if (inBean.getType() == XMPPBean.TYPE_ERROR) {
				Log.e(TAG, "IQ Type ERROR: " + inBean.toXML());
				isError = true;
			}

			// Handle and confirm GameOverBean
			if (inBean instanceof GameOverRequest) {
				GameOverRequest bean = (GameOverRequest) inBean;

				if (bean != null && !isError) {
					Message msg = new Message();
					msg.obj = bean.getReason();
					mGameOverHandler.sendMessage(msg);

					mMxaProxy
							.getIQProxy()
							.getProxy()
							.GameOver(
									mMxaProxy.getIQProxy().getGameServiceJid(),
									bean.getId());
				}

			} else if (inBean instanceof LocationRequest) {
				handleLocationBean((LocationRequest) inBean);
			}

			// Handle PlayersBan and update all players
			else if (inBean instanceof PlayersRequest) {
				PlayersRequest bean = (PlayersRequest) inBean;

				if (bean != null && !isError) {
					Log.v(TAG,
							"players updated: "
									+ mGame.updateGamePlayers(bean.getPlayers()));

					Message msg = new Message();
					msg.obj = bean.getInfo();
					mUpdatePlayersHandler.sendMessage(msg);

					mMxaProxy
							.getIQProxy()
							.getProxy()
							.Players(
									mMxaProxy.getIQProxy().getGameServiceJid(),
									bean.getId());
				}
			}
			// Handle StartRoundBean and store the tickets in the Game class.
			else if (inBean instanceof StartRoundRequest) {
				StartRoundRequest bean = (StartRoundRequest) inBean;

				if (bean != null && !isError) {
					mGame.setCurrentRound(bean.getRound());
					mGame.getRouteManagement().setMyTickets(bean.getTickets());

					Message msg = new Message();
					msg.obj = inBean.getId();

					mStartInitialRoundHandler.sendMessage(msg);
					// Result will be send if XHuntMapActivity is created
				}
			} else if (inBean instanceof UpdateTicketsRequest) {
				UpdateTicketsRequest bean = (UpdateTicketsRequest) inBean;

				if (bean != null && !isError) {
					//mMxaProxy.getIQProxy().getProxy().UpdateTickets(bean.getFrom(), bean.getId());
					mGame.getRouteManagement().setMyTickets(bean.getTickets());

					/* Commented out since tickets became hidden
					Message msg = new Message();
					msg.obj = "Ticket amount changed!";

					mMucHandler.sendMessage(msg); */
					// Result will be send if XHuntMapActivity is created
				}
			}
			// Other Beans of type get or set will be responded with an ERROR
			else if (inBean.getType() == XMPPBean.TYPE_GET
					|| inBean.getType() == XMPPBean.TYPE_SET) {
				inBean.errorType = "wait";
				inBean.errorCondition = "unexpected-request";
				inBean.errorText = "This request is not supportet at this game state(Lobby)";

				mMxaProxy.getIQProxy().sendXMPPBeanError(inBean);
			}
		}

		/**
		 * Handle location bean which was transmitted from server and send back
		 * own current position.
		 * 
		 * @param bean
		 *            the transmitted bean
		 */
		private void handleLocationBean(LocationRequest bean) {
			if (bean != null && bean.getType() != XMPPBean.TYPE_ERROR) {
				GeoPoint geoPoint = mServiceConnector.getXHuntService()
						.getGPSProxy().getCurrentLocationAsGeoPoint();

				mMxaProxy
						.getIQProxy()
						.getProxy()
						.Location(
								mMxaProxy.getIQProxy().getGameServiceJid(),
								bean.getId(),
								new LocationInfo(
										mMxaProxy.getXmppJid(),
										geoPoint.getLatitudeE6(),
										geoPoint.getLongitudeE6(),
										true));
			}
		}

	}
}
