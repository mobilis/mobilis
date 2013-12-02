package de.tudresden.inf.rn.mobilis.server.services.Coordination;

import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo.Feature;

import de.tudresden.inf.rn.mobilis.server.MobilisManager;

/**
 * 
 * @author Philipp Grubitzsch
 * Helper Class of CoordinatorService
 */
public class CoordinationHelper {
	
	/**
	 * Checks the local Runtime Roster for remote Service with given NS + Version and returns a Set of JIDs of remote Runtime supporting this service 
	 * @param serviceNamespace
	 * @param serviceVersion
	 * @return
	 */
	public static HashSet<String> getServiceOnRemoteRuntime(String serviceNamespace, int serviceVersion){
		Roster runtimeRoster = MobilisManager.getInstance().getRuntimeRoster();
		RosterGroup rg = runtimeRoster.getGroup(MobilisManager.remoteServiceGroup + "services");
		HashSet<String> runtimeJIDs = new  HashSet<String>();
		
		if(rg != null){
			//check all entries from the "services" RosterGroup
			for(RosterEntry entry : rg.getEntries()){
				
				//get resources for every entry and then the serviceDiscoveryInfo for every resource
				for ( Iterator<Presence> iter = runtimeRoster.getPresences(entry.getUser()); iter.hasNext(); )
				{
					
					Presence presence = iter.next();
					
					String fullJIDofService =  presence.getFrom();
					//just look for online services
					if(presence.isAvailable()){
						DiscoverInfo dInfo;
						try {
							dInfo = MobilisManager.getInstance().getServiceDiscoveryManager().discoverInfo(fullJIDofService);
							 
							  //check all feature vars of the DiscoverInfo of a resource for the mobilis service URN
							  if(dInfo != null){
								  Iterator<Feature> infos  = dInfo.getFeatures();
								  boolean ready=false;
								  
								  //check if service caps match the requested NS and Version of the DiscoBeans. if so add runtimeJID
								  while(infos.hasNext() && !ready){
									  String s = infos.next().getVar();
									  
									  //fast check for service agent. if not same ns / version -> skip to next presence
									  if (s.contains(MobilisManager.discoNamespace + "/service#")){
										  s = s.replace("http://mobilis.inf.tu-dresden.de/service#", "");
										  String[] segs = s.split( Pattern.quote( "," ) );
										  if(serviceNamespace.equals((segs[0].replaceFirst("servicenamespace=", "")))){
											  if((serviceVersion<0) || Integer.toString(serviceVersion).equals(segs[1].replaceFirst("version=", ""))){
												  runtimeJIDs.add(segs[3].replaceFirst("rt=", ""));
												  ready=true;
											  } else ready=true;
										  } else ready=true;
										  
									  }
								  }
							  }
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					}
				}
			}
		}
		return runtimeJIDs;
		
	}

}
