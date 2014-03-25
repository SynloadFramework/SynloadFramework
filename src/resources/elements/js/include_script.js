var totalToLoad={{jscount}};
var loaded=0;
{{javascripts}}
	$.getScript('{{script}}', function(){
		{{callEvents}}
			ws.addCallback({{func}},'{{event}}');
		{{/callEvents}}
		loaded++;
		if(loaded==totalToLoad){
			ws.callBack('init');
			//system.alert("Loaded script files",{header:"Javascript loader"});
		}
	});
{{/javascripts}}