package de.tudresden.inf.rn.mobilis.services.mobilist.proxy;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class GetListEntryRequest extends XMPPBean {

	private static final long serialVersionUID = 8434777084790975777L;
	private String listEntryID = null;


	public GetListEntryRequest( String listEntryID ) {
		super();
		this.listEntryID = listEntryID;

		this.setType( XMPPBean.TYPE_GET );
	}

	public GetListEntryRequest(){
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
				else if (tagName.equals( "listEntryID" ) ) {
					this.listEntryID = parser.nextText();
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

	public static final String CHILD_ELEMENT = "GetListEntryRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilist:iq:getlistentry";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		GetListEntryRequest clone = new GetListEntryRequest( listEntryID );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<listEntryID>" )
			.append( this.listEntryID )
			.append( "</listEntryID>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public GetListEntryRequest buildGetListEntryFault(String detailedErrorText){
		GetListEntryRequest fault = ( GetListEntryRequest )this.clone();

		fault.setTo( this.getFrom() );
    	fault.setId(this.getId());
		fault.setType( XMPPBean.TYPE_ERROR );
		fault.errorType = "modify";
		fault.errorCondition = "not-acceptable";
		fault.errorText = "List entry couldn't be retrieved";

		if(null != detailedErrorText && detailedErrorText.length() > 0)
			fault.errorText += " Detail: " + detailedErrorText;
		
		return fault;
	}





	public String getListEntryID() {
		return this.listEntryID;
	}

	public void setListEntryID( String listEntryID ) {
		this.listEntryID = listEntryID;
	}

}