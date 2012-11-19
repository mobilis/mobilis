package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans;

public interface IEmulationOutgoing {

	void sendXMPPBean( XMPPBean out, IXMPPCallback< ? extends XMPPBean > callback );

	void sendXMPPBean( XMPPBean out );

}