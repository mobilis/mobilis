package de.tudresden.inf.rn.mobilis.server.deployment.event;

import java.util.EventObject;

import de.tudresden.inf.rn.mobilis.server.deployment.container.ServiceContainerState;

/**
 * The Class ContainerStateEvent to transfer information about the container and
 * the changed state.
 */
public class ContainerStateEvent extends EventObject {

	/** The Constant serialVersionUID for serialization. */
	private static final long serialVersionUID = 273027913415309893L;

	/** The namespace of the ServiceContainer. */
	private String namespace, mychange;

	/** The version of the ServiceContainer. */
	private int version;

	/** The old state of the ServiceContainer. */
	private ServiceContainerState oldState;

	/** The new state of the ServiceContainer. */
	private ServiceContainerState newState;

	/**
	 * Instantiates a new container state event.
	 * 
	 * @param namespace
	 *            the namespace of the ServiceContainer
	 * @param version
	 *            the version of the ServiceContainer
	 * @param oldState
	 *            the old state of the ServiceContainer
	 * @param newState
	 *            the new state of the ServiceContainer
	 */
	public ContainerStateEvent(String namespace, int version, ServiceContainerState oldState,
			ServiceContainerState newState) {
		super( new Object[] { namespace, version, oldState, newState } );

		this.namespace = namespace;
		this.version = version;
		this.oldState = oldState;
		this.newState = newState;
	}

	/**
	 * Gets the namespace of the ServiceContainer.
	 * 
	 * @return the namespace of the ServiceContainer
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Sets the namespace of the ServiceContainer.
	 * 
	 * @param namespace
	 *            the namespace to set
	 */
	public void setNamespace( String namespace ) {
		this.namespace = namespace;
	}

	/**
	 * Gets the version of the ServiceContainer.
	 * 
	 * @return the version of the ServiceContainer
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Sets the version of the ServiceContainer.
	 * 
	 * @param version
	 *            the version to set
	 */
	public void setVersion( int version ) {
		this.version = version;
	}

	/**
	 * Gets the old state of the ServiceContainer.
	 * 
	 * @return the oldState of the ServiceContainer
	 */
	public ServiceContainerState getOldState() {
		return oldState;
	}

	/**
	 * Sets the old state of the ServiceContainer.
	 * 
	 * @param oldState
	 *            the oldState to set
	 */
	public void setOldState( ServiceContainerState oldState ) {
		this.oldState = oldState;
	}

	/**
	 * Gets the new state of the ServiceContainer.
	 * 
	 * @return the newState of the ServiceContainer
	 */
	public ServiceContainerState getNewState() {
		return newState;
	}

	/**
	 * Sets the new state of the ServiceContainer.
	 * 
	 * @param newState
	 *            the newState to set
	 */
	public void setNewState( ServiceContainerState newState ) {
		this.newState = newState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.EventObject#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"ContainerStateEvent={namespace=%s; version=%d; oldstate=%s; newstate=%s;}",
				this.namespace, this.version, this.oldState, this.newState );
	}

}
