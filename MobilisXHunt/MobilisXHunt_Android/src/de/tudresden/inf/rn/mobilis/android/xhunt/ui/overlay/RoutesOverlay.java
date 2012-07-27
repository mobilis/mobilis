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

import java.util.HashMap;
import java.util.Map;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import de.tudresden.inf.rn.mobilis.android.xhunt.activity.XHuntMapActivity;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Route;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.RouteManagement;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Station;
import de.tudresden.inf.rn.mobilis.android.xhunt.service.XHuntService;

/**
 * The Class RoutesOverlay provides the visual routes of the game map.
 */
public class RoutesOverlay extends Overlay {
	
	/** Identifier for the Log outputs. */
	public static final String TAG = "RoutesOverlay";
	
	/** The pair of path and paint (color etc) for each route. */
	private HashMap<Path, Paint> routePaths;
	
	/** The XHuntMapActivity. */
	private XHuntMapActivity mMapActivity;
	
	/** The RouteManagement. */
	private RouteManagement mRouteManagement;
	
		
	/**
	 * Instantiates a new RoutesOverlay.
	 *
	 * @param mapActivity the XHuntMapActivity
	 * @param mapview the mapview of the XHuntMapActivity
	 * @param xhuntService the XHuntService
	 */
	public RoutesOverlay(XHuntMapActivity mapActivity, MapView mapview, XHuntService xhuntService) {
		super();
		
		this.mRouteManagement = xhuntService.getCurrentGame().getRouteManagement();
		routePaths = new HashMap<Path, Paint>();
		this.mMapActivity = mapActivity;
		
		updatePath(mapview);
	}
	
	/**
	 * Clear all paths.
	 */
	public void clearPaths(){
		this.routePaths.clear();
	}
		
	/* (non-Javadoc)
	 * @see com.google.android.maps.Overlay#draw(android.graphics.Canvas, com.google.android.maps.MapView, boolean)
	 */
	public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
        updatePath(mapView);
    
        if(routePaths.size() > 0){
        	for ( Map.Entry<Path, Paint> entry : routePaths.entrySet()){
        		canvas.drawPath(entry.getKey(), entry.getValue()) ;
        	}
        }
	}
	
	/**
	 * Gets the stroke width related to the current zoomlevel of the map.
	 *
	 * @param currentZoomLvl the current zoomlevel of the map
	 * @return the stroke width
	 */
	private int getStrokeWidth(int currentZoomLvl){
		switch(currentZoomLvl){
			case 13: return 3;
			case 14: return 5;
			case 15: return 7;
			case 16: return 9;
			default: return currentZoomLvl > 16 ? 11 : 3;
		}
	}
	
	/**
	 * Updates the path for the overlay.
	 *
	 * @param mapView the MapView
	 */
	public void updatePath(MapView mapView){

		if(routePaths != null){
			routePaths.clear();
		}

    	Point pointStation = new Point();  
		boolean first;

		for (Route r : mRouteManagement.getRoutes().values()) { 
			Path path = new Path();      	         	 
			first = true;
			
			for (int stationId : r.getStationIds().values()) {
				Station station = mRouteManagement.getStationById(stationId);				
				if (station==null) Log.e(TAG, "station==null");
				
				mapView.getProjection().toPixels(station.getGeoPoint(), pointStation);
				
				if (first) {
					path.moveTo(pointStation.x, pointStation.y);
					first=false;
				} else {        		         		 
					path.lineTo(pointStation.x, pointStation.y);
				}        		 
			}
			Paint paint = r.getPathPaint();
			paint.setStrokeWidth(getStrokeWidth(mMapActivity.getCurrentZoomLevel()));
			
			routePaths.put(path, paint);
		}
	}
	
}
