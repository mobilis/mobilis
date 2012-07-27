package de.tudresden.inf.rn.mobilis.services.xhunt.proxy;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public interface IMobilisXHuntIncoming {

	XMPPBean onAreas( AreasRequest in );

	XMPPBean onCancelStartTimer( CancelTimerRequest in );

	XMPPBean onCreateGame( CreateGameRequest in );

	XMPPBean onDeprtureData( DepartureDataRequest in );

	XMPPBean onGameDetails( GameDetailsRequest in );

	void onGameOver( GameOverResponse in );

	void onGameOverError( GameOverRequest in);

	XMPPBean onJoinGame( JoinGameRequest in );

	void onLocation( LocationResponse in );

	void onLocationError( LocationRequest in);

	XMPPBean onPlayerExit( PlayerExitRequest in );

	void onPlayers( PlayersResponse in );

	void onPlayersError( PlayersRequest in);

	void onRoundStatus( RoundStatusResponse in );

	void onRoundStatusError( RoundStatusRequest in);

	void onSnapshot( SnapshotResponse in );

	void onSnapshotError( SnapshotRequest in);

	void onStartRound( StartRoundResponse in );

	void onStartRoundError( StartRoundRequest in);

	XMPPBean onTarget( TargetRequest in );

	XMPPBean onTransferTicket( TransferTicketRequest in );

	XMPPBean onUpdatePlayer( UpdatePlayerRequest in );

	void onUpdateTickets( UpdateTicketsResponse in );

	void onUpdateTicketsError( UpdateTicketsRequest in);

	XMPPBean onUsedTickets( UsedTicketsRequest in );

}