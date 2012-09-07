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

package de.tudresden.inf.rn.mobilis.consoleclient.listener;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

import de.tudresden.inf.rn.mobilis.consoleclient.Controller;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.admin.ConfigureServiceBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.admin.InstallServiceBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.admin.RegisterServiceBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.admin.UninstallServiceBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.admin.UnregisterServiceBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.admin.UpdateServiceBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.coordination.CreateNewServiceInstanceBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.coordination.MobilisServiceDiscoveryBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.coordination.MobilisServiceInfo;
import de.tudresden.inf.rn.mobilis.xmpp.beans.coordination.StopServiceInstanceBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.deployment.PrepareServiceUploadBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.deployment.ServiceUploadConclusionBean;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanIQAdapter;

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
public class IQListener implements PacketListener {

	/** The controller. */
	private Controller _controller;
	
	/** True if all iqs should be logged to file. */
	private boolean _logIQsToFile = false;

	/**
	 * Instantiates a new iQ listener.
	 *
	 * @param controller the controller
	 */
	public IQListener(Controller controller) {
		_controller = controller;
	}

	/* (non-Javadoc)
	 * @see org.jivesoftware.smack.PacketListener#processPacket(org.jivesoftware.smack.packet.Packet)
	 */
	@Override
	public void processPacket( Packet arg0 ) {
		if ( isLogIQsToFile() ) {
			_controller.getLog().writeToFile(
					( "Incoming IQ: " + ( (IQ)arg0 ).toXML().replaceAll( "<", "\n<" ) ) );
		}

		// just log incoming admin iqs to console
		if ( arg0 instanceof BeanIQAdapter ) {
			XMPPBean inBean = ( (BeanIQAdapter)arg0 ).getBean();

			if ( inBean.getType() == XMPPBean.TYPE_ERROR ) {
				_controller.getLog().writeErrorToConsole(
						String.format( "Incoming IQ of type error:%s", ( (IQ)arg0 ).toXML()
								.replaceAll( "<", "\n<" ) ) );
			} else {
				if ( inBean instanceof PrepareServiceUploadBean ) {
					PrepareServiceUploadBean cBean = (PrepareServiceUploadBean)inBean;

					_controller.getLog().writeToConsole(
							String.format( "Prepare service successful: %b",
									cBean.AcceptServiceUpload ) );
				} else if ( inBean instanceof ServiceUploadConclusionBean ) {
					ServiceUploadConclusionBean cBean = (ServiceUploadConclusionBean)inBean;
					StringBuilder sb = new StringBuilder();
					
					if( cBean.UploadSuccessful ){
						sb.append( "Service upload: Negotiating Stream...\nFilename=" ).append( cBean.FileName );
					}
					else{
						sb.append( "Service uplaod failed." );
					}
					
					if( null != cBean.Message ){
						sb.append( "\nmessage=" ).append( cBean.Message );
					}
					
					_controller.getLog().writeToConsole( sb.toString() );
				} else if ( inBean instanceof InstallServiceBean ) {
					InstallServiceBean cBean = (InstallServiceBean)inBean;
					StringBuilder sb = new StringBuilder();

					sb.append( "Install service successful: " )
							.append( cBean.InstallationSucessful );

					if ( cBean.InstallationSucessful ) {
						sb.append( "\nnamespace=" ).append( cBean.ServiceNamespace );
						sb.append( "\nversion=" ).append( cBean.ServiceVersion );
					}

					if ( null != cBean.Message ) {
						sb.append( "\nmessage=" ).append( cBean.Message );
					}

					_controller.getLog().writeToConsole( sb.toString() );
				} else if ( inBean instanceof ConfigureServiceBean ) {
					_controller.getLog().writeToConsole( "Configure service successful." );
				} else if ( inBean instanceof RegisterServiceBean ) {
					RegisterServiceBean cBean = (RegisterServiceBean)inBean;

					_controller.getLog().writeToConsole(
							String.format( "Register service successful: %b",
									cBean.RegistrationSuccessful ) );
				} else if ( inBean instanceof UnregisterServiceBean ) {
					_controller.getLog().writeToConsole( "Unregister service successful." );
				} else if ( inBean instanceof UninstallServiceBean ) {
					_controller.getLog().writeToConsole( "Uninstall service successful." );
				} else if ( inBean instanceof UpdateServiceBean ) {
					UpdateServiceBean cBean = (UpdateServiceBean)inBean;

					_controller.getLog().writeToConsole(
							String.format(
									"Update service successful. New\nnamespace=%s\nversion=%d",
									cBean.NewServiceNamespace, cBean.NewServiceVersion ) );
				} else if ( inBean instanceof CreateNewServiceInstanceBean ) {
					CreateNewServiceInstanceBean cBean = (CreateNewServiceInstanceBean)inBean;

					_controller.getLog().writeToConsole(
							String.format( "start service instant successful. New\njid= %s",
									cBean.jidOfNewService ) );
				} else if ( inBean instanceof MobilisServiceDiscoveryBean ) {
					MobilisServiceDiscoveryBean cBean = (MobilisServiceDiscoveryBean)inBean;
					StringBuilder sb = new StringBuilder();

					if ( null == cBean.getDiscoveredServices() || cBean.getDiscoveredServices().size() < 1 ) {
						sb.append( "No discovery result." );
					} else {
						sb.append( "Discovered:" );
						for ( MobilisServiceInfo service : cBean.getDiscoveredServices() ) {
							sb.append( "\nnamespace=" ).append( service.getServiceNamespace() );
							sb.append( " version=" ).append( service.getVersion() );

							if ( null != service.getServiceName() ) {
								sb.append( " name=" ).append( service.getServiceName() );
							}

							if ( null != service.getJid() ) {
								sb.append( " jid=" ).append( service.getJid() );
							}

							if ( null != service.getMode() ) {
								sb.append( " mode=" ).append( service.getMode() );
							}

							if ( service.getInstances() > -1 ) {
								sb.append( " instances=" ).append( service.getInstances() );
							}
						}
						
						_controller.getLog().writeToConsole( sb.toString() );
					}
				} else if ( inBean instanceof StopServiceInstanceBean ) {
					_controller.getLog().writeToConsole( "Stopping service instance successful." );
				}
			}
		}
	}

	/**
	 * Checks if logging to file is on.
	 *
	 * @return the _logIQsToFile
	 */
	public boolean isLogIQsToFile() {
		return _logIQsToFile;
	}

	/**
	 * Sets the logging to file.
	 *
	 * @param _logIQsToFile true, if the incoming iqs should be logged into log file
	 */
	public void setLogIQsToFile( boolean _logIQsToFile ) {
		this._logIQsToFile = _logIQsToFile;
	}

}
