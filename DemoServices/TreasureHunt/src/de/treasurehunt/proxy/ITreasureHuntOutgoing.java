package de.treasurehunt.proxy;
public interface ITreasureHuntOutgoing {

	void PickUpTreasure( PickUpTreasureResponse out );

	void GetLocation( GetLocationRequest out, IXMPPCallback< GetLocationResponse > callback );

	void TreasureCollected( TreasureCollected out );

}