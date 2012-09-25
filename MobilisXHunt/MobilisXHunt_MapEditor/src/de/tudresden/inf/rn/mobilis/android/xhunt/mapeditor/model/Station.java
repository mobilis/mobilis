
package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model;

import org.jdesktop.swingx.mapviewer.GeoPosition;


/**
 * The Class Station.
 */
public class Station {
	
	/** The geo location of this station. */
	private GeoPoint mGeoPoint;
	
	/** The name of this station. */
	private String name;
	
	/** The abbrevation. */
	private String abbrevation;
	
	/** The id of this station. */
	private int id;
	
	/** If this station is reachable from the current station. */
	private boolean reachableFromCurrentStation;
	
	/**
	 * Instantiates a new station.
	 */
	public Station() {}
	
	/**
	 * Instantiates a new station.
	 *
	 * @param id the id of the station
	 * @param abbrev the abbrev
	 * @param name the name of this station
	 * @param longitudeMicroDegrees the longitude in micro degrees
	 * @param latitudeMicroDegrees the latitude in micro degrees
	 */
	public Station(int id, String abbrev, String name, int longitudeMicroDegrees, int latitudeMicroDegrees) {	
		this.id = id;
		this.abbrevation = abbrev;
		this.name = name;
		this.mGeoPoint = new GeoPoint(latitudeMicroDegrees, longitudeMicroDegrees);
		this.reachableFromCurrentStation = false;
	}
	
	/**
	 * Instantiates a new station.
	 *
	 * @param id the id of this station
	 * @param abbrev the abbrev
	 * @param name the name of this station
	 * @param geopoint the geolocation of this station
	 */
	public Station(int id, String abbrev, String name, GeoPoint geopoint) {
		this.id = id;
		this.abbrevation = abbrev;
		this.name = name;
		this.mGeoPoint = geopoint;
		this.reachableFromCurrentStation = false;
	}
	
	/**
	 * Instantiates a new station.
	 *
	 * @param id the id
	 * @param abbrev the abbrev
	 * @param name the name
	 * @param geoPos the geo pos
	 */
	public Station(int id, String abbrev, String name, GeoPosition geoPos) {
		this.id = id;
		this.abbrevation = abbrev;
		this.name = name;
		this.reachableFromCurrentStation = false;
		
		setGeoPosition(geoPos);
	}
	
	/**
	 * Gets the abbrevation.
	 *
	 * @return the abbrevation
	 */
	public String getAbbrevation() {
		return abbrevation;
	}

	/**
	 * Sets the abbrevation.
	 *
	 * @param abbrevation the new abbrevation
	 */
	public void setAbbrevation(String abbrevation) {
		this.abbrevation = abbrevation;
	}

	/**
	 * Get the GeoPoint of a station, which represents the location.
	 * 
	 * @return the geo point of this station
	 */
	public GeoPoint getGeoPoint() {
		return mGeoPoint;
	}
	
	/**
	 * Sets the geo point.
	 *
	 * @param geoPoint the new geo point
	 */
	public void setGeoPoint(GeoPoint geoPoint) {
		this.mGeoPoint = geoPoint;
	}
	
	/**
	 * Sets the geo point.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	public void setGeoPoint(int latitude, int longitude) {
		this.mGeoPoint = new GeoPoint(latitude, longitude);
	}
	
	/**
	 * Sets the geo position.
	 *
	 * @param geoPos the new geo position
	 */
	public void setGeoPosition(GeoPosition geoPos){
		this.mGeoPoint = new GeoPoint((int)(geoPos.getLatitude() * 1E6),
				(int)(geoPos.getLongitude() * 1E6));
	}

	/**
	 * Get the id of the station.
	 * 
	 * @return string identifier of the station
	 */
	public int getId() {
		return id;
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
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get the name of the station.
	 * 
	 * @return name of the Station
	 */
	public String getName() {
		return name;
	}

	
	/**
	 * Returns true if the Station is reachable from the station the player
	 * is currently located at. false otherwise.
	 * Used to draw the StationSignOverlay differently for reachable and
	 * unreachable stations.	 *
	 * 
	 * @return true if station is reachable from current station. false otherwise.
	 */
	public boolean isReachableFromCurrentStation() {
		return reachableFromCurrentStation;
	}
	
	/**
	 * Sets the reachable from current station.
	 * 
	 * @param reachableFromCurrentStation the new reachable from current station
	 */
	public void setReachableFromCurrentStation(boolean reachableFromCurrentStation) {
		this.reachableFromCurrentStation = reachableFromCurrentStation;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if(other == this) return true;
		if(!(other instanceof Station)) return false;		
		Station s = (Station) other;
		if (this.getId() == s.getId()) return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Station [mLatitude=" + mGeoPoint.getLatitudeE6() + ", mLongitude=" + mGeoPoint.getLongitudeE6()
				+ ", name=" + name + ", abbrevation=" + abbrevation + ", id="
				+ id + ", reachableFromCurrentStation="
				+ reachableFromCurrentStation + "]";
	}
	

}
