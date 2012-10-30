package de.tudresden.inf.rn.mobilis.gwtemulationserver.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTEmulationServer implements EntryPoint {
	
	private static final int REFRESH_INTERVAL = 2000;
	
	private Button connectButton = new Button("Connect...");
	private Button disconnectButton = new Button("Disconnect...");
	private Button sendButton = new Button("Send Command");
	private Label lblCommand = new Label("Command: ");
	private Label lblSendStatus = new Label();
	private Label lblIncommingCommand = new Label();
	private TextBox txtCommand = new TextBox();
	private FlowPanel mainPanel = new FlowPanel();
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	private HorizontalPanel commandPanel = new HorizontalPanel();
	private Label statusLabel = new Label();
	private EmuServerConnectServiceAsync emuServerConnectSvc = GWT.create(EmuServerConnectService.class);
	private Boolean connectionStatus = false;
	private String lastCommand = "";
	private Timer refreshTimer;
	
	public void onModuleLoad() {
		
		updateLabel();
		checkServerConnection();
		
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
		connectButton.setWidth("150px");
		disconnectButton.setWidth("150px");
		sendButton.setWidth("150px");
		txtCommand.setWidth("300px");
		
		mainPanel.setStyleName("center");
		buttonPanel.setStyleName("centerButtons");
		commandPanel.setStyleName("centerCommand");
		
		statusLabel.setText("NOT CONNECTED");
		buttonPanel.add(connectButton);
		buttonPanel.add(disconnectButton);
		
		commandPanel.add(txtCommand);
		commandPanel.add(sendButton);
		
		mainPanel.add(statusLabel);
		mainPanel.add(buttonPanel);
		mainPanel.add(commandPanel);
		mainPanel.add(lblSendStatus);
		mainPanel.add(lblIncommingCommand);
		RootPanel.get("ui").add(mainPanel);
		
	    refreshTimer = new Timer() {
	      @Override
	      public void run() {
	        incommingCommand();
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
		}
	}
	
	private void errorLabel() {
		
		statusLabel.setText("ERROR");
		
	}
	
	private void incommingCommand() {
		
		emuServerConnectSvc.incommingCommand(new IncommingCommandCallback());
		
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
	
	private class IncommingCommandCallback implements AsyncCallback<String> {
		@Override
		public void onFailure(Throwable caught) {
			
		}

		@Override
		public void onSuccess(String result) {
			lblIncommingCommand.setText(result);
		}
	}
	
}
