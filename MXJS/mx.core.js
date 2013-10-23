/** File: mx.core.js
*  Mobilis XMPP for JavaScript Framework Core Plugin
*
*  This file contains the core functionality of the MXJS framework. 
*  It extends the MX object created in the main file mx.js
*/


var core = {

    init: function() {
        MX.addNamespace('URL', 'http://mobilis.inf.tu-dresden.de');
        MX.addNamespace('COORDINATOR', MX.NS.URL + '#services/CoordinatorService');
    },

    Status: { // TODO: refactor this
        ERROR: 0,
        CONNECTING: 1,
        CONNFAIL: 2,
        AUTHENTICATING: 3,
        AUTHFAIL: 4,
        CONNECTED: 5,
        DISCONNECTED: 6,
        DISCONNECTING: 7,
        ATTACHED: 8
    },




    connect: function(server, jid, password, callback) {

        var connection = new Strophe.Connection('http://'+server+'/http-bind');

        connection.rawInput = function (data) {
            // console.log('RECV: ' + data);
        };
        connection.rawOutput = function (data) {
            // console.log('SEND: ' + data);
        };

        connection.connect(
            jid,
            password,
            function(status) {
                if (status == MX.core.Status.ERROR) {
                    console.log('connection error');
                } else if (status == MX.core.Status.CONNECTING) {
                    console.log('connecting');
                } else if (status == MX.core.Status.CONNFAIL) {
                    console.log('connection fail');
                } else if (status == MX.core.Status.AUTHENTICATING) {
                    console.log('authenticating');
                } else if (status == MX.core.Status.AUTHFAIL) {
                    console.log('authentication fail');
                } else if (status == MX.core.Status.CONNECTED) {
                    console.log('connected');

                    connection.addHandler(
                        MX.core.onChatMessage,
                        null,
                        'message',
                        'chat'
                    );
                    connection.send($pres());

                } else if (status == MX.core.Status.DISCONNECTED) {
                    console.log('disconnected');
                } else if (status == MX.core.Status.DISCONNECTING) {
                    console.log('disconnecting');
                } else if (status == MX.core.Status.ATTACHED) {
                    console.log('attached');
                }
                if (callback) {
                    callback(status);
                }
            }
        );

        MX.connection = connection;
    },


    onChatMessage : function(message){
        console.log('private message:',message);
    },


    disconnect : function(reason) {

        if (MX.connection) {
            if (MX.connection.connected) {
                MX.connection.send($pres({
                    type : 'unavailable'
                }));
                MX.connection.disconnect(reason);
            };
        }

    },




    send: function(elem) {
        MX.connection.send(elem);
    },




    sendIQ: function(elem, callback, errback) {
        if (MX.connection) {
            MX.connection.sendIQ(elem, callback, errback, 3000);
        } else {
            errback('MX.conneciton not defined');
        }
    },





    discoverService: function(service, callback, errback, version) {

        var iq = $iq({
            to: jQuery.jStorage.get('settings').coordinator,
            type: 'get'
        })
        .c('serviceDiscovery', {
            xmlns: MX.NS.COORDINATOR
        });

        if (service && service !== null) {
            iq.c('serviceNamespace').t(service);
        };

        if (version && version !== null) {
            iq.up().c('serviceVersion').t(version);
        };

        MX.core.sendIQ(iq, callback, errback);
    },




};

MX.extend('core', core);
