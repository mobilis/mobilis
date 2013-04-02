package de.tudresden.inf.rn.mobilis.gwtemulationserver.client;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.SessionList;

public class ContentLogs extends VerticalPanel {
	
	private SessionList sessions;
	private VerticalPanel logPanel;
	private ScrollPanel scrollPanel;
	
	public ContentLogs(EmuServerConnectServiceAsync emuServerConnectSvc) {
		
		super();
		
		HorizontalPanel errorPanel = new HorizontalPanel();
		final Label lblError = new Label("");
		lblError.setStyleName("errorlabel");
		errorPanel.add(lblError);
		
		logPanel = new VerticalPanel();
		logPanel.add(getRow("ID", "Start Time", "End Time", "Script"));
		
		scrollPanel = new ScrollPanel(logPanel);
		this.add(errorPanel);
		this.add(scrollPanel);
		
		emuServerConnectSvc.getSessionList(new AsyncCallback<SessionList>() {
			@Override
			public void onSuccess(SessionList result) {
				sessions = result;
				initLogs();
			}
			@Override
			public void onFailure(Throwable caught) {
				lblError.setText("Fehler beim Abruf der Logs");
			}
		});
	}
	
	@Override
	protected void onLoad() {
		scrollPanel.setSize(Window.getClientWidth() + "px", (Window.getClientHeight() - this.getAbsoluteTop() - 50) + "px");
		super.onLoad();
	}
	
	private void initLogs() {
				
		for(int i=0;i<sessions.getId().size();i++) {
			Date startDate = new Date(sessions.getStartTime().get(i));
			Date endDate = new Date(sessions.getEndTime().get(i));
			
			logPanel.add(getRow(sessions.getId().get(i),
					startDate,
					endDate,
					sessions.getScript().get(i),
					true));
		}
		
	}
	
	private HorizontalPanel getRow(String id, String start, String end, String script) {
		
		HorizontalPanel hp = new HorizontalPanel();
		Label l1 = new Label(id);
		Label l2 = new Label(start);
		Label l3 = new Label(end);
		Label l4 = new Label(script);
		l1.setWidth("100px");
		l2.setWidth("250px");
		l3.setWidth("250px");
		l4.setWidth("200px");
		l1.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		l2.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		l3.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		l4.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		hp.add(l1);
		hp.add(l2);
		hp.add(l3);
		hp.add(l4);
		
		return hp;
		
	}
	
	private FocusPanel getRow(final Long id, Date start, Date end, String script, Boolean mouseOver) {
				
		FocusPanel fp = new FocusPanel();
		final HorizontalPanel hp = getRow(id.toString(), start.toString(), end.toString(), script); 
		fp.add(hp);
		
		if(mouseOver) {
			fp.addMouseOverHandler(new MouseOverHandler() {
				
				@Override
				public void onMouseOver(MouseOverEvent event) {
					hp.setBorderWidth(1);
				}
			});
			fp.addMouseOutHandler(new MouseOutHandler() {
				
				@Override
				public void onMouseOut(MouseOutEvent event) {
					hp.setBorderWidth(0);
				}
			});
			fp.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					int index = sessions.getId().indexOf(id);
					
					PopupPanel popup = new PopupPanel(true);
					popup.setGlassEnabled(true);
					//popup.setSize("400px", "300px");
					popup.setTitle("Details für Emulation " + id.toString());
					popup.setAnimationEnabled(true);
					
					VerticalPanel vp = new VerticalPanel();
					vp.setHorizontalAlignment(ALIGN_CENTER);
					
					HorizontalPanel finishedPanel = new HorizontalPanel();
					Label finishedLabel1 = new Label("Übermittelte Methoden: ");
					Label finishedLabel2 = new Label(sessions.getFinished().get(index).toString());
					
					HorizontalPanel notFinishedPanel = new HorizontalPanel();
					Label notFinishedLabel1 = new Label("Nicht übermittelte Methoden: ");
					Label notFinishedLabel2 = new Label(sessions.getNotFinished().get(index).toString());
					
					HorizontalPanel dirPanel = new HorizontalPanel();
					Label dirLabel1 = new Label("Log-Verzeichnis: ");
					Label dirLabel2 = new Label(sessions.getSessionDir().get(index));
					
					finishedPanel.add(finishedLabel1);
					finishedPanel.add(finishedLabel2);
					notFinishedPanel.add(notFinishedLabel1);
					notFinishedPanel.add(notFinishedLabel2);
					dirPanel.add(dirLabel1);
					dirPanel.add(dirLabel2);
					
					vp.add(finishedPanel);
					vp.add(notFinishedPanel);
					vp.add(dirPanel);
					
					popup.setWidget(vp);
					popup.center();
				}
			});
		}
		
		return fp;
		
	}

}
