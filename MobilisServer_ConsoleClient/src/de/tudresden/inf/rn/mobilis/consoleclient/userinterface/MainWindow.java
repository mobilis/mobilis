package de.tudresden.inf.rn.mobilis.consoleclient.userinterface;

import de.tudresden.inf.rn.mobilis.consoleclient.ServiceHandler;
import de.tudresden.inf.rn.mobilis.consoleclient.XMPPConsoleClient;
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
    private ServiceHandler serviceHandler;

    private void initUI() {
        setLayout(new BorderLayout());

        setupSettingsPanel();
        final FileUploadPanel fileUploadPanel = new FileUploadPanel();
        getContentPane().add(fileUploadPanel, BorderLayout.CENTER);

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
        buttonConstraints.weightx = 1.0;

        JPanel buttonPanel = new JPanel();
        JButton uploadButton = new JButton("Upload File");
        JButton reconnectButton = new JButton("Reconnect");

        layout.setConstraints(uploadButton, buttonConstraints);
        layout.setConstraints(reconnectButton, buttonConstraints);

        buttonPanel.add(uploadButton);
        buttonPanel.add(reconnectButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serviceHandler.sendFile(fileUploadPanel.getFilePath(), true, fileUploadPanel.isSingleModeService());
            }
        });
        reconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consoleClient.getController().getConnection().disconnect();
                consoleClient.loginClient();
            }
        });

    }

    private void setupSettingsPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        JPanel settingsPanel = new JPanel(gridBagLayout);
        getContentPane().add(settingsPanel, BorderLayout.NORTH);

        JPanel resourcePanel = new ResourcePanel(consoleClient.getController().getSettings());
        GridBagConstraints resourceConstraint = new GridBagConstraints();
        resourceConstraint.anchor = GridBagConstraints.WEST;
        gridBagLayout.setConstraints(resourcePanel, resourceConstraint);
        settingsPanel.add(resourcePanel);

        JPanel userInfoPanel = new UserInfoPanel(consoleClient.getController().getSettings());
        GridBagConstraints userConstraint = new GridBagConstraints();
        userConstraint.anchor = GridBagConstraints.EAST;
        gridBagLayout.setConstraints(userInfoPanel, userConstraint);
        settingsPanel.add(userInfoPanel);
    }

    public MainWindow(XMPPConsoleClient consoleClient) {
        this.consoleClient = consoleClient;
        this.serviceHandler = new ServiceHandler(this.consoleClient.getController(), this);

        initUI();

        setTitle("MobilisServer Console Client");
        setSize(500, 300);
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
    }
}
