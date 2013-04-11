package de.tudresden.inf.rn.mobilis.services.mobilist.proxy;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public interface IMobilistIncoming {

	XMPPBean onAddListEntry( AddListEntryRequest in );

	XMPPBean onRemoveListEntry( RemoveListEntryRequest in );

	XMPPBean onGetListEntry( GetListEntryRequest in );

}