package de.tudresden.inf.rn.mobilis.gwtemulationserver.server;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.dev.util.collect.HashMap;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.AbstractInstanceType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.AppCommandType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.BlockType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.CommandType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.ForType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.InstanceGroupType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.InstanceType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.ParameterType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.Script;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.StartType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.StopType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.WaitType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.notgen.InstanceGroupImpl;

public abstract class XMLScriptExecutor {
	
	private Map<String, AbstractInstanceType> instances = new HashMap<String, AbstractInstanceType>();
	
	public boolean execute(File scriptFile) {
		if (!scriptFile.exists()) {
			System.err.println("Couldn't read script file!");
			return false;
		}
		System.out.println("Parsing script file...");
		XMLScriptParser parser = new XMLScriptParser();
		Script script = parser.parse(scriptFile);
		for (int i = 0; i < script.getCommand().size(); i++) {
			CommandType command = script.getCommand().get(i).getValue();
			executeCommand(command);
		}
		return true;
	}
	
	private void executeCommand(CommandType command) {
		if (command instanceof InstanceType) {
			InstanceType instanceDef = (InstanceType) command;
			instances.put(instanceDef.getVarName(), instanceDef);
		} else if (command instanceof InstanceGroupType) {
			InstanceGroupImpl instanceGroup = new InstanceGroupImpl((InstanceGroupType) command);
			instances.put(instanceGroup.getVarName(), instanceGroup);
		} else if (command instanceof StartType) {
			StartType startCommand = (StartType) command;
			AbstractInstanceType instance = instances.get(startCommand.getInstance());
			if (instance instanceof InstanceType) {
				executeStartCommand((InstanceType) instance, startCommand);
			} else if (instance instanceof InstanceGroupImpl) {
				InstanceGroupImpl instanceGroup = (InstanceGroupImpl) instance;
				for (InstanceType virtualInstance : instanceGroup.getInstances()) {
					executeStartCommand(virtualInstance, startCommand);
				}
			} else {
				System.err.println("Ingoring command as instance type is unknown!");
			}
		} else if (command instanceof StopType) {
			StopType stopCommand = (StopType) command;
			AbstractInstanceType instance = instances.get(stopCommand.getInstance());
			if (instance instanceof InstanceType) {
				executeStopCommand((InstanceType) instance, stopCommand);
			} else if (instance instanceof InstanceGroupImpl) {
				InstanceGroupImpl instanceGroup = (InstanceGroupImpl) instance;
				for (InstanceType virtualInstance : instanceGroup.getInstances()) {
					executeStopCommand(virtualInstance, stopCommand);
				}
			} else {
				System.err.println("Ingoring command as instance type is unknown!");
			}
		} else if (command instanceof AppCommandType) {
			AppCommandType appCommand = (AppCommandType) command;
			
			ParameterType jaxbParams = appCommand.getParameter();
			List<Serializable> params = null;
			if (jaxbParams != null) {
				params = jaxbParams.getIntOrStringOrBoolean();
			}

			// take care of parameters containing %id% placeholders
			List<Integer> placeholderParams = new ArrayList<Integer>();
			if (params != null) {
				
				for (int i = 0; i < params.size(); i++) {
					Serializable parameter = params.get(i);
					if (parameter instanceof String) {
						if (((String) parameter).contains("%id%")) {
							placeholderParams.add(i);
						}
					}
				}
			}
			
			AbstractInstanceType instance = instances.get(appCommand.getInstance());
			if (instance instanceof InstanceType) {
				if (params != null) {
					for (int index : placeholderParams) {
						params.set(index, ((String) params.get(index)).replace("%id%", String.valueOf(((InstanceType) instance).getInstanceId())));
					}
				}
				executeAppCommand((InstanceType) instance, appCommand);
			} else if (instance instanceof InstanceGroupImpl) {
				InstanceGroupImpl instanceGroup = (InstanceGroupImpl) instance;
				List<Serializable> paramsCopy = new ArrayList<Serializable>(params);
				for (InstanceType virtualInstance : instanceGroup.getInstances()) {
					if (params != null) {
						for (int index : placeholderParams) {
							params.set(index, ((String) paramsCopy.get(index)).replace("%id%", String.valueOf(virtualInstance.getInstanceId())));
						}
					}
					executeAppCommand(virtualInstance, appCommand);
				}
			} else {
				System.err.println("Ingoring command as instance type is unknown!");
			}
		} else if (command instanceof WaitType) {
			try {
				Thread.sleep(((WaitType) command).getTime());
			} catch (InterruptedException e) {
				System.err.println("Sleep timer has been interrupted!");
				e.printStackTrace();
			}
		} else if (command instanceof ForType) {
			ForType forStructure = (ForType) command;
			for (int i = 0; i < forStructure.getTimes(); i++) {
				for (int j = 0; j < forStructure.getCommand().size(); j++) {
					CommandType innerCommand = forStructure.getCommand().get(j).getValue();
					executeCommand(innerCommand);
				}
			}
		} else if (command instanceof BlockType) {
			// TODO: send all commands for block execution - implementation details not completely defined atm
		}
	}
	
	public abstract void executeStartCommand(InstanceType instance, StartType startCommand);
	public abstract void executeStopCommand(InstanceType instance, StopType stopCommand);
	public abstract void executeAppCommand(InstanceType instance, AppCommandType appCommand);
}
