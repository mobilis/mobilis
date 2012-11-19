package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans;

public interface IEmulationIncoming {

	XMPPBean onConnect( ConnectRequest in );

	void onCommand( CommandAck in );

	void onCommandError( CommandRequest in);

	XMPPBean onExecutionResult( ExecutionResultRequest in );

}