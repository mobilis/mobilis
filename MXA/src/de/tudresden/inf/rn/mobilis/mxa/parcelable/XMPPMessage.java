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
public class XMPPMessage implements Parcelable {

	// members
	public String to;
	public String from;
	public String body;
	public int type;
	public String namespace;
	public String token;
	
	// type enums
	public static final int TYPE_CHAT = 0;
	public static final int TYPE_ERROR = 1;
	public static final int TYPE_GROUPCHAT = 2;
	public static final int TYPE_HEADLINE = 3;
	public static final int TYPE_NORMAL = 4;

	
	public static final Parcelable.Creator<XMPPMessage> CREATOR = new Parcelable.Creator<XMPPMessage>() {
		public XMPPMessage createFromParcel(Parcel in) {
			return new XMPPMessage(in);
		}

		public XMPPMessage[] newArray(int size) {
			return new XMPPMessage[size];
		}
	};

	public XMPPMessage() {
	}
	
	public XMPPMessage(String from, String to, String body, int type) {
		this.from = from;
		this.to = to;
		this.body = body;
		this.type = type;
	}

	private XMPPMessage(Parcel in) {
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
		dest.writeString(to);
		dest.writeString(from);
		dest.writeString(body);
		dest.writeInt(type);
		dest.writeString(namespace);
		dest.writeString(token);
	}

	public void readFromParcel(Parcel in) {
		to = in.readString();
		from = in.readString();
		body = in.readString();
		type = in.readInt();
		namespace = in.readString();
		token = in.readString();
	}
}
