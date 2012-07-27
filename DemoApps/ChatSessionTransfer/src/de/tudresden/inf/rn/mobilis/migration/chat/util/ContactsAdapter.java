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

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import de.tudresden.inf.rn.mobilis.migration.chat.R;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.RosterItems;

public class ContactsAdapter extends SimpleCursorAdapter {

	private int layout;

	public ContactsAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to) {
		super(context, layout, c, from, to);
		this.layout = layout;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(layout, parent, false);

		return create(v, cursor);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		create(view, cursor);
	}

	private View create(View v, Cursor c) {

		TextView tv_name = (TextView) v.findViewById(R.id.contacts_item_name);

		String resource = c.getString(c.getColumnIndex(RosterItems.RESSOURCE));
		String jid = c.getString(c.getColumnIndex(RosterItems.XMPP_ID));
		// if (resource != null && !resource.equals(""))
		// tv_name.setText(jid + "/" + resource);
		// else
			tv_name.setText(jid);

		// ImageView imgView = (ImageView)
		// v.findViewById(R.id.contacts_item_name);
		// String mode =
		// c.getString(c.getColumnIndex(RosterItems.PRESENCE_MODE));
		// if (mode.equals(RosterItems.MODE_UNAVAILABLE))
		// imgView.setImageResource(R.drawable.offline);
		// else if (mode.equals(RosterItems.MODE_AVAILABLE))
		// imgView.setImageResource(R.drawable.available);
		// else if (mode.equals(RosterItems.MODE_CHAT))
		// imgView.setImageResource(R.drawable.chat);
		// else if (mode.equals(RosterItems.MODE_AWAY))
		// imgView.setImageResource(R.drawable.away);
		// else if (mode.equals(RosterItems.MODE_DO_NOT_DISTURB))
		// imgView.setImageResource(R.drawable.busy);
		// else if (mode.equals(RosterItems.MODE_EXTENDED_AWAY))
		// imgView.setImageResource(R.drawable.extended_away);

		TextView tvStatus = (TextView) v
				.findViewById(R.id.contacts_item_status);
		String status = c.getString(c
				.getColumnIndex(RosterItems.PRESENCE_STATUS));
		tvStatus.setText(status);
		return v;
	}

}
