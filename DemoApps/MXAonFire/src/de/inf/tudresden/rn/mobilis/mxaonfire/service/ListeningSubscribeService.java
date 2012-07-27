package de.inf.tudresden.rn.mobilis.mxaonfire.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.tudresden.inf.rn.mobilis.mxa.IXMPPService;
import de.tudresden.inf.rn.mobilis.mxa.MXAController;
import de.tudresden.inf.rn.mobilis.mxa.MXAListener;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.ISubscribeCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.pubsub.IPubSubService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * This service waits for publish events from
 * the callback and saves them, the gui then can get them.
 * This service must be start by startService() command. It runs forever!
 * (till the application is destroyed)
 * @author Christian Magenheimer
 *
 */
public class ListeningSubscribeService  extends Service implements MXAListener{

	private IXMPPService mXMPPService;
	private IPubSubService mPubSubService;
	private final static String TAG="ListingSubscribeService";
	private ListeningSubscribeServiceBinder mBinder= new ListeningSubscribeServiceBinder();
	private HashMap<String,HashMap<String,ArrayList<String>>> mItems;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		MXAController.get().connectMXA(this, this);
		mItems= new HashMap<String, HashMap<String,ArrayList<String>>>();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onMXAConnected() {		
		try {
			Log.v(TAG,"mxaconnected");
			mXMPPService=MXAController.get().getXMPPService();
			if (mXMPPService!=null)mPubSubService=mXMPPService.getPubSubService();
		} catch (RemoteException e) {
			Log.e(TAG,"error binding to mxa "+e.getMessage());
		}
		
	}

	@Override
	public void onMXADisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	public void subscribe(String target, String node){
		try {
			mPubSubService.subscribe(mCallback,target,node);
			
		} catch (RemoteException e) {
			Log.e(TAG,"error subscribing "+e.getMessage());
		}
		
		HashMap<String,ArrayList<String>> map= new HashMap<String,ArrayList<String>>();
		
		ArrayList<String> list= new ArrayList<String>();
		map.put(node, list);
		if (mItems.containsKey(target))
		{
			if(!mItems.get(target).containsKey(node))
			{
				mItems.put(target, map);
			}
		}else
		{
			mItems.put(target, map);
		}
	}
	
	
	public void unsubsribe(String target, String node){
		try {
			mPubSubService.unsubscribe(mCallback,target,node);
		} catch (RemoteException e) {
			Log.e(TAG,"error unsubscribing "+e.getMessage());
		}
		if (mItems.containsKey(target))
		{
			if (mItems.get(target).containsKey(node))
			{
				mItems.get(target).remove(node);
			}
		}
	}
	
	/**
	 * Gives back the current subscribed targets
	 * @return List containing the targets
	 */
	public String[] getTargets() {
		Set<String> set=mItems.keySet();
		return (String[])set.toArray(new String[set.size()]);
	} 
	
	
	/**
	 * Gives back the current subscribed nodes to a target
	 * @return List containing the nodes
	 */
	public String[] getNodes(String target) {
		if (mItems.containsKey(target))
		{
			Set<String> set=mItems.get(target).keySet();
			return (String[])set.toArray(new String[set.size()]);
		}
		return null;
	}
	/**
	 * Gives back all items received from the target and node
	 * @return List containing the targets
	 */
	public String[] getItems(String target, String node){
		if (mItems.containsKey(target))
		{
			if (mItems.get(target).containsKey(node))
			{
				ArrayList<String> list=mItems.get(target).get(node);
				return (String[])list.toArray(new String[list.size()]);
			}
		}
		return null;
	}
	
	private ISubscribeCallback mCallback=new ISubscribeCallback.Stub() {
		
		@Override
		public void onPublishEvent(String from, String node, String items)
				throws RemoteException {
			mItems.get(from).get(node).add(items);
			Log.v(TAG,"received pubsub "+ items);
			
		}
	};
	
	
	public class ListeningSubscribeServiceBinder extends Binder{
		public ListeningSubscribeService getService(){ return ListeningSubscribeService.this;}
	}
}
