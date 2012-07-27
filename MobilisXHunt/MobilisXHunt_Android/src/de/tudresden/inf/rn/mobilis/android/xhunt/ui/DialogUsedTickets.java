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

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Display;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;

/**
 * The Class DialogUsedTickets.
 */
public class DialogUsedTickets extends Dialog {
	
	/** The Constant DIALOG_ID. */
	public static final int DIALOG_ID = 302;
	
	/** The TableLayout which holds the used tickets. */
	private TableLayout mTableLayout;
	
	/** The applications context. */
	private Context mContext;
	
	/** The display of the players device. */
	private Display mDisplay;
	
	/** True if dialog already has content. */
	private boolean mHasContent = false;

	/**
	 * Instantiates a new DialogUsedTickets.
	 *
	 * @param context the context of the application
	 * @param display the display of the players device
	 */
	public DialogUsedTickets(Context context, Display display) {
		super(context);
		
		mContext = context;
		this.mDisplay = display;
		
		this.setContentView(R.layout.dialog_body);
		this.setTitle("Consumed Tickets");
    	
    	mTableLayout = (TableLayout)findViewById(R.id.dialog_list);
	}
	
	/**
	 * Clear content of this dialog.
	 */
	public void clearContent(){
		mTableLayout.removeAllViews();
		mHasContent = false;
	}
	
	/**
	 * Add a player and his used tickets to the TableLayout.
	 *
	 * @param playerIconId the id of the players icon
	 * @param playername the players name
	 * @param usedTicketIcons the used ticket icons
	 */
	public void addPlayer(int playerIconId, String playername, ArrayList<Bitmap> usedTicketIcons){
		// wrapper layout for the players icon (a line in TableLayout)
		LinearLayout llIcon = new LinearLayout(mContext);
    	llIcon.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
    	
    	// ImageView with the players icon
    	ImageView imgPlayerIcon = new ImageView(mContext);
    	imgPlayerIcon.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	imgPlayerIcon.setScaleType(ScaleType.CENTER);
    	imgPlayerIcon.setBackgroundResource(playerIconId);
    	
    	llIcon.addView(imgPlayerIcon);

    	// wrapper layout for the players name and used tickets
    	LinearLayout llNameTickets = new LinearLayout(mContext);
    	llNameTickets.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    	llNameTickets.setOrientation(LinearLayout.VERTICAL);
      	
    	// TextView with the name of the player
    	TextView tvPlayerName = new TextView(mContext);
    	tvPlayerName.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	tvPlayerName.setText(playername);
    	tvPlayerName.setPadding(10, 0, 0, 0);
    	
    	// wrapper layout for the used tickets
    	LinearLayout llTickets = new LinearLayout(mContext);
    	llTickets.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    	llTickets.setPadding(10, 2, 0, 0);
    	
    	// adding ticket icons
    	for(Bitmap icon : usedTicketIcons){
    		ImageView imgIcon = new ImageView(mContext);
    		imgIcon.setImageBitmap(icon);
    		
    		llTickets.addView(imgIcon);
    	}

    	
    	llNameTickets.addView(tvPlayerName);
    	llNameTickets.addView(llTickets);    	

    	// do not use the full display width to display the used tickets (just 90%)
    	int displayWidth = mDisplay != null
    		? (int)(mDisplay.getWidth() * 0.9)
    		: LayoutParams.FILL_PARENT;
    	
    	LinearLayout row = new LinearLayout(mContext);
    	row.setLayoutParams(new LayoutParams(displayWidth, LayoutParams.WRAP_CONTENT));
//    	llNameTickets.setPadding(0, 10, 0, 0);
    	row.addView(llIcon);
    	row.addView(llNameTickets);
    	
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
}
