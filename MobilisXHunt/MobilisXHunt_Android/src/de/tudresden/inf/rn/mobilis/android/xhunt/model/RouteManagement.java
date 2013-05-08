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
package de.tudresden.inf.rn.mobilis.android.xhunt.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.graphics.Paint;
import android.util.Log;

import com.google.android.maps.GeoPoint;

import de.tudresden.inf.rn.mobilis.android.xhunt.Const;
import de.tudresden.inf.rn.mobilis.android.xhunt.clientstub.TicketAmount;
import de.tudresden.inf.rn.mobilis.android.xhunt.helper.TicketMap;

/**
 * The Class RouteManagement provides all necessary data for the current 
 * game area.
 */
public class RouteManagement {
	
	/** The Constant TAG is used for logging. */
	public static final String TAG = "RouteManagement";
	
	/** The id of the area. */
	private int mAreaId;
	
	/** The name of the area. */
	private String mAreaName;
	
	/** The description of the area. */
	private String mAreaDescription;
	
	/** The version of the area. */
	private int mAreaVersion;
	
	/** All routes for this game (routeId, Route). */
	private HashMap<Integer, Route> mAreaRoutes;
	
	/** The stations for this game (stationId, Station). */
	private HashMap<Integer, Station> mAreaStations;
	
	/** The map center point. */
	private GeoPoint mapCenter;
	
	/** The tickets of the area (ticketId, Ticket). */
	private HashMap<Integer, Ticket> mAreaTickets;
	
	/** The amount of own tickets in this game (ticketId, amount). */
	private TicketMap mMyTickets;
	
	/** The available paints for the routes. */
	private ArrayList<Paint> mPathPaints;
	
	/**
	 * Instantiates a new route management.
	 */
	public RouteManagement() {
		mAreaRoutes = new HashMap<Integer, Route>();
		mAreaStations = new HashMap<Integer, Station>();
		mAreaTickets = new HashMap<Integer, Ticket>();
		mapCenter = null;
		mMyTickets = new TicketMap();
		
		mPathPaints = new ArrayList<Paint>();
		
		initPathPaints();
	}
	
	/**
	 * Compute distance between 2 geographical points.
	 *
	 * @param loc1 the start point
	 * @param loc2 the end point
	 * @return the distance between this points in double
	 */
	public double computeDistance(GeoPoint loc1, GeoPoint loc2)	{
		double x = 71.5 *  (loc1.getLongitudeE6() / 1E6 - loc2.getLongitudeE6() / 1E6);
		double y = 111.3* (loc1.getLatitudeE6() / 1E6 - loc2.getLatitudeE6() / 1E6);
		
		return Math.sqrt(x * x + y * y);
	}
	
	/**
	 * Decrease own ticket.
	 *
	 * @param ticketId the ticket id
	 */
	public void decreaseTicket(int ticketId){
		if(ticketId > 0)
			mMyTickets.put(ticketId, mMyTickets.get(ticketId) - 1);
	}
	
	/**
	 * Gets the area description.
	 *
	 * @return the area description
	 */
	public String getAreaDescription() {
		return mAreaDescription;
	}
	
	/**
	 * Gets the area id.
	 *
	 * @return the area id
	 */
	public int getAreaId() {
		return mAreaId;
	}
	
	/**
	 * Gets the area name.
	 *
	 * @return the area name
	 */
	public String getAreaName() {
		return mAreaName;
	}
	
	/**
	 * Gets the area tickets.
	 *
	 * @return the area tickets
	 */
	public HashMap<Integer, Ticket> getAreaTickets() {
		return mAreaTickets;
	}
	
	/**
	 * Gets the area version.
	 *
	 * @return the area version
	 */
	public int getAreaVersion(){
		return this.mAreaVersion;
	}
	
	/**
	 * Gets the map center point.
	 * 
	 * @return the map center point
	 */
	public GeoPoint getMapCenter() {
		if (mapCenter == null) {
			// Calculate the Center of the map
			int maxLat = Const.MAX_LATITUDE_VALUE;
			int maxLon = Const.MAX_LONGITUDE_VALUE;
			int minLat = Const.MIN_LATITUDE_VALUE;
			int minLon = Const.MIN_LONGITUDE_VALUE;
			
			for (Station station : mAreaStations.values()) {				
				if (station.getLatitude() > maxLat) maxLat = station.getLatitude();
				if (station.getLatitude() < minLat) minLat = station.getLatitude();
				if (station.getLongitude() > maxLon) maxLon = station.getLongitude();
				if (station.getLongitude() < minLon) minLon = station.getLongitude();			
			}

			mapCenter = new GeoPoint((maxLat + minLat) / 2, (maxLon + minLon) / 2);
		}
		
		return mapCenter;
	}
	
