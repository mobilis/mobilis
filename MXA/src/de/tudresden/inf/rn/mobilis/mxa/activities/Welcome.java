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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

/**
 * Determines if there is already an account in the preferences; if not, shows
 * the Setup activity.
 * @author Istvan Koren
 */
public class Welcome extends Activity {

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// read in preferences
		SharedPreferences pref = getSharedPreferences("de.tudresden.inf.rn.mobilis.mxa_preferences",
				Context.MODE_PRIVATE);
		if (!pref.contains("pref_xmpp_user")) {
			Log.i("Welcome", "starting setup" + pref.contains("pref_xmpp_user"));
			// no preferences set, show Setup activity
			Setup.actionShowSetup(this);
		} else {
			Log.i("Welcome", "starting main activity");
			// show MainActivity
			Intent i= new Intent(this, MainActivity.class);
			startActivity(i);
		}
		finish();
	}

}
