/*******************************************************************************
 * Copyright (C) 2010 Technische Universit�t Dresden
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
package de.tudresden.inf.rn.mobilis.server.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import de.tudresden.inf.rn.mobilis.server.MobilisManager;
import de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent;
import de.tudresden.inf.rn.mobilis.server.deployment.container.ServiceContainer;
import de.tudresden.inf.rn.mobilis.server.deployment.container.ServiceContainerState;
import de.tudresden.inf.rn.mobilis.server.deployment.exception.StartNewServiceInstanceException;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.coordination.CreateNewServiceInstanceBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.coordination.MobilisServiceDiscoveryBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.coordination.MobilisServiceInfo;
import de.tudresden.inf.rn.mobilis.xmpp.beans.coordination.StopServiceInstanceBean;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanHelper;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanIQAdapter;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanProviderAdapter;

/**
 * 
 * @author Robert L�bke
 *
 */
public class CoordinatorService extends MobilisService {

	private int serviceVersion = 1;
	private Map<String, Map<String, Object>> serviceSettings;
	/**
	 * List of all application-specific services, that the Coordinator created
	 * and now manages. 
	 */
	private List<AppSpecificService> appSpecificServicesList = null;
	/**
	 * Maintenance mode of the MobilisServer. In this mode, the Coordinator will not 
	 * respond to Service Discovery request and will not create new service instances.
	 */
	private boolean maintenanceMode = false;
	
	/**
	 * To transfer files like a msdl file to a participant
	 */
	private FileTransferManager _fileTransferManager;
	
	
	public CoordinatorService() {
		super();
		System.out.println("CoordinatorService created");
		serviceSettings = MobilisManager.getInstance().getSettings("services");		
		appSpecificServicesList=new ArrayList<AppSpecificService>();			
	}
	
	public String getNode() {
		return super.getNode() + "#" + serviceVersion;
	}
	
	public void startup(MobilisAgent agent) throws Exception {
		super.startup(agent);
		
		_fileTransferManager = new FileTransferManager(agent.getConnection());
		FileTransferNegotiator.setServiceEnabled(agent.getConnection(), true);
	}
		
	@Override
	protected void registerPacketListener() {
		XMPPBean prototype1 = new CreateNewServiceInstanceBean();	
		XMPPBean prototype2 = new MobilisServiceDiscoveryBean();
		XMPPBean prototype3 = new StopServiceInstanceBean();
		
		(new BeanProviderAdapter(prototype1)).addToProviderManager();
		(new BeanProviderAdapter(prototype2)).addToProviderManager();
		(new BeanProviderAdapter(prototype3)).addToProviderManager();
		
		getAgent().getConnection().addPacketListener( this, new PacketTypeFilter( IQ.class ) );
	}
	
    public void processPacket(Packet p) {
    	super.processPacket(p);
    	if (p instanceof BeanIQAdapter) {
    		XMPPBean b = ((BeanIQAdapter) p).getBean();
    		
    		if (b instanceof CreateNewServiceInstanceBean) {
    			CreateNewServiceInstanceBean bb = (CreateNewServiceInstanceBean) b;    			
    			if (b.getType() == XMPPBean.TYPE_SET)
    				this.inCreateServiceInstanceSet(bb);   				
    		} else if (b instanceof MobilisServiceDiscoveryBean) {
    			MobilisServiceDiscoveryBean bb = (MobilisServiceDiscoveryBean) b;    			
    			if (b.getType() == XMPPBean.TYPE_GET)
    				this.inMobilisServiceDiscoveryGet(bb); 
    		} else if (b instanceof StopServiceInstanceBean) {
    			StopServiceInstanceBean bb = (StopServiceInstanceBean) b;    			
    			if (b.getType() == XMPPBean.TYPE_SET)
    				this.stopServiceInstance(bb); 
    		}
    	}
    }
    
