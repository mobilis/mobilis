package de.tudresden.inf.rn.mobilis.emulation.serverstubs;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public interface IEmulationServerOutgoing {

	void sendXMPPBean( XMPPBean out, IXMPPCallback< ? extends XMPPBean > callback );

	void sendXMPPBean( XMPPBean out );

}