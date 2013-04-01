package de.tudresden.inf.rn.mobilis.services.mobilist.proxy;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class AddListEntryRequest extends XMPPBean {

	private static final long serialVersionUID = 2827845570829615550L;
	private String listEntryTitle = null;
	private String listEntryContent = null;
	private long listEntryDueDate = Long.MIN_VALUE;


	public AddListEntryRequest( String listEntryTitle, String listEntryContent, long listEntryDueDate ) {
		super();
		this.listEntryTitle = listEntryTitle;
		this.listEntryContent = listEntryContent;
		this.listEntryDueDate = listEntryDueDate;

		this.setType( XMPPBean.TYPE_SET );
	}

	public AddListEntryRequest(){
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
				else if (tagName.equals( "listEntryTitle" ) ) {
					this.listEntryTitle = parser.nextText();
				}
				else if (tagName.equals( "listEntryContent" ) ) {
					this.listEntryContent = parser.nextText();
				}
				else if (tagName.equals( "listEntryDueDate" ) ) {
					this.listEntryDueDate = Long.parseLong( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "AddListEntryRequest";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilist:iq:addlistentry";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		AddListEntryRequest clone = new AddListEntryRequest( listEntryTitle, listEntryContent, listEntryDueDate );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<listEntryTitle>" )
			.append( this.listEntryTitle )
			.append( "</listEntryTitle>" );

		sb.append( "<listEntryContent>" )
			.append( this.listEntryContent )
			.append( "</listEntryContent>" );

		sb.append( "<listEntryDueDate>" )
			.append( this.listEntryDueDate )
			.append( "</listEntryDueDate>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public AddListEntryRequest buildAddListEntryFault(String detailedErrorText){
		AddListEntryRequest fault = ( AddListEntryRequest )this.clone();

		fault.setTo( this.getFrom() );
    	fault.setId(this.getId());
		fault.setType( XMPPBean.TYPE_ERROR );
		fault.errorType = "modify";
		fault.errorCondition = "not-acceptable";
		fault.errorText = "List entry couldn't be added";

		if(null != detailedErrorText && detailedErrorText.length() > 0)
			fault.errorText += " Detail: " + detailedErrorText;
		
		return fault;
	}





	public String getListEntryTitle() {
		return this.listEntryTitle;
	}

	public void setListEntryTitle( String listEntryTitle ) {
		this.listEntryTitle = listEntryTitle;
	}

	public String getListEntryContent() {
		return this.listEntryContent;
	}

	public void setListEntryContent( String listEntryContent ) {
		this.listEntryContent = listEntryContent;
	}

	public long getListEntryDueDate() {
		return this.listEntryDueDate;
	}

	public void setListEntryDueDate( long listEntryDueDate ) {
		this.listEntryDueDate = listEntryDueDate;
	}

}