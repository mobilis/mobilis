package de.tudresden.inf.rn.mobilis.gwtemulationserver.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.SessionInfo;

@RemoteServiceRelativePath("connectionService")
public interface EmuServerConnectService extends RemoteService {
	
	/*public Boolean isConnected();
	public Boolean connectServer();
	public Boolean disconnectServer();*/
	public Boolean sendCommand(String cmd);
	public List<String> getDeviceList(String id);
	public SessionInfo openSession(String id);
	public Boolean closeSession(String id);

}
