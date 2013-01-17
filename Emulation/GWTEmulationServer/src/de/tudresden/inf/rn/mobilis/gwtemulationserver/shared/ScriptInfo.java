package de.tudresden.inf.rn.mobilis.gwtemulationserver.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptInfo implements Serializable {
	
	private List<String> instances;
	private Map<String,Integer> instanceGroups;
	
	public ScriptInfo() {
		
		this.instances = new ArrayList<String>();
		this.instanceGroups = new HashMap<String, Integer>();
		
	}
	
	public ScriptInfo(List<String> instances, Map<String,Integer> instanceGroups) {
		
		this.instances = new ArrayList<String>();
		this.instanceGroups = new HashMap<String, Integer>();
		
		this.setInstances(instances);
		this.setInstanceGroups(instanceGroups);
		
	}
	
	public void addInstance(String instance) {
		this.instances.add(instance);
	}
	
	public void addInstanceGroup(String instance, Integer count) {
		this.instanceGroups.put(instance, count);
	}

	public List<String> getInstances() {
		return instances;
	}

	public void setInstances(List<String> instances) {
		this.instances = instances;
	}

	public Map<String, Integer> getInstanceGroups() {
		return instanceGroups;
	}

	public void setInstanceGroups(Map<String, Integer> instanceGroups) {
		this.instanceGroups = instanceGroups;
	}

}
