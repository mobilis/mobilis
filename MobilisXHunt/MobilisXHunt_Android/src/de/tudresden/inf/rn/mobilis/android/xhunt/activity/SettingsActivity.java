/*******************************************************************************
 * Copyright (C) 2010 Technische Universität Dresden
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 ******************************************************************************/
package de.tudresden.inf.rn.mobilis.android.xhunt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;
import de.tudresden.inf.rn.mobilis.android.xhunt.service.ServiceConnector;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;

/**
 * The Class SettingsActivity.
 */
public class SettingsActivity extends PreferenceActivity 
	/*implements OnSharedPreferenceChangeListener*/ {
	
	/** The Constant TAG for logging. */
	private static final String TAG = "SettingsActivity";
	
	/** The EditText for the servers jid. */
	private EditTextPreference mEditServerJID;
	
	/** The EditText for the nickname. */
	private EditTextPreference mEditNickname;	
	
	/** The CheckBox for switching between Static Mode/GPS Mode. */
	private CheckBoxPreference mSetStaticMode;
	
	/** The CheckBox for activating/deactivating logging. */
	private CheckBoxPreference mSetLogging;
	
	/** A button-like field for deleting logfiles. */
	private Preference mDeleteLogs;
	
	/** The Button to start the MXA preferences. */
	private Button btnMxaSettings;
	
	/** The ServiceConnector to connect to the XHuntService. */
	private ServiceConnector mServiceConnector;
	
	
    /** The Handler which will be called if the XHuntService was 
     * successfully bound. */
    private Handler mXHuntServiceBoundHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			addPreferencesFromResource(R.xml.layout_settings);
			setContentView(R.layout.activity_settings);
			
			initComponents();
		}
	};
	
	
	/**
	 * Bind XHuntService.
	 */
	private void bindXHuntService(){
    	mServiceConnector = new ServiceConnector(this);
    	mServiceConnector.doBindXHuntService(mXHuntServiceBoundHandler);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		mServiceConnector.doUnbindXHuntService();
		super.finish();
	}
	
	/**
	 * Gets the shared preference key for the nickname.
	 *
	 * @return the key of the nickname
	 */
	private String getKeyNickname(){
		return getResources().getString(R.string.bundle_key_settings_username);
	}
	
	/**
	 * Gets the shared preference key for the server jid.
	 *
	 * @return the key of the server jid
	 */
	private String getKeyServerJid(){
		return getResources().getString(R.string.bundle_key_settings_serverjid);
	}
	
	/**
	 * Gets the shared preference key for the static mode.
	 * 
	 * @return the key of the static mode
	 */
	private String getKeyStaticMode() {
		return getResources().getString(R.string.bundle_key_settings_staticmode);
	}
	
	/**
	 * Gets the shared preference key for the checkbox.
	 * 
	 * @return the key of the logging checkbox
	 */
	private String getKeyLogging() {
		return getResources().getString(R.string.bundle_key_settings_logging);
	}
	
	/**
	 * Gets the shared preference key for the delete-logfiles-button.
	 * 
	 * @return the key of the button to delete logfiles
	 */
	private String getKeyDeleteLogs() {
		return getResources().getString(R.string.bundle_key_settings_deletelogs);
	}

	/**
	 * Gets the shared preference value of a key.
	 *
	 * @param key the key related to a shared preference
	 * @return the shared preference value
	 */
	private String getSharedPrefValue(String key){
		return mServiceConnector.getXHuntService().getSharedPrefHelper()
			.getValue(key);
	}
	
	
	/**
	 * Inits the components.
	 */
	private void initComponents(){
		mEditNickname = (EditTextPreference)getPreferenceScreen().findPreference(
				getKeyNickname());
		
		mEditServerJID = (EditTextPreference)getPreferenceScreen().findPreference(
				getKeyServerJid());
		mEditServerJID.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String cJid = (String) newValue;
				if (cJid!=null && !cJid.equals("")) {				
					String[] arr = cJid.split("/");
					if (arr.length<2) {							
							((EditTextPreference)preference).setText(arr[0].toLowerCase() + "/Coordinator");
							Toast.makeText(getApplicationContext(), "No resource set. '/Coordinator' has been added.", Toast.LENGTH_LONG).show();
							return false;
					}					
					((EditTextPreference)preference).setText(arr[0].toLowerCase() + "/" + arr[1]);
					return false;
				} else {
					String defaultJID = getResources().getString(R.string.default_jid_server);
					((EditTextPreference)preference).setText(defaultJID);
					Toast.makeText(getApplicationContext(), "No JID set. Using default value '" + defaultJID + "'.", Toast.LENGTH_LONG).show();
					return false;
				}
			}
		});
		
		mSetStaticMode = (CheckBoxPreference)getPreferenceScreen().findPreference(getKeyStaticMode());
		mSetStaticMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				boolean staticMode = (Boolean)newValue;
				Log.v(TAG, "User set StaticMode to " + staticMode);	
				if(staticMode)
					mServiceConnector.getXHuntService().getMXAProxy().setStaticMode(true);
				else
					mServiceConnector.getXHuntService().getMXAProxy().setStaticMode(false);

				return true;
			}
		});
		
		
		mSetLogging = (CheckBoxPreference)getPreferenceScreen().findPreference(getKeyLogging());
		mSetLogging.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				boolean logging = (Boolean) newValue;
				Log.v(TAG, "User set logging to " + logging);
				if(logging) {
					mServiceConnector.getXHuntService().getTools().writeLogToFile();
				} else {
					mServiceConnector.getXHuntService().getTools().stopWritingLogToFile();
				}
				return true;
			}
		});
		
		
		mDeleteLogs = (Preference)getPreferenceScreen().findPreference(getKeyDeleteLogs());
		mDeleteLogs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
				dialogBuilder.setMessage("Delete all logfiles from external memory?").setCancelable(false);
				
				dialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mServiceConnector.getXHuntService().getTools().deleteLogFiles();
						Log.v(TAG, "User deleted logfiles from external memory");
					}
				});
				
				dialogBuilder.setNegativeButton("no", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				
				dialogBuilder.create().show();
				return false;
			}
		});
		
		
		btnMxaSettings = (Button)findViewById(R.id.settings_btn_mxa_preferences);
		btnMxaSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ConstMXA.INTENT_PREFERENCES);
				i.addCategory(Intent.CATEGORY_PREFERENCE);
				startActivity(i);
			}
		});
		
		updateSummaries();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		bindXHuntService();
    }	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onWindowFocusChanged(boolean)
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		updateSummaries();
		super.onWindowFocusChanged(hasFocus);
	}
	
	/**
	 * Update summaries of all preference entries.
	 * A summary displays the current value of a preference.
	 */
	private void updateSummaries(){
		if (mEditNickname != null) {
			mEditNickname.setSummary(getSharedPrefValue(getKeyNickname()));
		}
		
		if (mEditServerJID != null) {
			mEditServerJID.setSummary(getSharedPrefValue(getKeyServerJid()));
		}
	}

}
