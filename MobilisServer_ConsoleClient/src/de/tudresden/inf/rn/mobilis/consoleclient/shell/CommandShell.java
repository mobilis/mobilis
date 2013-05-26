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

package de.tudresden.inf.rn.mobilis.consoleclient.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.tudresden.inf.rn.mobilis.consoleclient.Controller;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.admin.AgentConfigInfo;
import de.tudresden.inf.rn.mobilis.xmpp.beans.admin.ConfigureServiceBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.admin.InstallServiceBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.admin.UninstallServiceBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.admin.UnregisterServiceBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.admin.UpdateServiceBean;

/**
 * The Class CommandShell to process input commands.
 */
public class CommandShell {

	/** The command map. */
	private CommandMap _commandMap;

	/** The controller. */
	private Controller _controller;

	/** The command interpreter. */
	private CommandInterpreter _commandInterpreter;

	/** True if this is running. */
	private boolean _isRunning;

	/**
	 * Instantiates a new command shell.
	 * 
	 * @param controller
	 *            the controller
	 */
	public CommandShell(Controller controller) {
		_controller = controller;
		_commandMap = new CommandMap();
		_commandInterpreter = new CommandInterpreter( controller );

		_isRunning = false;
	}

	/**
	 * Executes a command.
	 * 
	 * @param input
	 *            the command input
	 */
	public void executeCommand( String input ) {
		if ( null == input || input.length() < 1 )
			return;

		// extract command and parameters separated by spaces
		String[] inputArray = input.indexOf( " " ) > -1 ? input.split( " " )
				: new String[] { input };

		// switch the detected command
		switch ( parseCommand( inputArray[0] ) ) {
		case Configure:
			// parameters: namespace, version, mode
			if( inputArray.length > 3 ){
				ConfigureServiceBean bean = new ConfigureServiceBean();

				bean.AgentConfig = new AgentConfigInfo();
				bean.ServiceNamespace = inputArray[1];
				bean.ServiceVersion = Integer.parseInt( inputArray[2] );	
				bean.AgentConfig.Mode = inputArray[3];				
				
				bean.setTo( _controller.getSettings().getMobilisAdminJid() );
				bean.setType( XMPPBean.TYPE_SET );
				
				_controller.getConnection().sendXMPPBean( bean );
			}
			else if ( inputArray.length > 1 ) {
				_commandInterpreter.configure( inputArray[1] );
			} else {
				_controller.getLog().writeToConsole(
						String.format( "Missing parameter for command < %s >", input ) );
			}

			break;
		case Connect:
			_controller.getLog().writeToConsole(
					"connection succesful? " + _controller.getConnection().connectToXMPPServer() );

			_controller.getLog().writeToConsole(
					"login succesful? " + _controller.getConnection().loginXMPP() );

			break;

		case Discover:
			// 3 paramers: namespace, version, query msdl
			if ( inputArray.length > 3 ) {
				_controller.getConnection().sendServiceDiscovery( inputArray[1],
						Integer.parseInt( inputArray[2] ), Boolean.parseBoolean( inputArray[3] ) );
			}
			// 2 paramers: namespace, version
			else if ( inputArray.length > 2 ) {
				_controller.getConnection().sendServiceDiscovery( inputArray[1],
						Integer.parseInt( inputArray[2] ), false );
			}
			// 1 paramer: namespace and all versions
			else if ( inputArray.length > 1 ) {
				_controller.getConnection().sendServiceDiscovery( inputArray[1], -1, false );
			}
			// 0 parameters: all services
			else {
				_controller.getConnection().sendServiceDiscovery( null, -1, false );
			}

			break;

		case Exit:
			if ( inputArray.length > 1 && null != inputArray[1] && inputArray[1].equals( "-h" ) )
				stop( true );
			else
				stop( false );

			break;

		case Help:
			StringBuilder sb = new StringBuilder();

			sb.append( "\nconfigure <namespace> <version> <mode>" );
			sb.append( "\nconnect" );
			sb.append( "\ndiscover [<namespace>] [<version>] [<query msdl>]" );
			sb.append( "\nexit [-h]" );
			sb.append( "\nhelp" );
			sb.append( "\ninstall <filename>" );
			sb.append( "\nregister <namespace> <version>" );
			sb.append( "\nsend [single | multi] <path>" );
			sb.append( "\nset <clientnode | clientpw | clientresource | coordinatorresource | adminresource | deploymentresource | servernode | serverresource | serveraddress | serverport | serverdomain> <value>" );
			sb.append( "\nstartsvc <namespace> [<version>]" );
			sb.append( "\nstopsvc <jid>" );
			sb.append( "\nuninstall <namespace> <version>" );
			sb.append( "\nunregister <namespace> <version>" );
			sb.append( "\nupdate <filename> <namespace> <version>" );
			sb.append( "\nxmppinfo" );
			sb.append( "\n\n<...> == required parameter; [<...>] == optional parameter" );

			_controller.getLog().writeToConsole( sb.toString() );

			break;

		case Install:
			if ( inputArray.length > 1 ) {

				InstallServiceBean ibean = new InstallServiceBean( inputArray[1] );
				ibean.setTo( _controller.getSettings().getMobilisAdminJid() );
				ibean.setType( XMPPBean.TYPE_SET );

				_controller.getConnection().sendXMPPBean( ibean );
			} else
				_controller.getLog().writeToConsole( "Missing parameter for command <Install>" );

			break;

		case Register:
			if ( inputArray.length > 2 ) {
				try {
					_commandInterpreter.register( inputArray[1], Integer.parseInt( inputArray[2] ) );
				} catch ( NumberFormatException e ) {
					e.printStackTrace();
				}
			} else
				_controller.getLog().writeToConsole( "Missing parameter for command <Register>" );

			break;

		case Send:
			if ( inputArray.length == 2 ) {
				_commandInterpreter.sendFile( inputArray[1], false, false);
			} else if (inputArray.length > 2) {
				if (inputArray[1].equals("single")) {
					_commandInterpreter.sendFile(inputArray[2], true, true);
				} else if (inputArray[1].equals("multi")) {
					_commandInterpreter.sendFile(inputArray[2], true, false);
				} else {
					_controller.getLog().writeToConsole( "Unknown parameter " + inputArray[1] + " for command <Send>");
				}
			} else
				_controller.getLog().writeToConsole( "Missing parameter for command <Send>" );

			break;

		case Set:
			if ( inputArray.length > 2 ) {
				if ( inputArray[1].toLowerCase().equals( "clientnode" ) ) {
					_controller.getSettings().setClientNode( inputArray[2] );
				} else if ( inputArray[1].toLowerCase().equals( "clientpw" ) ) {
					_controller.getSettings().setClientPassword( inputArray[2] );
				} else if ( inputArray[1].toLowerCase().equals( "clientresource" ) ) {
					_controller.getSettings().setClientResource( inputArray[2] );
				} else if ( inputArray[1].toLowerCase().equals( "coordinatorresource" ) ) {
					_controller.getSettings().setMobilisCoordinatorResource( inputArray[2] );
				} else if ( inputArray[1].toLowerCase().equals( "adminresource" ) ) {
					_controller.getSettings().setMobilisAdminResource( inputArray[2] );
				} else if ( inputArray[1].toLowerCase().equals( "deploymentresource" ) ) {
					_controller.getSettings().setMobilisDeploymentResource( inputArray[2] );
				} else if ( inputArray[1].toLowerCase().equals( "servernode" ) ) {
					_controller.getSettings().setMobilisServerNode( inputArray[2] );
				} else if ( inputArray[1].toLowerCase().equals( "serverresource" ) ) {
					_controller.getSettings().setMobilisServerResource( inputArray[2] );
				} else if ( inputArray[1].toLowerCase().equals( "serveraddress" ) ) {
					_controller.getSettings().setXMPPServerAddress( inputArray[2] );
				} else if ( inputArray[1].toLowerCase().equals( "serverport" ) ) {
					_controller.getSettings().setXMPPServerPort( Integer.parseInt( inputArray[2] ) );
				} else if ( inputArray[1].toLowerCase().equals( "serverdomain" ) ) {
					_controller.getSettings().setXMPPServerDomain( inputArray[2] );
				} else {
					_controller.getLog().writeToConsole(
							"Unknown parameters <" + inputArray[1] + ", " + inputArray[2]
									+ "> for command <" + input + ">" );
				}
			} else
				_controller.getLog().writeToConsole( "Missing parameter for command <Set>" );

			break;

		case StartSvc:
			// 3 parameters: namespace, min version, max version
			if ( inputArray.length > 3 ) {
				_controller.getConnection().sendCreateService( inputArray[1],
						Integer.parseInt( inputArray[2] ), Integer.parseInt( inputArray[3] ) );
			}
			// 2 parameters: namespace, version
			else if ( inputArray.length > 2 ) {
				_controller.getConnection().sendCreateService( inputArray[1],
						Integer.parseInt( inputArray[2] ) );
			}
			// 1 parameter: namespace
			else if ( inputArray.length > 1 ) {
				_controller.getConnection().sendCreateService( inputArray[1], -1 );
			} else
				_controller.getLog().writeToConsole( "Missing parameter for command <StartSvc>" );

			break;

		case StopSvc:
			// jid
			if ( inputArray.length > 1 ) {
				_commandInterpreter.stopService( inputArray[1] );
			} else
				_controller.getLog().writeToConsole(
						String.format( "Missing parameter for command < %s >", input ) );

			break;
		case Uninstall:
			if ( inputArray.length > 1 ) {
				// 2 parameters: namespace, version
				if ( inputArray.length > 2 ) {
					int version = Integer.parseInt( inputArray[2] );
					String namespace = inputArray[1];
					_commandInterpreter.uninstallService(namespace, version);
				} else {
					_controller.getLog().writeToConsole(
							"Missing parameter for command <" + input + ">" );
				}
			} else
				_controller.getLog().writeToConsole( "Missing parameter for command <Uninstall>" );

			break;

		case Unregister:
			// 2 parameters: namespace, version
			if ( inputArray.length > 2 ) {
				int version = Integer.parseInt( inputArray[2] );
				UnregisterServiceBean uibean = new UnregisterServiceBean( inputArray[1], version );

				uibean.setTo( _controller.getSettings().getMobilisAdminJid() );
				uibean.setType( XMPPBean.TYPE_SET );

				_controller.getConnection().sendXMPPBean( uibean );
			} else
				_controller.getLog().writeToConsole(
						"Missing parameter for command <" + input + ">" );

			break;

		case Update:
			// 3 parameters: filename, namespace, version
			if ( inputArray.length > 3 ) {
				int version = Integer.parseInt( inputArray[3] );
				UpdateServiceBean ubean = new UpdateServiceBean( inputArray[1], inputArray[2],
						version );

				ubean.setTo( _controller.getSettings().getMobilisAdminJid() );
				ubean.setType( XMPPBean.TYPE_SET );

				_controller.getConnection().sendXMPPBean( ubean );
			} else
				_controller.getLog().writeToConsole(
						"Missing parameter for command <" + input + ">" );

			break;

		case XMPPInfo:
			_controller.getConnection().printXMPPInfo();

			break;
			
		case FastInstall:
			if (inputArray.length > 1) {
				_controller.getLog().writeToConsole("Not yet implemented!");
			} else {
				_controller.getLog().writeToConsole(
						"Missing parameter for command <" + input + ">" );
			}
			break;

		default:
			_controller.getLog().writeToConsole( "Unknown command <" + input + ">" );

			break;
		}
	}

	/**
	 * Parse a command.
	 * 
	 * @param input
	 *            the input command
	 * @return the command as enum
	 */
	private CommandEnum parseCommand( String input ) {
		input = input.toLowerCase();

		return input.contains( " " ) ? _commandMap
				.get( input.subSequence( 0, input.indexOf( " " ) ) ) : _commandMap.get( input );
	}

	/**
	 * Starts this shell.
	 */
	public void start() {
		try {
			InputStreamReader isr = new InputStreamReader( System.in );
			BufferedReader br = new BufferedReader( isr );
			String input = "";

			_isRunning = true;

			while ( _isRunning ) {
				System.out.println( "Type command:- " );
				input = br.readLine();

				executeCommand( input );
			}
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Stops this shell.
	 * 
	 * @param closeApplication
	 *            true, if app should be closed
	 */
	public void stop( boolean closeApplication ) {
		_controller.getConnection().disconnect();

		_controller.getLog().writeToConsole( "Connection closed." );

		if ( closeApplication ) {
			_isRunning = false;
			_controller.getLog().writeToConsole( "System exit." );
			System.exit( 0 );
		}
	}
}