	/**
	 * Gets all available tickets for the route between the current station 
	 * and target station of our own player.
	 *
	 * @param currentStationId the id of the current station
	 * @param targetStationId the id of the target station
	 * @return all available routes and their related tickets if the count of 
	 * available tickets is at minimum one
	 */
	public HashMap<Route, Integer> getMyRouteTicketsForTarget(int currentStationId, int targetStationId){
		HashMap<Route, Integer> routeTickets = new HashMap<Route, Integer>();
		
		Log.v(TAG, "currentStation: " + currentStationId);
		Log.v(TAG, "targetstation: " + targetStationId);
		
		for(Route routeCurrentStation : getRoutesForStation(currentStationId)){
			// is target station a neighbor of current station?
			if(routeCurrentStation.getNextStationIds(currentStationId).contains(targetStationId)){
				// do my player has more than 0 tickets for this route?
				if(mMyTickets.get(routeCurrentStation.getTicketId()) > 0){
					routeTickets.put(routeCurrentStation, routeCurrentStation.getTicketId());
				}
			}
		}
		
		return routeTickets;
	}
	
	/**
	 * Gets own player tickets (ticketId, amount).
	 *
	 * @return own player tickets and amount
	 */
	public Map<Integer, Integer> getMyTickets() {
		return mMyTickets;
	}
	
	/**
	 * Get the routes (routeId, Route).
	 * 
	 * @return all routes
	 */
	public HashMap<Integer, Route> getRoutes() {
		return mAreaRoutes;
	}
	
	/**
	 * Get routes for station.
	 *
	 * @param stationId the id of the station which routes should be returned
	 * @return the routes for a station
	 */
	public ArrayList<Route> getRoutesForStation(int stationId){
		ArrayList<Route> result = new ArrayList<Route>();
		
		for(Route route : mAreaRoutes.values()){
			if(route.containsStation(stationId))
				result.add(route);
		}

		return result;
	}
	
	/**
	 * Get the routes for station.
	 * 
	 * @param station the station which routes should be returned
	 * 
	 * @return the routes for a station
	 */
	public ArrayList<Route> getRoutesForStation(Station station){
		return getRoutesForStation(station.getId());
	}
	
	/**
	 * Parsing a route from an @see org.xmlpull.v1.XmlPullParser.
	 *
	 * @param parser the parser which contains the route
	 * @return the route
	 */
	private Route getRouteFromAttributes(XmlPullParser parser){
		Route route = new Route();
		
		// parse all attributes of the route
		for(int i=0; i<parser.getAttributeCount(); i++){
			if(parser.getAttributeName(i).equals("id"))
				route.setId(Integer.valueOf(parser.getAttributeValue(i)));
			else if(parser.getAttributeName(i).equals("name"))
				route.setName(parser.getAttributeValue(i));
			else if(parser.getAttributeName(i).equals("type"))
				route.setTicketId(Integer.valueOf(parser.getAttributeValue(i)));
			else if(parser.getAttributeName(i).equals("start"))
				route.setStart(parser.getAttributeValue(i));
			else if(parser.getAttributeName(i).equals("end"))
				route.setEnd(parser.getAttributeValue(i));
		}
		
		// parse all stops(stations) of the route
		boolean done = false;
		try {
			do {
				switch (parser.getEventType()){				
					case XmlPullParser.START_TAG:
						String tagName = parser.getName();
						
						if (tagName.equals("Route")){
							parser.next();
						}
						else if (tagName.equals("stop")) {
							int pos = -1;
							int num = -1;
							
							if(parser.getAttributeCount() > 0){
								if(parser.getAttributeName(0).equals("pos"))
									pos = Integer.valueOf(parser.getAttributeValue(0));
							}
							
							num = Integer.valueOf(parser.nextText());
							
							if(num > -1 && pos > -1)
								route.addStation(pos, num);
						}
						else
							parser.next();
						break;
					case XmlPullParser.END_TAG:
						if (parser.getName().equals("Route")){
							done = true;
						}
						else
							parser.next();
						break;
					case XmlPullParser.END_DOCUMENT:
						done = true;
						break;
					default:
						parser.next();
				}
			} while (!done);
		} catch (XmlPullParserException e) {
			Log.e(TAG, "Parsing game data file fails.");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "Reading game data file fails.");
			e.printStackTrace();
		}

