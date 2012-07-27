package de.tudresden.inf.rn.mobilis.mxa.util;

import java.util.HashMap;
import java.util.HashSet;

import org.jivesoftware.smack.filter.PacketFilter;

import de.tudresden.inf.rn.mobilis.mxa.callbacks.IChatStateCallback;

import android.os.IBinder;
import android.os.RemoteCallbackList;

/**
 * Holds the callbacks for chatstate events. Every app can register with a jid,
 * this callback is then notifies when the particular jid has send a chatstate.
 * Can also be used for other callbacks registered for one jid.
 * 
 * @author Christian Magenheimer
 * 
 * @param <E>
 *            Interface that holds the ChatStateCallbacks, in this case this
 *            will be IChatStateCallback
 */
public class JIDFilteredCallbackList<E extends android.os.IInterface> extends
		RemoteCallbackList<E> {

	private HashMap<String, HashSet<IBinder>> mCallbacks = new HashMap<String, HashSet<IBinder>>();

	/**
	 * Inserts an element into the list.
	 * 
	 * @param callback
	 *            Callback to be registered
	 * @param jid
	 *            users jid
	 * @return true, if registering was successful, otherwise false
	 */
	public boolean register(E callback, String jid) {
		if (super.register(callback)) {
			HashSet<IBinder> set;
			if (mCallbacks.containsKey(jid)) {
				set = mCallbacks.get(jid);
			} else {
				set = new HashSet<IBinder>();
			}
			set.add(callback.asBinder());

			mCallbacks.put(jid, set);
			return true;
		}
		return false;
	}

	/**
	 * Removes a callback from the list
	 * 
	 * @param callback
	 *            the callback to be removed
	 * @param jid
	 *            the users jid, which sent the chatstates
	 * @return true, if unregistering was successful
	 */
	public boolean unregister(E callback, String jid) {
		if (super.unregister(callback)) {
			return mCallbacks.get(jid).remove(callback.asBinder());
		} else
			return false;
	}

	public void onCallbackDied(E callback) {
		synchronized (mCallbacks) {
			mCallbacks.values().remove(callback.asBinder());
		}
	};

	public HashSet<IBinder> getCallbacks(String jid) {
		return mCallbacks.get(jid);
	}

	public boolean appliesToJid(IChatStateCallback cb, String jid) {
		return (mCallbacks.containsKey(jid) && mCallbacks.get(jid).contains(
				cb.asBinder()));

	}
}
