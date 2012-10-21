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
package de.tudresden.inf.rn.mobilis.android.xhunt.proxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.android.maps.GeoPoint;

import de.tudresden.inf.rn.mobilis.android.xhunt.Const;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Station;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.XHuntPlayer;
import de.tudresden.inf.rn.mobilis.android.xhunt.service.XHuntService;

/**
 * The Class GPSProxy is used to update the players current position.
 */
public class GPSProxy {
	
	/** The Constant TAG for logging. */
	public static final String TAG = "GPSProxy";

	/** The Constant INTENT_LOCATION_CHANGED. */
	public static final String INTENT_LOCATION_CHANGED 
		= "de.tudresden.inf.rn.mobilis.android.xhunt.proxy.GPSProxy.LocationChanged";
	
	/** The current location. */
	private Location mCurrentLocation;
	
	/** The LocationManager. */
	private LocationManager mLocationManager;
	
	/** True if GPS is up. false if not (just for testing purposes). */
	private boolean mIsGpsRunning;
	
	/** The thread who is listening for location changes. */
	private LocationListenerThread mLocationListener;
	
	/** The min distance (in meters) before a new GPS fix is needed. */
	float mGpsMinDistance = 2;
	
	/** The min distance (in meters) before a new Network Provider location fix is needed. */
	float mNwpMinDistance = 2;
	
	/** The time in milliseconds before a new GPS fix is needed. */
	long mGpsMinTime = 5 * 1000;
	
	/** The time in milliseconds before a new Network Provider location fix is needed. */
	long mNwpMinTime = 5 * 1000;
	
	/** The time in milliseconds after which a location fix is considered as old */
	int locationExpireTime = 10 * 1000;
	
	/** The applications context. */
	private Context mContext;
	
	/** The formatter to format the date. */
	private DateFormat mDateFormatter;
	
	/** The GPX file for logging movement of the player in a file (without GPX header). */
	private File mGpxFile;
	
	/** True if logging of movement is active. */
	private boolean mLogTracks = false;
	
	
	/**
	 * Instantiates a new GPSProxy.
	 *
	 * @param ctx the applications context
	 */
	public GPSProxy(Context ctx){
		this.mContext = ctx;
		
		mLocationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		mIsGpsRunning = false;
		
		mDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		
		// TODO: need to put an options in SettingsActivity for toggling logging of tracks
		mLogTracks = true;
		
		initGpx();
	}
	
	/**
	 * Gets the current location.
	 *
	 * @return the current location
	 */
	public Location getCurrentLocation(){
		return mCurrentLocation;
	}
	
	/**
	 * Gets the current location as GeoPoint.
	 *
	 * @return the current location as GeoPoint
	 */
	public GeoPoint getCurrentLocationAsGeoPoint(){
		return parseLocation(mCurrentLocation);
	}
	
	/**
	 * Inits the GPX logging. This function will create a new folder and file with 
	 * the current timestamp.
	 */
	private void initGpx(){
		File sdFolder = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "xhunt");
		if(!sdFolder.isDirectory())
			sdFolder.mkdir();
		
		File gpxFolder = new File(sdFolder.getAbsoluteFile(), "gpx");
		if(!gpxFolder.isDirectory())
			gpxFolder.mkdir();
		
