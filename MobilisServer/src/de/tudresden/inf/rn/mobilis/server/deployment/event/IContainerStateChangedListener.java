package de.tudresden.inf.rn.mobilis.server.deployment.event;

import java.util.EventListener;

/**
 * The listener interface for receiving IContainerStateChanged events. The class
 * that is interested in processing a IContainerStateChanged event implements
 * this interface, and the object created with that class is registered with a
 * component using the component's
 * <code>addIContainerStateChangedListener<code> method. When
 * the IContainerStateChanged event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see IContainerStateChangedEvent
 */
public interface IContainerStateChangedListener extends EventListener {

	/**
	 * On state changed.
	 * 
	 * @param evt
	 *            the event which will be fired
	 */
	public void onStateChanged( ContainerStateEvent evt );

}
