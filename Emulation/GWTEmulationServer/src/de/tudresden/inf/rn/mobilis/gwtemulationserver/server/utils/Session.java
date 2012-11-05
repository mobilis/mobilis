package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils;

import java.util.List;

import org.jivesoftware.smack.Connection;

/**
 * 
 * Class Session represents an Emulation Session that is managed in the SessionManager.
 * 
 * @author Thomas Walther
 *
 */
public class Session {
	
	private Connection connection;
	private Boolean connected;
	private List<String> devices;
	private String id;
	
	public Session(String id) {
		this.id = id;
	}

}
