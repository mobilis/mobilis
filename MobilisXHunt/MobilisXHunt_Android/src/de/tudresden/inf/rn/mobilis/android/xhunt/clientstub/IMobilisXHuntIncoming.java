package de.tudresden.inf.rn.mobilis.android.xhunt.clientstub;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public interface IMobilisXHuntIncoming {

	void onAreas( AreasResponse in );

	void onAreasError( AreasRequest in);

	void onCancelStartTimer( CancelTimerResponse in );

	void onCancelStartTimerError( CancelTimerRequest in);

	void onCreateGame( CreateGameResponse in );

	void onCreateGameError( CreateGameRequest in);

	void onDeprtureData( DepartureDataResponse in );

	void onDeprtureDataError( DepartureDataRequest in);

	void onGameDetails( GameDetailsResponse in );

	void onGameDetailsError( GameDetailsRequest in);

	XMPPBean onGameOver( GameOverRequest in );

	void onJoinGame( JoinGameResponse in );

	void onJoinGameError( JoinGameRequest in);

	XMPPBean onLocation( LocationRequest in );

	void onPlayerExit( PlayerExitResponse in );

	void onPlayerExitError( PlayerExitRequest in);

	XMPPBean onPlayers( PlayersRequest in );

	XMPPBean onRoundStatus( RoundStatusRequest in );

	XMPPBean onSnapshot( SnapshotRequest in );

	XMPPBean onStartRound( StartRoundRequest in );

	void onTarget( TargetResponse in );

	void onTargetError( TargetRequest in);

	void onTransferTicket( TransferTicketResponse in );

	void onTransferTicketError( TransferTicketRequest in);

	void onUpdatePlayer( UpdatePlayerResponse in );

	void onUpdatePlayerError( UpdatePlayerRequest in);

	XMPPBean onUpdateTickets( UpdateTicketsRequest in );

	void onUsedTickets( UsedTicketsResponse in );

	void onUsedTicketsError( UsedTicketsRequest in);

}