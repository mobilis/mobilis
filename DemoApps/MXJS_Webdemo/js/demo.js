$(document).ready(function () {
	
	$('#login_dialog').dialog({
        autoOpen: true,
        draggable: false,
        modal: true,
        title: 'Connect to Mobilis',
        buttons: {
            "Connect": function () {
                $(document).trigger('connect', {
                    jid: $('#jid').val(),
                    password: $('#password').val()
                });
                
                $('#password').val('');
                $(this).dialog('close');
            }
        }
    });	
	
	$( "#disconnect" ).button({ disabled: true });
	$( "#go_online" ).button({ disabled: true });
	$( "#go_offline" ).button({ disabled: true });
	$( "#subscribe" ).button({ disabled: false });
	$( "#unsubscribe" ).button({ disabled: false });
	$( "#serviceDisco" ).button({ disabled: false });
	$( "#publish_mood" ).button({ disabled: false });
	$( "#tabs" ).tabs();
	$( "#selectable" ).selectable();
	
	$('#disconnect').click(function () {
	    Mobilis.core.disconnect();    	
	});
	
	$('#go_online').click(function() {
	    Mobilis.core.sendPresence();
	});
	
	$('#go_offline').click(function() {
        Mobilis.core.sendPresence({
			type:'unavailable'
		})
	});
	
	$('#subscribe').click(function() {
        Mobilis.context.subscribe(
        	$("#sub_node").val(), 
        	$("#sub_jid").val()
        );
	});
	
	$('#unsubscribe').click(function() {
        Mobilis.context.unsubscribe(
        	$("#sub_node").val(), 
        	$("#sub_jid").val()
        );
	});
	
	$('#publish').click(function() { 
        Mobilis.context.publish(
        	$("#pub_path").val(), 
        	$("#pub_key").val(), 
        	$("#pub_value").val()
        );
	});
	
	$('#serviceDisco').click(function() {  
		
		var discoiq = $iq({
            to: Mobilis.core.SERVICES[Mobilis.core.NS.COORDINATOR].jid,
            type: "get"
        })
        .c("serviceDiscovery", {
            xmlns: Mobilis.core.NS.COORDINATOR
        });
        Mobilis.connection.sendIQ(discoiq,
        function(iq) {
            $(iq).find("mobilisService").each(function() {
            	
            	$('#mobilisService-list').append("<li>" +
                        $(this).attr('namespace') +
                        " / " +
                        $(this).attr('version') +
                        " / " +
                        $(this).attr('mode') +
                        " / " +
                        $(this).attr('instances') +
                        " / " +
                        $(this).attr('jid') +
                        "</li>");
                
            });
        });
		
	});
		
	$("form").submit(function () { return false; }); 	
});

$(document).bind('connect', function (ev, data) {
     Mobilis.core.connect(data.jid, data.password, function(status){
         if (status == Mobilis.core.Status.CONNECTING){
             $("#status").html("<img id='status_img' src='img/user-clear-16x16.gif'/>connecting...");
         } else if (status == Mobilis.core.Status.CONNFAIL){
				$("#status").html("<img id='status_img' src='img/user-clear-16x16.gif'/>failed");
         } else if (status == Mobilis.core.Status.DISCONNECTING){
             $("#status").html("<img id='status_img' src='img/user-clear-16x16.gif'/>disconnection...");
         } else if (status == Mobilis.core.Status.DISCONNECTED){
             $("#status").html("<img id='status_img' src='img/user-clear-16x16.gif'/>disconnected");
             $("#go_offline").button("option", "disabled", true);
             $("#go_online").button("option", "disabled", true);
             $("#disconnect").button("option", "disabled", true);
             $("#connect").button("option", "disabled", false);
             $("#roster").html("<ul></ul>");
             $("#login_dialog").dialog('open');
         } else if (status == Mobilis.core.Status.CONNECTED){
             $("#status").html("<img id='status_img' src='img/user-green-16x16.gif'/>connected");
             $("#go_offline").button("option", "disabled", false);
             $("#go_online").button("option", "disabled", true);
             $("#disconnect").button("option", "disabled", false);
             $("#connect").button("option", "disabled", true);
         } else if (status == Mobilis.core.Status.AUTHFAIL) {
        	 $("#status").html("<img id='status_img' src='img/user-clear-16x16.gif'/>not authorized");
         }
     });
});