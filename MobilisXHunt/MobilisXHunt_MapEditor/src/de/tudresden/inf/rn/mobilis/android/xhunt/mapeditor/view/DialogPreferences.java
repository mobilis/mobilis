package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.Controller;

/**
 * The Class DialogPreferences.
 */
public class DialogPreferences extends JDialog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 318525163427080717L;

	/** The content panel. */
	private final JPanel contentPanel = new JPanel();
	
	/** The tf server address. */
	private JTextField tfServerAddress;
	
	/** The tf server port. */
	private JTextField tfServerPort;
	
	/** The tf db name. */
	private JTextField tfDbName;
	
	/** The tf db user. */
	private JTextField tfDbUser;
	
	/** The pf db password. */
	private JPasswordField pfDbPassword;
	
	/** The m controller. */
	private Controller mController;

	/**
	 * Create the dialog.
	 *
	 * @param controller the controller
	 * @param parent the parent
	 */
	public DialogPreferences(Controller controller, JFrame parent) {
		super(parent, true);
		mController = controller;
		initialize(parent);
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * Initialize.
	 *
	 * @param parent the parent
	 */
	private void initialize(JFrame parent) {
		setTitle("DB Preferences");
		setBounds(parent.getX() + parent.getWidth() / 2, 
				parent.getY() + parent.getHeight() / 2, 345, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{30, 30, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{35, 0, 0, 35, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel label = new JLabel("Server Address:");
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.anchor = GridBagConstraints.EAST;
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.gridx = 2;
			gbc_label.gridy = 1;
			contentPanel.add(label, gbc_label);
		}
		{
			tfServerAddress = new JTextField();
			tfServerAddress.setText(mController.getSqlHelper().getServerAddress());
			tfServerAddress.setColumns(10);
			GridBagConstraints gbc_tfServerAddress = new GridBagConstraints();
			gbc_tfServerAddress.insets = new Insets(0, 0, 5, 0);
			gbc_tfServerAddress.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfServerAddress.gridx = 3;
			gbc_tfServerAddress.gridy = 1;
			contentPanel.add(tfServerAddress, gbc_tfServerAddress);
		}
		{
			JLabel label = new JLabel("Server Port:");
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.anchor = GridBagConstraints.EAST;
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.gridx = 2;
			gbc_label.gridy = 2;
			contentPanel.add(label, gbc_label);
		}
		{
			tfServerPort = new JTextField();
			tfServerPort.setText(mController.getSqlHelper().getServerPort());
			tfServerPort.setColumns(10);
			GridBagConstraints gbc_tfServerPort = new GridBagConstraints();
			gbc_tfServerPort.insets = new Insets(0, 0, 5, 0);
			gbc_tfServerPort.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfServerPort.gridx = 3;
			gbc_tfServerPort.gridy = 2;
			contentPanel.add(tfServerPort, gbc_tfServerPort);
		}
		{
			JLabel label = new JLabel("DB Name:");
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.anchor = GridBagConstraints.EAST;
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.gridx = 2;
			gbc_label.gridy = 4;
			contentPanel.add(label, gbc_label);
		}
		{
			tfDbName = new JTextField();
			tfDbName.setText(mController.getSqlHelper().getDbName());
			tfDbName.setColumns(10);
			GridBagConstraints gbc_tfDbName = new GridBagConstraints();
			gbc_tfDbName.insets = new Insets(0, 0, 5, 0);
			gbc_tfDbName.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfDbName.gridx = 3;
			gbc_tfDbName.gridy = 4;
			contentPanel.add(tfDbName, gbc_tfDbName);
		}
		{
			JLabel label = new JLabel("DB Username:");
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.anchor = GridBagConstraints.EAST;
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.gridx = 2;
			gbc_label.gridy = 5;
			contentPanel.add(label, gbc_label);
		}
		{
			tfDbUser = new JTextField();
			tfDbUser.setText(mController.getSqlHelper().getDbUsername());
			tfDbUser.setColumns(10);
			GridBagConstraints gbc_tfDbUser = new GridBagConstraints();
			gbc_tfDbUser.insets = new Insets(0, 0, 5, 0);
			gbc_tfDbUser.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfDbUser.gridx = 3;
			gbc_tfDbUser.gridy = 5;
			contentPanel.add(tfDbUser, gbc_tfDbUser);
		}
		{
			JLabel label = new JLabel("DB Password:");
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.anchor = GridBagConstraints.EAST;
			gbc_label.insets = new Insets(0, 0, 0, 5);
			gbc_label.gridx = 2;
			gbc_label.gridy = 6;
			contentPanel.add(label, gbc_label);
		}
		{
			pfDbPassword = new JPasswordField();
			pfDbPassword.setText(mController.getSqlHelper().getDbPassword());
			GridBagConstraints gbc_pfDbPassword = new GridBagConstraints();
			gbc_pfDbPassword.fill = GridBagConstraints.HORIZONTAL;
			gbc_pfDbPassword.gridx = 3;
			gbc_pfDbPassword.gridy = 6;
			contentPanel.add(pfDbPassword, gbc_pfDbPassword);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						mController.getSqlHelper().setServerAddress(tfServerAddress.getText());
						mController.getSqlHelper().setServerPort(tfServerPort.getText());
						mController.getSqlHelper().setDbName(tfDbName.getText());
						mController.getSqlHelper().setDbUsername(tfDbUser.getText());
						mController.getSqlHelper().setDbPassword(new String(pfDbPassword.getPassword()));
						
						DialogPreferences.this.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						DialogPreferences.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
