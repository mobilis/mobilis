package de.tudresden.inf.rn.mobilis.android.xhunt.emulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.android.maps.GeoPoint;
import com.jayway.android.robotium.solo.SoloWithMaps;

import de.tudresden.inf.rn.mobilis.android.xhunt.activity.MainActivity;
import de.tudresden.inf.rn.mobilis.android.xhunt.activity.XHuntMapActivity;
import de.tudresden.inf.rn.mobilis.android.xhunt.emulation.beans.CommandRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.emulation.beans.ConnectAck;
import de.tudresden.inf.rn.mobilis.android.xhunt.emulation.beans.ConnectRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.emulation.beans.EmulationProxy;
import de.tudresden.inf.rn.mobilis.android.xhunt.emulation.beans.ExecutionResultAck;
import de.tudresden.inf.rn.mobilis.android.xhunt.emulation.beans.ExecutionResultRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.emulation.beans.IEmulationIncoming;
import de.tudresden.inf.rn.mobilis.android.xhunt.emulation.beans.IEmulationOutgoing;
import de.tudresden.inf.rn.mobilis.android.xhunt.emulation.beans.IXMPPCallback;
import de.tudresden.inf.rn.mobilis.android.xhunt.emulation.beans.LogRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.emulation.beans.StartRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.emulation.beans.StopRequest;
import de.tudresden.inf.rn.mobilis.android.xhunt.model.Station;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IXMPPIQCallback;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPIQ;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import android.app.Instrumentation;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.TableLayout;

public class MainTest extends ActivityInstrumentationTestCase2<MainActivity> implements MXAListener {

	private static final String TAG = "Mobilis XHunt Emulation - MainTest";

	private SoloWithMaps solo;
	private String userName;
	private String startID;
	private String emuserver;

	private Map<String,Map<String,XMPPBean>> prototypes	= Collections.synchronizedMap(new HashMap<String,Map<String,XMPPBean>>());

	private Boolean started;
	private Boolean registered;
	private Boolean running;
	private Boolean firstRound;
	private BlockingQueue<Command> commands;
	
	private MXAController mxaController;
	private IXMPPService ixmppService;
	private EmulationProxy emuProxy;
	private String fileName;
	
	private List<String> incomingIDs = new ArrayList<String>();

	public MainTest() {
		super(MainActivity.class);
	}

	protected void setUp() throws Exception {

		solo = new SoloWithMaps(getInstrumentation(), getActivity());
		started = true;
		registered = false;
		firstRound = true;
		commands = new LinkedBlockingQueue<Command>();
		mxaController = MXAController.get();
		emuProxy = new EmulationProxy(new EmulationOutgoing());
		new EmulationIncoming();
		
		fileName = "EmulationLog_" + System.currentTimeMillis() + ".txt";

		userName = "";
		startID = "";

		try {

			Instrumentation i = this.getInstrumentation();
			startID = ((XHuntTestStarter)i).getStartID();
			emuserver = ((XHuntTestStarter)i).getHost();
			
			Log.d(TAG, "setUp startID: " + startID);
			Log.d(TAG, "setUp HOST: " + emuserver);

		} catch(Exception e) {

			Log.d(TAG, "can't get settings");

		}

		Log.d(TAG, "XHunt-Test started");
	}

	protected void tearDown() throws Exception {
		Log.d(TAG, "Emulation tearDown()");
		if(registered) unregisterCallbacks();
		solo.finishOpenedActivities();
	}

	private void clickOnIcon(final String icon) {
		// click on player icon
		Log.d(TAG, "Tap on MapOverlay: " + icon);
		try {
			solo.getCurrentActivity().runOnUiThread(new Runnable(){
				public void run() {
					solo.tapMapMarkerItem(icon, 10000);
				}
			});
		} catch (Exception e) {
			Log.d(TAG, "Tap on MapOverlay: FAILED");
		}
	}

	private ArrayList<Double> getCoords() {
		// get coordinates of player icon
		//String item = solo.getMapMarkerItem(userName+"@"+HOST+"/MXA");
		String item = solo.getMapMarkerItem(userName);
		Log.d(TAG, "Marker item: " + item);
		ArrayList<Double> coords = new ArrayList<Double>();

		try {
			JSONObject json = new JSONObject(item);
			coords.add((Double) json.get("latitude"));
			coords.add((Double) json.get("longitude"));
		} catch (Exception e) {
			Log.d(TAG, "JSON parse: FAILED");
		}
		return coords;
	}

