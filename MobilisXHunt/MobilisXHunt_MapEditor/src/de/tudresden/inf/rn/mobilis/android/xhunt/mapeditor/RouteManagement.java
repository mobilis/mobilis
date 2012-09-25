
package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor;

import java.util.ArrayList;
import java.util.HashMap;

import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.AreaInfo;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.GeoPoint;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.Route;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.Station;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.Ticket;

/**
 * The Class RouteManagement.
 */
public class RouteManagement {
	
	/** The Constant TAG. */
	public static final String TAG = "RouteManagement";
	
	/** The m area info. */
	private AreaInfo mAreaInfo = null;
	
	/** All routes for this game. */
	private HashMap<Integer, Route> mAreaRoutes;
	
	/** The stations. */
	private HashMap<Integer, Station> mAreaStations;
	
	/** The m area tickets. */
	private HashMap<Integer, Ticket> mAreaTickets;
	
	/**
	 * Instantiates a new route management.
	 */
	public RouteManagement() {
		mAreaRoutes = new HashMap<Integer, Route>();
		mAreaStations = new HashMap<Integer, Station>();
		mAreaTickets = new HashMap<Integer, Ticket>();
	}
	
	/**
	 * Compute distance between 2 geopoints.
	 *
	 * @param loc1 the loc1
	 * @param loc2 the loc2
	 * @return the distance
	 */
	public double computeDistance(GeoPoint loc1, GeoPoint loc2)	{
		double x = 71.5 *  (loc1.getLongitudeE6() / 1E6 - loc2.getLongitudeE6() / 1E6);
		double y = 111.3* (loc1.getLatitudeE6() / 1E6 - loc2.getLatitudeE6() / 1E6);
		
		return Math.sqrt(x * x + y * y);
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
	 * Gets the nearest station.
	 *
	 * @param geoPoint the geo point
	 * @return the nearest station
	 */
	public Station getNearestStation(GeoPoint geoPoint){
		Station nearestStation = null;
		double minDistance = Double.MAX_VALUE;
		double computedDistance = Double.MAX_VALUE;
		
		for(Station station : mAreaStations.values()){
			computedDistance = computeDistance(geoPoint, station.getGeoPoint());
			
			if(computedDistance < minDistance){
				minDistance = computedDistance;
				nearestStation = station;
			}
		}
		
		return nearestStation;
	}
	
	/**
	 * Gets the new station id.
	 *
	 * @return the new station id
	 */
	public int getNewStationId(){
		int id = 1;
		
		for(int sId : mAreaStations.keySet()){
			if(sId > id) id = sId;
		}
		
		return id + 1;
	}
	
	/**
	 * Gets the new route id.
	 *
	 * @return the new route id
	 */
	public int getNewRouteId(){
		int id = 1;
		
		for(int rId : mAreaRoutes.keySet()){
			if(rId > id) id = rId;
		}
		
		return id + 1;
	}
	
	/**
	 * Gets the routes.
	 * 
	 * @return all routes
	 */
	public HashMap<Integer, Route> getRoutes() {
		return mAreaRoutes;
	}
	
	/**
	 * Gets the routes for station.
	 *
	 * @param stationId the station id
	 * @return the routes for station
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
	 * Gets the routes for station.
	 * 
	 * @param station the station
	 * 
	 * @return the routes for station
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
	 * Removes the route.
	 *
	 * @param routeId the route id
	 * @return the route
	 */
	public Route removeRoute(int routeId){
		return mAreaRoutes.remove(routeId);
	}
	
	/**
	 * Removes the station.
	 *
	 * @param stationId the station id
	 * @return the station
	 */
	public Station removeStation(int stationId){
		ArrayList<Route> routesOfStation = getRoutesForStation(stationId);
		
		for(Route route : routesOfStation)
			route.removeStation(stationId);
		
		return mAreaStations.remove(stationId);
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

	/**
	 * Gets the area info.
	 *
	 * @return the area info
	 */
	public AreaInfo getAreaInfo() {
		return mAreaInfo;
	}

	/**
	 * Sets the area info.
	 *
	 * @param info the new area info
	 */
	public void setAreaInfo(AreaInfo info) {
		this.mAreaInfo = info;
	}
	
	

}
