package de.tudresden.inf.rn.mobilis.mxa.util;

import java.util.HashMap;
import java.util.HashSet;

import de.tudresden.inf.rn.mobilis.mxa.callbacks.IChatStateCallback;

import android.os.IBinder;
import android.os.RemoteCallbackList;

/**
 * This class is responsible for storing callbacks to message extensions, in
 * particular the identification is based on two strings, normally this would
 * apply to something like this: <message to=".." from=".."> <elementName
 * xmln="namespace">payload</elementName> </message>
 * 
 * @author Christian Magenheimer
 * 
 * @param <E>
 *            the Binder of the object
 */
public class MessageExtensionCallbackList<E extends android.os.IInterface>
		extends RemoteCallbackList<E> {

	/**
	 * Holds the info, is a concatenation of strings
	 * (elementName->namespace->Callbacks)
	 */
	private HashMap<MessageExtensionCallbackListEntry, HashSet<IBinder>> mCallbacks = new HashMap<MessageExtensionCallbackListEntry, HashSet<IBinder>>();

	/**
	 * Inserts an element into the list.
	 * 
	 * @param callback
	 *            Callback to be registered
	 * @param elementName
	 *            first string in the list
	 * @param namespace
	 *            second string in the list
	 * @return true, if registering was successful, otherwise false
	 */
	public boolean register(E callback, String elementName, String namespace) {
		if (super.register(callback)) {
			MessageExtensionCallbackListEntry e = new MessageExtensionCallbackListEntry();
			e.namespace = namespace;
			e.elementName = elementName;
			HashSet<IBinder> set = null;
			// check if the key exists
			if (mCallbacks.containsKey(e)) {
				set = mCallbacks.get(e);
				set.add(callback.asBinder());
			} else {
				set = new HashSet<IBinder>();

			}
			set.add(callback.asBinder());
			mCallbacks.put(e, set);
			return true;
		}
		return false;
	}

	/**
	 * Removes a callback from the list
	 * 
	 * @param callback
	 *            the callback to be removed
	 * @param elementName
	 *            first string in the list
	 * @param namespace
	 *            second string in the list
	 * @return true, if unregistering was successful
	 */
	public boolean unregister(E callback, String elementName, String namespace) {
		if (super.unregister(callback)) {
			MessageExtensionCallbackListEntry e = new MessageExtensionCallbackListEntry();
			e.namespace = namespace;
			e.elementName = elementName;
			if (mCallbacks.containsKey(e)) {
				boolean result = mCallbacks.get(e).remove(callback.asBinder());
				if (mCallbacks.get(e).size() == 0)
					mCallbacks.remove(e);
				return result;
			}
			return false;
		}
		return false;
	}

	public void onCallbackDied(E callback) {
		synchronized (mCallbacks) {
			mCallbacks.values().remove(callback.asBinder());
		}
	};

	public HashSet<IBinder> getCallbacks(String elementName, String namespace) {
		MessageExtensionCallbackListEntry e = new MessageExtensionCallbackListEntry();
		e.namespace = namespace;
		e.elementName = elementName;
		return mCallbacks.get(e);
	}

	private class MessageExtensionCallbackListEntry {
		public String elementName;
		public String namespace;

		@Override
		public int hashCode() {
			// just compute the sum of both elements
			return elementName.hashCode() + namespace.hashCode();
		}

		@Override
		public boolean equals(Object o) {

			if (this == o)
				return true;
			if (o instanceof MessageExtensionCallbackList.MessageExtensionCallbackListEntry) {
				MessageExtensionCallbackListEntry oc = (MessageExtensionCallbackList.MessageExtensionCallbackListEntry) o;
				return (oc.equals(elementName) && oc.equals(namespace));
			}
			return false;
		}
	}
}
