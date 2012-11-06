package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans.ConnectRequest;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanFilterAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanIQAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanProviderAdapter;

/**
 * 
 * Class EmulationSession represents an Emulation Session that is managed in the SessionManager.
 * 
 * @author Thomas Walther
 *
 */
public class EmulationSession {
	
	private Connection connection;
	private Boolean connected;
	private List<String> devices;
	private String id;
	
	public EmulationSession(String id) {
		this.id = id;
		devices = new ArrayList<String>();
		connected = false;
		connection = null;
	}
	
	public String getId() {
		return id;
	}
	
	public List<String> getDeviceList() {
		return devices;
	}
	
	public Boolean connect() {
		
		if( (connection != null) && (connection.isConnected()) ) {
			
			connected = true;
			System.out.println("already connected");
			
		} else {
			
			if( connection == null ) {
				// Create the configuration for this new connection
				ConnectionConfiguration config = new ConnectionConfiguration("mobilis.inf.tu-dresden.de", 5222);
				config.setCompressionEnabled(true);
				config.setSASLAuthenticationEnabled(true);

				connection = new XMPPConnection(config);
			}
			
			// Connect to the server
			try {
				connection.connect();
				System.out.println("connected to server");
			} catch (XMPPException e) {
				connected = false;
				System.out.println("error connecting to server: " + e.getMessage());
			}
			// Log into the server
			try {
				connection.login("emulationserver", "emulationserver", "EmulationServer"+id);
				connected = true;
				System.out.println("logged in");
				
				// Bean handling
				// TODO: export to handler class
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
				System.out.println("error logging in: " + e.getMessage());
			}
			
		}
		
		return connected;
	}
	
	public Boolean disconnect() {
		
		if((connection != null) && (connection.isConnected())) {
			connection.disconnect();
			connected = false;
			devices.clear();
			System.out.println("disconnected");
		}
		
		return connected;
		
	}

}
