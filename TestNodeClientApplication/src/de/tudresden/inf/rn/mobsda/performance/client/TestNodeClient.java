package de.tudresden.inf.rn.mobsda.performance.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public abstract class TestNodeClient {
	
	public TestNodeClient() {
		initTestNodeClient();
	}
	
	public void initTestNodeClient() {
		File configFile = new File("testNodeClient.conf");
		try {
			configFile.createNewFile();
			File logFile = getLogFile();
			if (logFile != null) {
				Files.write(configFile.toPath(), ("log=" + logFile.getPath().toString()).getBytes(), StandardOpenOption.WRITE);
			} else {
				System.out.println("No log!");
			}
		} catch (IOException e) {
			System.out.println("Couldn't create or write to config file!");
			e.printStackTrace();
		}
	}
	
	public abstract File getLogFile();

}
