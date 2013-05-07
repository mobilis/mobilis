/**
 * Copyright (C) 2009 Technische Universit�t Dresden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contains parts of Android Email App (C) 2008 The Android Open Source Project
 */

/**
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 */

package de.tudresden.inf.rn.mobilis.mxa.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.R;

/**
 * Displays the activity for typing in the XMPP ID and its password.
 * @author Istvan Koren, Christian Magenheimer
 */
public class SetupBasics extends Activity implements OnClickListener,
		TextWatcher,MXAListener {

	private static final String TAG = "SetupBasics";

	// views
	private EditText mEdtAddress;
	private EditText mEdtPassword;
	private Button mBtnNext;

	// members
	private Server mServer;
	private SharedPreferences mPreferences;

	//MXA related members
	private IXMPPService mXMPPService;
	
	//dialogs during testing
	private ProgressDialog mProgressDialog;
	
	private boolean mMxaConnected=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mxa_setup_basics);

		// initialize members for UI elements.
		initResourceRefs();
	}
	
	@Override
	protected void onDestroy() {
		MXAController.get().disconnectMXA(this);
		super.onDestroy();
	}

	// ==========================================================
	// Interface methods
	// ==========================================================

	@Override
	public void onClick(View v) {
		if (v == mBtnNext) {			
			String xmppID = mEdtAddress.getText().toString().trim();
			
			if (!xmppID.matches("[a-zA-Z_0-9]+@[a-zA-Z_0-9.-]+"))
			{
				new AlertDialog.Builder(this).
					setMessage("Username does not match required schema: username@host.com")
				       .setCancelable(false)
				       .setPositiveButton("OK",null)
				       .show();
				return;
			}else if (mEdtPassword.getText().length()==0)
			{
				new AlertDialog.Builder(this).
				setMessage("Please submit a password")
			       .setCancelable(false)
			       .setPositiveButton("OK",null)
			       .show();
				return;
			}
			
			
			String[] xmppIDParts = xmppID.split("@");
			String domain = xmppIDParts[1].trim();
			mServer = findServerForDomain(domain);
			if (mServer == null) {
				// no default settings available, show Preference Activity
				//no, we do some clever things her
				//reworked code, just try the settings
				mPreferences = MXAController.get().getSharedPreferences();

				SharedPreferences.Editor editor = mPreferences.edit();

				editor.putString("pref_host", domain);
				editor.putString("pref_service", null);
				editor.putString("pref_resource", ConstMXA.XMPP_SETTINGS_STANDARD_RESSOURCE);
				editor.putString("pref_port", ConstMXA.XMPP_SETTINGS_STANDARD_SERVER_PORT);

				editor.putString("pref_xmpp_user", xmppID);
				editor.putString("pref_xmpp_password", mEdtPassword.getText().toString().trim());

				editor.commit();
				// now try these settings
				if (!mMxaConnected)MXAController.get().connectMXA(this, this);
				else {
					try {
						mXMPPService.connect(new Messenger(mxaHandler));
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				mProgressDialog=new ProgressDialog(this);
				mProgressDialog.setTitle("Connection in progress...");
				mProgressDialog.setMessage("Establishing connection to "+domain);
				mProgressDialog.setIndeterminate(true);
				mProgressDialog.show();
				return;
			}
			
			finishAutoSetup();
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		validateFields();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	// ==========================================================
	// Private methods
	// ==========================================================

	/**
	 * Initialize all UI elements from resources.
	 */
	private void initResourceRefs() {
		mEdtAddress = (EditText) findViewById(R.id.setup_basics_edt_address);
		mEdtAddress.addTextChangedListener(this);
		mEdtPassword = (EditText) findViewById(R.id.setup_basics_edt_password);
		mEdtPassword.addTextChangedListener(this);
		mBtnNext = (Button) findViewById(R.id.setup_basics_btn_next);
		//validateFields();
		mBtnNext.setOnClickListener(this);
	}

	/**
	 * Enables and disables the next button.
	 */
	private void validateFields() {
		// TODO validate ID
		boolean valid = (mEdtAddress.getText().length() > 2)
				&& (mEdtPassword.getText().length() > 0);
		mBtnNext.setEnabled(valid);
	}

	/**
	 * Attempts to get the given attribute as a String resource first, and if it
	 * fails returns the attribute as a simple String value.
	 * 
	 * @param xml
	 * @param name
	 * @return
	 */
	private String getXmlAttribute(XmlResourceParser xml, String name) {
		int resId = xml.getAttributeResourceValue(null, name, 0);
		if (resId == 0) {
			return xml.getAttributeValue(null, name);
		} else {
			return getString(resId);
		}
	}

	/**
	 * Looks up if there is a server in the servers.xml file for the given
	 * domain.
	 * 
	 * @param domain
	 * @return
	 */
	private Server findServerForDomain(String domain) {
		XmlResourceParser xml = getResources().getXml(R.xml.mxa_servers);
		int xmlEventType;
		Server server = null;
		try {
			while ((xmlEventType = xml.next()) != XmlResourceParser.END_DOCUMENT) {
				if (xmlEventType == XmlResourceParser.START_TAG
						&& "server".equals(xml.getName())
						&& domain.equalsIgnoreCase(getXmlAttribute(xml,
								"domain"))) {
					server = new Server();
					server.id = getXmlAttribute(xml, "id");
					server.domain = getXmlAttribute(xml, "domain");
				} else if (xmlEventType == XmlResourceParser.START_TAG
						&& "host".equals(xml.getName()) && server != null) {
					server.host = xml.nextText();
				} else if (xmlEventType == XmlResourceParser.START_TAG
						&& "port".equals(xml.getName()) && server != null) {
					server.port = xml.nextText();
				} else if (xmlEventType == XmlResourceParser.START_TAG
						&& "servicename".equals(xml.getName())
						&& server != null) {
					server.serviceName = xml.nextText();
				} else if (xmlEventType == XmlResourceParser.END_TAG
						&& "server".equals(xml.getName()) && server != null) {
					return server;
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Error while trying to load server settings.", e);
		}
		return null;
	}

	/**
	 * Saves the configuration in the Preferences file.
	 */
	private void finishAutoSetup() {
		Log.i(TAG, "Saving preference data.");

		String xmppID = mEdtAddress.getText().toString().trim();
		String password = mEdtPassword.getText().toString().trim();

		mPreferences = getSharedPreferences(
				"de.tudresden.inf.rn.mobilis.mxa_preferences",
				Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = mPreferences.edit();

		editor.putString("pref_host", mServer.host);
		editor.putString("pref_service", mServer.serviceName);
		editor.putString("pref_resource", "MXA");
		editor.putString("pref_port", mServer.port);

		editor.putString("pref_xmpp_user", xmppID);
		editor.putString("pref_xmpp_password", password);

		editor.commit();

		// show main activity
		Intent i = new Intent(this, SetupComplete.class);
		startActivity(i);
	}

	// ==========================================================
	// Inner classes
	// ==========================================================

	static class Server {
		public String id;
		public String domain;
		public String host;
		public String port;
		public String serviceName;
	}

	@Override
	public void onMXAConnected() {
		//now connect
		Log.d(TAG,"mxa connected");
		mMxaConnected=true;
		mXMPPService=MXAController.get().getXMPPService();
		try {
			mXMPPService.connect(new Messenger(mxaHandler));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"error binding xmppremoteservice: "+e.getMessage());
		}
	}

	@Override
	public void onMXADisconnected() {
		Log.d(TAG, "MXA disconnected");
	}
	
	private Handler guiHandler= new Handler(){
		
		public void handleMessage(android.os.Message msg)
		{
			Log.d(TAG,msg.toString());
		
		if (msg.what==ConstMXA.MSG_CONNECT && msg.arg1==ConstMXA.MSG_STATUS_SUCCESS)
		{
			Log.d(TAG,"succesfully connected");
			
			try {
				mXMPPService.disconnect(new Messenger(mxaHandler));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			
		}else if  (msg.what==ConstMXA.MSG_CONNECT && msg.arg1==ConstMXA.MSG_STATUS_ERROR)
		{
			String errorMessage=msg.getData().getString(ConstMXA.EXTRA_ERROR_MESSAGE);
			
			mProgressDialog.dismiss();
			AlertDialog.Builder builder= new AlertDialog.Builder(SetupBasics.this);
			if (errorMessage==null)errorMessage="Unknown Reason";
			else errorMessage="Reason: "+errorMessage;
			builder.setTitle("Connection failed");
			builder.setMessage(errorMessage+"\n\nDo you want to correct your input or do a manual setup?");
			builder.setPositiveButton("Manual setup", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(SetupBasics.this, PreferencesClient.class);
					startActivity(i);
					
					finish();

				}
			});
			builder.setNegativeButton("correct input", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}
			});		
			builder.show();
		
	}else if(msg.what==ConstMXA.MSG_DISCONNECT)
	{
		mProgressDialog.dismiss();
		Intent i = new Intent(SetupBasics.this, SetupComplete.class);
		startActivity(i);
		finish();
	}
}
};
	
	private Handler mxaHandler= new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			Message m=Message.obtain(msg);
			guiHandler.sendMessage(m);
		}
	};
}
