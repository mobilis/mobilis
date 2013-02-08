package de.tudresden.inf.rn.mobilis.gwtemulationserver.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InstanceGroupExecutorInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String varName;
	private List<String> executors;
	private int firstInstanceId;
	
	public InstanceGroupExecutorInfo() {
		this.varName = "";
		this.executors = new ArrayList<String>();
		this.firstInstanceId = -1;
	}
	
	public InstanceGroupExecutorInfo(String varName, List<String> executors, int firstInstanceId) {
		this.varName = varName;
		this.executors = executors;
		this.firstInstanceId = firstInstanceId;
	}
	
	public String getVarName() {
		return varName;
	}
	
	public void setVarName(String varName) {
		this.varName = varName;
	}

	public List<String> getExecutors() {
		return executors;
	}

	public void setExecutors(List<String> executors) {
		this.executors = executors;
	}

	public int getFirstInstanceId() {
		return firstInstanceId;
	}

	public void setFirstInstanceId(int firstInstanceId) {
		this.firstInstanceId = firstInstanceId;
	}
	
}
