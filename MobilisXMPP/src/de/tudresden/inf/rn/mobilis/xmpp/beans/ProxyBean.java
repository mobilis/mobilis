package de.tudresden.inf.rn.mobilis.xmpp.beans;

import java.io.StringReader;
import java.util.logging.Level;

import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * The Class ProxyBean to handle multiple versions of an IQ/XMPPBean. This bean
 * encapsulate a bean.
 */
public class ProxyBean extends XMPPBean {

	/** The Constant serialVersionUID for serialization. */
	private static final long serialVersionUID = -6340046177746865456L;

	/** The namespace of the xmpp bean. */
	private String namespace;

	/** The child element of the xmpp bean. */
	private String childElement;

	/** The payload of the xmpp bean. */
	private String payload;

	/**
	 * Instantiates a new proxy bean.
	 * 
	 * @param namespace
	 *            the namespace
	 * @param childElement
	 *            the child element
	 */
	public ProxyBean(String namespace, String childElement) {
		this.namespace = namespace;
		this.childElement = childElement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo#fromXML(org.xmlpull.v1
	 * .XmlPullParser)
	 */
	@Override
	public void fromXML( XmlPullParser parser ) {
		StringBuilder str = new StringBuilder();

		int evtType;
		try {
			evtType = parser.getEventType();

			while ( evtType != XmlPullParser.END_DOCUMENT ) {
				if ( evtType == XmlPullParser.START_TAG ) {
					str.append( "<" ).append( parser.getName() );

					if ( !parser.getName().equals( getChildElement() ) ) {
						str.append( " xmlns=\"" ).append( getNamespace() ).append( "\"" );
					}

					str.append( ">" );
				} else if ( evtType == XmlPullParser.END_TAG ) {
					str.append( "</" ).append( parser.getName() ).append( ">" );

					if ( parser.getName().equals( getChildElement() ) ) {
						break;
					}
				} else if ( evtType == XmlPullParser.TEXT ) {
					str.append( parser.getText() );
				}

				evtType = parser.next();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		this.payload = str.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo#getChildElement()
	 */
	@Override
	public String getChildElement() {
		return this.childElement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPInfo#getNamespace()
	 */
	@Override
	public String getNamespace() {
		return this.namespace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean#clone()
	 */
	@Override
	public XMPPBean clone() {
		ProxyBean bean = new ProxyBean( this.namespace, this.childElement );
		bean.setPayload( this.payload );

		bean.cloneBasicAttributes( this );

		return bean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean#payloadToXML()
	 */
	@Override
	public String payloadToXML() {
		return this.payload;
	}

	/**
	 * Sets the payload.
	 * 
	 * @param payload
	 *            the new payload
	 */
	public void setPayload( String payload ) {
		this.payload = payload;
	}

	/**
	 * Checks if this encapsulated bean is a type of the given namespace and
	 * version.
	 * 
	 * @param namespace
	 *            the namespace
	 * @param childElement
	 *            the child element
	 * @return true, if it is type of
	 */
	public boolean isTypeOf( String namespace, String childElement ) {
		return null != this.getNamespace() && null != this.getChildElement() && null != namespace
				&& null != childElement && this.getNamespace().equals( namespace )
				&& this.getChildElement().equals( childElement );
	}

	/**
	 * Parses the payload of the encapsulated bean into a given bean.
	 * 
	 * @param toBean
	 *            the specialized bean which can handle the payload
	 * @return the xmpp bean with the parsed payload
	 */
	public XMPPBean parsePayload( XMPPBean toBean ) {
		if ( null == toBean )
			return null;

		try {
			XmlPullParser parser = new MXParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
			
			parser.setInput( new StringReader( this.payload ) );
			toBean.fromXML( parser );
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		// clone basic attributes
		this.cloneBasicAttributes( toBean );

		return toBean;
	}

}
