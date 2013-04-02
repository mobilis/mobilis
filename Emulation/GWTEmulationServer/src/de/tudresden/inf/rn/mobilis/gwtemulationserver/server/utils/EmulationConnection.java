package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.CommandAck;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.CommandRequest;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.ConnectAck;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.ConnectRequest;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.DisconnectRequest;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.ExecutionResultAck;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.ExecutionResultRequest;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.IEmulationIncoming;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.IEmulationOutgoing;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.StartAck;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.StartRequest;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.StopAck;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.StopRequest;
import de.tudresden.inf.rn.mobilis.xmpp.beans.IXMPPCallback;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.helper.DoubleKeyMap;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanIQAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanProviderAdapter;

public class EmulationConnection {
	
	private static final String HOST = "localhost";
	private static final String USER = "emulationserver";
	private static final String PASS = "emulationserver";
	private static final String RESSOURCE = "EmulationServer";
	private static final String TAG = "EmulationConnection";
	
	private XMPPConnection connection;
	private EmulationIncoming incoming = new EmulationIncoming();
	private EmulationOutgoing outgoing = new EmulationOutgoing();
	private DoubleKeyMap<String, String, XMPPBean> beans = new DoubleKeyMap<String, String, XMPPBean>(false);
	private Boolean connected;
	private List<String> devices;
	
	public EmulationConnection() {
		devices = new ArrayList<String>();
		connected = false;
		connection = null;
	}
	
	public List<String> getDeviceList() {
		return devices;
	}
	
	public void removeDevice(int index) {
		devices.remove(index);
	}
	
	public XMPPConnection getConnection() {
		return connection;
	}
	
	public Boolean connect() {
		
		if( (connection != null) && (connection.isConnected()) ) {
			
			connected = true;
			System.out.println(TAG + ": already connected");
			
		} else {
			
			if( connection == null ) {
				// Create the configuration for this new connection
				ConnectionConfiguration config = new ConnectionConfiguration(HOST, 5222);
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
				connection.login(USER, PASS, RESSOURCE);
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
	
	private void registerPacketListener() {
		
		beans.put(ConnectRequest.NAMESPACE, ConnectRequest.CHILD_ELEMENT, new ConnectRequest());
		beans.put(DisconnectRequest.NAMESPACE, DisconnectRequest.CHILD_ELEMENT, new DisconnectRequest());
		beans.put(ExecutionResultRequest.NAMESPACE, ExecutionResultRequest.CHILD_ELEMENT, new ExecutionResultRequest());
		
		for(XMPPBean b : beans.getListOfAllValues()) {
			new BeanProviderAdapter(b).addToProviderManager();
		}
		
		IQListener listener = new IQListener();
		PacketTypeFilter filter = new PacketTypeFilter(IQ.class);
		
		connection.addPacketListener(listener, filter);
		
	}
	
	private IXMPPCallback<StartAck> startCallback = new IXMPPCallback<StartAck>() {

		@Override
		public void invoke(StartAck xmppBean) {
			if(xmppBean.getType() == XMPPBean.TYPE_ERROR) {
				System.out.println("Couldn't execute StartCommand!");
			}
		}
	};
	
	private class IQListener implements PacketListener {

		@Override
		public void processPacket(Packet p) {
			
			//System.out.println(TAG + ": processPacket: " + p.getFrom());
			if (p instanceof BeanIQAdapter) {
				
				XMPPBean b = ((BeanIQAdapter) p).getBean();
				System.out.println(TAG + ": bean check: " + b.getChildElement());
				
				if (b instanceof ConnectRequest) {
					// handle ConnectRequest
					System.out.println(TAG + ": connectRequest");
					ConnectRequest conReq = (ConnectRequest) b;
										
					if(!devices.contains(conReq.getFrom())) {
						devices.add(conReq.getFrom());
						System.out.println(TAG + ": " + conReq.getFrom() + " added");
					}
					
					// send back ConnectAck
					XMPPBean ackBean = incoming.onConnect(conReq);
					outgoing.sendXMPPBean(ackBean);
					
			   } else if (b instanceof DisconnectRequest) {
				   // handle DisconnectRequest
				   System.out.println(TAG + ": disconnectRequest");
				   DisconnectRequest disReq = (DisconnectRequest) b;
				   
				   if(devices.contains(disReq.getFrom())) {
						devices.remove(disReq.getFrom());
						System.out.println(TAG + ": " + disReq.getFrom() + " removed");
					}
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
				System.out.println();
			}
			
		}
		
	}
	
	private class EmulationIncoming implements IEmulationIncoming {

		@Override
		public XMPPBean onConnect(ConnectRequest in) {
			ConnectAck connAck = new ConnectAck();
			connAck.setFrom(in.getTo());
			connAck.setTo(in.getFrom());
			connAck.setId(in.getId());
			return connAck;
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
			ExecutionResultAck ack = new ExecutionResultAck();
			ack.setFrom(in.getTo());
			ack.setTo(in.getFrom());
			ack.setId(in.getId());
			return ack;
		}

		@Override
		public void onStart(StartAck in) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStartError(StartRequest in) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStop(StopAck in) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopError(StopRequest in) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDisconnect(DisconnectRequest in) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class EmulationOutgoing implements IEmulationOutgoing {

		@Override
		public void sendXMPPBean(XMPPBean out, IXMPPCallback<? extends XMPPBean> callback) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sendXMPPBean(XMPPBean out) {
			connection.sendPacket(new BeanIQAdapter(out));
		}
		
	}

}
