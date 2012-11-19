package de.tudresden.inf.rn.mobilis.emulation.clientstub;

public interface IEmulationOutgoing {

	void sendXMPPBean( XMPPBean out, IXMPPCallback< ? extends XMPPBean > callback );

	void sendXMPPBean( XMPPBean out );

}