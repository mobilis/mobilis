package de.tudresden.inf.rn.mobilis.gwtemulationserver.shared;

import java.io.Serializable;
import java.util.List;

public class SessionInfo implements Serializable {
	
	private Boolean isSession;
	private String sessionID;
	private String errorMessage;
	private List<String> scriptList;
	
	// empty constructor is needed for GWT
	public SessionInfo() {
		this.isSession = false;
		this.sessionID = "";
		this.errorMessage = "";
		this.setScriptList(null);
	}
	
	public SessionInfo(Boolean isSession, String id, List<String> scriptList) {
		this.isSession = isSession;
		this.sessionID = id;
		this.setErrorMessage("");
		this.setScriptList(scriptList);
	}
	
	public SessionInfo(Boolean isSession, String id, String errorMessage) {
		this.isSession = isSession;
		this.sessionID = id;
		this.setErrorMessage(errorMessage);
	}
	
	public Boolean isSession() {
		return isSession;
	}
	
	public String getSessionID() {
		return sessionID;
	}
	
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<String> getScriptList() {
		return scriptList;
	}

	public void setScriptList(List<String> scriptList) {
		this.scriptList = scriptList;
	}

}
