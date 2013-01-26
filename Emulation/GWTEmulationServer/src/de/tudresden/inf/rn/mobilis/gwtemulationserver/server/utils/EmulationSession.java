package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Class EmulationSession represents an Emulation Session that is managed in the SessionManager.
 * 
 * @author Thomas Walther
 *
 */
public class EmulationSession {
	
	private List<String> devicesInUse;
	private String id;
	private String script;
	private String sessionDir;
	private long startTime;
	private long endTime;
	
	public EmulationSession(String id, String sessionDir) {
		this.id = id;
		devicesInUse = new ArrayList<String>();
		this.sessionDir = sessionDir;
	}
	
	public String getId() {
		return id;
	}
	
	public List<String> getDeviceList() {
		return devicesInUse;
	}
	
	public void addDeviceList(List<String> deviceList) {
		devicesInUse = deviceList;
	}
	
	public void setScriptName(String script) {
		this.script = script;
	}
	
	public String getScriptName() {
		return script;
	}
	
	public void setSessionDir(String sessionDir) {
		this.sessionDir = sessionDir;
	}
	
	public String getSessionDir() {
		return sessionDir;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	/*public Connection getConnection() {
		return connection;
	}
	
	public Boolean connect() {
		
		if( (connection != null) && (connection.isConnected()) ) {
			
			connected = true;
			System.out.println(TAG + ": already connected");
			
		} else {
			
			if( connection == null ) {
				// Create the configuration for this new connection
				ConnectionConfiguration config = new ConnectionConfiguration("quer-dd", 5222);
				config.setCompressionEnabled(true);
				config.setSASLAuthenticationEnabled(true);

				connection = new XMPPConnection(config);
			}
			
			// Connect to the server
			try {
				connection.connect();
				System.out.println(TAG + ": connected to server");
			} catch (XMPPException e) {
				connected = false;
				System.out.println(TAG + ": error connecting to server: " + e.getMessage());
			}
			// Log into the server
			try {
				connection.login("emulationserver", "emulationserver", "EmulationServer"+id);
				connected = true;
				System.out.println(TAG + ": logged in");
				
				registerPacketListener();
				
			} catch (XMPPException e) {
				connected = false;
				System.out.println(TAG + ": error logging in: " + e.getMessage());
			}
			
		}
		
		return connected;
	}
	
	public Boolean disconnect() {
		
		if((connection != null) && (connection.isConnected())) {
			connection.disconnect();
			connected = false;
			devices.clear();
			System.out.println(TAG + ": disconnected");
		}
		
		return connected;
		
	}
	
	public Boolean send() {
		Boolean sent = false;
		
		if(connected) {
			
			int methodID = 1234;
			Boolean x = true;
			
			for(String device:devices) {
				List<String> params = new ArrayList<String>();
				List<String> types = new ArrayList<String>();
				String user = device.split("@")[0];
				Integer d = devices.size();
				
				params.add(user);
				types.add("String");
				params.add(x.toString());
				types.add("Boolean");
				params.add(d.toString());
				types.add("Integer");
				
				CommandRequest commReq = new CommandRequest("startAutomaticTest",params,types,methodID, "");
				commReq.setFrom(connection.getUser());
				
				commReq.setTo(device);
				connection.sendPacket(new BeanIQAdapter((XMPPBean)commReq));
				
				System.out.println(TAG + ": sendCommand to " + device + " with params: " + params.toString() + " of types: " + types.toString());
				x = false;
			}
			
			sent = true;
		}
		
		return sent;
	}
	
	private void registerPacketListener() {
		
		beans.put(ConnectRequest.NAMESPACE, ConnectRequest.CHILD_ELEMENT, new ConnectRequest());
		
		for(XMPPBean b : beans.getListOfAllValues()) {
			new BeanProviderAdapter(b).addToProviderManager();
		}
		
		IQListener listener = new IQListener();
		PacketTypeFilter filter = new PacketTypeFilter(IQ.class);
		
		connection.addPacketListener(listener, filter);
		
	}
	
	private class IQListener implements PacketListener {

		@Override
		public void processPacket(Packet p) {
			
			System.out.println(TAG + ": processPacket");
			if (p instanceof BeanIQAdapter) {
				
				XMPPBean b = ((BeanIQAdapter) p).getBean();
				System.out.println(TAG + ": bean check");
				
				if (b instanceof ConnectRequest) {
					// handle ConnectRequest
					System.out.println(TAG + ": connectRequest");
					ConnectRequest conReq = (ConnectRequest) b;
					Calendar cal = Calendar.getInstance();
					StringBuilder time = new StringBuilder();
					time.append(cal.get(Calendar.HOUR_OF_DAY));
					time.append(":");
					time.append(cal.get(Calendar.MINUTE));
					time.append(":");
					time.append(cal.get(Calendar.SECOND));
					time.append(":");
					time.append(cal.get(Calendar.MILLISECOND));
					if(devices.contains(conReq.getFrom())) {
						devices.remove(conReq.getFrom());
						System.out.println(TAG + ": " + time.toString() + " > " + conReq.getFrom() + " removed");
					} else {
						devices.add(conReq.getFrom());
						System.out.println(TAG + ": " + time.toString() + " > " + conReq.getFrom() + " added");
					}
					
					// send back ConnectAck
					XMPPBean ackBean = incoming.onConnect(conReq);
					ackBean.setFrom(conReq.getTo());
					ackBean.setTo(conReq.getFrom());
					outgoing.sendXMPPBean(ackBean);
					
			   } else if (b instanceof ExecutionResultRequest) {
				   // handle ExecutionResultRequest
				   System.out.println(TAG + ": executionResultRequest");
				   ExecutionResultRequest exResReq = (ExecutionResultRequest) b;
				   
				   // send back ExecutionResultAck
				   XMPPBean ackBean = incoming.onExecutionResult(exResReq);
				   ackBean.setFrom(exResReq.getTo());
				   ackBean.setTo(exResReq.getFrom());
				   outgoing.sendXMPPBean(ackBean);
			   }
			}
			
		}
		
	}
	
	private class EmulationIncoming implements IEmulationIncoming {

		@Override
		public XMPPBean onConnect(ConnectRequest in) {
			return new ConnectAck();
		}

		@Override
		public void onCommand(CommandAck in) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCommandError(CommandRequest in) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public XMPPBean onExecutionResult(ExecutionResultRequest in) {
			return new ExecutionResultAck();
		}
		
	}
	
	private class EmulationOutgoing implements IEmulationOutgoing {

		@Override
		public void sendXMPPBean(XMPPBean out,
				IXMPPCallback<? extends XMPPBean> callback) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sendXMPPBean(XMPPBean out) {
			connection.sendPacket(new BeanIQAdapter(out));
		}
		
	}*/

}
