package de.tudresden.inf.rn.mobilis.consoleclient.userinterface;

import de.tudresden.inf.rn.mobilis.consoleclient.Connection;
import de.tudresden.inf.rn.mobilis.consoleclient.Controller;
import de.tudresden.inf.rn.mobilis.consoleclient.ServiceHandler;
import de.tudresden.inf.rn.mobilis.consoleclient.XMPPConsoleClient;
import de.tudresden.inf.rn.mobilis.consoleclient.helper.ConnectionStatus;
import de.tudresden.inf.rn.mobilis.consoleclient.helper.StatusInformation;
import de.tudresden.inf.rn.mobilis.consoleclient.userinterface.panels.FileUploadPanel;
import de.tudresden.inf.rn.mobilis.consoleclient.userinterface.panels.ResourcePanel;
import de.tudresden.inf.rn.mobilis.consoleclient.userinterface.panels.UserInfoPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * @author cmdaltent
 */
public class MainWindow extends JFrame implements Observer {

    private XMPPConsoleClient consoleClient;

    private JButton uploadButton;

    private void initUI() {
        setLayout(new BorderLayout());
        setupSettingsPanel();

        final Controller controller = Controller.getController();

        final FileUploadPanel fileUploadPanel = new FileUploadPanel();
        fileUploadPanel.setMainWindow(this);
        getContentPane().add(fileUploadPanel, BorderLayout.CENTER);

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
        buttonConstraints.weightx = 1.0;

        JPanel buttonPanel = new JPanel();
        uploadButton = new JButton("Upload File");
        JButton reconnectButton = new JButton("Reconnect");

        uploadButton.setEnabled(controller.getConnection().isConnected());

        layout.setConstraints(uploadButton, buttonConstraints);
        layout.setConstraints(reconnectButton, buttonConstraints);

        buttonPanel.add(uploadButton);
        buttonPanel.add(reconnectButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.getServiceHandler().sendFile(fileUploadPanel.getFilePath(), true, fileUploadPanel.isSingleModeService());
            }
        });
        reconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.getConnection().disconnect();
                consoleClient.loginClient();
                fileUploadPanel.discoverRuntimeInformation();
            }
        });

    }

    private void setupSettingsPanel() {
        Controller controller = Controller.getController();

        GridBagLayout gridBagLayout = new GridBagLayout();
        JPanel settingsPanel = new JPanel(gridBagLayout);
        getContentPane().add(settingsPanel, BorderLayout.NORTH);

        JPanel resourcePanel = new ResourcePanel(controller.getSettings());
        GridBagConstraints resourceConstraint = new GridBagConstraints();
        resourceConstraint.anchor = GridBagConstraints.WEST;
        gridBagLayout.setConstraints(resourcePanel, resourceConstraint);
        settingsPanel.add(resourcePanel);

        JPanel userInfoPanel = new UserInfoPanel(controller.getSettings());
        GridBagConstraints userConstraint = new GridBagConstraints();
        userConstraint.anchor = GridBagConstraints.EAST;
        gridBagLayout.setConstraints(userInfoPanel, userConstraint);
        settingsPanel.add(userInfoPanel);
    }

    public MainWindow(XMPPConsoleClient consoleClient) {
        this.consoleClient = consoleClient;
        Controller.getController().getServiceHandler().addObserver(this);
        Controller.getController().getConnection().addObserver(this);

        initUI();

        setTitle("MobilisServer Console Client");
        setSize(500, 220);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ServiceHandler)
            if (arg instanceof StatusInformation) {
                StatusInformation information = (StatusInformation)arg;
                JOptionPane.showMessageDialog(this, information.getMessage(), "File Upload Status Information", information.optionPaneStatus());
            }
        if (o instanceof Connection) {
            if (arg instanceof ConnectionStatus) {
                ConnectionStatus status = (ConnectionStatus)arg;
                switch (status) {
                    case CONNECTED:
                        uploadButton.setEnabled(true);
                        break;
                    default:
                        uploadButton.setEnabled(false);
                }
            }
        }
    }

    public void setAllowUpload(boolean allowUpload)
    {
        uploadButton.setEnabled(allowUpload);
    }
}
