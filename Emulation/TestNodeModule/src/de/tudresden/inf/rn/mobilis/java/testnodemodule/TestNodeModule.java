package de.tudresden.inf.rn.mobilis.java.testnodemodule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import de.tudresden.inf.rn.mobilis.emulation.clientstub.CommandAck;
import de.tudresden.inf.rn.mobilis.emulation.clientstub.CommandRequest;
import de.tudresden.inf.rn.mobilis.emulation.clientstub.ConnectAck;
import de.tudresden.inf.rn.mobilis.emulation.clientstub.ConnectRequest;
import de.tudresden.inf.rn.mobilis.emulation.clientstub.ExecutionResultAck;
import de.tudresden.inf.rn.mobilis.emulation.clientstub.ExecutionResultRequest;
import de.tudresden.inf.rn.mobilis.emulation.clientstub.IEmulationIncoming;
import de.tudresden.inf.rn.mobilis.emulation.clientstub.IEmulationOutgoing;
import de.tudresden.inf.rn.mobilis.emulation.clientstub.LogRequest;
import de.tudresden.inf.rn.mobilis.emulation.clientstub.StartAck;
import de.tudresden.inf.rn.mobilis.emulation.clientstub.StartRequest;
import de.tudresden.inf.rn.mobilis.emulation.clientstub.StopAck;
import de.tudresden.inf.rn.mobilis.emulation.clientstub.StopRequest;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.XMLScriptExecutor;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.AppCommandType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.InstanceType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.ParameterType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.StartType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.StopType;
import de.tudresden.inf.rn.mobilis.xmpp.beans.IXMPPCallback;
import de.tudresden.inf.rn.mobilis.xmpp.beans.ProxyBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.helper.DoubleKeyMap;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanIQAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanProviderAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanSenderReceiver;
import de.tudresden.inf.rn.mobsda.performance.client.module.RMITestNodeModule;

public class TestNodeModule {
	
	private static String xmppServer = "mobilis.inf.tu-dresden.de";
	private static String xmppLogin = "testnodemodule";
	private static String xmppPass = "testnodemodule";
	private static String xmppResource = "";
	private static String emulationServerJid = "emulation@mobilis.inf.tu-dresden.de";
	
	private static boolean serverless = false;
	
	private static String xmppJid = "";
	
	private static XMPPConnection con;
	private static TestNodeModuleIncomingHandler xmppIncomingHandler;
	private static TestNodeModuleSender xmppSender;
	private static DoubleKeyMap<String, String, XMPPBean> beanPrototypes = new DoubleKeyMap<String, String, XMPPBean>(false);

	private static Map<String, String> appPaths = new HashMap<String, String>();
	private static Map<String, TestApplicationRunnable> appInstances = new HashMap<String, TestApplicationRunnable>();
	
	private static ExecutorService executorService = Executors.newCachedThreadPool();
	
	private static Object startMonitor = new Object();
	private static Object stopMonitor = new Object();
	private static RMITestNodeModule stub; // hold reachable to prevent GC
	private static Registry registry; // hold reachable to prevent GC
	private static String name = "TestNodeClient";
	private static RMIConnector rmiConnector;
	
