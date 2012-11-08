package de.tudresden.inf.rn.mobilis.emulation.clientstub;

import java.util.List;import java.util.ArrayList;public class EmulationServerProxy {

	private IEmulationServerOutgoing _bindingStub;


	public EmulationServerProxy( IEmulationServerOutgoing bindingStub) {
		_bindingStub = bindingStub;
	}


	public IEmulationServerOutgoing getBindingStub(){
		return _bindingStub;
	}


	public XMPPBean Connect( String toJid, IXMPPCallback< ConnectAck > callback ) {
		if ( null == _bindingStub || null == callback )
			return null;

		ConnectRequest out = new ConnectRequest(  );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );

		return out;
	}

	public void Command( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return;

		CommandAck out = new CommandAck(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );
	}

	public XMPPBean ExecutionResult( String toJid, int commandId, String message, IXMPPCallback< ExecutionResultAck > callback ) {
		if ( null == _bindingStub || null == callback )
			return null;

		ExecutionResultRequest out = new ExecutionResultRequest( commandId, message );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );

		return out;
	}

	public void Log( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return;

		LogResponse out = new LogResponse(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );
	}

}