package de.tudresden.inf.rn.mobilis.services.mobilist.proxy;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class GetListEntryResponse extends XMPPBean {

	private static final long serialVersionUID = -5294945562357263000L;
	private ListEntry listEntry = null;


	public GetListEntryResponse(ListEntry listEntry) {
		super();
		this.listEntry = listEntry;

		this.setType( XMPPBean.TYPE_RESULT );
	}

	public GetListEntryResponse(){
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
				else if (tagName.equals( "listEntry" ) ) {
					String id, title, content;
					long dueDate;
					
					parser.next(); // start tag <listEntryID>
					if (parser.getEventType() == XmlPullParser.START_TAG
						&& parser.getName().equals("listEntryID")) {
						id = parser.nextText(); // content of listEntryID
						parser.next(); // end tag </listEntryID>
					} else {
						throw new XmlPullParserException("listEntryID was expected");
					}
					
					parser.next(); // start tag <listEntryTitle>
					if (parser.getEventType() == XmlPullParser.START_TAG
						&& parser.getName().equals("listEntryTitle")) {
						title = parser.nextText(); // content of listEntryTitle
						parser.next(); // end tag </listEntryTitle>
					} else {
						throw new XmlPullParserException("listEntryTitle was expected");
					}
					
					parser.next(); // start tag <listEntryContent>
					if (parser.getEventType() == XmlPullParser.START_TAG
						&& parser.getName().equals("listEntryContent")) {
						content = parser.nextText(); // content of listEntryContent
						parser.next(); // end tag </listEntryContent>
					} else {
						throw new XmlPullParserException("listEntryContent was expected");
					}
					
					parser.next(); // start tag <listEntryDueDate>
					if (parser.getEventType() == XmlPullParser.START_TAG
						&& parser.getName().equals("listEntryDueDate")) {
						dueDate = Long.parseLong(parser.nextText()); // content of listEntryDueDate
						parser.next(); // end tag </listEntryDueDate>
					} else {
						throw new XmlPullParserException("listEntryDueDate was expected");
					}
					
					this.listEntry = new ListEntry(id, title, content, dueDate);
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

	public static final String CHILD_ELEMENT = "GetListEntryResponse";

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
		GetListEntryResponse clone = new GetListEntryResponse( listEntry );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<listEntry>" )
			.append( this.listEntry )
			.append( "</listEntry>" );

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public ListEntry getListEntry() {
		return this.listEntry;
	}

	public void setListEntry(ListEntry listEntry ) {
		this.listEntry = listEntry;
	}

}