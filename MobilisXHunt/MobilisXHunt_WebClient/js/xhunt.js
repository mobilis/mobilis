var xhunt = {
	NAME: localStorage.getItem('mobilis.xhunt.username'),
	JID: localStorage.getItem('mobilis.xhunt.jid'),
	PASSWORD: localStorage.getItem('mobilis.xhunt.password'),
	gameinfo: {
		joined : 'false'
	},
	test: {
		'round' : '0'
	},
	tickets :{
		'4' : {
			'title' : 'Black',
			'url' :  'http://mobilis.inf.tu-dresden.de/bilder/ti_black.png'
		},
		'1' : {
			'title' : 'Tram',
			'url' :  'http://mobilis.inf.tu-dresden.de/bilder/ti_tram.png'
		},
		'2' : {
			'title' : 'Bus',
			'url' :  'http://mobilis.inf.tu-dresden.de/bilder/ti_bus.png'
		},
		'3' : {
			'title' : 'Railway',
			'url' :  'http://mobilis.inf.tu-dresden.de/bilder/ti_railway.png'
		}
	},
	icons : {
		black : {
			url : 'http://mobilis.inf.tu-dresden.de/bilder/ic_player_mrx_36.png',
			color : '#000',
			used : 'false',
			markericon : new google.maps.MarkerImage('http://mobilis.inf.tu-dresden.de/bilder/ic_player_mrx_36.png',
				new google.maps.Size(36, 36),
				new google.maps.Point(0, 0),
				new google.maps.Point(18, 26),
				new google.maps.Size(36, 36)),
		},
		blue : {
			url : 'http://mobilis.inf.tu-dresden.de/bilder/ic_player_blue_36.png',
			color : '#04F',
			used : 'false',
			markericon : new google.maps.MarkerImage('http://mobilis.inf.tu-dresden.de/bilder/ic_player_blue_36.png',
				new google.maps.Size(36, 36),
				new google.maps.Point(0, 0),
				new google.maps.Point(18, 26),
				new google.maps.Size(36, 36)),
		
		},
		green : {
			url : 'http://mobilis.inf.tu-dresden.de/bilder/ic_player_green_36.png',
			color : '#2F0',
			used : 'false',
			markericon : new google.maps.MarkerImage('http://mobilis.inf.tu-dresden.de/bilder/ic_player_green_36.png',
				new google.maps.Size(36, 36),
				new google.maps.Point(0, 0),
				new google.maps.Point(18, 26),
				new google.maps.Size(36, 36)),
		},
		orange : {
			url : 'http://mobilis.inf.tu-dresden.de/bilder/ic_player_orange_36.png',
			color : '#F90',
			used : 'false',
			markericon : new google.maps.MarkerImage('http://mobilis.inf.tu-dresden.de/bilder/ic_player_orange_36.png',
				new google.maps.Size(36, 36),
				new google.maps.Point(0, 0),
				new google.maps.Point(18, 26),
				new google.maps.Size(36, 36)),
		},
		red : {
			url : 'http://mobilis.inf.tu-dresden.de/bilder/ic_player_red_36.png',
			color : '#F00',
			used : 'false',
			markericon : new google.maps.MarkerImage('http://mobilis.inf.tu-dresden.de/bilder/ic_player_red_36.png',
				new google.maps.Size(36, 36),
				new google.maps.Point(0, 0),
				new google.maps.Point(18, 26),
				new google.maps.Size(36, 36)),
		},
		yellow : {
			url : 'http://mobilis.inf.tu-dresden.de/bilder/ic_player_yellow_36.png',
			color : '#EF0',
			used : 'false',
			markericon : new google.maps.MarkerImage('http://mobilis.inf.tu-dresden.de/bilder/ic_player_yellow_36.png',
				new google.maps.Size(36, 36),
				new google.maps.Point(0, 0),
				new google.maps.Point(18, 26),
				new google.maps.Size(36, 36)),
		}
	},
	hlst: new google.maps.MarkerImage('http://mobilis.inf.tu-dresden.de/bilder/station_50px.gif',
		new google.maps.Size(18, 18),
		new google.maps.Point(0, 0),
		new google.maps.Point(9, 9),
		new google.maps.Size(18, 18)),
	colors: ['#800000', '#008000', '#0000A0'],
	stations: {},
	players: {},
















/* ============================================================================================================== 

 #    #  #    #  #    #  #    #  #####      #    #    ##    #    #  #####   #       ######  #####    ####  
  #  #   #    #  #    #  ##   #    #        #    #   #  #   ##   #  #    #  #       #       #    #  #      
   ##    ######  #    #  # #  #    #        ######  #    #  # #  #  #    #  #       #####   #    #   ####  
   ##    #    #  #    #  #  # #    #        #    #  ######  #  # #  #    #  #       #       #####        # 
  #  #   #    #  #    #  #   ##    #        #    #  #    #  #   ##  #    #  #       #       #   #   #    # 
 #    #  #    #   ####   #    #    #        #    #  #    #  #    #  #####   ######  ######  #    #   ####  

============================================================================================================== */









	onPlayersRequest: function (iq) { //  <PlayersRequest xmlns="mobilisxhunt:iq:players">

		console.log('PlayersRequest: ');
		console.log(iq); 

		Mobilis.xhunt.respondPlayer($(iq).attr('from'));

					//			$('#user-list').append('<li>' + from + '</li>').listview('refresh');

		// $('#players').html('');
		$(iq).find('PlayerInfo').each(function(){ 
			// var flag_ready = '';
			var jid = $(this).find('Jid').text(	);
			console.log('PlayerInfo Jid: '+jid);
			xhunt.createPlayer(jid);

			var player = xhunt.players[jid];
			player.name = $(this).find('PlayerName').text();
			player.ismod = $(this).find('IsModerator').text();
			player.ismrx = $(this).find('IsMrx').text();

			var ready = $(this).find('IsReady').text();
			var flag_ready = (ready === 'true') ? '  &#10003;' : '';
			$('#user-list').append('<li><img class="ui-li-icon" src="' + player.icon.url +' "/>' + player.name + flag_ready +'</li>').listview('refresh');
			// $('#players').append('<div id="' + jid + '" class="playerinfobox"></div>')
			// $('#' + jid ).append('<div class="playername"><img class="playericon" src="' + 
			// player.icon.url +' "/>' + 
			// player.name + flag_ready + '</div>' +
			// '</div><span class="lat">' + 
			// '</span><span class="lon">' + 
			// '</span><div class="playertarget">' + 
			// '</div><div class="playertickets" style="font-size:small;">' + 
			// '<div id="'+ jid +'_4" class="tickets"></div>' +
			// '<div id="'+ jid +'_1" class="tickets"></div>' +
			// '<div id="'+ jid +'_2" class="tickets"></div>' +
			// '<div id="'+ jid +'_3" class="tickets"></div>' +
			// '</div>');       
		});
		console.log('xhunt.players:' );
		console.log(xhunt.players);

		var info = $(iq).find('Info').text();
		console.log('Info: '+ info );

		return true;   
	},
	onUpdatePlayerResponse: function (iq) {
		console.log('UpdatePlayerResponse:'); 
		console.log($(iq).find('Info').text()); 
	},

/*	on_gameOverIQ: function (iq) {
		console.log('Game Over');
		//$.jGrowl('Game Over');
		console.log($(iq).find('reason').text(), {sticky: true });   
		//$.jGrowl($(iq).find('reason').text(), {sticky: true });   
		return true;  
	},
*/	
/*	on_usedTicketsIQ: function (iq) {
		$(iq).find('player').each(function(){
			var jid = Strophe.getNodeFromJid($(this).find('playername').text());
			var ticket = $(this).find('ticketid').text();
			$('#' + jid + '_' + ticket).append('+');
		});
		//console.log('usedTickets');
		//console.log(iq);   
		return true;
	},
*/	
/*	on_startRoundIQ: function (iq) {
		var round = $(iq).find('gameround').text();
		var nextround = parseInt( round, 10 ) + 1 ;
		$('#roundinfo').html('<div id="roundinfo_content">Round : ' + 
			round + '   Show MrX: <span id="showmrx"></span></div>');
		var showmrx = $(iq).find('showmrx').text();
		if (showmrx === 'true')
			$('#showmrx').html('YES');
		else {
			$('#showmrx').html('NO');
		}
		$.each(xhunt.players, function(index, value) { 
			if (parseInt( value.round , 10 )=== parseInt( round, 10 )){
				value.round = nextround;
				$(iq).find('ticket').each(function(){
					var ticketid = $(this).find('ticketid').text();
					var ticketamount =$(this).find('ticketamount').text();
					$('#' + index + '_' + ticketid).html('<img class="ticketicon" src="'+ xhunt.tickets[ticketid].url +'"/> ' + ticketamount);
				})                    
				return false;
			} 
		});

		return true;   
	},
*/
/*	on_roundStatusIQ: function (iq) {
		
		$(iq).find('player').each(function(){

			var jid = Strophe.getNodeFromJid($(this).find('jid').text());
			var stationID = $(this).find('stationid').text();
			var isfinal = $(this).find('isfinal').text();
			var arrived = $(this).find('arrived').text();
			if (xhunt.stations[stationID])
				var stationName = xhunt.stations[stationID].name;
			else
				var stationName = '-';
			if (arrived === 'true') {
				$('#' + jid + ' > .playertarget').html('Target Station: -');
				$('#map').gmap3({
				  action:'clear',
				  name:'polyline',
				  tag: jid, 
				});   
			} else {
				$('#' + jid + ' > .playertarget').html('Target Station: ' + stationName);
				if (xhunt.stations[stationID]){
					//.log('setTargetPolyline');
					xhunt.players[jid].tlat = xhunt.stations[stationID].lat;
					xhunt.players[jid].tlon = xhunt.stations[stationID].lon;
				}
				$('#map').gmap3(
				   {
					   action: 'addPolyline',
					   options: {
						   strokeColor: xhunt.players[jid].icon.color,
						   strokeOpacity: 1.0,
						   strokeWeight: 8
					   },
					   path: [[xhunt.players[jid].lat , xhunt.players[jid].lon], [xhunt.players[jid].tlat, xhunt.players[jid].tlon]],
					   tag: jid
				   }
				);
			}  


  
		});
		return true;   
	},
*/
/*	on_locationIQ: function (iq) {
		$(iq).find('location').each(function(){
			var jid = Strophe.getNodeFromJid($(this).find('jid').text());
			var lat = xhunt.convert($(this).find('lat').text());
			var lon = xhunt.convert($(this).find('lon').text());
			xhunt.players[jid].lat = lat;
			xhunt.players[jid].lon = lon;
						
			$('#map').gmap3({
			  action:'clear',
			  name:'marker',
			  tag: jid, 
			});
			$('#map').gmap3(
			   {
				   action: 'addMarker',
				   latLng: [lat, lon],
				   options: { 
					   icon: xhunt.players[jid].icon.markericon,
				   },
				   tag: jid
			   }
			);
			$('#' + jid + ' > .lat').html('Latitude: ' + lat + ', ');
			$('#' + jid + ' > .lon').html('Longitude: ' + lon);       
		});
		return true;   
	},
*/
/*	on_invitation: function (iq) {
		var gameJid = $(iq).find('param').first().text();
		var gameName = $(iq).find('param').next().text();
		xhunt.gameinfo['gameJID'] = gameJid;
		xhunt.gameinfo['name'] = gameName;
		xhunt.joinGame( { jid: $(this).attr('id') } );
		$('#login_dialog').dialog('close');
		return true;   
	},
*/













/* ============================================================================================================== 

 #    #  #    #  #    #  #    #  #####      #    #  ######  #####  #    #   ####   #####    ####  
  #  #   #    #  #    #  ##   #    #        ##  ##  #         #    #    #  #    #  #    #  #      
   ##    ######  #    #  # #  #    #        # ## #  #####     #    ######  #    #  #    #   ####  
   ##    #    #  #    #  #  # #    #        #    #  #         #    #    #  #    #  #    #       # 
  #  #   #    #  #    #  #   ##    #        #    #  #         #    #    #  #    #  #    #  #    # 
 #    #  #    #   ####   #    #    #        #    #  ######    #    #    #   ####   #####    ####  

============================================================================================================== */








	addHandlers : function () {
		Mobilis.xhunt.addLocationHandler(xhunt.on_locationIQ);
		Mobilis.xhunt.addPlayersHandler(xhunt.onPlayersRequest);
		Mobilis.xhunt.addUpdatePlayerHandler(xhunt.onUpdatePlayerResponse);
		Mobilis.xhunt.addStartRoundHandler(xhunt.on_startRoundIQ);
		Mobilis.xhunt.addRoundStatusHandler(xhunt.on_roundStatusIQ);
		// Mobilis.xhunt.addJoinGameHandler(xhunt.onJoinGameResponse);

/*		
		Mobilis.connection.addHandler(xhunt.on_gameOverIQ, 'mobilisxhunt:iq:gameover', 'iq', 'set');
*/
	},

	connect : function() {
		// if (Mobilis.core.Status.CONNECTED){
		// 	Mobilis.core.disconnect('reconnect');
		// };
		console.log('connect: ' + xhunt.JID + ' '+ xhunt.PASSWORD);
		Mobilis.core.connect(
		xhunt.JID,
		xhunt.PASSWORD,
		function(status) {
			
			if (status == Mobilis.core.Status.CONNECTED) {
				xhunt.queryGames();
			} else {
				console.log('connection failed')
			}
		}
		);
	},


	queryGames : function() {

		$("#game-list > li").each(function(n,item){
			jQuery(item).remove(); //remove old game list items
		});

		Mobilis.core.mobilisServiceDiscovery(
			[Mobilis.core.NS.XHUNT],
			function (iq) {
				var gameList = $('#game-list');
				if ($(iq).find("mobilisService").length){
					gameList.append('<li data-role="divider">Available Games</li>');
					$(iq).find("mobilisService").each(function() {
						Mobilis.core.SERVICES[$(this).attr('namespace')] = {
							'version': $(this).attr('version'),
							'jid': $(this).attr('jid'),
							'servicename' : $(this).attr('serviceName')
						};
						gameList.append('<li><a class="available-game" id="' + $(this).attr('jid') + '" href="#" data-transition="slide" data-rel="back">' + $(this).attr('serviceName') + '</a></li>').listview('refresh');
					}); 
				} else {
					gameList.append('<li>No games found</li>').listview('refresh');
				}
			}
		);

		$('#game-list-container').popup('open', {
			positionTo: 'window',
			theme: 'b',
			corners: false
		});

	},


	sendChat : function (message) {

		if (xhunt.gameinfo['room']) {
			Mobilis.connection.muc.message(
				xhunt.gameinfo['room'],
				xhunt.gameinfo['nick'], 
				message, 
				'groupchat');
		}

	},

	updatePlayer : function () {
		Mobilis.xhunt.updatePlayer(
			localStorage.getItem('mobilis.xhunt.gamejid'), //gameJID
			Mobilis.connection.jid, //playerJid
			xhunt.NAME, //playerName
			false, //isModerator
			false, //isMrX
			true, //isReady
			function (iq){  // resultcallback
				console.log('updatePlayer result:');
				console.log(iq);

			},
			function (iq){ // errorcallback
				console.log('updatePlayer result:');
				console.log(iq);
			}
		);
	},

	joinGame : function (data) {
		Mobilis.xhunt.joinGame(
			data.jid, //gameJID
			xhunt.NAME, //playerName
			function (iq){  // resultcallback: <JoinGameResponse xmlns="mobilisxhunt:iq:joingame">

				console.log('JoinGameResponse: ');
				console.log(iq);

				var room = $(iq).find('ChatRoom').text();
				var pwd = $(iq).find('ChatPassword').text();
				
				xhunt.gameinfo['room'] = room.toLowerCase();
				xhunt.gameinfo['pwd'] = pwd;
				xhunt.gameinfo['nick'] = xhunt.NAME;

  				$('#query-games-button').remove();
				$('#game-name').prepend(xhunt.gameinfo.name+' | Mobilis XHunt');
				$('title').prepend(xhunt.gameinfo.name+' | Mobilis XHunt');
				$('#header').append('<a href="#user-list-panel" class="ui-btn-left" id="show-players-button" data-rel="popup" data-position-to="window" data-role="button">Show Players</a>');
				$('#header').append('<a href="#"class="ui-btn-right" id="ingame-menu-button" data-rel="popup" data-position-to="window" data-role="button">Menu</a>');

				$('#ingame-menu-container').popup('open', {
					positionTo: 'window',
					theme: 'b',
					corners: true
				});

				$('#user-list').append('<li data-role="divider">Present Users</li>').listview('refresh');

				if (xhunt.gameinfo['joined'] === 'false'){
					Mobilis.connection.muc.join(
						xhunt.gameinfo['room'], // room
						xhunt.gameinfo['nick'], // nick
						function(message) {     // msg_handler_cb: <message .../>

							if ( from = Strophe.getResourceFromJid($(message).attr('from')) ){
								var htmlMsg =  '<div class="message">' +
													'<strong>' + from + ': </strong>' +
													$(message).text() +
													'</div>';
								console.log(htmlMsg);
								$('#chat-panel').append(htmlMsg);
							}
							return true;
						}, 						
						function (presence){       // pres_handler_cb: <presence ... />

							if ( from = Strophe.getResourceFromJid($(presence).attr('from')) ){
								console.log('presence: ' + from)
								// $('#user-list').append('<li>' + from + '</li>').listview('refresh');
							}
							return true;
						},
						xhunt.gameinfo['pwd']  // password
					);
					xhunt.gameinfo['joined'] = 'true';
				}

			},
			function (iq){ // errorcallback
				console.log('joinGame error:');
				console.log(iq);

				xhunt.queryGames();
			}
		);

	},

	exitGame : function () {
		if (Mobilis.connection.connected) {
			if (Mobilis.xhunt.gameJID){
				Mobilis.xhunt.playerExit(
					localStorage.getItem('mobilis.xhunt.gamejid'), //gameJID
					Mobilis.connection.jid, //playerJid
					function (iq){  // resultcallback
						console.log('playerExit result:');
						console.log(iq);
					}, 
					function (iq){ // errorcallback
						console.log('playerExit error:');
						console.log(iq);
					}
				);
				$('#ingame-menu-button').remove();
				$('#show-players-button').remove();
				$('#game-name').empty().append('Mobilis XHunt');
				$('title').empty().append('Mobilis XHunt');
				$('#header').append('<a href="#" class="ui-btn-right" id="query-games-button" >Games</a>');

				xhunt.queryGames();

				// $('#players').html('');  
				// $('#roundinfo').html('');
				// $('#chatdisplay').html(''); 
				// $('#gameinformation').html('Game Information');
				// $('#map_canvas').gmap3({
				// 	action:'clear',
				// 	name:'polyline'
				// });  
				// $('#map_canvas').gmap3({
				// 	action:'clear',
				// 	name:'marker'
				// });

			}
		}

	},

	initMap : function(data) {
		$.ajax({
			type: "GET",
			url: data.xml,
			dataType: "xml",
			success: function(xml) {
				$(xml).find('Station').each(function() {
					xhunt.stations[$(this).attr('id')] = {
						'abbrev': $(this).attr('abbrev'),
						'name': $(this).attr('name'),
						'lat': $(this).attr('latitude'),
						'lon' : $(this).attr('longitude')
					};
					$('#map').gmap3({
						action: 'addMarker',
						latLng: [$(this).attr('latitude'), $(this).attr('longitude')],
						marker: {
							options: {
								icon: xhunt.hlst,
								title: $(this).attr('name')
							}
						},
					}//,"autofit"
					);
				});
				$(xml).find('Route').each(function() {
					var stops = [];
					var i = 0;
					var colornummer = $(this).attr('type');

					$(this).find('stop').each(function() {
						$(xml).find("Station[id='" + $(this).text() + "']").each(function() {
							var latitude = $(this).attr('latitude');
							var longitude = $(this).attr('longitude');
							stops[i] = [];
							stops[i][0] = latitude;
							stops[i][1] = longitude;
							i++;
						});

					});

					$('#map').gmap3({
						action: 'addPolyline',
						options: {
							strokeColor: xhunt.colors[colornummer],
							strokeOpacity: 1.0,
							strokeWeight: 2
						},
						path: stops
					});
				});
			}
		});
		
	},

	createPlayer : function (jid) {
		if (!xhunt.players[jid]) {
			xhunt.players[jid] = {
				'name'  : '',
				'lat'   : '',
				'lon'   : '',
				'tlat'  : '',
				'tlon'  : '',
				'ismrx' : '',
				'ismod' : '',
				'icon'  : '',
				'round' : '0'
			};
			$.each(xhunt.icons, function(index, value) { 
				if (value.used === 'false'){
					value.used = 'true';
					xhunt.players[jid].icon = value;                    
					return false;
				} else {
					//console.log('out of colors');
				}
			});
		}
	},














/* ============================================================================================================== 

 #    #  ######  #       #####   ######  #####    ####  
 #    #  #       #       #    #  #       #    #  #      
 ######  #####   #       #    #  #####   #    #   ####  
 #    #  #       #       #####   #       #####        # 
 #    #  #       #       #       #       #   #   #    # 
 #    #  ######  ######  #       ######  #    #   ####  

============================================================================================================== */








	convert : function (num) {
		var num=num;
		ans="";
		for (var i=0;i<num.length;i++){
			if (i==num.length-6)
				ans=ans+"."+num.charAt(i);
			else
				ans=ans+num.charAt(i);
		}
		return ans;
	},

	log: function (msg) {
		console.log(msg);
		//$('#logwindow').append('<div class="message" ></div>').append(document.createTextNode(msg))
	},
};















