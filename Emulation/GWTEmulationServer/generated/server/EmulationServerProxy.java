package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.beans;

import java.util.List;import java.util.ArrayList;public class EmulationServerProxy {

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

	public void Command( String toJid, String method_name, List< String > parameters, List< String > parameter_types, int command_id, String instance_id, IXMPPCallback< CommandAck > callback ) {
		if ( null == _bindingStub || null == callback )
			return;

		CommandRequest out = new CommandRequest( method_name, parameters, parameter_types, command_id, instance_id );
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

	public void Log( String toJid, IXMPPCallback< LogResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return;

		LogRequest out = new LogRequest(  );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );
	}

}