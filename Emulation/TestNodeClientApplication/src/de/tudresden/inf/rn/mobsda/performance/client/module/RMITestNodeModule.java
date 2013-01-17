package de.tudresden.inf.rn.mobsda.performance.client.module;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMITestNodeModule extends Remote {

	public void notifyOfStart(String rmiID) throws RemoteException;
	
	public void notifyOfStop(String rmiID) throws RemoteException;
	
}