/* ============================================================================================================== 

      #   ####   #    #  ######  #####   #   #      #    #    ##    #    #  #####   #       ######  #####    ####  
      #  #    #  #    #  #       #    #   # #       #    #   #  #   ##   #  #    #  #       #       #    #  #      
      #  #    #  #    #  #####   #    #    #        ######  #    #  # #  #  #    #  #       #####   #    #   ####  
      #  #  # #  #    #  #       #####     #        #    #  ######  #  # #  #    #  #       #       #####        # 
 #    #  #   #   #    #  #       #   #     #        #    #  #    #  #   ##  #    #  #       #       #   #   #    # 
  ####    ### #   ####   ######  #    #    #        #    #  #    #  #    #  #####   ######  ######  #    #   ####  

============================================================================================================== */








$(document).on('pageinit', '#game-page', function() {

	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(
			function (position) {

				var current_latitude = position.coords.latitude;
				var current_longitude = position.coords.longitude;

				$('#map').gmap3({
					action: 'init',
						options:{
							center: [current_latitude, current_longitude],
							zoom: 15,
							mapTypeId: google.maps.MapTypeId.MAP,
							mapTypeControl: false,
							navigationControl: true,
							scrollwheel: true,
							streetViewControl: false
						}
					}		
				).css('height','100%');

				xhunt.initMap( { xml: 'data/sites.xml' } );
				
				xhunt.connect();

			},
			function (msg) {
				console.log(typeof msg == 'string' ? msg : "error");
			}
		);
	} else {
		alert('HTML5 GeoLocation not supported');
	}

});


