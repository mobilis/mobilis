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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.NodeInformationProvider;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;

import de.tudresden.inf.rn.mobilis.server.MobilisManager;
import de.tudresden.inf.rn.mobilis.server.agents.MobilisAgent;
import de.tudresden.inf.rn.mobilis.xmpp.beans.Mobilis;
import de.tudresden.inf.rn.mobilis.xmpp.beans.coordination.StopServiceInstanceBean;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanIQAdapter;

public abstract class MobilisService implements PacketListener, NodeInformationProvider {	  

	protected MobilisAgent mAgent = null;
	private int _serviceVersion = 1;
	
    private Map<String, Map<String, Object>> mUserSettings;
    private Map<String, Object> mDefaultSettings;
    
    private String _serviceName = null;
	private boolean selfSuicideMode = false;
    
    public MobilisService() {
		mUserSettings = Collections.synchronizedMap(new HashMap<String, Map<String, Object>>());
		try {
			mDefaultSettings = MobilisManager.getInstance().getSettings("services", getIdent());
		} catch (Exception e) {
			mDefaultSettings = Collections.synchronizedMap(new HashMap<String, Object>());
			MobilisManager.getLogger().warning("Mobilis Service (" + getIdent() + ") could not read configuration, using empty settings instead.");
		}
    }

    public void startup() throws Exception {
		if (mAgent == null) {
			try {
				String agentName = MobilisManager.getInstance().getSettingString("services", getIdent(), "agent");
				mAgent = MobilisManager.getInstance().getAgent(agentName);
			} catch (Exception e) {
				throw e;
			}
		}
		
		try {
			startup(mAgent);
		} catch (Exception e) {
			throw e;
		}
    }

    public void startup(MobilisAgent agent) throws Exception {
		try {
			mAgent = agent;
			
			// packet listener
			registerPacketListener();
			
			// logging
			MobilisManager.getLogger().info("Mobilis Service (" + getIdent() + ") started up.");
		} catch (Exception e) {
			throw e;
		}
    }

    public void shutdown() throws Exception {
		// packet listener
		mAgent.getConnection().removePacketListener(this);
		
		// logging
		MobilisManager.getLogger().info("Mobilis Service (" + getNamespace() + ") shut down.");
		
		// TODO: setting a boolean is a quickfix for service shutdown behavior (see MO-8) 
		if (!selfSuicideMode) {
			selfSuicideMode = true;
			String fullJid = getAgent().getFullJid();
			String jid = fullJid.split("/")[0];
			StopServiceInstanceBean bean = new StopServiceInstanceBean(fullJid);
			
			bean.setTo(jid + "/Coordinator");
			getAgent().getConnection().sendPacket(new BeanIQAdapter(bean));
			
			mAgent.getConnection().disconnect();			
		}
    }
    
    // getter + setter methods
    
    protected void setSettingString(String jid, String name, String value) {
    	synchronized(mUserSettings) {
    		if (!mUserSettings.containsKey(jid)) {
    			mUserSettings.put( jid, Collections.synchronizedMap(new HashMap<String, Object>()) );
    		}
    		Map<String, Object> settings = mUserSettings.get(jid);
   			synchronized(settings) {
   				settings.put( name, value );
   			}
    	}
    }

	protected void setSettingStrings(String jid, String name, Map<String,String> value) {
    	synchronized(mUserSettings) {
    		if (!mUserSettings.containsKey(jid)) {
    			mUserSettings.put( jid, Collections.synchronizedMap(new HashMap<String, Object>()) );
    		}
    		Map<String, Object> settings = mUserSettings.get(jid);
   			synchronized(settings) {
   				settings.put( name, value );
   			}
    	}
    }
    
    protected String getSettingString(String name) {
    	Object value = null;
    	
    	try{
			synchronized(mDefaultSettings) {
	    		if (mDefaultSettings.containsKey(name)) {
	    			value = mDefaultSettings.get(name);
	    		}
			}
    	}
    	catch (Exception e) {
			MobilisManager.getLogger().log( Level.WARNING, "Cannot fetch service settings value of service: " + e.getMessage() );
		}
    	
    	return value instanceof String ? (String)value : null;
    }
    
