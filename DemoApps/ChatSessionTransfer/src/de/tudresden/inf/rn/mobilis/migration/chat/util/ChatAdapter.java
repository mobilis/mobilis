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

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import de.tudresden.inf.rn.mobilis.migration.chat.R;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.MessageItems;

/**
 * Simple Adapter for the MXA Chat Items. Takes the items from the database and
 * does a simple layout: ---------------------- -me:Hi John-----------
 * -(date)--------------- ---------------------- ----John: Good,thanks-
 * ----(date)------------ ----------------------
 * 
 * @author Christian Magenheimer
 * 
 */
public class ChatAdapter extends SimpleCursorAdapter {

	private int layout;
	private Context context;
	private String mUserXMPPId = null;

	/**
	 * Sets the users id for filtering the messages
	 * 
	 * @param xmppid
	 *            The XMPP ID of the current user
	 */
	public void setUserXMPPId(String xmppid) {
		mUserXMPPId = xmppid;
	}

	/*
	 * Constructs an ChatAdapter, just forwards calls to the super constructor.
	 */
	public ChatAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to) {
		super(context, layout, c, from, to);
		this.layout = layout;
		this.context = context;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.ResourceCursorAdapter#newView(android.content.Context,
	 * android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(layout, parent, false);

		// String

		return create(v, cursor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleCursorAdapter#bindView(android.view.View,
	 * android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		create(view, cursor);
	}

	/**
	 * Creates the View for the ChatAdapter. This methode filters the xmpp id
	 * and gives the own messages a slight red background, and all others a
	 * slightly yellow one. Also generates a date out of the timestamp
	 * 
	 * @param v
	 *            View to be used
	 * @param c
	 *            Cursor the database with messages
	 * @return the generated View
	 */
	public View create(View v, Cursor c) {
		TextView tv = (TextView) v.findViewById(R.id.chat_item_txt_message);
		Date sendTime = new Date(Long.valueOf(c.getString(c
				.getColumnIndex(MessageItems.DATE_SENT))));
		String date;
		String sender = c.getString(c.getColumnIndex(MessageItems.SENDER));
		// if (sender!=null) sender= sender.split("@")[0];
		String body = c.getString(c.getColumnIndex(MessageItems.BODY));
		Log.v("ChatAdapter", "String: " + body);
		Date midnight = new Date(System.currentTimeMillis());
		midnight.setHours(0);
		midnight.setMinutes(0);
		midnight.setSeconds(0);

		if (sendTime.before(midnight)) {
			date = DateFormat.format("MM/dd/yy h:mm aa", sendTime).toString();
		} else {
			date = DateFormat.format("h:mm aa", sendTime).toString();
		}

		// set the indent appropriate
		if (mUserXMPPId != null) {
			if (sender.contains(mUserXMPPId.split("/")[0])) {
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tv
						.getLayoutParams();
				params.setMargins(0, 0, 0, 0); // substitute parameters for
												// left, top, right, bottom
				tv.setLayoutParams(params);
				tv.setGravity(Gravity.LEFT);
				tv.setBackgroundColor(Color.parseColor("#ffebe9"));
				sender = "me";
			} else {
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tv
						.getLayoutParams();
				params.setMargins(20, 0, 0, 0); // substitute parameters for
												// left, top, right, bottom
				tv.setLayoutParams(params);
				tv.setGravity(Gravity.RIGHT);
				if (sender.contains("@"))
					sender = sender.split("@")[0];
				tv.setBackgroundColor(Color.parseColor("#fff5df"));
			}
		}

		// align to the right
		// int w=v.getWidth();
		// tv.setWidth((int)(v.getWidth()*0.8));
		// Log.v("ChatAdapter",w+" "+v.getWidth());

		tv.setText(Html.fromHtml("<b>" + sender + ":</b> " + body + "<br>("
				+ date + ")"));
		tv.setTextColor(Color.BLACK);

		// generate the greyish pattern for the date string
		Spannable s = (Spannable) tv.getText();
		s.setSpan(new TextAppearanceSpan(context,
				android.R.style.TextAppearance_Small), tv.getText().toString()
				.indexOf(date) - 1, date.length()
				+ tv.getText().toString().indexOf(date) + 1,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new ForegroundColorSpan(Color.parseColor("#9f9f9f")), tv
				.getText().toString().indexOf(date) - 1, date.length()
				+ tv.getText().toString().indexOf(date) + 1,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return v;
	}

}