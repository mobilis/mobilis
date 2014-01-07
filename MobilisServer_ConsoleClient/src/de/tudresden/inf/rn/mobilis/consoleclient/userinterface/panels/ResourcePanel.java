package de.tudresden.inf.rn.mobilis.consoleclient.userinterface.panels;

import de.tudresden.inf.rn.mobilis.consoleclient.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author cmdaltent
 */
public class ResourcePanel extends JPanel {

    private Settings _settings;

    public ResourcePanel(final Settings settings) {
        _settings = settings;
        init();
    }
    private void init() {
        setLayout(new GridLayout(4, 2));

        JTextField runtimeTextField = new JTextField(_settings.getMobilisRuntimeResource());
        JTextField deploymentTextField = new JTextField(_settings.getMobilisDeploymentResource());
        JTextField coordinatorTextField = new JTextField(_settings.getMobilisCoordinatorResource());

        JLabel runtimeName = new JLabel("Runtime Name");
        runtimeName.setToolTipText("Mobilis Runtime Username at XMPPServer, i.e. 'mobilis' or 'runtime1'");

        JTextField runtimeNameTextField = new JTextField(_settings.getMobilisServerNode());

        add(runtimeName);
        add(runtimeNameTextField);
        add(new JLabel("Runtime"));
        add(runtimeTextField);
        add(new JLabel("Deployment"));
        add(deploymentTextField);
        add(new JLabel("Coordinator"));
        add(coordinatorTextField);
    }

}
