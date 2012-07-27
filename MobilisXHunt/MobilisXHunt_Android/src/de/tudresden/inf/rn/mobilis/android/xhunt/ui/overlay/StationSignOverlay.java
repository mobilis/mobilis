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
package de.tudresden.inf.rn.mobilis.android.xhunt.ui.overlay;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import de.tudresden.inf.rn.mobilis.android.xhunt.R;
import de.tudresden.inf.rn.mobilis.android.xhunt.activity.XHuntMapActivity;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Station;

/**
 * The Class StationSignOverlay is used to display where the stations are located at. 
 * If a station is green, the own player can reach this station from his current position.
 */
public class StationSignOverlay extends ItemizedOverlay<OverlayItem> {

	/** The station overlays. */
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	/** The stations. */
	private ArrayList<Station> stations;
	
	/** The XHuntMapActivity. */
	private XHuntMapActivity mMapActivity;

	/**
	 * Instantiates a new station sign overlay.
	 * 
	 * @param stations the stations we want to draw
	 * @param mapActivity the mapactivity wich we need for the zoomlevels
	 */
	public StationSignOverlay(ArrayList<Station> stations, XHuntMapActivity mapActivity) {
		// Set a dummy Marker as default. The Markers will later on be changed according to
		// the zoom level and the fact, if the station is reachable from the current station.
		super(boundCenterBottom(mapActivity.getResources().getDrawable(R.drawable.spacer)));
		
		this.stations=stations;
		this.mMapActivity=mapActivity;
		update();
	}

	/**
	 * Sets the marker of station.
	 * 
	 * @param s the station
	 * @param marker the marker for the station
	 */
	public void setMarkerOfStation(Station s, Drawable marker) {
		if (s.getOverlayItem()==null)
			s.setOverlayItem( new OverlayItem(s.getGeoPoint(), s.getName(), s.getAbbreviation()) );
		s.getOverlayItem().setMarker(boundCenterBottom(marker));		
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#createItem(int)
	 */
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#size()
	 */
	@Override
	public int size() {
		return mOverlays.size();
	}
	
	/**
	 * Adds an overlayitem.
	 * 
	 * @param overlay the overlayitem
	 */
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
	 */
	@Override
	protected boolean onTap(int i) {
		Station s = stations.get(i);
		if (s == null) return false;
		
		
		if (s.isReachableFromCurrentStation()) {
			// Station is reachable. Call the method to open the ContextMenu
			mMapActivity.reachableStationTapped(s);
		} else {
			// Station is not reachable.
			// Simply show a short message with the name of the station.
			Toast.makeText(mMapActivity, s.getName(), Toast.LENGTH_SHORT).show();		
		}
		
		return true;		
	}
	
	/**
	 * Update the station overlays.
	 */
	public void update(){
		mOverlays.clear();
		
		int zoomLevel = mMapActivity.getCurrentZoomLevel();
		Drawable d_normal, d_reachable;	
		
		// Set the Markers according to the zoom level
		d_normal    = mMapActivity.getResources().getDrawable(R.drawable.station_10px);
		d_reachable = mMapActivity.getResources().getDrawable(R.drawable.station_green_10px);
		if (zoomLevel==14) {
			d_normal=mMapActivity.getResources().getDrawable(R.drawable.station_20px);
			d_reachable = mMapActivity.getResources().getDrawable(R.drawable.station_green_20px);
		} else if (zoomLevel==15) {
			d_normal=mMapActivity.getResources().getDrawable(R.drawable.station_30px);
			d_reachable = mMapActivity.getResources().getDrawable(R.drawable.station_green_30px);
		} else if (zoomLevel==16) {
			d_normal=mMapActivity.getResources().getDrawable(R.drawable.station_40px);
			d_reachable = mMapActivity.getResources().getDrawable(R.drawable.station_green_40px);			
		} else if (zoomLevel>16) {
			d_normal=mMapActivity.getResources().getDrawable(R.drawable.station_50px);
			d_reachable = mMapActivity.getResources().getDrawable(R.drawable.station_green_50px);
		}
		
		// Iterates thru all stations and creates an overlayitem
		OverlayItem overlayitem;
		for(Station s: stations) {
			overlayitem = s.getOverlayItem();
			if (overlayitem==null) overlayitem = new OverlayItem(s.getGeoPoint(), s.getName(), s.getAbbreviation());
			
			if (s.isReachableFromCurrentStation()) {
				// Use the alternate marker for reachable stations
				overlayitem.setMarker(boundCenterBottom(d_reachable));
			} else {	
				// Use the normal marker for unreachable stations
				overlayitem.setMarker(boundCenterBottom(d_normal));				
			}
			s.setOverlayItem(overlayitem);
			mOverlays.add(overlayitem);
		}			
		populate();
	}

}
