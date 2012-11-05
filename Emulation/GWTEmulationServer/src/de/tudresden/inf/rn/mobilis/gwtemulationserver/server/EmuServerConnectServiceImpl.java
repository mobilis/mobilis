package de.tudresden.inf.rn.mobilis.gwtemulationserver.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.client.EmuServerConnectService;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.CommandRequest;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.ConnectRequest;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils.SessionManager;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanFilterAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanIQAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanProviderAdapter;

public class EmuServerConnectServiceImpl extends RemoteServiceServlet implements EmuServerConnectService {
	
	private Connection connection;
	private Boolean connected = false;
	private List<String> devices = new ArrayList<String>();
	private SessionManager sessionManager = new SessionManager();;

	@Override
	public Boolean connectServer() {
		
		//connected = false;
		
		//Connection.DEBUG_ENABLED = true;
		
		// testing SessionManager
		sessionManager.getSession("");
		sessionManager.getSession("");
		sessionManager.getSession("");
		sessionManager.getSession("1");
		sessionManager.getSession("abcd");
		sessionManager.getSession("");
		
		if((connection != null) && (connection.isConnected())) {
			
			connected = true;
			System.out.println("already connected");
			
		} else {
			
			// Create the configuration for this new connection
			ConnectionConfiguration config = new ConnectionConfiguration("mobilis.inf.tu-dresden.de", 5222);
			config.setCompressionEnabled(true);
			config.setSASLAuthenticationEnabled(true);

			connection = new XMPPConnection(config);
			// Connect to the server
			try {
				connection.connect();
				System.out.println("connected to server");
			} catch (XMPPException e) {
				connected = false;
				System.out.println(e);
			}
			// Log into the server
			try {
				connection.login("emulationserver", "emulationserver", "EmulationServer");
				connected = true;
				System.out.println("logged in");
				
				XMPPBean cr = new ConnectRequest();
				
				(new BeanProviderAdapter(cr)).addToProviderManager();
				
				connection.addPacketListener(new PacketListener(){

					@Override
					public void processPacket(Packet p) {
						System.out.println("processPacket");
						if (p instanceof BeanIQAdapter) {
							System.out.println("iqAdapter");
							XMPPBean b = ((BeanIQAdapter) p).getBean();
							if (b instanceof ConnectRequest) {
								System.out.println("connectRequest");
								ConnectRequest bean = (ConnectRequest) b;
								Calendar cal = Calendar.getInstance();
								StringBuilder time = new StringBuilder();
								time.append(cal.get(Calendar.HOUR_OF_DAY));
								time.append(":");
								time.append(cal.get(Calendar.MINUTE));
								time.append(":");
								time.append(cal.get(Calendar.SECOND));
								time.append(":");
								time.append(cal.get(Calendar.MILLISECOND));
								time.append(" > ");
								if(devices.contains(bean.getFrom())) {
									devices.remove(bean.getFrom());
									System.out.println(time.toString() + bean.getFrom() + " removed");
								} else {
									devices.add(bean.getFrom());
									System.out.println(time.toString() + bean.getFrom() + " added");
								}
						   }
						}
					}
					
				}, new BeanFilterAdapter(cr));
				
			} catch (XMPPException e) {
				connected = false;
				System.out.println(e);
			}
			
		}
		
		return (connected);
		
	}

	@Override
	public Boolean disconnectServer() {
		
		//connected = true;
		
		if((connection != null) && (connection.isConnected())) {
			connection.disconnect();
			connected = false;
			devices.clear();
			System.out.println("disconnected");
		}
		
		return connected;
		
	}

	@Override
	public Boolean isConnected() {
		return connected;
	}

	@Override
	public Boolean sendCommand(String cmd) {
		
		Boolean send = false;
		
		if(isConnected()) {
			List<String> params = new ArrayList<String>();
			params.add("testParam1");
			params.add("testParam2");
			
			List<String> types = new ArrayList<String>();
			types.add("testType1");
			types.add("testType2");
			
			int methodID = 1234;
			CommandRequest commReq = new CommandRequest("startAutomaticTest",params,types,methodID);
			
			commReq.setTo("walther02@mobilis.inf.tu-dresden.de/MXA");
			commReq.setFrom(connection.getUser());
			connection.sendPacket(new BeanIQAdapter((XMPPBean)commReq));
			
			send = true;
		}
		
		return send;
		
	}

	@Override
	public List<String> getDeviceList() {
		
		return devices;
		
	}

}
