/** File: mx.core.js
*  Mobilis XMPP for JavaScript Framework Core Plugin
*
*  This file contains the core functionality of the MXJS framework. 
*  It extends the MX object created in the main file mx.js
*/

(function() {

	var core = {



		HTTPBIND: 'http://mobilis-dev.inf.tu-dresden.de/http-bind',



		NS : {
			COORDINATOR : "http://mobilis.inf.tu-dresden.de#services/CoordinatorService",
			ADMIN : "http://mobilis.inf.tu-dresden.de#services/AdminService",
			DEPLOYMENT : "http://mobilis.inf.tu-dresden.de#services/DeploymentService",
			NAMESPACE_ERROR_STANZA : "urn:ietf:params:xml:ns:xmpp-stanzas"
		},



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



		SERVICES : {
			"http://mobilis.inf.tu-dresden.de#services/CoordinatorService" : {
				version : "1.0",
				mode : "single",
				instances : "1",
				jid : 'mobilis@mobilis-dev.inf.tu-dresden.de/Coordinator'
			}
		},



		// start: {},



		connect: function(server, jid, password, callback) {

			if (server) {
				MX.core.HTTPBIND = 'http://'+server+'/http-bind';
				MX.core.SERVICES[MX.core.NS.COORDINATOR].jid = 'mobilis@'+server+'/Coordinator'
			}

			var connection = new Strophe.Connection(MX.core.HTTPBIND);
			
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
						$('#error-popup').popup({
							afteropen: function( event, ui ) {
								$(this).find('h1').html('Error');
								$(this).find('.ui-content h3').html('Authentication Fail');
								$(this).find('.ui-content p').html('Please check the settings');
							},
							afterclose: function( event, ui ) {
								MX.connection.disconnect();
								jQuery.mobile.changePage('#start', {
									transition: 'slide',
									reverse: true,
									changeHash: true
								});
							}
						});
						$('#error-popup').popup('open', {
							positionTo: 'window'
						});
					} else if (status == MX.core.Status.CONNECTED) {
						console.log('connected');

						connection.addHandler(
							MX.core.onChatMessage,
							null,
							'message',
							'chat'
						);
						connection.send($pres());

						var discoiq = $iq({
							to: MX.core.SERVICES[MX.core.NS.COORDINATOR].jid,
							type: "get"
						})
						.c("serviceDiscovery", {
							xmlns: MX.core.NS.COORDINATOR
						});

						connection.sendIQ(discoiq,
						function(iq) {
							$(iq).find("mobilisService").each(function() {
								MX.core.SERVICES[$(this).attr('namespace')] =
								{
									'version': $(this).attr('version'),
									'mode': $(this).attr('mode'),
									'instances': $(this).attr('instances'),
									'jid': $(this).attr('jid')
								};
							});
							// if (iq) {console.log(iq);}
							console.log('Initial Service Discovery successful');
						},
						function(iq) {
							// if (iq) {console.log(iq);}
							console.log('Initial Service Discovery failed')
						},
						30000);
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




		sendIQ: function(elem, resultcallback, errorcallback) {
			if (MX.connection) {
				//MX.core.start = (new Date).getTime();
				MX.connection.sendIQ(elem, resultcallback, errorcallback, 3000);
			} else {
				errorcallback(null, 'MX.conneciton not defined');   
			}
		},




		mobilisServiceDiscovery: function(attr, resultcallback, errorcallback) {

			// console.log('attr',attr);

			if (!resultcallback) {
				resultcallback = MX.core.defaultcallback;
			};
			if (!errorcallback) {
				errorcallback = MX.core.defaulterrorback;
			}; 
			var discoiq = $iq({
				to: MX.core.SERVICES[MX.core.NS.COORDINATOR].jid,
				type: "get"
			})
			.c("serviceDiscovery", {
				xmlns: MX.core.NS.COORDINATOR
			});
			if (attr[0] && attr[0] !== null) {
				discoiq.c('serviceNamespace').t(attr[0]);
			};
			if (attr[1] && attr[1] !== null) {
				discoiq.up().c('serviceVersion').t(attr[1]);
			};
			MX.core.sendIQ(discoiq, resultcallback, errorcallback);
		},






		defaultcallback: function (iq) {
			if (iq) {
				// var diff = (new Date).getTime() - MX.core.start;
				// console.log('Stanza received in: ' + diff + ' ms');
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

	MX.extend('core', core);

})();
