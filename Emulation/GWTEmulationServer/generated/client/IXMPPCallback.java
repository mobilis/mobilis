package de.tudresden.inf.rn.mobilis.emulation.clientstub;

public interface IXMPPCallback<B extends XMPPBean> {

	void invoke(B xmppBean);

}