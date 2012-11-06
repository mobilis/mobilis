/*******************************************************************************
 * Copyright (C) 2010 Technische Universität Dresden
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 ******************************************************************************/
package de.tudresden.inf.rn.mobilis.server.deployment.container;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.event.EventListenerList;

import org.jivesoftware.smack.XMPPException;

import de.tudresden.inf.rn.mobilis.server.MobilisManager;
import de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent;
import de.tudresden.inf.rn.mobilis.server.deployment.event.ContainerStateEvent;
import de.tudresden.inf.rn.mobilis.server.deployment.event.IContainerStateChangedListener;
import de.tudresden.inf.rn.mobilis.server.deployment.exception.InstallServiceException;
import de.tudresden.inf.rn.mobilis.server.deployment.exception.RegisterServiceException;
import de.tudresden.inf.rn.mobilis.server.deployment.exception.StartNewServiceInstanceException;
import de.tudresden.inf.rn.mobilis.server.deployment.exception.UpdateServiceException;
import de.tudresden.inf.rn.mobilis.server.deployment.helper.FileHelper;
import de.tudresden.inf.rn.mobilis.server.deployment.helper.JarClassLoader;
import de.tudresden.inf.rn.mobilis.server.deployment.helper.MSDLReader;
import de.tudresden.inf.rn.mobilis.server.deployment.helper.MSDLReader.ServiceDependency;
import de.tudresden.inf.rn.mobilis.server.services.MobilisService;
import de.tudresden.inf.rn.mobilis.xmpp.beans.helper.DoubleKeyMap;

/**
 * The Class ServiceContainer handles the life cycle of a service and its
 * instances.
 */
