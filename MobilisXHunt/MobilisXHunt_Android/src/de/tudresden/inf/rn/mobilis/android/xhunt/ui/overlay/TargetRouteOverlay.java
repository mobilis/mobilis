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

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Point;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import de.tudresden.inf.rn.mobilis.android.xhunt.model.XHuntPlayer;
import de.tudresden.inf.rn.mobilis.android.xhunt.service.XHuntService;

/**
 * The Class TargetRouteOverlay.
 */
public class TargetRouteOverlay extends Overlay {
	
	/** The paint which sets up the style of the target routes. */
	private Paint paint;
	
	/** The path effect is needed to make the PathDashPathEffect. */
	private PathEffect pathEffect;
	
	/** The XHuntService. */
	private XHuntService mXhuntService;
	
	/**
	 * Instantiates a new TargetRouteOverlay.
	 *
	 * @param xhuntService the XHuntService
	 */
	public TargetRouteOverlay(XHuntService xhuntService){
		this.mXhuntService = xhuntService;
		
		this.paint = new Paint();
		// path effect to display path to target station
		this.pathEffect = new PathDashPathEffect(makePathDash(), 20, 20,
                PathDashPathEffect.Style.ROTATE);
		
		paint.setStrokeWidth(3);
		paint.setStyle(Paint.Style.STROKE);
		paint.setAlpha(100);
		paint.setPathEffect(pathEffect);
		
	}
	
	 /**
 	 * Make path dash which represents the arrow figure.
 	 * 
 	 * @return the path shape
 	 */
 	private static Path makePathDash() {
         Path p = new Path();
         p.moveTo(4, 0);
         p.lineTo(0, -4);
         p.lineTo(8, -4);
         p.lineTo(12, 0);
         p.lineTo(8, 4);
         p.lineTo(0, 4);
         return p;
     }
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.Overlay#draw(android.graphics.Canvas, com.google.android.maps.MapView, boolean)
	 */
	public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);            
       
        Point currentLoc = new Point();
        Point targetLoc = new Point();
        
        // draw a path to the current target for each player if they do not have already 
        // reached it
        for(XHuntPlayer player : mXhuntService.getCurrentGame().getGamePlayers().values()){

        	if(!player.getReachedTarget() 
        			&& mXhuntService.getCurrentGame().getRouteManagement()
        				.getStationById(player.getCurrentTargetId()) != null
        			&& player.isCurrentTargetFinal()
        			&& player.getGeoLocation() != null){
		        mapView.getProjection().toPixels(player.getGeoLocation(), currentLoc);
		        mapView.getProjection().toPixels(mXhuntService.getCurrentGame()
		        		.getRouteManagement().getStationById(player.getCurrentTargetId()).getGeoPoint(), targetLoc);
		        
		        Path path = new Path();
		        path.moveTo(currentLoc.x, currentLoc.y);
		        path.lineTo(targetLoc.x, targetLoc.y);
		        
		        paint.setColor(player.getPlayerColorID());
		        
		        canvas.drawPath(path, paint);
        	}
        }
        
	}

}
