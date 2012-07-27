package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class TargetResponse extends XMPPBean {

	private int TicketId = Integer.MIN_VALUE;


	public TargetResponse( int TicketId ) {
		super();
		this.TicketId = TicketId;

		this.setType( XMPPBean.TYPE_RESULT );
	}

	public TargetResponse(){
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
				else if (tagName.equals( "TicketId" ) ) {
					this.TicketId = Integer.parseInt( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "TargetResponse";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:target";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		TargetResponse clone = new TargetResponse( TicketId );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<TicketId>" )
			.append( this.TicketId )
			.append( "</TicketId>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public int getTicketId() {
		return this.TicketId;
	}

	public void setTicketId( int TicketId ) {
		this.TicketId = TicketId;
	}

}