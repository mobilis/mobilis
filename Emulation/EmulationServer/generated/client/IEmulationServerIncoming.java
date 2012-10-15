package de.tudresden.inf.rn.mobilis.android.xhunt.emulation.clientstub;

public interface IEmulationServerIncoming {

	void onConnect( ConnectAck in );

	void onConnectError( ConnectRequest in);

	XMPPBean onCommand( CommandRequest in );

	void onExecutionResult( ExecutionResultAck in );

	void onExecutionResultError( ExecutionResultRequest in);

	XMPPBean onLog( LogRequest in );

}