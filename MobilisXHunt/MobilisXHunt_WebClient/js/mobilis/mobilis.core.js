(function() {
    /** Class: Mobilis.core
    *  Core functionality for the Mobilis Framwork
    *
    */
    var core = {
        HTTPBIND: 'http://mobilis.inf.tu-dresden.de/http-bind',

        /** Constants: Mobilis Services Namespaces
         *
         *  NS.COORDINATOR - Coordinator Service.
         *  NS.ADMIN - Admin Service.
         *  NS.DEPLOYMENT - Deployment Service.
         *  NS.XHUNT - MobilisXHunt Service.
         */
        NS: {
            PUBSUB: 'http://jabber.org/protocol/pubsub',
            COORDINATOR: 'http://mobilis.inf.tu-dresden.de#services/CoordinatorService',
            ADMIN: 'http://mobilis.inf.tu-dresden.de#services/AdminService',
            DEPLOYMENT: 'http://mobilis.inf.tu-dresden.de#services/DeploymentService',
            XHUNT: 'http://mobilis.inf.tu-dresden.de#services/MobilisXHuntService'
        },

        /** Constants: Connection Status Constants
         *  Connection status constants for use by the connection handler
         *  callback.
         *
         *  Status.ERROR - An error has occurred
         *  Status.CONNECTING - The connection is currently being made
         *  Status.CONNFAIL - The connection attempt failed
         *  Status.AUTHENTICATING - The connection is authenticating
         *  Status.AUTHFAIL - The authentication attempt failed
         *  Status.CONNECTED - The connection has succeeded
         *  Status.DISCONNECTED - The connection has been terminated
         *  Status.DISCONNECTING - The connection is currently being terminated
         *  Status.ATTACHED - The connection has been attached
         */
        Status: {
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

        /** Object: Services
         *  Object containing Services objects referenced by their namespace.
         */
        SERVICES: {
            'http://mobilis.inf.tu-dresden.de#services/CoordinatorService': {
                version: '1.0',
                mode: 'single',
                instances: '1',
                jid: 'mobilis@mobilis.inf.tu-dresden.de/Coordinator'
            }
        },
        
        start: {},

        /** Function: connect
        *  Establishes connection to XMPP over BOSH
        *
        *  Parameters:
        *    (String) jid - JID of the user
        *    (String) password - password for the jid
        *    (Function) callback - Callback function using connection status constants
        */
        connect: function(jid, password, callback) {
            var conn = new Strophe.Connection(Mobilis.core.HTTPBIND);
            conn.rawInput = function (data) { 
                //console.log('RECV: ' + data);
    	    	//$('#log2').append('<div></div>').append(document.createTextNode('RECV: ' + data ))
    	    };
    	    conn.rawOutput = function (data) {
    	        //console.log('Send: ' + data);
    	    	//$('#log1').append('<div></div>').append(document.createTextNode('SEND: ' + data ))
    	    };
    	    //var barejid = jid + '@mobilis.inf.tu-dresden.de';
    	    console.log(jid);
            conn.connect(jid, password,
            function(status) {
                if (status == Mobilis.core.Status.ERROR) {
                    console.log('connection error');
                } else if (status == Mobilis.core.Status.CONNECTING) {
                    console.log('connecting');
                } else if (status == Mobilis.core.Status.CONNFAIL) {
                    console.log('connection fail');
                } else if (status == Mobilis.core.Status.AUTHENTICATING) {
                    console.log('authenticating');
                } else if (status == Mobilis.core.Status.AUTHFAIL) {
                    console.log('authentication fail');
                } else if (status == Mobilis.core.Status.CONNECTED) {
                    console.log('connected');
                    conn.send($pres());
                    var discoiq = $iq({
                        to: Mobilis.core.SERVICES[Mobilis.core.NS.COORDINATOR].jid,
                        type: "get"
                    })
                    .c("serviceDiscovery", {
                        xmlns: Mobilis.core.NS.COORDINATOR
                    });
                    conn.sendIQ(discoiq,
                    function(iq) {
                        $(iq).find("mobilisService").each(function() {
                            Mobilis.core.SERVICES[$(this).attr('namespace')] =
                            {
                                'version': $(this).attr('version'),
                                'mode': $(this).attr('mode'),
                                'instances': $(this).attr('instances'),
                                'jid': $(this).attr('jid')
                            };
                        });
                        console.log('Initial Service Discovery successful');
                    },
                    function(iq) {
                        if (iq) {console.log(iq);}
                        console.log('Initial Service Discovery failed')
                    },
                    30000);
                } else if (status == Mobilis.core.Status.DISCONNECTED) {
                    console.log('disconnected');
                } else if (status == Mobilis.core.Status.DISCONNECTING) {
                } else if (status == Mobilis.core.Status.ATTACHED) {}
                if (callback) {
                    callback(status);
                }
            });

            Mobilis.connection = conn;
        },

        /** Function: disconnect
        *  Initiates a graceful teardown of the connection
        *
        *  Parameters:
        *    (String) reason - reason for disconnection
        */
        disconnect: function(reason) {
            console.log('disconnect');
            Mobilis.connection.disconnect(reason);
        },

        /** Function: send
        *  Send stanza that does not require acknowledgement, such as <msg> or <presence>
        *
        *  Parameters:
        *    (XMLElement) elem - Stanza to send.
        */
        send: function(elem) {
            Mobilis.connection.send(elem);
            // Mobilis.connection.send(
            // $pres(attr));
            // if (typeof attr === "undefined") {} else {}
        },

        /** Function: sendIQ
        *  Sends stanza that requires acknowledgement, such as <iq>. Callback functions are specified for the response stanzas
        *
        *  Parameters:
        *    (XMLElement) elem - Stanza to send.
        *    (Function) resultcallback - Callback for incoming response IQ stanzas of type RESULT
        *    (Function) errorcallback - Callback for incoming response IQ stanzas of type ERROR, or timeout
        */
        sendIQ: function(elem, resultcallback, errorcallback) {
            if (Mobilis.connection) {
                Mobilis.core.start = (new Date).getTime();
                Mobilis.connection.sendIQ(elem, resultcallback, errorcallback, 3000);
            } else {
                errorcallback(null, 'Mobilis.conneciton not defined');   
            }
        },

        /** Function: createServiceInstance
        *  Create Mobilis Service Instance
        *
        *  Parameters:
        *    (Array) attr - Attributes [serviceNamespace, serviceName, servicePassword]
        *    (Function) resultcallback - Callback for incoming response IQ stanzas of type RESULT
        *    (Function) errorcallback - Callback for incoming response IQ stanzas of type ERROR, or timeout
        */
        createServiceInstance: function(attr, resultcallback, errorcallback) {
            if (!resultcallback) {
                resultcallback = Mobilis.core.defaultcallback;
            };
            if (!errorcallback) {
                errorcallback = Mobilis.core.defaulterrorback;
            };

            if (Mobilis.core.SERVICES[Mobilis.core.NS.COORDINATOR]){

                var customiq = $iq({
                    to: Mobilis.core.SERVICES[Mobilis.core.NS.COORDINATOR].jid,
                    type: 'set'
                })
                .c('createNewServiceInstance' , {xmlns : Mobilis.core.NS.COORDINATOR});
                if (attr[0] && attr[0] !== null) {
                    customiq.c('serviceNamespace').t(attr[0]);
                }
                if (attr[1] && attr[1] !== null) {
                    customiq.up().c('serviceName').t(attr[1]);
                }
                if (attr[2] && attr[2] !== null) {
                    customiq.up().c('servicePassword').t(attr[2]);
                }               

            } else {
                errorcallback(null, 'Mobilis.core.SERVICES.' + Mobilis.core.NS.COORDINATOR + ' not defined');
            }

            Mobilis.core.sendIQ(customiq, resultcallback, errorcallback);
        },

        /** Function: mobilisServiceDiscovery
        *  Performes a Mobilis Service discovery with the Mobilis Server
        *
        *  Parameters:
        *    (Array) attr - Attributes for requests for specific Mobilis Services[Namespace, Version]
        *    (Function) resultcallback - Callback for incoming response IQ stanzas of type RESULT
        *    (Function) errorcallback - Callback for incoming response IQ stanzas of type ERROR, or timeout
        */
        mobilisServiceDiscovery: function(attr, resultcallback, errorcallback) {
            if (!resultcallback) {
                resultcallback = Mobilis.core.defaultcallback;
            };
            if (!errorcallback) {
                errorcallback = Mobilis.core.defaulterrorback;
            }; 
            var discoiq = $iq({
                to: Mobilis.core.SERVICES[Mobilis.core.NS.COORDINATOR].jid,
                type: "get"
            })
            .c("serviceDiscovery", {
                xmlns: Mobilis.core.NS.COORDINATOR
            });
            if (attr[0] && attr[0] !== null) {
                discoiq.c('serviceNamespace').t(attr[0]);
            };
            if (attr[1] && attr[1] !== null) {
                discoiq.up().c('serviceVersion').t(attr[1]);
            };
            Mobilis.core.sendIQ(discoiq, resultcallback, errorcallback);
        },
        
        getUnixTime: function () {
            var genericdate = new Date; 
            var unixtime_ms = genericdate.getTime(); 
            var unixtime = parseInt(unixtime_ms / 1000);
            return unixtime;
        },
        
        defaultcallback: function (iq) {
            if (iq) {
                var diff = (new Date).getTime() - Mobilis.core.start;
                console.log('Stanza received in: ' + diff + ' ms');
                console.log(iq);

            }
        },
        
        defaulterrorback: function (iq,msg) {
            console.log('default error from core');
            if (iq) {
                console.log(iq);
            }
            if (msg) {
                console.log(msg);
            }    
        }

    };
    Mobilis.extend("core", core);
})();
