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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.Controller;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.AreaInfo;

/**
 * The Class DialogNewArea.
 */
public class DialogNewArea extends JDialog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2471478120165617206L;
	
	/** The content panel. */
	private final JPanel contentPanel = new JPanel();
	
	/** The tf name. */
	private JTextField tfName;
	
	/** The tf desc. */
	private JTextField tfDesc;

	/** The m controller. */
	private Controller mController;
	
	/**
	 * Instantiates a new dialog new area.
	 *
	 * @param controller the controller
	 * @param parent the parent
	 */
	public DialogNewArea(Controller controller, JFrame parent) {
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
		setTitle("New Area");
		setBounds(parent.getX() + parent.getWidth() / 2, 
				parent.getY() + parent.getHeight() / 2, 377, 145);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{15, 0, 0, 15, 0};
		gbl_contentPanel.rowHeights = new int[]{19, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblName = new JLabel("Name:");
			GridBagConstraints gbc_lblName = new GridBagConstraints();
			gbc_lblName.anchor = GridBagConstraints.EAST;
			gbc_lblName.insets = new Insets(0, 0, 5, 5);
			gbc_lblName.gridx = 1;
			gbc_lblName.gridy = 1;
			contentPanel.add(lblName, gbc_lblName);
		}
		{
			tfName = new JTextField();
			GridBagConstraints gbc_tfName = new GridBagConstraints();
			gbc_tfName.insets = new Insets(0, 0, 5, 5);
			gbc_tfName.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfName.gridx = 2;
			gbc_tfName.gridy = 1;
			contentPanel.add(tfName, gbc_tfName);
			tfName.setColumns(10);
		}
		{
			JLabel lblDescription = new JLabel("Description:");
			GridBagConstraints gbc_lblDescription = new GridBagConstraints();
			gbc_lblDescription.anchor = GridBagConstraints.EAST;
			gbc_lblDescription.insets = new Insets(0, 0, 0, 5);
			gbc_lblDescription.gridx = 1;
			gbc_lblDescription.gridy = 2;
			contentPanel.add(lblDescription, gbc_lblDescription);
		}
		{
			tfDesc = new JTextField();
			GridBagConstraints gbc_tfDesc = new GridBagConstraints();
			gbc_tfDesc.insets = new Insets(0, 0, 0, 5);
			gbc_tfDesc.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfDesc.gridx = 2;
			gbc_tfDesc.gridy = 2;
			contentPanel.add(tfDesc, gbc_tfDesc);
			tfDesc.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Create");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						mController.getRouteManagement().setAreaInfo(
								new AreaInfo(0, tfName.getText(), tfDesc.getText(), 1));
						DialogNewArea.this.dispose();
					}
				});
				okButton.setActionCommand("Create");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						DialogNewArea.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
