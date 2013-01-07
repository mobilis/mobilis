package de.tudresden.inf.rn.mobilis.java.testnodemodule;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import de.tudresden.inf.rn.mobsda.performance.client.RMITestNodeClient;
import de.tudresden.inf.rn.mobsda.performance.client.exception.RunMethodException;

public class TestApplicationRunnable implements Runnable {

	private String appName;
	private String[] cmd;
	private RMITestNodeClient run;
	private BlockingQueue<Command> commands = new LinkedBlockingQueue<Command>();
	private boolean shallExecute = true;
	private ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {
		
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setName(appName + "_methodThread");
			return t;
		}
	});
	
	public String getAppName() {
		return this.appName;
	}
	
	public TestApplicationRunnable(String appName, String[] cmd) {
		this.appName = appName;
		this.cmd = cmd;
	}
	
	@Override
	public void run() {
		// create working directory
		File workingDir = new File(appName);
		workingDir.mkdir();

		// create application command file
		File commandFile = new File(appName + "/" + appName + ".command");
		commandFile.delete();
		try {
			commandFile.createNewFile();
		} catch (IOException e1) {
			System.out.println("Couldn't create new command file for instance " + appName);
			e1.printStackTrace();
			return;
		}
		
		// make command file executable
		commandFile.setExecutable(true);
		// TODO: pass cmd to this class as a String (also eases replace operation in TestNodeModule)
		try {
			Files.write(commandFile.toPath(), ("cd " + workingDir.getAbsolutePath() + "\n").getBytes(Charset.defaultCharset()), StandardOpenOption.APPEND, StandardOpenOption.WRITE);
			for (String s : cmd) {
					Files.write(commandFile.toPath(), (s + " ").getBytes(Charset.defaultCharset()), StandardOpenOption.APPEND, StandardOpenOption.WRITE);
			}
		} catch (IOException e) {
			System.out.println("Couldn't write to command file for instance " + appName);
			e.printStackTrace();
			return;
		}
		
		// run application command file
		System.out.println("Starting instance " + appName);
		try {
			Desktop.getDesktop().open(commandFile);
		} catch (IOException e1) {
			System.out.println("Couldn't open command file for instance " + appName + "!");
			e1.printStackTrace();
			return;
		}
		
		// wait for the application to start
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		// set up RMI
        String stubName = "TestNodeClient_" + appName;
        Registry registry;
		try {
			registry = LocateRegistry.getRegistry();
			run = (RMITestNodeClient) registry.lookup(stubName);
		} catch (RemoteException e) {
			System.out.println("Couldn't obtain RMI registry!");
			e.printStackTrace();
			return;
		} catch (NotBoundException e) {
			System.out.println("RMI: Error binding to " + stubName);
			e.printStackTrace();
			return;
		}
		
		while (shallExecute) {
			try {
				final Command command = commands.take();
				if (command.async) {
					executorService.execute(new Runnable() {
						
						@Override
						public void run() {
							runMethod(command);
						}
					});
				} else {
					runMethod(command);
				}
				if (command.methodName.equals("exit")) {
					shallExecute = false;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				shallExecute = false;
			}
		}
		
		System.out.println("Shutting down client " + appName);
		try {
			executorService.awaitTermination(3000, TimeUnit.MILLISECONDS);
			executorService.shutdownNow();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void runMethod(final Command command) {
		try {
			run.runMethod(command.methodName, command.parameterTypes, command.parameters);
		} catch (IllegalAccessException | InvocationTargetException
				| NoSuchMethodException | RunMethodException | RemoteException e) {
			if (!(command.methodName.equals("exit") && e instanceof RemoteException)) {
				System.out.println("Error while running method: " + command.methodName + "(" + Arrays.toString(command.parameterTypes) + ")" + "with parameters " + "(" + Arrays.toString(command.parameters) + ") on instance " + getAppName());
				e.printStackTrace();
			} else {
				/*
				 * We expect a remote exception here as we force the
				 * client to shut down which eventually also shuts down
				 * it's RMI connection, hence the RMI server (aka the
				 * testing client) cannot post the result of the method
				 * call after it's execution.
				 */
			}
		}
	}
	
	public void postCommand(Command command) {
		try {
			commands.put(command);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public File getLogFile() {
		try {
			return run.getLogFile();
		} catch (RemoteException e) {
			System.out.println("RemoteException during access of getLogFile() method.");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Tries to stop this application as soon as possible.
	 */
	public void stop() {
		Command exitCommand = new Command();
		exitCommand.methodName = "exit";
		exitCommand.parameters = new String[0];
		exitCommand.parameterTypes = new String[0];
		try {
			commands.put(exitCommand);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
