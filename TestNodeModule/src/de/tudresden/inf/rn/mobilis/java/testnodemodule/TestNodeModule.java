package de.tudresden.inf.rn.mobilis.java.testnodemodule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
import de.tudresden.inf.rn.mobilis.emulation.clientstub.StartRequest;
import de.tudresden.inf.rn.mobilis.emulation.clientstub.StopRequest;
import de.tudresden.inf.rn.mobilis.xmpp.beans.IXMPPCallback;
import de.tudresden.inf.rn.mobilis.xmpp.beans.ProxyBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.helper.DoubleKeyMap;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanIQAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanProviderAdapter;

public class TestNodeModule {
	
	private static String xmppServer = "mobilis.inf.tu-dresden.de";
	private static String xmppLogin = "testnodemodule";
	private static String xmppPass = "testnodemodule";
	private static String xmppResource = "";
	
	private static String xmppJid = "";
	
	private static XMPPConnection con;
	private static TestNodeModuleIncomingHandler xmppIncomingHandler;
	private static TestNodeModuleSender xmppSender;
	private static DoubleKeyMap<String, String, XMPPBean> beanPrototypes = new DoubleKeyMap<String, String, XMPPBean>(false);

	private static Map<String, TestApplicationRunnable> appInstances = new HashMap<String, TestApplicationRunnable>();
	
	/**
	 * @param args
	 * 				args[0] is the count of the application instances which shall be started,
	 * 				args[1] is the path to the actual JAR file. Any additional
	 * 				parameters are put into subsequent elements of args.
	 * 				args[1]...args[args.length] may use %in% which will be replaced by the
	 * 				application instance number at runtime.
	 */
	public static void main(String[] args) {
		
		fetchSettings();
		
		connectToXMPP();
		registerPacketListener();
		
		xmppIncomingHandler = new TestNodeModuleIncomingHandler();
		xmppSender = new TestNodeModuleSender();
		
		// set up RMI
		try {
			LocateRegistry.createRegistry(1099);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			System.out.println("Couldn't create RMI registry!");
			e1.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Starting " + args[0] + " instances of " + args[1]);
		final String[] cmd = new String[1 + args.length];
		cmd[0] = System.getProperty("java.home") + "/bin/java";
		cmd[1] = "-jar";
		for (int i = 1; i < args.length; i++) {
			cmd[i+1] = args[i];
		}
		
		int instanceCount = 0;
		try {
			instanceCount = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.out.println("Couldn't parse instance count! Assuming 1.");
			e.printStackTrace();
			instanceCount = 1;
		}
		for (int i = 1; i <= instanceCount; i++) {
			
			String[] cmd2 = cmd.clone();
			for (int j = 3; j < cmd.length; j++) {
				cmd2[j] = cmd[j].replace("%in%", String.valueOf(i));
			}
			
			TestApplicationRunnable testApplicationRunnable = new TestApplicationRunnable(i, cmd2);
			appInstances.put("app"+i, testApplicationRunnable);
			
			new Thread(testApplicationRunnable).start();
			
			// TODO: remove this test code
//			try {
//				Thread.sleep(3000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			testApplicationRunnable.stop();
		}

	}
	
	private static void fetchSettings() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("TestNodeModuleSettings.properties"));
		} catch (FileNotFoundException e) {
			System.out.println("Couldn\'t find settings file. Using default settings.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Couldn\'t read settings file. Using default settings.");
			e.printStackTrace();
		}
		
		xmppServer = properties.getProperty("xmppserver").trim();
		xmppLogin = properties.getProperty("xmpplogin").trim();
		xmppResource = properties.getProperty("xmppresource").trim();
		xmppPass = properties.getProperty("xmpppass").trim();
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
	
	private static void registerPacketListener() {
		beanPrototypes.put(CommandRequest.NAMESPACE, CommandRequest.CHILD_ELEMENT, new CommandRequest());
		
		for (XMPPBean prototype : beanPrototypes.getListOfAllValues()) {
			new BeanProviderAdapter(new ProxyBean(prototype.getNamespace(), prototype.getChildElement())).addToProviderManager();
		}

		IQListener iqListener = new IQListener();
		PacketTypeFilter filter = new PacketTypeFilter(IQ.class);
		con.addPacketListener(iqListener, filter);
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
			appInstances.get(in.getInstanceId()).postCommand(command);
			return new CommandAck();
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
			File log = appInstances.get(in.getInstanceId()).getLogFile();
			
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public XMPPBean onStop(StopRequest in) {
			// TODO Auto-generated method stub
			return null;
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

}
