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

import org.jivesoftware.smack.packet.PacketExtension;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The GeolocItem Parcelable is used after a new XEP-0080 User Location Event
 * has been received.
 * @author Istvan Koren
 */
public class GeolocItem implements PacketExtension, Parcelable {

	public static final String ELEMENT_NAME = "geoloc";
	public static final String NAMESPACE = "http://jabber.org/protocol/geoloc";

	private double accuracy;
	private boolean isAccuracySet = false;
	private double alt;
	private boolean isAltSet = false;
	private String area;
	private double bearing;
	private boolean isBearingSet = false;
	private String building;
	private String country;
	private String countrycode;
	private String datum;
	private String description;
	private double error;
	private boolean isErrorSet = false;
	private String floor;
	private double lat;
	private boolean isLatSet = true;
	private String locality;
	private double lon;
	private boolean isLonSet = false;
	private String postalcode;
	private String region;
	private String room;
	private double speed;
	private boolean isSpeedSet = false;
	private String street;
	private String text;
	private String timestamp;
	private String uri;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jivesoftware.smack.packet.PacketExtension#getElementName()
	 */
	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jivesoftware.smack.packet.PacketExtension#getNamespace()
	 */
	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jivesoftware.smack.packet.PacketExtension#toXML()
	 */
	@Override
	public String toXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append(ELEMENT_NAME).append(" xmlns='").append(NAMESPACE).append("'>");
		
		buf.append("</").append(ELEMENT_NAME).append(">");
		return buf.toString();
	}
	
	public static final Parcelable.Creator<GeolocItem> CREATOR = new Parcelable.Creator<GeolocItem>() {
		public GeolocItem createFromParcel(Parcel in) {
			return new GeolocItem(in);
		}

		public GeolocItem[] newArray(int size) {
			return new GeolocItem[size];
		}
	};
	
	public GeolocItem() {
		
	}
	
	private GeolocItem(Parcel in) {
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
		dest.writeDouble(accuracy);
		dest.writeDouble(alt);
		dest.writeString(area);
		dest.writeDouble(bearing);
		dest.writeString(building);
		dest.writeString(country);
		dest.writeString(countrycode);
		dest.writeString(datum);
		dest.writeString(description);
		dest.writeDouble(error);
		dest.writeString(floor);
		dest.writeDouble(lat);
		dest.writeString(locality);
		dest.writeDouble(lon);
		dest.writeString(postalcode);
		dest.writeString(region);
		dest.writeString(room);
		dest.writeDouble(speed);
		dest.writeString(street);
		dest.writeString(text);
		dest.writeString(timestamp);
		dest.writeString(uri);
		boolean[] setValues = new boolean[7];
		setValues[0] = isAccuracySet;
		setValues[1] = isAltSet;
		setValues[2] = isBearingSet;
		setValues[3] = isErrorSet;
		setValues[4] = isLatSet;
		setValues[5] = isLonSet;
		setValues[6] = isSpeedSet;
		dest.writeBooleanArray(setValues);
	}
	
	public void readFromParcel(Parcel in) {
		accuracy = in.readDouble();
		alt = in.readDouble();
		area = in.readString();
		bearing = in.readDouble();
		building = in.readString();
		country = in.readString();
		countrycode = in.readString();
		datum = in.readString();
		description = in.readString();
		error = in.readDouble();
		floor = in.readString();
		lat = in.readDouble();
		locality = in.readString();
		lon = in.readDouble();
		postalcode = in.readString();
		region = in.readString();
		room = in.readString();
		speed = in.readDouble();
		street = in.readString();
		text = in.readString();
		timestamp = in.readString();
		uri = in.readString();
		boolean[] setValues = new boolean[7];
		in.readBooleanArray(setValues);
		isAccuracySet = setValues[0];
		isAltSet = setValues[1];
		isBearingSet = setValues[2];
		isErrorSet = setValues[3];
		isLatSet = setValues[4];
		isLonSet = setValues[5];
		isSpeedSet = setValues[6];
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
		isAccuracySet = true;
	}

	public double getAlt() {
		return alt;
	}

	public void setAlt(double alt) {
		this.alt = alt;
		isAltSet = true;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public double getBearing() {
		return bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
		isBearingSet = true;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountrycode() {
		return countrycode;
	}

	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}

	public String getDatum() {
		return datum;
	}

	public void setDatum(String datum) {
		this.datum = datum;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
		isErrorSet = true;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
		isLatSet = true;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
		isLonSet = true;
	}

	public String getPostalcode() {
		return postalcode;
	}

	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
		isSpeedSet = true;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