    @SuppressWarnings("unchecked")
	protected Map<String,String> getSettingStrings(String name) {
    	Object value = null;
		synchronized(mDefaultSettings) {
    		if (mDefaultSettings.containsKey(name)) {
    			value = mDefaultSettings.get(name);
    		}
		}
    	return value instanceof Map<?,?> ? (Map<String,String>)value : null;
    }
    
    protected String getSettingString(String jid, String name) {
    	synchronized(mUserSettings) {
    		if (mUserSettings.containsKey(name)) {
    			Object value = null;
    			Map<String, Object> settings = mUserSettings.get(name);
    			synchronized(settings) {
    				if (settings.containsKey(jid)) {
        				value = settings.get(jid);
    				}
    			}
    	    	return (value instanceof String ? (String)value : null);
    	    } else {
        		return getSettingString(name);
        	}
    	}
    }
    
    @SuppressWarnings("unchecked")
	protected Map<String,String> getSettingStrings(String jid, String name) {
    	synchronized(mUserSettings) {
    		if (mUserSettings.containsKey(name)) {
    			Object value = null;
    			Map<String, Object> settings = mUserSettings.get(name);
    			synchronized(settings) {
    				if (settings.containsKey(jid)) {
        				value = settings.get(jid);
    				}
    			}
    	    	return (value instanceof Map<?,?> ? (Map<String,String>)value : null);
    	    } else {
        		return getSettingStrings(name);
        	}
    	}
    }
    
    public DiscoverItems.Item getDiscoverItem() {
    	Item item = new DiscoverItems.Item(mAgent.getConnection().getUser());
		item.setName(getName());
		item.setNode(getNode());
		return item;
    }

	public String getNode() {
		return MobilisManager.discoServicesNode + "/" + getIdent();
	}
	
	/**
	 * Gets the name of this service (this is not the jid)
	 * @return the name of the service
	 */
	public String getName() {
		String result = null;		
		
		if(null != _serviceName){
			result = _serviceName;
		}
		else if (getSettingString("name") != null) {
			result = getSettingString("name");
		} else if (getSettingString("description") != null) {
			result = getSettingString("description");
		} else {
			result = "Mobilis Service";
		}
		
		return result;
	}
	
	/**
	 * Sets the name of this service
	 * @param name the new name
	 */
	public void setName(String name){
		_serviceName = name;
	}
	
    public MobilisAgent getAgent() {
    	return mAgent;
    }
    
    public void setAgent(MobilisAgent agent) {
    	this.mAgent = agent ;
    }
    
	public String getIdent() {
		return getClass().getSimpleName();
	}
	
	public String getNamespace() {
		return Mobilis.NAMESPACE + "#services/" + getIdent();
	}
	
	/**
	 * Gets the version of the MobilisService. Each MobilisService SHOULD override this method
	 * because otherwise simply "1" is returned.
	 * @return the version of the MobilisService if the version is set, otherwise: "1"
	 */
	public int getVersion() {
		return _serviceVersion;
	}
	
	protected void setVersion(int version){
		_serviceVersion = version;
	}
    
    // XMPP related functions
    
    @Override
    public List<DiscoverInfo.Identity> getNodeIdentities() {
		List<DiscoverInfo.Identity> identities = new ArrayList<DiscoverInfo.Identity>();
		identities.add(new DiscoverInfo.Identity("component", getName()));
		if (getNodeItems().size() > 0) {
			identities.add(new DiscoverInfo.Identity("hierarchy", "branch"));
		} else {
			identities.add(new DiscoverInfo.Identity("hierarchy", "leaf"));
		}
		return identities;
	}
	
	@Override
	public List<String> getNodeFeatures() {
		List<String> features = new ArrayList<String>();
		features.add(MobilisManager.discoNamespace);
		return features;
	}

	@Override
    public List<DiscoverItems.Item> getNodeItems() {
        List<DiscoverItems.Item> items = new ArrayList<DiscoverItems.Item>();
        return items;
    }
    
    protected abstract void registerPacketListener();

	@Override
	public void processPacket(Packet arg0) {
	}
    
}
