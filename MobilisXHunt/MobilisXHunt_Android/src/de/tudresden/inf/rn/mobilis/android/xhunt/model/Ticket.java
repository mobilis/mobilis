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
package de.tudresden.inf.rn.mobilis.android.xhunt.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * The Class Ticket.
 */
public class Ticket {

	/** The id of the ticket. */
	private int mId;
	
	/** The name of the ticket. */
	private String mName;
	
	/** The name of the icon resource in res/drawable-mdpi/. */
	private String mIconFileName;
	
	/** If this type of ticket is special (currently if 
	 * this type is independent from routes like black ticket). */
	private boolean mIsSuperior;
	
	/**
	 * Instantiates a new ticket.
	 */
	public Ticket() {}
	
	/**
	 * Gets the icon as Bitmap (@see android.graphics.Bitmap).
	 *
	 * @return the icon
	 */
	public Bitmap getIcon(Resources res) {
		String iconName = mIconFileName.substring(0, mIconFileName.indexOf(".png"));
		int id = res.getIdentifier(iconName, "drawable", "de.tudresden.inf.rn.mobilis.android.xhunt");
		
		return BitmapFactory.decodeResource(res, id);
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return mId;
	}
	
	/**
	 * Checks if this ticket is superior.
	 *
	 * @return true, if it is superior
	 */
	public boolean isSuperior() {
		return mIsSuperior;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * Sets the icon file name in /res/drawable-mdpi/.
	 *
	 * @param iconFileName the new icon file name
	 */
	public void setIconFileName(String iconFileName) {
		this.mIconFileName = iconFileName;
	}

	/**
	 * Sets the id.
	 *
	 * @param mId the new id
	 */
	public void setId(int mId) {
		this.mId = mId;
	}

	/**
	 * Sets the name.
	 *
	 * @param mName the new name
	 */
	public void setName(String mName) {
		this.mName = mName;
	}
	
	/**
	 * Sets the superior state of this ticket.
	 *
	 * @param mIsSuperior the new superior
	 */
	public void setSuperior(boolean mIsSuperior) {
		this.mIsSuperior = mIsSuperior;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Ticket [mId=" + mId + ", mName=" + mName + ", mIconPath=res/drawable-mdpi/"
				+ mIconFileName + ", mIsSuperior=" + mIsSuperior + "]";
	}	
}
