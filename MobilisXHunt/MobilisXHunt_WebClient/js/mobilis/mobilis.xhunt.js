(function(){
    /** Class: Mobilis.xhunt
    *  Mobilis xHunt Client
    *
    */
    var xhunt = {
        
        NS: {
            JOINGAME: 'mobilisxhunt:iq:joingame',
            UPDATEPLAYER: 'mobilisxhunt:iq:updateplayer',
            GAMEDETAILS: 'mobilisxhunt:iq:gamedetails',
            USEDTICKETS: 'mobilisxhunt:iq:usedtickets',
            SNAPSHOT: 'mobilisxhunt:iq:snapshot',
            PLAYEREXIT: 'mobilisxhunt:iq:playerexit',
            LOCATION: 'mobilisxhunt:iq:location',
            PLAYERS: 'mobilisxhunt:iq:players',
            STARTROUND: 'mobilisxhunt:iq:startround',
            ROUNDSTATUS: 'mobilisxhunt:iq:roundstatus'      
        },

        settings: {},
        
        handlers: {},

        /** Function:  setXhuntServer
        *  Sets the URL to the mobilis Server
        *
        *  Parameters:
        *    (Function) resultcallback - Callback for incoming response IQ stanzas of type RESULT
        *    (Function) errorcallback - Callback for incoming response IQ stanzas of type ERROR, or timeout
        */
        setXhuntServer: function(resultcallback, errorcallback) {
            if (!resultcallback) {
                resultcallback = Mobilis.core.defaultcallback;
            };
            if (!errorcallback) {
                errorcallback = Mobilis.core.defaulterrorback;
            }; 
            Mobilis.core.mobilisServiceDiscovery(
                ['http://mobilis.inf.tu-dresden.de#services/XHunt'],
                function (iq) {
                    $(iq).find("mobilisService").each(function() {
                        Mobilis.core.SERVICES[$(this).attr('namespace')] =
                        {
                            'version': $(this).attr('version'),
                            'jid': $(this).attr('jid')
                        };
                    }); 
                    console.log(Mobilis.core.SERVICES[Mobilis.core.NS.XHUNT].jid);
                }
            );
        },

        /** Function:  joinGame
        *  Sends joinGameIQ of type 'set' to join Game.
        *
        *  Parameters:
        *    (String) gameJID - JID of the Xhuntservice representing the game
        *    (Array) attr - Attribute. [playername] toDo password
        *    (Function) resultcallback - Callback for incoming response IQ stanzas of type RESULT
        *    (Function) errorcallback - Callback for incoming response IQ stanzas of type ERROR, or timeout
        */
        joinGame: function(gameJID, playerName, resultcallback, errorcallback) {
            if (!resultcallback) 
                resultcallback = Mobilis.core.defaultcallback; 
            if (!errorcallback) 
                errorcallback = Mobilis.core.defaulterrorback;
                 
            if (gameJID){ 
                Mobilis.xhunt.gameJID = gameJID;
                var customiq = $iq({
                    to: gameJID,
                    type: 'set'
                })
                .c('JoinGameRequest' , {xmlns : Mobilis.xhunt.NS.JOINGAME})
                .c('GamePassword').up();
                if (playerName) 
                    customiq.c('PlayerName').t(playerName).up().c('IsSpectator').t('false');
            } else {
                errorcallback(null, 'Game JID not defined');
            }

            Mobilis.core.sendIQ(customiq, resultcallback, errorcallback);
        },

        /** Function:  updatePlayer
        *  Sends updatePlayerIQ of type 'set'.
        *
        *  Parameters:
        *    (String) gameJID - JID of the Xhuntservice representing the game
        *    (Array) attr - Attribute. [playername] toDo password
        *    (Function) resultcallback - Callback for incoming response IQ stanzas of type RESULT
        *    (Function) errorcallback - Callback for incoming response IQ stanzas of type ERROR, or timeout
        */
        updatePlayer: function(gameJID, playerJid, playerName, isModerator, isMrX, isReady, resultcallback, errorcallback) {
            if (!resultcallback) 
                resultcallback = Mobilis.core.defaultcallback; 
            if (!errorcallback) 
                errorcallback = Mobilis.core.defaulterrorback;

            if (gameJID) {
                if (playerName) {
                    Mobilis.xhunt.gameJID = gameJID;
                    var customIq = $iq({
                        to: gameJID,
                        type: 'set'
                    })
                    .c('UpdatePlayerRequest', {xmlns : Mobilis.xhunt.NS.UPDATEPLAYER})
                    .c('PlayerInfo')
                    .c('Jid').t(playerJid).up()//+'@mobilis.inf.tu-dresden.de/35098e1e').up()
                    .c('PlayerName').t(playerName).up()
                    .c('IsModerator').t(isModerator ?'true':'false').up()
                    .c('IsMrX').t(isMrX ?'true':'false').up()
                    .c('IsReady').t(isReady ?'true':'false').up();
                    // if (isModerator) customIq.c('IsModerator').t('true').up();
                    // if (isMrX) customIq.c('IsMrX').t('true').up();
                    // if (isReady) customIq.c('IsReady').t('true').up();
                } else {
                    errorcallback(null, 'playerName not defined');
                }
            } else {
                errorcallback(null, 'gameJID not defined');
            }

            Mobilis.core.sendIQ(customIq, resultcallback, errorcallback);
        }, 
        /** Response Functions
        *  respond to request IQs
        */
        respondPlayer: function(gameJID, iqid, resultcallback, errorcallback){
            if (!resultcallback) 
                resultcallback = Mobilis.core.defaultcallback; 
            if (!errorcallback) 
                errorcallback = Mobilis.core.defaulterrorback;

            var customIq = $iq({
                to: gameJID,
                id: iqid,
                type: 'result'
            })
            .c('PlayersResponse', {xmlns : Mobilis.xhunt.NS.PLAYERS});
            Mobilis.core.sendIQ(customIq);
        },

        respondStartRound: function(gameJID, iqid, resultcallback, errorcallback){
            if (!resultcallback) 
                resultcallback = Mobilis.core.defaultcallback; 
            if (!errorcallback) 
                errorcallback = Mobilis.core.defaulterrorback;

            var customIq = $iq({
                to: gameJID,
                id: iqid,
                type: 'result'
            })
            .c('StartRoundResponse', {xmlns : Mobilis.xhunt.NS.PLAYERS});
            Mobilis.core.sendIQ(customIq);
        },

        respondLocation: function(gameJID, playerJid, iqid, latitude, longitude, resultcallback, errorcallback){
            if (!resultcallback) 
                resultcallback = Mobilis.core.defaultcallback; 
            if (!errorcallback) 
                errorcallback = Mobilis.core.defaulterrorback;

            if (gameJID) {
                if (playerJid) {
                    Mobilis.xhunt.gameJID = gameJID;

                    var customIq = $iq({
                        to: gameJID,
                        id: iqid,
                        type: 'result'
                    })
                    .c('LocationResponse', {xmlns : Mobilis.xhunt.NS.LOCATION})
                    .c('LocationInfo')
                    .c('Jid').t(playerJid).up()
                    .c('Latitude').t(latitude.toString()).up()
                    .c('Longitude').t(longitude.toString()).up();
                } else {
                    errorcallback(null, 'playerJid not defined');
                }
            } else {
                errorcallback(null, 'gameJID not defined');
            }

            Mobilis.core.sendIQ(customIq, resultcallback, errorcallback);
        },

        /** Function:  gameDetails
        *  Sends gameDetailsIQ of type 'get'.
        *
        *  Parameters:
        *    (String) gameJID - JID of the Xhuntservice representing the game
        *    (Function) resultcallback - Callback for incoming response IQ stanzas of type RESULT
        *    (Function) errorcallback - Callback for incoming response IQ stanzas of type ERROR, or timeout
        */
        gameDetails: function(gameJID, resultcallback, errorcallback) {
            if (!resultcallback) 
                resultcallback = Mobilis.core.defaultcallback; 
            if (!errorcallback) 
                errorcallback = Mobilis.core.defaulterrorback;
                 
            if (gameJID){         
                var customiq = $iq({
                    to: gameJID,
                    type: 'get'
                })
                .c('GameDetailsRequest' , {xmlns : Mobilis.xhunt.NS.GAMEDETAILS})
            } else {
                errorcallback(null, 'Game JID not defined');
            }

            Mobilis.core.sendIQ(customiq, resultcallback, errorcallback);
        },
        
        /** Function:  usedTickets
        *  Sends usedTicketsIQ of type 'get'.
        *
        *  Parameters:
        *    (String) gameJID - JID of the Xhuntservice representing the game
        *    (Function) resultcallback - Callback for incoming response IQ stanzas of type RESULT
        *    (Function) errorcallback - Callback for incoming response IQ stanzas of type ERROR, or timeout
        */
        usedTickets: function(gameJID, resultcallback, errorcallback) {
            if (!resultcallback) 
                resultcallback = Mobilis.core.defaultcallback; 
            if (!errorcallback) 
                errorcallback = Mobilis.core.defaulterrorback;

            if (gameJID){         
                var customiq = $iq({
                    to: gameJID,
                    type: 'get'
                })
                .c('UsedTicketsRequest' , {xmlns : Mobilis.xhunt.NS.USEDTICKETS});
            } else {
                errorcallback(null, 'Game JID not defined');
            }

            Mobilis.core.sendIQ(customiq, resultcallback, errorcallback);
        },
        
        /** Function:  snapshot
        *  Sends snapshotIQ of type 'get'. 
        *
        *  Parameters:
        *    (String) gameJID - JID of the Xhuntservice representing the game
        *    (Function) resultcallback - Callback for incoming response IQ stanzas of type RESULT
        *    (Function) errorcallback - Callback for incoming response IQ stanzas of type ERROR, or timeout
        */
        snapshot: function(gameJID, resultcallback, errorcallback) {
            if (!resultcallback) 
                resultcallback = Mobilis.core.defaultcallback; 
            if (!errorcallback) 
                errorcallback = Mobilis.core.defaulterrorback;

            if (gameJID){         
                var customiq = $iq({
                    to: gameJID,
                    type: 'get'
                })
                .c('SnapshotRequest' , {xmlns : Mobilis.xhunt.NS.SNAPSHOT});
            } else {
                errorcallback(null, 'Game JID not defined');
            }

            Mobilis.core.sendIQ(customiq, resultcallback, errorcallback);
        },
        
        /** Function:  playerExit
        *  Sends playerExitIQ of type 'set'.
        *
        *  Parameters:
        *    (String) gameJID - JID of the Xhuntservice representing the game
        *    (String) playerJID - JID of the spectator exiting the game
        *    (Function) resultcallback - Callback for incoming response IQ stanzas of type RESULT
        *    (Function) errorcallback - Callback for incoming response IQ stanzas of type ERROR, or timeout
        */
        playerExit: function(gameJID, playerJID, resultcallback, errorcallback) {
            if (!resultcallback) 
                resultcallback = Mobilis.core.defaultcallback;
            if (!errorcallback) 
                errorcallback = Mobilis.core.defaulterrorback;
                
            if (gameJID){         
                 var customiq = $iq({
                        to: gameJID,
                        type: 'set'
                    })
                    .c('PlayerExitRequest' , {xmlns : Mobilis.xhunt.NS.PLAYEREXIT});
                    if (playerJID) 
                        customiq.c('Jid').t(playerJID).up().c('IsSpectator').t('false');
            } else {
                errorcallback(null, 'Game JID not defined');
            }
            Mobilis.core.sendIQ(customiq, resultcallback, errorcallback);
        },

        /** Function:  addLocationHandler
        *  Adds handler function for incoming LocationIQ
        *
        *  Parameters:
        *    (Function) handler - Function to handle incoming LocationIQs
        */
        addLocationHandler: function(handler) {  
            Mobilis.connection.addHandler(
                handler,
                Mobilis.xhunt.NS.LOCATION
            );
        },
        
        /** Function:  addPlayersHandler
        *  Adds handler function for incoming PlayersIQ
        *
        *  Parameters:
        *    (Function) handler - Function to handle incoming PlayersIQs
        */
        addPlayersHandler: function(handler) {  
            Mobilis.connection.addHandler(
                handler,
                Mobilis.xhunt.NS.PLAYERS
            );
        },
        
        /** Function:  addPlayerExitHandler
        *  Adds handler function for incoming PlayerExitIQ
        *
        *  Parameters:
        *    (Function) handler - Function to handle incoming PlayerExitIQs
        */
        addPlayerExitHandler: function(handler) {  
            Mobilis.connection.addHandler(
                handler,
                Mobilis.xhunt.NS.PLAYEREXIT
            )
        },
        
        /** Function:  addStartRoundHandler
        *  Adds handler function for incoming StartRoundIQ
        *
        *  Parameters:
        *    (Function) handler - Function to handle incoming StartRoundIQs
        */
        addStartRoundHandler: function(handler) {  
            Mobilis.connection.addHandler(
                handler,
                Mobilis.xhunt.NS.STARTROUND
            );
        },

        /** Function:  addRoundStatusHandler
        *  Adds handler function for incoming RoundStatusIQ
        *
        *  Parameters:
        *    (Function) handler - Function to handle incoming RoundStatusIQs
        */
        addRoundStatusHandler: function(handler) {  
            Mobilis.connection.addHandler(
                handler,
                Mobilis.xhunt.NS.ROUNDSTATUS
            );
        },

        /** Function:  addJoinGameHandler
        *  Adds handler function for incoming JoinGameIQ
        *
        *  Parameters:
        *    (Function) handler - Function to handle incoming JoinGamesIQ responses from other players joining
        */
        addJoinGameHandler: function(handler) {
            Mobilis.connection.addHandler(
                handler,
                Mobilis.xhunt.NS.JOINGAME
            );
        },

        addUpdatePlayerHandler: function(handler) {
            Mobilis.connection.addHandler(
                handler,
                Mobilis.xhunt.NS.UPDATEPLAYER
            );
        },


    };
    Mobilis.extend("xhunt", xhunt);
})();