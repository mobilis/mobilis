package de.treasurehunt.proxy;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public interface IXMPPCallback<B extends XMPPBean> {

	void invoke(B xmppBean);

}