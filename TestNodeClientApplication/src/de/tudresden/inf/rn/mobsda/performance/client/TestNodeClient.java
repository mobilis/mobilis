package de.tudresden.inf.rn.mobsda.performance.client;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import de.tudresden.inf.rn.mobsda.performance.client.exception.NonSerializableException;
import de.tudresden.inf.rn.mobsda.performance.client.exception.ParameterTypeException;
import de.tudresden.inf.rn.mobsda.performance.client.exception.RunMethodException;

public abstract class TestNodeClient implements RMITestNodeClient {
	
	private String workingDir = "";
	
	public TestNodeClient() {
		super();
	}
	
	public void initTestNodeClient() {
		// working dir
		String absoluteWorkingDir = System.getProperty("user.dir");
		System.out.println("workingDir = " + absoluteWorkingDir);
		String[] splitWorkingDir = absoluteWorkingDir.split("/");
		workingDir = splitWorkingDir[splitWorkingDir.length - 1];
		
		// config file
//		try {
//			File logFile = getLogFile();
//			if (logFile != null) {
//				File configFile = new File(workingDir + "testNodeClient.conf");
//				configFile.createNewFile();
//				Files.write(configFile.toPath(), ("log=" + logFile.getPath().toString()).getBytes(Charset.defaultCharset()), StandardOpenOption.WRITE);
//			} else {
//				System.out.println("No log!");
//			}
//		} catch (IOException e) {
//			System.out.println("Couldn't create or write to config file!");
//			e.printStackTrace();
//		}
		
		// RMI initialization
		// TODO: put server codebase into initTestNodeClient parameter to make the class more generic
		String path = TestNodeClient.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		try {
			String decodedPath = URLDecoder.decode(path, "UTF-8");
			System.setProperty("java.rmi.server.codebase", "file:" + decodedPath);
			System.out.println("decoded path of codedbase is " + decodedPath);
			
			try {
				String name = "TestNodeClient_" + workingDir;
				
				RMITestNodeClient stub = (RMITestNodeClient) UnicastRemoteObject.exportObject(this, 0);
				Registry registry = LocateRegistry.getRegistry();
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
	}
	
	@Override
	public Serializable runMethod(String methodName, String[] parameterClassNames, String[] parameterValues) throws RemoteException, RunMethodException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (parameterClassNames.length != parameterValues.length) {
			throw new IllegalArgumentException("Parameter class name array lenght doesn't match parameter value array lenght.");
		}
		
		@SuppressWarnings("rawtypes")
		Class[] parameterClasses = new Class[parameterClassNames.length];
		Object[] typedParameterValues = new Object[parameterValues.length];
		for (int i = 0; i < parameterClassNames.length; i++) {
			String parameterClassName = parameterClassNames[i];
			try {
				parameterClasses[i] = Class.forName(parameterClassName);
				// TODO: parse all primitive types from String, throw exception if there is a non-primitive type - this could also be done in constructor of Command class
				// typedParameterValues[i] = 
				
			} catch (ClassNotFoundException e) {
				System.out.println("Couldn't find parameter type " + parameterClassName);
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
	
	@Override
	public abstract File getLogFile();
	
	/**
	 * Shuts down the application (normally by somehow calling System.exit()).
	 */
	public abstract void exit();

}