$(document).on('click', '.available-game', function () {

	localStorage.setItem('mobilis.xhunt.gamejid', $(this).attr('id'));

	xhunt.gameinfo['gameJID'] = $(this).attr('id');
	xhunt.gameinfo['name'] = $(this).text();


	xhunt.addHandlers();
	xhunt.joinGame( { jid: $(this).attr('id') } );
	
});


$(document).on('click', '#send-message-button', function () {
	xhunt.sendChat('Chat!');
});


$(document).on('click', '#get-ready-button', function() {
	xhunt.updatePlayer();
});


$(document).on('click', '#exit-game-button', function() {
	xhunt.exitGame();
});

$(document).on('click', '#query-games-button', function() {
	xhunt.queryGames();
});

$(document).on('click', '#ingame-menu-button', function() {
	$('#ingame-menu-container').popup('open', {
		positionTo: 'window',
		theme: 'b',
		corners: true
	});
});


$(document).on('pageinit', '#settings-page', function() {
	$('#settings-form #username').val( function() {
		return localStorage.getItem('mobilis.xhunt.username');
	});
	$('#settings-form #jid').val( function() {
		return localStorage.getItem('mobilis.xhunt.jid');
	});
	$('#settings-form #password').val( function() {
		return localStorage.getItem('mobilis.xhunt.password');
	});
});


$(document).on('click', '#settings-form #submit', function() {
	localStorage.setItem('mobilis.xhunt.username', $('#settings-form #username').val());
	localStorage.setItem('mobilis.xhunt.jid', $('#settings-form #jid').val());
	localStorage.setItem('mobilis.xhunt.password', $('#settings-form #password').val());
	console.log(localStorage.getItem('mobilis.xhunt.username'));
	console.log(localStorage.getItem('mobilis.xhunt.jid'));
	console.log(localStorage.getItem('mobilis.xhunt.password'));
	return true;
});



$(window).on('orientationchange resize pageshow', function() {

	/* http://www.semicomplete.com/blog/geekery/jquery-mobile-full-height-content
	 * Some orientation changes leave the scroll position at something
	 * that isn't 0,0. This is annoying for user experience. */
	scroll(0, 0);

	/* Calculate the geometry that our content area should take */
	var header = $("#header:visible");
	//var footer = $("#footer:visible");
	var content = $("#content:visible");
	var viewport_height = $(window).height();
	var content_height = viewport_height - header.outerHeight();// - footer.outerHeight();

	/* Trim margin/border/padding height */
	content_height -= (content.outerHeight() - content.height());
	content.height(content_height);
});