	/**
	 * @param args
	 * 				Only used if running in serverless mode (set serverless=true) in
	 * 				settings file.
	 * 				### OUTDATED ###
	 * 				args[0] is the count of the application instances which shall be started,
	 * 				args[1] is the path to the actual JAR file. Any additional
	 * 				parameters are put into subsequent elements of args.
	 * 				args[1]...args[args.length] may use %in% which will be replaced by the
	 * 				application instance number at runtime.
	 * 				### OUTDATED END ###
	 *				Alternatively args[0] is the script file (ending by .xml!) to be executed
	 *				with this module. This also only works in serverless mode.
	 */
	public static void main(String[] args) {
		
		// prepare shutdown
		Thread shutdownThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				shutdown();
			}
		});
		
		Runtime.getRuntime().addShutdownHook(shutdownThread);
		
		// set up TestNodeModule
		fetchSettings();
		if (!serverless) {
			XMPPConnection.DEBUG_ENABLED = true;
			connectToXMPP();
			registerPacketListener();
			
			connectToEmulationServer();
			
			xmppIncomingHandler = new TestNodeModuleIncomingHandler();
			xmppSender = new TestNodeModuleSender();
		}
		
		// set up RMI
		try {
			LocateRegistry.createRegistry(1099);
			
			try {
				rmiConnector = new RMIConnector();
				stub = (RMITestNodeModule) UnicastRemoteObject.exportObject(rmiConnector, 0);
				registry = LocateRegistry.getRegistry();
				registry.rebind(name, stub);
				System.out.println("TestNodeClient bound on " + name);
			} catch (Exception e) {
				System.out.println("TestNodeClient exception during RMI setup:");
				e.printStackTrace();
				System.exit(1);
			}
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			System.err.println("Couldn't create RMI registry!");
			e1.printStackTrace();
			System.exit(1);
		}
		
		if (serverless) {
			if (args.length == 0) {
				System.err.println("Need more program arguments. Script not specified!");
				System.exit(1);
			}
			
			// start local mode
			if (args[0].trim().toLowerCase().endsWith(".xml")) {
				File scriptFile = new File(args[0].trim());
				
				if (new TestNodeModuleScriptExecutor().execute(scriptFile)) {
					System.out.println("Finished running script!");
				} else {
					System.err.println("Cannot read script!");
					System.exit(1);
				}
				
				System.exit(0);
				
			} 
//			else {
//				System.out.println("Starting " + args[0] + " instances of " + args[1]);
//				final String[] cmd = new String[1 + args.length];
//				cmd[0] = System.getProperty("java.home") + "/bin/java";
//				cmd[1] = "-jar";
//				for (int i = 1; i < args.length; i++) {
//					cmd[i+1] = args[i];
//				}
//				
//				int instanceCount = 0;
//				try {
//					instanceCount = Integer.parseInt(args[0]);
//				} catch (NumberFormatException e) {
//					System.err.println("Couldn't parse instance count! Assuming 1.");
//					e.printStackTrace();
//					instanceCount = 1;
//				}
//				for (int i = 1; i <= instanceCount; i++) {
//					
//					String[] cmd2 = cmd.clone();
//					for (int j = 3; j < cmd.length; j++) {
//						cmd2[j] = cmd[j].replace("%in%", String.valueOf(i));
//					}
//					
//					TestApplicationRunnable testApplicationRunnable = new TestApplicationRunnable(String.valueOf(i), cmd2);
//					appInstances.put("app"+i, testApplicationRunnable);
//					
//					new Thread(testApplicationRunnable).start();
//				}
//			}
			
		}

	}

	private static void shutdown() {
		if (executorService != null && !executorService.isShutdown() && !executorService.isTerminated()) {
			try {
				executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
				executorService.shutdownNow();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (registry != null) {
			try {
				registry.unbind(name);
				UnicastRemoteObject.unexportObject(rmiConnector, true);
			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static void fetchSettings() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("TestNodeModuleSettings.properties"));
		
			// TODO: only set values if getProperty() != null
			serverless = Boolean.parseBoolean(properties.getProperty("serverless").trim());
			
			if (!serverless) {
				xmppServer = properties.getProperty("xmppserver").trim();
				xmppLogin = properties.getProperty("xmpplogin").trim();
				xmppResource = properties.getProperty("xmppresource").trim();
				xmppPass = properties.getProperty("xmpppass").trim();
				emulationServerJid = properties.getProperty("emulationserverjid").trim();
				
			}
			
			// read application NS <-> path mappings
			for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
				String propertyName = ((String) e.nextElement()).trim();
				
				if (!propertyName.equals("serverless") &&
						!propertyName.equals("xmppserver") &&
						!propertyName.equals("xmpplogin") &&
						!propertyName.equals("xmppresource") &&
						!propertyName.equals("xmpppass") &&
						!propertyName.equals("emulationserverjid")) {
					
					String jarPathAndParams = properties.getProperty(propertyName);
					if (jarPathAndParams != null && jarPathAndParams != "") {
						appPaths.put(propertyName, jarPathAndParams);
					} else {
						System.out.println("Jar path specified for namespace " + propertyName +
								" is either empty or null. Check the TestNodeModuleSettings.properties file. Ommiting entry...");
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find settings file. Using default settings.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Couldn't read settings file. Using default settings.");
			e.printStackTrace();
		}
	}

	private static void connectToXMPP() {
		con = new XMPPConnection(xmppServer);
		try {
			con.connect();
		} catch (XMPPException e) {
			System.out.println("Couldn't connect to Openfire.");
			e.printStackTrace();
			System.exit(1);
		}
		
		try {
			if (xmppResource == null || xmppResource.equals("")) {
				con.login(xmppLogin, xmppPass);
				xmppJid = xmppLogin + "@" + xmppServer;
			} else {
				con.login(xmppLogin, xmppPass, xmppResource);
				xmppJid = xmppLogin + "@" + xmppServer + "/" + xmppResource;
			}
		} catch (XMPPException e) {
			System.out.println("Couldn't login to Openfire.");
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Successfully logged in to XMPP server " + xmppServer + " .");
	}
	
	private static void connectToEmulationServer() {
		ConnectRequest request = new ConnectRequest();
		request.setTo(emulationServerJid);
		BeanSenderReceiver<ConnectRequest, ConnectAck> bsr = new BeanSenderReceiver<>(con);
		XMPPBean resultBean = bsr.exchange(request, new ConnectAck(), 3);
		
		if (resultBean != null) {
			if (resultBean.getType() == XMPPBean.TYPE_ERROR) {
				System.err.println("Couldn't connect to Emulation Server! Error details as follows: ");
				System.err.println("\tError type: " + resultBean.errorType);
				System.err.println("\tError condition: " + resultBean.errorCondition);
				System.err.println("\tError message: " + resultBean.errorText);
				System.exit(1);
			} else {
				System.out.println("Successfully connected to Emulation Server.");
			}
		} else {
			System.err.println("Couldn't connect to Emulation Server! Exiting...");
			System.exit(1);
		}
	}
	
	private static void registerPacketListener() {
		beanPrototypes.put(CommandRequest.NAMESPACE, CommandRequest.CHILD_ELEMENT, new CommandRequest());
		
		for (XMPPBean prototype : beanPrototypes.getListOfAllValues()) {
			new BeanProviderAdapter(new ProxyBean(prototype.getNamespace(), prototype.getChildElement())).addToProviderManager();
		}

		IQListener iqListener = new IQListener();
		PacketTypeFilter filter = new PacketTypeFilter(IQ.class);
		con.addPacketListener(iqListener, filter);
	}
	
	private static XMPPBean startApp(StartRequest in, String appNS,
			int instanceID, String parameters) {
		String appPath = appPaths.get(appNS);
		
		if (appPath == null) {
			String errorText = "This TestNodeModule does not know the application namespace " + appNS + "!";
			System.out.println(errorText);
			if (in != null) {
				StartRequest error = in.buildStartError(errorText);
				return error;
			} else {
				return null;
			}
		}
		
		if (appInstances.containsKey(appNS + "_" + instanceID)) {
			String errorText = "Instance " + instanceID + " of application " + appNS + " is already running!";
			System.out.println(errorText);
			if (in != null) {
				StartRequest error = in.buildStartError(errorText);
				return error;
			} else {
				return null;
			}
		}
		
		String[] cmd = (System.getProperty("java.home") + "/bin/java -jar " + appPath + " " + parameters).trim().split(" ");
		
		TestApplicationRunnable runnable = new TestApplicationRunnable(appNS + "_" + instanceID, cmd);
		
		executorService.execute(runnable);
		
		try {
			synchronized (startMonitor) {
				startMonitor.wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		appInstances.put(appNS + "_" + instanceID, runnable);
		
		if (in != null) {
			StartAck ack = new StartAck();
			ack.setId(in.getId());
			ack.setTo(in.getFrom());
			
			return ack;
		} else {
			return null;
		}
	}

	private static XMPPBean stopApp(StopRequest in, String appNS, int instanceID) {
		TestApplicationRunnable app = appInstances.get(appNS + "_" + instanceID);
		
		if (app == null) {
			String errorText = "Instance " + instanceID + " of application " + appNS + " is not running on this TestNodeModule!";
			System.err.println(errorText);
			if (in != null) {
				StopRequest error = in.buildStopError(errorText);
				return error;
			} else {
				return null;
			}
		}
		
		appInstances.remove(appNS + "_" + instanceID);
		
		try {
			synchronized (stopMonitor) {
				app.stop();
				stopMonitor.wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (in != null) {
			StopAck ack = new StopAck();
			ack.setId(in.getId());
			ack.setTo(in.getFrom());
			
			return ack;
		} else {
			return null;
		}
	}

	private static XMPPBean executeCommand(CommandRequest in, Command command, String appNamespace, int instanceId) {
		TestApplicationRunnable instance = appInstances.get(appNamespace + "_" + instanceId);
		if (instance != null) {
			instance.postCommand(command);
		} else {
			String errorText = "Instance " + instanceId + " of application " + appNamespace + " is not running on this TestNodeModule!";
			System.err.println(errorText);
			if (in != null) {
				CommandRequest error = in.buildCommandError("errorText");
				return error;
			} else {
				return null;
			}
		}
		
		if (in != null) {
			CommandAck commandAck = new CommandAck();
			commandAck.setId(in.getId());
			commandAck.setTo(in.getFrom());
			return commandAck;
		} else {
			return null;
		}
	}

	private static class IQListener implements PacketListener {

		@Override
		public void processPacket(Packet packet) {
			if (packet instanceof BeanIQAdapter) {
				XMPPBean bean = ((BeanIQAdapter) packet).getBean();
				
				if (bean instanceof ProxyBean) {
					ProxyBean proxy = (ProxyBean) bean;
					XMPPBean outBean = null;
					
					if (proxy.isTypeOf(CommandRequest.NAMESPACE, CommandRequest.CHILD_ELEMENT)) {
						outBean = xmppIncomingHandler.onCommand((CommandRequest) proxy.parsePayload(new CommandRequest()));
					} else if (proxy.isTypeOf(LogRequest.NAMESPACE, LogRequest.CHILD_ELEMENT)) {
						xmppIncomingHandler.onLog((LogRequest) proxy.parsePayload(new LogRequest()));
					} else if (proxy.isTypeOf(StartRequest.NAMESPACE, StartRequest.CHILD_ELEMENT)) {
						xmppIncomingHandler.onStart((StartRequest) proxy.parsePayload(new StartRequest()));
					} else if (proxy.isTypeOf(StopRequest.NAMESPACE, StopRequest.CHILD_ELEMENT)) {
						xmppIncomingHandler.onStop((StopRequest) proxy.parsePayload(new StopRequest()));
					}
					
					if (outBean != null) {
						// send response
						outBean.setTo(proxy.getFrom());
						xmppSender.sendXMPPBean(outBean);
					}
					
				}
			}
		}
		
	}
	
	private static class TestNodeModuleIncomingHandler implements IEmulationIncoming {

		@Override
		public void onConnect(ConnectAck in) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onConnectError(ConnectRequest in) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public XMPPBean onCommand(CommandRequest in) {
			Command command = new Command();
			command.methodName = in.getMethodName();
			command.parameters = (String[]) in.getParameters().toArray();
			command.parameterTypes = (String[]) in.getParameterTypes().toArray();
			command.async = in.getAsync();
			return executeCommand(in, command, in.getAppNamespace(), in.getInstanceId());
		}

		@Override
		public void onExecutionResult(ExecutionResultAck in) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onExecutionResultError(ExecutionResultRequest in) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onLog(LogRequest in) {
			File log = appInstances.get(in.getAppNamespace() + "_" + in.getInstanceId()).getLogFile();
			
			if (log != null && log.canRead() && log.exists()) {
				// initiate file transfer
				FileTransferManager fileTransferManager = new FileTransferManager(con);
				OutgoingFileTransfer fileTransfer = fileTransferManager.createOutgoingFileTransfer(in.getFrom());
				try {
					fileTransfer.sendFile(log, "log for application instance " + in.getInstanceId());
				} catch (XMPPException e) {
					System.out.println("Couldn't send log file for application instance " + in.getInstanceId());
					e.printStackTrace();
				}
			} else {
				System.out.println("Log file for application instance " + in.getInstanceId() + " couldn't be read!");
			}
			
		}

		@Override
		public XMPPBean onStart(StartRequest in) {
			String appNS = in.getAppNamespace();
			int instanceID = in.getInstanceId();
			String parameters = in.getParameters();

			return startApp(in, appNS, instanceID, parameters);
		}

		@Override
		public XMPPBean onStop(StopRequest in) {
			String appNS = in.getAppNamespace();
			int instanceID = in.getInstanceId();
			
			return stopApp(in, appNS, instanceID);
		}
		
	}
	
	public static class TestNodeModuleSender implements IEmulationOutgoing {

		@Override
		public void sendXMPPBean(XMPPBean out) {
			out.setFrom(xmppJid);
			con.sendPacket(new BeanIQAdapter(out));
		}
		
		@Override
		public void sendXMPPBean(XMPPBean out,
				IXMPPCallback<? extends XMPPBean> callback) {
//			waitingCallbacks.put(out.getId(), callback);
			con.sendPacket(new BeanIQAdapter(out));
		}
		
	}
	
	private static class TestNodeModuleScriptExecutor extends XMLScriptExecutor {

		@Override
		public void executeStartCommand(InstanceType instance,
				StartType startCommand) {
			if (instance != null) {
				ParameterType jaxbParameters = startCommand.getParameters();
				String parameterString = "";
				if (jaxbParameters != null) {
					List<Serializable> parameters = jaxbParameters.getIntOrStringOrBoolean();
					for (int i = 0; i < parameters.size(); i++) {
						parameterString += parameters.get(i).toString();
					}
					parameterString.trim();
				}
				startApp(null, instance.getAppNS(), instance.getInstanceId(), parameterString);
			}
		}

		@Override
		public void executeStopCommand(InstanceType instance,
				StopType stopCommand) {
			if (instance != null) {
				stopApp(null, instance.getAppNS(), instance.getInstanceId());
			}
		}

		@Override
		public void executeAppCommand(InstanceType instance,
				AppCommandType appCommand) {
			if (instance != null) {
				Command command = new Command();
				command.methodName = appCommand.getMethodName();
				command.async = appCommand.isAsync();
				
				ParameterType jaxbParameters = appCommand.getParameter();
				if (jaxbParameters != null) {
					List<Serializable> parameters = jaxbParameters.getIntOrStringOrBoolean();
					String[] parameterStringArray = new String[parameters.size()];
					String[] parameterTypesStringArray = new String[parameters.size()];
					
					for (int i = 0; i < parameters.size(); i++) {
						Serializable parameter = parameters.get(i);
						
						parameterStringArray[i] = parameter.toString();
						parameterTypesStringArray[i] = parameter.getClass().getName();
					}
					command.parameters = parameterStringArray;
					command.parameterTypes = parameterTypesStringArray;
				} else {
					command.parameters = new String[0];
					command.parameterTypes = new String[0];
				}
				executeCommand(null, command, instance.getAppNS(), instance.getInstanceId());
			}
		}
		
	}
	
	private static class RMIConnector implements RMITestNodeModule {

		@Override
		public void notifyOfStart() throws RemoteException {
			synchronized (startMonitor) {
				startMonitor.notify();
			}
		}

		@Override
		public void notifyOfStop() throws RemoteException {
			synchronized (stopMonitor) {
				stopMonitor.notify();
			}
		}
		
	}

}
