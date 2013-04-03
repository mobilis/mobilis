package de.tudresden.inf.rn.mobilis.services.xhunt.helper;

import de.tudresden.inf.rn.mobilis.xmpp.beans.IXMPPCallback;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

/**
 * Since null-callbacks are not allowed by the class MobilisXHuntProxy,
 * this empty callback is used instead.
 * 
 * @author Matthias Köngeter
 */
@SuppressWarnings("rawtypes")
public class EmptyCallback implements IXMPPCallback {

	@Override
	public void invoke(XMPPBean xmppBean) {
		// don't do anything
	}
}