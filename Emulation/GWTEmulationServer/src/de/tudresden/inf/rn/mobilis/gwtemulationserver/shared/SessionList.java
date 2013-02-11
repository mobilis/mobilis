package de.tudresden.inf.rn.mobilis.gwtemulationserver.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SessionList implements Serializable {
	
	private List<Long> id;
	private List<Long> startTime;
	private List<Long> endTime;
	private List<String> script;
	
	public SessionList() {
		
		this.id = new ArrayList<Long>();
		this.startTime = new ArrayList<Long>();
		this.endTime = new ArrayList<Long>();
		this.script = new ArrayList<String>();
		
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

}
