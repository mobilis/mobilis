package de.tudresden.inf.rn.mobilis.android.xhunt;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

public class TestNodeModule {

	/**
	 * @param args
	 * 				args[0] is the path to the JAR file to be started including any
	 * 				additional parameters for the started application (remember to put
	 * 				" around the parameter if it contains parameters
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
		
//		try {
//			Process p = pb.start();
////			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
////			String line = null;
////			while ((line = reader.readLine()) != null) {
////				System.out.println(line);
////			}
////			reader.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		Process p;
//		try {
//			p = Runtime.getRuntime().exec("java -jar " + args[0]);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				System.out.println(line);
//			}
//			reader.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
