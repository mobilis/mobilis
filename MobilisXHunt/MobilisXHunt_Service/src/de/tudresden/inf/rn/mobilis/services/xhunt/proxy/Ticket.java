package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

public class Ticket implements XMPPInfo {

	private int ID = Integer.MIN_VALUE;
	private String Name = null;


	public Ticket( int ID, String Name ) {
		super();
		this.ID = ID;
		this.Name = Name;
	}

	public Ticket(){}



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
				else if (tagName.equals( "ID" ) ) {
					this.ID = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "Name" ) ) {
					this.Name = parser.nextText();
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

	public static final String CHILD_ELEMENT = "Ticket";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "http://mobilis.inf.tu-dresden.de#services/MobilisXHuntService#type:Ticket";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<ID>" )
			.append( this.ID )
			.append( "</ID>" );

		sb.append( "<Name>" )
			.append( this.Name )
			.append( "</Name>" );

		return sb.toString();
	}



	public int getID() {
		return this.ID;
	}

	public void setID( int ID ) {
		this.ID = ID;
	}

	public String getName() {
		return this.Name;
	}

	public void setName( String Name ) {
		this.Name = Name;
	}

}