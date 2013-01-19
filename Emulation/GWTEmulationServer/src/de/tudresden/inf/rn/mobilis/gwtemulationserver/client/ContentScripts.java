package de.tudresden.inf.rn.mobilis.gwtemulationserver.client;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ContentScripts extends VerticalPanel {
	
	private EmuServerConnectServiceAsync emuServerConnectSvc;
	private VerticalPanel scriptList;
	
	public ContentScripts(EmuServerConnectServiceAsync emuServerConnectSvc) {
		
		super();
		
		this.emuServerConnectSvc = emuServerConnectSvc;
		
		initList();
		
	}
	
	private void initList() {
		
		// Create a FormPanel and point it at a service.
		final FormPanel form = new FormPanel();
		form.setAction("/EmulationServer/gwtemulationserver/upload");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		// Create a FileUpload widget.
		final FileUpload upload = new FileUpload();
		upload.setWidth("400px");
		upload.setName("uploadFormElement");
		
		Button submit = new Button("Upload");
		submit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String fileName = upload.getFilename();
				String fileType = fileName.substring(fileName.length()-4, fileName.length());
				if(fileType.equals(".xml")) {
					System.out.println("Submit");
					form.submit();
				} else {
					Window.alert("Bitte Skript im xml-Format uploaden!");
				}
			}
		});
		
		VerticalPanel uploadPanel = new VerticalPanel();
		uploadPanel.add(upload);
		uploadPanel.add(submit);
		
    	form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
    		public void onSubmitComplete(SubmitCompleteEvent event) {
    			getScriptList();
    		}
    	});
		
		form.add(uploadPanel);
		
		HorizontalPanel titlePanel = new HorizontalPanel();
		Label lblTitle = new Label("Liste der Skripte auf dem Server:");
		titlePanel.add(lblTitle);
		
		scriptList = new VerticalPanel();
		
		this.add(form);
		this.add(titlePanel);
		this.add(scriptList);
		
		getScriptList();
		
	}
	
	private void getScriptList() {
		
		scriptList.clear();
		
		emuServerConnectSvc.getScriptList(new AsyncCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> result) {
				if(result.size() > 0) {
					for(String s:result) {
						Label script = new Label(s);
						scriptList.add(script);
					}
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("Error getting scriptList!");
			}
		});
		
	}

}
