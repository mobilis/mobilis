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

package de.tudresden.inf.rn.mobilis.mxa.services.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A DiscoverItem is an item element of a ServiceDiscovery.
 * @author Benjamin Söllner
 */
public class DiscoverItem implements Parcelable {

	public String name;
	public String jid;
	public String node;
	
	public static final Parcelable.Creator<DiscoverItem> CREATOR
			= new Parcelable.Creator<DiscoverItem>() {
		public DiscoverItem createFromParcel(Parcel in) {
			return new DiscoverItem(in);
		};
		public DiscoverItem[] newArray(int size) {
			return new DiscoverItem[size];
		}
	}; 
	
	public DiscoverItem() {}
	
	public DiscoverItem(Parcel in) {
		this.readFromParcel(in);
	}
	
	public DiscoverItem(String name, String jid, String node) {
		this.name = name;
		this.jid = jid;
		this.node = node;
	}
	
	public void readFromParcel(Parcel in) {
		this.name = in.readString();
		this.jid  = in.readString();
		this.node = in.readString();
	}	

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.name);
		out.writeString(this.jid);
		out.writeString(this.node);
	}
	
}
