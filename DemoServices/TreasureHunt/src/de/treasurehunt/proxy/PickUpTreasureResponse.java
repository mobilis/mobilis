package de.treasurehunt.proxy;
import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class PickUpTreasureResponse extends XMPPBean {

	private long TreasureValue = Long.MIN_VALUE;


	public PickUpTreasureResponse( long TreasureValue ) {
		super();
		this.TreasureValue = TreasureValue;

		this.setType( XMPPBean.TYPE_RESULT );
	}

	public PickUpTreasureResponse(){
		this.setType( XMPPBean.TYPE_RESULT );
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
				else if (tagName.equals( "TreasureValue" ) ) {
					this.TreasureValue = Long.parseLong( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "PickUpTreasureResponse";

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
		PickUpTreasureResponse clone = new PickUpTreasureResponse( TreasureValue );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<TreasureValue>" )
			.append( this.TreasureValue )
			.append( "</TreasureValue>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public long getTreasureValue() {
		return this.TreasureValue;
	}

	public void setTreasureValue( long TreasureValue ) {
		this.TreasureValue = TreasureValue;
	}

}