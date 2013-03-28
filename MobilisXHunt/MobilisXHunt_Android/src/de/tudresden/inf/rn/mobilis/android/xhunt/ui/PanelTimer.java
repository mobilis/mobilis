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
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The Class PanelTimer.
 */
public class PanelTimer extends PanelTransparent {
	
	/** Is timer running. */
	private boolean mIsRunning;
	
	/** The TextView of the big(own) timer. */
	private TextView mTextViewTimerBig;
	
	/** The TextView of the small(opponents) timer. */
	private TextView mTextViewTimerSmall;
	
	/** The TextView to cancel the timer if own player is mr.x. */
	private TextView mTextViewCancel;
	
	/** The termination time in millis of the timer. */
	private long mTerminationTimeInMillis;
	
	/** The timer delay in millis. */
	private long mTimerDelayInMillis;
	
	/** True if own player is mr.x. */
	private boolean mIsMrXTimer = false;
	
	/** The default text in front of the agents timer. */
	private String mTextAgetns = "Time Agents: ";
	
	/** The default text in front of mr.x timer. */
	private String mTextMrX = "Time Mr.X: ";	
	
	/** The Handler which is called if timer was canceled. */
	private Handler mCancelHandler = null;
	
	/** The Handler to handle the timer using a callback. */
	private Handler mHandler = new Handler();
	

	/**
	 * Instantiates a new PanelTimer.
	 *
	 * @param context the context of the application
	 */
	public PanelTimer(Context context) {
		super(context);
		
		initComponents(context);
	}
	
	/**
	 * Instantiates a new PanelTimer.
	 *
	 * @param context the context of the application
	 * @param attrs the attributes
	 */
	public PanelTimer(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initComponents(context);
	}
	
	/**
	 * Cancel timer.
	 */
	public void cancelTimer(){
		mHandler.removeCallbacks(mUpdateTimerTask);
		mIsRunning = false;
		this.setVisibility(GONE);
	}
	
	/**
	 * Enable mr.x timer. This timer can be canceled if enabled is true.
	 *
	 * @param enable true to enable mr.x timer
	 */
	public void enableMrXTimer(boolean enable){
		this.mIsMrXTimer = enable;
		
		if(enable) this.mTextViewCancel.setVisibility(VISIBLE);
		else this.mTextViewCancel.setVisibility(GONE);
	}
	
	/**
	 * Generate text for timers.
	 *
	 * @param millis the current millis
	 * @return the formated string of time left
	 */
	private String generateText(long millis){
		String str = "";		
		int seconds = 0;
		int minutes = 0;		
		
		if(millis > 0){
			seconds = (int) (millis / 1000);
			minutes = seconds / 60;
			seconds = seconds % 60;
		}
		
		str += (minutes < 10 ? ("0" + minutes) : minutes);
		str += ":" + (seconds < 10 ? ("0" + seconds) : seconds);
		
		return str;
	}
	
	/**
	 * Inits the components.
	 *
	 * @param context the context of the application
	 */
	private void initComponents(Context context){
		this.setOrientation(VERTICAL);
		
		// wrapper for the big(own) timer
		LinearLayout llBig = new LinearLayout(context);

		// the big(own) timers TextView
		this.mTextViewTimerBig = new TextView(context);
		this.mTextViewTimerBig.setPadding(10, 10, 10, 0);
		this.mTextViewTimerBig.setGravity(Gravity.CENTER);
		this.mTextViewTimerBig.setTextSize((int)(this.mTextViewTimerBig.getTextSize() * 1.5));
		this.mTextViewTimerBig.setTextColor(Color.WHITE);
		llBig.addView(this.mTextViewTimerBig);
		
		// if mr.x timer is enabled, display an option to cancel current timer
		this.mTextViewCancel = new TextView(context);
		this.mTextViewCancel.setText("X");
		this.mTextViewCancel.setPadding(5, 0, 10, 0);
		this.mTextViewCancel.setTextColor(Color.RED);
		this.mTextViewCancel.setTypeface(null, Typeface.BOLD);
		this.mTextViewCancel.setVisibility(GONE);
		this.mTextViewCancel.setTextSize((int)(this.mTextViewCancel.getTextSize() * 1.5));
		this.mTextViewCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// call mCancelHandler to notify subscribed activity
				mCancelHandler.sendEmptyMessage(0);
			}
		});
		llBig.addView(this.mTextViewCancel);
		
		this.addView(llBig);
		
		// the small(opponents) TextView
		this.mTextViewTimerSmall = new TextView(context);
		this.mTextViewTimerSmall.setPadding(10, 0, 0, 10);
		this.mTextViewTimerSmall.setGravity(Gravity.CENTER);
		this.mTextViewTimerSmall.setTextColor(Color.WHITE);
		this.addView(this.mTextViewTimerSmall);
		
		mIsRunning = false;
	}
	
	/**
	 * Checks if timer is running.
	 *
	 * @return true, if timer is running
	 */
	public boolean isTimerRunning(){
		return this.mIsRunning;
	}
	
	/**
	 * Start timer.
	 *
	 * @param timeInMillis the timers duration in millis
	 * @param cancelHandler the callback cancel Handler
	 */
	public void startTimer(long timeInMillis, Handler cancelHandler){
		this.mCancelHandler = cancelHandler;
		this.mTimerDelayInMillis = timeInMillis;
		// termination time is the addition if the current time + timer duration
		this.mTerminationTimeInMillis = SystemClock.uptimeMillis() + timeInMillis;
		
		// call timers inner callback handler to run logic in mUpdateTimerTask#run all 1000ms
		mHandler.postDelayed(mUpdateTimerTask, 1000);
		mIsRunning = true;
		
		this.mTextViewTimerBig.setText(generateText(mTerminationTimeInMillis - SystemClock.uptimeMillis()));
		this.setVisibility(VISIBLE);
	}
	
	/** The mUpdateTimerTask to execute functions if inner callback Handler delays. */
	private Runnable mUpdateTimerTask = new Runnable() {

		@Override
		public void run() {
			long now = SystemClock.uptimeMillis();
			long millis = mTerminationTimeInMillis - now;
			long halfOfMillis = mTerminationTimeInMillis - (mTimerDelayInMillis / 2) - now;
			
			if (millis<=0) {
				cancelTimer();
	   
				try {
					this.finalize();
				} catch (Throwable e) {
					e.printStackTrace();
				}
				return;
			}		       

			if(mIsMrXTimer){
				mTextViewTimerBig.setText("Time left: " + generateText(millis));
				mTextViewTimerSmall.setText(mTextAgetns + generateText(halfOfMillis));
			}
			else{
				mTextViewTimerBig.setText("Time left: " + generateText(halfOfMillis));
				mTextViewTimerSmall.setText(mTextMrX + generateText(millis));
			}
 
			mHandler.postAtTime(this,
					now + 1000);
		}
	};
}
