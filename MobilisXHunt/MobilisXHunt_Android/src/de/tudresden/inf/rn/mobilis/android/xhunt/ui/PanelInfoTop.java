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
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Game;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.XHuntPlayer;

/**
 * The Class PanelInfoTop.
 */
public class PanelInfoTop extends PanelTransparent {

	/** The TextView to display current round. */
	private TextView mTextViewRound;
	
	/** The TextView to display whether it's Mr.X' or the Agents' turn. */
	private TextView mTextViewTurn;
	
	/** The applications context. */
	private Context mContext;

	/** The current Game. */
	private Game mGame;
	
	/** Whether the player is Mr.X or not */
	private boolean isMrX;
	
	/** True if players are still distributing. */
	private boolean beginning;
	
	
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
				
		mTextViewTurn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.LEFT));
		mTextViewTurn.setTextColor(Color.WHITE);
		this.addView(mTextViewTurn);
		
		mTextViewRound.setTextColor(Color.WHITE);
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
		
		beginning = true;
		
		// Commented out due to the substitution of the ticket counts for round information
		// (http://jira.inf.tu-dresden.de/browse/MO-124)
		//mImageTextViewPairs = new HashMap<Integer, PanelTickets.ImageTextViewPair>();		
		//mInnerViewGapUpdated = false;
	}
	

	/**
	 * Updates the info text and the round number in the panel.
	 */
	public void updateText() {

		String newText = "nearest station will be assigned";
		
		// only count online players. try/catch to ensure compatibility with older service versions.
		List<XHuntPlayer> onlinePlayers = new ArrayList<XHuntPlayer>();
		try {
			for(XHuntPlayer player : mGame.getGamePlayers().values())
				if(player.isOnline())
					onlinePlayers.add(player);
		} catch(Exception e) {
			for(XHuntPlayer player : mGame.getGamePlayers().values())
				onlinePlayers.add(player);
		}
		
		if(isMrX) {
			if(onlinePlayers.size() == 1)
				newText = "You are playing alone.";
			
			else {
				if(mGame.getCurrentRound() > 0) {
					newText = "";
					
					for(XHuntPlayer player : onlinePlayers) {		
						if(player.isMrX()) {
							if(!player.getReachedTarget())
								newText = "Go to marked station";
							if(player.getReachedTarget()) {
								if(onlinePlayers.size() == 2)
									newText = "Wait till the agent made his move";
								else
									newText ="Wait till agents made their moves";
							}
							if(mGame.getRouteManagement().getStationById(player.getCurrentTargetId()) == null)
								newText = "Decide where to go next";
						}
					}	
				}
			}
		}
		
		else if(!isMrX) {
			
			// check if Mr.X responds to IQs
			if(!mGame.getMrX().isOnline()) {
				newText = "!! Mr.X currently w/o connection !!";
			}
			
			else {
				if(mGame.getCurrentRound() == 0) {	
					
					int stillMovingCounter = 0;
					for(XHuntPlayer player : onlinePlayers) {		
						if((!player.isMrX()) && (!player.getReachedTarget())) {
							if(mGame.getRouteManagement().getStationById(player.getCurrentTargetId()) != null) {
								stillMovingCounter++;
								beginning = false;
							}
						}
					}	
					
					if(!beginning) {
						if(stillMovingCounter == 0)
							newText = "Wait till Mr.X chose next target...";
						else if(stillMovingCounter == 1)
							newText = stillMovingCounter + " agent still on the move";
						else
							newText = stillMovingCounter + " agents still on the move";
					}
				}
				
				else if(mGame.getCurrentRound() > 0) {		
					
					int stillChoosingCounter = 0;
					int stillMovingCounter = 0;		
					for(XHuntPlayer player : onlinePlayers) {
						if(!player.isMrX())
							if(mGame.getRouteManagement().getStationById(player.getCurrentTargetId()) == null)
								stillChoosingCounter++;
					}		
					
					for(XHuntPlayer player : onlinePlayers) {
						if((!player.isMrX()) && (!player.getReachedTarget()))
							if(mGame.getRouteManagement().getStationById(player.getCurrentTargetId()) != null)
								stillMovingCounter++;
					}	
					
					
					if(stillMovingCounter == 1)
						newText = stillMovingCounter + " agent still on the move";
					else
						newText = stillMovingCounter + " agents still on the move";
					
					if(stillChoosingCounter == 1)
						newText = stillChoosingCounter + " agent still choosing target";
					else if(stillChoosingCounter > 1)
						newText = stillChoosingCounter + " agents still choosing targets";
					
					if((stillMovingCounter == 0) && (stillChoosingCounter == 0))
						newText = "Wait till Mr.X chose next target...";
				}
			}
		}

		mTextViewTurn.setText(newText);
		mTextViewRound.setText("Round: " + mGame.getCurrentRound());
		
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
