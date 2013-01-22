package de.tudresden.inf.rn.mobilis.gwtemulationserver.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.CommandAck;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.CommandRequest;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.StartAck;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.StartRequest;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.StopAck;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.StopRequest;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.AppCommandType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.InstanceType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.ParameterType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.StartType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.StopType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils.EmulationConnection;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils.EmulationSession;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanIQAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanSenderReceiver;

public class ScriptRunner extends XMLScriptExecutor {
	
	private EmulationConnection emuConnection;
	private EmulationSession session;
	private Map<String, String> instanceSelection;
	private Map<String, List<String>> instanceGroupSelection;
	
	public ScriptRunner(EmulationConnection emuConn, EmulationSession session, Map<String, String> instanceSelection, Map<String, List<String>> instanceGroupSelection) {
		super();
		this.emuConnection = emuConn;
		this.session = session;
		this.instanceSelection = instanceSelection;
		this.instanceGroupSelection = instanceGroupSelection;
	}

	@Override
	public void executeStartCommand(InstanceType instance, StartType startCommand) {
		
		String parameters = "";
		
		if(startCommand.getParameters() != null) {
			ParameterType params = startCommand.getParameters();
			List<Serializable> list = params.getIntOrStringOrBoolean();
			if(list != null) {
				for(int i=0;i<list.size();i++) {
					Serializable param = list.get(i);
					if(param instanceof Integer) {
						Integer intParam = (Integer)param;
						System.out.println("Integer-Param: " + intParam.toString());
						parameters += intParam + " ";
					}
					if(param instanceof String) {
						String stringParam = (String)param;
						System.out.println("String-Param: " + stringParam);
						parameters += stringParam + " ";
					}
					if(param instanceof Boolean) {
						Boolean boolParam = (Boolean)param;
						System.out.println("Boolean-Param: " + boolParam.toString());
						parameters += boolParam + " ";
					}
					if(param instanceof Double) {
						Double doubleParam = (Double)param;
						System.out.println("Double-Param: " + doubleParam.toString());
						parameters += doubleParam + " ";
					}
				}
				// removes last " "
				int count = parameters.length();
				parameters = parameters.substring(0, count-1);
			}
		}
		
		StartRequest startReq = new StartRequest(instance.getAppNS(), instance.getInstanceId(), parameters);
		//String sendTo = deviceAssignment.get(startCommand.getInstance());
		String sendTo = "";
		if(instanceSelection.containsKey(startCommand.getInstance())) {
			sendTo = instanceSelection.get(startCommand.getInstance());
		} else if(instanceGroupSelection.containsKey(startCommand.getInstance())) {
			List<String> selections = instanceGroupSelection.get(startCommand.getInstance());
			sendTo = selections.get(instance.getInstanceId()-1);
		}
		
		startReq.setTo(sendTo);
		
		System.out.println("StartCommand -> " + sendTo);
		BeanSenderReceiver<StartRequest, StartAck> bsr = new BeanSenderReceiver<StartRequest, StartAck>(emuConnection.getConnection());
		XMPPBean result = bsr.exchange(startReq, new StartAck(), 1);
		
		if (result != null) {
			if (result.getType() == XMPPBean.TYPE_ERROR) {
				System.err.println("Couldn't send StartCommand to " + sendTo + ": ");
				System.err.println("\tError type: " + result.errorType);
				System.err.println("\tError condition: " + result.errorCondition);
				System.err.println("\tError message: " + result.errorText);
			} else {
				System.out.println("Successfully sent StartCommand to " + sendTo);
			}
		} else {
			System.err.println("Couldn't send StartCommand to " + sendTo);
		}
		
		//emuConnection.getConnection().sendPacket(new BeanIQAdapter((XMPPBean)startReq));
	}

