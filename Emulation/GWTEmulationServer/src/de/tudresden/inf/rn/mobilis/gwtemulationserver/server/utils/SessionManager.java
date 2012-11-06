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
	
	private HashMap<String,EmulationSession> sessionList;
	
	public SessionManager() {
		sessionList = new HashMap<String,EmulationSession>();
	}
	
	public EmulationSession getSession(String id) {
		
		EmulationSession s = null;
		
		if(sessionList.containsKey(id)) {
			s = sessionList.get(id);
			System.out.println("Session with id " + id + " exist!");
		} else {
			if(id.equals("")) {
				Integer num = sessionList.size();
				s = new EmulationSession(num.toString());
				sessionList.put(num.toString(), s);
				System.out.println("Session with id " + num.toString() + " created!");
			} else {
				System.out.println("Session with id " + id + " unknown!");
			}
		}
		
		return s;
		
	}
	
	public void deleteSession(String id) {
		
		if(sessionList.containsKey(id)) {
			sessionList.remove(id);
		}
		
	}

}
