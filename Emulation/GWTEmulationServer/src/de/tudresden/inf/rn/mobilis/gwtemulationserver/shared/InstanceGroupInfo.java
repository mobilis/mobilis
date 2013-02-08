package de.tudresden.inf.rn.mobilis.gwtemulationserver.shared;

import java.io.Serializable;

public class InstanceGroupInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String varName;
	private int count;
	private int firstInstanceId;
	
	@SuppressWarnings("unused")
	private InstanceGroupInfo() {
		this.varName = "";
		this.count = 0;
		this.firstInstanceId = 0;
	}
	
	public InstanceGroupInfo(String varName, int count, int firstInstanceId) {
		this.varName = varName;
		this.count = count;
		this.firstInstanceId = firstInstanceId;
	}
	
	public String getVarName() {
		return varName;
	}
	
	public void setVarName(String varName) {
		this.varName = varName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getFirstInstanceId() {
		return firstInstanceId;
	}

	public void setFirstInstanceId(int firstInstanceId) {
		this.firstInstanceId = firstInstanceId;
	}

}
