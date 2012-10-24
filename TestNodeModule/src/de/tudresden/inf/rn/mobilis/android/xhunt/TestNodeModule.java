package de.tudresden.inf.rn.mobilis.android.xhunt;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

public class TestNodeModule {

	/**
	 * @param args
	 * 				args[0] is the path to the JAR file to be started. Any additional
	 * 				parameters are put into subsequent elements of args
	 */
	public static void main(String[] args) {
		System.out.println("Starting " + args[0]);
		String[] cmd = new String[2 + args.length];
		cmd[0] = System.getProperty("java.home") + "/bin/java";
		cmd[1] = "-jar";
		for (int i = 0; i < args.length; i++) {
			cmd[i+2] = args[i];
		}
		ProcessBuilder pb = new ProcessBuilder(cmd);
		File workingDir = new File("app1");
		workingDir.mkdir();
		pb.directory(workingDir);
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectInput(Redirect.INHERIT);
		try {
			pb.start().waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