    private void inMobilisServiceDiscoveryGet(MobilisServiceDiscoveryBean bean) {
    	Connection c = this.mAgent.getConnection();
    	String from = bean.getFrom();
		String to = bean.getTo();
			
		MobilisServiceDiscoveryBean beanAnswer=null;
		
		if (maintenanceMode) {
			//The MobilisServer is currently in maintenance mode.
			beanAnswer = new MobilisServiceDiscoveryBean("wait",
					"unexpected-request",
					"The MobilisServer is currently in maintenance mode. Retry later.");	
		} else  if (bean.serviceNamespace==null) {
			//Empty request for all active services
			beanAnswer = new MobilisServiceDiscoveryBean(null);	
			
			// query all ServiceContainers which are available
			for ( ServiceContainer container : MobilisManager.getInstance().getAllServiceContainers() ) {
				// filter collected ServiceContainers by active(registered) containers
				if(container.getContainerState() == ServiceContainerState.ACTIVE){
					// read service mode (single or multi)
					String serviceMode = container.getConfigurationValue( 
							MobilisManager.CONFIGURATION_CATEGORY_AGENT_KEY,
							"mode" ).toString();
					
					// create a service information entry
					MobilisServiceInfo serviceInfo = new MobilisServiceInfo();
					serviceInfo.setMode( serviceMode );
					serviceInfo.setServiceNamespace( container.getServiceNamespace() );
					serviceInfo.setVersion( "" + container.getServiceVersion() );
					
					// if container is of type multi, include information about the size of running instances
					if(serviceMode.equalsIgnoreCase( "multi" )){
						serviceInfo.setInstances( container.getSizeOfRunningServices() );
					}
					
					beanAnswer.addDiscoveredService(serviceInfo);
				}
			}
			
			// query all autostart services which are listed in MobilisSettings.xml
			List<MobilisService> autostartServices = MobilisManager.getInstance().getAllServices();				
			// Sort out all services with the wrong namespace and version
			for (MobilisService service : autostartServices) {
				if ( 	service.mAgent!=null
						&&
						(bean.serviceNamespace==null ||
						(bean.serviceNamespace!=null && bean.serviceNamespace.equals(service.getNamespace())))
						&&
						(bean.serviceVersion < 0 ||
						(bean.serviceVersion > 0 && bean.serviceVersion == service.getVersion())))
					beanAnswer.addDiscoveredService(service.getNamespace(), "" + service.getVersion(), service.mAgent.getFullJid());
			}
			
			// query all running app specific services
			for (String ident : serviceSettings.keySet()) {
				Map<String, Object> settings = serviceSettings.get(ident);
				if (settings.containsKey("start") && settings.get("start").equals("ondemand")) {
					int count = 0;
					for (AppSpecificService ass : appSpecificServicesList)
						if (ident.equals(ass.getIdent()))
							count++;									
					beanAnswer.addDiscoveredService(MobilisManager.discoServicesNode+"/"+ident, count);
				}
			}
			
		} else {
			// Request for a special service with given Namespace and optional given Version			
			
			beanAnswer = new MobilisServiceDiscoveryBean(null);
			
			// query all ServiceContainers which are available
			for ( ServiceContainer container : MobilisManager.getInstance().getAllServiceContainers( bean.serviceNamespace ) ) {
				// filter collected ServiceContainers by active(registered) containers
				if(container.getContainerState() == ServiceContainerState.ACTIVE){
					// add service to list if no version is requested or if the version of the service matches the required version
					if(bean.serviceVersion < 0 || container.getServiceVersion() == bean.serviceVersion){
						for ( Map.Entry< String, MobilisService> entity : container.getRunningServiceInstances().entrySet() ) {
							// create a service information entry
							MobilisServiceInfo serviceInfo = new MobilisServiceInfo();
							serviceInfo.setServiceNamespace( container.getServiceNamespace() );
							serviceInfo.setVersion( "" + container.getServiceVersion() );
							serviceInfo.setJid( entity.getKey() );
							serviceInfo.setServiceName( entity.getValue().getName() );
							
							beanAnswer.addDiscoveredService(serviceInfo);
						}
					}
				}
			}
		}	
		
		beanAnswer.setTo(from); beanAnswer.setFrom(to);
		beanAnswer.setId(bean.getId());
		c.sendPacket(new BeanIQAdapter(beanAnswer));
		
		
		// If parameter 'requestMSDL' == true, start filetransfer with MSDL file
		if(bean.serviceNamespace != null && bean.serviceVersion > 0 && bean.requestMSDL){
			ServiceContainer container = MobilisManager.getInstance().getServiceContainer( bean.serviceNamespace, bean.serviceVersion );
			
			if(null != container){
				transmitFile( container.getMsdlFile(), "MSDL", bean.getFrom() );
			}
		}
	}
    

