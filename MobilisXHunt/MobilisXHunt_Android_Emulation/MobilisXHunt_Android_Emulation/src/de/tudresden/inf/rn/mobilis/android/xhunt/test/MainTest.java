package de.tudresden.inf.rn.mobilis.android.xhunt.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.jayway.android.robotium.solo.Solo;
import com.jayway.android.robotium.solo.SoloWithMaps;

import de.tudresden.inf.rn.mobilis.android.xhunt.activity.MainActivity;
import de.tudresden.inf.rn.mobilis.android.xhunt.activity.OpenGamesActivity;
import de.tudresden.inf.rn.mobilis.android.xhunt.activity.XHuntMapActivity;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Route;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Station;
import android.app.Instrumentation;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestRunner;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

public class MainTest extends ActivityInstrumentationTestCase2<MainActivity> {
	
	private static final String HOST = "mobilis.inf.tu-dresden.de";
	private static final String TAG = "XHuntAutomatic - MainTest";
	private static final String GAMENAME = "XHuntAutomaticTest";
	
	private SoloWithMaps solo;
	private Boolean isMisterX;
	private String userName;
	private Integer playerCount;

	public MainTest() {
		super(MainActivity.class);
	}

	protected void setUp() throws Exception {
		solo = new SoloWithMaps(getInstrumentation(), getActivity());
		
		try {

			Instrumentation i = this.getInstrumentation();
			isMisterX = ((XHuntTestStarter)i).isMisterX();
			userName = ((XHuntTestStarter)i).getUserName();
			
			if(isMisterX) {
				playerCount = ((XHuntTestStarter)i).getPlayerCount();
			}
			
			Log.d(TAG, "userName: " + ((XHuntTestStarter)i).getUserName());
			Log.d(TAG, "isMisterX: " + ((XHuntTestStarter)i).isMisterX().toString());
			Log.d(TAG, "playerCount: " + ((XHuntTestStarter)i).getPlayerCount().toString());
			
		} catch(Exception e) {
			
			Log.d(TAG, "getUserName/isMisterX: can't get values");
			
		}
		
		Log.d(TAG, "XHunt-Test started");
	}

	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
	
	private void clickOnIcon(final String icon) {
		// click on player icon
		Log.d(TAG, "Tap on MapOverlay: " + icon);
		
				try {
					solo.getCurrentActivity().runOnUiThread(new Runnable(){

						public void run() {
							// TODO Auto-generated method stub
							solo.tapMapMarkerItem(icon, 10000);
						}
						
					});
					
				} catch (Exception e) {
					Log.d(TAG, "Tap on MapOverlay: FAILED");
				}
	}
	
	private ArrayList<Double> getCoords() {
					// get coordinates of player icon
					String item = solo.getMapMarkerItem(userName+"@"+HOST+"/MXA");
//					Log.d("Robotium - Marker Item:", item);
					ArrayList<Double> coords = new ArrayList<Double>();

					try {
						JSONObject json = new JSONObject(item);
						coords.add((Double) json.get("latitude"));
						coords.add((Double) json.get("longitude"));
					} catch (Exception e) {
						Log.d(TAG, "JSON parse: FAILED");
					}
					return coords;
	}
	
