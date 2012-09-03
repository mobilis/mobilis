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

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Game;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Ticket;

/**
 * The Class PanelTickets.
 */
public class PanelTickets extends PanelTransparent {
	
	/** The applications context. */
	private Context mContext;
	
	/** The map with the tickets and its related icon and amount 
	 * (ticketId, (icon, amount)). */
	private HashMap<Integer, ImageTextViewPair> mImageTextViewPairs;
	
	/** The TextView to display current round. */
	private TextView mTextViewRound;
	
	/** The gap between the icons. */
	private boolean mInnerViewGapUpdated;
	
	/** The current Game. */
	private Game mGame;
	

	/**
	 * Instantiates a new panel tickets.
	 *
	 * @param context the context of the application
	 */
	public PanelTickets(Context context) {
		super(context);
		
		initComponents(context);
	}
	
	/**
	 * Instantiates a new PanelTickets.
	 *
	 * @param context the context of the application
	 * @param attrs the attributes
	 */
	public PanelTickets(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initComponents(context);
	}
	
	/**
	 * Creates the Panel and initiates the tickets, amounts and game round.
	 *
	 * @param game the current Game
	 */
	public void create(Game game){
		this.mGame = game;
		
		Log.v("", "myTickets: " + mGame.getRouteManagement().getMyTickets().size());
		
		for(Ticket ticket : mGame.getRouteManagement().getAreaTickets().values()){
			if(mGame.getRouteManagement().getMyTickets().containsKey(ticket.getId())){
				ImageTextViewPair pair = new ImageTextViewPair(mContext);
				pair.create(ticket.getIcon(getResources()),
						"" + mGame.getRouteManagement().getMyTickets().get(ticket.getId()));
				mImageTextViewPairs.put(ticket.getId(), pair);
				
				this.addView(pair);
			}
		}
		
		mTextViewRound.setText("R: " + mGame.getCurrentRound());
		this.addView(mTextViewRound);
		
		mInnerViewGapUpdated = false;
		
		this.invalidate();
	}
	
    /* (non-Javadoc)
     * @see de.tudresden.inf.rn.mobilis.android.xhunt.ui.PanelTransparent#dispatchDraw(android.graphics.Canvas)
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {		
		if(!mInnerViewGapUpdated){
			updateInnerViewGap();
		}
		
		super.dispatchDraw(canvas);
    }
	
	/**
	 * Inits the components.
	 *
	 * @param context the context of the application
	 */
	private void initComponents(Context context){
		this.mContext = context;
		
		mImageTextViewPairs = new HashMap<Integer, PanelTickets.ImageTextViewPair>();
		mTextViewRound = new TextView(mContext);		
		mInnerViewGapUpdated = false;
	}
	
	/**
	 * Updates the Panel with actual data read from curren Game.
	 */
	public void update(){
		for(Ticket ticket : mGame.getRouteManagement().getAreaTickets().values()){
			if(mGame.getRouteManagement().getMyTickets().containsKey(ticket.getId())){
				mImageTextViewPairs.get(ticket.getId()).setText(
						"" + mGame.getRouteManagement().getMyTickets().get(ticket.getId()));
			}
		}
		
		mTextViewRound.setText("R: " + mGame.getCurrentRound());
	}
	
	/**
	 * Update gap between the panel elements.
	 */
	private void updateInnerViewGap(){
		int padding = this.getWidth() / (mImageTextViewPairs.size() + 1);
		Log.v("", "padding: " + padding);
		
		for(ImageTextViewPair pair : this.mImageTextViewPairs.values()){
			pair.setLayoutParams(new LayoutParams(padding, LayoutParams.WRAP_CONTENT));
			pair.setGravity(Gravity.CENTER);
		}
		
		if(mTextViewRound != null){
			mTextViewRound.setLayoutParams(new LayoutParams(padding, LayoutParams.WRAP_CONTENT));
			mTextViewRound.setGravity(Gravity.CENTER);
		}

		mInnerViewGapUpdated = true;
	}
	

	/**
	 * The Class ImageTextViewPair.
	 */
	private class ImageTextViewPair extends LinearLayout {
		
		/** The applications context. */
		private Context mContext;
		
		/** The ImageView for a ticket icon. */
		private ImageView mImageView;
		
		/** The TextView for the amount of ticket. */
		private TextView mTextView;

		/**
		 * Instantiates a new ImageTextViewPair.
		 *
		 * @param context the context
		 */
		public ImageTextViewPair(Context context) {
			super(context);
			this.mContext = context;
		}
		
		/**
		 * Instantiates a new ImageTextViewPair.
		 *
		 * @param context the context of the application
		 * @param attrs the attributes
		 */
		public ImageTextViewPair(Context context, AttributeSet attrs) {
			super(context, attrs);
			this.mContext = context;
		}
		
		/**
		 * Creates the ImageTextViewPair.
		 *
		 * @param imagePath the path to the image of the ticket
		 * @param text the amount of the ticket
		 */
		public void create(Bitmap icon, String text){
			this.mImageView = new ImageView(mContext);
			this.mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			this.mImageView.setImageBitmap(icon);
			this.addView(mImageView);
			
			this.mTextView = new TextView(mContext);
			this.mTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			this.mTextView.setText(text);
			this.mTextView.setPadding(5, 0, 0, 0);
			this.addView(mTextView);
		}
		
		/**
		 * Sets the amount of ticket.
		 *
		 * @param text the new amount
		 */
		public void setText(String text){
			this.mTextView.setText(text);
		}
		
	}
}
