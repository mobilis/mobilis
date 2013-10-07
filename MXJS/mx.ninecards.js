(function() {

	var ninecards = {

		NS : {
			SERVICE : "http://mobilis.inf.tu-dresden.de#services/MobilisNineCardsService",
			CONFIGUREGAME : "mobilisninecards:iq:configuregame",
			JOINGAME : "mobilisninecards:iq:joingame"
		},

        settings: {},
        
        handlers: {},


        createServiceInstance: function (name, resultcallback, errorcallback) {
            var customIq = $iq({
                to: MX.core.SERVICES[MX.core.NS.COORDINATOR].jid,
                type: 'set'                
            })
            .c('createNewServiceInstance', {xmlns: MX.core.NS.COORDINATOR} )
            .c('serviceNamespace').t(MX.ninecards.NS.SERVICE).up()
            .c('serviceName').t(name);

            MX.core.sendIQ(customIq, resultcallback, errorcallback);
        },


        ConfigureGame: function(gameJID, GameName, MaxPlayers, NumberOfRounds, resultcallback, errorcallback) {
            if (!resultcallback) 
                resultcallback = MX.core.defaultcallback; 
            if (!errorcallback) 
                errorcallback = MX.core.defaulterrorback;

            var customIq = $iq({
                to: gameJID,
                type: 'set'
            })
            .c('ConfigureGameRequest', {xmlns : MX.ninecards.NS.CONFIGUREGAME})
            .c('gamename').t(GameName).up()
            .c('players').t(MaxPlayers).up()
            .c('rounds').t(NumberOfRounds).up();

            MX.core.sendIQ(customIq, resultcallback, errorcallback);
        }, 




        joinGame: function(gameJid, resultcallback, errorcallback) {
            if (!resultcallback) 
                resultcallback = MX.core.defaultcallback; 
            if (!errorcallback) 
                errorcallback = MX.core.defaulterrorback;
                 
            if (gameJid){ 
                MX.ninecards.gameJID = gameJid;
                var customiq = $iq({
                    to: gameJid,
                    type: 'set'
                })
                .c('JoinGameRequest' , {xmlns : MX.ninecards.NS.JOINGAME}).up();
            } else {
                errorcallback(null, 'Game JID not defined');
            }

            MX.core.sendIQ(customiq, resultcallback, errorcallback);
        }



	}

	MX.extend('ninecards', ninecards);

})();
