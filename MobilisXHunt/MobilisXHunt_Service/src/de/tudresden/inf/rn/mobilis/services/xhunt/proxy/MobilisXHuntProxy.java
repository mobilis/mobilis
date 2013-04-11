package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import java.util.List;

import de.tudresden.inf.rn.mobilis.xmpp.beans.IXMPPCallback;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
public class MobilisXHuntProxy {

	private IMobilisXHuntOutgoing _bindingStub;


	public MobilisXHuntProxy( IMobilisXHuntOutgoing bindingStub) {
		_bindingStub = bindingStub;
	}


	public IMobilisXHuntOutgoing getBindingStub(){
		return _bindingStub;
	}


	public XMPPBean Areas( String toJid, String packetId, List< AreaInfo > Areas ) {
		if ( null == _bindingStub )
			return null;

		AreasResponse out = new AreasResponse( Areas );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

	public XMPPBean CancelStartTimer( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return null;

		CancelTimerResponse out = new CancelTimerResponse(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

	public XMPPBean CreateGame( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return null;

		CreateGameResponse out = new CreateGameResponse(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

	public XMPPBean DeprtureData( String toJid, String packetId, List< DepartureInfo > Departures ) {
		if ( null == _bindingStub )
			return null;

		DepartureDataResponse out = new DepartureDataResponse( Departures );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

	public XMPPBean GameDetails( String toJid, String packetId, String GameName, boolean RequirePassword, int CountRounds, int StartTimer, List< String > PlayerNames, boolean IsOpen ) {
		if ( null == _bindingStub )
			return null;

		GameDetailsResponse out = new GameDetailsResponse( GameName, RequirePassword, CountRounds, StartTimer, PlayerNames, IsOpen );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

	public void GameOver( String toJid, String Reason, IXMPPCallback< GameOverResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return;

		GameOverRequest out = new GameOverRequest( Reason );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );
	}

	public XMPPBean JoinGame( String toJid, String packetId, String ChatRoom, String ChatPassword, int StartTimer, List< String > IncomingGameFileNames ) {
		if ( null == _bindingStub )
			return null;

		JoinGameResponse out = new JoinGameResponse( ChatRoom, ChatPassword, StartTimer, IncomingGameFileNames );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

	public void Location( String toJid, List< LocationInfo > LocationInfos, boolean MrXOnline, IXMPPCallback< LocationResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return;

		LocationRequest out = new LocationRequest( LocationInfos, MrXOnline );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );
	}

	public XMPPBean PlayerExit( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return null;

		PlayerExitResponse out = new PlayerExitResponse(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

	public void Players( String toJid, List< PlayerInfo > Players, String Info, IXMPPCallback< PlayersResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return;

		PlayersRequest out = new PlayersRequest( Players, Info );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );
	}

	public void RoundStatus( String toJid, int Round, List< RoundStatusInfo > RoundStatusInfos, IXMPPCallback< RoundStatusResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return;

		RoundStatusRequest out = new RoundStatusRequest( Round, RoundStatusInfos );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );
	}

	public void Snapshot( String toJid, String GameName, int Round, boolean IsRoundStart, boolean ShowMrX, int StartTimer, List< TicketAmount > Tickets, List< PlayerSnapshotInfo > PlayerSnapshots, IXMPPCallback< SnapshotResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return;

		SnapshotRequest out = new SnapshotRequest( GameName, Round, IsRoundStart, ShowMrX, StartTimer, Tickets, PlayerSnapshots );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );
	}

	public void StartRound( String toJid, int Round, boolean ShowMrX, List< TicketAmount > Tickets, IXMPPCallback< StartRoundResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return;

		StartRoundRequest out = new StartRoundRequest( Round, ShowMrX, Tickets );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );
	}

	public XMPPBean Target( String toJid, String packetId, int TicketId ) {
		if ( null == _bindingStub )
			return null;

		TargetResponse out = new TargetResponse( TicketId );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

	public XMPPBean TransferTicket( String toJid, String packetId ) {
		if ( null == _bindingStub )
			return null;

		TransferTicketResponse out = new TransferTicketResponse(  );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

	public XMPPBean UpdatePlayer( String toJid, String packetId, String Info ) {
		if ( null == _bindingStub )
			return null;

		UpdatePlayerResponse out = new UpdatePlayerResponse( Info );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

	public void UpdateTickets( String toJid, List< TicketAmount > Tickets, IXMPPCallback< UpdateTicketsResponse > callback ) {
		if ( null == _bindingStub || null == callback )
			return;

		UpdateTicketsRequest out = new UpdateTicketsRequest( Tickets );
		out.setTo( toJid );

		_bindingStub.sendXMPPBean( out, callback );
	}

	public XMPPBean UsedTickets( String toJid, String packetId, List< UsedTicketsInfo > UsedTickets ) {
		if ( null == _bindingStub )
			return null;

		UsedTicketsResponse out = new UsedTicketsResponse( UsedTickets );
		out.setTo( toJid );
		out.setId( packetId );

		_bindingStub.sendXMPPBean( out );

		return out;
	}

}