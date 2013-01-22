package de.tudresden.inf.rn.mobsda.performance.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import de.tudresden.inf.rn.mobsda.performance.client.exception.NonSerializableException;
import de.tudresden.inf.rn.mobsda.performance.client.exception.ParameterTypeException;
import de.tudresden.inf.rn.mobsda.performance.client.exception.RunMethodException;
import de.tudresden.inf.rn.mobsda.performance.client.helper.TeePrintStream;
import de.tudresden.inf.rn.mobsda.performance.client.module.RMITestNodeModule;

public abstract class TestNodeClient implements RMITestNodeClient {
	
	private String workingDir = "";
	
	private static RMITestNodeModule testNodeModule; // this is intentionally static to prevent GC from collecting it

	private static RMITestNodeClient stub;

	private static Registry registry;

	private String name;
	
	public TestNodeClient() {
		super();
	}
	
	public void initTestNodeClient() {
		// working dir
		String absoluteWorkingDir = System.getProperty("user.dir");
		System.out.println("workingDir = " + absoluteWorkingDir);
		String[] splitWorkingDir = absoluteWorkingDir.split("/");
		workingDir = splitWorkingDir[splitWorkingDir.length - 1];
		
		new File("logs").mkdir();
		try {
			File file = new File("logs/sysOutLog " + System.currentTimeMillis() + ".txt");
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			TeePrintStream tee = new TeePrintStream(System.out, new PrintStream(fos));
			System.setOut(tee);
			System.setErr(tee);
		} catch (IOException e) {
			System.err.println("Cannot write system output to logfile!");
			e.printStackTrace();
		}
		
		// RMI initialization
		String path = TestNodeClient.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		try {
			String decodedPath = URLDecoder.decode(path, "UTF-8");
			System.setProperty("java.rmi.server.codebase", "file:" + decodedPath);
			System.out.println("decoded path of codebase is " + decodedPath);
			
			try {
				name = workingDir;
				
				stub = (RMITestNodeClient) UnicastRemoteObject.exportObject(this, 0);
				registry = LocateRegistry.getRegistry("localhost");
				registry.rebind(name, stub);
				System.out.println("TestNodeClient bound on " + name);
			} catch (Exception e) {
				System.out.println("TestNodeClient exception during RMI setup:");
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e1) {
			System.out.println("Couldn't decode JAR file path.");
			e1.printStackTrace();
		}
		
		String stubName = "TestNodeModule";
        Registry registry;
		try {
			registry = LocateRegistry.getRegistry("localhost");
			testNodeModule = (RMITestNodeModule) registry.lookup(stubName);
		} catch (RemoteException e) {
			System.out.println("Couldn't obtain RMI registry!");
			e.printStackTrace();
			return;
		} catch (NotBoundException e) {
			System.out.println("RMI: Error binding to " + stubName);
			e.printStackTrace();
			return;
		}
	}
	
	@Override
	public Serializable runMethod(String methodName, String[] parameterClassNames, String[] parameterValues) throws RemoteException, RunMethodException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		System.out.println("Executing " + methodName + "(" + Arrays.toString(parameterValues) + ")");
		System.out.println("Parameter types are " + Arrays.toString(parameterClassNames));
		
		if (parameterClassNames.length != parameterValues.length) {
			throw new IllegalArgumentException("Parameter class name array length doesn't match parameter value array length.");
		}
		
		@SuppressWarnings("rawtypes")
		Class[] parameterClasses = new Class[parameterClassNames.length];
		Object[] typedParameterValues = new Object[parameterValues.length];
		for (int i = 0; i < parameterClassNames.length; i++) {
			String parameterClassName = parameterClassNames[i];
			try {
				Class<?> parameterClass = Class.forName(parameterClassName);
				parameterClasses[i] = parameterClass;
				
				if (parameterClass.equals(String.class)) {
					typedParameterValues[i] = parameterValues[i];
				} else if (parameterClass.equals(Boolean.class)) {
					typedParameterValues[i] = Boolean.parseBoolean(parameterValues[i]);
				} else if (parameterClass.equals(Double.class)) {
					typedParameterValues[i] = Double.parseDouble(parameterValues[i]);
				} else if (parameterClass.equals(Integer.class)) {
					typedParameterValues[i] = Integer.parseInt(parameterValues[i]);
				} else {
					String errorText = "Parameter type " + parameterClass.getName() + " not supported.";
					System.out.println(errorText);
					throw new IllegalArgumentException(errorText);
				}
			} catch (ClassNotFoundException e) {
				System.err.println("Couldn't find parameter type " + parameterClassName);
				e.printStackTrace();
				throw new ParameterTypeException(parameterClassName);
			}
		}
		try {
			Method method = this.getClass().getMethod(methodName, parameterClasses);

			Object returnValue = method.invoke(this, typedParameterValues);
			
			// TODO: write call and result into log
			if (returnValue instanceof Serializable) {
				return (Serializable) returnValue; 
			} else if (returnValue == null) {
				return new SerializableNull();
			} else {
				throw new NonSerializableException(returnValue);
			}
		} catch (IllegalAccessException e) {
			System.out.println("Couldn't access method " + methodName);
			e.printStackTrace();
			throw e;
		} catch (IllegalArgumentException e) {
			System.out.println("Illegal argument in method invocation: " + methodName);
			e.printStackTrace();
			throw e;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw e;
		} catch (NoSuchMethodException e) {
			System.out.println("The specified method doesn't exist: " + methodName);
			e.printStackTrace();
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void notifyStart() {
		try {
			testNodeModule.notifyOfStart(name);
		} catch (RemoteException e) {
			System.err.println("Couldn't notify TestNodeModule of finished startup sequence!");
			e.printStackTrace();
		}
	}
	
	public void notifyStop() {
		System.out.println("notifyStop() called");
		try {
			testNodeModule.notifyOfStop(name);
		} catch (RemoteException e) {
			System.err.println("Couldn't notify TestNodeModule of shutdown!");
			e.printStackTrace();
		}
		try {
			registry.unbind(name);
			UnicastRemoteObject.unexportObject(this, true);
		} catch (RemoteException | NotBoundException e) {
			System.err.println("Couldn't unbind from RMI registry!");
			e.printStackTrace();
		}
	}
	
	@Override
	public abstract File getLogFile();
	
	/**
	 * Shuts down the application (normally by somehow calling System.exit()).
	 */
	public abstract void exit();

}
