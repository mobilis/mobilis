package de.tudresden.inf.rn.mobilis.emulation.serverstubs;

public interface IEmulationServerOutgoing {

	void sendXMPPBean( XMPPBean out, IXMPPCallback< ? extends XMPPBean > callback );

	void sendXMPPBean( XMPPBean out );

}