		return route;
	}
	
	/**
	 * Get a station by id.
	 *
	 * @param stationId the id of the station
	 * @return the station
	 */
	public Station getStationById(int stationId){
		return mAreaStations.get(stationId);
	}

	/**
	 * Get a station by location.
	 * 
	 * @param geo a GeoPoint
	 * 
	 * @return the nearest station to the geopoint
	 */
	public Station getStationByLocation(GeoPoint geo) {
		for(Station s : mAreaStations.values()){
			if (computeDistance(geo,s.getGeoPoint()) < Const.IS_LOCATIONNEAR_STATION_RADIUS)
				return s;
		}

		return null;
	}
	
	/**
	 * Parsing a station from an @see org.xmlpull.v1.XmlPullParser.
	 *
	 * @param parser the parser which contains the station
	 * @return the station
	 */
	private Station getStationFromAttributes(XmlPullParser parser){
		Station station = new Station();
		int lat = 0;
		int lon = 0;
		
		for(int i=0; i<parser.getAttributeCount(); i++){
			if(parser.getAttributeName(i).equals("id"))
				station.setId(Integer.valueOf(parser.getAttributeValue(i)));
			else if(parser.getAttributeName(i).equals("abbrev"))
				station.setAbbreviation(parser.getAttributeValue(i));
			else if(parser.getAttributeName(i).equals("name"))
				station.setName(parser.getAttributeValue(i));
			else if(parser.getAttributeName(i).equals("latitude"))
				lat = Integer.valueOf(parser.getAttributeValue(i));
			else if(parser.getAttributeName(i).equals("longitude"))
				lon = Integer.valueOf(parser.getAttributeValue(i));
		}
		station.setGeoPoint(lat, lon);
		
		return station;
	}
	
	/**
	 * Gets the stations.
	 *
	 * @return the stations
	 */
	public HashMap<Integer, Station> getStations(){
		return this.mAreaStations;
	}
	
	
	/**
	 * Gets the stations as list.
	 *
	 * @return the stations as list
	 */
	public ArrayList<Station> getStationsAsList(){
		return new ArrayList<Station>(mAreaStations.values());
	}

	/**
	 * Parsing a ticket from an @see org.xmlpull.v1.XmlPullParser.
	 *
	 * @param parser the parser which contains the ticket
	 * @return the ticket
	 */
	private Ticket getTicketFromAttributes(XmlPullParser parser){
		Ticket ticket = new Ticket();
		
		for(int i=0; i<parser.getAttributeCount(); i++){
			if(parser.getAttributeName(i).equals("id"))
				ticket.setId(Integer.valueOf(parser.getAttributeValue(i)));
			else if(parser.getAttributeName(i).equals("name"))
				ticket.setName(parser.getAttributeValue(i));
			else if(parser.getAttributeName(i).equals("icon")){
				/*
				 * Changed because vehicle icons now are included in res/drawable-mdpi/.
				 * 
				 * ticket.setIconPath(Environment.getExternalStorageDirectory().getAbsoluteFile()
						+ File.separator + Const.GAME_DATA_DIR_NAME + File.separator + parser.getAttributeValue(i));
				*/
				ticket.setIconFileName(parser.getAttributeValue(i));
			}
			else if(parser.getAttributeName(i).equals("issuperior"))
				ticket.setSuperior(Boolean.valueOf(parser.getAttributeValue(i)));
		}

		return ticket;
	}
	
	/**
	 * Init the default paints for the routes.
	 */
	private void initPathPaints(){
		mPathPaints.add(makePaint(75, 35, 180, 245)); //blue
		mPathPaints.add(makePaint(75, 245, 180, 35)); //orange
		mPathPaints.add(makePaint(75, 85, 205, 25)); //green
		mPathPaints.add(makePaint(75, 180, 35, 245)); //violet
		mPathPaints.add(makePaint(75, 245, 35, 35)); //red
	}
	
	
	/**
	 * Checks if the player is movable or not.
	 *
	 * @param myPlayer the my player
	 * @return true, if player is unmovable
	 */
	public boolean isMyPlayerUnmovable(XHuntPlayer myPlayer){		
		boolean unmoveable = true;
		
		for(Route route : getRoutesForStation(myPlayer.getLastStationId())){
			if(mMyTickets.get(route.getTicketId()) > 0
					|| mAreaTickets.get(route.getTicketId()).isSuperior()){
				unmoveable = false;
				break;
			}
		}
	 	
	 	return unmoveable;
	}
	
	/**
	 * Creates a Paint object from int values.
	 *
	 * @param a the alpha value
	 * @param r the red value
	 * @param g the green value
	 * @param b the blue value
	 * @return the paint
	 */
	private Paint makePaint(int a, int r, int g, int b){
		Paint paint = new Paint();
		paint.setStrokeWidth(3);
		paint.setStyle(Paint.Style.STROKE);
		paint.setARGB(a, r, g ,b);
		
		return paint;
	}
	
	/**
	 * Reset reachable stations.
	 */
	public void resetReachableStations(){
		for (Station s : mAreaStations.values()) {							
			s.setReachableFromCurrentStation(false);
		}
	}

	
	/**
	 * Parse an xml which contains the game data.
	 *
	 * @param reader a reader for the xml file.
	 * @return true, if successful
	 */
	/*
	 * Changed argument because the area.xml isn't stored on the external memory any more,
	 * instead it's integrated in /res/raw/. I'll keep the two old lines outcommented just
	 * in case someone wants to change it back.
	 * 
	 * public boolean parseDataXML(String path){
	 */
	public boolean parseDataXML(Reader reader) {
		boolean success = false;
		
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			
			//parser.setInput(new FileReader(path));
			parser.setInput(reader);
			
			boolean done = false;
			
			do {
				switch (parser.getEventType()){
				
					case XmlPullParser.START_TAG:
						String tagName = parser.getName();
						
						if (tagName.equals("area")){
							parser.next();
						}
						else if (tagName.equals("id")) {
							this.mAreaId = Integer.valueOf(parser.nextText()).intValue();
						}
						else if (tagName.equals("name")) {
							this.mAreaName = parser.nextText();
						}
						else if (tagName.equals("desc")) {
							this.mAreaDescription = parser.nextText();
						}
						else if (tagName.equals("version")) {
							this.mAreaVersion = Integer.valueOf(parser.nextText()).intValue();
						}						
						else if (tagName.equals("Ticket")) {
							Ticket t = getTicketFromAttributes(parser);
							this.mAreaTickets.put(t.getId(), t);
							parser.next();
						}
						else if (tagName.equals("Station")) {
							Station s = getStationFromAttributes(parser);
							this.mAreaStations.put(s.getId(), s);
							parser.next();
						}
						else if (tagName.equals("Route")) {
							Route r = getRouteFromAttributes(parser);
							int stationCount = r.getStationIds().size();
							if (stationCount > 1) {
								this.mAreaRoutes.put(r.getId(), r);
							}
							parser.next();
						}
						else
							parser.next();
						break;
					case XmlPullParser.END_TAG:
						if (parser.getName().equals("area")){
							done = true;
						}
						else
							parser.next();
						break;
					case XmlPullParser.END_DOCUMENT:
						done = true;
						break;
					default:
						parser.next();
				}
			} while (!done);
			
			success = true;

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//TODO: currently there are just 5 different kinds of routes supported
		int counter = 0;
		for(Ticket ticket : mAreaTickets.values()){
			if(!ticket.isSuperior()){
				for(Route route : mAreaRoutes.values()){
					if(route.getTicketId() == ticket.getId()
							&& counter < mPathPaints.size())
						route.setPathPaint(mPathPaints.get(counter));
				}
				counter++;
			}
		}
		
		

/*		Log.v("Routemanagement", "areaId: " + mAreaId + " areaname: " + mAreaName + " areadesc: " + mAreaDescription);
		
		for(Map.Entry<Integer, Ticket> t : mAreaTickets.entrySet())
			Log.v("Routemanagement", t.toString());
		
		for(Map.Entry<Integer, Station> s : mAreaStations.entrySet())
			Log.v("Routemanagement", s.toString());
		
		for(Map.Entry<Integer, Route> r : mAreaRoutes.entrySet())
			Log.v("Routemanagement", r.toString());
*/		
		return success;
	}


	/**
	 * Sets the area description.
	 *
	 * @param areaDescription the new area description
	 */
	public void setAreaDescription(String areaDescription) {
		this.mAreaDescription = areaDescription;
	}
	
	/**
	 * Sets the area id.
	 *
	 * @param areaId the new area id
	 */
	public void setAreaId(int areaId) {
		this.mAreaId = areaId;
	}

	/**
	 * Sets the area name.
	 *
	 * @param areaName the new area name
	 */
	public void setAreaName(String areaName) {
		this.mAreaName = areaName;
	}
	
	/**
	 * Sets the area tickets.
	 *
	 * @param areaTickets the area tickets
	 */
	public void setAreaTickets(HashMap<Integer, Ticket> areaTickets) {
		this.mAreaTickets = areaTickets;
	}
	
	/**
	 * Sets the own tickets.
	 *
	 * @param mMyTickets the ticket type and amount of tickets (ticketId, amount)
	 */
	public void setMyTickets(List<TicketAmount> mMyTickets) {
		for ( TicketAmount ticketAmount : mMyTickets ) {
			this.mMyTickets.put( ticketAmount.getID(), ticketAmount.getAmount() );
		}
	}
	
	/**
	 * Update reachable stations from players current position.
	 *
	 * @param stationId the current station id where own player id located at
	 */
	public void updateReachableStations(int stationId){
		for(Route route : getRoutesForStation(stationId)){
			if(mMyTickets.get(route.getTicketId()) > 0
					|| mAreaTickets.get(route.getTicketId()).isSuperior()){
				for(Integer neighborStationId : route.getNextStationIds(stationId)){
					mAreaStations.get(neighborStationId).setReachableFromCurrentStation(true);
				}
			}
		}
	}

}
