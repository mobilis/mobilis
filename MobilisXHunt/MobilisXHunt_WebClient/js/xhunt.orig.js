/// <reference path="../../MXJS/js/mobilis/mobilis.sessionmobility.js" />

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
        
    on_joinGameIQ: function (iq) {
        var room = $(iq).find('ChatRoom').text();
        var pwd = $(iq).find('ChatPassword').text();
        xhunt.gameinfo['room'] = room.toLowerCase();
        xhunt.gameinfo['pwd'] = pwd;
        xhunt.gameinfo['nick'] = 'Spectator';
        
        if (xhunt.gameinfo['joined'] === 'false'){
            xhunt.gameinfo['joined'] = 'true';
            Mobilis.connection.muc.join(
                xhunt.gameinfo['room'], 
                xhunt.gameinfo['nick'],
                function(message) {
                    //console.log('call message handler');
                    var from = Strophe.getResourceFromJid($(message).attr('from'));
                    if (from !== null){
                        var message_html = '<div class="message"><strong>' +
                            from + ': </strong>' +
                            $(message).text() +
                            '</div>';

                        $('#chatdisplay').append (message_html);
                        var objDiv = document.getElementById("chatdisplay");
                        objDiv.scrollTop = objDiv.scrollHeight;
                    }
                    return true;
                },
                function (pres){
                   //.log(pres); 
                },
                xhunt.gameinfo['pwd']);            
        }

        return true;
    },
 
    on_gameOverIQ: function (iq) {
        $.jGrowl('Game Over');
        $.jGrowl($(iq).find('reason').text(), {sticky: true });   
    },
    
    on_updatePlayerIQ: function (iq) {
        // $.jGrowl($(iq).find('info').text()); 
        return true;  
    },
    
    on_usedTicketsIQ: function (iq) {
        $(iq).find('player').each(function(){
            var jid = Strophe.getNodeFromJid($(this).find('playername').text());
            var ticket = $(this).find('ticketid').text();
            $('#' + jid + '_' + ticket).append('+');
        });
        //console.log('usedTickets');
        //console.log(iq);   
        return true;
    },
    
    on_startRoundIQ: function (iq) {
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

    on_roundStatusIQ: function (iq) {
        
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
                $('#map_canvas').gmap3({
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
                $('#map_canvas').gmap3(
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
    
    on_playersIQ: function (iq) {
        $('#players').html('');
        $(iq).find('player').each(function(){ 
            var flag_ready = '';
            var jid = Strophe.getNodeFromJid($(this).find('jid').text());
            xhunt.createPlayer(jid);
            var player = xhunt.players[jid]
            player.name = $(this).find('playername').text();
            player.ismoderator = $(this).find('ismoderator').text();
            player.ismrx = $(this).find('ismrx').text();
            var ready= $(this).find('isready').text();
            if (ready === 'true')
                flag_ready = '  &#10003;';
            $('#players').append('<div id="' + jid + '" class="playerinfobox"></div>')
            $('#' + jid ).append('<div class="playername"><img class="playericon" src="' + 
            player.icon.url +' "/>' + 
            player.name + flag_ready + '</div>' +
            '</div><span class="lat">' + 
            '</span><span class="lon">' + 
            '</span><div class="playertarget">' + 
            '</div><div class="playertickets" style="font-size:small;">' + 
            '<div id="'+ jid +'_4" class="tickets"></div>' +
            '<div id="'+ jid +'_1" class="tickets"></div>' +
            '<div id="'+ jid +'_2" class="tickets"></div>' +
            '<div id="'+ jid +'_3" class="tickets"></div>' +
            '</div>');       
        });
        return true;   
    },
    
    on_locationIQ: function (iq) {
        $(iq).find('location').each(function(){
            var jid = Strophe.getNodeFromJid($(this).find('jid').text());
            var lat = xhunt.convert($(this).find('lat').text());
            var lon = xhunt.convert($(this).find('lon').text());
            xhunt.players[jid].lat = lat;
            xhunt.players[jid].lon = lon;
                        
            $('#map_canvas').gmap3({
              action:'clear',
              name:'marker',
              tag: jid, 
            });
            $('#map_canvas').gmap3(
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
    
    createPlayer : function (jid) {
        if (!xhunt.players[jid]) {
            xhunt.players[jid] ={
                'name': '',
                'lat': '',
                'lon': '',
                'tlat' : '',
                'tlon' : '',
                'ismrx':'',
                'ismoderator' : '',
                'icon':'',
                'round': '0'
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
    
    on_invitation: function (iq) {
    	var gameJid = $(iq).find('param').first().text();
    	var gameName = $(iq).find('param').next().text();
    	xhunt.gameinfo['gameJID'] = gameJid;
    	xhunt.gameinfo['name'] = gameName;
    	$(document).trigger('joinGame', {
            jid: gameJid
         });
    	$('#login_dialog').dialog('close');
        return true;   
    },
};

$(document).ready(function() {
    $('#opengames').selectable();
    $('#nogameavailable').hide();
    
    $('#rezoom').click(function() {
    });

    $('#gameDetails').click(function() {
        Mobilis.xhunt.gameDetails(xhunt.gameinfo['gameJID'],
        function (iq){
            console.log(iq);
        });
    });

    $('#joinGameBtn').click(function() {
        $('#login_dialog').dialog('open');
    });
    
    $('#sendChat').click(function() {
        if (xhunt.gameinfo['room'])
            Mobilis.connection.muc.message(xhunt.gameinfo['room'], xhunt.gameinfo['nick'], $('#chatarea').val(), 'groupchat');
        $('#chatarea').val('');
    });
    
    $('#exitGame').click(function() {
        if (Mobilis.connection.connected)
            if (Mobilis.xhunt.gameJID){
                Mobilis.xhunt.playerExit(
                    Mobilis.xhunt.gameJID, 
                    xhunt.USER + '@' + xhunt.XMPP + '/webClient',
                    function (){
                        $.jGrowl("Spectator has left the game"); 
                    }
                );
            $('#players').html('');  
            $('#roundinfo').html('');
            $('#chatdisplay').html(''); 
            $('#gameinformation').html('Game Information');
            $('#map_canvas').gmap3({
              action:'clear',
              name:'polyline'
            });  
            $('#map_canvas').gmap3({
              action:'clear',
              name:'marker'
            });
            }

    });
  
    $(document).trigger('connect', {});

    $('#login_dialog').dialog({
        autoOpen: true,
        draggable: false,
        modal: true,
        title: 'Select Game',
        buttons: {
            "Refresh": function(){
                $(document).trigger('openGames', {});     
            },
            "Select": function () {
                if ($("#opengames .ui-selected").length){
                    xhunt.gameinfo['gameJID'] = $('#opengames .ui-selected').attr('id');
                    xhunt.gameinfo['name'] = $('#opengames .ui-selected').text();
                    $(document).trigger('joinGame', {
                       jid: $('#opengames .ui-selected').attr('id')
                    });
                    $(this).dialog('close');
                } else {
                    $.jGrowl("Please select game");
                }
            }
            
        }
    });


});

$(document).bind('connect',
    function(ev, data) {
        Mobilis.core.connect(
        'webxhunttest',
        'mobilis',
        function(status) {
            if (status == Mobilis.core.Status.CONNECTED) {
            	// connected
                $.jGrowl("XMPP Connected");
                $(document).trigger('openGames', {});
                
                // add handler for session mobility
                Mobilis.sessionmobility.addInvitationHandler(xhunt.on_invitation);
                // display qr code with own JID
                var chartURL = "https://chart.googleapis.com/chart?chs=250x250&cht=qr&choe=UTF-8&chl=" + encodeURIComponent(Mobilis.connection.jid);
                $("#img_qrcode").attr("src", chartURL);
            }
        }
        );
    }
);

$(document).bind('joinGame',
    function(ev, data) {
		xhunt.addHandlers();
        Mobilis.xhunt.joinGame(
            data.jid,
            'WebSpectator',
            function (iq){
                // ToDO Game Name
                $.jGrowl('Game joined');
                $('#gameinformation').append(': ' + xhunt.gameinfo.name);
                $(document).trigger('initMap', {xml: 'sites.xml'});
                //console.log('addHandlers');
                
                //xhunt.addHandlers();
                
            },
            function (iq){
                $.jGrowl("ERROR");
            }
        );
    }
);

$(document).bind('openGames',
    function(ev, data) {
        $('#nogameavailable').hide(); 
        $("#opengames > li").each(function(n,item){
               //if (item.id==id) {
                   //jQuery(item).effect("fade", {}, 500, function(){
                   //    jQuery(item).remove();
                   //});
                   //jQuery(item).hide(500, function(){
                       jQuery(item).remove();
                   //});
               //}
           });
        Mobilis.core.mobilisServiceDiscovery(
            [Mobilis.core.NS.XHUNT],
            function (iq) {
                $('#lookingforopengame').hide();
                //console.log(iq);
                if ($(iq).find("mobilisService").length){
                    $(iq).find("mobilisService").each(function() {
                        Mobilis.core.SERVICES[$(this).attr('namespace')] =
                        {
                            'version': $(this).attr('version'),
                            'jid': $(this).attr('jid'),
                            'servicename' : $(this).attr('serviceName')
                        };
                        $('#opengames').append('<li id="' + $(this).attr('jid') + '" class="ui-widget-content">' + $(this).attr('serviceName') + '</li>');
                        //$( "#selectable" ).selectable();
                    }); 
                } else {
                     $('#nogameavailable').show();   
                }
            }
        );
    }
);

$(document).bind('initMap',
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



