package de.tudresden.inf.rn.mobilis.services.mobilist.proxy;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo;

public class ListEntry implements XMPPInfo {

	private static final long serialVersionUID = -2617105749624929621L;
	private String listEntryID = null;
	private String listEntryTitle = null;
	private String listEntryContent = null;
	private long listEntryDueDate = Long.MIN_VALUE;


	public ListEntry(String listEntryID, String listEntryTitle, String listEntryContent, long listEntryDueDate) {
		super();
		this.listEntryID = listEntryID;
		this.listEntryTitle = listEntryTitle;
		this.listEntryContent = listEntryContent;
		this.listEntryDueDate = listEntryDueDate;
	}
	
	public ListEntry() {}

	@Override
	public void fromXML(XmlPullParser parser) throws Exception {
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
				else if (tagName.equals( "listEntryTitle" ) ) {
					this.listEntryTitle = parser.nextText();
				}
				else if (tagName.equals( "listEntryContent" ) ) {
					this.listEntryContent = parser.nextText();
				}
				else if (tagName.equals( "listEntryDueDate" ) ) {
					this.listEntryDueDate = Long.parseLong( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "ListEntry";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "http://mobilis.inf.tu-dresden.de#services/MobilistService#type:ListEntry";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String toXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<listEntryID>" )
			.append( this.listEntryID )
			.append( "</listEntryID>" );

		sb.append( "<listEntryTitle>" )
			.append( this.listEntryTitle )
			.append( "</listEntryTitle>" );

		sb.append( "<listEntryContent>" )
			.append( this.listEntryContent )
			.append( "</listEntryContent>" );

		sb.append( "<listEntryDueDate>" )
			.append( this.listEntryDueDate )
			.append( "</listEntryDueDate>" );

		return sb.toString();
	}



	public String getListEntryID() {
		return this.listEntryID;
	}

	public void setListEntryID( String listEntryID ) {
		this.listEntryID = listEntryID;
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