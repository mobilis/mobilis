import java.io.IOException;

import org.jivesoftware.smack.SmackConfiguration;

/**
 * This class creates a Smack-XMPP-Connection to the server and waits for
 * commands from users, which are sent as messages.
 * @author Christian Magenheimer
 *
 */
public class MXAEcho {

	static{
		//org.jivesoftware.smack.Connection.DEBUG_ENABLED=true; 
	}
	
	public static void main(String[] args) throws InterruptedException
	{
		Connection con= new Connection("bob","54321#pca","mobilis.inf.tu-dresden.de");
		
		con.connect();
		while(true)
			Thread.sleep(1000);
	}
}
