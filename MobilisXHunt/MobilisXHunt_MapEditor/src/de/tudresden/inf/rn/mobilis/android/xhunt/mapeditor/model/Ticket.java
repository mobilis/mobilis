package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model;


/**
 * The Class Ticket.
 */
public class Ticket {

	/** The m id. */
	private int mId;
	
	/** The m name. */
	private String mName;
	
	/** The m icon. */
	private String mIcon;
	
	/** The m is superior. */
	private boolean mIsSuperior;
	
	/**
	 * Instantiates a new ticket.
	 */
	public Ticket() {}
	
	/**
	 * Instantiates a new ticket.
	 *
	 * @param id the id
	 * @param name the name
	 */
	public Ticket(int id, String name) {
		this.mId = id;
		this.mName = name;
	}

	/**
	 * Instantiates a new ticket.
	 *
	 * @param id the id
	 * @param name the name
	 * @param icon the icon
	 * @param isSuperior the is superior
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
	 * Checks if is superior.
	 *
	 * @return true, if is superior
	 */
	public boolean isSuperior() {
		return mIsSuperior;
	}	
	
	/**
	 * Gets the icon.
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
	 * Sets the superior.
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
		return "Ticket [mId=" + mId + ", mName=" + mName + ", mIsSuperior=" + mIsSuperior + "]";
	}

	
}
