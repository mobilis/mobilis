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

package de.tudresden.inf.rn.mobilis.consoleclient.shell;

import java.util.HashMap;

/**
 * The Class CommandMap which maps the commands from CommandEnum to strings.
 */
public class CommandMap extends HashMap< String, CommandEnum > {

	/** The Constant serialVersionUID for serialization. */
	private static final long serialVersionUID = -8541848249247628453L;

	/**
	 * Instantiates a new command map.
	 */
	public CommandMap() {
		super();

		initCommands();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	@Override
	public CommandEnum get( Object key ) {
		return null != super.get( key ) ? super.get( key ) : CommandEnum.Unknown;
	}

	/**
	 * Inits the commands.
	 */
	private void initCommands() {
		put( "configure", CommandEnum.Configure );
		put( "connect", CommandEnum.Connect );
		put( "discover", CommandEnum.Discover );
		put( "exit", CommandEnum.Exit );
		put( "help", CommandEnum.Help );
		put( "install", CommandEnum.Install );
		put( "ping", CommandEnum.Ping );
		put( "register", CommandEnum.Register );
		put( "send", CommandEnum.Send );
		put( "set", CommandEnum.Set );
		put( "startsvc", CommandEnum.StartSvc );
		put( "stopsvc", CommandEnum.StopSvc );
		put( "uninstall", CommandEnum.Uninstall );
		put( "unknown", CommandEnum.Unknown );
		put( "unregister", CommandEnum.Unregister );
		put( "update", CommandEnum.Update );
		put( "xmppinfo", CommandEnum.XMPPInfo );
		put( "fastinstall", CommandEnum.FastInstall);
		put( "sync", CommandEnum.Sync);
	}
}
