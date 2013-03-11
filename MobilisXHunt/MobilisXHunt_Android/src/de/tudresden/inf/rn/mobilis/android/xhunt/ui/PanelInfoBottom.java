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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;

/**
 * The Class PanelInfoBottom.
 */
public class PanelInfoBottom extends LinearLayout {
	
	/** The inner paint. */
	private Paint innerPaint;
	
	/** The applications context. */
	private Context mContext;
	
	/** The ImageView if target is reached or not. */
	private ImageView mImageViewReachedTarget;
	
	/** The TextView which displays a specific status. */
	private TextView mTextViewStatus;
	

	/**
	 * Instantiates a new PanelInfo.
	 *
	 * @param context the context
	 */
	public PanelInfoBottom(Context context) {
		super(context);
		
		initComponents(context);
	}
	
	/**
	 * Instantiates a new PanelInfo.
	 *
	 * @param context the applications context
	 * @param attrs the attributes
	 */
	public PanelInfoBottom(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initComponents(context);
	}
	
    /* (non-Javadoc)
     * @see android.view.ViewGroup#dispatchDraw(android.graphics.Canvas)
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
    	// draw rectangle in bottom
    	RectF drawRect = new RectF();
    	drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());
    	
    	canvas.drawRect(drawRect, innerPaint);
		
		super.dispatchDraw(canvas);
    }
	
	/**
	 * Inits the components.
	 *
	 * @param context the applications context
	 */
	private void initComponents(Context context){
		this.mContext = context;
		
		innerPaint = new Paint();
		innerPaint.setARGB(225, 75, 75, 75);
		innerPaint.setAntiAlias(true);
		
		mImageViewReachedTarget = new ImageView(mContext);
		mImageViewReachedTarget.setBackgroundResource(R.drawable.ic_flag_red);
		
		mTextViewStatus = new TextView(mContext);
		mTextViewStatus.setGravity(Gravity.CENTER_VERTICAL);
		mTextViewStatus.setPadding(5, 0, 0, 0);
		mTextViewStatus.setTextColor(Color.WHITE);
		
		this.addView(mImageViewReachedTarget);
		this.addView(mTextViewStatus);
	}
	
	/**
	 * Sets the text for the status.
	 *
	 * @param text the new status text
	 */
	public void setInfoText(String text){		
		mTextViewStatus.setText(text);
	}
	
	/**
	 * Toggle icon related to own player has reached his target or not.
	 *
	 * @param reached true, if current target was reached (green flag, else red)
	 */
	public void setTargetReached(boolean reached){
		mImageViewReachedTarget.setBackgroundResource(reached 
				? R.drawable.ic_flag_green : R.drawable.ic_flag_red);
	}

}
