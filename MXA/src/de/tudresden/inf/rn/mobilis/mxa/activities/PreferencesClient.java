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
 */

/**
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 */

package de.tudresden.inf.rn.mobilis.mxa.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.R;

/**
 * A simple Preference Activity for manually changing the preference values. 
 * @author Istvan Koren, Christian Magenheimer
 */
public class PreferencesClient extends PreferenceActivity {
	
	SharedPreferences mSharedPreferences;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.getPreferenceManager().setSharedPreferencesName(MXAController.get().getSharedPreferencesName());

        // Load the preferences page layout from an XML resource
        addPreferencesFromResource(R.xml.mxa_preferences);
        
        mSharedPreferences = MXAController.get().getSharedPreferences();
        
        //set the FileChooserActivity for the directory
        Preference customPref = (Preference) findPreference("pref_xmpp_debug_directory");
        customPref.setSummary(mSharedPreferences.getString("pref_xmpp_debug_directory", "not selected"));
        customPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				//start the activity where you can choose a directory 
				Intent i= new Intent(PreferencesClient.this,FileChooserActivity.class);
				startActivityForResult(i,FileChooserActivity.REQUEST_CODE);
				return true;
			}
		});
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==FileChooserActivity.REQUEST_CODE &&
				resultCode==RESULT_OK)
		{
			Preference customPref = (Preference) findPreference("pref_xmpp_debug_directory");
	        
			String dirName=data.getExtras().getString(FileChooserActivity.EXTRA_FILE_NAME);
			if (dirName!=null)
			{
				
				mSharedPreferences.edit().putString("pref_xmpp_debug_directory", dirName).commit();
				customPref.setSummary(mSharedPreferences.getString("pref_xmpp_debug_directory", "not selected"));
			}
		}else
		{
			Preference customPref = (Preference) findPreference("pref_xmpp_debug_directory");
			mSharedPreferences.edit().putString("pref_xmpp_debug_directory", null).commit();
			customPref.setSummary(mSharedPreferences.getString("pref_xmpp_debug_directory", "not selected"));
		}
	}
}

