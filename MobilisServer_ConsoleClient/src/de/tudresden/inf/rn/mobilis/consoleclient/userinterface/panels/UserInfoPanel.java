package de.tudresden.inf.rn.mobilis.consoleclient.userinterface.panels;

import de.tudresden.inf.rn.mobilis.consoleclient.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author cmdaltent
 */
public class UserInfoPanel extends JPanel {

    private Settings _settings;

    public UserInfoPanel(Settings settings) {
        _settings = settings;
        init();
    }
    private void init() {
        setLayout(new GridLayout(4,2));

        JLabel userLabel = new JLabel("Username");
        userLabel.setToolTipText("The username to use to upload files to the mobilis runtime. Keep in mind that this user has to be in the sug:deploy groyp of the mobilis runtime.\nProvide just the username, not the bare JID neither the full JID");

        JLabel smackModeLabel = new JLabel("Smack Debug Enabled");
        smackModeLabel.setToolTipText("If debug mode is enabled, the smack debug window will be launched alongside the Console Client application");

        JTextField userTextField = new JTextField(_settings.getClientNode());
        JPasswordField passwordTextField = new JPasswordField(_settings.getClientPassword());
        JTextField serverTextField = new JTextField(_settings.getXMPPServerAddress());
        JCheckBox smackDebugCheckBox = new JCheckBox();
        smackDebugCheckBox.setSelected(_settings.isSmackDebugMode());

        // FIXME: make the following codeblock more generic
        userTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                _settings.setClientNode(((JTextField)e.getSource()).getText());
            }
        });
        passwordTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                _settings.setClientPassword(((JTextField) e.getSource()).getText());
            }
        });
        serverTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                _settings.setXMPPServerAddress(((JTextField)e.getSource()).getText());
            }
        });
        smackDebugCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                _settings.setSmackDebugMode(e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        add(userLabel);
        add(userTextField);
        add(new JLabel("Password"));
        add(passwordTextField);
        add(new JLabel("XMPP Server Address"));
        add(serverTextField);
        add(smackModeLabel);
        add(smackDebugCheckBox);
    }
}
