package de.tudresden.inf.rn.mobilis.gwtemulationserver.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.InstanceGroupExecutorInfo;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.ScriptInfo;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.SessionInfo;

public interface EmuServerConnectServiceAsync {
	
	/*public void isConnected(AsyncCallback<Boolean> callback);
	public void connectServer(AsyncCallback<Boolean> callback);
	public void disconnectServer(AsyncCallback<Boolean> callback);*/
	public void sendCommand(String cmd, AsyncCallback<Boolean> callback);
	public void getDeviceList(String id, AsyncCallback<List<String>> callback);
	public void openSession(String id, AsyncCallback<SessionInfo> callback);
	public void closeSession(String id, AsyncCallback<Boolean> callback);
	public void getScriptList(AsyncCallback<List<String>> callback);
	public void getNeededDevices(String script, AsyncCallback<ScriptInfo> callback);
	public void startScript(String id, String script, Map<String, String> instanceSelection, Map<String, InstanceGroupExecutorInfo> instanceGroupSelection, AsyncCallback<Boolean> callback);

}
