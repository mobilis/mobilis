package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans;

import java.util.List;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
public class EmulationServerProxy {

	private IEmulationServerOutgoing _bindingStub;


	public EmulationServerProxy( IEmulationServerOutgoing bindingStub) {
		_bindingStub = bindingStub;
	}


	public IEmulationServerOutgoing getBindingStub(){
		return _bindingStub;
	}


	public XMPPBean Connect( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return null;

		ConnectAck out = new ConnectAck(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

	public void Command( String toJid, String methodName, List< String > parameters, List< String > parameterTypes, int commandId, String instanceId, IXMPPCallback< CommandAck > callback ) {
		if ( null == _bindingStub || null == callback )
			return;

		CommandRequest out = new CommandRequest( methodName, parameters, parameterTypes, commandId, instanceId );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );
	}

	public XMPPBean ExecutionResult( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return null;

		ExecutionResultAck out = new ExecutionResultAck(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

	public void Log( String toJid, String instanceId, IXMPPCallback< LogResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return;

		LogRequest out = new LogRequest( instanceId );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );
	}

}