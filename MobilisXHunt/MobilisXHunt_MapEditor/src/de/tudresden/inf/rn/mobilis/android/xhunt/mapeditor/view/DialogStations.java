package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.Controller;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.Station;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * The Class DialogStations.
 */
public class DialogStations extends JDialog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6553985944427853503L;
	
	/** The content panel. */
	private final JPanel contentPanel = new JPanel();
	
	/** The table. */
	private JTable table;

	/**
	 * Create the dialog.
	 */
	public DialogStations() {
		initialize();
		updateData();
		
		setTitle("Stations");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * Update data.
	 */
	private void updateData(){
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		while (model.getRowCount() > 0){
			model.removeRow(0);
		}

		for(Station station : Controller.getInstance().getRouteManagement().getStations().values()){
			model.addRow(new Object[]{station.getId(), station.getName(),
					station.getAbbrevation()});
		}
		
		this.invalidate();
	}
	
	/**
	 * Initialize.
	 */
	private void initialize() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				table = new JTable();
				table.setModel(new DefaultTableModel(
					new Object[][] {
					},
					new String[] {
						"ID", "name", "Abbreavation"
					}
				));
				table.getColumnModel().getColumn(0).setCellEditor(new UneditableTableCellEditor());
				scrollPane.setViewportView(table);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						Controller.getInstance().setSelectedStation(
								Integer.valueOf(table.getModel().getValueAt(table.getSelectedRow(), 0).toString()));
						DialogStations.this.dispose();
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
						DialogStations.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
