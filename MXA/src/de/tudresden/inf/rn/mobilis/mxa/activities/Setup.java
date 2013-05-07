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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.tudresden.inf.rn.mobilis.mxa.R;

/**
 * Displays the Setup activity that is shown if there is no account created yet.
 * @author Istvan Koren
 */
public class Setup extends Activity implements OnClickListener {

	private static final String TAG = "Setup";

	// views
	private Button mBtnManual;
	private Button mBtnNext;

	/**
	 * Start the Setup activity. Uses the CLEAR_TOP flag which means that other
	 * stacked activities may be killed in order to get back to Setup.
	 */
	public static void actionShowSetup(Context context) {
		Intent i = new Intent(context, Setup.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.mxa_setup);

		// initialize members for UI elements.
		initResourceRefs();
	}

	// ==========================================================
	// Interface methods
	// ==========================================================

	@Override
	public void onClick(View v) {
		if (v == mBtnManual) {
			// show Preference activity
			Intent i = new Intent(this, PreferencesClient.class);
			startActivity(i);
		} else if (v == mBtnNext) {
			// show SetupBasics dialog
			Intent i = new Intent(this, SetupBasics.class);
			startActivity(i);
		}
		finish();
	}

	// ==========================================================
	// Private methods
	// ==========================================================

	/**
	 * Initialize all UI elements from resources.
	 */
	private void initResourceRefs() {
		mBtnManual = (Button) findViewById(R.id.setup_btn_manual);
		mBtnManual.setOnClickListener(this);
		mBtnNext = (Button) findViewById(R.id.setup_btn_next);
		mBtnNext.setOnClickListener(this);
	}

}
