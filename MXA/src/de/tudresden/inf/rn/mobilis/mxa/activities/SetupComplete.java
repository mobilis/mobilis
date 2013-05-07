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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.tudresden.inf.rn.mobilis.mxa.R;

/**
 * Displays an activity to show that the setup assistant successfully added the
 * new account.
 * @author Istvan Koren
 */
public class SetupComplete extends Activity implements OnClickListener {

	private static final String TAG = "SetupComplete";

	// views
	private Button mBtnClose;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.mxa_setup_complete);
		
		// initialize members for UI elements.
		initResourceRefs();
	}

	// ==========================================================
	// Interface methods
	// ==========================================================

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v == mBtnClose) {
			// TODO close assistant
			finish();
		}
	}
	
	// ==========================================================
	// Private methods
	// ==========================================================

	/**
	 * Initialize all UI elements from resources.
	 */
	private void initResourceRefs() {
		mBtnClose = (Button) findViewById(R.id.setup_complete_btn_close);
		mBtnClose.setOnClickListener(this);
	}
}