	@Override
	public void executeStopCommand(InstanceType instance, StopType stopCommand) {
		
		StopRequest stopReq = new StopRequest(instance.getAppNS(), instance.getInstanceId());
		//String sendTo = deviceAssignment.get(stopCommand.getInstance());
		String sendTo = "";
		if(instanceSelection.containsKey(stopCommand.getInstance())) {
			sendTo = instanceSelection.get(stopCommand.getInstance());
		} else if(instanceGroupSelection.containsKey(stopCommand.getInstance())) {
			List<String> selections = instanceGroupSelection.get(stopCommand.getInstance());
			sendTo = selections.get(instance.getInstanceId()-1);
		}
		
		stopReq.setTo(sendTo);
		
		System.out.println("StopCommand -> " + sendTo);
		BeanSenderReceiver<StopRequest, StopAck> bsr = new BeanSenderReceiver<StopRequest, StopAck>(emuConnection.getConnection());
		XMPPBean result = bsr.exchange(stopReq, new StopAck(), 1);
		
		if (result != null) {
			if (result.getType() == XMPPBean.TYPE_ERROR) {
				System.err.println("Couldn't send StopCommand to " + sendTo + ": ");
				System.err.println("\tError type: " + result.errorType);
				System.err.println("\tError condition: " + result.errorCondition);
				System.err.println("\tError message: " + result.errorText);
			} else {
				System.out.println("Successfully sent StopCommand to " + sendTo);
			}
		} else {
			System.err.println("Couldn't send StopCommand to " + sendTo);
		}
		//emuConnection.getConnection().sendPacket(new BeanIQAdapter((XMPPBean)stopReq));
	}

	@Override
	public void executeAppCommand(InstanceType instance, AppCommandType appCommand) {
		
		String methodName = appCommand.getMethodName();
		List<String> parameters = new ArrayList<String>();
		List<String> parameterTypes = new ArrayList<String>();
		Integer commandId = instance.getInstanceId();
		Integer instanceId = instance.getInstanceId();
		String appNamespace = instance.getAppNS();
		Boolean async = appCommand.isAsync();
		
		if(appCommand.getParameter() != null) {
			ParameterType params = appCommand.getParameter();
			List<Serializable> list = params.getIntOrStringOrBoolean();
			if(list != null) {
				for(int i=0;i<list.size();i++) {
					Serializable param = list.get(i);
					if(param instanceof Integer) {
						Integer intParam = (Integer)param;
						System.out.println("Integer-Param: " + intParam.toString());
						parameters.add(intParam.toString());
						parameterTypes.add("Integer");
					}
					if(param instanceof String) {
						String stringParam = (String)param;
						System.out.println("String-Param: " + stringParam);
						parameters.add(stringParam);
						parameterTypes.add("String");
					}
					if(param instanceof Boolean) {
						Boolean boolParam = (Boolean)param;
						System.out.println("Boolean-Param: " + boolParam.toString());
						parameters.add(boolParam.toString());
						parameterTypes.add("Boolean");
					}
					if(param instanceof Double) {
						Double doubleParam = (Double)param;
						System.out.println("Double-Param: " + doubleParam.toString());
						parameters.add(doubleParam.toString());
						parameterTypes.add("Double");
					}
				}
			}
		}
		
		CommandRequest commReq = new CommandRequest(methodName, parameters, parameterTypes, commandId, instanceId, appNamespace, async);
		//String sendTo = deviceAssignment.get(appCommand.getInstance());
		String sendTo = "";
		if(instanceSelection.containsKey(appCommand.getInstance())) {
			sendTo = instanceSelection.get(appCommand.getInstance());
		} else if(instanceGroupSelection.containsKey(appCommand.getInstance())) {
			List<String> selections = instanceGroupSelection.get(appCommand.getInstance());
			sendTo = selections.get(instance.getInstanceId()-1);
		}
		
		commReq.setTo(sendTo);
		
		System.out.println("AppComand: method->" + methodName + ", to->" + sendTo);
		BeanSenderReceiver<CommandRequest, CommandAck> bsr = new BeanSenderReceiver<CommandRequest, CommandAck>(emuConnection.getConnection());
		XMPPBean result = bsr.exchange(commReq, new CommandAck(), 1);
		
		if (result != null) {
			if (result.getType() == XMPPBean.TYPE_ERROR) {
				System.err.println("Couldn't send AppComand to " + sendTo + ": ");
				System.err.println("\tError type: " + result.errorType);
				System.err.println("\tError condition: " + result.errorCondition);
				System.err.println("\tError message: " + result.errorText);
			} else {
				System.out.println("Successfully sent AppComand to " + sendTo);
			}
		} else {
			System.err.println("Couldn't send AppComand to " + sendTo);
		}
		
		//emuConnection.getConnection().sendPacket(new BeanIQAdapter((XMPPBean)commReq));
		
	}

}
