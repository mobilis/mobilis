package de.tudresden.inf.rn.mobilis.gwtemulationserver.shared;

import java.io.Serializable;

public class SessionInfo implements Serializable {
	
	private Boolean connected;
	private String sessionID;
	private String errorMessage;
	
	public SessionInfo() {
		this.connected = false;
		this.sessionID = "";
		this.errorMessage = "";
	}
	
	public SessionInfo(Boolean connected, String id) {
		this.connected = connected;
		this.sessionID = id;
		this.setErrorMessage("");
	}
	
	public SessionInfo(Boolean connected, String id, String errorMessage) {
		this.connected = connected;
		this.sessionID = id;
		this.setErrorMessage(errorMessage);
	}
	
	public Boolean getConnected() {
		return connected;
	}
	
	public void setConnected(Boolean connected) {
		this.connected = connected;
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

}
