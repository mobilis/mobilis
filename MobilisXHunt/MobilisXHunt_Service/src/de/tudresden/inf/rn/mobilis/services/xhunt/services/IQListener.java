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
package de.tudresden.inf.rn.mobilis.services.xhunt.services;

import java.util.logging.Logger;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

import de.tudresden.inf.rn.mobilis.services.xhunt.Connection;
import de.tudresden.inf.rn.mobilis.services.xhunt.XHunt;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.JoinGameRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.JoinGameResponse;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.PlayerExitRequest;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.PlayerExitResponse;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.server.BeanIQAdapter;

/**
 * The listener interface for receiving IQ events.
 * The class that is interested in processing a IQ
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addIQListener<code> method. When
 * the IQ event occurs, that object's appropriate
 * method is invoked.
 *
 * @see IQEvent
 */
public class IQListener implements PacketListener{
		
	/** The service controller. */
	private XHunt control;
	
	/** The class specific Logger object. */
	private final static Logger LOGGER = Logger.getLogger(IQListener.class.getCanonicalName());

	/**
	 * Instantiates a new IQListener.
	 *
	 * @param control service controller, who administrates the whole life cycle
	 */
	public IQListener(XHunt control){
		this.control = control;
	}	

	// No IIncoming Interface is used in here
	/* (non-Javadoc)
	 * @see org.jivesoftware.smack.PacketListener#processPacket(org.jivesoftware.smack.packet.Packet)
	 */
	@Override
	public void processPacket(Packet packet) {
		LOGGER.info("incoming packet: " + packet.toXML());
		
		// Check if the incoming Packet is of type IQ (BeanIQAdapter is just a wrapper)
		if (packet instanceof BeanIQAdapter) {
			// Convert packet to @see XMPPBean
			XMPPBean xmppBean = control.getConnection().unpackBeanIQAdapter( (BeanIQAdapter)packet );
	    		
    		// Check if the incoming packet is a JoinGameBean for a new spectator
    		if( xmppBean instanceof JoinGameRequest
    				&& xmppBean.getType()==XMPPBean.TYPE_SET
    				&& ((JoinGameRequest) xmppBean).getIsSpectator()) {
    			
    			// Save spectator in spectator list of service
    			control.addSpectator(xmppBean.getFrom());
    			LOGGER.info("Spectator " + xmppBean.getFrom() + " added");
    			
    			// Send the result
    			control.getConnection().getProxy().getBindingStub().sendXMPPBean( 
    					Connection.createXMPPBeanResult( new JoinGameResponse(), xmppBean )
    					);
    			//sendXMPPBeanResult(new JoinGameResponse(), xmppBean);    	
    			
    		// Check if the incoming packet is a PlayerExitBean to remove a spectator
    		} else if (xmppBean instanceof PlayerExitRequest 
    				&& xmppBean.getType()==XMPPBean.TYPE_SET
    				&& ((PlayerExitRequest) xmppBean).getIsSpectator()) {
    			
    			// Remove spectator from spectator list
    			control.removeSpectator(xmppBean.getFrom());
    			
    			// Send the result
    			control.getConnection().getProxy().getBindingStub().sendXMPPBean( 
    					Connection.createXMPPBeanResult( new PlayerExitResponse(), xmppBean )
    					);
    			//sendXMPPBeanResult(new PlayerExitResponse(), xmppBean);
    			
    		// Else bean comes from an XHuntPlayer
    		} else {
	    		// If Bean was verified, handle Bean in current GameState
	    		if(control.getConnection().verifyIncomingBean(xmppBean)) {
	    			control.getActGame().processPacket(xmppBean);
	    		}
	    		// If Bean is of type ERROR it will be logged
	    		else if(xmppBean.getType() == XMPPBean.TYPE_ERROR) {
	    			LOGGER.severe("ERROR: Bean of Type ERROR received: " 
	    					+ "type: " + xmppBean.errorType  
	    					+ " condition:" + xmppBean.errorCondition
	    					+ " text: " + xmppBean.errorText
	    					+ "\n" + control.getConnection().beanToString(xmppBean));
	    		}
    		}
		}
	}

}