	private void centerMap() {
		
		try {
			ArrayList<Double> co = getCoords();
			solo.setMapCenter(co.get(0), co.get(1));
		} catch (Exception e) {
			Log.d(TAG, "Center: FAILED");
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public void testClicks() {
		
		// edit settings
		try {
			// press Settings button
			String btnSet = solo.getCurrentActivity().getString(de.tudresden.inf.rn.mobilis.android.xhunt.R.string.main_button_settings_title);
			solo.clickOnButton(btnSet);
			
			// wait for Settings Activity
			solo.waitForActivity("SettingsActivity");
			
			// go back to MainActivity
			solo.goBackToActivity("MainActivity");
			solo.waitForActivity("MainActivity");
			
		} catch(Exception e) {
			Log.d(TAG, "Edit Settings Failed");
		}
		
		// start the game
		try {
			
			// press Play button
			String btnPlay = solo.getCurrentActivity().getString(de.tudresden.inf.rn.mobilis.android.xhunt.R.string.main_button_play_title);
			solo.clickOnButton(btnPlay);
			
			// wait for gamelist
			solo.waitForActivity("OpenGamesActivity");
			solo.sleep(2500);
			
			// get the list of games
			ListView gamesListView = (ListView) solo.getCurrentActivity().findViewById(de.tudresden.inf.rn.mobilis.android.xhunt.R.id.opengames_list);
			ArrayList<String> gamesList = new ArrayList<String>();
			ListAdapter adapter = gamesListView.getAdapter();
			for(int i=0;i<adapter.getCount();i++) {
				Object o = adapter.getItem(i);
				Class c = o.getClass();
				Field textField = c.getField("Name");
				Object value = textField.get(o);
				String text = value.toString();
				gamesList.add(text);
			}
			
			// check for available game name
			Integer gameNumber = 0;
			Log.d(TAG, "gamesList.contains(" + GAMENAME+gameNumber.toString() + ") ? -> " + ((Boolean)gamesList.contains(GAMENAME+gameNumber.toString())).toString());
			while(gamesList.contains(GAMENAME+gameNumber.toString())) {
				if(gamesList.contains(GAMENAME+gameNumber.toString())) {
					Log.d(TAG, "gamesList.contains(" + GAMENAME + gameNumber.toString() + ") ? -> " + ((Boolean)gamesList.contains(GAMENAME + gameNumber.toString())).toString());
					gameNumber++;
				}
			}
			
			// MisterX: create new game
			// Agents: join latest game
			String gameName;
			if(isMisterX) {
				gameName = GAMENAME + gameNumber.toString();
				Log.d(TAG, "gamesList.contains(" + gameName + ") ? -> " + ((Boolean)gamesList.contains(gameName)).toString());
				Log.d(TAG, "new Game: " + gameName);
				
				// create a new game		
				solo.clickOnButton("Create New Game");
				solo.clearEditText(0);
				solo.enterText(0, gameName);
				solo.clickOnButton("Ok");
				solo.clickOnText("Dresden");
				solo.clickOnButton("Create");
				
			} else {
				Integer nr = gameNumber - 1;
				gameName = GAMENAME + (nr).toString();
				Log.d(TAG, "gamesList.contains(" + gameName + ") ? -> " + ((Boolean)gamesList.contains(gameName)).toString());
				Log.d(TAG, "join Game: " + gameName);
				
				// join game
				solo.clickOnText(gameName);
				solo.clickOnText("Join");
			}
			
			// wait for lobby activity and playerlist
			solo.waitForActivity("LobbyActivity");
			solo.sleep(2000);
			
			// MisterX: wait for number of players configured in the starter
			if(isMisterX) {
				TableLayout lobbyTable = (TableLayout) solo.getCurrentActivity().findViewById(de.tudresden.inf.rn.mobilis.android.xhunt.R.id.tbl_lobby);
				while(!(lobbyTable.getChildCount() == playerCount)) {
					Log.d(TAG, "Lobby Table Count: " + lobbyTable.getChildCount());
					solo.sleep(4000);
				}
				Log.d(TAG, "Lobby Table Count: " + lobbyTable.getChildCount() + " -> all players are here!");
				solo.sleep(2000);
			}
			
			// click on ready button
			String btnReady = solo.getCurrentActivity().getString(de.tudresden.inf.rn.mobilis.android.xhunt.R.string.lobby_button_ready_title);
			solo.clickOnButton(btnReady);
			
			// wait for map activity
			solo.waitForActivity("XHuntMapActivity");
			
			Boolean gameOver = false;
			Integer round = 0;
			
			while(!gameOver) {
				
				// wait for new round or game over message
				Log.d(TAG, "wait for new round or game over message");
				while(!solo.searchText("New Round") && !solo.searchText("Game Over.")) {
					solo.sleep(2000);
				}
				if(solo.searchText("Game Over.")) {
					Log.d(TAG, "Game Over!");
					gameOver = true;
					solo.clickOnButton("Ok");
				}
				
				if(!gameOver) {
					
					solo.clickOnButton("OK");
					Log.d(TAG, "Round " + (round+1) + " started");
					
					this.centerMap();
//					solo.sleep(500);
					
					if(round==0) {
						// round 1: zoom in
						
						Log.d(TAG, "Zoom: "+solo.getMapZoom());
						solo.setMapZoom(solo.getMapZoom() + 1);
//						solo.sleep(500);
						
						Log.d(TAG, "Zoom: "+solo.getMapZoom());
						solo.setMapZoom(solo.getMapZoom() + 1);
//						solo.sleep(500);
						
						Log.d(TAG, "Zoom: "+solo.getMapZoom());
						
					} else {
						// other rounds: wait for positioning of player
//						Log.d(TAG, "wait for position change");
//						solo.sleep(15000);
					}
					
					// find next station
					XHuntMapActivity map = (XHuntMapActivity) solo.getCurrentActivity();
					ArrayList<Double> co = getCoords();
					Station stat = map.getGame().getRouteManagement().getStationByLocation(new GeoPoint((int)(co.get(0) * 1E6), (int)(co.get(1) * 1E6)));
					ArrayList<Station> statList = map.getGame().getRouteManagement().getStationsAsList();

					Log.d(TAG, "Current Station: "+stat.getName());

					ArrayList<Station> reachable = new ArrayList<Station>();
					for(Station s:statList) {
						if(s.isReachableFromCurrentStation()) {
							reachable.add(s);
						}
					}
					int count = reachable.size();
					Random r = new Random();
					int id = r.nextInt(count - 1);
					Station nextStation = reachable.get(id);
					String title = nextStation.getName();

					Log.d(TAG, "Next Station: "+title);
					Log.d(TAG, "Click on station: "+title);
					clickOnIcon(title);
					solo.sleep(500);
					
					Log.d(TAG, "click next target 1");
					solo.waitForText("Select as next target");
					Log.d(TAG, "click next target 2");
					solo.clickOnText("Select as next target");
					Log.d(TAG, "click ticket");
					solo.clickInList(0);
					
					Log.d(TAG, "end of round " + (round+1));
					round++;
					
				}
			
			}
			
			// quit game
			solo.waitForActivity("OpenGamesActivity",20000);
			solo.goBack();
			solo.clickOnButton("Exit");
			
			Log.d(TAG, "XHuntTest finished");

			
		} catch(Exception e) {
			Log.d(TAG, "TEST FAILED");
			Log.d(TAG, e.toString());
		}
		
		
	}

}
