package de.tudresden.inf.rn.mobilis.mxa.services.xmpp;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import de.tudresden.inf.rn.mobilis.mxa.services.sessionmobility.SessionInvitationIQ;

public class SessionInvitationProvider implements IQProvider {

	private static final String TAG = "SessionInvitationProvider";

	@Override
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		Log.i(TAG, "Parsing SessionInvitationIQ");

		SessionInvitationIQ invitationIQ = new SessionInvitationIQ();
		boolean done = false;
		ArrayList<String> params = new ArrayList<String>();

		while (!done) {
			int eventType = parser.next();

			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("param")) {
					params.add(parser.nextText());
				} else if (parser.getName().equals("appuri")) {
					invitationIQ.setAppURI(parser.nextText());
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("query")) {
					done = true;
				}
			}
		}
		
		if (params.size() > 0) {
			invitationIQ.setParams(params);
		}
		
		return invitationIQ;
	}

}
