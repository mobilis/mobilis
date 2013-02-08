package de.tudresden.inf.rn.mobilis.gwtemulationserver.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.InstanceGroupExecutorInfo;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.InstanceGroupInfo;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.ScriptInfo;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.SessionInfo;

public class ContentEmulation extends VerticalPanel {
	
	private static final int REFRESH_INTERVAL = 500;
	
	private HorizontalPanel errorPanel;
	private HorizontalPanel buttonPanel;
		
	private Button btnNewSession;
	private Button btnOpenSession;
	private Button btnCloseSession;
	private Button btnStartEmulation;
	
	private Label lblError;
	private Label lblConnectedDevices = new Label();
	private Label lblNeededDevices = new Label();
	
	private List<String> connectedDevices = new ArrayList<String>();
	private ScriptInfo virtualDevices;
	private List<String> scriptList = new ArrayList<String>();
	
	private String currentSession;
	private Integer neededDevices = 0;
	private EmuServerConnectServiceAsync emuServerConnectSvc;
	private Timer deviceFetcher;
	private String currentStep;
	private String selectedScript;
	
	private ScrollPanel mainScrollPanel;

	private VerticalPanel mainVPanel;
	
	public ContentEmulation (EmuServerConnectServiceAsync emuServerConnectSvc) {
		
		super();
		
		this.emuServerConnectSvc = emuServerConnectSvc;
		currentSession = "";
		
		deviceFetcher = new Timer() {
			
			@Override
			public void run() {
				getDeviceList();
				updateDevices();
			}
		};
		
		currentStep = "";
		selectedScript = "";
		
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
				initClosedSession();
			}
		});
		
	}
	
	@Override
	protected void onLoad() {
		mainScrollPanel.setSize(Window.getClientWidth() + "px", (Window.getClientHeight() - this.getAbsoluteTop()) + "px");
		super.onLoad();
	}
	
	private void getDeviceList() {
		emuServerConnectSvc.getDeviceList(currentSession, new GetDeviceCallback());
	}
	
	private void openSession(String id) {
		emuServerConnectSvc.openSession(id, new SessionOpenCallback());
	}
	
	private void newSession() {
		emuServerConnectSvc.openSession("", new SessionOpenCallback());
	}
	
	private void closeSession() {
		emuServerConnectSvc.closeSession(currentSession, new SessionCloseCallback());
	}
	
	private void initOpenedSession() {
		
		this.clear();
		mainScrollPanel.clear();
		mainVPanel.clear();
		
		this.currentStep = "STEP1";
		
		emuServerConnectSvc.getNeededDevices(selectedScript, new AsyncCallback<ScriptInfo>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onSuccess(ScriptInfo result) {
				if(result != null) {
					Integer count = 0;
					count += result.getInstances().size();
					for(Map.Entry<String, InstanceGroupInfo> entry:result.getInstanceGroups().entrySet()) {
						count += entry.getValue().getCount();
					}
					virtualDevices = result;
					neededDevices = count;
				} else {
					setErrorLabel("Problem mit dem gewählten Skript!");
				}
			}
		});
		
		getDeviceList();
		
		lblError.setText("");
		
		buttonPanel = new HorizontalPanel();
		btnCloseSession = new Button("close session");
		
		btnCloseSession.setStyleName("button");
		btnCloseSession.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				closeSession();
			}
		});
		
		buttonPanel.add(btnCloseSession);
		
		VerticalPanel sessionPanel = getSessionPanel();
		this.add(mainScrollPanel);
		mainScrollPanel.add(mainVPanel);
		mainVPanel.add(buttonPanel);
		mainVPanel.add(errorPanel);
		mainVPanel.add(sessionPanel);
		
		deviceFetcher.scheduleRepeating(REFRESH_INTERVAL);
		
	}
	
	private void initClosedSession() {
		
		this.clear();
		mainScrollPanel.clear();
		mainVPanel.clear();
		
		buttonPanel = new HorizontalPanel();
		btnNewSession = new Button("new session");
		btnOpenSession = new Button("open session...");
		
		HorizontalPanel listBoxPanel = new HorizontalPanel();
		Label lblScript = new Label("Bitte Skript wählen:");
		lblScript.setWidth("250px");
		final ListBox lbScriptList = new ListBox(false);
		lbScriptList.setWidth("200px");
		for(String script:scriptList) {
			lbScriptList.addItem(script);
		}
		listBoxPanel.add(lblScript);
		listBoxPanel.add(lbScriptList);
		
		btnNewSession.setStyleName("button");
		btnNewSession.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				selectedScript = lbScriptList.getItemText(lbScriptList.getSelectedIndex());
				newSession();
			}
		});
		btnOpenSession.setStyleName("button");
		btnOpenSession.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				selectedScript = lbScriptList.getItemText(lbScriptList.getSelectedIndex());
				final DialogBox inputIDBox = new DialogBox();
				inputIDBox.setAnimationEnabled(true);
				inputIDBox.setText("Session-ID eingeben");
				
				VerticalPanel dialogV = new VerticalPanel();
				final TextBox input = new TextBox();
				Button inputButton = new Button("Open");
				inputButton.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						String inputID = input.getText();
						if(!inputID.equals("")) {
							openSession(inputID);
						}
						inputIDBox.hide();
					}
				});
				dialogV.add(input);
				dialogV.add(inputButton);
				
				inputIDBox.setWidget(dialogV);
				inputIDBox.center();
			}
		});
		
		if(scriptList.size() < 1) {
			btnNewSession.setEnabled(false);
			btnOpenSession.setEnabled(false);
		} else {
			btnNewSession.setEnabled(true);
			btnOpenSession.setEnabled(true);
		}
		
		buttonPanel.add(btnNewSession);
		//buttonPanel.add(btnOpenSession);
		
		this.add(mainScrollPanel);
		mainScrollPanel.add(mainVPanel);
		mainVPanel.add(buttonPanel);
		mainVPanel.add(errorPanel);
		mainVPanel.add(listBoxPanel);
		
	}
	
	private class SessionOpenCallback implements AsyncCallback<SessionInfo> {

		@Override
		public void onFailure(Throwable caught) {
			setErrorLabel("Error opening session: " + caught.getMessage());
		}

		@Override
		public void onSuccess(SessionInfo result) {
			
			Boolean sessionExist = result.isSession();
			if(sessionExist) {
				currentSession = result.getSessionID();
				scriptList = result.getScriptList();
				initOpenedSession();
			} else {
				setErrorLabel(result.getErrorMessage());
			}
		}
		
	}
	
	private class SessionCloseCallback implements AsyncCallback<Boolean> {

		@Override
		public void onFailure(Throwable caught) {
			setErrorLabel("Error closing session: Can't connect to Emulation Server!");
		}

		@Override
		public void onSuccess(Boolean result) {
			if(result) {
				deviceFetcher.cancel();
				currentSession = "";
				currentStep = "";
				selectedScript = "";
				connectedDevices.clear();
				virtualDevices = null;
				btnStartEmulation.setEnabled(false);
				neededDevices = 0;
				initClosedSession();
			}
		}
		
	}
	
	private class GetDeviceCallback implements AsyncCallback<List<String>> {
		@Override
		public void onFailure(Throwable caught) {
			setErrorLabel("Error getting device list!");
		}

		@Override
		public void onSuccess(List<String> deviceListReturn) {
			if(deviceListReturn != null) {
				connectedDevices = deviceListReturn;
				updateDevices();
				//deviceListDataProvider.setList(devices);
				//deviceList.redraw();
			} else {
				deviceFetcher.cancel();
				String id = currentSession;
				setErrorLabel("Session with ID " + id + " was closed!");
				initClosedSession();
			}
		}
	}
	
	private void setErrorLabel(String error) {
		
		lblError.setText(error);
		//errorPanel.setVisible(true);
		
	}
	
	// creates the panel for device count
	private VerticalPanel getSessionPanel() {
		
		VerticalPanel sessionPanel = new VerticalPanel();
		
		HorizontalPanel connectedPanel = new HorizontalPanel();
		HorizontalPanel neededPanel = new HorizontalPanel();
		HorizontalPanel startPanel = new HorizontalPanel();
		
		connectedPanel.add(new Label("Verbundene Clients: "));
		connectedPanel.add(lblConnectedDevices);
		
		neededPanel.add(new Label("Clients im Skript: "));
		neededPanel.add(lblNeededDevices);
		
		/*Integer deviceCount = devices.size();
		connectedDevices = new Label("Verbundene Clients: " + deviceCount.toString());
		neededLabel = new Label("F�r die Ausf�hrung des Skriptes ben�tigte Clients: " + neededDevices);*/
		
		btnStartEmulation = new Button("Next...");
		//btnStartEmulation.setEnabled(false);
		btnStartEmulation.setStyleName("button");
		btnStartEmulation.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				startEmulation1();
			}
		});
		
		startPanel.add(btnStartEmulation);
		
		/*if(connectedDevices.size() < neededDevices) {
			btnStartEmulation.setEnabled(false);
		} else {
			btnStartEmulation.setEnabled(true);
		}*/
		
		updateDevices();
		
		sessionPanel.add(connectedPanel);
		sessionPanel.add(neededPanel);
		sessionPanel.add(startPanel);
		
		return sessionPanel;
	}
	
	// creates the panel for device selection
	private VerticalPanel getSessionPanel2() {
		
		VerticalPanel sessionPanel2 = new VerticalPanel();
		
		HorizontalPanel pHeader = new HorizontalPanel();
		pHeader.setSpacing(2);
		Label header1 = new Label("Skriptvariable");
		Label header2 = new Label("Client");
		header1.setHorizontalAlignment(ALIGN_CENTER);
		header1.setWidth("250px");
		header2.setHorizontalAlignment(ALIGN_CENTER);
		header2.setWidth("200px");
		pHeader.add(header1);
		pHeader.add(header2);
		sessionPanel2.add(pHeader);
		
		final Map<String,ListBox> instanceListBoxes = new HashMap<String,ListBox>();
		final Map<String,InstanceGroupListBoxesList> instanceGroupListBoxes = new HashMap<String,InstanceGroupListBoxesList>();
		
		final Button btnStartEmulation2 = new Button("Start");
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
				
				startEmulation2(instanceSelection,instanceGroupSelection);
			}
		});
		
		// add selection for instances
		for(int i=0;i<virtualDevices.getInstances().size();i++) {
			
			String virtDev = virtualDevices.getInstances().get(i);
			
			// variable in the script
			Label lblVirtual = new Label(virtDev);
			lblVirtual.setHorizontalAlignment(ALIGN_CENTER);
			lblVirtual.setWidth("250px");
			
			// listbox with connected devices
			ListBox lbConnectedDevices = new ListBox(false);
			lbConnectedDevices.setWidth("200px");
			for(String device:connectedDevices) {
				lbConnectedDevices.addItem(device);
			}
			instanceListBoxes.put(virtDev, lbConnectedDevices);
						
			HorizontalPanel devPanel = new HorizontalPanel();
			devPanel.setSpacing(4);
			devPanel.setBorderWidth(1);
			
			devPanel.add(lblVirtual);
			devPanel.add(lbConnectedDevices);
			
			sessionPanel2.add(devPanel);
		}
		
		// add selection for instance groups
		Tree groupTree = new Tree();
		for(Map.Entry<String, InstanceGroupInfo> entry:virtualDevices.getInstanceGroups().entrySet()) {
			
			final List<ListBox> groupListBoxesSolo = new ArrayList<ListBox>();
			
			String virtDevGroupName = entry.getKey();
			InstanceGroupInfo instanceGroup = entry.getValue();
			Integer virtDevCount = instanceGroup.getCount();
			
			HorizontalPanel groupRootPanel = new HorizontalPanel();
			Label lblGroupRoot = new Label(virtDevGroupName);
			lblGroupRoot.setHorizontalAlignment(ALIGN_CENTER);
			lblGroupRoot.setWidth("230px");
			ListBox lbGroupRoot = new ListBox(false);
			lbGroupRoot.setWidth("200px");
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
			for(int i = firstInstanceId; i < virtDevCount + firstInstanceId + 1; i++) {
				HorizontalPanel groupItemPanel = new HorizontalPanel();
				Label lblGroupItem = new Label(virtDevGroupName + "/" + i);
				lblGroupItem.setWidth("250px");
				ListBox lbGroupItem = new ListBox(false);
				lbGroupItem.setWidth("200px");
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
			
			sessionPanel2.add(devGroupPanel);
			
		}
		
		//sessionPanel2.add(groupTree);
		sessionPanel2.add(btnStartEmulation2);
		
		return sessionPanel2;
	}
	
	// device selection
	private void startEmulation1() {
		
		//deviceFetcher.cancel();
		this.clear();
		mainScrollPanel.clear();
		mainVPanel.clear();
		
		this.currentStep = "STEP2";
		
		lblError.setText("");
		
		buttonPanel = new HorizontalPanel();
		btnCloseSession = new Button("close session");
		
		btnCloseSession.setStyleName("button");
		btnCloseSession.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				closeSession();
			}
		});
		
		buttonPanel.add(btnCloseSession);
		
		VerticalPanel sessionPanel = getSessionPanel2();
		this.add(mainScrollPanel);
		mainScrollPanel.add(mainVPanel);
		mainVPanel.add(buttonPanel);
		mainVPanel.add(errorPanel);
		mainVPanel.add(sessionPanel);
		
	}
	
	// execute emulation
	private void startEmulation2(Map<String, String> instanceSelection, Map<String, InstanceGroupExecutorInfo> instanceGroupSelection) {
		
		this.clear();
		mainScrollPanel.clear();
		mainVPanel.clear();
		
		this.currentStep = "STEP3";
		
		HorizontalPanel statusPanel = new HorizontalPanel();
		final Label lblStatus = new Label("Executing...");
		statusPanel.add(lblStatus);
		
		emuServerConnectSvc.startScript(currentSession, selectedScript, instanceSelection, instanceGroupSelection, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				setErrorLabel("Skriptausführung konnte nicht gestartet werden: " + caught.getStackTrace().toString());
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
	
	private void updateDevices() {
		
		Integer deviceCount = connectedDevices.size();
		lblConnectedDevices.setText(deviceCount.toString());
		lblNeededDevices.setText(neededDevices.toString());
		
		/*if(neededDevices > 0) {
			if(deviceCount < neededDevices) {
				btnStartEmulation.setEnabled(false);
			} else {
				btnStartEmulation.setEnabled(true);
			}
		}*/
		
	}

}
