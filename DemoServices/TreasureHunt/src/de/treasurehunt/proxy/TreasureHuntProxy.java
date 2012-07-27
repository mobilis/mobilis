package de.treasurehunt.proxy;
public class TreasureHuntProxy {

	private ITreasureHuntOutgoing _bindingStub;


	public TreasureHuntProxy( ITreasureHuntOutgoing bindingStub) {
		_bindingStub = bindingStub;
	}


	public void PickUpTreasure( String toJid, long TreasureValue ) {
		if ( null == _bindingStub )
			return;

		PickUpTreasureResponse out = new PickUpTreasureResponse( TreasureValue );
		out.setTo( toJid );

		_bindingStub.PickUpTreasure( out );
	}

	public void GetLocation( String toJid, IXMPPCallback< GetLocationResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return;

		GetLocationRequest out = new GetLocationRequest(  );
		out.setTo( toJid );

		_bindingStub.GetLocation( out, callback );
	}

	public void TreasureCollected( String toJid, String PlayerName, Location TreasureLocation, long TreasureValue ) {
		if ( null == _bindingStub )
			return;

		TreasureCollected out = new TreasureCollected( PlayerName, TreasureLocation, TreasureValue );
		out.setTo( toJid );

		_bindingStub.TreasureCollected( out );
	}

}