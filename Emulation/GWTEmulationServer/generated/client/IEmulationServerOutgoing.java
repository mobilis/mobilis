package de.tudresden.inf.rn.mobilis.emulation.clientstub;

public interface IEmulationServerOutgoing {

	void sendXMPPBean( XMPPBean out, IXMPPCallback< ? extends XMPPBean > callback );

	void sendXMPPBean( XMPPBean out );

}