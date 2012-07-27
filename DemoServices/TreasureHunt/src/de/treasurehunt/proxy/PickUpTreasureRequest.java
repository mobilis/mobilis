package de.treasurehunt.proxy;
import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class PickUpTreasureRequest extends XMPPBean {

	private Location TreasureLocation = new Location();


	public PickUpTreasureRequest( Location TreasureLocation ) {
		super();
		this.TreasureLocation = TreasureLocation;

		this.setType( XMPPBean.TYPE_SET );
	}

	public PickUpTreasureRequest(){
		this.setType( XMPPBean.TYPE_SET );
	}


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
				else if (tagName.equals( Location.CHILD_ELEMENT ) ) {
					this.TreasureLocation.fromXML( parser );
				}
				else if (tagName.equals("error")) {
					parser = parseErrorAttributes(parser);
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

	public static final String CHILD_ELEMENT = "PickUpTreasureRequest";

	@Override
	public String getChildElement() {
		return this.CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "treasurehunt:iq:pickuptreasure";

	@Override
	public String getNamespace() {
		return this.NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		PickUpTreasureRequest clone = new PickUpTreasureRequest( TreasureLocation );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<" + this.TreasureLocation.getChildElement() + ">" )
			.append( this.TreasureLocation.toXML() )
			.append( "</" + this.TreasureLocation.getChildElement() + ">" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public PickUpTreasureRequest buildPickUpFault(String detailedErrorText){
		PickUpTreasureRequest fault = ( PickUpTreasureRequest )this.clone();

		fault.setTo( this.getFrom() );
		fault.setType( XMPPBean.TYPE_ERROR );
		fault.errorType = "modify";
		fault.errorCondition = "not-acceptable";
		fault.errorText = "This Treasure is not valid";

		if(null != detailedErrorText && detailedErrorText.length() > 0)
			fault.errorText += " Detail: " + detailedErrorText;
		
		return fault;
	}





	public Location getTreasureLocation() {
		return this.TreasureLocation;
	}

	public void setTreasureLocation( Location TreasureLocation ) {
		this.TreasureLocation = TreasureLocation;
	}

}