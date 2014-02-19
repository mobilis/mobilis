/** File: mx.ninecards.js
*  Mobilis XMPP for JavaScript Framework 9Cards Plugin
*
*  This file will be generated automatically from ninecards.msdl using XSLT-Transformations
*  It should not be modified manually in the future.
*/


var ninecards = {


    init: function() {
        MX.addNamespace('SERVICE', MX.NS.URL + '#services/MobilisNineCardsService');
        MX.addNamespace('APP', MX.NS.URL + '/apps/9cards');
        MX.addNamespace('JOINGAME', 'mobilisninecards:iq:joingame');
    },


    createServiceInstance: function (name, result_cb, error_cb) {
        var settings = jQuery.jStorage.get('settings');
        var customIq = $iq({
            to: settings.coordinator,
            type: 'set'                
        })
        .c('createNewServiceInstance', {xmlns: MX.NS.COORDINATOR} )
        .c('serviceNamespace').t(MX.NS.SERVICE).up()
        .c('serviceName').t(name);

        MX.core.sendIQ(customIq, result_cb, error_cb);
    },


    configureGame: function(gameJid, maxPlayers, numberOfRounds, result_cb, error_cb) {

        var customIq = $iq({
            to: gameJid,
            type: 'set'
        })
        .c('ConfigureGameRequest', {xmlns: MX.NS.APP})
        .c('players').t(maxPlayers).up()
        .c('rounds').t(numberOfRounds).up();

        MX.core.sendIQ(customIq, result_cb, error_cb);
    }, 


    getGameConfiguration: function(gameJid, result_cb, error_cb) {

        var customIq = $iq({
            to: gameJid,
            type: 'set'
        })
        .c('GetGameConfigurationRequest', {xmlns: MX.NS.APP})
        console.log(customIq.toString());
        MX.core.sendIQ(customIq, result_cb, error_cb);
    },


    joinGame: function(gameJid, result_cb, error_cb) {

        if (gameJid){ 
            var customiq = $iq({
                to: gameJid,
                type: 'set'
            })
            .c('JoinGameRequest' , {xmlns: MX.NS.JOINGAME}).up();
        } else {
            error_cb(null, 'Game JID not defined');
        }

        MX.core.sendIQ(customiq, result_cb, error_cb);
    }


}

MX.extend('ninecards', ninecards);
