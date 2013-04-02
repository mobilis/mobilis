package de.tudresden.inf.rn.mobilis.gwtemulationserver.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SessionList implements Serializable {
	
	private List<Long> id;
	private List<Long> startTime;
	private List<Long> endTime;
	private List<String> script;
	private List<List<String>> devices;
	private List<String> sessionDir;
	private List<Integer> finished;
	private List<Integer> notFinished;
	
	public SessionList() {
		
		this.id = new ArrayList<Long>();
		this.startTime = new ArrayList<Long>();
		this.endTime = new ArrayList<Long>();
		this.script = new ArrayList<String>();
		this.devices = new ArrayList<List<String>>();
		this.sessionDir = new ArrayList<String>();
		this.finished = new ArrayList<Integer>();
		this.notFinished = new ArrayList<Integer>();
		
	}
	
	public void addId(Long id) {
		this.id.add(id);
	}
	
	public void addStartTime(Long start) {
		this.startTime.add(start);
	}
	
	public void addEndTime(Long end) {
		this.endTime.add(end);
	}
	
	public void addScript(String script) {
		this.script.add(script);
	}
	
	public void addDevices(List<String> devices) {
		this.devices.add(devices);
	}
	
	public void addSessionDir(String dir) {
		this.sessionDir.add(dir);
	}
	
	public void addFinished(Integer finished) {
		this.finished.add(finished);
	}
	
	public void addNotFinished(Integer notFinished) {
		this.notFinished.add(notFinished);
	}

	public List<Long> getId() {
		return id;
	}

	public void setId(List<Long> id) {
		this.id = id;
	}

	public List<Long> getStartTime() {
		return startTime;
	}

	public void setStartTime(List<Long> startTime) {
		this.startTime = startTime;
	}

	public List<Long> getEndTime() {
		return endTime;
	}

	public void setEndTime(List<Long> endTime) {
		this.endTime = endTime;
	}

	public List<String> getScript() {
		return script;
	}

	public void setScript(List<String> script) {
		this.script = script;
	}

	public List<List<String>> getDevices() {
		return devices;
	}

	public void setDevices(List<List<String>> devices) {
		this.devices = devices;
	}

	public List<String> getSessionDir() {
		return sessionDir;
	}

	public void setSessionDir(List<String> sessionDir) {
		this.sessionDir = sessionDir;
	}

	public List<Integer> getFinished() {
		return finished;
	}

	public void setFinished(List<Integer> finished) {
		this.finished = finished;
	}

	public List<Integer> getNotFinished() {
		return notFinished;
	}

	public void setNotFinished(List<Integer> notFinished) {
		this.notFinished = notFinished;
	}

}
