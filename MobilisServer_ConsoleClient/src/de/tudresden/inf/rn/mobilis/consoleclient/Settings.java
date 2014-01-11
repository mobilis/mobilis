package de.tudresden.inf.rn.mobilis.consoleclient;

import java.io.*;
import java.util.Properties;

/**
 * The Class Settings.
 */
public class Settings {

    private static final String CLIENT_NODE = "clientNode";
    private static final String CLIENT_PASSWORD = "clientPassword";
    private static final String CLIENT_RESOURCE = "clientResource";
    private static final String MOBILIS_COORDINATOR_RESOURCE = "mobilisCoordinatorResource";
    private static final String MOBILIS_DEPLOYMENT_RESOURCE = "mobilisDeploymentResource";
    private static final String MOBILIS_RUNTIME_RESOURCE = "mobilisRuntimeResource";
    private static final String MOBILIS_SERVER_NODE = "mobilisServerNode";
    private static final String MOBILIS_SERVER_RESOURCE = "mobilisServerResource";
    private static final String XMPP_SERVER_ADDRESS = "xmppServerAddress";
    private static final String XMPP_SERVER_PORT = "xmppServerPort";
    private static final String XMPP_SERVER_DOMAIN = "xmppServerDomain";
    private static final String SMACK_DEBUG_MODE = "smackDebugMode";

    /** The client node. */
	private String _clientNode;	
	
	/** The client password. */
	private String _clientPassword;
	
	/** The client resource. */
	private String _clientResource;
	
	/** The mobilis coordinator resource. */
	private String _mobilisCoordinatorResource;
	
	/** The mobilis coordinator resource. */
	private String _mobilisDeploymentResource;
	
	/** The mobilis coordinator resource. */
	private String _mobilisRuntimeResource;
	
	/** The mobilis server node. */
	private String _mobilisServerNode;
	
	/** The mobilis server resource. */
	private String _mobilisServerResource;
	
	/** The xmpp server address. */
	private String _xmppServerAddress;
	
	/** The xmpp server domain. */
	private String _xmppServerDomain;
	
	/** The xmpp server port. */
	private int _xmppServerPort;

    private boolean _smackDebugMode;

    private Properties _properties;
	
	/**
	 * Instantiates a new settings.
	 */
	public Settings(){
		init();
	}


