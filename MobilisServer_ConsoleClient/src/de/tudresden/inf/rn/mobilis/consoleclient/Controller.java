package de.tudresden.inf.rn.mobilis.consoleclient;

/**
 * The Class Controller.
 */
public class Controller {

    private static Controller instance;

	/** The connection. */
	private Connection _connection;

	/** The settings. */
	private Settings _settings;

    private ServiceHandler _serviceHandler;

	private Controller(){
		init();
	}

    public static Controller getController() {
        if (instance == null)
            synchronized (Controller.class) {
                if (instance == null) instance = new Controller();
            }
        return instance;
    }
	
	/**
	 * Inits the controller.
	 */
	private void init(){
		_settings = new Settings();
		_connection = new Connection(this);
        _serviceHandler = new ServiceHandler(this);
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public Connection getConnection() {
		return _connection;
	}

	/**
	 * Gets the settings.
	 *
	 * @return the settings
	 */
	public Settings getSettings() {
		return _settings;
	}

    public ServiceHandler getServiceHandler() { return _serviceHandler; };
	
	
}
