package de.tudresden.inf.rn.mobilis.gwtemulationserver.server;

import java.util.List;

public abstract class Command {
	
	public int id;
	public String receiver; // contains app namespace and instance number
	public String methodName;
	public List<String> parameter;
	public List<String> parameterTypes;
}
