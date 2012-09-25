package de.tudresden.inf.rn.mobilis.mxa.services.xmpp;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import de.tudresden.inf.rn.mobilis.mxa.services.sessionmobility.SessionTransferIQ;

public class SessionTransferProvider implements IQProvider {

	private static final String TAG = "SessionTransferProvider";

	@Override
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		Log.i(TAG, "Parsing SessionTransferIQ");

		SessionTransferIQ transferIQ = new SessionTransferIQ();
		boolean done = false;

		while (!done) {
			int eventType = parser.next();

			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("mechanism")) {
					transferIQ.addMechanism(parser.nextText());
				} else if (parser.getName().equals("appuri")) {
					transferIQ.setAppURI(parser.nextText());
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("query")) {
					done = true;
				}
			}
		}
		return transferIQ;
	}

}
