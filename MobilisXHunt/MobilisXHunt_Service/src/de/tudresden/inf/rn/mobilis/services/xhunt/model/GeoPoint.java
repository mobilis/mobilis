/*******************************************************************************
 * Copyright (C) 2010 Technische Universit�t Dresden
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
package de.tudresden.inf.rn.mobilis.services.xhunt.model;

/**
 * The Class GeoPoint represents a geographic point.
 */
public class GeoPoint {
	
	/** The latitude in microdegrees. */
	private int latitude = 0;
	
	/** The longitude in microdegrees. */
	private int longitude = 0;

	
	/**
	 * Instantiates a new GeoPoint.
	 */
	public GeoPoint() {}
	
	/**
	 * Instantiates a new GeoPoint.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	public GeoPoint(int latitude, int longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/**
	 * Sets the latitude in microdegrees (multiply degrees with 1E6).
	 *
	 * @param latitude the new latitude in microdegrees
	 */
	public void setLatitudeE6(int latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * Gets the latitude in microdegrees (multiply degrees with 1E6).
	 *
	 * @return the latitude in microdegrees
	 */
	public int getLatitudeE6() {
		return latitude;
	}
	
	/**
	 * Sets the longitude in microdegrees (multiply degrees with 1E6).
	 *
	 * @param longitude the new longitude in microdegrees
	 */
	public void setLongitudeE6(int longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * Gets the longitude in microdegrees (multiply degrees with 1E6).
	 *
	 * @return the longitude in microdegrees
	 */
	public int getLongitudeE6() {
		return longitude;
	}

}