	private void inCreateServiceInstanceSet(CreateNewServiceInstanceBean bean) {
    	Connection c = this.mAgent.getConnection();
    	String from = bean.getFrom();
		String to = bean.getTo();
			
		XMPPBean beanAnswer=null;
				
		if (maintenanceMode) {
			//The MobilisServer is currently in maintenance mode.
			beanAnswer = new CreateNewServiceInstanceBean("wait", "unexpected-request", "The MobilisServer is currently in maintenance mode. Retry later.");			
		} else if (bean.serviceNamespace==null) {
			//ServiceNamespace is null
			beanAnswer = new CreateNewServiceInstanceBean("modify", "not-acceptable", "The service namespace is not set.");
		} else {
			ServiceContainer serviceContainer = null;
			
			// query ServiceContainer with a specific version
			if(bean.serviceVersion > -1){
				serviceContainer = MobilisManager.getInstance().getServiceContainer( bean.serviceNamespace, bean.serviceVersion );
			}
			// or the highest one in a given range of versions
			else{
				serviceContainer = MobilisManager.getInstance().getServiceContainer(
						bean.serviceNamespace,
						bean.minVersion,
						bean.maxVersion,
						ServiceContainerState.ACTIVE );
			}

			// if no service container was found or the container isn't in state active, respond an error
			if(null == serviceContainer || serviceContainer.getContainerState() != ServiceContainerState.ACTIVE){
				beanAnswer = BeanHelper.CreateErrorBean( 
						bean,
						"cancel",
						"service-unavailable",
						"This combination of service namespace and version is not available." );
				
				MobilisManager.getLogger().log( Level.WARNING,
						String.format( "Service instantiation error: %s", beanAnswer.toXML() ) );
			} else {
				// CoordinatorService#inCreateServiceInstanceSet: moved code to class ServiceContainer#startNewServiceInstance
				
				// try to start a new instance of the service
				try {
					MobilisService newService = serviceContainer.startNewServiceInstance();
					newService.setName( bean.serviceName );
					
					beanAnswer = new CreateNewServiceInstanceBean(newService.getAgent().getFullJid(),
							serviceContainer.getServiceVersion());
				// exception was thrown, respond an error 
				} catch ( StartNewServiceInstanceException e ) {
					beanAnswer = bean.clone();
					beanAnswer.setType( XMPPBean.TYPE_ERROR );
					
					beanAnswer.errorType = "wait";
					beanAnswer.errorCondition = "internal-server-error";
					beanAnswer.errorText = String.format( "Error starting new instance of service. Reason: %s", e.getMessage() );
					
					MobilisManager.getLogger().log( Level.WARNING,
							String.format( "Service instantiation error: %s", e.getMessage() ) );
				}
			}
		}
				
		beanAnswer.setTo(from); beanAnswer.setFrom(to);
		beanAnswer.setId(bean.getId());
		
		c.sendPacket(new BeanIQAdapter(beanAnswer));
	}
	
