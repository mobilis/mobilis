package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 
 * Class EmulationSession represents an Emulation Session that is managed in the SessionManager.
 * 
 * @author Thomas Walther
 *
 */

@Entity
@Table(name="EmulationSessions")
public class EmulationSession implements Serializable {
	
	private Long id;
	private String script;
	private String sessionDir;
	@Column(columnDefinition="longblob")
	private ArrayList<String> devices;
	private Long startTime;
	private Long endTime;
	@Column(columnDefinition="longblob")
	private EmulationStatus status;
	
	public EmulationSession() {
	}
	
	public EmulationSession(String script, String sessionDir, ArrayList<String> devices) {
		this.script = script;
		this.sessionDir = sessionDir;
		this.devices = devices;
	}
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	public Long getId() {
		return id;
	}
	
	private void setId(Long id) {
		this.id = id;
	}
	
	public void setScript(String script) {
		this.script = script;
	}
	
	public String getScript() {
		return script;
	}
	
	public void setSessionDir(String sessionDir) {
		this.sessionDir = sessionDir;
	}
	
	public String getSessionDir() {
		return sessionDir;
	}
	
	public void setDevices(ArrayList<String> devices) {
		this.devices = devices;
	}
	
	public ArrayList<String> getDevices() {
		return devices;
	}
	
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getStartTime() {
		return startTime;
	}
	
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	public long getEndTime() {
		return endTime;
	}

	public EmulationStatus getStatus() {
		return status;
	}

	public void setStatus(EmulationStatus status) {
		this.status = status;
	}
}
