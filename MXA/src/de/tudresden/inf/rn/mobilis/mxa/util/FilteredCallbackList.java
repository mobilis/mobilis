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

package de.tudresden.inf.rn.mobilis.mxa.util;

import java.util.HashMap;
import java.util.HashSet;

import org.jivesoftware.smack.filter.PacketFilter;

import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.util.Log;

/**
 * A single callback interface can handle different namespaces and tokens. This
 * class provides a convenient way of managing these callbacks.
 * @author Istvan Koren
 */
public class FilteredCallbackList<E extends android.os.IInterface> extends
		RemoteCallbackList<E> {
	
	/** The TAG for the Log. */
	private final static String TAG = "FilteredCallbackList";

	private HashMap<IBinder, HashSet<PacketFilter>> mFilters = new HashMap<IBinder, HashSet<PacketFilter>>();

	/**
	 * Registers a callback and the associated filter.
	 * 
	 * @param callback
	 * @param filter
	 * @return
	 */
	public boolean register(E callback, PacketFilter filter) {
		if (super.register(callback)) {
			synchronized (mFilters) {
				if (mFilters.containsKey(callback.asBinder())) {
//					Log.i(TAG, "register() --> Callback already registered.");
					boolean alreadyPresent = false;
					for (PacketFilter pf : mFilters.get(callback.asBinder())) {
						if (pf.equals(filter)) {
							alreadyPresent = true; break;
						}						
					}
					if (!alreadyPresent) {
						boolean result = mFilters.get(callback.asBinder()).add(filter);
//						Log.i(TAG, "register() --> Added Filter for already present Callback. result:"+result);
					} else {
//						Log.i(TAG, "register() --> Filter NOT added, because Callback already contained it.");
					}
				} else {
//					Log.i(TAG, "register() --> Callback NOT already registered. New one is created.");
					HashSet<PacketFilter> filters = new HashSet<PacketFilter>();
					filters.add(filter);
					mFilters.put(callback.asBinder(), filters);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns all the filters that are available for this callback. Works on
	 * asBinder().
	 * 
	 * @param callback
	 * @return
	 */
	public HashSet<PacketFilter> getFilters(E callback) {
		return mFilters.get(callback.asBinder());
	}

	/**
	 * Removes the callback if there are no more filters for it.
	 * 
	 * @param callback
	 * @param filter
	 * @return
	 */
	public boolean unregister(E callback, PacketFilter filter) {
		Log.i(TAG, "unregister()");
		synchronized (mFilters) {
			if (mFilters.containsKey(callback.asBinder())) {
//				Log.i(TAG, "unregister() --> This Callback is registered.");
				// check if there is more than one filter
				if (mFilters.get(callback.asBinder()).size() > 1) {
//					Log.i(TAG, "unregister() --> There is more than one filter for this callback.");
					// remove the filter if it is present
					boolean result=false;
					for (PacketFilter pf : mFilters.get(callback.asBinder())) {
						if (pf.equals(filter)) {
							result=mFilters.get(callback.asBinder()).remove(pf);
							Log.i(TAG, "unregister() --> Removed filter: "+pf);
							break;
						}
					}
//					Log.i(TAG, "unregister() --> Removing only one Filter. result:"+result);
					return result;
				} else {
//					Log.i(TAG, "unregister() --> There is only one filter for this callback.");
					// only one filter available, if it is the current one,
					// remove
					// it both from the super and our filters list
					for (PacketFilter pf : mFilters.get(callback.asBinder())) {
						if (pf.equals(filter)) {
							HashSet<PacketFilter> a = mFilters.remove(callback.asBinder());
//							Log.i(TAG, "unregister() --> This element was removed: "+a.toString());
							return super.unregister(callback);
						}
					}
//					Log.i(TAG, "unregister() --> The callback does not have THIS filter");
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public void onCallbackDied(E callback) {
		synchronized (mFilters) {
			mFilters.remove(callback.asBinder());
		}
		super.onCallbackDied(callback);
	}
}
