package de.tudresden.inf.rn.mobilis.emulation.clientstub;

import de.tudresden.inf.rn.mobilis.xmpp.beans.IXMPPCallback;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
public class EmulationProxy {

	private IEmulationOutgoing _bindingStub;


	public EmulationProxy( IEmulationOutgoing bindingStub) {
		_bindingStub = bindingStub;
	}


	public IEmulationOutgoing getBindingStub(){
		return _bindingStub;
	}


	public void Start( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return;

		StartAck out = new StartAck(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );
	}

	public void Stop( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return;

		StopAck out = new StopAck(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );
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

}