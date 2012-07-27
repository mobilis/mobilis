package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.Station;

/**
 * The Class DialogStationInfo.
 */
public class DialogStationInfo extends JDialog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2307521747853588699L;
	
	/** The content panel. */
	private final JPanel contentPanel = new JPanel();
	
	/** The m station. */
	private Station mStation;
	
	/** The tf id. */
	private JTextField tfId;
	
	/** The tf name. */
	private JTextField tfName;
	
	/** The tf abbrev. */
	private JTextField tfAbbrev;
	
	/** The tf lat. */
	private JTextField tfLat;
	
	/** The tf lon. */
	private JTextField tfLon;

	/**
	 * Create the dialog.
	 *
	 * @param parent the parent
	 * @param station the station
	 */
	public DialogStationInfo(Component parent, Station station) {
		this.mStation = station;
		initialize(parent);
		
		tfId.setText("" + station.getId());
		tfName.setText(station.getName());
		tfAbbrev.setText(station.getAbbrevation());
		tfLat.setText("" + station.getGeoPoint().getLatitudeE6());
		tfLon.setText("" + station.getGeoPoint().getLongitudeE6());
		
		setTitle(station.getName());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		setVisible(true);
	}
	
	/**
	 * Initialize.
	 *
	 * @param parent the parent
	 */
	private void initialize(Component parent) {
		setBounds(parent.getX() + parent.getWidth() / 2, 
				parent.getY() + parent.getHeight() / 2, 346, 239);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{30, 0, 0, 30, 0};
		gbl_contentPanel.rowHeights = new int[]{30, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblId = new JLabel("ID:");
			GridBagConstraints gbc_lblId = new GridBagConstraints();
			gbc_lblId.anchor = GridBagConstraints.EAST;
			gbc_lblId.insets = new Insets(0, 0, 5, 5);
			gbc_lblId.gridx = 1;
			gbc_lblId.gridy = 1;
			contentPanel.add(lblId, gbc_lblId);
		}
		{
			tfId = new JTextField();
			tfId.setEditable(false);
			GridBagConstraints gbc_tfId = new GridBagConstraints();
			gbc_tfId.insets = new Insets(0, 0, 5, 5);
			gbc_tfId.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfId.gridx = 2;
			gbc_tfId.gridy = 1;
			contentPanel.add(tfId, gbc_tfId);
			tfId.setColumns(10);
		}
		{
			JLabel lblName = new JLabel("Name:");
			GridBagConstraints gbc_lblName = new GridBagConstraints();
			gbc_lblName.anchor = GridBagConstraints.EAST;
			gbc_lblName.insets = new Insets(0, 0, 5, 5);
			gbc_lblName.gridx = 1;
			gbc_lblName.gridy = 2;
			contentPanel.add(lblName, gbc_lblName);
		}
		{
			tfName = new JTextField();
			GridBagConstraints gbc_tfName = new GridBagConstraints();
			gbc_tfName.insets = new Insets(0, 0, 5, 5);
			gbc_tfName.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfName.gridx = 2;
			gbc_tfName.gridy = 2;
			contentPanel.add(tfName, gbc_tfName);
			tfName.setColumns(10);
		}
		{
			JLabel lblAbbrevation = new JLabel("Abbrevation:");
			GridBagConstraints gbc_lblAbbrevation = new GridBagConstraints();
			gbc_lblAbbrevation.anchor = GridBagConstraints.EAST;
			gbc_lblAbbrevation.insets = new Insets(0, 0, 5, 5);
			gbc_lblAbbrevation.gridx = 1;
			gbc_lblAbbrevation.gridy = 3;
			contentPanel.add(lblAbbrevation, gbc_lblAbbrevation);
		}
		{
			tfAbbrev = new JTextField();
			GridBagConstraints gbc_tfAbbrev = new GridBagConstraints();
			gbc_tfAbbrev.insets = new Insets(0, 0, 5, 5);
			gbc_tfAbbrev.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfAbbrev.gridx = 2;
			gbc_tfAbbrev.gridy = 3;
			contentPanel.add(tfAbbrev, gbc_tfAbbrev);
			tfAbbrev.setColumns(10);
		}
		{
			JLabel lblLatitude = new JLabel("Latitude:");
			GridBagConstraints gbc_lblLatitude = new GridBagConstraints();
			gbc_lblLatitude.anchor = GridBagConstraints.EAST;
			gbc_lblLatitude.insets = new Insets(0, 0, 5, 5);
			gbc_lblLatitude.gridx = 1;
			gbc_lblLatitude.gridy = 4;
			contentPanel.add(lblLatitude, gbc_lblLatitude);
		}
		{
			tfLat = new JTextField();
			tfLat.setEditable(false);
			GridBagConstraints gbc_tfLat = new GridBagConstraints();
			gbc_tfLat.insets = new Insets(0, 0, 5, 5);
			gbc_tfLat.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfLat.gridx = 2;
			gbc_tfLat.gridy = 4;
			contentPanel.add(tfLat, gbc_tfLat);
			tfLat.setColumns(10);
		}
		{
			JLabel lblLongitude = new JLabel("Longitude:");
			GridBagConstraints gbc_lblLongitude = new GridBagConstraints();
			gbc_lblLongitude.anchor = GridBagConstraints.EAST;
			gbc_lblLongitude.insets = new Insets(0, 0, 0, 5);
			gbc_lblLongitude.gridx = 1;
			gbc_lblLongitude.gridy = 5;
			contentPanel.add(lblLongitude, gbc_lblLongitude);
		}
		{
			tfLon = new JTextField();
			tfLon.setEditable(false);
			GridBagConstraints gbc_tfLon = new GridBagConstraints();
			gbc_tfLon.insets = new Insets(0, 0, 0, 5);
			gbc_tfLon.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfLon.gridx = 2;
			gbc_tfLon.gridy = 5;
			contentPanel.add(tfLon, gbc_tfLon);
			tfLon.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Save");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						mStation.setName(tfName.getText());
						mStation.setAbbrevation(tfAbbrev.getText());
						
						DialogStationInfo.this.dispose();
					}
				});
				okButton.setActionCommand("Save");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						DialogStationInfo.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
