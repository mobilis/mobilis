package de.tudresden.inf.rn.mobilis.emulationserver;

import java.util.List;

public class Script {
	
	private String applicationName; // for Android: package name, for Java: application ID within TestNodeModule app registry
	private List<Command> commands;
	private List<String> devices;
	
	public String getApplicationName() {
		return applicationName;
	}
	
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	public List<String> getDevices() {
		return devices;
	}

	public void setDevices(List<String> devices) {
		this.devices = devices;
	}
	
}
