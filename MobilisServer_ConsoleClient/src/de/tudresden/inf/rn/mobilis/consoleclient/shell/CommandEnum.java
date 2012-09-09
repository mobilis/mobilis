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

/**
 * The Enum CommandEnum to map the input commands to integer.
 */
public enum CommandEnum {

	/** The Configure command. */
	Configure,

	/** The Connect command. */
	Connect,

	/** The Discover command. */
	Discover,

	/** The Exit command. */
	Exit,

	/** The Help command. */
	Help,

	/** The Install command. */
	Install,

	/** The Ping command. */
	Ping,

	/** The Register command. */
	Register,

	/** The Send command. */
	Send,

	/** The Set command. */
	Set,

	/** The Start service command. */
	StartSvc,

	/** The Stop service command. */
	StopSvc,

	/** The Uninstall command. */
	Uninstall,

	/** The Unknown command. */
	Unknown,

	/** The Unregister command. */
	Unregister,

	/** The Update command. */
	Update,

	/** The XMPP info command. */
	XMPPInfo,
	
	/** The FastInstall command. */
	FastInstall

}
