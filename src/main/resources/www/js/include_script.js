var totalToLoad={{jscount}};
var loaded=0;
{{javascripts}}
	$.getScript('{{script}}', function(){
		{{callEvents}}
			_sf.addCallback({{func}},'{{event}}');
		{{/callEvents}}
		loaded++;
		if(loaded==totalToLoad){
			_sf.callBack('init');
			//system.alert("Loaded script files",{header:"Javascript loader"});
		}
	});
{{/javascripts}}