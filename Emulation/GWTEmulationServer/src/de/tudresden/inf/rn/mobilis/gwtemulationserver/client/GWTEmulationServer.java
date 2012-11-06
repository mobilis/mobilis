package de.tudresden.inf.rn.mobilis.gwtemulationserver.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.SessionInfo;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTEmulationServer implements EntryPoint {
	
	private static final int REFRESH_INTERVAL = 2000;
	private static final String SESSION_LABEL = "Enter Session-ID to open existing Emulation Session or leave blank to create a new Session";
	
	// UI-Stuff
	private RootPanel rootPanel = RootPanel.get();
	private FlowPanel mainPanel = new FlowPanel();
	private FlowPanel titlePanel = new FlowPanel();
	private FlowPanel sessionInfoPanel = new FlowPanel();
	private FlowPanel sessionIDInputPanel = new FlowPanel();
	private FlowPanel sessionStartPanel = new FlowPanel();
	private FlowPanel commandPanel = new FlowPanel();
	private FlowPanel commandStatusPanel = new FlowPanel();
	private FlowPanel devicePanel = new FlowPanel();
	private FlowPanel footerPanel = new FlowPanel();
	private FlowPanel separator = new FlowPanel();
	private FlowPanel errorPanel = new FlowPanel();
	
	private Label title = new Label("Emulation Server Webfrontend");
	private Label errorLabel = new Label();
	
	private Label sessionLabel = new Label();
	private TextBox sessionIDTextBox = new TextBox();
	private Button sessionOpenButton = new Button("Open Emulation Session");
	private Button sessionCloseButton = new Button("Close Emulation Session");
	
	private TextBox txtCommand = new TextBox();
	private Button sendButton = new Button("Send Command");
	
	private Label lblSendStatus = new Label();
	
	private Label lblIncommingCommand = new Label();
	
	private CellList<String> deviceList = new CellList<String>(new TextCell());
	private ListDataProvider<String> deviceListDataProvider = new ListDataProvider<String>();
	
	// Service
	private EmuServerConnectServiceAsync emuServerConnectSvc = GWT.create(EmuServerConnectService.class);
	
	// 
	private String lastCommand;
	private Boolean connectionStatus = false;
	private Timer refreshTimer;
	private List<String> devices = new ArrayList<String>();
	private String currentSessionID = "";
	
	public void onModuleLoad() {
		
	    refreshTimer = new Timer() {
	      @Override
	      public void run() {
	        getDeviceList();
	      }
	    };
	    
	    initLayout();
		
	}
	
	private void onSessionOpenClick() {

		String inputID = sessionIDTextBox.getText();
		emuServerConnectSvc.openSession(inputID, new SessionOpenCallback());
		
	}
	
	private void onSessionCloseClick() {

		refreshTimer.cancel();
		emuServerConnectSvc.closeSession(currentSessionID, new SessionCloseCallback());
		
	}
	
	private void onSendClick() {
		
		if(!txtCommand.getText().isEmpty()) {
			lastCommand = txtCommand.getText();
			emuServerConnectSvc.sendCommand(lastCommand, new SendCommandCallback());
		} else {
			lblSendStatus.setText("Please enter Command!");
		}
		
	}
	
	private void setErrorLabel(String error) {
		
		errorLabel.setText(error);
		errorPanel.setVisible(true);
		
	}
	
	private void getDeviceList() {
		
		emuServerConnectSvc.getDeviceList(currentSessionID, new GetDeviceCallback());
		
	}
	
	private class SendCommandCallback implements AsyncCallback<Boolean> {
		@Override
		public void onFailure(Throwable caught) {
			lblSendStatus.setText("Error sending command: '" + lastCommand + "'!");
		}

		@Override
		public void onSuccess(Boolean result) {
			lblSendStatus.setText("Command: '" + lastCommand + "' was sent!");
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
				devices = deviceListReturn;
				deviceListDataProvider.setList(devices);
				deviceList.redraw();
			} else {
				refreshTimer.cancel();
				String id = currentSessionID;
				initClosed();
				setErrorLabel("Session with ID " + id + " was closed!");
			}
		}
	}
	
	private class SessionOpenCallback implements AsyncCallback<SessionInfo> {

		@Override
		public void onFailure(Throwable caught) {
			setErrorLabel("Error opening session: Can't connect to Emulation Server!");
		}

		@Override
		public void onSuccess(SessionInfo result) {
			currentSessionID = result.getSessionID();
			connectionStatus = result.getConnected();
			if(connectionStatus) {
				refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
				initOpened();
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
				initClosed();
			}
		}
		
	}
	
	private void initLayout() {
		
		mainPanel.setStyleName("mainPanel");
		titlePanel.setStyleName("outerPanel");
		sessionInfoPanel.setStyleName("innerPanel");
		sessionIDInputPanel.setStyleName("innerPanel");
		sessionStartPanel.setStyleName("innerPanel");
		commandPanel.setStyleName("innerPanel");
		commandStatusPanel.setStyleName("innerPanel");
		devicePanel.setStyleName("innerPanel");
		footerPanel.setStyleName("outerPanel");
		separator.setStyleName("separator");
		errorPanel.setStyleName("error");
		
		sessionOpenButton.setStyleName("buttonStyle");
		sessionCloseButton.setStyleName("buttonStyle");
		sendButton.setStyleName("buttonStyle");
		
		sessionOpenButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onSessionOpenClick();
			}
		});
		sessionCloseButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onSessionCloseClick();
			}
		});
		sendButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSendClick();
			}
		});
		
		// create cellList
		deviceListDataProvider.addDataDisplay(deviceList);
		//deviceListDataProvider.getList().add("TEST");
		
		// add widgets to panels
		titlePanel.add(title);
		sessionInfoPanel.add(sessionLabel);
		sessionIDInputPanel.add(sessionIDTextBox);
		sessionStartPanel.add(sessionOpenButton);
		sessionStartPanel.add(sessionCloseButton);
		commandPanel.add(txtCommand);
		commandPanel.add(sendButton);
		commandStatusPanel.add(lblSendStatus);
		devicePanel.add(deviceList);
		errorPanel.add(errorLabel);
		
		// add panels to main panel
		mainPanel.add(titlePanel);
		mainPanel.add(sessionInfoPanel);
		mainPanel.add(sessionIDInputPanel);
		mainPanel.add(sessionStartPanel);
		mainPanel.add(errorPanel);
		mainPanel.add(separator);
		mainPanel.add(commandPanel);
		mainPanel.add(commandStatusPanel);
		mainPanel.add(devicePanel);
		mainPanel.add(footerPanel);
		
		rootPanel.add(mainPanel);
		
		initClosed();
		
	}
	
	private void initOpened() {
		
		sessionIDTextBox.setEnabled(false);
		sessionOpenButton.setEnabled(false);
		sessionCloseButton.setEnabled(true);
		commandPanel.setVisible(true);
		commandStatusPanel.setVisible(true);
		devicePanel.setVisible(true);
		separator.setVisible(true);
		errorPanel.setVisible(false);
		
		sessionLabel.setText("Session with ID " + currentSessionID + " open");
		
	}
	
	private void initClosed() {
		
		currentSessionID = "";
		
		devices.clear();
		deviceListDataProvider.setList(devices);
		
		sessionIDTextBox.setEnabled(true);
		sessionOpenButton.setEnabled(true);
		sessionCloseButton.setEnabled(false);
		commandPanel.setVisible(false);
		commandStatusPanel.setVisible(false);
		devicePanel.setVisible(false);
		separator.setVisible(false);
		errorPanel.setVisible(false);
		
		sessionLabel.setText(SESSION_LABEL);
		
	}
	
}
