package de.tudresden.inf.rn.mobilis.emulation.clientstub;

public interface IEmulationIncoming {

	void onConnect( ConnectAck in );

	void onConnectError( ConnectRequest in);

	XMPPBean onCommand( CommandRequest in );

	void onExecutionResult( ExecutionResultAck in );

	void onExecutionResultError( ExecutionResultRequest in);

}