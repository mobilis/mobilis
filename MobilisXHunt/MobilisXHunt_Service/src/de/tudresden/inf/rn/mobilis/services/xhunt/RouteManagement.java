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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import de.tudresden.inf.rn.mobilis.services.xhunt.model.GeoPoint;
import de.tudresden.inf.rn.mobilis.services.xhunt.model.Route;
import de.tudresden.inf.rn.mobilis.services.xhunt.model.Station;
import de.tudresden.inf.rn.mobilis.services.xhunt.model.Ticket;
import de.tudresden.inf.rn.mobilis.services.xhunt.model.XHuntPlayer;

/**
 * The Class RouteManagement provides all necessary data for the current 
 * game area.
 */
public class RouteManagement {
	
	/** The Constant TAG is used for logging. */
	public static final String TAG = "RouteManagement";
	
	/** The m area id. */
	private int mAreaId;
	
	/** The m area name. */
	private String mAreaName;
	
	/** The m area description. */
	private String mAreaDescription;
	
	/** All routes for this game (routeId, Route). */
	private HashMap<Integer, Route> mAreaRoutes;
	
	/** The stations for this game (stationId, Station). */
	private HashMap<Integer, Station> mAreaStations;
	
	/** The tickets of the area (ticketId, Ticket). */
	private HashMap<Integer, Ticket> mAreaTickets;
	
	/** The start stations which were already assigned to a player. */
	private Map<XHuntPlayer, Station> assignedStartStations;
	
	/** The class specific Logger object. */
	private final static Logger LOGGER = Logger.getLogger(RouteManagement.class.getCanonicalName());
	
	/**
	 * Instantiates a new RouteManagement.
	 */
	public RouteManagement() {
		mAreaRoutes = new HashMap<Integer, Route>();
		mAreaStations = new HashMap<Integer, Station>();
		mAreaTickets = new HashMap<Integer, Ticket>();
		assignedStartStations = new ConcurrentHashMap<XHuntPlayer, Station>();
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
	 * Gets the nearest station of a Player, but only returns a specific station once.
	 * In case a specific station was already returned, it returns the second nearest station instead.
	 *
	 * @param player the Player for which this method is called
	 * @return the nearest station
	 */
	public Station getNearestStation(XHuntPlayer player) {
		if(assignedStartStations.containsKey(player))
			return assignedStartStations.get(player);
		
		else {
			Station nearestStation = null;
			double minDistance = Double.MAX_VALUE;
			double computedDistance = Double.MAX_VALUE;
			
			for(Station station : mAreaStations.values()) {
				computedDistance = computeDistance(player.getGeoLocation(), station.getGeoPoint());
				
				if(computedDistance < minDistance) {
					if(!assignedStartStations.values().contains(station)) {
						minDistance = computedDistance;
						nearestStation = station;
					}
				}
			}
			
			assignedStartStations.put(player, nearestStation);
			return nearestStation;
		}
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
	 * Gets the station.
	 *
	 * @param stationId the station id
	 * @return the station
	 */
	public Station getStation(int stationId){
		return mAreaStations.get(stationId);
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
	 * Checks if a player is unmovable.
	 *
	 * @param player the player to check for
	 * @return true, if the player is unmovable
	 */
	public boolean isPlayerUnmovable(XHuntPlayer player){		
		boolean unmoveable = true;
		
		for(Route route : getRoutesForStation(player.getLastStationId())){
			if(player.getTicketsAmount().get(route.getTicketId()) > 0
					|| mAreaTickets.get(route.getTicketId()).isSuperior()){
				unmoveable = false;
				break;
			}
		}
	 	
	 	return unmoveable;
	}
	
	/**
	 * Test if the target can be reached by the player, depends on the tickets of the player.
	 *
	 * @param stationId the station id
	 * @param player the player
	 * @return True, if the target is reachable by the player, else false.
	 */
	public boolean isTargetReachable(int stationId, XHuntPlayer player){
		LOGGER.info("current station: " + player.getLastStationId());
		for(Route route : getRoutesForStation(stationId)){LOGGER.info("route: " + route.getId() + " target: " + stationId);
			if(route.containsStation(player.getLastStationId())){
				LOGGER.info("route: " + route.getId() + " contains target " + " nextStations: " + route.getNextStationIds(stationId).toString());
				for(int nextStationId : route.getNextStationIds(stationId)){LOGGER.info("nextStation: " + nextStationId);
					if(nextStationId == player.getLastStationId())
						return true;
				}
			}
		}
		
		return false;
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
	 * Sets the area routes.
	 *
	 * @param mAreaRoutes the m area routes
	 */
	public void setAreaRoutes(HashMap<Integer, Route> mAreaRoutes) {
		this.mAreaRoutes = mAreaRoutes;
	}

	/**
	 * Sets the area stations.
	 *
	 * @param mAreaStations the m area stations
	 */
	public void setAreaStations(HashMap<Integer, Station> mAreaStations) {
		this.mAreaStations = mAreaStations;
	}

	/**
	 * Sets the area tickets.
	 *
	 * @param areaTickets the area tickets
	 */
	public void setAreaTickets(HashMap<Integer, Ticket> areaTickets) {
		this.mAreaTickets = areaTickets;
	}

}
