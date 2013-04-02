package de.tudresden.inf.rn.mobilis.android.emulation.beans;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public interface IEmulationIncoming {

	XMPPBean onStart( StartRequest in );

	XMPPBean onStop( StopRequest in );

	void onConnect( ConnectAck in );

	void onConnectError( ConnectRequest in);

	XMPPBean onCommand( CommandRequest in );

	void onExecutionResult( ExecutionResultAck in );

	void onExecutionResultError( ExecutionResultRequest in);

	void onLog( LogRequest in );

}