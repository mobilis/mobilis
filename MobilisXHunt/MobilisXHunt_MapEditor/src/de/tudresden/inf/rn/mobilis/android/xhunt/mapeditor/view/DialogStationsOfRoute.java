package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.Controller;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.Route;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.Station;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * The Class DialogStationsOfRoute.
 */
public class DialogStationsOfRoute extends JDialog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7713534973625585127L;
	
	/** The content panel. */
	private final JPanel contentPanel = new JPanel();

	/** The m route. */
	private Route mRoute;
	
	/** The table. */
	private JTable table;

	/**
	 * Create the dialog.
	 *
	 * @param parent the parent
	 * @param route the route
	 */
	public DialogStationsOfRoute(JFrame parent, Route route) {
		super(parent);
		this.mRoute = route;
		initialize(parent);
		updateData();		
		
		setTitle("Stations of Route: " + route.getName());
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

		for(Map.Entry<Integer, Integer> entry : mRoute.getStationIds().entrySet()){
			Station station = Controller.getInstance().getRouteManagement().getStation(entry.getValue());
			
			if(station != null){
				model.addRow(new Object[]{entry.getKey(), station.getName(),
						station.getId()});
			}
		}
		
		this.invalidate();
	}
	
	/**
	 * Initialize.
	 *
	 * @param parent the parent
	 */
	private void initialize(JFrame parent) {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JToolBar toolBar = new JToolBar();
			contentPanel.add(toolBar, BorderLayout.NORTH);
			{
				JButton btnPositionUp = new JButton("Position Up");
				btnPositionUp.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						mRoute.positionUp(Integer.valueOf(table.getModel().getValueAt(table.getSelectedRow(), 0).toString()));
						updateData();
						Controller.getInstance().getMainView().updateOverlays();
					}
				});
				toolBar.add(btnPositionUp);
			}
			{
				JButton btnPositionDown = new JButton("Position Down");
				btnPositionDown.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						mRoute.positionDown(Integer.valueOf(table.getModel().getValueAt(table.getSelectedRow(), 0).toString()));
						updateData();
						Controller.getInstance().getMainView().updateOverlays();
					}
				});
				toolBar.add(btnPositionDown);
			}
			{
				JSeparator separator = new JSeparator();
				toolBar.add(separator);
			}
			{
				JButton btnAdd = new JButton("Add");
				btnAdd.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						DialogStations dialog = new DialogStations();
						dialog.addWindowListener(new WindowListener() {
							
							@Override
							public void windowOpened(WindowEvent arg0) {}
							
							@Override
							public void windowIconified(WindowEvent arg0) {}
							
							@Override
							public void windowDeiconified(WindowEvent arg0) {}
							
							@Override
							public void windowDeactivated(WindowEvent arg0) {}
							
							@Override
							public void windowClosing(WindowEvent arg0) {}
							
							@Override
							public void windowClosed(WindowEvent arg0) {
								mRoute.addStationAtLast(Controller.getInstance().getSelectedStation());
								updateData();
								Controller.getInstance().getMainView().updateOverlays();
							}
							
							@Override
							public void windowActivated(WindowEvent arg0) {}
						});
					}
				});
				toolBar.add(btnAdd);
			}
			{
				JButton btnDelete = new JButton("Delete");
				btnDelete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						mRoute.removeStation(Integer.valueOf(table.getModel().getValueAt(table.getSelectedRow(), 2).toString()));
						updateData();
						Controller.getInstance().getMainView().updateOverlays();
					}
				});
				toolBar.add(btnDelete);
			}
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				table = new JTable();
				table.setModel(new DefaultTableModel(
					new Object[][] {
					},
					new String[] {
						"Position", "Name", "ID"
					}
				));
				
				table.getColumnModel().getColumn(0).setCellEditor(new UneditableTableCellEditor());
				table.getColumnModel().getColumn(1).setCellEditor(new UneditableTableCellEditor());
				table.getColumnModel().getColumn(2).setCellEditor(new UneditableTableCellEditor());
				
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
						DialogStationsOfRoute.this.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}
