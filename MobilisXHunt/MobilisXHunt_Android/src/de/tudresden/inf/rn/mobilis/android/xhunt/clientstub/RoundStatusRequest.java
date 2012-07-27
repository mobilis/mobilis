package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class RoundStatusRequest extends XMPPBean {

	private int Round = Integer.MIN_VALUE;
	private List< RoundStatusInfo > RoundStatusInfos = new ArrayList< RoundStatusInfo >();


	public RoundStatusRequest( int Round, List< RoundStatusInfo > RoundStatusInfos ) {
		super();
		this.Round = Round;
		for ( RoundStatusInfo entity : RoundStatusInfos ) {
			this.RoundStatusInfos.add( entity );
		}

		this.setType( XMPPBean.TYPE_SET );
	}

	public RoundStatusRequest(){
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
				else if (tagName.equals( "Round" ) ) {
					this.Round = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( RoundStatusInfo.CHILD_ELEMENT ) ) {
					RoundStatusInfo entity = new RoundStatusInfo();

					entity.fromXML( parser );
					this.RoundStatusInfos.add( entity );
					
					parser.next();
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

	public static final String CHILD_ELEMENT = "RoundStatusRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:roundstatus";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		RoundStatusRequest clone = new RoundStatusRequest( Round, RoundStatusInfos );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<Round>" )
			.append( this.Round )
			.append( "</Round>" );

		for( RoundStatusInfo entry : this.RoundStatusInfos ) {
			sb.append( "<" + RoundStatusInfo.CHILD_ELEMENT + ">" );
			sb.append( entry.toXML() );
			sb.append( "</" + RoundStatusInfo.CHILD_ELEMENT + ">" );
		}

		sb = appendErrorPayload(sb);

		return sb.toString();
	}





	public int getRound() {
		return this.Round;
	}

	public void setRound( int Round ) {
		this.Round = Round;
	}

	public List< RoundStatusInfo > getRoundStatusInfos() {
		return this.RoundStatusInfos;
	}

	public void setRoundStatusInfos( List< RoundStatusInfo > RoundStatusInfos ) {
		this.RoundStatusInfos = RoundStatusInfos;
	}

}