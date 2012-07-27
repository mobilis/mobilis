package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor;

import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.helper.SqlHelper;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.view.MainView;

/**
 * The Class Controller.
 */
public class Controller {
	
	/** The m main view. */
	private MainView mMainView;
	
	/** The m sql helper. */
	private SqlHelper mSqlHelper;
	
	/** The m route management. */
	private RouteManagement mRouteManagement;
	
	/** The m settings. */
	private Settings mSettings;
	
	/** The m selected station. */
	private int mSelectedStation;
	
	
	/** The m instance. */
	private static Controller mInstance = null;
	
	/**
	 * Gets the single instance of Controller.
	 *
	 * @return single instance of Controller
	 */
	public static Controller getInstance(){
		if(mInstance == null)
			mInstance = new Controller();
		
		return mInstance;
	}
	
	/**
	 * Instantiates a new controller.
	 */
	private Controller() {
		mSqlHelper = new SqlHelper(this, "127.0.0.1", "3306",
				"mobilis_server", "mobilis", "mobilis");
		mRouteManagement = new RouteManagement();
		
		mMainView = new MainView(this);
		mSettings = new Settings();
	}
	
	/**
	 * Gets the sql helper.
	 *
	 * @return the sql helper
	 */
	public SqlHelper getSqlHelper(){
		return mSqlHelper;
	}
	
	/**
	 * Log.
	 *
	 * @param str the str
	 */
	public void log(String str){
		System.out.println(str);
	}

	/**
	 * Gets the main view.
	 *
	 * @return the main view
	 */
	public MainView getMainView() {
		return mMainView;
	}

	/**
	 * Gets the settings.
	 *
	 * @return the settings
	 */
	public Settings getSettings(){
		return mSettings;
	}
	
	/**
	 * Gets the route management.
	 *
	 * @return the route management
	 */
	public RouteManagement getRouteManagement() {
		return mRouteManagement;
	}
	
	/**
	 * Reset route management.
	 */
	public void resetRouteManagement(){
		mRouteManagement = new RouteManagement();
	}

	/**
	 * Gets the selected station.
	 *
	 * @return the selected station
	 */
	public int getSelectedStation() {
		return mSelectedStation;
	}

	/**
	 * Sets the selected station.
	 *
	 * @param mSelectedStation the new selected station
	 */
	public void setSelectedStation(int mSelectedStation) {
		this.mSelectedStation = mSelectedStation;
	}
	
	
}
