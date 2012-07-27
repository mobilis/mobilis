package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

public class DepartureInfo implements XMPPInfo {

	private int VehicleId = Integer.MIN_VALUE;
	private String VehicleName = null;
	private String Direction = null;
	private String TimeLeft = null;


	public DepartureInfo( int VehicleId, String VehicleName, String Direction, String TimeLeft ) {
		super();
		this.VehicleId = VehicleId;
		this.VehicleName = VehicleName;
		this.Direction = Direction;
		this.TimeLeft = TimeLeft;
	}

	public DepartureInfo(){}



	@Override
	public void fromXML( XmlPullParser parser ) throws Exception {
		boolean done = false;
			
		do {
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				
				if (tagName.equals(getChildElement())) {
					parser.next();
				}
				else if (tagName.equals( "VehicleId" ) ) {
					this.VehicleId = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "VehicleName" ) ) {
					this.VehicleName = parser.nextText();
				}
				else if (tagName.equals( "Direction" ) ) {
					this.Direction = parser.nextText();
				}
				else if (tagName.equals( "TimeLeft" ) ) {
					this.TimeLeft = parser.nextText();
				}
				else
					parser.next();
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals(getChildElement()))
					done = true;
				else
					parser.next();
				break;
			case XmlPullParser.END_DOCUMENT:
				done = true;
				break;
			default:
				parser.next();
			}
		} while (!done);
	}

	public static final String CHILD_ELEMENT = "DepartureInfo";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "http://mobilis.inf.tu-dresden.de#services/MobilisXHuntService#type:DepartureInfo";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<VehicleId>" )
			.append( this.VehicleId )
			.append( "</VehicleId>" );

		sb.append( "<VehicleName>" )
			.append( this.VehicleName )
			.append( "</VehicleName>" );

		sb.append( "<Direction>" )
			.append( this.Direction )
			.append( "</Direction>" );

		sb.append( "<TimeLeft>" )
			.append( this.TimeLeft )
			.append( "</TimeLeft>" );

		return sb.toString();
	}



	public int getVehicleId() {
		return this.VehicleId;
	}

	public void setVehicleId( int VehicleId ) {
		this.VehicleId = VehicleId;
	}

	public String getVehicleName() {
		return this.VehicleName;
	}

	public void setVehicleName( String VehicleName ) {
		this.VehicleName = VehicleName;
	}

	public String getDirection() {
		return this.Direction;
	}

	public void setDirection( String Direction ) {
		this.Direction = Direction;
	}

	public String getTimeLeft() {
		return this.TimeLeft;
	}

	public void setTimeLeft( String TimeLeft ) {
		this.TimeLeft = TimeLeft;
	}

}