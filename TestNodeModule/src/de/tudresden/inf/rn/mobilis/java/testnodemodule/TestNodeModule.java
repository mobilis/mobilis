package de.tudresden.inf.rn.mobilis.java.testnodemodule;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


public class TestNodeModule {

	/**
	 * @param args
	 * 				args[0] is the count of the application instances which shall be started,
	 * 				args[1] is the path to the actual JAR file. Any additional
	 * 				parameters are put into subsequent elements of args.
	 * 				args[1]...args[args.length] may use %in% which will be replaced by the
	 * 				application instance number at runtime.
	 */
	public static void main(String[] args) {
		// set up RMI
		try {
			LocateRegistry.createRegistry(1099);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.setProperty("java.security.policy", "file:/Users/sven/Desktop/client.policy");
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
		// TODO: http://docs.oracle.com/javase/tutorial/rmi/running.html
		
		System.out.println("Starting " + args[0] + " instances of " + args[1]);
		final String[] cmd = new String[1 + args.length];
		cmd[0] = System.getProperty("java.home") + "/bin/java";
		cmd[1] = "-jar";
		for (int i = 1; i < args.length; i++) {
			cmd[i+1] = args[i];
		}
		
		int instanceCount = 0;
		try {
			instanceCount = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.out.println("Couldn't parse instance count! Assuming 1.");
			e.printStackTrace();
			instanceCount = 1;
		}
		for (int i = 1; i <= instanceCount; i++) {
			String[] cmd2 = cmd.clone();
			for (int j = 3; j < cmd.length; j++) {
				cmd2[j] = cmd[j].replace("%in%", String.valueOf(i));
			}
			TestApplicationRunnable testApplicationRunnable = new TestApplicationRunnable(i, cmd2);
			new Thread(testApplicationRunnable).start();
			
			// TODO: remove this test code
//			try {
//				Thread.sleep(3000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			testApplicationRunnable.stop();
		}

	}

}
