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
 * Activity that shows the Map of the city with all stations and its
 * connections as well as the actual positions of the players.
 */
package de.tudresden.inf.rn.mobilis.android.xhunt.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Display;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;

/**
 * The Class DialogDepartures.
 * 
 * !!! This class is available but current version there will be 
 * no departures provided by the server.
 */
public class DialogDepartures extends Dialog {
	
	/** The Constant DIALOG_ID. */
	public static final int DIALOG_ID = 301;
	
	/** The TableLayout which holds the departues. */
	private TableLayout mTableLayout;
	
	/** The applications context. */
	private Context mContext;
	
	/** The display of the players device. */
	private Display mDisplay;
	
	/** True if dialog already has content. */
	private boolean mHasContent = false;

	/**
	 * Instantiates a new DialogDepartures.
	 *
	 * @param context the context of the application
	 * @param display the display of the players device
	 */
	public DialogDepartures(Context context, Display display) {
		super(context);
		mContext = context;
		this.mDisplay = display;
		
		this.setContentView(R.layout.dialog_body);
		this.setTitle("Departures At: ");
    	
    	mTableLayout = (TableLayout)findViewById(R.id.dialog_list);
	}
	
	/**
	 * Instantiates a new dialog departures.
	 *
	 * @param context the context of the application
	 * @param display the display of the players device
	 * @param stationName the station name of the players current station
	 */
	public DialogDepartures(Context context, Display display, String stationName) {
		super(context);
		mContext = context;
		this.mDisplay = display;
		
		this.setContentView(R.layout.dialog_body);
		this.setTitle("Departures At: " + stationName);
    	
    	mTableLayout = (TableLayout)findViewById(R.id.dialog_list);
	}
	
	/**
	 * Clear content.
	 */
	public void clearContent(){
		mTableLayout.removeAllViews();
		mHasContent = false;
	}
	
	/**
	 * Add a departure.
	 *
	 * @param vehicleIcon the vehicle icon
	 * @param vehicleNumber the vehicle number
	 * @param direction the direction
	 * @param minutesLeft the minutes left
	 */
	public void addDeparture(Bitmap vehicleIcon, String vehicleNumber, String direction, String minutesLeft){
		// wrapper layout for the departure icon (a line in TableLayout)
		LinearLayout llIcon = new LinearLayout(mContext);
    	llIcon.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
    	
    	// ImageView with the departure type icon
    	ImageView imgVehicleIcon = new ImageView(mContext);
    	imgVehicleIcon.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	imgVehicleIcon.setImageBitmap(vehicleIcon);
    	
    	llIcon.addView(imgVehicleIcon);

    	// TextView with the vehicles number
    	TextView tvVehicleNumber = new TextView(mContext);
    	tvVehicleNumber.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	tvVehicleNumber.setText(vehicleNumber);
    	tvVehicleNumber.setPadding(5, 0, 0, 0);
    	
    	// TextView with the vehicles direction
    	TextView tvDirection = new TextView(mContext);
    	tvDirection.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	tvDirection.setText(direction);
    	tvDirection.setPadding(5, 0, 0, 0);
    	
    	// TextView with the minutes left
    	TextView tvMinutesLeft = new TextView(mContext);
    	tvMinutesLeft.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	tvMinutesLeft.setText(minutesLeft + "min");
    	tvMinutesLeft.setPadding(5, 0, 0, 0); 	

    	// do not use the full display width to display the used tickets (just 90%)
    	int displayWidth = mDisplay != null
    		? (int)(mDisplay.getWidth() * 0.9)
    		: LayoutParams.FILL_PARENT;
    	
    	LinearLayout row = new LinearLayout(mContext);
    	row.setLayoutParams(new LayoutParams(displayWidth, LayoutParams.WRAP_CONTENT));
    	row.addView(llIcon);
    	row.addView(tvVehicleNumber);
    	row.addView(tvDirection);
    	row.addView(tvMinutesLeft);
    	
    	mTableLayout.addView(row);
    	mHasContent = true;
	}
	
	/**
	 * Checks if dialog has content.
	 *
	 * @return true, if content is available
	 */
	public boolean hasContent(){
		return this.mHasContent;
	}
	
	/**
	 * Sets the station name.
	 *
	 * @param stationName the new station name
	 */
	public void setStationName(String stationName){
		this.setTitle("Departures At: " + stationName);
	}
}
