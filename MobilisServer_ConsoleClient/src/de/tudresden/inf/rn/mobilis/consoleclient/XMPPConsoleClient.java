package de.tudresden.inf.rn.mobilis.consoleclient;

import de.tudresden.inf.rn.mobilis.consoleclient.userinterface.MainWindow;

import javax.swing.*;

/**
 * The Class XMPPConsoleClient.
 */
public class XMPPConsoleClient {

    private Controller controller;

	/**
	 * Instantiates a new xMPP console client.
	 */
	public XMPPConsoleClient(){
		controller = Controller.getController();
        loginClient();
    }

    public void loginClient() {
        if (controller.getSettings().allSettingsAvailable()) {
            if (!controller.getConnection().connectToXMPPServer()) {
                JOptionPane.showMessageDialog(null, "Could not set up connection.\nCheck your internet connection or localhost.", "Connection error", JOptionPane.ERROR_MESSAGE);
            } else {
                 if (!controller.getConnection().loginXMPP()) {
                     JOptionPane.showMessageDialog(null, "Could not authenticate.\nPlease check your credentials or set them up first.", "Authentication error", JOptionPane.ERROR_MESSAGE);
                 }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please specify all required Settings first. Then reconnect.", "Missing Settings", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
        final XMPPConsoleClient consoleClient = new XMPPConsoleClient();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow mainWindow = new MainWindow(consoleClient);
                mainWindow.setVisible(true);
            }
        });
	}

    public Controller getController() {
        return controller;
    }
}
