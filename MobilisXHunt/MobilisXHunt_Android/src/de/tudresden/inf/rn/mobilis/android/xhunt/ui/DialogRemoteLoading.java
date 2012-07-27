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

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;

/**
 * The Class DialogRemoteLoading.
 */
public class DialogRemoteLoading {

	/** The applications context. */
	private Context mContext;
	
	/** @see android.app.ProgressDialog. */
	private ProgressDialog mProgressDialog;
	
	/** The timer for reloading timeout. */
	private Timer mTimeOutTimer;
	
	/** The mTimeOutTimer's delay in ms. */
	private int mTimeOutDelay;
	
	/** The text which will be shown if dialog is visible. */
	private String mLoadingText = "Requesting data.\n\n     Please wait...";
	
	/** The text which will be shown if request reaches timeout limit. */
	private String mTimeOutText = "Sorry, your request has timed out.";
	
	/** The dialog as a progress bar or not. */
	private boolean mIsHorizontal = false;

	
    /** The callback Handler which will be called if request reaches timeout limit. */
    private Handler mTimeOutHandler;
    
	
	/**
	 * Instantiates a new DialogRemoteLoading.
	 *
	 * @param context the context of the application
	 * @param delay the delay time for a request in milliseconds
	 */
	public DialogRemoteLoading(Context context, int delay) {
		this.mContext = context;
		this.mTimeOutDelay = delay;
		
		mProgressDialog = new ProgressDialog(mContext);		
		setDefaultTimeoutHandler();
	}
	
	/**
	 * Instantiates a new DialogRemoteLoading.
	 *
	 * @param context the context of the application
	 * @param delay the delay time for a request in milliseconds
	 * @param timeOutHandler an extern Handler which should be called if 
	 * request reaches time out limit
	 */
	public DialogRemoteLoading(Context context, int delay, Handler timeOutHandler) {
		this.mContext = context;
		this.mTimeOutDelay = delay;
		this.mTimeOutHandler = timeOutHandler;
		
		mProgressDialog = new ProgressDialog(mContext);
		setDefaultTimeoutHandler();
	}
	
    /**
     * Cancels the current running ProgressDialog and Timer.
     */
    public void cancel(){
		if(mTimeOutTimer != null
				&& mProgressDialog != null){
			mTimeOutTimer.cancel();
			setDefaultTimeoutHandler();
			
			if(mProgressDialog.isShowing()){
				try{
					mProgressDialog.dismiss();
				}
				catch(IllegalArgumentException e){
					Log.e("DialogRemoteLoading", "Cannot dismiss Dialog: " + e.getMessage());
				}
			}
		}
	}
    
    /**
     * Gets the ProgressDialog.
     *
     * @return the ProgressDialog
     */
    public ProgressDialog getDialog(){
    	return mProgressDialog;
    }
    
	/**
	 * Gets the delay for time out.
	 *
	 * @return the time out delay
	 */
	public int getTimeOutDelay() {
		return mTimeOutDelay;
	}
	
	/**
	 * Increment current value of the progress bar.
	 *
	 * @param value the value to increase by
	 */
	public void incrementCurrentValueBy(int value){
		mProgressDialog.incrementProgressBy(value);
	}
	
	/**
	 * Checks if loading of progress bar is complete (if max value was reached).
	 *
	 * @return true, if loading is complete
	 */
	public boolean isLoadingComplete(){
		return mProgressDialog.getProgress() == mProgressDialog.getMax();
	}
	
	/**
	 * Checks if current progress is running.
	 *
	 * @return true, if it is running
	 */
	public boolean isRunning(){
		return mProgressDialog.isShowing();
	}

    /**
     * Run the instantiate progress and Timer.
     */
    public void run(){
    	if(mIsHorizontal){    		
    		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    	}
    	else{
    		mProgressDialog.setIndeterminate(true);
    	}
    	
    	mProgressDialog.setMessage(this.mLoadingText);    	
    	mProgressDialog.setCancelable(true);
    	mProgressDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {}
		});
    	
    	
    	mProgressDialog.show();
    	
    	mTimeOutTimer = new Timer();
    	mTimeOutTimer.schedule(
    			new TimerTask() {
    				public void run() {
    					timeOutOccurs();
    		        }
    			}, mTimeOutDelay);
    }  

	/**
	 * Sets the loading text.
	 *
	 * @param mLoadingText the new loading text
	 */
	public void setLoadingText(String mLoadingText) {
		this.mLoadingText = mLoadingText;
	}
	
	/**
	 * Sets the default timeout handler. Will be used if no extern 
	 * Handler was defined.
	 */
	public void setDefaultTimeoutHandler(){
		mTimeOutHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				cancel();
				
	    		AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
	    		alert.setMessage(mTimeOutText
	    				+ "\nPlease Check your Settings");
	    		
	    		alert.setTitle("Connection Timeout");
	    		alert.setIcon(R.drawable.ic_error_48);
	    		
	    		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {}
	    		});

	    		alert.show();
			}
	    };
	}
	
	/**
	 * Sets the max value for the progress bar.
	 *
	 * @param value the new max value
	 */
	public void setMaxValue(int value){
		mProgressDialog.setMax(value);
	}
	
	/**
	 * Sets the style horizontal to use the dialog as a progress bar.
	 */
	public void setStyleHorizontal(){
		this.mIsHorizontal = true;
	}

	/**
	 * Sets the time out delay.
	 *
	 * @param mTimeOutDelay the new time out delay
	 */
	public void setTimeOutDelay(int mTimeOutDelay) {
		this.mTimeOutDelay = mTimeOutDelay;
	}

	/**
	 * Sets the time out handler which will be called if the request 
	 * reaches time out limit.
	 *
	 * @param mTimeOutHandler the new time out handler
	 */
	public void setTimeOutHandler(Handler mTimeOutHandler) {
		this.mTimeOutHandler = mTimeOutHandler;
	}

	/**
	 * Sets the time out text.
	 *
	 * @param mTimeoutText the new time out text
	 */
	public void setTimeOutText(String mTimeoutText) {
		this.mTimeOutText = mTimeoutText;
	}    
	
    /**
     * Call if a time out occurs. This will stop the progress dialog and 
     * notify the time out handler that a time out appeared.
     */
    private void timeOutOccurs(){
    	if(mProgressDialog.isShowing()){
    		mTimeOutTimer.cancel();
    		
    		mTimeOutHandler.sendEmptyMessage(0);
    	}
    }
    
}
