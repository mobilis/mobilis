package de.tudresden.inf.rn.mobilis.consoleclient;

import de.tudresden.inf.rn.mobilis.consoleclient.shell.CommandShell;

/**
 * The Class Controller.
 */
public class Controller {

	/** The connection. */
	private Connection _connection;
	
	/** The logger. */
	private Log _log;
	
	/** The settings. */
	private Settings _settings;	
	
	
	/**
	 * Instantiates a new controller.
	 */
	public Controller(){
		init();
	};
	
	/**
	 * Inits the controller.
	 */
	private void init(){
		_settings = new Settings();
		_log = new Log();
		_connection = new Connection(this);
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
	 * Gets the log.
	 *
	 * @return the log
	 */
	public Log getLog() {
		return _log;
	}


	/**
	 * Gets the settings.
	 *
	 * @return the settings
	 */
	public Settings getSettings() {
		return _settings;
	}
	
	
}
