package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.Controller;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.AreaInfo;

/**
 * The Class DialogLoadArea.
 */
public class DialogLoadArea extends JDialog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4851017653001449697L;
	
	/** The content panel. */
	private final JPanel contentPanel = new JPanel();
	
	/** The m area infos. */
	private ArrayList<AreaInfo> mAreaInfos;
	
	/** The tbl areas. */
	private JTable tblAreas;
	
	/** The m controller. */
	private Controller mController;

	/**
	 * Create the dialog.
	 *
	 * @param controller the controller
	 * @param parent the parent
	 * @param areaInfos the area infos
	 */
	public DialogLoadArea(Controller controller, JFrame parent, ArrayList<AreaInfo> areaInfos) {
		super(parent, true);
		mController = controller;
		mAreaInfos = areaInfos;
		
		initialize(parent);
		initData();
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		pack();
		setVisible(true);
	}
	
	/**
	 * Inits the data.
	 */
	private void initData(){
		DefaultTableModel model = (DefaultTableModel)tblAreas.getModel();

		for(AreaInfo info : mAreaInfos){
			model.addRow(new Object[]{info.ID, info.Name, info.Description, info.Version});
		}
	}
	
	/**
	 * Initialize.
	 *
	 * @param parent the parent
	 */
	private void initialize(JFrame parent) {
		setTitle("Load Area Data");
		setBounds(parent.getX() + parent.getWidth() / 2, 
				parent.getY() + parent.getHeight() / 2,
				450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			{
				tblAreas = new JTable();
				scrollPane.setViewportView(tblAreas);
				tblAreas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				tblAreas.setModel(new DefaultTableModel(
					new Object[][] {
					},
					new String[] {
						"ID", "Name", "Description", "Version"
					}
				));
				for(int i=0; i<tblAreas.getColumnModel().getColumnCount(); i++)
					tblAreas.getColumnModel().getColumn(i).setCellEditor(new UneditableTableCellEditor());
				
				tblAreas.addMouseListener(new MouseListener() {
					
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
						if(arg0.getClickCount() == 2){
							loadArea();
						}
					}
				});
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
						loadArea();
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
						DialogLoadArea.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * Load area.
	 */
	private void loadArea(){
		String str = tblAreas.getModel().getValueAt(tblAreas.getSelectedRow(), 0).toString();
		
		if(str != null){
			try	{
				int areaId = Integer.valueOf(str);
				AreaInfo selectedAreaInfo = null;
				
				for(AreaInfo info : mAreaInfos){
					if(info.ID == areaId){
						selectedAreaInfo = info.clone();
						break;
					}
				}
				
				if(selectedAreaInfo != null){
					mController.getRouteManagement().setAreaInfo(selectedAreaInfo);
					mController.getMainView().loadAreaData();
				}
			}
			catch(NumberFormatException e){
				System.err.println(e.getMessage());
			}
		}
		
		DialogLoadArea.this.dispose();
	}
}
