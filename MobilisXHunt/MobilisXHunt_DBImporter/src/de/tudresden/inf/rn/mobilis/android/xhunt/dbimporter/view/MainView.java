package de.tudresden.inf.rn.mobilis.android.xhunt.dbimporter.view;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.tudresden.inf.rn.mobilis.android.xhunt.dbimporter.SqlHelper;

/**
 * The Class MainView.
 */
public class MainView {

	/** The frm mobilisxhunt dbimporter. */
	private JFrame frmMobilisxhuntDbimporter;
	
	/** The tf server address. */
	private JTextField tfServerAddress;
	
	/** The tf server port. */
	private JTextField tfServerPort;
	
	/** The tf db name. */
	private JTextField tfDbName;
	
	/** The tf db username. */
	private JTextField tfDbUsername;
	
	/** The btn import file. */
	private JButton btnImportFile;
	
	/** The btn connect. */
	private JButton btnConnect;
	
	/** The m sql helper. */
	private SqlHelper mSqlHelper;
	
	/** The pf db password. */
	private JPasswordField pfDbPassword;
	
	/** The btn create structure. */
	private JButton btnCreateStructure;
	
	/** The btn export file. */
	private JButton btnExportFile;

	/**
	 * Launch the application.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainView window = new MainView();
					window.frmMobilisxhuntDbimporter.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainView() {
		initialize();
		mSqlHelper = new SqlHelper();
		
		insertDefaultValues();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMobilisxhuntDbimporter = new JFrame();
		frmMobilisxhuntDbimporter.setTitle("MobilisXHunt DBImporter");
		frmMobilisxhuntDbimporter.setBounds(100, 100, 377, 304);
		frmMobilisxhuntDbimporter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{30, 30, 30, 30, 30, 0, 30, 30, 30, 30, 30, 30, 30, 0};
		gridBagLayout.rowHeights = new int[]{30, 0, 0, 0, 0, 0, 0, 30, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frmMobilisxhuntDbimporter.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblServerAddress = new JLabel("Server Address:");
		GridBagConstraints gbc_lblServerAddress = new GridBagConstraints();
		gbc_lblServerAddress.gridwidth = 5;
		gbc_lblServerAddress.anchor = GridBagConstraints.EAST;
		gbc_lblServerAddress.insets = new Insets(0, 0, 5, 5);
		gbc_lblServerAddress.gridx = 1;
		gbc_lblServerAddress.gridy = 1;
		frmMobilisxhuntDbimporter.getContentPane().add(lblServerAddress, gbc_lblServerAddress);
		
		tfServerAddress = new JTextField();
		GridBagConstraints gbc_tfServerAddress = new GridBagConstraints();
		gbc_tfServerAddress.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfServerAddress.gridwidth = 6;
		gbc_tfServerAddress.insets = new Insets(0, 0, 5, 5);
		gbc_tfServerAddress.gridx = 6;
		gbc_tfServerAddress.gridy = 1;
		frmMobilisxhuntDbimporter.getContentPane().add(tfServerAddress, gbc_tfServerAddress);
		tfServerAddress.setColumns(10);
		
		JLabel lblServerPort = new JLabel("Server Port:");
		GridBagConstraints gbc_lblServerPort = new GridBagConstraints();
		gbc_lblServerPort.gridwidth = 5;
		gbc_lblServerPort.anchor = GridBagConstraints.EAST;
		gbc_lblServerPort.insets = new Insets(0, 0, 5, 5);
		gbc_lblServerPort.gridx = 1;
		gbc_lblServerPort.gridy = 2;
		frmMobilisxhuntDbimporter.getContentPane().add(lblServerPort, gbc_lblServerPort);
		
		tfServerPort = new JTextField();
		tfServerPort.setColumns(10);
		GridBagConstraints gbc_tfServerPort = new GridBagConstraints();
		gbc_tfServerPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfServerPort.gridwidth = 6;
		gbc_tfServerPort.insets = new Insets(0, 0, 5, 5);
		gbc_tfServerPort.gridx = 6;
		gbc_tfServerPort.gridy = 2;
		frmMobilisxhuntDbimporter.getContentPane().add(tfServerPort, gbc_tfServerPort);
		
		JLabel lblDbName = new JLabel("DB Name:");
		GridBagConstraints gbc_lblDbName = new GridBagConstraints();
		gbc_lblDbName.gridwidth = 5;
		gbc_lblDbName.anchor = GridBagConstraints.EAST;
		gbc_lblDbName.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbName.gridx = 1;
		gbc_lblDbName.gridy = 4;
		frmMobilisxhuntDbimporter.getContentPane().add(lblDbName, gbc_lblDbName);
		
		tfDbName = new JTextField();
		tfDbName.setColumns(10);
		GridBagConstraints gbc_tfDbName = new GridBagConstraints();
		gbc_tfDbName.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfDbName.gridwidth = 6;
		gbc_tfDbName.insets = new Insets(0, 0, 5, 5);
		gbc_tfDbName.gridx = 6;
		gbc_tfDbName.gridy = 4;
		frmMobilisxhuntDbimporter.getContentPane().add(tfDbName, gbc_tfDbName);
		
		JLabel lblDbUsername = new JLabel("DB Username:");
		GridBagConstraints gbc_lblDbUsername = new GridBagConstraints();
		gbc_lblDbUsername.gridwidth = 5;
		gbc_lblDbUsername.anchor = GridBagConstraints.EAST;
		gbc_lblDbUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbUsername.gridx = 1;
		gbc_lblDbUsername.gridy = 5;
		frmMobilisxhuntDbimporter.getContentPane().add(lblDbUsername, gbc_lblDbUsername);
		
		tfDbUsername = new JTextField();
		tfDbUsername.setColumns(10);
		GridBagConstraints gbc_tfDbUsername = new GridBagConstraints();
		gbc_tfDbUsername.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfDbUsername.gridwidth = 6;
		gbc_tfDbUsername.insets = new Insets(0, 0, 5, 5);
		gbc_tfDbUsername.gridx = 6;
		gbc_tfDbUsername.gridy = 5;
		frmMobilisxhuntDbimporter.getContentPane().add(tfDbUsername, gbc_tfDbUsername);
		
		JLabel lblDbPassword = new JLabel("DB Password:");
		GridBagConstraints gbc_lblDbPassword = new GridBagConstraints();
		gbc_lblDbPassword.gridwidth = 5;
		gbc_lblDbPassword.anchor = GridBagConstraints.EAST;
		gbc_lblDbPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbPassword.gridx = 1;
		gbc_lblDbPassword.gridy = 6;
		frmMobilisxhuntDbimporter.getContentPane().add(lblDbPassword, gbc_lblDbPassword);
		
		pfDbPassword = new JPasswordField();
		GridBagConstraints gbc_pfDbPassword = new GridBagConstraints();
		gbc_pfDbPassword.gridwidth = 6;
		gbc_pfDbPassword.insets = new Insets(0, 0, 5, 5);
		gbc_pfDbPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_pfDbPassword.gridx = 6;
		gbc_pfDbPassword.gridy = 6;
		frmMobilisxhuntDbimporter.getContentPane().add(pfDbPassword, gbc_pfDbPassword);
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateConnectionData();
				
				String messageText = "";
				int messageType = JOptionPane.INFORMATION_MESSAGE;
				
				if(mSqlHelper.testConnection()){
					messageText = "Connection successful.";
					
					if(!mSqlHelper.checkDbStructure()){
						messageText += "\nDatabase structure is unsupported. Please fix it!";
						messageType = JOptionPane.WARNING_MESSAGE;
					}
					else
						messageText += "\nDatabase structure is ok.";
				}
				else{
					messageText = "Connection failed.";
					messageType = JOptionPane.ERROR_MESSAGE;
				}
				
				JOptionPane.showMessageDialog(frmMobilisxhuntDbimporter, messageText, "Connection to DB", messageType);
			}
		});
		GridBagConstraints gbc_btnConnect = new GridBagConstraints();
		gbc_btnConnect.gridwidth = 4;
		gbc_btnConnect.insets = new Insets(0, 0, 5, 5);
		gbc_btnConnect.gridx = 2;
		gbc_btnConnect.gridy = 8;
		frmMobilisxhuntDbimporter.getContentPane().add(btnConnect, gbc_btnConnect);
		
		btnCreateStructure = new JButton("Create Structure");
		btnCreateStructure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateConnectionData();
				
				if(mSqlHelper.createDbStructure())
					JOptionPane.showMessageDialog(frmMobilisxhuntDbimporter, 
							"Database structure created.", "Success", JOptionPane.INFORMATION_MESSAGE);
				else
					JOptionPane.showMessageDialog(frmMobilisxhuntDbimporter, 
							"Database structure creation failed!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		btnImportFile = new JButton("Import File");
		btnImportFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateConnectionData();
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Choose a file");
				fileChooser.addChoosableFileFilter( new FileNameExtensionFilter("XML Files", "xml"));
				
				int fileResult = fileChooser.showOpenDialog(frmMobilisxhuntDbimporter);
				
			    if ( fileResult == JFileChooser.APPROVE_OPTION ) {			    	
					try {
						if(mSqlHelper.checkDbStructure()){
							importXmlFile(fileChooser.getSelectedFile().getAbsoluteFile().toString());
						}
						else{
							int state = JOptionPane.showOptionDialog(frmMobilisxhuntDbimporter, 
									"Database structure doesn't exist. Create Tables?\nWARNING: All data will be overriden!",
									"Error", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
							
							if(state == JOptionPane.YES_OPTION){
								mSqlHelper.createDbStructure();
								importXmlFile(fileChooser.getSelectedFile().getAbsoluteFile().toString());
							}
						}
					} catch (Exception e) {
						
						e.printStackTrace();
					}
			    }
			}
		});
		GridBagConstraints gbc_btnImportFile = new GridBagConstraints();
		gbc_btnImportFile.gridwidth = 4;
		gbc_btnImportFile.insets = new Insets(0, 0, 5, 5);
		gbc_btnImportFile.gridx = 8;
		gbc_btnImportFile.gridy = 8;
		frmMobilisxhuntDbimporter.getContentPane().add(btnImportFile, gbc_btnImportFile);
		GridBagConstraints gbc_btnCreateStructure = new GridBagConstraints();
		gbc_btnCreateStructure.gridwidth = 6;
		gbc_btnCreateStructure.insets = new Insets(0, 0, 0, 5);
		gbc_btnCreateStructure.gridx = 1;
		gbc_btnCreateStructure.gridy = 9;
		frmMobilisxhuntDbimporter.getContentPane().add(btnCreateStructure, gbc_btnCreateStructure);
		
		btnExportFile = new JButton("Export Data");
		btnExportFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String areaId = JOptionPane.showInputDialog(frmMobilisxhuntDbimporter, "Type in ID of area for export:",
						"Export to XML", JOptionPane.QUESTION_MESSAGE);
				
				if(areaId.length() > 0 && Integer.parseInt(areaId) > 0)
					mSqlHelper.exportAreaData(Integer.valueOf(areaId));
			}
		});
		GridBagConstraints gbc_btnExportFile = new GridBagConstraints();
		gbc_btnExportFile.gridwidth = 4;
		gbc_btnExportFile.insets = new Insets(0, 0, 0, 5);
		gbc_btnExportFile.gridx = 8;
		gbc_btnExportFile.gridy = 9;
		frmMobilisxhuntDbimporter.getContentPane().add(btnExportFile, gbc_btnExportFile);
	}
	
	/**
	 * Import xml file.
	 *
	 * @param filePath the file path
	 */
	private void importXmlFile(String filePath){
		boolean importSuccessful = false;
		
		try {
			importSuccessful = mSqlHelper.insertXmlDataIntoDB(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(importSuccessful)
			JOptionPane.showMessageDialog(frmMobilisxhuntDbimporter, 
					"File import successful.", "File Import", JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(frmMobilisxhuntDbimporter, 
					"File Import failed.", "File Import", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Insert default values.
	 */
	private void insertDefaultValues(){
		tfServerAddress.setText("127.0.0.1");
		tfServerPort.setText("3306");
		tfDbName.setText("mobilis_server");
		tfDbUsername.setText("mobilis");
		pfDbPassword.setText("mobilis");
		
		updateConnectionData();
	}
	
	/**
	 * Update connection data.
	 */
	private void updateConnectionData(){
		mSqlHelper.setServerAddress(tfServerAddress.getText());
		mSqlHelper.setServerPort(tfServerPort.getText());
		mSqlHelper.setDbName(tfDbName.getText());
		mSqlHelper.setDbUsername(tfDbUsername.getText());
		mSqlHelper.setDbPassword(new String(pfDbPassword.getPassword()));
	}

}
