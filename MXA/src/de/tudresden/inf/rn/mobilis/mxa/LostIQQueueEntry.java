package de.tudresden.inf.rn.mobilis.mxa;

import android.os.Message;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPIQ;

/**
 * Represents on entry in the LostIQQueue.
 * We need the XMPPIQ, the original Message to correctly rebuild a
 * wish to send an iq-stanza.
 * @author Christian Magenheimer
 *
 */
public class LostIQQueueEntry{
	//the XMPPIQ to send
	public XMPPIQ mXMPPIQ;
	//holds the reference to the handler that should be notified
	public Message mMessage;
	//time in milliseconds sind 1/1/1970
	public long mTime;
	//id for identifing the entry, also through boundaries of IPC
	public long mID;
	//count of retries
	public int mCount;
	//boolean indicator, if there is currently a try to send the packet
	public boolean mSending=false;
}