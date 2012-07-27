package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public interface IMobilisXHuntOutgoing {

	void sendXMPPBean( XMPPBean out, IXMPPCallback< ? extends XMPPBean > callback );

	void sendXMPPBean( XMPPBean out );

}