	/**
	 * Inits the default values.
	 */
	private void init() {
		_properties = new Properties();
		try {
			_properties.load(new FileInputStream("Settings.properties"));
		} catch (FileNotFoundException e) {
            File newFile = new File("Settings.properties");
            try {
                if (newFile.createNewFile()) {
                    _properties = new Properties();
                    setupPropertiesFile();
                    storeProperties();
                }
            } catch (IOException e1) {
                System.out.println("Could not create settings file. Allow write access to folder");
                e1.printStackTrace();
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		_clientNode = _properties.getProperty(CLIENT_NODE).trim();
		_clientPassword = _properties.getProperty(CLIENT_PASSWORD).trim();
		_clientResource = _properties.getProperty(CLIENT_RESOURCE).trim();
		
		_mobilisCoordinatorResource = _properties.getProperty(MOBILIS_COORDINATOR_RESOURCE).trim();
		_mobilisDeploymentResource = _properties.getProperty(MOBILIS_DEPLOYMENT_RESOURCE).trim();
		_mobilisRuntimeResource = _properties.getProperty(MOBILIS_RUNTIME_RESOURCE).trim();
		_mobilisServerNode = _properties.getProperty(MOBILIS_SERVER_NODE).trim();
		_mobilisServerResource = _properties.getProperty(MOBILIS_SERVER_RESOURCE).trim();
		
		_xmppServerAddress = _properties.getProperty(XMPP_SERVER_ADDRESS).trim();
		_xmppServerPort = Integer.parseInt(_properties.getProperty(XMPP_SERVER_PORT).trim());
		
		_xmppServerDomain = _properties.getProperty(XMPP_SERVER_DOMAIN).trim();

        _smackDebugMode = Boolean.parseBoolean(_properties.getProperty(SMACK_DEBUG_MODE).trim());
	}
	
	private void setupPropertiesFile() {
        _properties.setProperty(CLIENT_NODE, "");
        _properties.setProperty(CLIENT_PASSWORD, "");
        _properties.setProperty(CLIENT_RESOURCE, "");

        _properties.setProperty(MOBILIS_COORDINATOR_RESOURCE, "Coordinator");
        _properties.setProperty(MOBILIS_DEPLOYMENT_RESOURCE, "Deployment");
        _properties.setProperty(MOBILIS_RUNTIME_RESOURCE, "Runtime");
        _properties.setProperty(MOBILIS_SERVER_NODE, "");
        _properties.setProperty(MOBILIS_SERVER_RESOURCE, "");

        _properties.setProperty(XMPP_SERVER_ADDRESS, "");
        _properties.setProperty(XMPP_SERVER_PORT, "5222");

        _properties.setProperty(XMPP_SERVER_DOMAIN, "");

        _properties.setProperty(SMACK_DEBUG_MODE, "false");
    }

    public boolean allSettingsAvailable() {
        return !"".equals(getXMPPServerAddress());
    }

	/**
	 * Gets the client jid.
	 *
	 * @return the client jid
	 */
	public String getClientJid(){
		return _clientNode
				+ "@"
				+ _xmppServerDomain
				+ null !=_clientResource 
					? ( "/" + _clientResource )
					: "";
	}
	
	/**
	 * Sets the client jid.
	 *
	 * @param jid the new client jid
	 */
	public void setClientJid(String jid){
		_clientNode = jid.substring(0, jid.indexOf("@"));
		_clientResource = jid.substring(jid.indexOf("/"));
	}

	/**
	 * Gets the client node.
	 *
	 * @return the client node
	 */
	public String getClientNode() {
		return _clientNode;
	}


	/**
	 * Sets the client node.
	 *
	 * @param clientNode the new client node
	 */
	public void setClientNode(String clientNode) {
		_clientNode = clientNode;
        saveProperty(CLIENT_NODE, _clientNode);
	}


	/**
	 * Gets the client password.
	 *
	 * @return the _clientPassword
	 */
	public String getClientPassword() {
		return _clientPassword;
	}


	/**
	 * Sets the client password.
	 *
	 * @param clientPassword the new client password
	 */
	public void setClientPassword(String clientPassword) {
		_clientPassword = clientPassword;
        saveProperty(CLIENT_PASSWORD, _clientPassword);
	}


	/**
	 * Gets the client resource.
	 *
	 * @return the _clientResource
	 */
	public String getClientResource() {
		return _clientResource;
	}


	/**
	 * Sets the client resource.
	 *
	 * @param clientResource the new client resource
	 */
	public void setClientResource(String clientResource) {
		_clientResource = clientResource;
        saveProperty(CLIENT_RESOURCE, _clientResource);
	}
	
	
	/**
	 * Gets the mobilis coordinator jid.
	 *
	 * @return the mobilis coordinator jid
	 */
	public String getMobilisCoordinatorJid(){
		return _mobilisServerNode
				+ "@"
				+ _xmppServerDomain
				+ ( null !=_mobilisCoordinatorResource
					? ( "/" + _mobilisCoordinatorResource )
					: "" );
	}
	
	/**
	 * Gets the mobilis admin jid.
	 *
	 * @return the mobilis admin jid
	 */
	public String getMobilisAdminJid(){
		return _mobilisServerNode
				+ "@"
				+ _xmppServerDomain
				+ ( null != _mobilisDeploymentResource
					? ( "/" + _mobilisDeploymentResource)
					: "" );
	}
	
	/**
	 * Gets the mobilis deployment jid.
	 *
	 * @return the mobilis deployment jid
	 */
	public String getMobilisDeploymentJid(){
		return _mobilisServerNode
				+ "@"
				+ _xmppServerDomain
				+ ( null != _mobilisRuntimeResource
					? ( "/" + _mobilisRuntimeResource)
					: "" );
	}	
	
	/**
	 * Gets the mobilis coordinator resource.
	 *
	 * @return the _mobilisCoordinatorResource
	 */
	public String getMobilisCoordinatorResource() {
		return _mobilisCoordinatorResource;
	}


	/**
	 * Sets the mobilis coordinator resource.
	 *
	 * @param mobilisCoordinatorResource the new mobilis coordinator resource
	 */
	public void setMobilisCoordinatorResource(String mobilisCoordinatorResource) {
		_mobilisCoordinatorResource = mobilisCoordinatorResource;
        saveProperty(MOBILIS_COORDINATOR_RESOURCE, _mobilisCoordinatorResource);
	}


	/**
	 * Gets the mobilis server jid.
	 *
	 * @return the mobilis server jid
	 */
	public String getMobilisServerJid(){
		return _mobilisServerNode
				+ "@"
				+ _xmppServerDomain
				+ null !=_mobilisServerResource 
					? ( "/" + _mobilisServerResource )
					: "";
	}
	
	/**
	 * Gets the mobilis server node.
	 *
	 * @return the _serverNode
	 */
	public String getMobilisServerNode() {
		return _mobilisServerNode;
	}


	/**
	 * Sets the mobilis server node.
	 *
	 * @param serverNode the new mobilis server node
	 */
	public void setMobilisServerNode(String serverNode) {
		_mobilisServerNode = serverNode;
        saveProperty(MOBILIS_SERVER_NODE, _mobilisServerNode);
	}
	
	/**
	 * Gets the mobilis server resource.
	 *
	 * @return the _serverResource
	 */
	public String getMobilisServerResource() {
		return _mobilisServerResource;
	}


	/**
	 * Sets the mobilis server resource.
	 *
	 * @param serverResource the new mobilis server resource
	 */
	public void setMobilisServerResource(String serverResource) {
		_mobilisServerResource = serverResource;
        saveProperty(MOBILIS_SERVER_RESOURCE, _mobilisServerResource);
	}
	
	
	/**
	 * Gets the XMPP server address.
	 *
	 * @return the _serverAddress
	 */
	public String getXMPPServerAddress() {
		return _xmppServerAddress;
	}


	/**
	 * Sets the XMPP server address.
	 *
	 * @param serverAddress the new XMPP server address
	 */
	public void setXMPPServerAddress(String serverAddress) {
		_xmppServerAddress = serverAddress;
        saveProperty(XMPP_SERVER_ADDRESS, _xmppServerAddress);
	}


	/**
	 * Gets the XMPP server port.
	 *
	 * @return the _serverPort
	 */
	public int getXMPPServerPort() {
		return _xmppServerPort;
	}


	/**
	 * Sets the XMPP server port.
	 *
	 * @param serverPort the new XMPP server port
	 */
	public void setXMPPServerPort(int serverPort) {
		_xmppServerPort = serverPort;
        saveProperty(XMPP_SERVER_PORT, String.valueOf(_xmppServerPort));
	}


	/**
	 * Gets the XMPP server domain.
	 *
	 * @return the _xmppDomain
	 */
	public String getXMPPServerDomain() {
		return _xmppServerDomain;
	}


	/**
	 * Sets the XMPP server domain.
	 *
	 * @param xmppDomain the new XMPP server domain
	 */
	public void setXMPPServerDomain(String xmppDomain) {
		_xmppServerDomain = xmppDomain;
        saveProperty(XMPP_SERVER_DOMAIN, _xmppServerDomain);
	}
	
	/**
	 * @return the _mobilisDeploymentResource
	 */
	public String getMobilisDeploymentResource() {
		return _mobilisDeploymentResource;
	}


	/**
	 * @param mobilisDeploymentResource the _mobilisDeploymentResource to set
	 */
	public void setMobilisDeploymentResource( String mobilisDeploymentResource ) {
        _mobilisDeploymentResource = mobilisDeploymentResource;
        saveProperty(MOBILIS_DEPLOYMENT_RESOURCE, _mobilisDeploymentResource);
	}

	/**
	 * @return the _mobilisRuntimeResource
	 */
	public String getMobilisRuntimeResource() {
		return _mobilisRuntimeResource;
	}


	/**
	 * @param mobilisRuntimeResource the _mobilisRuntimeResource to set
	 */
	public void setMobilisRuntimeResource( String mobilisRuntimeResource ) {
		_mobilisRuntimeResource = mobilisRuntimeResource;
        saveProperty(MOBILIS_RUNTIME_RESOURCE, _mobilisRuntimeResource);
	}

    public boolean isSmackDebugMode() {
        return _smackDebugMode;
    }

    public void setSmackDebugMode(boolean enabled) {
        _smackDebugMode = enabled;
        saveProperty(SMACK_DEBUG_MODE, Boolean.valueOf(_smackDebugMode).toString());
    }

    private void saveProperty(String key, String value) {
        _properties.setProperty(key, value);
        storeProperties();
    }

    private void storeProperties() {
        try {
            _properties.store(new FileOutputStream("Settings.properties"), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append( "clientNode=" ).append( _clientNode );
		sb.append( "\nclientPassword=" ).append( _clientPassword );
		sb.append( "\nclientResource=" ).append( _clientResource );
		sb.append( "\nmobilisCoordinatorResource=" ).append( _mobilisCoordinatorResource );
		sb.append( "\nmobilisServerNode=" ).append( _mobilisServerNode );
		sb.append( "\nmobilisServerResource=" ).append( _mobilisServerResource );		
		sb.append( "\nxmppServerAddress=" ).append( _xmppServerAddress );
		sb.append( "\nxmppServerPort=" ).append( _xmppServerPort );
		sb.append( "\nxmppServerDomain=" ).append( _xmppServerDomain );

		return sb.toString();
	}
}
