package de.tudresden.inf.rn.mobilis.android.xhunt.emulation.clientstub;

public interface IXMPPCallback<B extends XMPPBean> {

	void invoke(B xmppBean);

}