package de.tudresden.inf.rn.mobilis.mxa;

import java.util.ArrayList;

import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPIQ;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Non persistent queue for saving lost iqs.
 * The underlying data structure is a linked list. 
 * @author Christian Magenheimer
 *
 */
public class LostIQQueue {

	/*
	 * Maximum entries in the list.
	 */
	private static int MAX_ENTRIES=5000;
	//points to the next entry in the queue, this is hold so every packet
	//is tried to send once before the first one is sended the second time
	//(sliding window of 1)
	private LostIQQueueEntry mNextEntry; 
	//list holding the entries
	private ArrayList<LostIQQueueEntry> mList;
	
	//holds the current maximum id, for every entry that is inserted, the number is incremented
	private long mId=1;
	
	private final static String TAG="LostIQQueue";
	
	private int mMaxRetryCount;
	//in minutes
	private int mMaxRetryTime;
	//in seconds
	private int mRetryInterval;
	
	public LostIQQueue(){
		mList= new ArrayList<LostIQQueueEntry>();
		mNextEntry=null;
	}
	
	public boolean insert(LostIQQueueEntry liqe)
	{
		if (liqe==null) return false;
		//check if the element is already in the list
		if (liqe.mID>0)
		{
			Log.v(TAG,"element with id "+liqe.mID+" already in the list, updating count");
			int pos=containsId(liqe.mID);
			if (pos<0) return false;
			else mList.get(pos).mCount++;
			return true;
		}else
		{
			//if we reached the maximum size, we notify the app about the first packet and remove it
			if (mList.size()>=MAX_ENTRIES)
			{
				LostIQQueueEntry e=mList.get(0);
				notifyApplication(e);
				mList.remove(0);
			}
			liqe.mID=mId;
			mId++;
		}
		//if list is empty, we need to set the mNextEntry
		if (mList.size()==0)
		{
			mNextEntry=liqe;
		}
		mList.add(liqe);
		Log.v(TAG,"Queue size : "+mList.size() +" "+liqe.mXMPPIQ.toString());
		return true;
	}
	
	public LostIQQueueEntry getOldestEntry()
	{
		//set the entry to the next and return the old one
		
		return setToNext(true);
	}
	
	public boolean delete(long id)
	{
		if (id<0) return false;
		//this one is more complicated, we search an id
		int pos=-1;
		for (int i=0;i<mList.size();i++)
		{
			if (mList.get(i).mID==id) 
			{
				pos=i;
				break;
			}
		}
		
		if (pos<0) return false;
		//check if the element we want to delete is the one we are pointing to
		if (mNextEntry!=null && (pos==mList.indexOf(mNextEntry)))
			setToNext(false);
		LostIQQueueEntry liqe=mList.remove(pos);
		if (mList.size()==0)
		{
			mNextEntry=null;
			Log.v(TAG,"Queue is empty");
		}else
		Log.v(TAG,"Queue size : "+mList.size() +" removed "+liqe.mXMPPIQ.toString());
		return true;
	}
	
	/**
	 * Every time the pointer is set to the next, we check if the element should be deleted
	 * and the application informed that the packet is lost.
	 * @param check check the last entry
	 * @return the last Queue entry that is now meant to be sent 
	 */
	private LostIQQueueEntry setToNext(boolean check)
	{
		if(check)
		{
			//TODO: Check this
			while ( mNextEntry != null && 
					(mNextEntry.mCount>mMaxRetryCount ||
							mNextEntry.mTime-System.currentTimeMillis()>mMaxRetryTime*60))
			{
				int oldPos=mList.indexOf(mNextEntry);
				mList.remove(mNextEntry);
				LostIQQueueEntry e=mNextEntry;
				notifyApplication(e);
				oldPos+=1;
				oldPos=oldPos%mList.size();
				if (mList.size()>0)mNextEntry=mList.get(oldPos);
				else mNextEntry=null;
			}
		}
		//list could be null
		if (mNextEntry==null)return null;
						
		LostIQQueueEntry e=mNextEntry;
		//we need to do some arithmetic because the list is cyclic
		int pos=mList.indexOf(mNextEntry)+1;
		if (pos==mList.size()) pos=0;
		if (mList.size()>0)mNextEntry=mList.get(pos);
		else mNextEntry=null;
		return e;
	}
	
	public int getCount()
	{
		return mList.size();
	}
	
	public void logState()
	{
		if (mList.size()==0)Log.v(TAG,"List is empty");
		if (mNextEntry!=null) Log.v(TAG,"nextEntry: "+mList.indexOf(mNextEntry)+" "+mNextEntry.mXMPPIQ+" "+mNextEntry.mMessage+" "+mNextEntry.mTime+" "+mNextEntry.mID);
		for (int i=0; i<mList.size();i++)
		{
			LostIQQueueEntry e=mList.get(i);
			Log.v(TAG,i+" "+e.mXMPPIQ+" "+e.mMessage+" "+e.mTime+" "+e.mID);
		}
		Log.v(TAG,"==========================");
	}
	
	/**
	 * Checks the list for a entry with the given id
	 * @param id ID to be searched for
	 * @return zero-based index of the element
	 */
	private int containsId(long id)
	{
		for (int i=0;i<mList.size();i++) if (mList.get(i).mID==id) return i;
		return -1;
	}
	

	public int getMaxRetryCount() {
		return mMaxRetryCount;
	}

	public void setMaxRetryCount(int MaxRetryCount) {
		this.mMaxRetryCount = MaxRetryCount;
	}

	public int getMaxRetryTime() {
		return mMaxRetryTime;
	}

	public void setMaxRetryTime(int MaxRetryTime) {
		this.mMaxRetryTime = MaxRetryTime;
	}

	public int getRetryInterval() {
		return mRetryInterval;
	}

	public void setRetryInterval(int RetryInterval) {
		this.mRetryInterval = RetryInterval;
	}
	
	/**
	 * Takes the parameters from the entry and notifies the application
	 * about the error, we could not send the packet correctly.
	 * @param e LostIQQueueEntry, that could not be sended
	 */
	private void notifyApplication(LostIQQueueEntry e)
	{
		Message msg=Message.obtain(e.mMessage);
		Messenger m=msg.getData().getParcelable("MSN_RESULT");
		if (m!=null)
		{
			msg.arg1=ConstMXA.MSG_STATUS_ERROR;
			msg.getData().putString(ConstMXA.EXTRA_ID,e.mXMPPIQ.packetID);
			msg.getData().putLong(ConstMXA.EXTRA_TIME,e.mTime);
			msg.getData().putInt(ConstMXA.EXTRA_COUNT,e.mCount);
			try {
				m.send(msg);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
		
	}
}
