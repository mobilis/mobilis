package de.tudresden.inf.rn.mobilis.gwtemulationserver.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.InstanceGroupExecutorInfo;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.ScriptInfo;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.SessionList;

@RemoteServiceRelativePath("connectionService")
public interface EmuServerConnectService extends RemoteService {
	
	/*public Boolean isConnected();
	public Boolean connectServer();
	public Boolean disconnectServer();*/
	public Boolean sendCommand(String cmd);
	public List<String> getDeviceList();
	//public SessionInfo openSession(String id);
	//public Boolean closeSession(String id);
	public List<String> getScriptList();
	public ScriptInfo getNeededDevices(String script);
	public Boolean startScript(String script, Map<String, String> instanceSelection, Map<String, InstanceGroupExecutorInfo> instanceGroupSelection);
	public SessionList getSessionList();
	
}
