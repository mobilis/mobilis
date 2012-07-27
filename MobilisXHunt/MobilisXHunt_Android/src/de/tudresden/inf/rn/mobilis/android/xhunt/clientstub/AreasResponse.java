package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class AreasResponse extends XMPPBean {

	private List< AreaInfo > Areas = new ArrayList< AreaInfo >();


	public AreasResponse( List< AreaInfo > Areas ) {
		super();
		for ( AreaInfo entity : Areas ) {
			this.Areas.add( entity );
		}

		this.setType( XMPPBean.TYPE_RESULT );
	}

	public AreasResponse(){
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
				else if (tagName.equals( AreaInfo.CHILD_ELEMENT ) ) {
					AreaInfo entity = new AreaInfo();

					entity.fromXML( parser );
					this.Areas.add( entity );
					
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

	public static final String CHILD_ELEMENT = "AreasResponse";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:areas";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		AreasResponse clone = new AreasResponse( Areas );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		for( AreaInfo entry : this.Areas ) {
			sb.append( "<" + AreaInfo.CHILD_ELEMENT + ">" );
			sb.append( entry.toXML() );
			sb.append( "</" + AreaInfo.CHILD_ELEMENT + ">" );
		}

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public List< AreaInfo > getAreas() {
		return this.Areas;
	}

	public void setAreas( List< AreaInfo > Areas ) {
		this.Areas = Areas;
	}

}