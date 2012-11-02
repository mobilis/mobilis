package de.tudresden.inf.rn.mobilis.emulation.serverstubs;

public interface IXMPPCallback<B extends XMPPBean> {

	void invoke(B xmppBean);

}