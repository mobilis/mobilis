package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.Ticket;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * The Class DialogTickets.
 */
public class DialogTickets extends JDialog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5852031474910587745L;
	
	/** The content panel. */
	private final JPanel contentPanel = new JPanel();
	
	/** The table. */
	private JTable table;
	
	/** The m tickets. */
	private ArrayList<Ticket> mTickets = new ArrayList<Ticket>();

	/**
	 * Instantiates a new dialog tickets.
	 *
	 * @param parent the parent
	 * @param tickets the tickets
	 */
	public DialogTickets(JFrame parent, ArrayList<Ticket> tickets) {
		super(parent, true);
		this.mTickets = tickets;System.out.println(tickets.size());
		initialize(parent);
		initData();
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * Inits the data.
	 */
	private void initData(){
		DefaultTableModel model = (DefaultTableModel)table.getModel();

		for(Ticket ticket : mTickets){
			model.addRow(new Object[]{ticket.getId(), ticket.getName(), ticket.getIcon(), ticket.isSuperior()});
		}
	}
	
	/**
	 * Initialize.
	 *
	 * @param parent the parent
	 */
	private void initialize(Component parent) {
		setBounds(parent.getX() + parent.getWidth() / 2, 
				parent.getY() + parent.getHeight() / 2, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			{
				table = new JTable();
				table.setModel(new DefaultTableModel(
					new Object[][] {
					},
					new String[] {
						"ID", "Name", "Icon", "Is Superior"
					}
				));
				table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JCheckBox()));
				table.getColumnModel().getColumn(0).setCellEditor(new UneditableTableCellEditor());
				table.getColumnModel().getColumn(3).setCellRenderer(table.getDefaultRenderer(Boolean.class));
				scrollPane.setViewportView(table);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Save");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						DialogTickets.this.dispose();
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
						DialogTickets.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
