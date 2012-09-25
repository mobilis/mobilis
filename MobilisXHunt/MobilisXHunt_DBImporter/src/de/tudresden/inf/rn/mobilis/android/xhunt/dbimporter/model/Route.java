
package de.tudresden.inf.rn.mobilis.android.xhunt.dbimporter.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class Route.
 */
public class Route {
	
	/** The id of the route. */
	private int id;
	
	private String name;
	
	/** The type of the route. */
	private int ticketId;
	
	/** The start of the route. */
	private String startName;
	
	/** The end of the route. */
	private String endName;
	
	/** The route stations. */
	// position, stationId
	private Map<Integer, Integer> stationIds;
	
	public Route() {
		this.stationIds = new HashMap<Integer, Integer>();
	}
	
	/**
	 * Instantiates a new route.
	 * 
	 * @param id the route id
	 * @param type the route type
	 * @param start the route start
	 * @param end the route end
	 */
	public Route(int id, String name, int ticketId, String start, String end){
		this.id = id;
		this.ticketId = ticketId;
		this.startName = start;
		this.endName = end;
		this.stationIds = new HashMap<Integer, Integer>();
	}

	/**
	 * Adds a station to the route.
	 * 
	 * @param position the position of the station
	 * @param station the station itself
	 * 
	 * @return true, if successful
	 */
	public void addStation(int position, Station station){
		stationIds.put(position, station.getId());
	}
	
	public void addStation(int position, int stationId){
		stationIds.put(position, stationId);
	}
	
	public boolean containsStation(int cmpStationId){
		for(int stationId : stationIds.values()){
			if(stationId == cmpStationId )
				return true;
		}
		
		return false;
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

	public String getName() {
		return name;
	}


	
	/**
	 * Gets the position of a station.
	 * 
	 * @param station the station
	 * 
	 * @return the position of a station
	 */
	public Integer getPositionOfStation(Station station){
		for (Integer position : stationIds.keySet()){
			if (stationIds.get(position).equals(station.getId())) return position;
		}
		return -1;
	}
	
	/**
	 * Gets the start of this route.
	 * 
	 * @return the start
	 */
	public String getStart() {
		return startName;
	}
	
	/**
	 * Gets the stations belongs to this route.
	 * 
	 * @return the stations
	 */
	public Map<Integer, Integer> getStationIds() {
		return stationIds;
	}

	/**
	 * Gets the type of this route.
	 * 
	 * @return the type
	 */
	public int getTicketId() {
		return this.ticketId;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the type of this route.
	 * 
	 * @param type the new type
	 */
	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}



	/**
	 * Sets the start of this route.
	 * 
	 * @param start the new start
	 */
	public void setStart(String start) {
		this.startName = start;
	}

	/**
	 * Gets the end of this route.
	 * 
	 * @return the end
	 */
	public String getEnd() {
		return endName;
	}

	/**
	 * Sets the end of this route.
	 * 
	 * @param end the new end
	 */
	public void setEnd(String end) {
		this.endName = end;
	}



	/**
	 * Sets the stations belong to this route.
	 * 
	 * @param stops the stations of this route
	 */
	public void setStations(Map<Integer, Integer> stops) {
		this.stationIds = stops;
	}
	
	/**
	 * Gets the next stations of this route.
	 * 
	 * @param station the stations to get the neighbors from.
	 * 
	 * @return the neighbor stations of 'station' (in this Route) in an ArrayList.
	 */
	public ArrayList<Integer> getNextStationIds(int stationId){
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		/** Return null, if station does not belong to route. **/
		if (!stationIds.values().contains(stationId)) return list;
		
		int position=-1;		
		for (int pos : stationIds.keySet()){
			if (stationIds.get(pos) == stationId) {
				position=pos;
				break;
			}
		}
		
		/** Return null, if station was not found in the HashMap **/
		if (position==-1) return list;
		
		/** Add neighbor stations to the list. **/
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
	
	public ArrayList<Integer> getNextStationIds(Station station){
		return getNextStationIds(station.getId());
	}

	@Override
	public String toString() {
		String stations = "";
		
		for(Map.Entry<Integer, Integer> s : stationIds.entrySet()){
			stations += " " + s.getKey() + ";" + s.getValue();
		}
		
		return "Route [id=" + id + ", name=" + name + ", ticketId=" + ticketId
				+ ", startName=" + startName + ", endName=" + endName
				+ ", stationIds=" + stations + "]";
	}
}
