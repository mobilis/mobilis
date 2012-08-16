var xhunt = {
	XMPP: 'mobilis.inf.tu-dresden.de',
	USER: 'webxhunttest',
	PASSWORD: 'mobilis',
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
	log: function (msg) {
		console.log(msg);
		//$('#logwindow').append('<div class="message" ></div>').append(document.createTextNode(msg))
	},
	addHandlers: function () {
		Mobilis.xhunt.addLocationHandler(xhunt.on_locationIQ);
		//Mobilis.connection.addHandler(xhunt.on_locationIQ, 'mobilisxhunt:iq:location', 'iq', 'set');
		
		Mobilis.xhunt.addPlayersHandler(xhunt.on_playersIQ);
		//Mobilis.connection.addHandler(xhunt.on_playersIQ, 'mobilisxhunt:iq:players', 'iq', 'set');
		
		Mobilis.xhunt.addStartRoundHandler(xhunt.on_startRoundIQ);
		//Mobilis.connection.addHandler(xhunt.on_startRoundIQ, 'mobilisxhunt:iq:startround', 'iq', 'set');
		
		Mobilis.xhunt.addRoundStatusHandler(xhunt.on_roundStatusIQ);
		//Mobilis.connection.addHandler(xhunt.on_roundStatusIQ, 'mobilisxhunt:iq:roundstatus', 'iq', 'set');
		
		Mobilis.xhunt.addJoinGameHandler(xhunt.on_joinGameIQ);
		//Mobilis.connection.addHandler(xhunt.on_joinGameIQ, 'mobilisxhunt:iq:joingame', 'iq', 'result');
		
		Mobilis.connection.addHandler(xhunt.on_updatePlayerIQ, 'mobilisxhunt:iq:updateplayer', 'iq', 'result');
		Mobilis.connection.addHandler(xhunt.on_gameOverIQ, 'mobilisxhunt:iq:gameover', 'iq', 'set');
	},
};


$(document).on('connect',
	function(ev, data) {
		// if (Mobilis.core.Status.CONNECTED){
		// 	Mobilis.core.disconnect('reconnect');
		// };
		Mobilis.core.connect(
		'webxhunttest',
		'mobilis',
		function(status) {
			
			if (status == Mobilis.core.Status.CONNECTED) {

				$(document).trigger('queryGames', {});
				
			} 
		}
		);
	}
);


$(document).on('queryGames',
	function(ev, data) {

		$("#game-list > li").each(function(n,item){
			jQuery(item).remove(); //remove old game list items
		});

		Mobilis.core.mobilisServiceDiscovery(
			[Mobilis.core.NS.XHUNT],
			function (iq) {

				var gameList = $('#game-list');

				if ($(iq).find("mobilisService").length){
					$(iq).find("mobilisService").each(function() {
						Mobilis.core.SERVICES[$(this).attr('namespace')] =
						{
							'version': $(this).attr('version'),
							'jid': $(this).attr('jid'),
							'servicename' : $(this).attr('serviceName')
						};
						gameList.append('<li><a class="available-game" id="' + $(this).attr('jid') + '" href="game.html" data-transition="slide">' + $(this).attr('serviceName') + '</a></li>');
					}); 
				} else {
					gameList.append('<li>No games found</li>');
				}

				gameList.listview('refresh'); // jQuery Mobile Style übernehmen

			}
		);
	}
);



$(document).on('joinGame',
	function(ev, data) {
		console.log('joinGame');
		xhunt.addHandlers();
		console.log(localStorage.getItem('mobilis.xhunt.jid'));
		data.jid = (!data.jid) ? localStorage.getItem('mobilis.xhunt.jid') : data.jid;
		Mobilis.xhunt.joinGame(
			data.jid, //gameJID
			'WebClient', //playerName
			function (iq){  // resultcallback

				$('#game-name').append(': ' + xhunt.gameinfo.name);
				//$(document).trigger('initMap', {xml: 'data/sites.xml'});
				//console.log('addHandlers');
				
				//xhunt.addHandlers();
				
			},
			function (iq){ // errorcallback
				//console.log('ERROR: '+iq)
				// $.jGrowl("ERROR");
			}
		);
	}
);



$(document).on('pageinit', '#game-list-page', function() {

	$(document).trigger('connect');

	$('#game-list').on('click', 'a', function () {

		localStorage.setItem('mobilis.xhunt.jid', $(this).attr('id'));

		$(document).trigger('joinGame', {
			jid: $(this).attr('id')
		});
		
	});

	$('#refresh-button').on('click', function () {

		if (status != Mobilis.core.Status.CONNECTED) {
				$(document).trigger('connect');
			} 

		$(document).trigger('queryGames');

	});

});



$(document).on('pageinit', '#index-page', function() {

	// $('#play-xhunt').on('click', function () {
	// 	$(document).trigger('connect');
	// });

});


$(document).on('initMap',
	function(ev, data) {
		$.ajax({
			type: "GET",
			url: data.xml,
			dataType: "xml",
			success: function(xml) {
				$(xml).find('Station').each(function() {
					xhunt.stations[$(this).attr('id')] =
					{
						'abbrev': $(this).attr('abbrev'),
						'name': $(this).attr('name'),
						'lat': $(this).attr('latitude'),
						'lon' : $(this).attr('longitude')
					};
					$('#map_canvas').gmap3(
					{
						action: 'addMarker',
						latLng: [$(this).attr('latitude'), $(this).attr('longitude')],
						marker: {
							options: {
								icon: xhunt.hlst,
								title: $(this).attr('name')
							}
						},
					},
					"autofit"
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

					$('#map_canvas').gmap3(
					{
						action: 'addPolyline',
						options: {
							strokeColor: xhunt.colors[colornummer],
							strokeOpacity: 1.0,
							strokeWeight: 2
						},
						path: stops
					}
					);
				});
			}
		});
		

	}
);
