package de.tudresden.inf.rn.mobilis.server.deployment.container;

import de.tudresden.inf.rn.mobilis.server.deployment.event.IContainerStateChangedListener;

/**
 * The Interface IContainerStateChangable to register and remove listeners for
 * containers state.
 */
public interface IContainerStateChangable {

	/**
	 * Adds a container state changed listener to act on changing the container
	 * state.
	 * 
	 * @param listener
	 *            the listener which should be added
	 */
	public abstract void addContainerStateChangedListener( IContainerStateChangedListener listener );

	/**
	 * Removes a container state changed listener.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public abstract void removeContainerStateChangedListener(
			IContainerStateChangedListener listener );

}