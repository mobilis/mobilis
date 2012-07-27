package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class TargetRequest extends XMPPBean {

	private int StationId = Integer.MIN_VALUE;
	private int Round = Integer.MIN_VALUE;
	private int TicketId = Integer.MIN_VALUE;
	private boolean IsFinal = false;


	public TargetRequest( int StationId, int Round, int TicketId, boolean IsFinal ) {
		super();
		this.StationId = StationId;
		this.Round = Round;
		this.TicketId = TicketId;
		this.IsFinal = IsFinal;

		this.setType( XMPPBean.TYPE_SET );
	}

	public TargetRequest(){
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
				else if (tagName.equals( "StationId" ) ) {
					this.StationId = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "Round" ) ) {
					this.Round = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "TicketId" ) ) {
					this.TicketId = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "IsFinal" ) ) {
					this.IsFinal = Boolean.parseBoolean( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "TargetRequest";

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
		TargetRequest clone = new TargetRequest( StationId, Round, TicketId, IsFinal );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<StationId>" )
			.append( this.StationId )
			.append( "</StationId>" );

		sb.append( "<Round>" )
			.append( this.Round )
			.append( "</Round>" );

		sb.append( "<TicketId>" )
			.append( this.TicketId )
			.append( "</TicketId>" );

		sb.append( "<IsFinal>" )
			.append( this.IsFinal )
			.append( "</IsFinal>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public int getStationId() {
		return this.StationId;
	}

	public void setStationId( int StationId ) {
		this.StationId = StationId;
	}

	public int getRound() {
		return this.Round;
	}

	public void setRound( int Round ) {
		this.Round = Round;
	}

	public int getTicketId() {
		return this.TicketId;
	}

	public void setTicketId( int TicketId ) {
		this.TicketId = TicketId;
	}

	public boolean getIsFinal() {
		return this.IsFinal;
	}

	public void setIsFinal( boolean IsFinal ) {
		this.IsFinal = IsFinal;
	}

}