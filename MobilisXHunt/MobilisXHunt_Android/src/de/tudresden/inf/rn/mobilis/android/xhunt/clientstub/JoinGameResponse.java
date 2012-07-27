package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class JoinGameResponse extends XMPPBean {

	private String ChatRoom = null;
	private String ChatPassword = null;
	private int StartTimer = Integer.MIN_VALUE;
	private List< String > IncomingGameFileNames = new ArrayList< String >();


	public JoinGameResponse( String ChatRoom, String ChatPassword, int StartTimer, List< String > IncomingGameFileNames ) {
		super();
		this.ChatRoom = ChatRoom;
		this.ChatPassword = ChatPassword;
		this.StartTimer = StartTimer;
		for ( String entity : IncomingGameFileNames ) {
			this.IncomingGameFileNames.add( entity );
		}

		this.setType( XMPPBean.TYPE_RESULT );
	}

	public JoinGameResponse(){
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
				else if (tagName.equals( "ChatRoom" ) ) {
					this.ChatRoom = parser.nextText();
				}
				else if (tagName.equals( "ChatPassword" ) ) {
					this.ChatPassword = parser.nextText();
				}
				else if (tagName.equals( "StartTimer" ) ) {
					this.StartTimer = Integer.parseInt( parser.nextText() );
				}
				else if (tagName.equals( "IncomingGameFileNames" ) ) {
					IncomingGameFileNames.add( parser.nextText() );
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

	public static final String CHILD_ELEMENT = "JoinGameResponse";

	@Override
	public String getChildElement() {
		return CHILD_ELEMENT;
	}

	public static final String NAMESPACE = "mobilisxhunt:iq:joingame";

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public XMPPBean clone() {
		JoinGameResponse clone = new JoinGameResponse( ChatRoom, ChatPassword, StartTimer, IncomingGameFileNames );
		clone.cloneBasicAttributes( clone );

		return clone;
	}

	@Override
	public String payloadToXML() {
		StringBuilder sb = new StringBuilder();

		sb.append( "<ChatRoom>" )
			.append( this.ChatRoom )
			.append( "</ChatRoom>" );

		sb.append( "<ChatPassword>" )
			.append( this.ChatPassword )
			.append( "</ChatPassword>" );

		sb.append( "<StartTimer>" )
			.append( this.StartTimer )
			.append( "</StartTimer>" );

		for( String entry : this.IncomingGameFileNames ) {
			sb.append( "<IncomingGameFileNames>" );
			sb.append( entry );
			sb.append( "</IncomingGameFileNames>" );
		}

		sb = appendErrorPayload(sb);

		return sb.toString();
	}


	public String getChatRoom() {
		return this.ChatRoom;
	}

	public void setChatRoom( String ChatRoom ) {
		this.ChatRoom = ChatRoom;
	}

	public String getChatPassword() {
		return this.ChatPassword;
	}

	public void setChatPassword( String ChatPassword ) {
		this.ChatPassword = ChatPassword;
	}

	public int getStartTimer() {
		return this.StartTimer;
	}

	public void setStartTimer( int StartTimer ) {
		this.StartTimer = StartTimer;
	}

	public List< String > getIncomingGameFileNames() {
		return this.IncomingGameFileNames;
	}

	public void setIncomingGameFileNames( List< String > IncomingGameFileNames ) {
		this.IncomingGameFileNames = IncomingGameFileNames;
	}

}