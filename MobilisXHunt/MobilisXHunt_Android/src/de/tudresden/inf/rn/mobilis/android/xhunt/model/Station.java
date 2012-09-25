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
 * Represents a Bus-, Tram- or Railway-Station. 
 * @author Robert, Fanny
 */

package de.tudresden.inf.rn.mobilis.android.xhunt.model;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * The Class Station.
 */
public class Station {
	
	/** The geographical location of this station (latitude, longitude). */
	private GeoPoint mGeoPoint;
	
	/** The name of this station. */
	private String name;
	
	/** The abbreviation. */
	private String abbreviation;
	
	/** The id of this station. */
	private int id;
	
	/** The overlay item of this station to display it on the map. */
	private OverlayItem overlayItem;
	
	/** The status if this station is reachable from the players 
	 * current station. */
	private boolean reachableFromCurrentStation;
	
	/**
	 * Instantiates a new station.
	 */
	public Station() {}
	
	/**
	 * Instantiates a new station.
	 *
	 * @param id the id of the station
	 * @param abbrev the abbreviation of this station
	 * @param name the name of this station
	 * @param longitudeMicroDegrees the longitude in micro degrees
	 * @param latitudeMicroDegrees the latitude in micro degrees
	 */
	public Station(int id, String abbrev, String name, int longitudeMicroDegrees, int latitudeMicroDegrees) {	
		this.id = id;
		this.abbreviation = abbrev;
		this.name = name;
		this.mGeoPoint = new GeoPoint(latitudeMicroDegrees, longitudeMicroDegrees);
		this.overlayItem = null;
		this.reachableFromCurrentStation = false;
	}
	
	/**
	 * Instantiates a new station.
	 *
	 * @param id the id of this station
	 * @param abbrev the abbreviation of this station
	 * @param name the name of this station
	 * @param geopoint the geographical location of this station
	 */
	public Station(int id, String abbrev, String name, GeoPoint geopoint) {
		this.id = id;
		this.abbreviation = abbrev;
		this.name = name;
		this.mGeoPoint = geopoint;
		this.overlayItem = null;
		this.reachableFromCurrentStation = false;
	}
	
	/**
	 * Gets the abbreviation.
	 *
	 * @return the abbreviation
	 */
	public String getAbbreviation() {
		return abbreviation;
	}

	/**
	 * Sets the abbreviation.
	 *
	 * @param abbreviation the new abbreviation
	 */
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	/**
	 * Gets the GeoPoint of a station, which represents the location.
	 * 
	 * @return the geo point of this station
	 */
	public GeoPoint getGeoPoint() {
		return mGeoPoint;
	}
	
	/**
	 * Gets the latitude.
	 *
	 * @return the latitude
	 */
	public int getLatitude(){
		return mGeoPoint.getLatitudeE6();
	}
	
	/**
	 * Gets the longitude.
	 *
	 * @return the longitude
	 */
	public int getLongitude(){
		return mGeoPoint.getLongitudeE6();
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
	 * Gets the id of the station.
	 * 
	 * @return identifier of the station
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
	 * Gets the name of the station.
	 * 
	 * @return name of the Station
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the Stations OverlayItem on the map.
	 *
	 * @param overlayItem the new overlay item
	 */
	public void setOverlayItem(OverlayItem overlayItem) {
		this.overlayItem=overlayItem;
	}	
	
	/**
	 * Gets the Stations OverlayItem on the map.
	 * 
	 * @return the overlay item
	 */
	public OverlayItem getOverlayItem() {
		return overlayItem;
	}
	
	/**
	 * Returns true if the Station is reachable from the station where the player
	 * is currently located at, false otherwise.
	 * Used to draw the StationSignOverlay differently for reachable and
	 * unreachable stations.
	 * 
	 * @return true if station is reachable from current station, false otherwise.
	 */
	public boolean isReachableFromCurrentStation() {
		return reachableFromCurrentStation;
	}
	
	/**
	 * Sets the reachability for this station from the players 
	 * current station.
	 * 
	 * @param reachableFromCurrentStation true if station is 
	 * reachable from players current
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
				+ ", name=" + name + ", abbrevation=" + abbreviation + ", id="
				+ id + ", reachableFromCurrentStation="
				+ reachableFromCurrentStation + "]";
	}
}
