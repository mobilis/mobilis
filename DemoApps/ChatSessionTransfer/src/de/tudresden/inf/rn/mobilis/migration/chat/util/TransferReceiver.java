/**
 * Copyright (C) 2009 Technische Universität Dresden
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

package de.tudresden.inf.rn.mobilis.migration.chat.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.tudresden.inf.rn.mobilis.migration.chat.activities.ContactsActivity;

public class TransferReceiver extends BroadcastReceiver {

	// Tag for log information
	private static String TAG = "TransferReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "received intent");
		abortBroadcast();

		Intent i = new Intent();
		i.setClass(context, ContactsActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("mobilis:iq:sessionmobility#from",
				intent.getStringExtra("mobilis:iq:sessionmobility#from"));
		context.startActivity(i);
	}
}
