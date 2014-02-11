package de.tudresden.inf.rn.mobilis.consoleclient.userinterface.panels;

import de.tudresden.inf.rn.mobilis.consoleclient.Connection;
import de.tudresden.inf.rn.mobilis.consoleclient.Controller;
import de.tudresden.inf.rn.mobilis.consoleclient.RuntimeDiscovery;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author cmdaltent
 */
public class FileUploadPanel extends JPanel {

    private JTextField _filePathTextField;
    private JRadioButton _singleRadioButton;
    private static JFileChooser _fileChooser;

    private String _acceptedFileExtension;
    private String _acceptedFileDescription;

    public FileUploadPanel() {
        performRuntimeDiscovery();
        init();
    }
    private void performRuntimeDiscovery() {
        Connection connection = Controller.getController().getConnection();
        String runtimeJID = Controller.getController().getSettings().getMobilisRuntimeJid();

        RuntimeDiscovery runtimeDiscovery = new RuntimeDiscovery(connection, runtimeJID);
        runtimeDiscovery.performRuntimeDiscovery();

        if (runtimeDiscovery.isJavaRuntime())
        {
            _acceptedFileExtension = "jar";
            _acceptedFileDescription = "*.jar";
        }
        else
        {
            _acceptedFileExtension = "bundle";
            _acceptedFileDescription = "*.bundle";
        }
    }
    private void init() {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        _filePathTextField= new JTextField();
        JButton fileChooserButton = new JButton("Choose File");

        GridBagConstraints textFieldConstraint = new GridBagConstraints();
        textFieldConstraint.weightx = 1.0;
        textFieldConstraint.fill = GridBagConstraints.HORIZONTAL;
        GridBagConstraints uploadButtonConstraint = new GridBagConstraints();
        uploadButtonConstraint.weightx = 1.0;
        uploadButtonConstraint.gridwidth = GridBagConstraints.REMAINDER;
        uploadButtonConstraint.anchor = GridBagConstraints.BASELINE_LEADING;

        layout.setConstraints(_filePathTextField, textFieldConstraint);
        layout.setConstraints(fileChooserButton, uploadButtonConstraint);

        add(_filePathTextField);
        add(fileChooserButton);

        JLabel modeLabel = new JLabel("Service Mode");
        _singleRadioButton = new JRadioButton("SINGLE");
        JRadioButton multiRadioButton = new JRadioButton("MULTI");

        ButtonGroup modeRadioGroup = new ButtonGroup();
        modeRadioGroup.add(_singleRadioButton);
        modeRadioGroup.add(multiRadioButton);
        multiRadioButton.setSelected(true);

        JPanel modeRadioPanel = new JPanel(new GridLayout(1, 2));
        modeRadioPanel.add(_singleRadioButton);
        modeRadioPanel.add(multiRadioButton);

        GridBagConstraints modeLabelConstraints = new GridBagConstraints();
        modeLabelConstraints.gridwidth = GridBagConstraints.RELATIVE;
        GridBagConstraints radioConstraints = new GridBagConstraints();
        radioConstraints.gridwidth = GridBagConstraints.REMAINDER;

        layout.setConstraints(modeLabel, modeLabelConstraints);
        layout.setConstraints(modeRadioPanel, radioConstraints);

        add(modeLabel);
        add(modeRadioPanel);

        fileChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = getFileChooser();
                if (fileChooser.showDialog(null, "Choose") == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    _filePathTextField.setText(selectedFile.getAbsolutePath());
                }
            }
        });
    }
    private JFileChooser getFileChooser() {
        if (_fileChooser == null) {
            _fileChooser = new JFileChooser();
            _fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            _fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileFilter fileFilter = new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().contains(_acceptedFileExtension);
                }

                @Override
                public String getDescription() {
                    return _acceptedFileDescription;
                }
            };
            _fileChooser.setAcceptAllFileFilterUsed(false);
            _fileChooser.setFileFilter(fileFilter);
            _fileChooser.addChoosableFileFilter(fileFilter);
        }

        return _fileChooser;
    }

    public String getFilePath() {
        return _filePathTextField.getText();
    }

    public boolean isSingleModeService() {
        return _singleRadioButton.isSelected();
    }
}
