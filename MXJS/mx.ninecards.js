/** File: mx.ninecards.js
*  Mobilis XMPP for JavaScript Framework 9Cards Plugin
*
*  This file will be generated automatically from ninecards.msdl using XSLT-Transformations
*  It should not be modified manually in the future.
*/


var ninecards = {


    init: function() {
        MX.addNamespace('SERVICE', MX.NS.URL + '#services/MobilisNineCardsService');
        MX.addNamespace('CONFIGUREGAME', 'mobilisninecards:iq:configuregame');
        MX.addNamespace('JOINGAME', 'mobilisninecards:iq:joingame');
    },


    createServiceInstance: function (name, resultcallback, errorcallback) {
        var settings = jQuery.jStorage.get('settings');
        var customIq = $iq({
            to: settings.coordinator,
            type: 'set'                
        })
        .c('createNewServiceInstance', {xmlns: MX.NS.COORDINATOR} )
        .c('serviceNamespace').t(MX.NS.SERVICE).up()
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
        .c('ConfigureGameRequest', {xmlns : MX.NS.CONFIGUREGAME})
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
            var customiq = $iq({
                to: gameJid,
                type: 'set'
            })
            .c('JoinGameRequest' , {xmlns : MX.NS.JOINGAME}).up();
        } else {
            errorcallback(null, 'Game JID not defined');
        }

        MX.core.sendIQ(customiq, resultcallback, errorcallback);
    }



}

MX.extend('ninecards', ninecards);
