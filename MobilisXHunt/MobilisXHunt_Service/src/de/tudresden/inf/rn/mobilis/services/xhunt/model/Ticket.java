/*******************************************************************************
 * Copyright (C) 2010 Technische Universit�t Dresden
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
package de.tudresden.inf.rn.mobilis.services.xhunt.model;


/**
 * The Class Ticket.
 */
public class Ticket {

	/** The ticket id. */
	private int mId;
	
	/** The ticket name. */
	private String mName;
	
	/** The ticket icon. */
	private String mIcon;
	
	/** True if ticket is special like black ticket 
	 * (can be used on all routes). */
	private boolean mIsSuperior;
	
	/**
	 * Instantiates a new Ticket.
	 */
	public Ticket() {}
	
	/**
	 * Instantiates a new Ticket.
	 *
	 * @param id the id of the ticket
	 * @param name the name of the ticket
	 */
	public Ticket(int id, String name) {
		this.mId = id;
		this.mName = name;
	}

	/**
	 * Instantiates a new Ticket.
	 *
	 * @param id the id of the ticket
	 * @param name the name of the ticket
	 * @param icon the icon of the ticket
	 * @param isSuperior true, if the ticket is special
	 */
	public Ticket(int id, String name, String icon, boolean isSuperior) {
		this.mId = id;
		this.mName = name;
		this.mIcon = icon;
		this.mIsSuperior = isSuperior;
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
	 * Checks if ticket is superior.
	 *
	 * @return true, if ticket is superior
	 */
	public boolean isSuperior() {
		return mIsSuperior;
	}	
	
	/**
	 * Gets the icon of the ticket.
	 *
	 * @return the icon
	 */
	public String getIcon(){
		return mIcon;
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
	 * Sets the icon.
	 *
	 * @param icon the new icon
	 */
	public void setIcon(String icon){
		mIcon = icon;
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
	 * Sets the superior state.
	 *
	 * @param mIsSuperior is ticket superior superior
	 */
	public void setSuperior(boolean mIsSuperior) {
		this.mIsSuperior = mIsSuperior;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Ticket [mId=" + mId + ", mName=" + mName + ", mIsSuperior=" + mIsSuperior + "]";
	}

	
}