		mGpxFile = new File(gpxFolder.getAbsoluteFile(), System.currentTimeMillis() + ".gpx");
	}

	/**
	 * Parses the location as GeoPoint.
	 *
	 * @param loc the locaion
	 * @return the GeoPoint
	 */
	public GeoPoint parseLocation(Location loc) {
		return loc != null
			? new GeoPoint((int)(loc.getLatitude() * 1E6), (int)(loc.getLongitude() * 1E6))
			: null;
	}
	
	/**
	 * Restart GPS.
	 *
	 * @param ctx the applications context
	 */
	public void restartGps(Context ctx){
		this.mContext = ctx;
		
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		
		startGps();
	}
	
	/**
	 * Send location changed broadcast to inform all listening components.
	 */
	public void sendLocationChangedBroadcast(){
		GeoPoint currentGeoPoint = parseLocation(mCurrentLocation);
		
		Intent i = new Intent(INTENT_LOCATION_CHANGED);
		i.putExtra(Const.BUNDLE_KEY_LOCATION_CHANGED_LAT, currentGeoPoint.getLatitudeE6());
		i.putExtra(Const.BUNDLE_KEY_LOCATION_CHANGED_LON, currentGeoPoint.getLongitudeE6());
		
		mContext.sendBroadcast(i);
	}
	
	/**
	 * Toggle the logging for tracks.
	 *
	 * @param log the new on logging tracks
	 */
	public void setOnLoggingTracks(boolean log){
		this.mLogTracks = log;
	}
	
	/**
	 * Start GPS.
	 */
	public void startGps() {
		if(!mIsGpsRunning) {
			SharedPreferences prefs = mContext.getSharedPreferences(Const.SHARED_PREF_KEY_FILE_NAME, Context.MODE_PRIVATE);
			String key = mContext.getResources().getString(R.string.bundle_key_settings_staticmode);
			boolean staticMode = prefs.getBoolean(key, false);

			if(staticMode) {
				List<Station> allStations = null;
				
				if(mContext instanceof XHuntService) {
					allStations = ((XHuntService) mContext).getCurrentGame().getRouteManagement().getStationsAsList();
					if((allStations != null) && (allStations.size() != 0))
						setRandomLocation(allStations);
				}
			}
			
			else if(!staticMode) {
				if(mLocationListener != null){
					mLocationManager.removeUpdates(mLocationListener);
					mLocationListener.interrupt();
				}
				
				mLocationListener = new LocationListenerThread();
				mLocationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, mGpsMinTime, mGpsMinDistance, mLocationListener);
				mLocationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, mNwpMinTime, mNwpMinDistance, mLocationListener);
				
				Location lastGpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				Location lastNwpLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				
				if((lastGpsLocation==null) && (lastNwpLocation!=null))
					mCurrentLocation = lastNwpLocation;
				
				if((lastNwpLocation==null) && (lastGpsLocation!=null))
					mCurrentLocation = lastGpsLocation;
				
				if((lastGpsLocation!=null) && (lastNwpLocation!=null)) {
					int twoMinutes = 2 * 60 * 1000;
					long timeDiff = lastNwpLocation.getTime() - lastGpsLocation.getTime();
					
					//if GPS less than 2min older than NWP: use GPS location
					if(timeDiff>0 && timeDiff<twoMinutes)
						mCurrentLocation = lastGpsLocation;
					
					//if GPS more than 2min older than NWP: use NWP location
					if(timeDiff>0 && timeDiff>twoMinutes)
						mCurrentLocation = lastNwpLocation;
					
					//if GPS newer than NWP: use GPS location
					if(timeDiff <= 0)
						mCurrentLocation = lastGpsLocation;
				}
			}
			
			mIsGpsRunning = true;
		}
	}
	
	
	/**
	 * Returns the coordinates of a random location as a Location object.
	 * Uses a list of all stations to calculate the geographic bounds.
	 * Needed as a starting point when playing in static mode.
	 *  
	 * @param allStations the list of all stations of the area
	 * @return a location object representing a random location
	 */
	private void setRandomLocation(List<Station> allStations) {

	 	int lat_min = Integer.MAX_VALUE;
		int long_min = Integer.MAX_VALUE;
		int lat_max = Integer.MIN_VALUE;
		int long_max = Integer.MIN_VALUE;
		GeoPoint currStation;
		
		for(Station station : allStations) {
			currStation = station.getGeoPoint();
			
			if(currStation.getLatitudeE6() < lat_min)
				lat_min = currStation.getLatitudeE6();
			if(currStation.getLatitudeE6() > lat_max)
				lat_max = currStation.getLatitudeE6();
			if(currStation.getLongitudeE6() < long_min)
				long_min = currStation.getLongitudeE6();
			if(currStation.getLongitudeE6() > long_max)
				long_max = currStation.getLongitudeE6();
		}
		
		Random rndm = new Random();
		int lat_rndm = rndm.nextInt(lat_max-lat_min +1) + lat_min;
		int long_rndm = rndm.nextInt(long_max-long_min +1) + long_min;

		setLocation(lat_rndm, long_rndm);	
		sendLocationChangedBroadcast();
	}
	
	
	/**
	 * Sets a location for testing purposes.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	public void setLocation(int latitude, int longitude){
		mCurrentLocation = new Location(LocationManager.GPS_PROVIDER);
		mCurrentLocation.setLatitude((double)latitude / 1E6);
		mCurrentLocation.setLongitude((double)longitude / 1E6);
		
		Log.v(TAG, "setLoc manual to: " + mCurrentLocation);
	}
	
	/**
	 * Stop GPS.
	 */
	public void stopGps(){
		if(mLocationManager != null && mLocationListener != null)
			mLocationManager.removeUpdates(mLocationListener);
		
		mIsGpsRunning = false;
	}
	
	/**
	 * Write location to file for logging purposes. Location will be formatted to 
	 * match the GPX standards without providing a header in GPX file (have to be 
	 * insert manually).
	 *
	 * @param location the location to log
	 */
	private void writeLocationToFile(Location location){	
		// element '<trkpt>' contains the location(latitude, longitude, altitude(in '<ele>')) 
		//and a timestamp ('<time>') when the player visits this location
		String track = "<trkpt lat=\"" 
				+ location.getLatitude() 
				+ "\" lon=\"" + location.getLongitude() 
				+ "\">\n<ele>" + (int)location.getAltitude() 
				+ "</ele>\n<time>" 
				+ mDateFormatter.format(location.getTime()) 
				+ "</time>\n</trkpt>\n";
		
		// writes the data to file
	    FileOutputStream os;
		try {
			os = new FileOutputStream(mGpxFile, true);

			OutputStreamWriter out = new OutputStreamWriter(os);
			out.write(track);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * The Class LocationListenerThread is listening for location updates.
	 */
	private class LocationListenerThread extends Thread implements LocationListener {
		
		/* (non-Javadoc)
		 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
		 */
		@Override
		public void onLocationChanged(Location location) {
			XHuntPlayer myPlayer = (mContext instanceof XHuntService)
					? ((XHuntService)mContext).getCurrentGame().getPlayerByJID(((XHuntService)mContext).getMXAProxy().getXmppJid())
					: null;
			
			// use new location only if it's from GPS or if current location is old/null
			boolean useNewLocation =
					((mCurrentLocation == null)
						|| (location.getProvider().equals(LocationManager.GPS_PROVIDER))
						|| (System.currentTimeMillis() - mCurrentLocation.getTime() > locationExpireTime));
			
			// update location only if player hasn't reached his target yet
			boolean hasReachedTarget = (myPlayer != null) ? myPlayer.getReachedTarget() : false;
			
			// if player has reached his target, set him onto the middle of the station icon
			if(hasReachedTarget) {
				Station target = ((XHuntService)mContext).getCurrentGame().getRouteManagement().getStationById(
						myPlayer.getCurrentTargetId());

				if((target != null) && (target.getLatitude() != myPlayer.getGeoLocation().getLatitudeE6())
						&& (target.getLongitude() != myPlayer.getGeoLocation().getLongitudeE6())) {
					setLocation(target.getLatitude(), target.getLongitude());
					sendLocationChangedBroadcast();
				}
			}
			
			// else update real position
			if(useNewLocation && !hasReachedTarget) {
				mCurrentLocation = location;	
				sendLocationChangedBroadcast();
				if(mLogTracks)
					writeLocationToFile(location);	
			}
		}

		/* (non-Javadoc)
		 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
		 */
		@Override
		public void onProviderDisabled(String provider) {}

		/* (non-Javadoc)
		 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
		 */
		@Override
		public void onProviderEnabled(String provider) {}

		/* (non-Javadoc)
		 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
//			String[] stat = {"Out of Service","Temporarily Unavailable","Available"};
			
//			Log.v(TAG, "Provider \'" + provider + "\' changed status to: " + stat[status]);
		}
	}
}
