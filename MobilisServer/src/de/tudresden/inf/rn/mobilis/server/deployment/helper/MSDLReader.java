package de.tudresden.inf.rn.mobilis.server.deployment.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.tudresden.inf.rn.mobilis.server.MobilisManager;

/**
 * The Class MSDLReader to query information of a MSDL file.
 */
public class MSDLReader implements IFFReader {

    @Override
    public int getServiceVersion( File msdlFile ) {
        int entity = -1;

        try {
            entity = Integer.parseInt( getXMLAttribute( msdlFile.getAbsolutePath(),
                    MSDL_ELEMENT_SERVICE, MSDL_ATTRIBUTE_SERVICE_VERSION ) );
        } catch ( Exception e ) {
            MobilisManager.getLogger().log(
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
            entity = getXMLAttribute( msdlFile.getAbsolutePath(), MSDL_ELEMENT_SERVICE,
                    MSDL_ATTRIBUTE_SERVICE_NAMESPACE );
        } catch ( Exception e ) {
            MobilisManager.getLogger().log(
                    Level.WARNING,
                    String.format( "Exception while reading the service namespace from msdl: %s",
                            e.getMessage() ) );
        }

        return entity;
    }

    @Override
    public String getServiceName( File msdlFile ) {
        String entity = null;

        try {
            entity = getXMLAttribute( msdlFile.getAbsolutePath(), MSDL_ELEMENT_SERVICE,
                    MSDL_ATTRIBUTE_SERVICE_NAME );
        } catch ( Exception e ) {
            MobilisManager.getLogger().log(
                    Level.WARNING,
                    String.format( "Exception while reading the service name from msdl: %s",
                            e.getMessage() ) );
        }

        return entity;
    }

    @Override
    public List< ServiceDependency > getServiceDependencies( File msdlFile ) {
        List< ServiceDependency > resultList = new ArrayList< MSDLReader.ServiceDependency >();

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            SAXServiceDependencyHandler saxHandler = new MSDLReader.SAXServiceDependencyHandler();

            saxParser.parse( msdlFile.getAbsolutePath(), saxHandler );

            resultList = saxHandler.getResultList();
        } catch ( Exception e ) {
            MobilisManager.getLogger().log(
                    Level.WARNING,
                    String.format(
                            "Exception while reading the service dependencies from msdl: %s",
                            e.getMessage() ) );
        }

        return resultList;
    }

    @Override
    public String getXMLAttribute( String uri, final String keyNode,
                                          final String keyAttribute ) throws ParserConfigurationException, SAXException,
            IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        SAXEntityHandler saxHandler = new MSDLReader.SAXEntityHandler( keyNode, keyAttribute );

        saxParser.parse( uri, saxHandler );

        return saxHandler.getResultValue();
    }

    @Override
    public String getXMLAttribute(String uri, String keyNode) throws ParserConfigurationException, SAXException, IOException {
        return null;
    }

    /**
     * The Class SAXEntityHandler to look for entities in a xml-based file.
     */
    private static class SAXEntityHandler extends DefaultHandler {

        /** True if a node was found. */
        private boolean _nodeFound = false;

        /** The key of the node. */
        private String _nodeKey = "";

        /** The key of the attribute. */
        private String _attributeKey = "";

        /** The result value of key node and attribute. */
        private String _resultValue = null;

        /**
         * Instantiates a new SAX entity handler.
         *
         * @param keyNode
         *            the key node after which is looking for
         * @param keyAttribute
         *            the key attribute after which is looking for
         */
        public SAXEntityHandler(String keyNode, String keyAttribute) {
            _nodeKey = keyNode;
            _attributeKey = keyAttribute;
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
                _resultValue = attributes.getValue( _attributeKey );
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
            if ( _nodeFound && null == _resultValue ) {
                _nodeFound = false;
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
        private List< ServiceDependency > _resultList = new ArrayList< MSDLReader.ServiceDependency >();

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

                String namespace = attributes.getValue( MSDL_ATTRIBUTE_SERVICE_NAMESPACE );
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