package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	private List<String> devices;
	private Long startTime;
	private Long endTime;
	private EmulationStatus status;
	
	public EmulationSession() {
	}
	
	public EmulationSession(String script, String sessionDir, List<String> devices) {
		this.script = script;
		this.sessionDir = sessionDir;
		this.devices = devices;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
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
	
	public void setDevices(List<String> devices) {
		this.devices = devices;
	}
	
	/*@ElementCollection
	@CollectionTable(name="devices", joinColumns=@JoinColumn(name="dev_id"))
	@Column(name="device")*/
	@Column(name="device")
	@ElementCollection
	//@Fetch(FetchMode.SUBSELECT)
	public List<String> getDevices() {
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

	@ManyToOne(cascade=CascadeType.ALL)
	public EmulationStatus getStatus() {
		return status;
	}

	public void setStatus(EmulationStatus status) {
		this.status = status;
	}
}
