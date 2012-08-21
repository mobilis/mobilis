(function() {
    /** Class: Mobilis.sessionmobility
     *  Session Mobility functionality for the Mobilis Framwork
     *
     */
    var sessionmobility = {
        /** Constants: Session Mobility Namespaces
         *
         *  NS.SESSIONINVITE - SessionInvitationIQ
         */
        NS : {
            SESSIONINVITATION : "mobilis:iq:sessionmobility#sessioninvitation",
            SESSIONTRANSFER : "mobilis:iq:sessionmobility#sessiontransfer",
            KIOSKREQUEST : "mobilis:iq:sessionmobility#kioskrequest"
        },

        /**
         * Sends a SessionInvitationIQ to the specified JID with the given app URI and parameters.
         * @param {Object} jid the invitee the invitation is sent to
         * @param {Object} appuri a nonambiguous application URI
         * @param {Object} params a list of parameters that should enable the invitee to identify the current session
         * @param {Object} resultcallback callback for incoming response IQ stanzas of type RESULT
         * @param {Object} errorcallback callback for incoming response IQ stanzas of type ERROR, or timeout
         */
        inviteToSession : function(jid, appuri, params, resultcallback, errorcallback) {
            var customiq = $iq({
                to : jid,
                type : "set"
            }).c("query", {
                xmlns : Mobilis.sessionmobility.NS.SESSIONINVITATION
            }).c("appuri", {}, appuri).c("params");

            // add parameters
            for ( i = 0; i < params.length; i++) {
                customiq.c("param", {}, params[i]);
            }

            console.log("sending IQ: " + customiq.toString());
            //Mobilis.core.sendIQ(customiq, resultcallback, errorcallback);
        },

        /**
         *
         * @param {Object} jid the session transfer target
         * @param {Object} appuri a nonambiguous application URI
         * @param {Object} mechanisms a list of supported mechanisms. May contain INBAND-XMPP, FILES and BUNDLE-COMPRESSED-ZIP
         * @param {Object} transfercallback callback for the incoming sessiontransfer IQ of type SET
         * @param {Object} resultcallback callback for incoming response IQ stanzas of type RESULT
         * @param {Object} errorcallback callback for incoming response IQ stanzas of type ERROR, or timeout
         */
        requestTransfer : function(jid, appuri, mechanisms, transfercallback, resultcallback, errorcallback) {
            var customiq = $iq({
                to : jid,
                type : "get"
            }).c("query", {
                xmlns : Mobilis.sessionmobility.NS.SESSIONTRANSFER
            }).c("appuri", {}, appuri).c("mechanisms");

            // add mechanisms
            for ( i = 0; i < mechanisms.length; i++) {
                customiq.c("mechanism", {}, mechanisms[i]);
            }

            console.log("sending IQ: " + customiq.toString());
            //Mobilis.core.sendIQ(customiq, resultcallback, errorcallback);
        },

        /**
         *
         * @param {Object} jid the session transfer target
         * @param {Object} appuri a nonambiguous application URI
         * @param {Object} mechanisms a list of supported mechanisms. May contain INBAND-XMPP, FILES and BUNDLE-COMPRESSED-ZIP
         * @param {Object} resultcallback callback for incoming response IQ stanzas of type RESULT
         * @param {Object} errorcallback callback for incoming response IQ stanzas of type ERROR, or timeout
         */
        acceptTransfer : function(jid, appuri, mechanisms, resultcallback, errorcallback) {
            var customiq = $iq({
                to : jid,
                type : "set"
            }).c("query", {
                xmlns : Mobilis.sessionmobility.NS.SESSIONTRANSFER
            }).c("appuri", {}, appuri).c("mechanisms");

            // add mechanisms
            for ( i = 0; i < mechanisms.length; i++) {
                customiq.c("mechanism", {}, mechanisms[i]);
            }

            console.log("sending IQ: " + customiq.toString());
            //Mobilis.core.sendIQ(customiq, resultcallback, errorcallback);
        },

        /**
         * Adds a handler for incoming session invitation requests.
         * @param {Object} handler a function to handle incoming session invitation requests.
         */
        addInvitationHandler : function(handler) {
            Mobilis.connection.addHandler(handler, Mobilis.sessionmobility.NS.SESSIONINVITATION);
        },

        /**
         * Adds a handler for incoming session transfer requests.
         * @param {Object} handler a function to handle incoming session transfer requests
         */
        addTransferHandler : function(handler) {
            Mobilis.connection.addHandler(handler, Mobilis.sessionmobility.NS.SESSIONTRANSFER);
        },
        
        addKioskRequestHandler : function(handler) {
            Mobilis.connection.addHandler(handler, Mobilis.sessionmobility.NS.KIOSKREQUEST);
        }
    };
    Mobilis.extend("sessionmobility", sessionmobility);
})();
