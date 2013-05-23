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
package de.tudresden.inf.rn.mobilis.server;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;

import de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent;
import de.tudresden.inf.rn.mobilis.server.deployment.container.ServiceContainer;
import de.tudresden.inf.rn.mobilis.server.deployment.container.ServiceContainerState;
import de.tudresden.inf.rn.mobilis.server.deployment.event.ContainerStateEvent;
import de.tudresden.inf.rn.mobilis.server.deployment.event.IContainerStateChangedListener;
import de.tudresden.inf.rn.mobilis.server.deployment.exception.InstallServiceException;
import de.tudresden.inf.rn.mobilis.server.deployment.exception.RegisterServiceException;
import de.tudresden.inf.rn.mobilis.server.deployment.exception.StartNewServiceInstanceException;
import de.tudresden.inf.rn.mobilis.server.persistency.IORPersistenceImplementor;
import de.tudresden.inf.rn.mobilis.server.persistency.PIDerby;
import de.tudresden.inf.rn.mobilis.server.persistency.PIHibernate;
import de.tudresden.inf.rn.mobilis.server.services.MobilisService;
import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.helper.DoubleKeyMap;

public class MobilisManager {

	private static final String PERSISTED_SERVICES_FILE_PATH = "service/services.txt";
	public final static String discoNamespace = Mobilis.NAMESPACE;	
	public final static String discoServicesNode = discoNamespace + "#services";
//	// all services
//	for (String serviceName : MobilisManager.getInstance().getServices().keySet()) {
//		MobilisService service = MobilisManager.getInstance().getService(serviceName);
//		Item item = new DiscoverItems.Item(service.getAgent().getConnection().getUser());
//		item.setName(service.getName());
//		item.setNode(service.getNode());
//		items.add(item);
//	}
	
	// the MobilisManager singleton instance
	private static MobilisManager instance;

	public static synchronized MobilisManager getInstance() {
		if (instance == null) {
			instance = new MobilisManager();
		}
		return instance;
	}

	public static Logger getLogger() {
		return Logger.getLogger("de.tudresden.inf.rn.mobilis");
	}
	
	public static Logger getHibernateLogger() {
		return Logger.getLogger("org.hibernate");
	}

	// container for configuration
	private final Map<String, Map<String, Map<String, Object>>> mConfiguration = 
		Collections.synchronizedMap(new HashMap<String, Map<String, Map<String, Object>>>());
	
	private final Map<String, MobilisAgent> mAgents = Collections.synchronizedMap(new HashMap<String, MobilisAgent>());

	private final Map<String, MobilisService> mServices = Collections.synchronizedMap(new HashMap<String, MobilisService>());

	private final Set<MobilisView> mServerViews = Collections.synchronizedSet(new HashSet<MobilisView>());

	private Boolean mStarted = false;

	private MobilisManager() {
		File tmpFolder = new File( DIRECTORY_TEMP_PATH );
		
		if(!tmpFolder.exists())
			tmpFolder.mkdir();
		
		
		// setup customized logger for GUI and console output
		getLogger().setUseParentHandlers(false);
		getLogger().setLevel(Level.ALL);
		getLogger().addHandler(new Handler() {
			public void close() throws SecurityException {}
			public void flush() {}
			public void publish(LogRecord record) {
				synchronized(mServerViews) {
					for (MobilisView view : mServerViews) {
						try {
							view.showLogMessage(record.getLevel(), record.getMessage());
						} catch (Exception e) {
							// TODO Auto-generated catch block
						}
					}
				}
			}
		});
		getHibernateLogger().setUseParentHandlers(false);
		getHibernateLogger().setLevel(Level.INFO);
		getHibernateLogger().addHandler(new Handler() {
			public void close() throws SecurityException {}
			public void flush() {}
			public void publish(LogRecord record) {
				synchronized(mServerViews) {
					for (MobilisView view : mServerViews) {
						try {
							view.showLogMessage(record.getLevel(), "Hibernate: " + record.getMessage());
						} catch (Exception e) {
							// TODO Auto-generated catch block
						}
					}
				}
			}
		});
		
		//getLogger().addHandler(new ConsoleHandler());
		for (Handler handler : getLogger().getHandlers()) {
			try {
				handler.setLevel(Level.ALL);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
			}
		}
	}
	
