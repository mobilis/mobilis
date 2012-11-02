package de.tudresden.inf.rn.mobilis.gwtemulationserver.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EmuServerConnectServiceAsync {
	
	public void isConnected(AsyncCallback<Boolean> callback);
	public void connectServer(AsyncCallback<Boolean> callback);
	public void disconnectServer(AsyncCallback<Boolean> callback);
	public void sendCommand(String cmd, AsyncCallback<Boolean> callback);
	public void getDeviceList(AsyncCallback<List<String>> callback);

}
