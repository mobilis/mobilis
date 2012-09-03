package de.tudresden.inf.rn.mobilis.consoleclient;

/**
 * The Class Settings.
 */
public class Settings {
	
	/** The client node. */
	private String _clientNode;	
	
	/** The client password. */
	private String _clientPassword;
	
	/** The client resource. */
	private String _clientResource;
	
	/** The mobilis coordinator resource. */
	private String _mobilisCoordinatorResource;
	
	/** The mobilis coordinator resource. */
	private String _mobilisAdminResource;
	
	/** The mobilis coordinator resource. */
	private String _mobilisDeploymentResource;
	
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
		_clientNode = "mobilis";
		_clientPassword = "54321#pca";
		_clientResource = "JavaClient";		
		
		_mobilisCoordinatorResource = "Coordinator";
		_mobilisAdminResource = "Admin";
		_mobilisDeploymentResource = "Deployment";
		_mobilisServerNode = "mobilis";
		_mobilisServerResource = "Smack";
		
		_xmppServerAddress = "mobilis.inf.tu-dresden.de";
		_xmppServerPort = 5222;				
		
		_xmppServerDomain = "mobilis.inf.tu-dresden.de";
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
				+ ( null !=_mobilisAdminResource
					? ( "/" + _mobilisAdminResource )
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
				+ ( null !=_mobilisDeploymentResource
					? ( "/" + _mobilisDeploymentResource )
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
	}
	
	/**
	 * @return the _mobilisAdminResource
	 */
	public String getMobilisAdminResource() {
		return _mobilisAdminResource;
	}


	/**
	 * @param _mobilisAdminResource the _mobilisAdminResource to set
	 */
	public void setMobilisAdminResource( String mobilisAdminResource ) {
		this._mobilisAdminResource = mobilisAdminResource;
	}


	/**
	 * @return the _mobilisDeploymentResource
	 */
	public String getMobilisDeploymentResource() {
		return _mobilisDeploymentResource;
	}


	/**
	 * @param _mobilisDeploymentResource the _mobilisDeploymentResource to set
	 */
	public void setMobilisDeploymentResource( String mobilisDeploymentResource ) {
		this._mobilisDeploymentResource = mobilisDeploymentResource;
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
