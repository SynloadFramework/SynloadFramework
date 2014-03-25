var javascriptLoaded = false;
var javascript = {
	exec: function(msg){
		if(msg.javascript.length>0){
			for(var i=0;i<msg.javascript.length;i++){
				eval(Mark.up(
					msg.javascript[i],
					msg
				));
			}
		}
	},
	msg: function(s,msg){
		if(msg.javascripts){
			if(!javascriptLoaded){
				if(msg.javascripts.length>0){
					var js = Mark.up(
						msg.js_template,
						msg
					);
					eval(js);
					javascriptLoaded = true;
				}
			}else{
				ws.callBack('init');
			}
		}
	}
}