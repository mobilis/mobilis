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
 * A 'Route' represents a bus-, tram- or railway-route in reality.
 * It consists of different stations, a start station and an end station.
 * @author Fanny, Robert, Sven
 */

package de.tudresden.inf.rn.mobilis.android.xhunt.model;

import java.util.ArrayList;
import java.util.LinkedList;

import android.graphics.Paint;
import android.util.Log;
import android.util.SparseArray;

/**
 * The Class Route represents a connection between two or more stations.
 */
public class Route {
	
	/** The id of the route. */
	private int id;
	
	/** The name. */
	private String name;
	
	/** The ticket which is needed to move one station ahead of this route. */
	private int ticketId;
	
	/** The name of the start direction of the route. */
	private String startName;
	
	/** The name of the end direction of the route. */
	private String endName;
	
	/** The stations which belong to this route 
	 * linked in the order they are situated along the route. */
	private LinkedList<Integer> stationIds;
	
	/** The paint for this route to display it on the map. */
	private Paint pathPaint;
	
	private SparseArray<float[]> pathsForZoomlevels = new SparseArray<float[]>(10);
	
	/**
	 * Instantiates a new Route.
	 */
	public Route() {
		this.stationIds = new LinkedList<Integer>();
		
		// Default values for a route paint
		this.pathPaint = new Paint();
		this.pathPaint.setStrokeWidth(3);
		this.pathPaint.setStyle(Paint.Style.STROKE);
		this.pathPaint.setARGB(75, 35, 180, 245);
	}
	
	/**
	 * Instantiates a new Route.
	 *
	 * @param id the routes id
	 * @param name the name of the route
	 * @param ticketId the type of ticket which can be used for this route
	 * @param start the name of the route start
	 * @param end the name of the route end
	 * @param pathpaint the paint which is used to display this route
	 */
	public Route(int id, String name, int ticketId, String start, String end, Paint pathpaint){
		this.id = id;
		this.ticketId = ticketId;
		this.startName = start;
		this.endName = end;
		this.stationIds = new LinkedList<Integer>();
		
		this.pathPaint = pathpaint;
		this.pathPaint.setStrokeWidth(3);
		this.pathPaint.setStyle(Paint.Style.STROKE);
	}

	/**
	 * Adds a station to the route.
	 * 
	 * @param position the position of the station (from start to end)
	 * @param station the station itself
	 * 
	 * @return true, if successful
	 */
	public void addStation(int position, Station station){
		stationIds.add(position, station.getId());
	}
	
	/**
	 * Adds a station.
	 *
	 * @param position the position of the station (from start to end)
	 * @param stationId the id of the station
	 */
	public void addStation(int position, int stationId){
		stationIds.add(position, stationId);
	}
	
	/**
	 * Checks if this route contains a station.
	 *
	 * @param cmpStationId the station id to compare with
	 * @return true, if station belongs to the route
	 */
	public boolean containsStation(int cmpStationId){
		return stationIds.contains(cmpStationId);
	}
	
	/**
	 * Gets the id of this route.
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of this route.
	 * 
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the position of a station (from start to end).
	 *
	 * @param stationId the id of a station
	 * @return the position of a station
	 */
	public Integer getPositionOfStation(int stationId){
		return stationIds.indexOf(stationId);
	}
	
	/**
	 * Gets the position of a station (from start to end).
	 *
	 * @param station a station
	 * @return the position of station
	 */
	public Integer getPositionOfStation(Station station){
		return getPositionOfStation(station.getId());
	}
	
	/**
	 * Gets the start name direction of this route.
	 * 
	 * @return the start name direction
	 */
	public String getStart() {
		return startName;
	}
	
	/**
	 * Gets the stations belongs to this route.
	 * 
	 * @return the stations
	 */
	public LinkedList<Integer> getStationIds() {
		return stationIds;
	}

	/**
	 * Gets the ticket type of this route.
	 * 
	 * @return the ticket type
	 */
	public int getTicketId() {
		return this.ticketId;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the ticket type of this route.
	 *
	 * @param ticketId the id of the new ticket type
	 */
	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}

	/**
	 * Sets the start name direction of this route.
	 * 
	 * @param start the new start name direction
	 */
	public void setStart(String start) {
		this.startName = start;
	}

	/**
	 * Gets the end name direction of this route.
	 * 
	 * @return the end name direction
	 */
	public String getEnd() {
		return endName;
	}

	/**
	 * Sets the end name direction of this route.
	 * 
	 * @param end the new end name direction
	 */
	public void setEnd(String end) {
		this.endName = end;
	}

	/**
	 * Sets the stations which belongs to this route.
	 * 
	 * @param stops the stations of this route
	 */
	public void setStations(LinkedList<Integer> stops) {
		this.stationIds = stops;
	}
	
	/**
	 * Gets the path paint of this route.
	 * 
	 * @return the path paint
	 */
	public Paint getPathPaint() {
		return pathPaint;
	}
	
	/**
	 * Sets the path paint for this route.
	 *
	 * @param paint the new path paint
	 */
	public void setPathPaint(Paint paint){
		this.pathPaint = paint;
	}
	
	/**
	 * Gets the neighbor stations of a specific station in this route.
	 *
	 * @param stationId the station id which neighbors should be found
	 * @return the an ArrayList of neighbors for the station (could be empty).
	 */
	public ArrayList<Integer> getNextStationIds(int stationId){
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		if (!this.containsStation(stationId)) return list;
		
		int position = this.getPositionOfStation(stationId);
		
		// Return an empty list, if station was not found in the HashMap.
		if (position==-1) return list;
		
		// Add neighbor stations to the list.
		int s;
		
		if(position > 0){
			s = stationIds.get(position-1);
			if (s>0) list.add(s);
		}
		
		if(position < stationIds.size() - 1){
			s = stationIds.get(position+1);
			if (s>0) list.add(s);
		}
			
		return list;
	}
	
	/**
	 * Gets the neighbor stations of a specific station in this route.
	 *
	 * @param station the station which neighbors should be found
	 * @return the ids of the next stations
	 */
	public ArrayList<Integer> getNextStationIds(Station station){
		return getNextStationIds(station.getId());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String stations = "";
		
		int i = 0;
		for (int stationId : stationIds) {
			stations += " " + i + ";" + stationId;
		}
		
		return "Route [id=" + id + ", name=" + name + ", ticketId=" + ticketId
				+ ", startName=" + startName + ", endName=" + endName
				+ ", stationIds=" + stations + "]";
	}

	public float[] getPathForZoomlevel(int zoomlevel) {
		Log.i("############", "Zoomlevel get:" + zoomlevel);
		float[] path = pathsForZoomlevels.get(zoomlevel);
		if (path != null) {
			return path.clone();
		} else {
			return null;
		}
	}

	public void setPathForZoomlevel(float[] path, int zoomlevel) {
		Log.i("############", "Zoomlevel set:" + zoomlevel);
		this.pathsForZoomlevels.put(zoomlevel, path.clone());
	}
}
