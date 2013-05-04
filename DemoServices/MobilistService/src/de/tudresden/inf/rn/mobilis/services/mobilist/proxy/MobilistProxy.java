package de.tudresden.inf.rn.mobilis.services.mobilist.proxy;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
public class MobilistProxy {

	private IMobilistOutgoing _bindingStub;


	public MobilistProxy( IMobilistOutgoing bindingStub) {
		_bindingStub = bindingStub;
	}


	public IMobilistOutgoing getBindingStub(){
		return _bindingStub;
	}


	public XMPPBean Ping( String toJid, String packetId, String content ) {
		if ( null == _bindingStub )
			return null;

		PingResponse out = new PingResponse( content );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

}