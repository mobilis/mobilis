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
	}

}