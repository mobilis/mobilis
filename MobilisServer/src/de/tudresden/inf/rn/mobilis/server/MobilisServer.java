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

package de.tudresden.inf.rn.mobilis.server;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jivesoftware.smack.Connection;

import de.tudresden.inf.rn.mobilis.server.persistence.MobilisPersistence;

/**
 * The main class of the application.
 */
public class MobilisServer extends SingleFrameApplication {
	
	protected MobilisServerView parentView;

    /**
     * At startup create and show the main frame of the application.
     * Also startup Mobilis Manager and associate with main frame.
     */
    @Override protected void startup() {
    	// prepare DB
    	MobilisPersistence.getInstance();
    	
    	parentView = new MobilisServerView(this);
    	Connection.DEBUG_ENABLED = true;
    	MobilisManager.getInstance().registerServerView(parentView);
    	show(parentView);
    	//Automatically startup the MobilsServer on application start
    	parentView.getStartupButton().doClick();
    }

    /**
     * Shutdown Mobilis Manager and unregister with main frame.
     */
    @Override protected void shutdown() {
    	MobilisManager.getInstance().shutdown();
    	MobilisManager.getInstance().unregisterServerView(parentView);
    	MobilisPersistence.getInstance().shutdown();
    	super.shutdown();
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of MobilisServer
     */
    public static MobilisServer getApplication() {
        return Application.getInstance(MobilisServer.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(MobilisServer.class, args);
    }
}
