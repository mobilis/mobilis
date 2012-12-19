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
package de.tudresden.inf.rn.mobilis.android.xhunt;

/**
 * The Class Const contains global application properties.
 */
public class Const {
	
	/** The Constant MAP_ACTIVITY_ID. */
	public static final int MAP_ACTIVITY_ID							= 9003;
	
	/** Defines path to shared preferences for XHunt. */
	public static final String SHARED_PREF_KEY_FILE_NAME			= "de.tudresden.inf.rn.mobilis.android.xhunt_preferences";
	
	/** Max possible zoom which is allowed in map activity. */
	public static final int MAP_MAX_ZOOM_LEVEL						= 13;
	
	/** Max length of message shown in map activity if a new groupchat message comes in. */
	public static final int MUC_NOTIFIER_MESSAGE_LENGTH				= 90;	
	
	/** The Constant BUNDLE_KEY_GAME_ID to share information between activities. */
	public static final String BUNDLE_KEY_GAME_ID					= "key_gameId";
	
	/** The Constant BUNDLE_KEY_LOCATION_CHANGED_LAT share information between activities. */
	public static final String BUNDLE_KEY_LOCATION_CHANGED_LAT		= "key_lc:latitude";
	
	/** The Constant BUNDLE_KEY_LOCATION_CHANGED_LON share information between activities. */
	public static final String BUNDLE_KEY_LOCATION_CHANGED_LON		= "key_lc:longitude";
	
	/** The Constant BUNDLE_KEY_STARTROUNDID share information between activities. */
	public static final String BUNDLE_KEY_STARTROUNDID				= "key_startroundid";
	
	/** Timeout for connection delays in milliseconds. */
	public static final int CONNECTION_TIMEOUT_DELAY				= 15000;
	
	/** Radius of a station(lat and long), which defines if a player is near a station or not 
	 * (in kilometers). */
	public static final double IS_LOCATIONNEAR_STATION_RADIUS		= 0.05;
	
	/** The Constant MAX_LATITUDE_VALUE. */
	public static final int MAX_LATITUDE_VALUE						= -91000000;
	
	/** The Constant MAX_LONGITUDE_VALUE. */
	public static final int MAX_LONGITUDE_VALUE						= -181000000;
	
	/** The Constant MIN_LATITUDE_VALUE. */
	public static final int MIN_LATITUDE_VALUE						= 91000000;
	
	/** The Constant MIN_LONGITUDE_VALUE. */
	public static final int MIN_LONGITUDE_VALUE						= 181000000;
	
	/** Ticket id if a player does a suggestion. */
	public static final int TICKET_ID_SUGGESTION 					= -1;
	
	/** Ticket id if a player can not move anymore from his current position. */
	public static final int TICKET_ID_UNMOVABLE 					= 0;
	
	/** Name of directory which is used to store received data on game. */
	public static final String GAME_DATA_DIR_NAME					= "xhunt";
}
