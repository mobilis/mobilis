package de.tudresden.inf.rn.mobilis.gwtemulationserver.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTEmulationServer implements EntryPoint {
	
	// Service
	private EmuServerConnectServiceAsync emuServerConnectSvc = GWT.create(EmuServerConnectService.class);
	
	private HorizontalPanel content;
	
	public void onModuleLoad() {
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
		logButton.addClickHandler(new LogClickHandler());
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
			content.clear();
			ContentScripts contScr = new ContentScripts(emuServerConnectSvc);			
			content.add(contScr);
		}
	}
	
	private class EmulationClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			content.clear();
			ContentEmulation contEmu = new ContentEmulation(emuServerConnectSvc);
			content.add(contEmu);
		}
	}
	
	private class LogClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			content.clear();
			ContentLogs contLogs = new ContentLogs(emuServerConnectSvc);
			content.add(contLogs);
		}
	}
	
}
