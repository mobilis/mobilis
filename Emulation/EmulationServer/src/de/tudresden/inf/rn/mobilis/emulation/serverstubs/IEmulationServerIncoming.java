package de.tudresden.inf.rn.mobilis.emulation.serverstubs;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public interface IEmulationServerIncoming {

	XMPPBean onConnect( ConnectRequest in );

	void onCommand( CommandAck in );

	void onCommandError( CommandRequest in);

	XMPPBean onExecutionResult( ExecutionResultRequest in );

	void onLog( LogResponse in );

	void onLogError( LogRequest in);

}