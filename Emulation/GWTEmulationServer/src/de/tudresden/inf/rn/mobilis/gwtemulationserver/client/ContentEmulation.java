package de.tudresden.inf.rn.mobilis.gwtemulationserver.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.InstanceGroupExecutorInfo;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.InstanceGroupInfo;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.ScriptInfo;

public class ContentEmulation extends VerticalPanel {
	
	private static final int REFRESH_INTERVAL = 500;
	
	private HorizontalPanel errorPanel;
	private Label lblError;
	private List<String> connectedDevices = new ArrayList<String>();
	private ScriptInfo virtualDevices;
	private List<String> scriptList = new ArrayList<String>();
	
	private Integer neededDevices = 0;
	private EmuServerConnectServiceAsync emuServerConnectSvc;
	private Timer deviceFetcher;
	private String selectedScript;
	
	private ScrollPanel mainScrollPanel;

	private VerticalPanel mainVPanel;
	
	public ContentEmulation (EmuServerConnectServiceAsync emuServerConnectSvc) {
		
		super();
		
		this.emuServerConnectSvc = emuServerConnectSvc;
				
		mainVPanel = new VerticalPanel();

		mainScrollPanel = new ScrollPanel(mainVPanel);
		
		errorPanel = new HorizontalPanel();
		lblError = new Label("");
		lblError.setStyleName("errorlabel");
		errorPanel.add(lblError);
		
		emuServerConnectSvc.getScriptList(new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				setErrorLabel("Fehler beim Abruf der Skriptliste!");
			}

			@Override
			public void onSuccess(List<String> result) {
				scriptList = result;
				initScriptSelection();
			}
		});
		
	}
	
	@Override
	protected void onLoad() {
		mainScrollPanel.setSize(Window.getClientWidth() + "px", (Window.getClientHeight() - this.getAbsoluteTop()) + "px");
		super.onLoad();
	}
	
	private void initScriptSelection() {
		
		this.clear();
		setErrorLabel("");
		
		if(scriptList.size() < 1) {
			this.add(errorPanel);
			setErrorLabel("Keine Skripte vorhanden!");
			return;
		}
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		HorizontalPanel listBoxPanel = new HorizontalPanel();
		HorizontalPanel connectedPanel = new HorizontalPanel();
		HorizontalPanel neededPanel = new HorizontalPanel();
		
		final Button btnSelectDevices = new Button("Weiter...");
		Label lblScript = new Label("Bitte Skript wÃ¤hlen:");
		final ListBox lbScriptList = new ListBox(false);
		Label lblConnectedDevices = new Label("Verbundene Clients: ");
		final Label lblConnectedDevicesCount = new Label();
		Label lblNeededDevices = new Label("Clients im Skript: ");
		final Label lblNeededDevicesCount = new Label();
		
		btnSelectDevices.setStyleName("button");
		btnSelectDevices.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				selectedScript = lbScriptList.getItemText(lbScriptList.getSelectedIndex());
				deviceFetcher.cancel();
				initDeviceAssignment(selectedScript);
			}
		});		
		if(scriptList.size() < 1 || connectedDevices.size() < 1) {
			btnSelectDevices.setEnabled(false);
		} else {
			btnSelectDevices.setEnabled(true);
		}
		buttonPanel.add(btnSelectDevices);
		
		lblScript.setWidth("250px");
		lbScriptList.setWidth("200px");
		for(String script:scriptList) {
			lbScriptList.addItem(script);
		}
		selectedScript = lbScriptList.getItemText(lbScriptList.getSelectedIndex());
		lbScriptList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				selectedScript = lbScriptList.getItemText(lbScriptList.getSelectedIndex());
				getNeededDevices(lblNeededDevicesCount);
			}
		});
		listBoxPanel.add(lblScript);
		listBoxPanel.add(lbScriptList);
		
		connectedPanel.add(lblConnectedDevices);
		connectedPanel.add(lblConnectedDevicesCount);
		neededPanel.add(lblNeededDevices);
		neededPanel.add(lblNeededDevicesCount);
		
		lblConnectedDevices.setWidth("250px");
		lblConnectedDevicesCount.setWidth("200px");
		lblNeededDevices.setWidth("250px");
		lblNeededDevicesCount.setWidth("200px");
		
		this.add(buttonPanel);
		this.add(errorPanel);
		this.add(listBoxPanel);
		this.add(connectedPanel);
		this.add(neededPanel);
		
		getNeededDevices(lblNeededDevicesCount);
		deviceFetcher = new Timer() {
			@Override
			public void run() {
				getConnectedDevices(lblConnectedDevicesCount,btnSelectDevices);
			}
		};
		deviceFetcher.scheduleRepeating(REFRESH_INTERVAL);
		
	}
	
	private void getConnectedDevices(final Label lblConnectedDevicesCount, final Button btnSelectDevices) {
		emuServerConnectSvc.getDeviceList(new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable caught) {
			}
			@Override
			public void onSuccess(List<String> result) {
				if(result != null) {
					Integer count = result.size();
					lblConnectedDevicesCount.setText(count.toString());
					if(count > 0) btnSelectDevices.setEnabled(true);
					else btnSelectDevices.setEnabled(false);
					connectedDevices = result;
				}
			}
		});
	}
	
	private void getNeededDevices(final Label lblNeededDevicesCount) {
		
		emuServerConnectSvc.getNeededDevices(selectedScript, new AsyncCallback<ScriptInfo>() {
			@Override
			public void onFailure(Throwable caught) {
			}
			@Override
			public void onSuccess(ScriptInfo result) {
				if(result != null) {
					Integer count = 0;
					count += result.getInstances().size();
					for(Entry<String, InstanceGroupInfo> entry:result.getInstanceGroups().entrySet()) {
						count += entry.getValue().getCount();
					}
					virtualDevices = result;
					neededDevices = count;
					lblNeededDevicesCount.setText(neededDevices.toString());
				} else {
					setErrorLabel("Fehler beim Abrfufen der Skript-Infos!");
				}
			}
		});
	}
	
	private void initDeviceAssignment(String script) {
		
		this.clear();
		mainScrollPanel.clear();
		mainVPanel.clear();
		lblError.setText("");
		
		this.add(mainScrollPanel);
		mainScrollPanel.add(mainVPanel);
		mainVPanel.add(errorPanel);
		mainVPanel.add(getDeviceSelectionPanel());
		
	}
	
	private void setErrorLabel(String error) {
		
		lblError.setText(error);
		//errorPanel.setVisible(true);
		
	}
	
	// creates the panel for device selection
	private VerticalPanel getDeviceSelectionPanel() {
			
		VerticalPanel deviceSelectionPanel = new VerticalPanel();
		final Button btnStartEmulation2 = new Button("Emulation starten...");
			
		HorizontalPanel pHeader = new HorizontalPanel();
		pHeader.setSpacing(2);
		Label header1 = new Label("Skriptvariable");
		Label header2 = new Label("Client");
		header1.setHorizontalAlignment(ALIGN_CENTER);
		header1.setWidth("400px");
		header2.setHorizontalAlignment(ALIGN_CENTER);
		header2.setWidth("400px");
		pHeader.add(header1);
		pHeader.add(header2);
			
		deviceSelectionPanel.add(btnStartEmulation2);
		deviceSelectionPanel.add(pHeader);
			
		final Map<String,ListBox> instanceListBoxes = new HashMap<String,ListBox>();
		final Map<String,InstanceGroupListBoxesList> instanceGroupListBoxes = new HashMap<String,InstanceGroupListBoxesList>();
			
		btnStartEmulation2.setStyleName("button");
		btnStartEmulation2.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
					
				Map<String,String> instanceSelection = new HashMap<String,String>();
				for(Map.Entry<String, ListBox> entry:instanceListBoxes.entrySet()) {
					//instanceSelection.add(instanceListBoxes.get(i).getItemText(instanceListBoxes.get(i).getSelectedIndex()));
					String virtDev = entry.getKey();
					ListBox lb = entry.getValue();
					instanceSelection.put(virtDev, lb.getItemText(lb.getSelectedIndex()));
				}
					
				Map<String,InstanceGroupExecutorInfo> instanceGroupSelection = new HashMap<String, InstanceGroupExecutorInfo>();
				for(Map.Entry<String, InstanceGroupListBoxesList> entry:instanceGroupListBoxes.entrySet()) {
					String virtDevGroupName = entry.getKey();
					List<ListBox> boxes = entry.getValue().getListBoxes();
					List<String> selected = new ArrayList<String>();
					for(ListBox lb:boxes) {
						selected.add(lb.getItemText(lb.getSelectedIndex()));
					}
					instanceGroupSelection.put(virtDevGroupName, new InstanceGroupExecutorInfo(virtDevGroupName, selected, entry.getValue().getFirstInstanceId()));
				}
					
				startEmulation(instanceSelection,instanceGroupSelection);
			}
		});
			
		// add selection for instances
		for(int i=0;i<virtualDevices.getInstances().size();i++) {
				
			String virtDev = virtualDevices.getInstances().get(i);
				
			// variable in the script
			Label lblVirtual = new Label(virtDev);
			lblVirtual.setHorizontalAlignment(ALIGN_CENTER);
			lblVirtual.setWidth("400px");
				
			// listbox with connected devices
			ListBox lbConnectedDevices = new ListBox(false);
			lbConnectedDevices.setWidth("400px");
			for(String device:connectedDevices) {
				lbConnectedDevices.addItem(device);
			}
			instanceListBoxes.put(virtDev, lbConnectedDevices);
							
			HorizontalPanel devPanel = new HorizontalPanel();
			devPanel.setSpacing(4);
			devPanel.setBorderWidth(1);
				
			devPanel.add(lblVirtual);
			devPanel.add(lbConnectedDevices);
				
			deviceSelectionPanel.add(devPanel);
		}
			
		// add selection for instance groups
		Tree groupTree = new Tree();
		for(Entry<String, InstanceGroupInfo> entry:virtualDevices.getInstanceGroups().entrySet()) {
				
			final List<ListBox> groupListBoxesSolo = new ArrayList<ListBox>();
			
			String virtDevGroupName = entry.getKey();
			InstanceGroupInfo instanceGroup = entry.getValue();
			Integer virtDevCount = instanceGroup.getCount();
				
			HorizontalPanel groupRootPanel = new HorizontalPanel();
			Label lblGroupRoot = new Label(virtDevGroupName);
			lblGroupRoot.setHorizontalAlignment(ALIGN_CENTER);
			lblGroupRoot.setWidth("380px");
			ListBox lbGroupRoot = new ListBox(false);
			lbGroupRoot.setWidth("400px");
			for(String device:connectedDevices) {
				lbGroupRoot.addItem(device);
			}
			lbGroupRoot.addChangeHandler(new ChangeHandler() {
					
				@Override
				public void onChange(ChangeEvent event) {
					ListBox listbox = ((ListBox)event.getSource());
					Integer selected = listbox.getSelectedIndex(); 
					for(ListBox lb:groupListBoxesSolo) {
						lb.setSelectedIndex(selected);
					}
				}
			});
			groupRootPanel.add(lblGroupRoot);
			groupRootPanel.add(lbGroupRoot);
				
			TreeItem groupRootItem = new TreeItem(groupRootPanel);
				
			int firstInstanceId = instanceGroup.getFirstInstanceId();
			for(int i = firstInstanceId; i < virtDevCount + firstInstanceId; i++) {
				HorizontalPanel groupItemPanel = new HorizontalPanel();
				Label lblGroupItem = new Label(virtDevGroupName + "/" + i);
				lblGroupItem.setWidth("380px");
				ListBox lbGroupItem = new ListBox(false);
				lbGroupItem.setWidth("380px");
				for(String device:connectedDevices) {
					lbGroupItem.addItem(device);
				}
				groupListBoxesSolo.add(lbGroupItem);
				groupItemPanel.add(lblGroupItem);
				groupItemPanel.add(lbGroupItem);
				groupRootItem.addItem(new TreeItem(groupItemPanel));
			}
			instanceGroupListBoxes.put(virtDevGroupName, new InstanceGroupListBoxesList(groupListBoxesSolo, firstInstanceId));
				
			groupTree.addItem(groupRootItem);
			
			HorizontalPanel devGroupPanel = new HorizontalPanel();
			devGroupPanel.setSpacing(4);
			devGroupPanel.setBorderWidth(1);
				
			devGroupPanel.add(groupTree);
				
			deviceSelectionPanel.add(devGroupPanel);
			
		}
			
		//sessionPanel2.add(groupTree);
			
		return deviceSelectionPanel;
	}
	
	// execute emulation
	private void startEmulation(Map<String, String> instanceSelection, Map<String, InstanceGroupExecutorInfo> instanceGroupSelection) {
		
		this.clear();
		mainScrollPanel.clear();
		mainVPanel.clear();
		
		HorizontalPanel statusPanel = new HorizontalPanel();
		final Label lblStatus = new Label("Executing...");
		statusPanel.add(lblStatus);
		
		emuServerConnectSvc.startScript(selectedScript, instanceSelection, instanceGroupSelection, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				setErrorLabel("Skriptausführung konnte nicht gestartet werden: <br>" + caught.getMessage());
				System.err.println("Skriptausführung konnte nicht gestartet werden! ");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Boolean result) {
				if(result) {
					lblStatus.setText("Skript wurde ausgeführt!");
				} else {
					lblStatus.setText("Skript wurde nicht ausgeführt!");
				}
			}
		});
		this.add(mainScrollPanel);
		mainScrollPanel.add(mainVPanel);
		mainVPanel.add(errorPanel);
		mainVPanel.add(statusPanel);
		
	}

}
