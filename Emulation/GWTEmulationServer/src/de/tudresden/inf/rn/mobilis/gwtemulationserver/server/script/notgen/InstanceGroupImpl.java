package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.notgen;

import java.util.ArrayList;
import java.util.List;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.InstanceGroupType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.InstanceType;

public class InstanceGroupImpl extends InstanceGroupType {

	private List<InstanceType> instances = new ArrayList<InstanceType>();
	
	public InstanceGroupImpl(InstanceGroupType instanceGroup) {
		for (int i = instanceGroup.getFirstInstanceId(); i < instanceGroup.getInstanceCount() - instanceGroup.getFirstInstanceId() + 2; i++) {
			InstanceType instance = new InstanceType();
			instance.setAppNS(instanceGroup.getAppNS());
			instance.setInstanceId(i);
			instances.add(instance);
		}
		this.setAppNS(instanceGroup.getAppNS());
		this.setFirstInstanceId(instanceGroup.getFirstInstanceId());
		this.setInstanceCount(instanceGroup.getInstanceCount());
		this.setVarName(instanceGroup.getVarName());
	}
	
	public List<InstanceType> getInstances() {
		return instances;
	}
	
}