	public MobilisAgent getAgent(String ident) {
		return getAgent(ident, "MobilisAgent");
	}
	
	public MobilisAgent getAgent(String ident, String classname) {
		MobilisAgent singleton = null;
		
		String packagename = getClass().getPackage().getName();
		if (!classname.startsWith(packagename)) {
			classname = packagename + ".agents." + classname;
		}

		synchronized(mAgents) {
			singleton = mAgents.get(ident);
		}

		if (singleton != null) {
			return singleton;
		}

		try {
			Class<?> agentClass = Class.forName(classname);
			Constructor<?> constructor = agentClass.getConstructor( new Class[]{ String.class } );
			singleton = (MobilisAgent) constructor.newInstance( new Object[]{ ident } );
		} catch (Exception e) {
			getLogger().severe("Couldn't instantiate agent: " + ident + " (" + classname + ")");
		}

		synchronized(mAgents) {
			mAgents.put(ident, singleton);
		}

		return singleton;
	}
	
	public List<Item> getNodeItems() {
    	List<Item> items = new LinkedList<Item>();
    	for (MobilisAgent agent: this.mAgents.values())
    		items.add( new Item(agent.getFullJid()) );
    	return items;
    }

	public MobilisService getService(String classname) {
		MobilisService singleton;

		String packagename = getClass().getPackage().getName();
		if (!classname.startsWith(packagename)) {
			classname = packagename + ".services." + classname;
		}

		synchronized(mServices) {
			singleton = mServices.get(classname);
		}

		if (singleton != null) {
			return singleton;
		}
		
		try {
			singleton = (MobilisService) Class.forName(classname).newInstance();
		} catch(Exception e) {
			getLogger().severe("Couldn't instantiate service: " + classname + " because of " + e.getClass().getName() + ": " + e.getMessage());    
		}

		synchronized(mServices) {
			if (singleton != null) mServices.put(classname, singleton);
		}

		return singleton;
	}
	
	/**
	 * @returna a list of all MobilisServices, that are registered at the MobilisManager
	 */
	public ArrayList<MobilisService> getAllServices() {
		return new ArrayList<MobilisService>(mServices.values());
	}

	// getter/setter related

	public boolean isStarted() {
		return mStarted;
	}

	public Map<String, Map<String, Object>> getSettings(String container) {
		synchronized(mConfiguration) {
			return mConfiguration.get(container);
		}
	}

	public Map<String, Object> getSettings(String container, String ident) {
		Map<String, Object> result = null;
		synchronized(mConfiguration) {
			if (mConfiguration.containsKey(container)) {
				result = mConfiguration.get(container).get(ident);
			}
		}
		return result;
	}

