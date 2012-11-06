package de.tudresden.inf.rn.mobilis.gwtemulationserver.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.SessionInfo;

public interface EmuServerConnectServiceAsync {
	
	/*public void isConnected(AsyncCallback<Boolean> callback);
	public void connectServer(AsyncCallback<Boolean> callback);
	public void disconnectServer(AsyncCallback<Boolean> callback);*/
	public void sendCommand(String cmd, AsyncCallback<Boolean> callback);
	public void getDeviceList(String id, AsyncCallback<List<String>> callback);
	public void openSession(String id, AsyncCallback<SessionInfo> callback);
	public void closeSession(String id, AsyncCallback<Boolean> callback);

}
