package de.tudresden.inf.rn.mobilis.emulationserver.serverstubs;

public interface IXMPPCallback<B extends XMPPBean> {

	void invoke(B xmppBean);

}