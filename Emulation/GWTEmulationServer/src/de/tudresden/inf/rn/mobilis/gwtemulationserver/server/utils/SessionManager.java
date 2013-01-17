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
	private Integer lastID = 0;
	
	public SessionManager() {
		sessionList = new HashMap<String,EmulationSession>();
	}
	
	public EmulationSession getSession(String id) {
		
		EmulationSession s = null;
		
		if(sessionList.containsKey(id)) {
			s = sessionList.get(id);
			//System.out.println("Session with id " + id + " exist!");
		} else {
			if(id.equals("")) {
				//Integer num = sessionList.size();
				//s = new EmulationSession(num.toString());
				s = new EmulationSession(lastID.toString());
				sessionList.put(lastID.toString(), s);
				lastID++;
				//System.out.println("Session with id " + num.toString() + " created!");
			} else {
				//System.out.println("Session with id " + id + " unknown!");
			}
		}
		
		return s;
		
	}
	
	public Boolean sessionExist(String id) {
		return sessionList.containsKey(id);
	}
	
	public void deleteSession(String id) {
		
		if(sessionList.containsKey(id)) {
			sessionList.remove(id);
		}
		
	}

}
