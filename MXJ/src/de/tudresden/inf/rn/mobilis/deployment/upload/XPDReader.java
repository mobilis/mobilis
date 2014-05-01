package de.tudresden.inf.rn.mobilis.deployment.upload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import de.tudresden.inf.rn.mobilis.MobilisLogger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The Class XPDReader to query information of a MSDL file.
 */
public class XPDReader implements IFFReader {

    @Override
	public int getServiceVersion( File msdlFile ) {
		int entity = -1;

		try {
			entity = Integer.parseInt( getXMLAttribute( msdlFile.getAbsolutePath(), XPD_SERVICE_VERSION) );
		} catch ( Exception e ) {
            MobilisLogger.getLogger().log(
					Level.WARNING,
					String.format( "Exception while reading the service verion from msdl: %s",
							e.getMessage() ) );
		}

		return entity;
	}

	@Override
	public String getServiceNamespace( File msdlFile ) {
		String entity = null;

		try {
			entity = getXMLAttribute( msdlFile.getAbsolutePath(), XPD_SERVICE_NAMESPACE);
		} catch ( Exception e ) {
            MobilisLogger.getLogger().log(
					Level.WARNING,
					String.format( "Exception while reading the service namespace from xpd: %s",
							e.getMessage() ) );
		}

		return entity;
	}

    @Override
	public String getServiceName( File msdlFile ) {
		String entity = null;

		try {
			entity = getXMLAttribute( msdlFile.getAbsolutePath(), XPD_SERVICE_NAME);
		} catch ( Exception e ) {
            MobilisLogger.getLogger().log(
					Level.WARNING,
					String.format( "Exception while reading the service name from msdl: %s",
							e.getMessage() ) );
		}

		return entity;
	}

	@Override
	public List< ServiceDependency > getServiceDependencies( File msdlFile ) {
		List< ServiceDependency > resultList = new ArrayList< XPDReader.ServiceDependency >();

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			SAXServiceDependencyHandler saxHandler = new XPDReader.SAXServiceDependencyHandler();

			saxParser.parse( msdlFile.getAbsolutePath(), saxHandler );

			resultList = saxHandler.getResultList();
		} catch ( Exception e ) {
            MobilisLogger.getLogger().log(
					Level.WARNING,
					String.format(
							"Exception while reading the service dependencies from msdl: %s",
							e.getMessage() ) );
		}

		return resultList;
	}

    @Override
    public String getXMLAttribute(String uri, String keyNode, String keyAttribute) throws ParserConfigurationException, SAXException, IOException {
        return null;
    }

    /**
	 * Gets an XML attribute from xml.
	 * 
	 * @param uri
	 *            the uri path to xml based file
	 * @param keyNode
	 *            the tag in a xml which should be queried
	 * @return the XML attribute
	 * @throws ParserConfigurationException
	 *             Signals that an parser configuration exception has occurred.
	 * @throws SAXException
	 *             Signals that an SAX exception has occurred.
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public String getXMLAttribute( String uri, final String keyNode)
            throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		SAXEntityHandler saxHandler = new XPDReader.SAXEntityHandler(keyNode);

		saxParser.parse( uri, saxHandler );

		return saxHandler.getResultValue();
	}

	/**
	 * The Class SAXEntityHandler to look for entities in a xml-based file.
	 */
	private static class SAXEntityHandler extends DefaultHandler {

		/** True if a node was found. */
		private boolean _nodeFound = false;

		/** The key of the node. */
		private String _nodeKey = "";

		/** The result value of key node and attribute. */
		private String _resultValue = null;

		/**
		 * Instantiates a new SAX entity handler.
		 * 
		 * @param keyNode
		 *            the key node after which is looking for
		 */
		public SAXEntityHandler(String keyNode) {
			_nodeKey = keyNode;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement( String uri, String localName, String qName, Attributes attributes )
				throws SAXException {

			if ( qName.equalsIgnoreCase( _nodeKey ) ) {
				_nodeFound = true;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
		 * java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement( String uri, String localName, String qName ) throws SAXException {
            _nodeFound = false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
		 */
		@Override
		public void characters( char ch[], int start, int length ) throws SAXException {
			if ( _nodeFound && null == _resultValue ) { // FIXME StringBuilder reading characters might be required.
				_resultValue = new String( ch, start, length );
			}

		}

		/**
		 * Gets the result value.
		 * 
		 * @return the resultValue
		 */
		public String getResultValue() {
			return _resultValue;
		}
	}

	/**
	 * The Class SAXServiceDependencyHandler to look for service dependencies in
	 * a msdl file.
	 */
	private static class SAXServiceDependencyHandler extends DefaultHandler {

		/** The result list with found dependencies. */
		private List< ServiceDependency > _resultList = new ArrayList< XPDReader.ServiceDependency >();

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement( String uri, String localName, String qName, Attributes attributes )
				throws SAXException {

			if ( qName.equalsIgnoreCase( MSDL_ELEMENT_SERVICE_DEPENDENCY ) ) {
				ServiceDependency serviceDependency = new ServiceDependency();

				String namespace = attributes.getValue(XPD_SERVICE_NAMESPACE);
				String minVersion = attributes
						.getValue( MSDL_ATTRIBUTE_SERVICE_DEPENDENCY_MINVERSION );
				String maxVersion = attributes
						.getValue( MSDL_ATTRIBUTE_SERVICE_DEPENDENCY_MAXVERSION );

				if ( null != namespace ) {
					serviceDependency.setServiceNameSpace( namespace );
				}

				if ( null != minVersion ) {
					try {
						serviceDependency.setServiceMinVersion( Integer.parseInt( minVersion ) );
					} catch ( NumberFormatException e ) {
					}
				}

				if ( null != maxVersion ) {
					try {
						serviceDependency.setServiceMaxVersion( Integer.parseInt( maxVersion ) );
					} catch ( NumberFormatException e ) {
					}
				}

				if ( null != serviceDependency.getServiceNameSpace()
						&& serviceDependency.getServiceNameSpace().length() > 0 )
					_resultList.add( serviceDependency );
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
		 * java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement( String uri, String localName, String qName ) throws SAXException {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
		 */
		@Override
		public void characters( char ch[], int start, int length ) throws SAXException {
		}

		/**
		 * Gets the result list.
		 * 
		 * @return the resultValue
		 */
		public List< ServiceDependency > getResultList() {
			return _resultList;
		}
	}
}
