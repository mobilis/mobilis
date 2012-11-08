package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public interface IEmulationIncoming {

	XMPPBean onConnect( ConnectRequest in );

	void onCommand( CommandAck in );

	void onCommandError( CommandRequest in);

	XMPPBean onExecutionResult( ExecutionResultRequest in );

}