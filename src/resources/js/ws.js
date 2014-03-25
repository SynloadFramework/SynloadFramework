var ws = {
	socket: null,
	onCallbacks: new Array(),
	send: function(e){
		e.templateCache = template.cache;
		ws.socket.send(JSON.stringify(e));
	},
	connect: function(){
		if ((typeof(WebSocket) == 'undefined') && (typeof(MozWebSocket) != 'undefined')) {
			WebSocket = MozWebSocket;
		}
		ws.socket =  new WebSocket('ws://animecap.com/ws/');
		ws.socket.onopen = function() {
			//system.alert("Connected to server!",{ header: 'Socket Connection' });
			ws.callBack('connect');
		};
		ws.socket.onclose = function() {
			//system.alert("connection lost to server!",{ header: 'Socket Connection' });
			ws.callBack('close');
			setTimeout(function(){
				ws.connect();
			},1000);
		};
		ws.socket.onmessage = function( msg ) {
			var data = jQuery.parseJSON(msg.data);
			if(data.callEvent){
				ws.callBack(data.callEvent,data);
			}else{
				ws.callBack('recieve', data);
			}
		};
	},
	addCallback: function( func, callbackName ){
		if(!ws.onCallbacks[callbackName]){
			ws.onCallbacks[callbackName] = new Array();
		}
		ws.onCallbacks[callbackName].push( func );
	},
	callBack: function (callbackName,data){
		if(ws.onCallbacks[callbackName]){
			$.each(ws.onCallbacks[callbackName],function(key,func){
				try{
					if(data){
						func(ws.socket, data);
					}else{
						func(ws.socket);
					}
				}catch(err){
					console.log(err);
				}
			});
		}
	}
}