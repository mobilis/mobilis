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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * The Class TransparentPanel.
 */
public class PanelTransparent extends LinearLayout { 
	
	/** The inner and the border paint. */
	protected Paint innerPaint, borderPaint;
    
	/**
	 * Instantiates a new transparent panel.
	 * 
	 * @param context the context to show this panel on
	 * @param attrs the attributes
	 */
	public PanelTransparent(Context context, AttributeSet attrs) {
		super(context, attrs);
		initProperties();
	}

	/**
	 * Instantiates a new transparent panel.
	 * 
	 * @param context the context to show this panel on
	 */
	public PanelTransparent(Context context) {
		super(context);
		initProperties();
	}

	/**
	 * Inits the properties.
	 */
	private void initProperties() {
		innerPaint = new Paint();
		innerPaint.setARGB(225, 75, 75, 75);
		innerPaint.setAntiAlias(true);

		borderPaint = new Paint();
		borderPaint.setARGB(255, 255, 255, 255);
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(2);
	}
	
	/**
	 * Sets the inner paint.
	 * 
	 * @param innerPaint the new inner paint
	 */
	public void setInnerPaint(Paint innerPaint) {
		this.innerPaint = innerPaint;
	}

	/**
	 * Sets the border paint.
	 * 
	 * @param borderPaint the new border paint
	 */
	public void setBorderPaint(Paint borderPaint) {
		this.borderPaint = borderPaint;
	}

    /* (non-Javadoc)
     * @see android.view.ViewGroup#dispatchDraw(android.graphics.Canvas)
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
    	
    	RectF drawRect = new RectF();
    	drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());
    	
    	canvas.drawRoundRect(drawRect, 5, 5, innerPaint);
		canvas.drawRoundRect(drawRect, 5, 5, borderPaint);
		
		super.dispatchDraw(canvas);
    }
}
