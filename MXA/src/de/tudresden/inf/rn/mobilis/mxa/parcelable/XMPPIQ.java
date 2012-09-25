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
 * 
 * Contains parts of Android Email App (C) 2008 The Android Open Source Project
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
 * @author Istvan Koren, Benjamin S�llner
 */
public class XMPPIQ implements Parcelable {

	// members
	public String packetID;
	public String to;
	public String from;
	public int type;
	public String payload;
	public String namespace;
	public String element;
	public String token;
	
	// type enums
	public static final int TYPE_GET = 0;
	public static final int TYPE_SET = 1;
	public static final int TYPE_RESULT = 2;
	public static final int TYPE_ERROR = 3;


	public static final Parcelable.Creator<XMPPIQ> CREATOR = new Parcelable.Creator<XMPPIQ>() {
		public XMPPIQ createFromParcel(Parcel in) {
			return new XMPPIQ(in);
		}

		public XMPPIQ[] newArray(int size) {
			return new XMPPIQ[size];
		}
	};

	public XMPPIQ() {
	}
	
	public XMPPIQ(String from, String to, int type, String element, String namespace, String payload) {
		this.from = from;
		this.to = to;
		this.type = type;
		this.payload = payload;
		this.namespace = namespace;
		this.element = element;
	}

	private XMPPIQ(Parcel in) {
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
		dest.writeString(packetID);
		dest.writeString(to);
		dest.writeString(from);
		dest.writeInt(type);
		dest.writeString(payload);
		dest.writeString(namespace);
		dest.writeString(element);
		dest.writeString(token);
	}

	public void readFromParcel(Parcel in) {
		packetID = in.readString();
		to = in.readString();
		from = in.readString();
		type = in.readInt();
		payload = in.readString();
		namespace = in.readString();
		element = in.readString();
		token = in.readString();
	}
	
	@Override
	public String toString() {
		StringBuffer sb= new StringBuffer();
		sb.append("<iq from='").append(from).append("' to='");
		sb.append(to).append("' type='").append(type).append("' id='").append(packetID).append("' >");
		sb.append("<").append(element).append(" xmlns='").append(namespace).append("' >");
		sb.append(payload).append("</").append(element).append(">");
		sb.append("</iq>");
		return sb.toString();
		
	}

}
