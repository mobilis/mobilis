package de.tudresden.inf.rn.mobilis.services.mobilist.proxy;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public interface IMobilistIncoming {

	XMPPBean onPing( PingRequest in );

}