package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public interface IEmulationIncoming {

	void onStart( StartAck in );

	void onStartError( StartRequest in);

	void onStop( StopAck in );

	void onStopError( StopRequest in);

	XMPPBean onConnect( ConnectRequest in );

	void onDisconnect( DisconnectRequest in );

	void onCommand( CommandAck in );

	void onCommandError( CommandRequest in);

	XMPPBean onExecutionResult( ExecutionResultRequest in );

}