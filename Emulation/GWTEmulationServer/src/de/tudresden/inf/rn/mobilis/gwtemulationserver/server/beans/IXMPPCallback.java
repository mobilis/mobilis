package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans;


public interface IXMPPCallback<B extends XMPPBean> {

	void invoke(B xmppBean);

}