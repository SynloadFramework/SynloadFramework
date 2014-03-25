var loadOut = 0;
var page = {
	get: function(method,page,out){
		if(!out){
			if(loadOut){
				loadOut();
				loadOut = 0;
			}
		}
		var s = {
			"request":method,
			"page":page,
			"class":"Request"
		};
		ws.send(s);
	},
	toggleMenu: function(){
		$(".navibar").fadeToggle(200);
	},
	hideMenu: function(){
		$(".navibar").fadeOut(200);
	},
	showMenu: function(){
		$(".navibar").fadeIn(200);
	}
}
