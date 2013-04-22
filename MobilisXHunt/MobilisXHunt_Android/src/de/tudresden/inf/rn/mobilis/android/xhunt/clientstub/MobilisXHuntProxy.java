package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
public class MobilisXHuntProxy {

	private IMobilisXHuntOutgoing _bindingStub;


	public MobilisXHuntProxy( IMobilisXHuntOutgoing bindingStub) {
		_bindingStub = bindingStub;
	}


	public IMobilisXHuntOutgoing getBindingStub(){
		return _bindingStub;
	}


	public XMPPBean Areas( String toJid, IXMPPCallback< AreasResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return null;

		AreasRequest out = new AreasRequest(  );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );

		return out;
	}

	public XMPPBean CancelStartTimer( String toJid, IXMPPCallback< CancelTimerResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return null;

		CancelTimerRequest out = new CancelTimerRequest(  );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );

		return out;
	}

	public XMPPBean CreateGame( String toJid, int AreaId, String GameName, String GamePassword, int CountRounds, int MinPlayers, int MaxPlayers, int StartTimer, int LocPollingInterval, TicketsMrX TicketsMrX, TicketsAgents TicketsAgents, IXMPPCallback< CreateGameResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return null;

		CreateGameRequest out = new CreateGameRequest( AreaId, GameName, GamePassword, CountRounds, MinPlayers, MaxPlayers, StartTimer, LocPollingInterval, TicketsMrX, TicketsAgents );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );

		return out;
	}

	public XMPPBean DeprtureData( String toJid, int StationId, IXMPPCallback< DepartureDataResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return null;

		DepartureDataRequest out = new DepartureDataRequest( StationId );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );

		return out;
	}

	public XMPPBean GameDetails( String toJid, IXMPPCallback< GameDetailsResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return null;

		GameDetailsRequest out = new GameDetailsRequest(  );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );

		return out;
	}

	public void GameOver( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return;

		GameOverResponse out = new GameOverResponse(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );
	}

	public XMPPBean JoinGame( String toJid, String GamePassword, String PlayerName, boolean IsSpectator, IXMPPCallback< JoinGameResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return null;

		JoinGameRequest out = new JoinGameRequest( GamePassword, PlayerName, IsSpectator );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );

		return out;
	}

	public void Location( String toJid, String packetId, LocationInfo LocationInfo ) {
		if ( null == _bindingStub )
			return;

		LocationResponse out = new LocationResponse( LocationInfo );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );
	}

	public XMPPBean PlayerExit( String toJid, String Jid, boolean IsSpectator, IXMPPCallback< PlayerExitResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return null;

		PlayerExitRequest out = new PlayerExitRequest( Jid, IsSpectator );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );

		return out;
	}

	public void Players( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return;

		PlayersResponse out = new PlayersResponse(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );
	}

	public void RoundStatus( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return;

		RoundStatusResponse out = new RoundStatusResponse(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );
	}

	public void Snapshot( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return;

		SnapshotResponse out = new SnapshotResponse(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );
	}

	public void StartRound( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return;

		StartRoundResponse out = new StartRoundResponse(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );
	}

	public XMPPBean Target( String toJid, int StationId, int Round, int TicketId, boolean IsFinal, IXMPPCallback< TargetResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return null;

		TargetRequest out = new TargetRequest( StationId, Round, TicketId, IsFinal );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );

		return out;
	}

	public XMPPBean TransferTicket( String toJid, String FromPlayerJid, String ToPlayerJid, TicketAmount Ticket, IXMPPCallback< TransferTicketResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return null;

		TransferTicketRequest out = new TransferTicketRequest( FromPlayerJid, ToPlayerJid, Ticket );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );

		return out;
	}

	public XMPPBean UpdatePlayer( String toJid, PlayerInfo PlayerInfo, IXMPPCallback< UpdatePlayerResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return null;

		UpdatePlayerRequest out = new UpdatePlayerRequest( PlayerInfo );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );

		return out;
	}

	public void UpdateTickets( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return;

		UpdateTicketsResponse out = new UpdateTicketsResponse(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );
	}

	public XMPPBean UsedTickets( String toJid, IXMPPCallback< UsedTicketsResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return null;

		UsedTicketsRequest out = new UsedTicketsRequest(  );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );

		return out;
	}

}