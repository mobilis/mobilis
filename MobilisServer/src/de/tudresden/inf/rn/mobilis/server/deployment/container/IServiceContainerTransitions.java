package de.tudresden.inf.rn.mobilis.server.deployment.container;

import java.io.File;

import de.tudresden.inf.rn.mobilis.server.deployment.exception.InstallServiceException;
import de.tudresden.inf.rn.mobilis.server.deployment.exception.RegisterServiceException;
import de.tudresden.inf.rn.mobilis.server.deployment.exception.UpdateServiceException;
import de.tudresden.inf.rn.mobilis.xmpp.beans.helper.DoubleKeyMap;

/**
 * The Interface IServiceContainerTransitions which is used by ServiceContainer.
 */
public interface IServiceContainerTransitions {

	/**
	 * Switch to state Install.
	 * 
	 * @throws InstallServiceException
	 *             is thrown if installation failed
	 */
	void install() throws InstallServiceException;

	/**
	 * Switch to state Uninstall.
	 */
	void uninstall();

	/**
	 * Switch to state Update.
	 * 
	 * @param newJarFile
	 *            the new jar file to update the old one
	 * @throws UpdateServiceException
	 *             is thrown if update failed
	 */
	void update( File newJarFile ) throws UpdateServiceException;

	/**
	 * Switch to state Configure.
	 * 
	 * @param configuration
	 *            the configuration of the ServiceContainer
	 */
	void configure( DoubleKeyMap< String, String, Object > configuration );

	/**
	 * Switch to state Register.
	 * 
	 * @throws RegisterServiceException
	 *             is thrown if registration failed
	 */
	void register() throws RegisterServiceException;

	/**
	 * Switch to state Unregister.
	 */
	void unregister();
}
