package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model;

/**
 * The Class AreaInfo.
 */
public class AreaInfo {

	/** The ID. */
	public int ID = -1;
	
	/** The Name. */
	public String Name = null;
	
	/** The Description. */
	public String Description = null;
	
	/** The Version. */
	public int Version = -1;
	
	/**
	 * Instantiates a new area info.
	 */
	public AreaInfo() {
		
	}
	
	/**
	 * Instantiates a new area info.
	 *
	 * @param id the id
	 * @param name the name
	 * @param desc the desc
	 * @param version the version
	 */
	public AreaInfo(int id, String name, String desc, int version) {
		this.ID = id;
		this.Name = name;
		this.Description = desc;
		this.Version = version;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public AreaInfo clone(){
		return new AreaInfo(this.ID, this.Name, this.Description, this.Version);
	}
}
