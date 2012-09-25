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

package de.tudresden.inf.rn.mobilis.adhoc.demo;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class SetColorMessage {

	// Tag for log information
	private static String TAG = "SetColorMessage";

	// members
	private String mPayload;
	private int color;

	public SetColorMessage(int color) {
		this.color = color;
	}

	public String toXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<setcolor xmlns=\"mobilis:adhoc:color\">");
		sb.append(color);
		sb.append("</setcolor>");
		return sb.toString();
	}

	public void fromXML(XmlPullParser parser) throws Exception {

		boolean done = false;
		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("setcolor")) {
					color = Integer.parseInt(parser.nextText());
					done = true;
				}
			}
		}
		Log.i(TAG, "parsed color " + color);
	}

	public void setPayload(String payload) {
		mPayload = payload;

		// parse
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(mPayload));
			fromXML(parser);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

}
