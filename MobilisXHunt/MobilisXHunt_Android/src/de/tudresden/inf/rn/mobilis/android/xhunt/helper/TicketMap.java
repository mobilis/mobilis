package de.tudresden.inf.rn.mobilis.android.xhunt.helper;

import java.util.HashMap;

import android.util.Log;

public class TicketMap extends HashMap<Integer, Integer> {

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = 5494866260460170329L;

	@Override
	public Integer get(Object key) {
		Integer value = super.get(key);
		if (value == null) {
			Log.w(TicketMap.class.getName(), "Ticket ID not found, returned 0.");
			return 0;
		} else
			return value;
	}
}
