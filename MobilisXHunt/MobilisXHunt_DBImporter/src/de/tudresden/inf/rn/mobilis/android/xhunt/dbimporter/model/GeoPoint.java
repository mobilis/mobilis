
package de.tudresden.inf.rn.mobilis.android.xhunt.dbimporter.model;


/**
 * The Class GeoPoint.
 */
public class GeoPoint {
	
	/** The latitude. */
	private int latitude = 0;
	
	/** The longitude. */
	private int longitude = 0;

	
	/**
	 * Instantiates a new geo point.
	 */
	public GeoPoint() {}
	
	/**
	 * Instantiates a new geo point.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	public GeoPoint(int latitude, int longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/**
	 * Sets the latitude e6.
	 *
	 * @param latitude the new latitude e6
	 */
	public void setLatitudeE6(int latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * Gets the latitude e6.
	 *
	 * @return the latitude e6
	 */
	public int getLatitudeE6() {
		return latitude;
	}
	
	/**
	 * Sets the longitude e6.
	 *
	 * @param longitude the new longitude e6
	 */
	public void setLongitudeE6(int longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * Gets the longitude e6.
	 *
	 * @return the longitude e6
	 */
	public int getLongitudeE6() {
		return longitude;
	}
}
