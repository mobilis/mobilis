package de.tudresden.inf.rn.mobilis.emulation.clientstub;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public interface IEmulationIncoming {

	void onConnect( ConnectAck in );

	void onConnectError( ConnectRequest in);

	XMPPBean onCommand( CommandRequest in );

	void onExecutionResult( ExecutionResultAck in );

	void onExecutionResultError( ExecutionResultRequest in);

	void onLog( LogRequest in );

}