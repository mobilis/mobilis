package de.tudresden.inf.rn.mobilis.gwtemulationserver.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private List<String> instances;
	private Map<String,InstanceGroupInfo> instanceGroups;
	
	public ScriptInfo() {
		
		this.instances = new ArrayList<String>();
		this.instanceGroups = new HashMap<String, InstanceGroupInfo>();
		
	}
	
	public ScriptInfo(List<String> instances, Map<String,InstanceGroupInfo> instanceGroups) {
		
		this.instances = new ArrayList<String>();
		this.instanceGroups = new HashMap<String, InstanceGroupInfo>();
		
		this.setInstances(instances);
		this.setInstanceGroups(instanceGroups);
		
	}
	
	public void addInstance(String instance) {
		this.instances.add(instance);
	}
	
	public void addInstanceGroup(String instance, InstanceGroupInfo instanceGroup) {
		this.instanceGroups.put(instance, instanceGroup);
	}

	public List<String> getInstances() {
		return instances;
	}

	public void setInstances(List<String> instances) {
		this.instances = instances;
	}

	public Map<String, InstanceGroupInfo> getInstanceGroups() {
		return instanceGroups;
	}

	public void setInstanceGroups(Map<String, InstanceGroupInfo> instanceGroups) {
		this.instanceGroups = instanceGroups;
	}

}
