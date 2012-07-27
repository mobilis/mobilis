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
package de.tudresden.inf.rn.mobilis.android.xhunt.service;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * The Class ServiceConnector.
 */
public class ServiceConnector {

	/** The Constant TAG for logging. */
	public final static String TAG ="ServiceConnector";
	
	/** The context of the application. */
	private Context mContext;
	
	/** True if service is already bound to application. */
	private boolean mIsXHuntServiceBound = false;
	
	/** The XHuntService. */
	private XHuntService mXHuntService;
	
	/** The bound handlers for the XHuntService. */
	private ArrayList<Handler> mXHuntServiceBoundHandlers;	
    
    
    /**
     * Instantiates a new ServiceConnector.
     *
     * @param context the context of the application
     */
    public ServiceConnector(Context context){
    	this.mContext = context;
    	
    	mXHuntServiceBoundHandlers = new ArrayList<Handler>();
    }

    /** The ServiceConnection for the application. */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mXHuntService = ((XHuntService.LocalBinder)service).getService();

            Log.v(TAG, "XHuntService bound");
            
            // notify all registered handlers
            for(int i=0; i<mXHuntServiceBoundHandlers.size(); i++){
            	mXHuntServiceBoundHandlers.get(i).sendEmptyMessage(0);
			}
        }

        public void onServiceDisconnected(ComponentName className) {
            mXHuntService = null;
          
            Log.v(TAG, "XHuntService unbound");
        }
    };
    
    /**
     * Bind XHuntService.
     */
    public void doBindXHuntService() {
    	doBindXHuntService(null);
    }

    /**
     * Bind XHuntService with a handler to notify if service is bound.
     *
     * @param h the callback handler
     */
    public void doBindXHuntService(Handler h) {
    	if(h != null){
    		mXHuntServiceBoundHandlers.add(h);
    	}
    	
    	mContext.bindService(new Intent(mContext, XHuntService.class),
    			mServiceConnection, Context.BIND_AUTO_CREATE);
        mIsXHuntServiceBound = true;
    }

    /**
     * Unbind XHuntService.
     */
    public void doUnbindXHuntService() {
        if (mIsXHuntServiceBound) {
        	mContext.unbindService(mServiceConnection);
            mIsXHuntServiceBound = false;
        }
    }
    
    /**
     * Gets XHuntService.
     *
     * @return the XHuntService
     */
    public XHuntService getXHuntService(){
    	return mXHuntService;
    }
    
    /**
     * Checks if XHuntService is bound.
     *
     * @return true, if XHuntService is bound
     */
    public boolean isXHuntServiceBound(){
    	return mIsXHuntServiceBound;
    }
    
    /**
     * Start XHuntService for this application.
     */
    public void startXHuntService(){
		Intent i = new Intent(mContext.getApplicationContext(), XHuntService.class);
		mContext.getApplicationContext().startService(i);
    }

}
