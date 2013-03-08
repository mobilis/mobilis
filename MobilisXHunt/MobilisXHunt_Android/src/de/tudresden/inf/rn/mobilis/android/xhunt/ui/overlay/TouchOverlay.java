package de.tudresden.inf.rn.mobilis.android.xhunt.ui.overlay;

import android.view.MotionEvent;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import de.tudresden.inf.rn.mobilis.android.xhunt.activity.XHuntMapActivity;

public class TouchOverlay extends Overlay {
    private int lastZoomLevel = -1;
    private XHuntMapActivity mapActivity;
    
    public TouchOverlay(XHuntMapActivity mapActivity) {
		this.mapActivity = mapActivity;
	}

    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapview) {
        if (event.getAction() == 1) {
            if (mapActivity.getCurrentZoomLevel() != lastZoomLevel) {
                onZoom();
                lastZoomLevel = mapActivity.getCurrentZoomLevel();
            }
        }
        return false;
    }

    public void onZoom() {
    	mapActivity.updateMapData(); //act on zoom level change event here
    }
}

