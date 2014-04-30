package de.tudresden.inf.rn.mobilis.server.deployment.helper;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author cmdaltent
 */
public interface IFFReader {

    /** The xml tag for the service element. */
    static final String MSDL_ELEMENT_SERVICE = "msdl:service";

    /** The xml tag for the service dependency element. */
    static final String MSDL_ELEMENT_SERVICE_DEPENDENCY = "msdl:svcdep";

    /** The xml tag for the service namespace element. */
    static final String MSDL_ATTRIBUTE_SERVICE_NAMESPACE = "ident";

    /** The xml tag for the service version element. */
    static final String MSDL_ATTRIBUTE_SERVICE_VERSION = "version";

    /** The xml tag for the service name element. */
    static final String MSDL_ATTRIBUTE_SERVICE_NAME = "name";

    /** The xml tag for the service dependency min versionelement. */
    static final String MSDL_ATTRIBUTE_SERVICE_DEPENDENCY_MINVERSION = "minVersion";

    /** The xml tag for the service dependency max versionelement. */
    static final String MSDL_ATTRIBUTE_SERVICE_DEPENDENCY_MAXVERSION = "maxVersion";

    /** The xml tag for the service namespace element. */
    static final String XPD_SERVICE_NAMESPACE = "ident";

    /** The xml tag for the service version element. */
    static final String XPD_SERVICE_VERSION = "version";

    /** The xml tag for the service name element. */
    static final String XPD_SERVICE_NAME = "name";


    /**
     * Gets the service version.
     *
     * @param msdlFile
     *            the msdl file which should be queried
     * @return the service version
     */
    public int getServiceVersion( File msdlFile );

    /**
     * Gets the service namespace.
     *
     * @param msdlFile
     *            the msdl file which should be queried
     * @return the service namespace
     */
    public String getServiceNamespace( File msdlFile );

    /**
     * Gets the service name.
     *
     * @param msdlFile
     *            the msdl file which should be queried
     * @return the service name
     */
    public String getServiceName( File msdlFile );

    /**
     * Gets the service dependencies.
     *
     * @param msdlFile
     *            the msdl file which should be queried
     * @return the service dependencies
     */
    public List< ServiceDependency > getServiceDependencies( File msdlFile );

    /**
     * Gets an XML attribute from xml.
     *
     * @param uri
     *            the uri path to xml based file
     * @param keyNode
     *            the tag in a xml which should be queried
     * @param keyAttribute
     *            the attribute name which should be queried
     * @return the XML attribute
     * @throws javax.xml.parsers.ParserConfigurationException
     *             Signals that an parser configuration exception has occurred.
     * @throws org.xml.sax.SAXException
     *             Signals that an SAX exception has occurred.
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    public String getXMLAttribute( String uri, final String keyNode, final String keyAttribute )
            throws ParserConfigurationException, SAXException, IOException;

    /**
     * Gets an XML attribute from xml.
     *
     * @param uri
     *            the uri path to xml based file
     * @param keyNode
     *            the tag in a xml which should be queried
     * @return the XML attribute
     * @throws javax.xml.parsers.ParserConfigurationException
     *             Signals that an parser configuration exception has occurred.
     * @throws org.xml.sax.SAXException
     *             Signals that an SAX exception has occurred.
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    public String getXMLAttribute( String uri, final String keyNode)
            throws ParserConfigurationException, SAXException, IOException;

    /**
     * The Class ServiceDependency to summarize service dependency information.
     */
    public static class ServiceDependency {

        /** The namespace of the service dependendy. */
        private String _serviceNameSpace = "";

        /** The max version of the service dependendy. */
        private int _serviceMaxVersion = -1;

        /** The min version of the service dependendy. */
        private int _serviceMinVersion = -1;

        /**
         * Instantiates a new service dependency.
         */
        public ServiceDependency() {
        }

        /**
         * Instantiates a new service dependency.
         *
         * @param serviceNameSpace
         *            the service namespace
         * @param serviceMaxVersion
         *            the service max version
         * @param serviceMinVesion
         *            the service min vesion
         */
        public ServiceDependency(String serviceNameSpace, int serviceMaxVersion,
                                 int serviceMinVesion) {
            super();
            _serviceNameSpace = serviceNameSpace;
            _serviceMaxVersion = serviceMaxVersion;
            _serviceMinVersion = serviceMinVesion;
        }

        /**
         * Gets the service namespace.
         *
         * @return the service namespace
         */
        public String getServiceNameSpace() {
            return _serviceNameSpace;
        }

        /**
         * Sets the service namespace.
         *
         * @param _serviceNameSpace
         *            the namespace to set
         */
        public void setServiceNameSpace( String _serviceNameSpace ) {
            this._serviceNameSpace = _serviceNameSpace;
        }

        /**
         * Gets the service max version.
         *
         * @return the service max version
         */
        public int getServiceMaxVersion() {
            return _serviceMaxVersion;
        }

        /**
         * Sets the service max version.
         *
         * @param _serviceMaxVersion
         *            the service max version to set
         */
        public void setServiceMaxVersion( int _serviceMaxVersion ) {
            this._serviceMaxVersion = _serviceMaxVersion;
        }

        /**
         * Gets the service min version.
         *
         * @return the service min version
         */
        public int getServiceMinVersion() {
            return _serviceMinVersion;
        }

        /**
         * Sets the service min version.
         *
         * @param _serviceMinVersion
         *            the service min vesion to set
         */
        public void setServiceMinVersion( int _serviceMinVersion ) {
            this._serviceMinVersion = _serviceMinVersion;
        }
    }
}
