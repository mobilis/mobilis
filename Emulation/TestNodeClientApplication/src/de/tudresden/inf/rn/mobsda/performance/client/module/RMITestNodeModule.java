package de.tudresden.inf.rn.mobsda.performance.client.module;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMITestNodeModule extends Remote {

	public void notifyOfStart() throws RemoteException;
	
	public void notifyOfStop() throws RemoteException;
	
}
