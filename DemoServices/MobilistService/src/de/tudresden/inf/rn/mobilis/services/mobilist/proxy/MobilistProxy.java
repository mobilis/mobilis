package de.tudresden.inf.rn.mobilis.services.mobilist.proxy;

import java.util.List;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class MobilistProxy {

	private IMobilistOutgoing _bindingStub;

	public MobilistProxy( IMobilistOutgoing bindingStub) {
		_bindingStub = bindingStub;
	}

	public IMobilistOutgoing getBindingStub(){
		return _bindingStub;
	}

	public XMPPBean AddListEntry( String toJid, String packetId, ListEntry listEntry ) {
		if ( null == _bindingStub )
			return null;

		AddListEntryResponse out = new AddListEntryResponse( listEntry );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

	public XMPPBean RemoveListEntry( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return null;

		RemoveListEntryResponse out = new RemoveListEntryResponse(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

	public XMPPBean GetListEntry( String toJid, String packetId, ListEntry listEntry ) {
		if ( null == _bindingStub )
			return null;

		GetListEntryResponse out = new GetListEntryResponse( listEntry );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

	public void GetList( String toJid, List<ListEntry> listEntries ) {
		if ( null == _bindingStub )
			return;

		GetListResponse out = new GetListResponse( listEntries );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out );
	}

}