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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.graphics.Paint;
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
	private HashMap<float[], Paint> routePaths;
	
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
		routePaths = new HashMap<float[], Paint>();
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
        	for ( Map.Entry<float[], Paint> entry : routePaths.entrySet()){
//        		canvas.drawPath(entry.getKey(), entry.getValue());
        		canvas.drawLines(entry.getKey(), entry.getValue());
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

		Collection<Route> routes = mRouteManagement.getRoutes().values();
		int currentZoomLevel = mMapActivity.getCurrentZoomLevel();
		for (Route r : routes) {
			/*
			 *  TODO: the commented lines in this loop contain some more potential performance improvements
			 *  by using caching of paths for every zoom level.
			 *  However, there seems to be a problem with the scaling of cached paths.
			 */
//			float[] path = r.getPathForZoomlevel(currentZoomLevel);
			float[] path = null;
//			if (path == null) {
				int pathArrayLength = (r.getStationIds().size() - 1) * 4;
				path = new float[pathArrayLength];
				Point start = new Point();
				
				LinkedList<Integer> stationIds = r.getStationIds();
				int i = 0;
				for (int stationId : stationIds) {
					Station station = mRouteManagement.getStationById(stationId);				
					if (station==null) Log.e(TAG, "station==null");
					
					mapView.getProjection().toPixels(station.getGeoPoint(), pointStation);
					
					if (i == 0) {
						start.set(pointStation.x, pointStation.y);
						path[i] = pointStation.x;
						path[i+1] = pointStation.y;
						i += 2;
					} else if (i == pathArrayLength - 2) {
						path[i] = pointStation.x;
						path[i+1] = pointStation.y;
						i += 2;
					} else {
						path[i] = pointStation.x;
						path[i + 1] = pointStation.y;
						path[i + 2] = pointStation.x;
						path[i + 3] = pointStation.y;
						i += 4;
					}        		 
				}
				
				// cache origin (e.g. (0,0) is start) version of path
//				float[] originPath = path.clone();
//				for (int j = 0; j < originPath.length; j+=2) {
//					originPath[j] -= start.x;
//					originPath[j+1] -= start.y;
//				}
//				r.setPathForZoomlevel(originPath, currentZoomLevel);
//			} else {
//				Point start = new Point();
//				int startStationId = r.getStationIds().iterator().next();
//				mapView.getProjection().toPixels(mRouteManagement.getStationById(startStationId).getGeoPoint(), start);
//					
//				// translate origin path to correct position
//				for (int j = 0; j < path.length; j+=2) {
//					path[j] += start.x;
//					path[j+1] += start.y;
//				}
//			}
			Paint paint = r.getPathPaint();
			paint.setStrokeWidth(getStrokeWidth(currentZoomLevel));
			
			routePaths.put(path, paint);
		}
	}
	
}