	private void centerMap() {
		try {
			ArrayList<Double> co = getCoords();
			solo.setMapCenter(co.get(0), co.get(1));
		} catch (Exception e) {
			Log.d(TAG, "Center: FAILED");
		}
	}

	public void testClicks() {
		
		if(startID.isEmpty()) {
			
			Log.e(TAG, "ID des Start-Commands nicht übermittelt");
			
			
		} else {
			
			//Skriptausführung
			
			running = true;
			while(running) {

				if(started) {
					
					//einmalige Ausführung bei Start
					
					//Verbindung mit MXA herstellen
					if(solo.waitForActivity("MainActivity")) {
						//Log.d(TAG, "MainActivity");
						
						mxaController.connectMXA(solo.getCurrentActivity().getApplicationContext(), this);
						
						started = false;
						
						Log.d(TAG, "Connect Part finished");
						
					}

				} else {
					
					Log.d(TAG, "wait for command");
					
					//Ausführung der Befehle

					//Log.d(TAG, "execute command if present");
					
					try {
						Command cmd = commands.take();
						String logMethod = "";
						String logParam = "";
						
						if(cmd != null) {
							if(cmd.methodName.equals("clickOnButton")) {
								Log.d(TAG, "execute method: clickOnButton " + cmd.parameters[0]);
								solo.clickOnButton(cmd.parameters[0]);
								solo.sleep(1000);
								logMethod = cmd.methodName;
								logParam = cmd.parameters[0];
								
							} else if(cmd.methodName.equals("clickOnText")) {
								Log.d(TAG, "execute method: clickOnText");
								solo.clickOnText(cmd.parameters[0]);
								solo.sleep(500);
								logMethod = cmd.methodName;
								logParam = cmd.parameters[0];
								
							} else if(cmd.methodName.equals("clickOnMenuItem")) {
								Log.d(TAG, "execute method: clickOnMenuItem " + cmd.parameters[0]);
								solo.clickOnMenuItem(cmd.parameters[0]);
								solo.sleep(500);
								logMethod = cmd.methodName;
								logParam = cmd.parameters[0];
								
							} else if(cmd.methodName.equals("waitForActivity")) {
								Log.d(TAG, "execute method: waitForActivity " + cmd.parameters[0]);
								solo.waitForActivity(cmd.parameters[0]);
								//solo.sleep(1000);
								logMethod = cmd.methodName;
								logParam = cmd.parameters[0];
								
							} else if(cmd.methodName.equals("waitForPlayers")) {
								Integer numPlayers = 0;
								for(int i=0;i<cmd.parameters.length;i++) {
									Class<?> paramType = Class.forName(cmd.parameterTypes[i]);
						    		if(paramType.equals(Integer.class)) {
						    			numPlayers = Integer.parseInt(cmd.parameters[i]);
						    		}
						    	}
								if(numPlayers>0) {
									TableLayout lobbyTable = (TableLayout) solo.getCurrentActivity().findViewById(de.tudresden.inf.rn.mobilis.android.xhunt.R.id.tbl_lobby);
									while(!(lobbyTable.getChildCount() == numPlayers)) {
										Log.d(TAG, "Lobby Table Count: " + lobbyTable.getChildCount());
										solo.sleep(2000);
									}
									Log.d(TAG, "Lobby Table Count: " + lobbyTable.getChildCount() + " -> all players are here!");
									solo.sleep(500);
								}
								logMethod = cmd.methodName;
								logParam = numPlayers.toString();
								
							} else if(cmd.methodName.equals("enterText")) {
								Log.d(TAG, "execute method: enterText " + cmd.parameters[0]);
								solo.clearEditText(0);
								solo.enterText(0, cmd.parameters[0]);
								logMethod = cmd.methodName;
								logParam = cmd.parameters[0];
								
							} else if(cmd.methodName.equals("centerMap")) {
								Log.d(TAG, "execute method: centerMap");
								this.centerMap();
								solo.sleep(1000);
								logMethod = cmd.methodName;
								
							} else if(cmd.methodName.equals("setMapZoom")) {
								if(firstRound) {
									Log.d(TAG, "execute method: setMapZoom");
									Integer zoom = 0;
									for(int i=0;i<cmd.parameters.length;i++) {
										Class<?> paramType = Class.forName(cmd.parameterTypes[i]);
							    		if(paramType.equals(Integer.class)) {
							    			zoom = Integer.parseInt(cmd.parameters[i]);
							    		}
							    	}
									solo.setMapZoom(solo.getMapZoom() + zoom);
									solo.sleep(500);
									firstRound = false;
									logMethod = cmd.methodName;
									logParam = zoom.toString();
								}
								
							} else if(cmd.methodName.equals("selectNextStation")) {
								Log.d(TAG, "execute method: selectNextStation");
								// find next station
								XHuntMapActivity map = (XHuntMapActivity) solo.getCurrentActivity();
								ArrayList<Double> co = getCoords();
								Station stat = map.getGame().getRouteManagement().getStationByLocation(new GeoPoint((int)(co.get(0) * 1E6), (int)(co.get(1) * 1E6)));
								ArrayList<Station> statList = map.getGame().getRouteManagement().getStationsAsList();

								Log.d(TAG, "Current Station: "+stat.getName());

								ArrayList<Station> reachable = new ArrayList<Station>();
								for(Station s:statList) {
									if(s.isReachableFromCurrentStation()) {
										reachable.add(s);
									}
								}
								int count = reachable.size();
								Random r = new Random();
								int id = r.nextInt(count - 1);
								Station nextStation = reachable.get(id);
								String title = nextStation.getName();

								Log.d(TAG, "Next Station: "+title);
								Log.d(TAG, "Click on station: "+title);
								clickOnIcon(title);
								solo.sleep(500);
								logMethod = cmd.methodName;
								
							} else if(cmd.methodName.equals("waitForText")) {
								Log.d(TAG, "execute method: waitForText >" + cmd.parameters[0] + "<");
								/*while(!solo.searchText(cmd.parameters[0])) {
									solo.sleep(1000);
								}*/
								Boolean wait = solo.waitForText(cmd.parameters[0],1,30000);
								Log.d(TAG, "wait for text >" + cmd.parameters[0] + "< : " + wait.toString());
								solo.sleep(1000);
								logMethod = cmd.methodName;
								logParam = cmd.parameters[0];
								
							} else if(cmd.methodName.equals("goBack")) {
								Log.d(TAG, "execute method: goBack");
								solo.goBack();
								solo.sleep(1000);
								logMethod = cmd.methodName;
								
							} else {
								Log.d(TAG, "unknown method");
								logMethod = "unknown method";
							}
							
							//write to log
							if(!logMethod.isEmpty()) {
								String toWrite = logMethod;
								if(!logParam.isEmpty()) toWrite += " \"" + logParam + "\"";
								writeToLog(toWrite);
							}
							
							//after execution send Ack
							emuProxy.Command(cmd.from, cmd.methodID);
							
						}
							
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				solo.sleep(1000);

			}
			
		}

	}

	private IXMPPIQCallback beanCallback = new IXMPPIQCallback.Stub() {
		public void processIQ(XMPPIQ iq) throws RemoteException {

			XMPPBean bean = convertXMPPIQToBean(iq);
			if(bean instanceof CommandRequest) {

				CommandRequest cr = (CommandRequest) bean;
				Log.d(TAG, "Incoming Method: " + cr.getMethodName() + ", ID: " + cr.getId());

				if(!incomingIDs.contains(cr.getId())) {
					incomingIDs.add(cr.getId());
					Command cmd = new Command();
					cmd.methodName = cr.getMethodName();
					Object[] parameterArray = cr.getParameters().toArray();
					cmd.parameters = Arrays.copyOf(parameterArray, parameterArray.length, String[].class);
					Object[] parameterTypeArray = cr.getParameterTypes().toArray();
					cmd.parameterTypes = Arrays.copyOf(parameterTypeArray, parameterTypeArray.length, String[].class);
					cmd.async = cr.getAsync();
					
					cmd.methodID = cr.getId();
					cmd.from = cr.getFrom();
					
					try {
						commands.put(cmd);
						Log.d(TAG, "Command added");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//emuProxy.Command(cr.getFrom(), cr.getId());

			}
			if(bean instanceof StopRequest) {
				Log.d(TAG, "StopRequest");
				StopRequest stopRequest = (StopRequest) bean;
				if(!incomingIDs.contains(stopRequest.getId())) {
					incomingIDs.add(stopRequest.getId());
					emuProxy.Stop(stopRequest.getFrom(), stopRequest.getId());
					
					running = false;
					try {
						tearDown();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if(bean instanceof LogRequest) {
				Log.d(TAG, "LogRequest");
				LogRequest logRequest = (LogRequest) bean;
				/*if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					File logFile = new File(getInstrumentation().getContext().getExternalFilesDir(null),
							fileName);
					if(logFile.exists()) {
						Log.d(TAG, "FileTransfer");
						FileTransfer fileTransfer = new FileTransfer();
						fileTransfer.from = logRequest.getTo();
						fileTransfer.to = logRequest.getFrom();
						fileTransfer.path = logFile.getPath();
						ixmppService.getFileTransferService().sendFile(new Messenger(new Handler()), 0, fileTransfer);
					}
				}*/
			}
		}
	};

	private XMPPBean convertXMPPIQToBean(XMPPIQ iq) {

		final Map<String,Map<String,XMPPBean>> prototypes = this.prototypes;

		try {
			String childElement = iq.element;
			String namespace    = iq.namespace;
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(iq.payload));
			XMPPBean bean = null;

			synchronized (prototypes) {
				if ( namespace != null && prototypes.containsKey(namespace)	&& prototypes.get(namespace).containsKey(childElement) ) {
					bean = prototypes.get(namespace).get(childElement).clone();
					bean.fromXML(parser);
					bean.setId(iq.packetID);
					bean.setFrom(iq.from);
					bean.setTo(iq.to);
					switch (iq.type) {
						case XMPPIQ.TYPE_GET: bean.setType(XMPPBean.TYPE_GET); break;
						case XMPPIQ.TYPE_SET: bean.setType(XMPPBean.TYPE_SET); break;
						case XMPPIQ.TYPE_RESULT: bean.setType(XMPPBean.TYPE_RESULT); break;
						case XMPPIQ.TYPE_ERROR: bean.setType(XMPPBean.TYPE_ERROR); break;
					}
					return bean;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private XMPPIQ convertXMPPBeanToIQ(XMPPBean bean, boolean mergePayload) {
		// default XMPP IQ type
		int type = XMPPIQ.TYPE_GET;

		switch (bean.getType()) {
			case XMPPBean.TYPE_GET:    type = XMPPIQ.TYPE_GET; break;
			case XMPPBean.TYPE_SET:    type = XMPPIQ.TYPE_SET; break;
			case XMPPBean.TYPE_RESULT: type = XMPPIQ.TYPE_RESULT; break;
			case XMPPBean.TYPE_ERROR:  type = XMPPIQ.TYPE_ERROR; break;
		}

		XMPPIQ iq;

		if (mergePayload)
			iq = new XMPPIQ( bean.getFrom(), bean.getTo(), type, null, null, bean.toXML() );
		else
			iq = new XMPPIQ( bean.getFrom(), bean.getTo(), type,
					bean.getChildElement(), bean.getNamespace(), bean.payloadToXML() );

		iq.packetID = bean.getId();

		return iq;
	}

	private void registerPrototypes() {
		registerXMPPBean(new CommandRequest());
		registerXMPPBean(new StopRequest());
		registerXMPPBean(new LogRequest());
	}

	private void registerXMPPBean(XMPPBean prototype) {

		String namespace    = prototype.getNamespace();
		String childElement = prototype.getChildElement();

		synchronized (this.prototypes) {

			if(!this.prototypes.keySet().contains(namespace)) {
				this.prototypes.put(namespace, Collections.synchronizedMap( new HashMap<String,XMPPBean>() ));
			}
			this.prototypes.get(namespace).put(childElement, prototype);
		}

	}

	private void unregisterXMPPBean(XMPPBean prototype) {

		String namespace    = prototype.getNamespace();
		String childElement = prototype.getChildElement();

		synchronized (this.prototypes) {
			if(this.prototypes.containsKey(namespace)) {
				this.prototypes.get(namespace).remove(childElement);
				if(this.prototypes.get(namespace).size() > 0) {
					this.prototypes.remove(namespace);
				}
			}
		}
	}

	private XMPPBean getRegisteredBean(String namespace, String element){
		try{
			return this.prototypes.get(namespace).get(element);
		}
		catch(NullPointerException e){
			Log.e(TAG, "Bean with namespace: '" + namespace + "' not found!");
			return null;
		}
	}

	private void registerCallbacks() {
		try {
			if(ixmppService.isConnected()) {
				for ( Map.Entry< String,Map<String,XMPPBean> > entity : this.prototypes.entrySet() ) {

					for ( Map.Entry< String, XMPPBean > subEntity : entity.getValue().entrySet() ) {

						XMPPBean bean = getRegisteredBean(subEntity.getValue().getNamespace(),subEntity.getValue().getChildElement());
						try {
							ixmppService.registerIQCallback(beanCallback, bean.getChildElement(), bean.getNamespace());
						} catch (RemoteException e) {
							Log.e(TAG, "Couldn't register Bean: '" + bean.getNamespace() + "'");
						}

					}

				}
				registered = true;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void unregisterCallbacks() {
		try {
			if(ixmppService.isConnected()) {
				for ( Map.Entry< String,Map<String,XMPPBean> > entity : this.prototypes.entrySet() ) {

					for ( Map.Entry< String, XMPPBean > subEntity : entity.getValue().entrySet() ) {

						XMPPBean bean = getRegisteredBean(subEntity.getValue().getNamespace(),subEntity.getValue().getChildElement());
						try {
							ixmppService.unregisterIQCallback(beanCallback, bean.getChildElement(), bean.getNamespace());
						} catch (RemoteException e) {
							Log.e(TAG, "Couldn't unregister Bean: '" + bean.getNamespace() + "'");
						}

					}

				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setConnected() {
		Log.d(TAG, "set connected");
		//emuProxy.Start(emuserver, startID);
		if (!registered) {
			Log.d(TAG, "register prototypes and callbacks");
			registerPrototypes();
			registerCallbacks();
		}
		try {
			userName = ixmppService.getUsername();
			Log.d(TAG, "userName: " + userName);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private Handler connectHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.d(TAG, "connectHandler");
			if ((msg.what == ConstMXA.MSG_CONNECT) && (msg.arg1 == ConstMXA.MSG_STATUS_SUCCESS)) {
				Log.d(TAG, "XMPP connected");
				setConnected();
			}
		}
	};
	
	private Handler disconnectHandler = new Handler() {
		public void handleMessage(Message msg) {
			if ((msg.what == ConstMXA.MSG_DISCONNECT) && (msg.arg1 == ConstMXA.MSG_STATUS_SUCCESS))
				Log.d(TAG, "XMPP disconnected");
		}
	};
	
	private class EmulationOutgoing implements IEmulationOutgoing {

		public void sendXMPPBean(XMPPBean out, IXMPPCallback<? extends XMPPBean> callback) {
			/*sendCallbacks.put(out.getId(), callback);
			try {
				Log.d(TAG, "send bean with callback");
		        xmppService.sendIQ(null, null, 0, convertXMPPBeanToIQ(out, true));
		    } catch(RemoteException localRemoteException) {
		    	Log.e(TAG, "Can't send IQ");
		    }*/
		}

		public void sendXMPPBean(XMPPBean out) {
			try {
				Log.d(TAG, "send bean: " + out.getNamespace() + " with ID: " + out.getId() + " to: " + out.getTo());
		        ixmppService.sendIQ(null, null, 0, convertXMPPBeanToIQ(out, true));
		    } catch(RemoteException localRemoteException) {
		    	Log.e(TAG, "Can't send IQ");
		    }
		}
		
	}
	
	private class EmulationIncoming implements IEmulationIncoming {

		public XMPPBean onStart(StartRequest in) {
			// TODO Auto-generated method stub
			return null;
		}

		public XMPPBean onStop(StopRequest in) {
			// TODO Auto-generated method stub
			return null;
		}

		public void onConnect(ConnectAck in) {
			// TODO Auto-generated method stub
			
		}

		public void onConnectError(ConnectRequest in) {
			// TODO Auto-generated method stub
			
		}

		public XMPPBean onCommand(CommandRequest in) {
			// TODO Auto-generated method stub
			return null;
		}

		public void onExecutionResult(ExecutionResultAck in) {
			// TODO Auto-generated method stub
			
		}

		public void onExecutionResultError(ExecutionResultRequest in) {
			// TODO Auto-generated method stub
			
		}

		public void onLog(LogRequest in) {
			// TODO Auto-generated method stub
			
		}
		
	}

	public void onMXAConnected() {
		Log.d(TAG, "onMXAConnected");
		ixmppService = mxaController.getXMPPService();
		try {
			if(!ixmppService.isConnected()) {
				Log.d(TAG, "onMXAConnected - isNotConnected");
				ixmppService.connect(new Messenger(connectHandler));
			} else {
				Log.d(TAG, "onMXAConnected - isConnected");
				setConnected();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void onMXADisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	private void writeToLog(String s) {
		
		String toWrite = getTimeString() + ": " + s;
		
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Log.d(TAG, "write to log - external");
			File logFile = new File(getInstrumentation().getContext().getExternalFilesDir(null),
									fileName);
			if(!logFile.exists()) {
				try {
					logFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
				bw.append(toWrite);
				bw.newLine();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getTimeString() {
		String time = "";
		
		Date date = new Date();
		time = date.toGMTString();
		
		return time;
	}
	
}