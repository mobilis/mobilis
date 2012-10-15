package de.tudresden.inf.rn.mobilis.emulationserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class EmulationServer {
	
	static Connection connection;
	static Boolean run = true;

	public static void main(String[] args) {
		
		InputStreamReader isr = new InputStreamReader( System.in );
		BufferedReader br = new BufferedReader( isr );
		String cmd = "";
		
		System.out.println("EmulationServer started");
		
		while(run) {
			System.out.print("command: ");
			try {
				cmd = br.readLine();
				execute(cmd);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
		
	}
	
	private static void execute(String cmd) {
		
		if(cmd.equals("exit")) {
			
			if(connection.isConnected()) {
				disconnect();
			}
			
			System.out.println("closing EmulationServer");
			run = false;
			
		} else if(cmd.equals("connect")) {
			
			InputStreamReader isr2 = new InputStreamReader( System.in );
			BufferedReader br2 = new BufferedReader( isr2 );
			String user = "";
			String pass = "";
			
			Connection.DEBUG_ENABLED = true;
			
			System.out.print("user: ");
			try {
				user = br2.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.print("pass: ");
			try {
				pass = br2.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			connect(user,pass);
		
		} else if(cmd.equals("disconnect")) {
			
			disconnect();
			
		} else {
			
			System.out.println("unknown command");
			
		}
		
	}
	
	private static void connect(String user, String pass) {
		
		// Create the configuration for this new connection
		ConnectionConfiguration config = new ConnectionConfiguration("mobilis.inf.tu-dresden.de", 5222);
		config.setCompressionEnabled(true);
		config.setSASLAuthenticationEnabled(true);

		connection = new XMPPConnection(config);
		// Connect to the server
		try {
			connection.connect();
			System.out.println("connected to server");
		} catch (XMPPException e) {
			System.out.println(e);
		}
		// Log into the server
		try {
			connection.login(user, pass, "EmulationServer");
			System.out.println("logged in");
		} catch (XMPPException e) {
			System.out.println(e);
		}
		
	}
	
	private static void disconnect() {
		
		connection.disconnect();
		System.out.println("disconnected");
		
	}

}
