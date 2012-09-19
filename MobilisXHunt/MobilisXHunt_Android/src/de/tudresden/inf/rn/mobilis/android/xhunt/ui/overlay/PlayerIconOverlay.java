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

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import de.tudresden.inf.rn.mobilis.android.xhunt.R;
import de.tudresden.inf.rn.mobilis.android.xhunt.activity.XHuntMapActivity;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Station;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.XHuntPlayer;
import de.tudresden.inf.rn.mobilis.android.xhunt.service.XHuntService;

/**
 * The Class PlayerIconOverlay.
 */
public class PlayerIconOverlay extends ItemizedOverlay<OverlayItem> {
	
	/** The overlayitems of the players. */
	private ArrayList<OverlayItem> overlayIcons;
	
	/** The XHuntMapActivity. */
	private XHuntMapActivity mMapActivity;
	
	/** The MapView. */
	private MapView mapView;
	
	/** The player mr x. */
	private XHuntPlayer mrX;
	
	/** The overlayitem for mr x. */
	private OverlayItem mrXOverlayItem;
	
	/** The last known position of mr x. */
	private GeoPoint lastKnownPositionMrX;
	
	/** The m xhunt service. */
	private XHuntService mXhuntService;

	/**
	 * Instantiates a new player icon overlay.
	 *
	 * @param mapActivity the XHuntMapActivity
	 * @param mapView the MapView
	 * @param xhuntService the xhunt service
	 */
	public PlayerIconOverlay(XHuntMapActivity mapActivity, MapView mapView, XHuntService xhuntService) {
		super(boundCenterBottom(mapActivity.getResources().getDrawable(R.drawable.spacer)));
		
		this.mXhuntService = xhuntService;
		this.mMapActivity = mapActivity;
		this.mapView = mapView;
		
		overlayIcons = new ArrayList<OverlayItem>();
		updatePlayers();
		
		/** Look for mr x and save him **/
		mrX = mXhuntService.getCurrentGame().getMrX();
		
		if(mrX != null && mrX.getJid().equals(xhuntService.getMXAProxy().getXmppJid())){
			mrX = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
	 */
	@Override
	protected boolean onTap(int i) {
		if(i >= overlayIcons.size())
			return false;
		
		// Get the station under the playericon
		Station s = mXhuntService.getCurrentGame().getRouteManagement().getStationByLocation(overlayIcons.get(i).getPoint());
		
		if(s != null) {
			if(s.isReachableFromCurrentStation())
				mMapActivity.reachableStationTapped(s);
			else
				Toast.makeText(mMapActivity, s.getName(), Toast.LENGTH_SHORT).show();		
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#createItem(int)
	 */
	@Override
	protected OverlayItem createItem(int i) {
		return i < overlayIcons.size()
			? overlayIcons.get(i)
			: null;
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#size()
	 */
	@Override
	public int size() {
		return overlayIcons.size();
	}
	
	/**
	 * Updates a player in case of no location or something else.
	 * 
	 * @param player the player
	 */
	public void updatePlayer(XHuntPlayer player){
		boolean matched = false;
		for(OverlayItem o : overlayIcons){
			if(o.getTitle().equals(player.getJid())){
				overlayIcons.remove(o);
				matched = true;
			}
		}
		
		if(matched){
			OverlayItem o = new OverlayItem(player.getGeoLocation(), player.getJid(), player.getName());
			o.setMarker(boundCenterBottom(mMapActivity.getResources().getDrawable(player.getPlayerIconID())));	
			overlayIcons.add(o);
		}
		setLastFocusedIndex(-1);
		populate();
	}

	/**
	 * Update all playeroverlays.
	 */
	public void updatePlayers(){
		overlayIcons.clear();
		
		if(mrX != null && mrXOverlayItem != null){
			overlayIcons.add(mrXOverlayItem);
		}
		
		for(XHuntPlayer player : mXhuntService.getCurrentGame().getGamePlayers().values()){
			if(player.getGeoLocation() != null
					&& player.getGeoLocation().getLatitudeE6() != -1
					&& player.getGeoLocation().getLongitudeE6() != -1){
				/** Creates the playerovleray for each player **/
				OverlayItem oi = new OverlayItem(player.getGeoLocation(), player.getJid(), player.getName());
				oi.setMarker(boundCenterBottom(mMapActivity.getResources().getDrawable(player.getPlayerIconID())));
				overlayIcons.add(oi);

				Station currentTarget = mXhuntService.getCurrentGame().getRouteManagement().getStationById(player.getCurrentTargetId());
				/** Creates a ghost of a player wich points on the target **/
				if(!player.getReachedTarget() && currentTarget != null){
					OverlayItem o = new OverlayItem(currentTarget.getGeoPoint(), player.getJid() + "_ghost", player.getName());
					Drawable d = mMapActivity.getResources().getDrawable(player.getPlayerIconID()).mutate();
					d.setAlpha(75);
					o.setMarker(boundCenterBottom(d));
					overlayIcons.add(o);
				}
			}
			else if(player.isMrX() && mrX == null){
				mrX = player;
			}
		}

		setLastFocusedIndex(-1);
		populate();
	}
	
	/**
	 * Update mr.x visible state. If mr.x should be visible, he will be displayed at his 
	 * current target, else his last known station will be displayed with a transparent icon.
	 * 
	 * @param show true if mr.x should be shown
	 */
	public void updateMrX(boolean show){
		if(mrX != null
				&& !mrX.getJid().equals(mXhuntService.getMXAProxy().getXmppJid())){
			Station currentTargetMrX = mXhuntService.getCurrentGame().getRouteManagement().getStationById(mrX.getCurrentTargetId());			
		
			if(currentTargetMrX != null){
				if(show){					
					lastKnownPositionMrX = currentTargetMrX.getGeoPoint();
					mrXOverlayItem = new OverlayItem(currentTargetMrX.getGeoPoint(), mrX.getJid(), mrX.getName());
					mrXOverlayItem.setMarker(boundCenterBottom(mMapActivity.getResources().getDrawable(mrX.getPlayerIconID())));
				}
				else if(!show && lastKnownPositionMrX != null){
					mrXOverlayItem = new OverlayItem(lastKnownPositionMrX, mrX.getJid() + "_ghost", mrX.getName());
					Drawable d = mMapActivity.getResources().getDrawable(mrX.getPlayerIconID()).mutate();
					d.setAlpha(150);
					mrXOverlayItem.setMarker(boundCenterBottom(d));
				}
			}
		}
	}
	
	/**
	 * Invalidates the MapView
	 */
	public void invalidateMapView() {
		mapView.invalidate();
	}
}
