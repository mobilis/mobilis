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

package de.tudresden.inf.rn.mobilis.mxa;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * The client side singleton class, included for convenience. * 
 * @author Istvan Koren * 
 */
public class MXAController implements ServiceConnection {

	private static MXAController instance;
	private IXMPPService mXMPPService = null;
	private MXAListener mListener;
	private SharedPreferences mSharedPreferences;

	private MXAController() {
	};

	public static MXAController get() {
		if (instance == null) {
			instance = new MXAController();
		}
		return instance;
	}
	
	public void connectMXA(Context ctx, MXAListener listener) {
		if (mSharedPreferences == null) {
			return;
		}
		
		mListener = listener;

		if (mXMPPService == null) {
			Intent i = new Intent(ctx, XMPPRemoteService.class);
			ctx.startService(i);
			ctx.bindService(i, instance, 0);
		} else {
			// inform listener if we're online
			if (mListener != null) {
				mListener.onMXAConnected();
			}
		}
	}

	public void disconnectMXA(Context ctx) {
		ctx.unbindService(instance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.ServiceConnection#onServiceConnected(android.content.
	 * ComponentName, android.os.IBinder)
	 */
	@Override
	public void onServiceConnected(ComponentName componentname, IBinder ibinder) {
		// This is called when the connection with the service has been
		// established, giving us the service object we can use to
		// interact with the service. We are communicating with our
		// service through an IDL interface, so get a client-side
		// representation of that from the raw service object.
		mXMPPService = IXMPPService.Stub.asInterface(ibinder);
		Log.i(this.getClass().getSimpleName(), "XMPP service connected");

		if (mListener != null) {
			mListener.onMXAConnected();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.ServiceConnection#onServiceDisconnected(android.content
	 * .ComponentName)
	 */
	@Override
	public void onServiceDisconnected(ComponentName componentname) {
		// This is called when the connection with the service has been
		// unexpectedly disconnected -- that is, its process crashed.
		Log.i(this.getClass().getSimpleName(), "XMPP service disconnected");
		mXMPPService = null;

		// notify initiator
		if (mListener != null) {
			mListener.onMXADisconnected();
		}
	}

	public IXMPPService getXMPPService() {
		return mXMPPService;
	}
	
	/**
	 * Checks whether the XMPP connection parameters have already been set.
	 * Doesn't check for correctness of data.
	 * @return <code>true</code> if XMPP connection parameters are all set,
	 * 		<code>false</code> otherwise.
	 */
	public boolean checkSetupDone() {
		return mSharedPreferences.contains("pref_host") 
				&& mSharedPreferences.contains("pref_service") 
				&& mSharedPreferences.contains("pref_xmpp_user") 
				&& mSharedPreferences.contains("pref_xmpp_password");
	}

	public SharedPreferences getSharedPreferences() {
		return mSharedPreferences;
	}

	public void setSharedPreferences(SharedPreferences mSharedPreferences) {
		this.mSharedPreferences = mSharedPreferences;
	}
}
