package de.tudresden.inf.rn.mobilis.consoleclient.userinterface.panels;

import de.tudresden.inf.rn.mobilis.MobilisLogger;
import de.tudresden.inf.rn.mobilis.consoleclient.Connection;
import de.tudresden.inf.rn.mobilis.consoleclient.Controller;
import de.tudresden.inf.rn.mobilis.consoleclient.RuntimeDiscovery;
import de.tudresden.inf.rn.mobilis.consoleclient.exceptions.RuntimeDiscoveryException;
import de.tudresden.inf.rn.mobilis.consoleclient.userinterface.MainWindow;
import de.tudresden.inf.rn.mobilis.deployment.upload.FileHelper;
import de.tudresden.inf.rn.mobilis.deployment.upload.IFFReader;
import de.tudresden.inf.rn.mobilis.deployment.upload.IFFReaderFactory;
import de.tudresden.inf.rn.mobilis.deployment.upload.JarClassLoader;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;

/**
 * @author cmdaltent
 */
public class FileUploadPanel extends JPanel {

    private JTextField _filePathTextField;
    private static JFileChooser _fileChooser;

    private String _acceptedFileExtension;
    private String _acceptedFileDescription;

    private boolean _singleService;

    private MainWindow __mainWindow;

    public FileUploadPanel() {
        performRuntimeDiscovery();
        init();
    }
    private void performRuntimeDiscovery() {
        Connection connection = Controller.getController().getConnection();
        String runtimeJID = Controller.getController().getSettings().getMobilisRuntimeJid();

        if (connection.isConnected())
        {
            RuntimeDiscovery runtimeDiscovery = new RuntimeDiscovery(connection, runtimeJID);
            runtimeDiscovery.performRuntimeDiscovery();

            try {
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
            } catch (RuntimeDiscoveryException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Runtime Discovery Exception", JOptionPane.ERROR_MESSAGE);
            }
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

        fileChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = getFileChooser();
                if (fileChooser.showDialog(null, "Choose") == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    _filePathTextField.setText(selectedFile.getAbsolutePath());
                    try {
                        determineServiceType(selectedFile);
                        __mainWindow.setAllowUpload(true);
                    } catch (Exception e1) {
                        displayErrorView(e1.getMessage());
                        __mainWindow.setAllowUpload(false);
                    }

                }
            }
        });
    }

    private void displayErrorView(String message) {
        JOptionPane.showMessageDialog(null, message, "Interface File Error", JOptionPane.ERROR_MESSAGE);
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

    public void discoverRuntimeInformation()
    {
        performRuntimeDiscovery();
    }

    public String getFilePath() {
        return _filePathTextField.getText();
    }

    public boolean isSingleModeService() {
        return _singleService;
    }

    private void determineServiceType(File file) throws Exception {
        if (_acceptedFileExtension.equalsIgnoreCase("jar"))
        {
            this.determineServiceTypeForJarFile(file);
        }
        else
        {
            this.determineServiceTypeForBundleFile(file);
        }
    }

    private void determineServiceTypeForJarFile(File jarFile) throws Exception {
        URL[] urls;
        urls = new URL[1];
        try {
            urls[0] = jarFile.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JarClassLoader jarClassLoader = new JarClassLoader(urls);

        String interfaceFilePath;
        File interfaceFile;
        try {
            List<String> interfaceFiles = FileHelper.getJarFiles(jarFile, "xpd");

            if (interfaceFiles.size() > 0) {
                interfaceFilePath = interfaceFiles.get(0);
            } else {
                MobilisLogger.getLogger().log(Level.INFO, "No XPD found. Try MSDL");
                interfaceFiles = FileHelper.getJarFiles(jarFile, "msdl");
                if (interfaceFiles.size() > 0) {
                    interfaceFilePath = interfaceFiles.get(0);
                } else {
                    jarClassLoader.close();
                    throw new Exception("Could neither find an XPD nor an MSDL.");
                }
            }

            interfaceFile = FileHelper.createFileFromInputStream(
                    jarClassLoader.getResourceAsStream(interfaceFilePath),
                    "tmp" + File.separator + jarFile.getName() + ".iff");

            if (null == interfaceFile) {
                jarClassLoader.close();
                throw new Exception("Result of XPD or MSDL file was NULL while loading from jar archive.");
            } else MobilisLogger.getLogger().log(Level.INFO, String.format("XPD or MSDL found"));

            IFFReader iffReader = (new IFFReaderFactory(interfaceFilePath)).getIFFReader();
            String serviceType = iffReader.getServiceType(interfaceFile);
            if (serviceType != null)
                _singleService = iffReader.getServiceType(interfaceFile).equalsIgnoreCase("single");
            else throw new Exception("Could not determine if service is single or multi.");

        } catch (UnsupportedClassVersionError | IOException e) {
            throw new Exception(e.getMessage());
        } catch (ClassNotFoundException e) {
            try {
                jarClassLoader.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            throw new Exception(e.getMessage());
        }
    }

    private void determineServiceTypeForBundleFile(File bundleFile)
    {
        // TODO needs to be implemented.
    }

    public MainWindow getMainWindow() {
        return __mainWindow;
    }

    public void setMainWindow(MainWindow mainWindow) {
        this.__mainWindow = mainWindow;
    }
}
