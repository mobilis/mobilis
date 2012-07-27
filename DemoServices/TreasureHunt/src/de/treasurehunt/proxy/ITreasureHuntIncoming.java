package de.treasurehunt.proxy;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public interface ITreasureHuntIncoming {

	XMPPBean onPickUpTreasure( PickUpTreasureRequest in );

	void onGetLocation( GetLocationResponse in );

	void onGetLocationError( GetLocationRequest in);

}