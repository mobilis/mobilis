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

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Game;

/**
 * The Class PanelInfoTop.
 */
public class PanelInfoTop extends PanelTransparent {
	
	/** The applications context. */
	private Context mContext;

	/** The current Game. */
	private Game mGame;

	/** The TextView to display current round. */
	private TextView mTextViewRound;
	
	/** The TextView to display whether it's Mr.X' or the Agents' turn. */
	private TextView mTextViewTurn;
	
	/** Whether the player is Mr.X or not */
	private boolean isMrX;
	
	private int turnTextViewUpdateCounter;
	
	
	/* The map with the tickets and its related icon and amount (ticketId, (icon, amount)). */
	// Commented out due to the substitution of the ticket counts for round information
	// (http://jira.inf.tu-dresden.de/browse/MO-124)
	//private HashMap<Integer, ImageTextViewPair> mImageTextViewPairs;
	
	/* The gap between the icons. */
	// Commented out due to the substitution of the ticket counts for round information
	// (http://jira.inf.tu-dresden.de/browse/MO-124)
	//private boolean mInnerViewGapUpdated;
	

	/**
	 * Instantiates a new panel tickets.
	 *
	 * @param context the context of the application
	 */
	public PanelInfoTop(Context context) {
		super(context);
		initComponents(context);
	}
	
	/**
	 * Instantiates a new PanelTickets.
	 *
	 * @param context the context of the application
	 * @param attrs the attributes
	 */
	public PanelInfoTop(Context context, AttributeSet attrs) {
		super(context, attrs);
		initComponents(context);
	}
	
	/**
	 * Creates the Panel and initiates the TextViews for turn and game round.
	 *
	 * @param game the current Game
	 */
	public void create(Game game, boolean isMrX) {
		this.mGame = game;
		this.isMrX = isMrX;
				
		mTextViewTurn.setText("Wait at assigned station");
		mTextViewTurn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.LEFT));
		this.addView(mTextViewTurn);
		
		mTextViewRound.setText("Round: " + mGame.getCurrentRound());
		this.addView(mTextViewRound);
				
		this.invalidate();
		
		// Commented out due to the substitution of the ticket counts for round information
		// (http://jira.inf.tu-dresden.de/browse/MO-124)
		/*Log.v("", "myTickets: " + mGame.getRouteManagement().getMyTickets().size());
		for(Ticket ticket : mGame.getRouteManagement().getAreaTickets().values()){
			if(mGame.getRouteManagement().getMyTickets().containsKey(ticket.getId())){
				ImageTextViewPair pair = new ImageTextViewPair(mContext);
				pair.create(ticket.getIcon(getResources()),
						"" + mGame.getRouteManagement().getMyTickets().get(ticket.getId()));
				mImageTextViewPairs.put(ticket.getId(), pair);
				this.addView(pair);
			}
		}
		mInnerViewGapUpdated = false;*/
	}
	
    /* (non-Javadoc)
     * @see de.tudresden.inf.rn.mobilis.android.xhunt.ui.PanelTransparent#dispatchDraw(android.graphics.Canvas)
     *
	 * Commented out due to the substitution of the ticket counts for round information
	 * (http://jira.inf.tu-dresden.de/browse/MO-124)
	 */
    /*@Override
    protected void dispatchDraw(Canvas canvas) {		
		if(!mInnerViewGapUpdated){
			updateInnerViewGap();
		}
		
		super.dispatchDraw(canvas);
    }*/
	
	/**
	 * Inits the components.
	 *
	 * @param context the context of the application
	 */
	private void initComponents(Context context){
		this.mContext = context;
		
		mTextViewRound = new TextView(mContext);
		mTextViewTurn = new TextView(mContext);
		
		turnTextViewUpdateCounter = 0;
		
		// Commented out due to the substitution of the ticket counts for round information
		// (http://jira.inf.tu-dresden.de/browse/MO-124)
		//mImageTextViewPairs = new HashMap<Integer, PanelTickets.ImageTextViewPair>();		
		//mInnerViewGapUpdated = false;
	}

	/**
	 * Updates the Panel with actual data read from current Game.
	 */
	public void update() {
		String whoseTurn;
		
		if(turnTextViewUpdateCounter % 2 == 0) {
			if(isMrX)
				whoseTurn = "Mr.X chooses next target";
			else
				whoseTurn = "Agents decide next target";
		}
		
		else {
			if(isMrX)
				whoseTurn = "Agents decide next target";
			else
				whoseTurn = "Mr.X chooses next target";
		}
		

		mTextViewTurn.setText(whoseTurn);
		mTextViewRound.setText("Round: " + mGame.getCurrentRound());
		
		turnTextViewUpdateCounter++;
			
		// Commented out due to the substitution of the ticket counts for round information
		// (http://jira.inf.tu-dresden.de/browse/MO-124)
		/*for(Ticket ticket : mGame.getRouteManagement().getAreaTickets().values()){
			if(mGame.getRouteManagement().getMyTickets().containsKey(ticket.getId())){
				mImageTextViewPairs.get(ticket.getId()).setText(
						"" + mGame.getRouteManagement().getMyTickets().get(ticket.getId()));
			}
		}*/
	}
	
	/*
	 * Update gap between the panel elements.
	 * 
	 * Commented out due to the substitution of the ticket counts for round information
	 * (http://jira.inf.tu-dresden.de/browse/MO-124)
	 */
	/*private void updateInnerViewGap(){
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
	}*/
	

	/* 
	 * The Class ImageTextViewPair.
	 *
	 * Commented out due to the substitution of the ticket counts for round information
	 * (http://jira.inf.tu-dresden.de/browse/MO-124)
	 *
	 */
	/*private class ImageTextViewPair extends LinearLayout {
		
		// The applications context.
		private Context mContext;
		
		// The ImageView for a ticket icon.
		private ImageView mImageView;
		
		// The TextView for the amount of ticket.
		private TextView mTextView;

		// Instantiates a new ImageTextViewPair.
		// @param context the context
		public ImageTextViewPair(Context context) {
			super(context);
			this.mContext = context;
		}
		
		// Instantiates a new ImageTextViewPair.
		// @param context the context of the application
		// @param attrs the attributes
		public ImageTextViewPair(Context context, AttributeSet attrs) {
			super(context, attrs);
			this.mContext = context;
		}
		
		// Creates the ImageTextViewPair.
		// @param imagePath the path to the image of the ticket
		// @param text the amount of the ticket
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
		
		// Sets the amount of ticket.
		// @param text the new amount
		public void setText(String text){
			this.mTextView.setText(text);
		}
		
	}*/
}
