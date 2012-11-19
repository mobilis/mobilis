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
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils.EmulationSession;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils.SessionManager;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.SessionInfo;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanFilterAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanIQAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanProviderAdapter;

public class EmuServerConnectServiceImpl extends RemoteServiceServlet implements EmuServerConnectService {
	
	private SessionManager sessionManager = new SessionManager();

	@Override
	public Boolean sendCommand(String cmd) {
		
		Boolean send = false;
		
		/*if(isConnected()) {
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
		}*/
		
		return send;
		
	}

	@Override
	public List<String> getDeviceList(String id) {
		
		EmulationSession session = sessionManager.getSession(id);
		List<String> deviceList = null;
		
		if(session != null) {
			deviceList = session.getDeviceList();
		}
		return deviceList;
		
	}

	@Override
	public SessionInfo openSession(String id) {
		
		//Connection.DEBUG_ENABLED = true;
		
		EmulationSession session = sessionManager.getSession(id);
		SessionInfo info;
		
		if(session != null) {
			Boolean connected = session.connect();
			String sessionID = session.getId();
			if(connected) {
				info = new SessionInfo(connected,sessionID);
			} else {
				info = new SessionInfo(connected,sessionID,"Session with ID " + id + " couldn't connect to XMPP-Server");
			}
		} else{
			info = new SessionInfo(false,"","Session with ID " + id + " don't exist");
		}
		
		return info;
		
	}

	@Override
	public Boolean closeSession(String id) {
		
		EmulationSession session = sessionManager.getSession(id);
		session.disconnect();
		sessionManager.deleteSession(id);
		
		return true;
		
	}

}
