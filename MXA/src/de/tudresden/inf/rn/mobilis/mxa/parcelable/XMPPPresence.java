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

package de.tudresden.inf.rn.mobilis.mxa.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Istvan Koren
 */
public class XMPPPresence implements Parcelable {

	// members
	public int mode;
	/**
	 * The status text for additional information, e.g. "having dinner".
	 */
	public String status;
	public int priority;

	// mode enums
	public static final int MODE_AVAILABLE = 0;
	public static final int MODE_AWAY = 1;
	public static final int MODE_CHAT = 2;
	public static final int MODE_DND = 3;
	public static final int MODE_XA = 4;

	public static final Parcelable.Creator<XMPPPresence> CREATOR = new Parcelable.Creator<XMPPPresence>() {
		public XMPPPresence createFromParcel(Parcel in) {
			return new XMPPPresence(in);
		}

		public XMPPPresence[] newArray(int size) {
			return new XMPPPresence[size];
		}
	};

	public XMPPPresence() {
	}

	public XMPPPresence(int mode, String status, int priority) {
		this.mode = mode;
		this.status = status;
		this.priority = priority;
	}

	private XMPPPresence(Parcel in) {
		readFromParcel(in);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mode);
		dest.writeString(status);
		dest.writeInt(priority);
	}

	public void readFromParcel(Parcel in) {
		mode = in.readInt();
		status = in.readString();
		priority = in.readInt();
	}

}
