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
 * 
 * Contains parts of Android Email App (C) 2008 The Android Open Source Project
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
 * The FileTransfer parcelable is included in all calls concerning FileTransfers (as defined in XEP-0096).
 * It includes members for determining the path and size of the underlying file.
 * @author Benjamin Söllner
 */
public class FileTransfer extends ByteStream {

	// members
	public String description = null;
	public String path = null;
	public int blockSize;
	public String mimeType;
	public long size;
	
	
	public static final Parcelable.Creator<FileTransfer> CREATOR = new Parcelable.Creator<FileTransfer>() {
		public FileTransfer createFromParcel(Parcel in) {
			return new FileTransfer(in);
		}

		public FileTransfer[] newArray(int size) {
			return new FileTransfer[size];
		}
	};

	public FileTransfer() {
		super();
	}
	
	public FileTransfer(String from, String to, String description, String path, String mimeType, int blockSize, long size) {
		this.from = from;
		this.to = to;
		this.description = description;
		this.path = path;
		this.mimeType = mimeType;
		this.blockSize = blockSize;
		this.size = size;
	}

	private FileTransfer(Parcel in) {
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
		return super.describeContents();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(this.path);
		dest.writeString(this.description);
		dest.writeString(this.mimeType);
		dest.writeInt(this.blockSize);
		dest.writeLong(this.size);
	}

	public void readFromParcel(Parcel in) {
		super.readFromParcel(in);
		this.path = in.readString();
		this.description = in.readString();
		this.mimeType = in.readString();
		this.blockSize = in.readInt();
		this.size = in.readLong();
	}

}
