package de.treasurehunt.proxy;
import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class TreasureCollected extends XMPPBean {

	private String PlayerName = null;
	private Location TreasureLocation = new Location();
	private long TreasureValue = Long.MIN_VALUE;


	public TreasureCollected( String PlayerName, Location TreasureLocation, long TreasureValue ) {
		super();
		this.PlayerName = PlayerName;
		this.TreasureLocation = TreasureLocation;
		this.TreasureValue = TreasureValue;

		this.setType( XMPPBean.TYPE_GET );
	}

	public TreasureCollected(){
		this.setType( XMPPBean.TYPE_GET );
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
				else if (tagName.equals( "PlayerName" ) ) {
					this.PlayerName = parser.nextText();
				}
				else if (tagName.equals( Location.CHILD_ELEMENT ) ) {
					this.TreasureLocation.fromXML( parser );
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

	public static final String CHILD_ELEMENT = "TreasureCollected";

	@Override
	public String getChildElement() {
		return this.CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "treasurehunt:iq:treasurecollected";

	@Override
	public String getNamespace() {
		return this.NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		TreasureCollected clone = new TreasureCollected( PlayerName, TreasureLocation, TreasureValue );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<PlayerName>" )
			.append( this.PlayerName )
			.append( "</PlayerName>" );

		sb.append( "<" + this.TreasureLocation.getChildElement() + ">" )
			.append( this.TreasureLocation.toXML() )
			.append( "</" + this.TreasureLocation.getChildElement() + ">" );

		sb.append( "<TreasureValue>" )
			.append( this.TreasureValue )
			.append( "</TreasureValue>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public String getPlayerName() {
		return this.PlayerName;
	}

	public void setPlayerName( String PlayerName ) {
		this.PlayerName = PlayerName;
	}

	public Location getTreasureLocation() {
		return this.TreasureLocation;
	}

	public void setTreasureLocation( Location TreasureLocation ) {
		this.TreasureLocation = TreasureLocation;
	}

	public long getTreasureValue() {
		return this.TreasureValue;
	}

	public void setTreasureValue( long TreasureValue ) {
		this.TreasureValue = TreasureValue;
	}

}