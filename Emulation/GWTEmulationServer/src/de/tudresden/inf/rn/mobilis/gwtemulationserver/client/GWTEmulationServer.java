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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.SessionInfo;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTEmulationServer implements EntryPoint {
	
	// Service
	private EmuServerConnectServiceAsync emuServerConnectSvc = GWT.create(EmuServerConnectService.class);
	
	private HorizontalPanel content;
	
	public void onModuleLoad() {
		
	    /*refreshTimer = new Timer() {
	      @Override
	      public void run() {
	        getDeviceList();
	      }
	    };
	    
	    initLayout();*/
		
		VerticalPanel main = new VerticalPanel();
		main.addStyleName("main");
		
		HorizontalPanel title = new HorizontalPanel();
		Image logo = new Image("img/logo.png");
		Label titleLabel = new Label("Mobilis Emulation");
		titleLabel.addStyleName("titleLabel");
		title.add(logo);
		title.add(titleLabel);
		title.addStyleName("inner");
		
		HorizontalPanel menu = new HorizontalPanel();
		Button scriptButton = new Button("Skripte");
		Button emuButton = new Button("Emulation");
		Button logButton = new Button("Logs");
		scriptButton.addStyleName("button");
		scriptButton.addClickHandler(new ScriptClickHandler());
		emuButton.addStyleName("button");
		emuButton.addClickHandler(new EmulationClickHandler());
		logButton.addStyleName("button");
		menu.add(scriptButton);
		menu.add(emuButton);
		menu.add(logButton);
		menu.addStyleName("inner");
		
		content = new HorizontalPanel();
		content.addStyleName("inner");
		
		main.add(title);
		main.add(menu);
		main.add(content);
		
		RootLayoutPanel.get().add(main);
		
	}
	
	private class ScriptClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			// TODO Auto-generated method stub
			content.clear();
			
			ContentScripts contScr = new ContentScripts(emuServerConnectSvc);			
			content.add(contScr);
			
		}
		
	}
	
	private class EmulationClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			// TODO Auto-generated method stub
			content.clear();
			
			ContentEmulation contEmu = new ContentEmulation(emuServerConnectSvc);
			content.add(contEmu);
			
		}
		
	}
	
	/*private void onSessionOpenClick() {

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
			emuServerConnectSvc.sendCommand(currentSessionID, new SendCommandCallback());
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
			lblSendStatus.setText("Error sending command: '" + lastCommand + "'! : " + caught.getMessage());
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
		
	}*/
	
}
