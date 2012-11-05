package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * Class is used for managing Emulation Sessions
 * 
 * @author Thomas Walther
 *
 */
public class SessionManager {
	
	private HashMap<String,Session> sessionList;
	
	public SessionManager() {
		sessionList = new HashMap<String,Session>();
	}
	
	public Session getSession(String id) {
		Session s = null;
		if(sessionList.containsKey(id)) {
			s = sessionList.get(id);
			System.out.println("Session with id " + id + " exist!");
		} else {
			if(id.equals("")) {
				Integer num = sessionList.size();
				s = new Session(num.toString());
				sessionList.put(num.toString(), s);
				System.out.println("Session with id " + num.toString() + " created!");
			} else {
				System.out.println("Session with id " + id + " unknown!");
			}
		}
		return s;
	}

}
