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
package de.tudresden.inf.rn.mobilis.android.xhunt.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;

/**
 * A custom SeekBar implemented as a Preference, for storing the value automatically.
 * Used e.g. for setting the number of tickets when creating a new Game.
 * @author Matthias Köngeter
 */
public class SeekBarPreference extends Preference implements OnSeekBarChangeListener {

	/** The tag used for logging */
	private final String TAG = "SeekBarPreference";
	
	private int mMinValue;
	private int mMaxValue;
	private int mInterval;
	private int mCurrentValue;
	private int mDefaultValue;
	private String mUnits;
	
	private SeekBar mSeekBar;
	private TextView mCurrValueText;
	
	/**
	 * Constructor for a new SeekBarPreference instance
	 * @param context the Context object
	 * @param minValue the minimum value
	 * @param maxValue the maximum value
	 * @param defaultValue the default value
	 * @param unit the unit shown next to the set value
	 */
	public SeekBarPreference(Context context, int minValue, int maxValue, int defaultValue, String unit) {
		super(context);
		
		mMinValue = minValue;
		mMaxValue = maxValue;
		mDefaultValue = defaultValue;
		mUnits = " " + unit;
		mInterval = 1;
		
		mCurrentValue = getPersistedInt(defaultValue);
		
		mSeekBar = new SeekBar(context);
		mSeekBar.setMax(mMaxValue - mMinValue);
		mSeekBar.setOnSeekBarChangeListener(this);
	}
	
	@Override
	protected View onCreateView(ViewGroup parent) {
		RelativeLayout layout = null;
		
		try {
			LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = (RelativeLayout) mInflater.inflate(R.layout.seek_bar_preference, parent, false);
		} catch (Exception e) {
			Log.e(TAG, "Error creating seekbar preference", e);
		}
		
		return layout;
	}
	
	@Override
	public void onBindView(View view) {
		super.onBindView(view);
		
		try {
			//move our seekbar to the new view we've been given
			ViewParent oldContainer = mSeekBar.getParent();
			ViewGroup newContainer = (ViewGroup) view.findViewById(R.id.seekBarPrefBarContainer);
			
			if(oldContainer != newContainer) {
				//remove the seekbar from the old view
				if(oldContainer != null)
					((ViewGroup) oldContainer).removeView(mSeekBar);
				//remove the existing seekbar (there may not be one) and add ours
				newContainer.removeAllViews();
				newContainer.addView(mSeekBar, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			}
		} catch (Exception e) {
			Log.e(TAG, "Error binding view: " + e.toString());
		}
		
		updateView(view);
	}
	
	/**
	 * Update a SeekBarPreference view with our current state
	 * @param view
	 */
	protected void updateView(View view) {
		try {
			RelativeLayout layout = (RelativeLayout) view;
			
			mCurrValueText = (TextView) layout.findViewById(R.id.seekBarPrefValue);
			mCurrValueText.setText(String.valueOf(mCurrentValue));
			mCurrValueText.setMinimumWidth(30);
			
			mSeekBar.setProgress(mCurrentValue - mMinValue);
			
			TextView units = (TextView) layout.findViewById(R.id.seekBarPrefUnits);
			units.setText(mUnits);
			
		} catch (Exception e) {
			Log.e(TAG, "Error updating seekbar preference", e);
		}
	}
	
	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {	
		if(restoreValue)
			mCurrentValue = getPersistedInt(mCurrentValue);	
		else {
			int tmp = 0;
			try {
				tmp = (Integer) defaultValue;
			} catch (Exception e) {
				Log.e(TAG, "Invalid default value: " + defaultValue.toString());
			}
			persistInt(tmp);
			mCurrentValue = tmp;
		}		
	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray ta, int index) {
		return ta.getInteger(index, mDefaultValue);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int newValue = progress + mMinValue;
		
		if(newValue > mMaxValue)
			newValue = mMaxValue;
		else if(newValue < mMinValue)
			newValue = mMinValue;
		else if((mInterval != 1) && (newValue % mInterval != 0))
			newValue = Math.round(((float) newValue) / mInterval) * mInterval;
		
		//change rejected, revert to the previous value
		if(!callChangeListener(newValue)) {
			seekBar.setProgress(mCurrentValue - mMinValue);
			return;
		}
		
		//change accepted, store it
		mCurrentValue = newValue;
		mCurrValueText.setText(String.valueOf(newValue));
		persistInt(newValue);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		notifyChanged();
	}	
}
