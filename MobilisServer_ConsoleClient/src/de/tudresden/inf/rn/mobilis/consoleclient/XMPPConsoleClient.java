package de.tudresden.inf.rn.mobilis.consoleclient;

/**
 * The Class XMPPConsoleClient.
 */
public class XMPPConsoleClient {
	
	/**
	 * Instantiates a new xMPP console client.
	 */
	public XMPPConsoleClient(){
		Controller controller = new Controller();
		// does a xmpp connect at startup
		controller.getCommandShell().executeCommand( "connect" );
		controller.getCommandShell().start();
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		new XMPPConsoleClient();
	}
}