	/**
	 * Stops a running service instance.
	 * @param inBean bean which contains the information about the stopped service
	 */
	private void stopServiceInstance(StopServiceInstanceBean inBean){
		ServiceContainer container = null;
		StopServiceInstanceBean outBean = new StopServiceInstanceBean();
		
		// query service instance
		if(null != inBean.ServiceJid){
			container = MobilisManager.getInstance().getServiceContainerByRunningInstanceJid( inBean.ServiceJid );
		}
		
		if(null != container){
			// stop service instance
			container.shutdownServiceInstance( inBean.ServiceJid );
			
			outBean = (StopServiceInstanceBean)BeanHelper.CreateResultBean( inBean, outBean );
		}
		else{
			outBean = (StopServiceInstanceBean)BeanHelper.CreateErrorBean( inBean,
					"cancel", "item-not-found", "Cannot find service of provided jid!" );
		}
		
		getAgent().getConnection().sendPacket(new BeanIQAdapter(outBean));
	}
	
	/**
	 * Shuts down all managed application-specific services before it shuts
	 * down itself.
	 */
	@Override
	public void shutdown() throws Exception {
		for (AppSpecificService ass : appSpecificServicesList)
			if (ass!=null && ass.getAgent()!=null) ass.getAgent().shutdown();
		super.shutdown();
    }
	
	public boolean removeAppSpecificService(AppSpecificService service) {
		return appSpecificServicesList.remove(service);
	}
	
	/**
	 * @return true, if MobilisServer is in maintenance mode. false, otherwise.
	 */
	public boolean isInMaintenanceMode() {
		return maintenanceMode;
	}
	
	/**
	 * Set the MobilisServer into maintenance mode. In this mode, the Coordinator
	 * will not respond to Service Discovery request and will not create new service
	 * instances.
	 * @param maintenanceMode 
	 */
	public void setMaintenanceMode(boolean maintenanceMode) {
		this.maintenanceMode = maintenanceMode;
	}
	
		
	private String getVersionOfAppService(String ident) {
		for (AppSpecificService ass : appSpecificServicesList)		
			if (ass.getIdent().equals(ident))
				return "" + ass.getVersion();		
		return null;
	}

	public boolean transmitFile(File file, String fileDesc, String toJid){
		boolean transferSuccessful = false;
		OutgoingFileTransfer transfer = _fileTransferManager.createOutgoingFileTransfer(toJid);
        
		// check if file exists
		if(file.exists()) {
			MobilisManager.getLogger().log(Level.INFO ,"Start transmitting file: " + file.getAbsolutePath()
					+ " to: " + toJid);
	        try {
	        	// counter for sending tries
	        	int counter = 0;
	        	
	        	// start sending file
	        	transfer.sendFile(file, fileDesc);
	        	
	        	// while file is sending
	        	while(!transfer.isDone()) {
	        		// if counter of maximum tries has reached, cancel transmission
	        		if(counter == 15){
	        			MobilisManager.getLogger().log(Level.WARNING ,"ERROR: Filetransfer canceled. No Response!");
	        			break;
	        		}
	        		// increase try counter of sending tries
	        		counter++;
	        		
	        		// wait for 1000 ms and try sending the file again
	        		try {
	        			Thread.sleep(1000);
	        		} catch (InterruptedException e1) {
	        			MobilisManager.getLogger().log(Level.WARNING ,"ERROR: Thread interrupted while transmitting file: " + file.getName());
	        		}
	        	}
	        	
	        	transferSuccessful = transfer.isDone();
	        } catch (XMPPException e) {
	        	MobilisManager.getLogger().log(Level.WARNING ,"FileTransfer throws XMPPException:");
	        	e.printStackTrace();
	        }
		}
		
		MobilisManager.getLogger().log(Level.INFO ,"FileTransfer=" + transfer.getStreamID() + " successful=" + transferSuccessful);
		
		return transferSuccessful;
	}
}
