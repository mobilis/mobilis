package de.tudresden.inf.rn.mobilis.mxa.services.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Sven Bendel
 */
public class ByteStream implements Parcelable {
	
	public String from;
	public String to;

	public static final Parcelable.Creator<ByteStream> CREATOR = new Parcelable.Creator<ByteStream>() {
		public ByteStream createFromParcel(Parcel in) {
			return new ByteStream(in);
		}

		public ByteStream[] newArray(int size) {
			return new ByteStream[size];
		}
	};
	
	public ByteStream() {
	}
	
	public ByteStream(String from, String to) {
		this.from = from;
		this.to = to;
	}
	
	private ByteStream(Parcel in) {
		readFromParcel(in);
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.from);
		dest.writeString(this.to);
	}
	
	public void readFromParcel(Parcel in) {
		this.from = in.readString();
		this.to = in.readString();
	}

}
