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

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTEmulationServer implements EntryPoint {
	
	private static final int REFRESH_INTERVAL = 2000;
	
	// UI-Stuff
	private RootPanel rootPanel = RootPanel.get();
	private FlowPanel mainPanel = new FlowPanel();
	private FlowPanel titlePanel = new FlowPanel();
	private FlowPanel connectStatusPanel = new FlowPanel();
	private FlowPanel connectPanel = new FlowPanel();
	private FlowPanel commandPanel = new FlowPanel();
	private FlowPanel commandStatusPanel = new FlowPanel();
	private FlowPanel devicePanel = new FlowPanel();
	private FlowPanel footerPanel = new FlowPanel();
	
	private Label title = new Label("Emulation Server Webfrontend");
	
	private Label statusLabel = new Label();
	private Button connectButton = new Button("Connect...");
	private Button disconnectButton = new Button("Disconnect...");
	
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
	
	public void onModuleLoad() {
		
		updateLabel();
		checkServerConnection();
		
		mainPanel.setStyleName("mainPanel");
		titlePanel.setStyleName("outerPanel");
		connectStatusPanel.setStyleName("innerPanel");
		connectPanel.setStyleName("innerPanel");
		commandPanel.setStyleName("innerPanel");
		commandStatusPanel.setStyleName("innerPanel");
		devicePanel.setStyleName("innerPanel");
		footerPanel.setStyleName("outerPanel");
		
		connectButton.setStyleName("buttonStyle");
		disconnectButton.setStyleName("buttonStyle");
		sendButton.setStyleName("buttonStyle");
		
		connectButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onConnectClick();
			}
		});
		disconnectButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onDisconnectClick();
			}
		});
		sendButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSendClick();
			}
		});
				
		statusLabel.setText("NOT CONNECTED");
		
		// create cellList
		deviceListDataProvider.addDataDisplay(deviceList);
		//deviceListDataProvider.getList().add("TEST");
		
		// add widgets to panels
		titlePanel.add(title);
		connectStatusPanel.add(statusLabel);
		connectPanel.add(connectButton);
		connectPanel.add(disconnectButton);
		commandPanel.add(txtCommand);
		commandPanel.add(sendButton);
		commandStatusPanel.add(lblSendStatus);
		devicePanel.add(deviceList);
		
		// add panels to main panel
		mainPanel.add(titlePanel);
		mainPanel.add(connectStatusPanel);
		mainPanel.add(connectPanel);
		mainPanel.add(commandPanel);
		mainPanel.add(commandStatusPanel);
		mainPanel.add(devicePanel);
		mainPanel.add(footerPanel);
		
		rootPanel.add(mainPanel);
		
	    refreshTimer = new Timer() {
	      @Override
	      public void run() {
	        getDeviceList();
	      }
	    };
		
	}
	
	private void checkServerConnection() {

	    // Make the call to the connection service.
	    emuServerConnectSvc.isConnected(new ConnectionCallback());
		
	}
	
	private void onConnectClick() {

	    // Make the call to the connection service.
	    emuServerConnectSvc.connectServer(new ConnectionCallback());
	    refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
		
	}
	
	private void onDisconnectClick() {

	    // Make the call to the connection service.
	    emuServerConnectSvc.disconnectServer(new ConnectionCallback());
	    refreshTimer.cancel();
		
	}
	
	private void onSendClick() {
		
		if(!txtCommand.getText().isEmpty()) {
			lastCommand = txtCommand.getText();
			emuServerConnectSvc.sendCommand(lastCommand, new SendCommandCallback());
		} else {
			lblSendStatus.setText("Please enter Command!");
		}
		
	}
	
	private void updateLabel() {
		if(connectionStatus) {
			statusLabel.setText("CONNECTED");
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(true);
			txtCommand.setText("");
			txtCommand.setVisible(true);
			sendButton.setVisible(true);
			lblSendStatus.setVisible(true);
			lblIncommingCommand.setVisible(true);
			deviceList.setVisible(true);
		} else {
			statusLabel.setText("NOT CONNECTED");
			connectButton.setEnabled(true);
			disconnectButton.setEnabled(false);
			txtCommand.setVisible(false);
			sendButton.setVisible(false);
			lblSendStatus.setVisible(false);
			lblIncommingCommand.setVisible(false);
			lblSendStatus.setText("");
			lblIncommingCommand.setText("");
			devices.clear();
			deviceListDataProvider.setList(devices);
			deviceList.setVisible(false);
		}
	}
	
	private void errorLabel() {
		
		statusLabel.setText("ERROR");
		
	}
	
	private void getDeviceList() {
		
		emuServerConnectSvc.getDeviceList(new GetDeviceCallback());
		
	}
	
	private class ConnectionCallback implements AsyncCallback<Boolean> {
		@Override
		public void onFailure(Throwable caught) {
			errorLabel();
		}

		@Override
		public void onSuccess(Boolean result) {
			connectionStatus = result;
			updateLabel();
		}
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
			
		}

		@Override
		public void onSuccess(List<String> deviceListReturn) {
			devices = deviceListReturn;
			deviceListDataProvider.setList(devices);
			deviceList.redraw();
		}
	}
	
}
