package de.tudresden.inf.rn.mobsda.performance.client;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import de.tudresden.inf.rn.mobsda.performance.client.exception.RunMethodException;

public interface RMITestNodeClient extends Remote {

	public Serializable runMethod(String methodName, String[] parameterClassNames, String[] parameterValues) throws RemoteException, RunMethodException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
	
	public File getLogFile() throws RemoteException;
}