public class ServiceContainer implements IServiceContainerTransitions,
		IContainerStateChangable {

	/** The key for the service class in manifest file. */
	private final String MANIFEST_SERVICECLASS_KEY = "Service-Class";

	/** The key for the msdl file in manifest file. */
	private final String MANIFEST_MSDLFILE_KEY = "MSDL-File";

	/** The jar file of this service. */
	private File _jarFile;

	/** The msdl file of this service. */
	private File _msdlFile;

	/** The running service instances (jid, service). */
	private Map<String, MobilisService> _runningServiceInstances;

	/** The container state. */
	private ServiceContainerState _containerState = ServiceContainerState.UNINSTALLED;

	/** The service class template to instantiate new service instances. */
	private Class _serviceClassTemplate;

	/** The service version. */
	private int _serviceVersion = -1;

	/** The service namespace. */
	private String _serviceNamespace = "";

	/** The service name. */
	private String _serviceName = "";

	/**
	 * The configuration of the service. Configuration will be stored as
	 * <mainKey, subKey, value> e.g. agent information for this ServiceContainer
	 * will be stored as < "agent", key, value > e.g. < "agent", "password",
	 * "myPassword" >
	 * */
	private DoubleKeyMap<String, String, Object> _configuration = new DoubleKeyMap<String, String, Object>(
			false);

	/** The container state changed listeners. */
	protected EventListenerList _containerStateChangedListeners = new EventListenerList();

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tudresden.inf.rn.mobilis.server.wrapper.IContainerStateChangable#
	 * addContainerStateChangedListener
	 * (de.tudresden.inf.rn.mobilis.server.event.IContainerStateChangedListener)
	 */
	@Override
	public void addContainerStateChangedListener(
			IContainerStateChangedListener listener) {
		_containerStateChangedListeners.add(
				IContainerStateChangedListener.class, listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tudresden.inf.rn.mobilis.server.wrapper.IContainerStateChangable#
	 * removeContainerStateChangedListener
	 * (de.tudresden.inf.rn.mobilis.server.event.IContainerStateChangedListener)
	 */
	@Override
	public void removeContainerStateChangedListener(
			IContainerStateChangedListener listener) {
		_containerStateChangedListeners.remove(
				IContainerStateChangedListener.class, listener);
	}

	/**
	 * Fires container state changed event.
	 * 
	 * @param oldState
	 *            the old state of this container
	 * @param newState
	 *            the new state of this container
	 */
	private void fireContainerStateChangedEvent(ServiceContainerState oldState,
			ServiceContainerState newState) {
		Object[] listeners = _containerStateChangedListeners.getListenerList();

		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == IContainerStateChangedListener.class) {
				((IContainerStateChangedListener) listeners[i + 1])
						.onStateChanged(new ContainerStateEvent(
								_serviceNamespace, _serviceVersion, oldState,
								newState));
			}
		}
	}

	/**
	 * Instantiates a new service container.
	 * 
	 * @param jarFile
	 *            the jar file of this container
	 */
	public ServiceContainer(File jarFile) {
		_jarFile = jarFile;

		_runningServiceInstances = Collections
				.synchronizedMap(new HashMap<String, MobilisService>());
	}

	/**
	 * Change container state.
	 * 
	 * @param state
	 *            the new state
	 */
	private void changeContainerState(ServiceContainerState state) {
		if (state != _containerState) {
			fireContainerStateChangedEvent(_containerState, state);

			MobilisManager
					.getLogger()
					.log(Level.INFO,
							String.format(
									"Mobilis Service Container [ %s, Version: %d ] changed state from [ %s ] to [ %s ]",
									_serviceNamespace, _serviceVersion,
									_containerState, state));

			_containerState = state;
		}
	}

	/**
	 * Everything will be reset beside the jar archive.
	 */
	private void resetContainer() {
		changeContainerState(ServiceContainerState.UNINSTALLED);

		_msdlFile.delete();
		_msdlFile = null;

		_serviceClassTemplate = null;

		_serviceNamespace = "";
		_serviceVersion = -1;

		_runningServiceInstances.clear();
		_configuration.clear();
	}

	/**
	 * Start new service instance.
	 * 
	 * @return the new mobilis service instance
	 * @throws StartNewServiceInstanceException
	 *             exception if starting a new instance failed
	 */
	public MobilisService startNewServiceInstance()
			throws StartNewServiceInstanceException {
		MobilisService mobilisService = null;

		// Can only start a new service instance if this service is in state
		// active
		if (_containerState == ServiceContainerState.ACTIVE) {

			String serviceMode = getConfigurationValue(
					MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "mode")
					.toString();

			// If service is in mode 'single' and there is already a running
			// instance, throw exception
			if (null != serviceMode && serviceMode.equalsIgnoreCase("single")
					&& _runningServiceInstances.size() > 0) {
				throw new StartNewServiceInstanceException(
						String.format(
								"There is already a running instance [ %s ] of this service, running in mode \'single\'.",
								_runningServiceInstances.keySet().toArray()[0]));
			}

			Constructor construcor;

			// Try to instantiate a new template class
			try {
				construcor = _serviceClassTemplate.getConstructor();
				mobilisService = (MobilisService) construcor
						.newInstance(new Object[] {});
			} catch (Exception e) {
				throw new StartNewServiceInstanceException(e.getMessage());
			}

			// Get an XMPP resource for the new agent, that is not already in
			// use.
			String agentIdent = this.getAgentId();

			if (!_configuration
					.containsMainKey(MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY)) {
				throw new StartNewServiceInstanceException(String.format(
						"Cannot find agent configuration for service [ %s ]",
						mobilisService.getNamespace()));
			}

			// create new resource name which is unused
			int i = 1;
			boolean alreadyTaken;
			do {
				alreadyTaken = false;
				for (MobilisService ms : _runningServiceInstances.values()) {
					if (ms.getAgent()
							.getResource()
							.equalsIgnoreCase(
									String.format("%s#%d", agentIdent, i))) {
						alreadyTaken = true;
						i++;
						break;
					}
				}
			} while (alreadyTaken);

			String agentResource = String.format("%s#%d", agentIdent, i);

			// Create the new Agent with the generated XMPP resource.
			MobilisAgent agent = new MobilisAgent(agentIdent, true,
					agentResource);
			agent.registerService(mobilisService);

			// Startup Agent and Service
			mobilisService.setAgent(agent);
			try {
				agent.startup();
				_runningServiceInstances.put(mobilisService.getAgent()
						.getFullJid(), mobilisService);
			} catch (XMPPException e) {
				e.printStackTrace();
				try {
					// Try to Shutdown agent and service.
					mobilisService.shutdown();
					agent.shutdown();
				} catch (Exception e1) {
					mobilisService = null;
				}
			}
		}

		return mobilisService;
	}

	/**
	 * Shutdown a service instance.
	 * 
	 * @param jid
	 *            the jid of the service instance
	 * @return the mobilis service which was shutted down
	 */
	public MobilisService shutdownServiceInstance(String jid) {
		if (_runningServiceInstances.containsKey(jid)) {
			MobilisService mobilisService = _runningServiceInstances
					.remove(jid);
			MobilisAgent agent = mobilisService.getAgent();

			// shutdown mobilis instance
			try {
				mobilisService.shutdown();
			} catch (Exception e) {
				MobilisManager
						.getLogger()
						.log(Level.WARNING,
								String.format(
										"Cannot shutdown Service [ %s ] because of: %s",
										jid, e.getMessage()));
			}

			// shutdown agent
			try {
				agent.shutdown();

				MobilisManager.getLogger().log(
						Level.WARNING,
						String.format(
								"Shutdown Agent [ %s ] which contains: %s",
								agent.getFullJid(), agent.servicesToString()));
			} catch (XMPPException e) {
				MobilisManager.getLogger().log(
						Level.WARNING,
						String.format(
								"Cannot shutdown Agent [ %s ] because of: %s",
								jid, e.getMessage()));
			}

			return mobilisService;
		} else {
			return null;
		}
	}

	/**
	 * Shut down all service instances.
	 */
	public void shutDownAllServiceInstances() {
		MobilisManager
				.getLogger()
				.log(Level.WARNING,
						String.format(
								"All service isntances of [ %s version %d ] will be shut down.",
								_serviceNamespace, _serviceVersion));

		for (String runningServiceJid : _runningServiceInstances.keySet()) {
			this.shutdownServiceInstance(runningServiceJid);

			MobilisManager.getLogger()
					.log(Level.INFO,
							String.format("Service [ %s ] is down.",
									runningServiceJid));
		}

		_runningServiceInstances.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tudresden.inf.rn.mobilis.server.deployment.container.
	 * IServiceContainerTransitions#install()
	 */
	@Override
	public void install() throws InstallServiceException {
		if (_containerState == ServiceContainerState.UNINSTALLED
				|| _containerState == ServiceContainerState.INSTALLED) {
			JarClassLoader jarClassLoader = new JarClassLoader();
			String msdlFilepath = null;
			String serviceFilePath = null;

			try {
				// read msdl file from manifest of jar file
				jarClassLoader.addFilePathAbsolute(_jarFile.getAbsolutePath());
				// Read MSDL file location from MANIFEST.MF
				msdlFilepath = FileHelper.getJarManifestValue(_jarFile,
						MANIFEST_MSDLFILE_KEY);

				// If MSDL was not found at described location, query whole jar
				// archive for a msdl file and use the first match
				if (null == msdlFilepath) {
					List<String> msdlFiles = FileHelper.getJarFiles(_jarFile,
							".msdl");

					if (msdlFiles.size() > 0) {
						msdlFilepath = msdlFiles.get(0);
					} else {
						throw new InstallServiceException(
								"Cannot find MSDL file in jar archive.");
					}
				}

				// Load MSDL file from jar archive and cache it localy in temp
				// directory using the name of the jar archive extended by .msdl
				_msdlFile = FileHelper.createFileFromInputStream(
						jarClassLoader.getResourceAsStream(msdlFilepath),
						MobilisManager.DIRECTORY_TEMP_PATH + File.separator
								+ _jarFile.getName() + ".msdl");

				// if msdl was not found, throw InstallServiceException
				if (null == _msdlFile)
					throw new InstallServiceException(
							"Result of MSDL file was NULL while loading from jar archive.");
				else
					MobilisManager.getLogger().log(Level.INFO,
							String.format("MSDL found"));

				// TODO: validate MSDL file against schema

				// read service namespace, version and name from msdl file
				_serviceNamespace = MSDLReader.getServiceNamespace(_msdlFile);
				_serviceVersion = MSDLReader.getServiceVersion(_msdlFile);
				_serviceName = MSDLReader.getServiceName(_msdlFile);

				MobilisManager.getLogger().log(
						Level.INFO,
						String.format("MSDL properties read (ns="
								+ _serviceNamespace + "; version="
								+ _serviceVersion + ")"));

				// read path of the MobilisService class from manifest of jar
				// file
				serviceFilePath = FileHelper.getJarManifestValue(_jarFile,
						MANIFEST_SERVICECLASS_KEY);

				// generate template class of service to instantiate service
				// instance
				_serviceClassTemplate = jarClassLoader
						.loadClass(serviceFilePath);

				MobilisManager.getLogger().log(Level.INFO,
						"Service class template created.");

				// Switch ContainerState to INSTALLED if everything goes right
				changeContainerState(ServiceContainerState.INSTALLED);

				MobilisManager
						.getLogger()
						.log(Level.INFO,
								String.format(
										"Service [ %s ] version [ %d ] sucessfully installed.",
										_serviceNamespace, _serviceVersion));
			} catch (UnsupportedClassVersionError e) {
				throw new InstallServiceException(e.getMessage());
			} catch (MalformedURLException e) {
				throw new InstallServiceException(e.getMessage());
			} catch (IOException e) {
				throw new InstallServiceException(e.getMessage());
			} catch (ClassNotFoundException e) {
				throw new InstallServiceException(e.getMessage());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tudresden.inf.rn.mobilis.server.deployment.container.
	 * IServiceContainerTransitions#uninstall()
	 */
	public void uninstall() {
		if (_containerState == ServiceContainerState.INSTALLED
				|| _containerState == ServiceContainerState.ACTIVE) {

			// at first unregister service if it is registered
			this.unregister();

			// shutdown all runnning serice instances
			this.shutDownAllServiceInstances();

			// reset container parameters
			this.resetContainer();

			// state == uninstalled
			changeContainerState(ServiceContainerState.UNINSTALLED);

			MobilisManager
					.getLogger()
					.log(Level.INFO,
							String.format(
									"Service [ %s ] version [ %d ] sucessfully uninstalled.",
									_serviceNamespace, _serviceVersion));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tudresden.inf.rn.mobilis.server.deployment.container.
	 * IServiceContainerTransitions#update(java.io.File)
	 */
	@Override
	public void update(File newJarFile) throws UpdateServiceException {
		MobilisManager
				.getLogger()
				.log(Level.INFO,
						String.format(
								"Start updating Service [ %s ] version < %d > with new file: %s.",
								_serviceNamespace, _serviceVersion,
								newJarFile.getName()));

		// backup old jar file, namepsace and version
		File oldJarFile = _jarFile;
		String oldNamespace = _serviceNamespace;
		int oldVersion = _serviceVersion;

		// unregister --> change state to installed
		unregister();

		// replace old jarFile with new one
		_jarFile = newJarFile;

		// install new jarFile, if fails, reset oldJarFile and install
		try {
			install();

			changeContainerState(ServiceContainerState.INSTALLED);
		} catch (InstallServiceException e) {
			_jarFile = oldJarFile;

			try {
				install();
			} catch (InstallServiceException e1) {
				MobilisManager
						.getLogger()
						.log(Level.SEVERE,
								String.format(
										"Rollback of aborted Service [ %s ] version < %d > failed: %s.",
										_serviceNamespace, _serviceVersion,
										e1.getMessage()));
			}

			MobilisManager
					.getLogger()
					.log(Level.SEVERE,
							String.format(
									"Updating Service [ %s ] version < %d > failed: %s.",
									_serviceNamespace, _serviceVersion,
									e.getMessage()));
			throw new UpdateServiceException(e.getMessage());
		}

		// refresh container in MobilisManager (remove and add)
		MobilisManager.getInstance().removeServiceContainer(oldNamespace,
				oldVersion);
		MobilisManager.getInstance().addServiceContainer(this);

		MobilisManager
				.getLogger()
				.log(Level.INFO,
						String.format(
								"Service [ %s ] version [ %d ] sucessfully updated to version [ %d ].",
								_serviceNamespace, oldVersion, _serviceVersion));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tudresden.inf.rn.mobilis.server.deployment.container.
	 * IServiceContainerTransitions
	 * #configure(de.tudresden.inf.rn.mobilis.server.
	 * deployment.helper.DoubleKeyMap)
	 */
	@Override
	public void configure(DoubleKeyMap<String, String, Object> configuration) {
		if (_containerState == ServiceContainerState.INSTALLED
				|| _containerState == ServiceContainerState.ACTIVE) {
			// Store configuration and override existing one
			_configuration.putAll(configuration);

			// _containerState will be the same as before

			MobilisManager
					.getLogger()
					.log(Level.INFO,
							String.format(
									"Configuration changed for service [ %s ] version [ %d ].",
									_serviceNamespace, _serviceVersion));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tudresden.inf.rn.mobilis.server.deployment.container.
	 * IServiceContainerTransitions#register()
	 */
	@Override
	public void register() throws RegisterServiceException {
		if (_containerState == ServiceContainerState.INSTALLED) {

			// check for configuration, if there is no one throw exception
			if (!_configuration
					.containsMainKey(MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY)) {
				throw new RegisterServiceException(String.format(
						"Cannot find agent configuration for service [ %s ].",
						_serviceNamespace));
			}

			// read dependencies of this service
			List<MSDLReader.ServiceDependency> dependencyServices = getServiceDependencies();
			List<String> missingDependencies = new ArrayList<String>();

			for (ServiceDependency serviceDependency : dependencyServices) {
				MobilisManager.getLogger().log(
						Level.INFO,
						"Requires following services NS="
								+ serviceDependency.getServiceNameSpace()
								+ " minVer="
								+ serviceDependency.getServiceMinVersion()
								+ " maxVer="
								+ serviceDependency.getServiceMaxVersion());

				ServiceContainer dependency = MobilisManager.getInstance()
						.getServiceContainer(
								serviceDependency.getServiceNameSpace(),
								serviceDependency.getServiceMinVersion(),
								serviceDependency.getServiceMaxVersion());

				if (null == dependency) {
					missingDependencies.add(String.format(
							"Unknown service: ns=%s version=[%s;%s]",
							serviceDependency.getServiceNameSpace(),
							serviceDependency.getServiceMinVersion() > -1 ? ""
									+ serviceDependency.getServiceMinVersion()
									: "-inf", serviceDependency
									.getServiceMaxVersion() > -1 ? ""
									+ serviceDependency.getServiceMaxVersion()
									: "+inf"));
				} else if (dependency.getContainerState() != ServiceContainerState.ACTIVE) {
					missingDependencies
							.add(String
									.format("Service not active: ns=%s version=[%s;%s] state=%s",
											serviceDependency
													.getServiceNameSpace(),
											serviceDependency
													.getServiceMinVersion() > -1 ? ""
													+ serviceDependency
															.getServiceMinVersion()
													: "-inf",
											serviceDependency
													.getServiceMaxVersion() > -1 ? ""
													+ serviceDependency
															.getServiceMaxVersion()
													: "+inf", dependency
													.getContainerState().name()));
				}
			}

			if (missingDependencies.size() > 0) {
				MobilisManager
						.getLogger()
						.log(Level.INFO,
								String.format(
										"Registration of service [ %s ] version [ %d ] failed in case of missing dependencies: %s.",
										_serviceNamespace, _serviceVersion,
										missingDependencies.toString()));

				throw new RegisterServiceException(
						"Missing following dependencies to register service: "
								+ missingDependencies.toString());
			}

			// register agent in MobilisManager like the before using an agent
			// id
			MobilisManager
					.getInstance()
					.addSettings(
							MobilisManager.CONFIGURATION_CATEGORY_AGENT_LIST_KEY,
							this.getAgentId(),
							_configuration
									.getSubKeyValueMap(MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY));

			MobilisManager
					.getLogger()
					.log(Level.INFO,
							String.format(
									"Registration of service [ %s ] version [ %d ] successful.",
									_serviceNamespace, _serviceVersion));

			// state == active
			changeContainerState(ServiceContainerState.ACTIVE);
		} else {
			throw new RegisterServiceException(
					String.format(
							"Can only register a Mobilis Service in state [ %s ]. Right now service is in state [ %s ]",
							ServiceContainerState.INSTALLED.toString(),
							_containerState.toString()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tudresden.inf.rn.mobilis.server.deployment.container.
	 * IServiceContainerTransitions#unregister()
	 */
	@Override
	public void unregister() {
		// 1. service im MobilisManager deregestrieren, aktuell laufende
		// instanzen laufen weiter --> deshalb kann configure auch im state
		// active gehen

		MobilisManager.getInstance().removeSettings(
				MobilisManager.CONFIGURATION_CATEGORY_AGENT_LIST_KEY,
				this.getAgentId());

		if (_containerState == ServiceContainerState.ACTIVE) {
			changeContainerState(ServiceContainerState.INSTALLED);

			MobilisManager
					.getLogger()
					.log(Level.INFO,
							String.format(
									"Unregistration of service [ %s ] version [ %d ] successful.",
									_serviceNamespace, _serviceVersion));
		}
	}

	/**
	 * Gets the agent id.
	 * 
	 * @return the agent id
	 */
	public String getAgentId() {
		return String.format("%s_v%d", _serviceName, _serviceVersion);
	}

	/**
	 * Gets a configuration value.
	 * 
	 * @param mainKey
	 *            the main key
	 * @param subKey
	 *            the sub key
	 * @return the configuration value
	 */
	public Object getConfigurationValue(String mainKey, String subKey) {
		return _configuration.get(mainKey, subKey);
	}

	/**
	 * Gets the jar file of this service.
	 * 
	 * @return the jar file
	 */
	public File getJarFile() {
		return _jarFile;
	}

	/**
	 * Gets the msdl file of this service.
	 * 
	 * @return the msdl file
	 */
	public File getMsdlFile() {
		return _msdlFile;
	}

	/**
	 * Gets the running service instances.
	 * 
	 * @return the running iervice instances
	 */
	public Map<String, MobilisService> getRunningServiceInstances() {
		return _runningServiceInstances;
	}

	/**
	 * Gets the container state.
	 * 
	 * @return the container state
	 */
	public ServiceContainerState getContainerState() {
		return _containerState;
	}

	/**
	 * Gets the service dependencies.
	 * 
	 * @return the service dependencies
	 */
	public List<MSDLReader.ServiceDependency> getServiceDependencies() {
		return MSDLReader.getServiceDependencies(_msdlFile);
	}

	/**
	 * Gets the service version.
	 * 
	 * @return the service version
	 */
	public int getServiceVersion() {
		return _serviceVersion;
	}

	/**
	 * Gets the service namespace.
	 * 
	 * @return the service namespace
	 */
	public String getServiceNamespace() {
		return _serviceNamespace;
	}

	/**
	 * Gets the size of running services.
	 * 
	 * @return the size of running services
	 */
	public int getSizeOfRunningServices() {
		return _runningServiceInstances.size();
	}

	/**
	 * Checks if this service depends on a service by the given namespace and
	 * version.
	 * 
	 * @param namespace
	 *            the namespace of the service
	 * @param version
	 *            the version of the service
	 * @return true, if this service depends on the service
	 */
	public boolean isRequiredService(String namespace, int version) {
		boolean isRequired = false;

		if (null != namespace && namespace.length() > 0) {
			for (MSDLReader.ServiceDependency serviceDependency : getServiceDependencies()) {
				if (namespace.equals(serviceDependency.getServiceNameSpace())) {
					// if version doesn't matter
					isRequired = (version < 0)
							// or if version is ge min version of dependency or
							// min
							// version isn't set
							|| ((version >= serviceDependency
									.getServiceMinVersion() || serviceDependency
									.getServiceMinVersion() == -1)
							// and if version is le max version of dependency or
							// max version isn't set
							&& (version <= serviceDependency
									.getServiceMaxVersion() || serviceDependency
									.getServiceMaxVersion() == -1));

					if (isRequired)
						break;
				}
			}
		}

		return isRequired;
	}

	public String getServiceName() {
		return _serviceName;
	}

	public void setServiceName(String serviceName) {
		this._serviceName = serviceName;
	}

}
