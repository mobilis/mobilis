package de.tudresden.inf.rn.mobilis.android.emulation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.InstrumentationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.tudresden.inf.rn.mobilis.android.emulation.beans.CommandAck;
import de.tudresden.inf.rn.mobilis.android.emulation.beans.CommandRequest;
import de.tudresden.inf.rn.mobilis.android.emulation.beans.ConnectAck;
import de.tudresden.inf.rn.mobilis.android.emulation.beans.ConnectRequest;
import de.tudresden.inf.rn.mobilis.android.emulation.beans.EmulationProxy;
import de.tudresden.inf.rn.mobilis.android.emulation.beans.ExecutionResultAck;
import de.tudresden.inf.rn.mobilis.android.emulation.beans.ExecutionResultRequest;
import de.tudresden.inf.rn.mobilis.android.emulation.beans.IEmulationIncoming;
import de.tudresden.inf.rn.mobilis.android.emulation.beans.IEmulationOutgoing;
import de.tudresden.inf.rn.mobilis.android.emulation.beans.IXMPPCallback;
import de.tudresden.inf.rn.mobilis.android.emulation.beans.LogRequest;
import de.tudresden.inf.rn.mobilis.android.emulation.beans.StartAck;
import de.tudresden.inf.rn.mobilis.android.emulation.beans.StartRequest;
import de.tudresden.inf.rn.mobilis.android.emulation.beans.StopAck;
import de.tudresden.inf.rn.mobilis.android.emulation.beans.StopRequest;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.callbacks.IXMPPIQCallback;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPIQ;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class EmulationStarterActivity extends Activity implements MXAListener {
	
	private static final String TAG = "Mobilis Emulation Starter";
	private static String EMUSERVER;
	private SharedPreferences sharedPref;
	
	private Button connectButton;
	private LinearLayout incomingLayout;
	private TextView textViewStatus;
	
	private Boolean connected;
	private Boolean registered;
	private Boolean emulationStarted;
	
	private EmulationProxy emuProxy;
	private MXAController mxaController;
	private IXMPPService xmppService;
	private Map<String,Map<String,XMPPBean>> prototypes	= Collections.synchronizedMap(new HashMap<String,Map<String,XMPPBean>>());
	private Map<String, IXMPPCallback<? extends XMPPBean>> sendCallbacks = new HashMap<String, IXMPPCallback<? extends XMPPBean>>();
	
	private List<String> incomingIDs = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		EMUSERVER = sharedPref.getString(getResources().getString(R.string.settings_key_emuserver), null);
		
		this.incomingLayout = (LinearLayout) findViewById(R.id.incomingLayout);
		this.connectButton = (Button) findViewById(R.id.connectButton);
		this.textViewStatus = (TextView) findViewById(R.id.connectionStatus);
		
		this.connected = false;
		this.registered = false;
		this.emulationStarted = false;
		
		this.emuProxy = new EmulationProxy(new EmulationOutgoing());
		new EmulationIncoming();
		this.mxaController = MXAController.get();
		this.mxaController.connectMXA(getApplicationContext(), this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_settings:
			startSettings();
			return true;
		case R.id.menu_clear:
			incomingLayout.removeAllViews();
			TextView tv = createTextView();
			tv.setText(getResources().getString(R.string.txtIncomingTitle));
			addTextView(tv);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void startSettings() {
		Log.d(TAG, "start settings activtiy");
		Intent i = new Intent(this, SettingsActivtiy.class);
		startActivity(i);
	}
	
	private void updateHost() {
		Log.d(TAG, "update host");
		String newHost = sharedPref.getString(getResources().getString(R.string.settings_key_emuserver), null);
		if(newHost != null && !newHost.equals(EMUSERVER)) EMUSERVER = newHost;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		if(!emulationStarted) updateHost();
		try {
			if (connected && !xmppService.isConnected()) {
				xmppService.connect(new Messenger(reconnectHandler));
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		super.onResume();
	}
	
	@Override
	public void finish() {
		if(connected) emuProxy.Disconnect(EMUSERVER);
	    unregisterCallbacks();
	    xmppDisconnect();
		super.finish();
	}

	public void onMXAConnected() {
		Log.d(TAG, "MXA Connected");
	    xmppService = mxaController.getXMPPService();
	}
	
	public void onMXADisconnected() {
		Log.d(TAG, "MXA Disconnected");
	}
	
	private TextView createTextView() {
		TextView tv = new TextView(EmulationStarterActivity.this);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
		layoutParams.setMargins(0, 0, 0, 5);
		tv.setLayoutParams(layoutParams);
		tv.setGravity(1);
		return tv;
	}
	
	private void addTextView(final TextView paramTextView) {
	    runOnUiThread(new Runnable() {
	    	public void run() {
	    		incomingLayout.addView(paramTextView);
	    	}
	    });
	}
	
	private void changeStatus() {
	    if (connected) {
	    	Log.d(TAG, "change to disconnected");
	    	runOnUiThread(new Runnable() {
	    		public void run() {
	    			textViewStatus.setText("not connected");
	    			connectButton.setText("Connect");
	    		}
	    	});
	    	connected = false;
	    } else {
	    	Log.d(TAG, "change to connected");
	    	runOnUiThread(new Runnable() {
	    		public void run() {
	    			textViewStatus.setText("connected");
	    			connectButton.setText("Disconnect");
	    		}
	    	});
	    	connected = true;
	    }
	}
	
	private void emuServerConnect() {
		/*ConnectRequest connectRequest = new ConnectRequest();
	    connectRequest.setTo(EMUSERVER);*/
	    emuProxy.Connect(EMUSERVER, connectCallback);//.getBindingStub().sendXMPPBean(connectRequest, connectCallback);
	}
	
	private void emuServerReconnect() {
		/*ConnectRequest connectRequest = new ConnectRequest();
	    connectRequest.setTo(EMUSERVER);*/
	    emuProxy.Connect(EMUSERVER, reconnectCallback);//.getBindingStub().sendXMPPBean(connectRequest, connectCallback);
	}
	
	private void emuServerDisconnect() {
	    /*DisconnectRequest disconnectRequest = new DisconnectRequest();
	    disconnectRequest.setTo(EMUSERVER);*/
	    emuProxy.Disconnect(EMUSERVER);//.getBindingStub().sendXMPPBean(disconnectRequest);
	    changeStatus();
	}
	
	private void startApp(StartRequest startRequest) {
		
		String nameSpace = startRequest.getAppNamespace();
		List<InstrumentationInfo> iiList = getPackageManager().queryInstrumentation(nameSpace, 0);
		ComponentName testComponent = instrumentationForPosition(iiList, 0);
		
	    if(testComponent != null) {
	    	Bundle bundle = new Bundle();
	    	Log.d(TAG, startRequest.getId());
	    	bundle.putString("startID", startRequest.getId());
	    	bundle.putString("host", EMUSERVER);
	    	emulationStarted = true;
	    	Log.d(TAG, "startInstrumentation");
	    	startInstrumentation(testComponent, null, bundle);
	    } else {
	    	StartAck startAck = new StartAck();
	    	startAck.setId(startRequest.getId());
	    	startAck.setTo(startRequest.getFrom());
	    	startAck.setFrom(startRequest.getTo());
	    	startAck.setType(XMPPBean.TYPE_ERROR);
	    }
	}
	
	private void startApp() {
		List<InstrumentationInfo> iiList = getPackageManager().queryInstrumentation("de.tudresden.inf.rn.mobilis.android.xhunt", 0);
		ComponentName testComponent = instrumentationForPosition(iiList, 0);
		Bundle bundle = new Bundle();
		bundle.putString("startID", "");
    	bundle.putString("host", "");
    	emulationStarted = true;
		startInstrumentation(testComponent, null, bundle);
	}
	
	private void xmppConnect() {
		try {
			if (!xmppService.isConnected()) {
				xmppService.connect(new Messenger(connectHandler));
			} else {
				if (!registered) {
					Log.d(TAG, "register prototypes and callbacks");
					registerPrototypes();
					registerCallbacks();
				}
				emuServerConnect();
			}
		} catch (RemoteException localRemoteException) {
			Log.e(TAG, "Can't connect XMPPService");
		}
	}
	
	private void xmppDisconnect() {
		try {
			if (xmppService.isConnected())
				xmppService.disconnect(new Messenger(disconnectHandler));
		} catch (RemoteException localRemoteException) {
			Log.e(TAG, "Can't disconnect XMPPService");
	    }
	}
	
	public void click(View view) {
		if(connected) {
			Log.d("Mobilis Emulation Starter", "click: disconnect");
			emuServerDisconnect();
		} else {
			Log.d("Mobilis Emulation Starter", "click: connect");
			if(EMUSERVER != null) {
				xmppConnect();
			} else {
				Toast.makeText(this, "Kein EmulationServer Host festgelegt.", Toast.LENGTH_LONG).show();
			}
	    }
	  }
	
	private class EmulationIncoming implements IEmulationIncoming {

		public XMPPBean onStart(StartRequest in) {
			StartAck startAck = new StartAck();
			
			startAck.setId(in.getId());
			startAck.setTo(in.getFrom());
			startAck.setFrom(in.getTo());
			
			return startAck;
		}

		public XMPPBean onStop(StopRequest in) {
			StopAck stopAck = new StopAck();
			
			stopAck.setId(in.getId());
			stopAck.setTo(in.getFrom());
			stopAck.setFrom(in.getTo());
			
			return stopAck;
		}

		// not used
		public void onConnect(ConnectAck in) {
			// TODO Auto-generated method stub
			
		}

		// not used
		public void onConnectError(ConnectRequest in) {
			// TODO Auto-generated method stub
			
		}

		public XMPPBean onCommand(CommandRequest in) {
			CommandAck commAck = new CommandAck();
			
			commAck.setId(in.getId());
			commAck.setTo(in.getFrom());
			commAck.setFrom(in.getTo());
			
			return commAck;
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
	
	private class EmulationOutgoing implements IEmulationOutgoing {

		public void sendXMPPBean(XMPPBean out, IXMPPCallback<? extends XMPPBean> callback) {
			sendCallbacks.put(out.getId(), callback);
			try {
				Log.d(TAG, "send bean with callback");
		        xmppService.sendIQ(null, null, 0, convertXMPPBeanToIQ(out, true));
		    } catch(RemoteException localRemoteException) {
		    	Log.e(TAG, "Can't send IQ");
		    }
		}

		public void sendXMPPBean(XMPPBean out) {
			try {
				Log.d(TAG, "send bean");
		        xmppService.sendIQ(null, null, 0, EmulationStarterActivity.this.convertXMPPBeanToIQ(out, true));
		    } catch(RemoteException localRemoteException) {
		    	Log.e(TAG, "Can't send IQ");
		    }
		}
		
	}
	
	private IXMPPIQCallback beanCallback = new IXMPPIQCallback.Stub() {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void processIQ(XMPPIQ iq) throws RemoteException {
			// IQ to Bean
			// createTextViews with incoming Beans
			
			XMPPBean bean = convertXMPPIQToBean(iq);
			
			if(bean instanceof CommandRequest) {
				Log.d(TAG, "CommandRequest");
				CommandRequest commandRequest = (CommandRequest)bean;
				
				if(!incomingIDs.contains(commandRequest.getId())) {
					incomingIDs.add(commandRequest.getId());
					TextView tv = createTextView();
	    			tv.setText("AppCommand: " + commandRequest.getMethodName());
	    			addTextView(tv);
				}
				
				/*if(emulationStarted) {
					imcomingCommands.add("AppCommand: " + commandRequest.getMethodName());
				} else {
					TextView tv = createTextView();
	    			tv.setText("AppCommand: " + commandRequest.getMethodName());
	    			addTextView(tv);
				}*/
    			
    			//XMPPBean commAck = emuInc.onCommand(commandRequest);
    			//emuProxy.Command(commAck.getTo(), commAck.getId());
			}
			if(bean instanceof StartRequest) {
				Log.d(TAG, "StartRequest");
				StartRequest startRequest = (StartRequest)bean;
				
				if(!incomingIDs.contains(startRequest.getId())) {
					incomingIDs.add(startRequest.getId());
					TextView tv = createTextView();
	    			tv.setText("StartCommand: " + startRequest.getAppNamespace());
	    			addTextView(tv);
	    			emuProxy.Start(startRequest.getFrom(), startRequest.getId());
	    			startApp(startRequest);
				}
				
				/*if(emulationStarted) {
					imcomingCommands.add("StartCommand: " + startRequest.getAppNamespace());
				} else {
					TextView tv = createTextView();
	    			tv.setText("StartCommand: " + startRequest.getAppNamespace());
	    			addTextView(tv);
				}*/
    			
    			//XMPPBean startAck = emuInc.onStart(startRequest);
    			//emuProxy.Start(startAck.getTo(), startAck.getId());
			}
			if(bean instanceof StopRequest) {
				Log.d(TAG, "StopRequest");
				StopRequest stopRequest = (StopRequest)bean;
				
				if(!incomingIDs.contains(stopRequest.getId())) {
					incomingIDs.add(stopRequest.getId());
					TextView tv = createTextView();
	    			tv.setText("StopCommand: " + stopRequest.getAppNamespace());
	    			addTextView(tv);
	    			emulationStarted = false;
				}
				
				/*if(emulationStarted) {
					imcomingCommands.add("StopCommand: " + stopRequest.getAppNamespace());
				} else {
					TextView tv = createTextView();
	    			tv.setText("StopCommand: " + stopRequest.getAppNamespace());
	    			addTextView(tv);
				}*/
    			
    			//XMPPBean stopAck = emuInc.onStop(stopRequest);
    			//emuProxy.Stop(stopAck.getTo(), stopAck.getId());
			}
			if(bean instanceof ConnectAck) {
				Log.d(TAG, "ConnectAck");
				TextView tv = createTextView();
				ConnectAck connectAck = (ConnectAck)bean;
    			tv.setText("ConnectAck");
    			addTextView(tv);
    			if(sendCallbacks.containsKey(connectAck.getId())) {
    				IXMPPCallback callback = sendCallbacks.get(connectAck.getId());
    				if(callback != null) {
    					try {
    						callback.invoke(connectAck);
    					} catch(ClassCastException e) {
    						e.printStackTrace();
    					}
    				}
    			}
			}
		}
		
	};
	
	private IXMPPCallback<ConnectAck> connectCallback = new IXMPPCallback<ConnectAck>() {
		public void invoke(ConnectAck xmppBean) {
			Log.d(TAG, "ConnectCallback invoked");
			if(xmppBean.getType() == XMPPBean.TYPE_ERROR) {
				Log.d(TAG, "Connect error");
		        Toast.makeText(EmulationStarterActivity.this, "Error connecting to Emulation Server", Toast.LENGTH_LONG).show();
		    } else {
		    	changeStatus();
		    }
		}
	};
	
	private IXMPPCallback<ConnectAck> reconnectCallback = new IXMPPCallback<ConnectAck>() {
		public void invoke(ConnectAck xmppBean) {
			Log.d(TAG, "ReconnectCallback invoked");
			if(xmppBean.getType() == XMPPBean.TYPE_ERROR) {
				Log.d(TAG, "Reconnect error");
		        Toast.makeText(EmulationStarterActivity.this, "Error reconnecting to Emulation Server", Toast.LENGTH_LONG).show();
		    }
		}
	};
	
	public XMPPBean convertXMPPIQToBean(XMPPIQ iq) {

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

	public XMPPIQ convertXMPPBeanToIQ(XMPPBean bean, boolean mergePayload) {
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
	
	public void registerPrototypes() {
	    registerXMPPBean(new ConnectAck());
	    registerXMPPBean(new CommandRequest());
	    registerXMPPBean(new StartRequest());
	    registerXMPPBean(new StopRequest());
	    registerXMPPBean(new ExecutionResultAck());
	    registerXMPPBean(new LogRequest());
	}
	
	public void registerXMPPBean(XMPPBean prototype) {

		String namespace    = prototype.getNamespace();
		String childElement = prototype.getChildElement();

		synchronized (this.prototypes) {

			if(!this.prototypes.keySet().contains(namespace)) {
				this.prototypes.put(namespace, Collections.synchronizedMap( new HashMap<String,XMPPBean>() ));
			}
			this.prototypes.get(namespace).put(childElement, prototype);
		}

	}

	public void unregisterXMPPBean(XMPPBean prototype) {

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

	public XMPPBean getRegisteredBean(String namespace, String element){
		try{
			return this.prototypes.get(namespace).get(element);
		}
		catch(NullPointerException e){
			Log.e(TAG, "Bean with namespace: '" + namespace + "' not found!");
			return null;
		}
	}

	public void registerCallbacks() {
		try {
			if(xmppService.isConnected()) {
				for ( Map.Entry< String,Map<String,XMPPBean> > entity : this.prototypes.entrySet() ) {

					for ( Map.Entry< String, XMPPBean > subEntity : entity.getValue().entrySet() ) {

						XMPPBean bean = getRegisteredBean(subEntity.getValue().getNamespace(),subEntity.getValue().getChildElement());
						try {
							xmppService.registerIQCallback(beanCallback, bean.getChildElement(), bean.getNamespace());
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

	public void unregisterCallbacks() {
		try {
			if(xmppService.isConnected()) {
				for ( Map.Entry< String,Map<String,XMPPBean> > entity : this.prototypes.entrySet() ) {

					for ( Map.Entry< String, XMPPBean > subEntity : entity.getValue().entrySet() ) {

						XMPPBean bean = getRegisteredBean(subEntity.getValue().getNamespace(),subEntity.getValue().getChildElement());
						try {
							xmppService.unregisterIQCallback(beanCallback, bean.getChildElement(), bean.getNamespace());
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
	
	private Handler connectHandler = new Handler() {
		public void handleMessage(Message msg) {
			if ((msg.what == ConstMXA.MSG_CONNECT) && (msg.arg1 == ConstMXA.MSG_STATUS_SUCCESS)) {
				Log.d(TAG, "XMPP connected");
				if (!registered) {
					Log.d(TAG, "register prototypes and callbacks");
					registerPrototypes();
					registerCallbacks();
				}
				emuServerConnect();
			}
		}
	};
	
	private Handler disconnectHandler = new Handler() {
		public void handleMessage(Message msg) {
			if ((msg.what == ConstMXA.MSG_DISCONNECT) && (msg.arg1 == ConstMXA.MSG_STATUS_SUCCESS))
				Log.d(TAG, "XMPP disconnected");
		}
	};
	
	private Handler reconnectHandler = new Handler() {
		public void handleMessage(Message msg) {
			if ((msg.what == ConstMXA.MSG_CONNECT) && (msg.arg1 == ConstMXA.MSG_STATUS_SUCCESS)) {
				Log.d(TAG, "XMPP reconnected");
				emuServerReconnect();
			}
		}
	};
	
	public ComponentName instrumentationForPosition(List<InstrumentationInfo> iiList, int position) {
        if(iiList == null) {
            return null;
        }
        InstrumentationInfo ii = iiList.get(position);
        return new ComponentName(ii.packageName, ii.name);
    }
}