$(document).ready(function(){
	ws.connect();
});
Mark.pipes.htmlescape = function (string) {
    return string.replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/\"/g, '&quot;')
      .replace(/\'/g, '&#39;');
};
Mark.pipes.nl2br = function (str, n) {
    return str.replace(/\n/g, '<br />');
};
/*$(window).on('hashchange', function() {
	var f = jQuery.parseJSON(window.atob(location.href.split("#")[1]));
	var data = {
		"request":f.request,
		"page":f.page,
		"class":"Request"
	}
	if(page.on!=data){
		ws.socket.send(JSON.stringify(data));
	}
});*/
ws.addCallback(template.msg,"recieve");
ws.addCallback(javascript.msg,"recieve");
ws.addCallback(template.ws_lost,"close");
//ws.addCallback(user.dashboard,"dashboard");
var system = {
	alert: function(text,extra){
		$.jGrowl(text,extra);
	},
	loadDefault: function(){
		var rSent = false;
		$.each( system.defaults , function(key,val){
			if((_.contains(user.flags,val.flag) || val.flag == "") && !rSent){
				if(val.resume){
					if(template.onPage==""){
						if(window.location.hash.split("/").length==3){
							ws.send(jQuery.parseJSON(window.atob(window.location.hash.split("/")[2])));
							rSent = true;
						}
					}
				}else{
					ws.send(val.request);
					rSent = true;
				}
			}
		});
	},
	cache: true,
	defaults: [
		{
			"flag":"r",
			"resume":true
		},
		{
			"flag":"r",
			"request": {
				"request":"get",
				"page":"shows",
				"class":"Request",
				"data": {
					"page":"0"
				}
			}
		},
		{
			"flag":"",
			"request": {
				"request":"get",
				"page":"login",
				"class":"Request"
			}
		}
	]
}
$.jGrowl.defaults.animateOpen = {
	height: 'show'
};