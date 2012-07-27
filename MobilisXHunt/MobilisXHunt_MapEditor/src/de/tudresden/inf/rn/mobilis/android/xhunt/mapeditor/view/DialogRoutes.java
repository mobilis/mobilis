package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.Controller;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.Route;

/**
 * The Class DialogRoutes.
 */
public class DialogRoutes extends JDialog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2676776721182974926L;
	
	/** The content panel. */
	private final JPanel contentPanel = new JPanel();
	
	/** The table. */
	private JTable table;
	
	/** The m routes. */
	private HashMap<Integer, Route> mRoutes = new HashMap<Integer, Route>();

	/**
	 * Instantiates a new dialog routes.
	 *
	 * @param parent the parent
	 */
	public DialogRoutes(JFrame parent) {
		super(parent);
		this.mRoutes = Controller.getInstance().getRouteManagement().getRoutes();
		initialize(parent);
		updateData();
		
		setTitle("Routes");
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

		for(Route route : mRoutes.values()){
			model.addRow(new Object[]{route.getId(), route.getName(),
					route.getTicketId(), route.getStart(), route.getEnd(),
					route.getStationIds().size(), route.isShowOnMap(), route.getColor()});
		}
		
		this.invalidate();
	}
	
	/**
	 * Initialize.
	 *
	 * @param parent the parent
	 */
	private void initialize(JFrame parent) {
		setBounds(parent.getX() + parent.getWidth() / 2, 
				parent.getY() + parent.getHeight() / 2, 463, 489);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			{
				table = new JTable();
				final DefaultTableModel model = new DefaultTableModel(
					new Object[][] {
					},
					new String[] {
						"ID", "Name", "Ticket", "Start", "End", "Stations", "Visible", "Color"
					}
				);
				table.setModel(model);
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				table.getColumnModel().getColumn(0).setCellEditor(new UneditableTableCellEditor());
				table.getColumnModel().getColumn(5).setCellEditor(new UneditableTableCellEditor());
				table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JCheckBox()));
				table.getColumnModel().getColumn(7).setCellEditor(new UneditableTableCellEditor());
				
				table.getColumnModel().getColumn(6).setCellRenderer(table.getDefaultRenderer(Boolean.class));
				table.getColumnModel().getColumn(7).setCellRenderer(new ColorCellRenderer());
				
				table.addMouseListener(new MouseListener() {
					
					@Override
					public void mouseReleased(MouseEvent arg0) {}
					
					@Override
					public void mousePressed(MouseEvent arg0) {}
					
					@Override
					public void mouseExited(MouseEvent arg0) {}
					
					@Override
					public void mouseEntered(MouseEvent arg0) {}
					
					@Override
					public void mouseClicked(MouseEvent arg0) {
						int column = table.columnAtPoint(arg0.getPoint());
						int row = table.rowAtPoint(arg0.getPoint());
						
						if(column == 7){
							Color color = JColorChooser.showDialog(DialogRoutes.this, "Choose a Color", Color.blue);
							
							if(color != null){
								mRoutes.get(table.getValueAt(row, 0)).setColor(color);
								
								Controller.getInstance().getMainView().updateOverlays();
								model.fireTableDataChanged();
							}
						}
						if(column == 6){
							mRoutes.get(table.getValueAt(row, 0)).toggleShowOnMap();
							Controller.getInstance().getMainView().updateOverlays();
						}
					}					
				});
				
				scrollPane.setViewportView(table);
			}
		}
		{
			JToolBar toolBar = new JToolBar();
			contentPanel.add(toolBar, BorderLayout.NORTH);
			{
				JButton btnAdd = new JButton("Add");
				btnAdd.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						Route route = new Route(Controller.getInstance().getRouteManagement().getNewRouteId(),
								"New", -1, "start", "end");
						Controller.getInstance().getRouteManagement().getRoutes().put(route.getId(), route);
						updateData();
					}
				});
				toolBar.add(btnAdd);
			}
			{
				JButton btnEdit = new JButton("Edit");
				btnEdit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						new DialogStationsOfRoute(null, mRoutes.get(table.getModel().getValueAt(table.getSelectedRow(), 0)));
					}
				});
				toolBar.add(btnEdit);
			}
			{
				JButton btnDelete = new JButton("Delete");
				btnDelete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if(table.getSelectedRow() > -1){
							Route route = mRoutes.get(table.getModel().getValueAt(table.getSelectedRow(), 0));
							
							Controller.getInstance().getRouteManagement().removeRoute(route.getId());
							Controller.getInstance().getMainView().updateOverlays();
							updateData();
						}
					}
				});
				toolBar.add(btnDelete);
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
						DialogRoutes.this.dispose();
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
						DialogRoutes.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	

	/**
	 * The Class ColorCellRenderer.
	 */
	private class ColorCellRenderer extends DefaultTableCellRenderer {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 5831177165184940735L;

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {			
			Route route = mRoutes.get(table.getValueAt(row, 0));
			
			if(route != null)
				setBackground(route.getColor());
			
			return this;
		}
	}

}