	public String getSettingString(String container, String ident, String key) {
		Object resultObject = null;
		synchronized(mConfiguration) {
			if (mConfiguration.containsKey(container)) {
				if (mConfiguration.get(container).containsKey(ident)) {
					resultObject = mConfiguration.get(container).get(ident).get(key);
				}
			}
		}
		return (resultObject instanceof String ? (String) resultObject : null);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,String> getSettingStrings(String container, String ident, String key) {
		Object resultObject = null;
		synchronized(mConfiguration) {
			if (mConfiguration.containsKey(container)) {
				if (mConfiguration.get(container).containsKey(ident)) {
					resultObject = mConfiguration.get(container).get(ident).get(key);
				}
			}
		}
		return (resultObject instanceof Map<?,?> ? (Map<String,String>) resultObject : null);
	}

	public void registerServerView(MobilisView serverView) {
		synchronized(mServerViews) {
			mServerViews.add(serverView);
		}
	}

	public void unregisterServerView(MobilisView serverView) {
		synchronized(mServerViews) {
			mServerViews.remove(serverView);
		}
	}

	private void loadConfiguration() {		
		try {
			String filename = "src/META-INF/MobilisSettings.xml";
			loadConfigurationFromFile(filename);
			getLogger().config("Mobilis Manager read settings file: " + filename);
		} catch (Exception e) {
			getLogger().severe("Mobilis Manager could not read settings file.");
		}
	}
	
	
	public void addSettings(String container, String key, Map<String, Object> settings){
		mConfiguration.get(container).put(key, settings);
	}
	
	public void removeSettings(String container, String key){
		mConfiguration.get(container).remove(key);
	}

	@SuppressWarnings("unchecked")
	private void loadConfigurationFromFile(String filename) throws Exception {
		Map<String, Map<String, Map<String, Object>>> settings = new HashMap<String, Map<String, Map<String, Object>>>();      
		Document doc = null;
		try {
			SAXBuilder b = new SAXBuilder(false);  // validierenden Parser nutzen
			File file = new File(filename);
			doc = b.build(file);
			Element root = doc.getRootElement();
			Map< String, Object > rawMappedChildren = getMappedChildren(root);
			
			// changed by Danny --> see ant script failure for creating jar-files
			Map<String, Map<String, Map<String, Object>>> mappedChildren 
					= new HashMap< String, Map<String,Map<String,Object>> >();//(Map<? extends String, ? extends Map<String, Map<String, Object>>>) getMappedChildren(root);
			
			for ( Map.Entry< String, Object > entity : rawMappedChildren.entrySet() ) {
				Map<String,Map<String,Object>> subMap = (HashMap< String, Map<String,Object> >)entity.getValue();
				
				mappedChildren.put( entity.getKey(), subMap );
			}
			
			settings.putAll(mappedChildren);
			
			getLogger().finest("Mobilis Manager settings: " + settings.toString());
		} catch (IOException e) {
			getLogger().severe("Mobilis Manager could not read settings file: " + e.getMessage());
			throw new Exception();
		} catch (JDOMException j) {    // nur eine Ausnahme fuer alle Situationen
			getLogger().severe("Mobilis Manager could not read settings.");
			throw new Exception();
		}

		synchronized(mConfiguration) {
			mConfiguration.clear();
			mConfiguration.putAll(settings);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getMappedChildren(Element elem) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> childResult = new HashMap<String, Object>();
		Iterator<Element> iter = elem.getChildren().iterator();
		String elemName = elem.getName();
		if (elemName.equals("setting")) {
			while (iter.hasNext()) {
				Element child = iter.next();
				result.put(child.getAttribute("name").getValue(), child.getContent(0).getValue());
			}
		} else if (elemName.equals("settings")) {
			while (iter.hasNext()) {
				Element child = iter.next();
				if (child.getChildren().size() > 0) {
					result.put(child.getAttribute("name").getValue(), getMappedChildren(child));
				} else 
					result.put(child.getAttribute("name").getValue(), child.getContent(0).getValue());
			}
		} else if (elemName.equals("service") || elemName.equals("network")
				|| elemName.equals("agent")) {
			while (iter.hasNext()) {
				Element child = iter.next();
				childResult.putAll(getMappedChildren(child));
			}
			if (elemName.equals("service")) {
				childResult.put("agent", elem.getAttribute("agent").getValue());				
			}
			if (elemName.equals("service") || elemName.equals("agent")) {
				childResult.put("start", elem.getAttribute("start").getValue());			
			}
			childResult.put("description", elem.getAttribute("description").getValue());
			childResult.put("type", elem.getAttribute("type").getValue());			
			result.put(elem.getAttribute("name").getValue(), childResult);
		} else if (elemName.equals("config")) {
			while (iter.hasNext()) {
				Element child = iter.next();
				result.putAll(getMappedChildren(child));
			}
		} else {
			while (iter.hasNext()) {
				Element child = iter.next();
				childResult.putAll(getMappedChildren(child));
			}
			result.put(elemName, childResult);
		}
		return result;
	}


	private void setupAgents() {	
		// bootstrap agent instances from configuration		
		for (String agentName : getSettings("agents").keySet()) {
			String className = null;
			try {
				className = getSettingString("agents", agentName, "type");
				String startType = getSettingString("agents", agentName, "start");
				if (startType.equalsIgnoreCase("auto"))
					getAgent(agentName, className);
			} catch (Exception e) {
				getLogger().severe("Couldn't setup agent: " + agentName);
			}
		}
	}

	private void setupServices() {	
		// bootstrap service instances from configuration
		for (String serviceName : getSettings("services").keySet()) {
			String className = null;
			MobilisService service = null;
			try {
				className = getSettingString("services", serviceName, "type");
				String startType = getSettingString("services", serviceName, "start");
				if (startType.equalsIgnoreCase("auto"))
					service = getService(className);
			} catch (Exception e) {
				getLogger().severe("Couldn't setup service: " + serviceName);
			}
			
			// register service with agent
			try {
				String agentIdent = getSettingString("services", serviceName, "agent");
				String startType = getSettingString("services", serviceName, "start");
				if (startType.equalsIgnoreCase("auto"))
					getAgent(agentIdent).registerService(service);
			} catch (Exception e) {
				getLogger().severe("Couldn't register service: " + serviceName);
			}
		}
		
		// bootstrap service instances from services.txt
		Path serviceFilePath = Paths.get(PERSISTED_SERVICES_FILE_PATH);
		if (Files.exists(serviceFilePath)) {
			try {
				List<String> serviceFileNamesAndModes = Files.readAllLines(serviceFilePath, Charset.defaultCharset());
				for (String fileNameAndMode : serviceFileNamesAndModes) {
					String[] fileNameAndModeSplit = fileNameAndMode.split(";");
					String fileName = "service/" + fileNameAndModeSplit[0];
					String mode = fileNameAndModeSplit[1];
					boolean singleMode = mode.equals("single");
					installAndConfigureAndRegisterServiceFromFile(new File(fileName), true, singleMode, "deployment");
				}
			} catch (IOException e) {
				System.err.println("Couldn't read from services.txt!");
				e.printStackTrace();
			}
		}
	}

	public void startup() {
		synchronized(mStarted) {
			if (!mStarted) {
				loadConfiguration();
				setupAgents();
				setupServices();
				
				synchronized(mAgents) {
					for (String key : mAgents.keySet()) {
						try {
							String startType = getSettingString("agents", mAgents.get(key).getIdent(), "start");
							if (startType.equalsIgnoreCase("auto"))
								mAgents.get(key).startup();
						} catch (XMPPException e) {
							getLogger().severe("Couldn't startup agent: " + key + " because " + e.getMessage());
						}
					}
				}
				mStarted = true;
				synchronized(mServerViews) {
					for (MobilisView view : mServerViews) {
						try {
							view.setStarted(mStarted);
						} catch (Exception e) {
							// TODO Auto-generated catch block
						}
					}
				}
				getLogger().info("Mobilis Manager started up.");
			} else {
				getLogger().info("Mobilis Manager is already up.");
			}
		}
	}

	public void shutdown() {
		synchronized(mStarted) {
			if (mStarted) {
				synchronized(mAgents) {
					String[] agentsArray = mAgents.keySet().toArray(new String[0]);
					for (int i = 0; i < agentsArray.length; i++) {
						String key = agentsArray[i];
						try {
							mAgents.get(key).shutdown();
						} catch (XMPPException e) {
							getLogger().severe("Couldn't shutdown agent: " + key);
						}
					}
					mAgents.clear();
				}
				synchronized (_serviceContainers) {
					persistServices();
				}
				synchronized(mServices) {
					mServices.clear();
				}
				synchronized(mConfiguration) {
					mConfiguration.clear();
				}
				mStarted = false;
				synchronized(mServerViews) {
					for (MobilisView view : mServerViews) {
						try {
							view.setStarted(mStarted);
						} catch (Exception e) {
							// TODO Auto-generated catch block
						}
					}
				}
				getLogger().info("Mobilis Manager shut down.");
			} else {
				getLogger().info("Mobilis Manager is already down.");
			}
		}
	}
	
	
	
	
	// ===== New functionality to handle dynamic deployment by ServiceContainers ===== \\
	
	public void addAgent(MobilisAgent agent) {
		synchronized (mAgents) {
			mAgents.put(agent.getIdent(), agent);
		}
	}
	
	public void notifyOfServiceShutdown(MobilisService service) {
		synchronized (mServices) {
			mServices.remove(service.getIdent());
		}
	}
	
	public void notifyOfAgentShutdown(MobilisAgent agent) {
		synchronized (mAgents) {
			mAgents.remove(agent.getIdent());
		}
	}
	
	public void notifyOfServiceContainerUninstall(ServiceContainer container) {
		synchronized (_serviceContainers) {
			_serviceContainers.remove(container.getServiceNamespace(), container.getServiceVersion());
		}
	}
	
	/**
	 * Stores all currently installed services in a file so that they
	 * can be reinstalled during the next application start.
	 */
	private void persistServices() {
		try {
			Path path = Paths.get(PERSISTED_SERVICES_FILE_PATH);
			Files.deleteIfExists(path);
			List<String> serviceInfos = new ArrayList<String>();
			
			List<ServiceContainer> serviceContainers = _serviceContainers.getListOfAllValues();
			for (ServiceContainer container : serviceContainers) {
				serviceInfos.add(container.getJarFile().getAbsoluteFile().getName() + ";" + container.getConfigurationValue(MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "mode"));
			}
			Files.write(path, serviceInfos, Charset.defaultCharset(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		} catch (IOException e) {
			System.err.println("Couldn't create services.txt!");
			e.printStackTrace();
		}
	}

	/**
	 * Path to temporary dir to extract files like msdl or resources.
	 */
	public static final String DIRECTORY_TEMP_PATH = "tmp";
	
	/**
	 * Keys for ServiceContainer configuration like agent
	 */
	public static final String CONFIGURATION_CATEGORY_AGENT_KEY = "agent";
	public static final String CONFIGURATION_CATEGORY_AGENT_LIST_KEY = "agents";
	
	public static final String CONFIGURATION_CATEGORY_SERVICE_LIST_KEY = "services";
	
	/**
	 * Services wich were uploaded but not installed yet
	 */
	private List<ServiceContainer> _pendingServices 
		= Collections.synchronizedList( new ArrayList< ServiceContainer >() );
	
	/**
	 * Map of available service containers.
	 */
	private DoubleKeyMap< String, Integer, ServiceContainer > _serviceContainers
		= new DoubleKeyMap< String, Integer, ServiceContainer >( true );
	
	
	// TODO: PersistenceInterfaces
	private static IORPersistenceImplementor _persistenceImplementorServer = new PIDerby();
	private static IORPersistenceImplementor _persistenceImplementorServices = new PIHibernate();
	
	
	/**
	 * Adds a new ServiceContainer.
	 * 
	 * @param serviceContainer container to add
	 * @return true if adding was successful
	 */
	public boolean addServiceContainer(ServiceContainer serviceContainer){
		boolean added = false;
		
		synchronized ( _serviceContainers ) {
			if(!_serviceContainers.containsSubKey( serviceContainer.getServiceNamespace(),
					serviceContainer.getServiceVersion() ))				
				added = (null != _serviceContainers.put( serviceContainer.getServiceNamespace(),
						serviceContainer.getServiceVersion(), serviceContainer ));
		}
		
		// add a listener which will be fired if the state of a container changes
		if(added){
			serviceContainer.addContainerStateChangedListener( _containerStateChangedListener );
		}
		
		return added;
	}
	
	/**
	 * Gets all available service containers.
	 * 
	 * @return List of all service containers
	 */
	public List<ServiceContainer> getAllServiceContainers(){		
		return _serviceContainers.getListOfAllValues();
	}
	
	/**
	 * Gets all available service containers by a given namespace independent
	 *  from service version.
	 * 
	 * @param namespace the namespace of the service containers
	 * @return a collection of service containers or <code>null</code> if there
	 * 		this there is no service container with this namespace
	 */
	public Collection<ServiceContainer> getAllServiceContainers(String namespace){		
		Map<Integer, ServiceContainer> subKeyValueMap = _serviceContainers.getSubKeyValueMap( namespace );
		if (subKeyValueMap == null) {
			return null;
		} else {
			return subKeyValueMap.values();
		}
	}
	
	/**
	 * Returns service container of the highest version.
	 * 
	 * @param namespace of the container
	 * @return the service container
	 */
	public ServiceContainer getServiceContainer(String namespace){
		Set<Integer> serviceVersions = _serviceContainers.getSubKeySet( namespace );
		int maxVersion = Integer.MIN_VALUE;
		
		for ( Integer version : serviceVersions ) {
			if(version > maxVersion)
				maxVersion = version;
		}
		
		return _serviceContainers.get( namespace, maxVersion );
	}
	
	/**
	 * Gets a service container by namespace and version.
	 * 
	 * @param namespace of the container
	 * @param version of the container
	 * @return the service container
	 */
	public ServiceContainer getServiceContainer(String namespace, int version){		
		return _serviceContainers.get( namespace, version );
	}
	
	/**
	 * Returns the service container with the highest version in range of minVersion
	 *  and maxVersion. 
	 * Use '-1' if one of this parameter shouldn't be considered.
	 * 
	 * @param namespace namespace of the container
	 * @param minVersion minVersion of the container
	 * @param maxVersion maxVersion of the container
	 * @return the service container
	 */
	public ServiceContainer getServiceContainer(String namespace, int minVersion,
			int maxVersion){
		Set<Integer> serviceVersions = _serviceContainers.getSubKeySet( namespace );
		int relativeMaxVersion = Integer.MIN_VALUE;
		
		for ( Integer version : serviceVersions ) {
			if(version > relativeMaxVersion
					&& ( version >= minVersion || minVersion == -1 )
					&& ( version <= maxVersion || maxVersion == -1 ) )
				relativeMaxVersion = version;
		}
		
		return _serviceContainers.get( namespace, relativeMaxVersion );
	}
	
	/**
	 * Returns service container with highest version in range of minVersion and maxVersion. 
	 * Use '-1' if one of this parameter shouldn't be considered. Also consider container state.
	 * 
	 * @param namespace namespace of the container
	 * @param minVersion minVersion of the container
	 * @param maxVersion maxVersion of the container
	 * @param state state of the container
	 * @return the service container
	 */
	public ServiceContainer getServiceContainer(String namespace, int minVersion, 
			int maxVersion, ServiceContainerState state){
		Set<Integer> serviceVersions = _serviceContainers.getSubKeySet( namespace );
		int relativeMaxVersion = Integer.MIN_VALUE;
		
		for ( Integer version : serviceVersions ) {
			if(version > relativeMaxVersion
					&& ( version >= minVersion || minVersion == -1 )
					&& ( version <= maxVersion || maxVersion == -1 )
					&& _serviceContainers.get( namespace, version ).getContainerState() == state)
				relativeMaxVersion = version;
		}
		
		return _serviceContainers.get( namespace, relativeMaxVersion );
	}
	
	/**
	 * Gets a service container by jar filename.
	 * 
	 * @param fileName filename of the jar
	 * @return the service container
	 */
	public ServiceContainer getServiceContainerByFileName(String fileName){		
		for ( ServiceContainer container : _serviceContainers.getListOfAllValues() ) {
			if(container.getJarFile().getName().toLowerCase().equals( fileName.toLowerCase() )){
				return container;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets a service container of a running service instance by jid.
	 * 
	 * @param jidOfInstance the jid of the running service instance
	 * @return the service container
	 */
	public ServiceContainer getServiceContainerByRunningInstanceJid(String jidOfInstance){
		ServiceContainer container = null;
		
		for ( ServiceContainer svcContainer : _serviceContainers.getListOfAllValues() ) {
			if(svcContainer.getRunningServiceInstances().containsKey( jidOfInstance )){
				container = svcContainer;
				break;
			}
		}
		
		return container;
	}
	
	/**
	 * Removes a service container from list.
	 * 
	 * @param namespace namespace of the service container
	 * @param version version of the service container
	 * @return the removed service container
	 */
	public ServiceContainer removeServiceContainer(String namespace, int version){
		ServiceContainer container = _serviceContainers.remove( namespace, version );
		
		if(null != container){
			container.removeContainerStateChangedListener( _containerStateChangedListener );
		}
		
		return container; 
	}
	
	/**
	 * Adds a pending service container. This is a container which is just uploaded 
	 * but not installed yet.
	 * 
	 * @param serviceContainer the service container
	 * @return true, if adding was successful
	 */
	public boolean addPendingService(ServiceContainer serviceContainer){
		return _pendingServices.add(serviceContainer);
	}
	
	/**
	 * Gets a pending service container by jar filename.
	 * 
	 * @param fileName filename of the jar
	 * @return the service container
	 */
	public ServiceContainer getPendingServiceByFileName(String fileName){		
		for ( ServiceContainer container : _pendingServices ) {
			if(container.getJarFile().getName().toLowerCase().equals( fileName.toLowerCase() )){
				return container;
			}
		}
		
		return null;
	}
	
	/**
	 * Removes a pending service container by the name of the jar file.
	 * 
	 * @param fileName name of the jar file
	 * @return the service container
	 */
	public ServiceContainer removePendingServiceByFileName(String fileName){
		ServiceContainer pendingService = null;
		
		for ( ServiceContainer container : _pendingServices ) {
			if(container.getJarFile().getName().toLowerCase().equals( fileName.toLowerCase() )){
				pendingService = container;
			}
		}
		
		if(null != pendingService && _pendingServices.remove( pendingService ))
			return pendingService;
		else
			return null;
	}
	
	/**
	 * Listener for container state changes. Current there will be only act on 
	 * unregister a service container.
	 */
	private IContainerStateChangedListener _containerStateChangedListener 
		= new IContainerStateChangedListener() {
		
		@Override
		public void onStateChanged( ContainerStateEvent evt ) {
			// If Mobilis-Service is no more active/registered, inform all depended services
			if(evt.getOldState() == ServiceContainerState.ACTIVE
					&& evt.getNewState() != ServiceContainerState.ACTIVE ){			
				// get all service containers which depend on the unregistered service 
				// container and which are active
				for ( ServiceContainer container : getAllServiceContainers() ) {
					if( container.isRequiredService( evt.getNamespace(), evt.getVersion() )
							&& container.getContainerState() == ServiceContainerState.ACTIVE ){
						// unregister service containers which depend on the unregistered 
						// service container. running service instances won't be shutdown
						container.unregister();
						
						MobilisManager.getLogger().log( Level.WARNING,
								String.format(
										"Service namespace=%s version=%d was unregistered by MobilisServer, because it depends on service namespace=%s version=%d which was unregistered by user.",
										container.getServiceNamespace(), 
										container.getServiceVersion(), 
										evt.getNamespace(),
										evt.getVersion() ) );
					}
				}
			}
		}
	};
	
	/**
	 * Creates a new service container with the data in the file and adds it to pending services.
	 * If autoDeploy is <code>true</code> the service is also installed, configured and registered.
	 * The mode (single or multi) can be set via the singleMode parameter. If you choose single, then
	 * the service will be automatically started for you.
	 * Default values for the agent are read from an alrady existing agent specified in 
	 * defaultValueAgent (e.g. deployment, coordinator). 
	 * @param serviceJar
	 * @param autoDeploy
	 * @param singleMode
	 * @param defaultValueAgent
	 * @return
	 * 		A human readable message describing the outcome of the operation. You may forward this to
	 * 		the user who issued the command.
	 */
	public String installAndConfigureAndRegisterServiceFromFile(File serviceJar, boolean autoDeploy, boolean singleMode, String defaultValueAgent) {
		String message = "";
		
		// check if service is already installed and uninstall if necessary
		ServiceContainer serviceContainer = new ServiceContainer( serviceJar );
		try {
			serviceContainer.extractServiceContainerConfig();
		} catch (InstallServiceException e) {
			message += "\n" + e.getMessage();
			e.printStackTrace();
			return message;
		}		
		ServiceContainer oldServiceContainer = getServiceContainer(serviceContainer.getServiceNamespace(), serviceContainer.getServiceVersion());
		if (oldServiceContainer != null) {
			oldServiceContainer.uninstall();
		}
		
		// Add a new uploaded service as a pending service which is
		// waiting for installation
		MobilisManager.getInstance().addPendingService(
				serviceContainer );
		
		if (autoDeploy) {
			try {
				// install
				serviceContainer.install();
				message += "\n***" + serviceContainer.getServiceNamespace() + " version " + serviceContainer.getServiceVersion() + "***";
				if ( serviceContainer.getContainerState() == ServiceContainerState.INSTALLED ) {
					MobilisManager.getInstance().addServiceContainer( serviceContainer );

					MobilisManager.getInstance().removePendingServiceByFileName(
							serviceJar.getName() );
					message += "\nService installation successful.";

				} else {
					message += "\nInstallation failed: Couldn't move service from pending to regular.";

					MobilisManager.getLogger().log( Level.WARNING, message );
				}

				
				// configure
				DoubleKeyMap< String, String, Object > configuration = new DoubleKeyMap< String, String, Object >(
						false );
				
				MobilisAgent deploymentAgent = getAgent(defaultValueAgent);
				
				configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "name",
						serviceContainer.getServiceName() );
				
				configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "mode",
						singleMode?"single":"multi" );
				
				configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "port",
						deploymentAgent.getSettingString( "port" ) );
				
				configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "username",
						deploymentAgent.getSettingString( "username" ) );
				
				configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "host",
						deploymentAgent.getSettingString( "host" ) );
				
				configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "start",
						"ondemand" );
				
				configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "description",
						serviceContainer.getServiceName() );
				
				configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "service",
						deploymentAgent.getSettingString( "service" ) );
				
				configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "resource",
						serviceContainer.getServiceName() );
				
				configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "type",
						"de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent" );
				
				configuration.put( MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY, "password",
						deploymentAgent.getSettingString( "password" ) );
				serviceContainer.configure(configuration);
				message += "\nService configuration successful.";
				
				// register
				serviceContainer.register();
				message += "\nService registration successful.";
				
				if (singleMode) {
					serviceContainer.startNewServiceInstance();
				}
			} catch (InstallServiceException e) {
				message += "\n" + e.getMessage();
				e.printStackTrace();
			} catch (RegisterServiceException e) {
				message += "\n" + e.getMessage();
				e.printStackTrace();
			} catch (StartNewServiceInstanceException e) {
				System.err.println("Couldn't start service instance of service " + serviceContainer.getServiceName());
				e.printStackTrace();
			}
		}
		return message;
	}
}
