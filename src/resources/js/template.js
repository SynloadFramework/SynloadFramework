var template = {
	storedTemplates: new Array(),
	onPage: "",
	onPage_request: "",
	loading: false,
	interval: new Array(),
	cache: new Array(),
	cacheTemplate: new Array(),
	showLoad: function(){
		loading = true;
		$("#ldbars").fadeIn(200);
	},
	hideLoad: function(){
		loading = false;
		$("#ldbars").fadeOut(200);
	},
	ws_lost: function(){
		template.onPage="";
		template.onPage_request="";
	},
	addInterval: function(interval,pageId){
		if(!template.interval[pageId]){
			template.interval[pageId] = new Array();
		}
		template.interval[pageId].push(interval);
	},
	killInterval: function(pageId){
		if(template.interval[pageId]){
			for(var i = 0; i < template.interval[pageId].length; i++){
				clearInterval(template.interval[pageId][i]);
			}
		}
	},
	msg: function(socket,msg){
		if(msg.pageTitle!="" && msg.pageTitle!=null){
			document.title = msg.pageTitle;
		}
		if(msg.templateId!=null && msg.templateId!=""){
			//console.log("Template recieved, "+msg.class);
			if(msg.pageId!=template.onPage){
				template.killInterval(template.onPage);
			}
			if($(msg.parent).length){
				console.log("Parent found, "+msg.parent);
				template.compile(msg);
			}else{
				console.log("Parent does not exist fetching, "+msg.parent);
				if(msg.forceParent){
					template.requestParent(socket,msg.parentTemplate);
					template.storedTemplates.push(msg);
				}
			}
		}
	},
	reload: function(){
		if(window.location.hash.split("/").length==3){
			if(window.atob(window.location.hash.split("/")[2])==template.onPage_request){
				ws.send(jQuery.parseJSON(window.atob(window.location.hash.split("/")[2])));
			}
		}
	},
	requestParent: function(sock,parentPage){
		var data = {
			"request":"get",
			"page":parentPage,
			"class":"Request"
		}
		ws.send(data);
	},
	build: function(){
		for(var i=0;i<template.storedTemplates.length;i++){
			if($(template.storedTemplates[i].parent).length){
				template.compile(template.storedTemplates[i]);
				template.storedTemplates.splice(i,1);
			}
		}
	},
	compile: function(tmpldata){
		if(template.cache.indexOf(tmpldata.templateId) > -1){
			template.inject(
				Mark.up(
					template.cacheTemplate[tmpldata.templateId],
					tmpldata
				),
				tmpldata.parent,
				tmpldata.action,
				tmpldata
			);
		}else{
			template.cacheTemplate[tmpldata.templateId] = tmpldata.template;
			template.cache.push(tmpldata.templateId);
			template.inject(
				Mark.up(
					tmpldata.template,
					tmpldata
				),
				tmpldata.parent,
				tmpldata.action,
				tmpldata
			);
		}
		if(tmpldata.pageId!="" && tmpldata.pageId!="null" && tmpldata.pageId!=undefined){
			template.onPage_request = JSON.stringify(tmpldata.request);
			window.location.hash = "/"+tmpldata.pageId+"/"+window.btoa(JSON.stringify(tmpldata.request));
			template.onPage = tmpldata.pageId;
		}
	},
	inject: function(html,parent,method,tmpldata){
		if($(parent).html()===html){
			return;
		}
		switch(method){
			case "alone":
				//$(parent).effect(tmpldata.transitionOut,300,function(){
					$(parent).addClass('animated fadeOutLeft');
					setTimeout(function(){
						$(parent).removeClass('animated fadeOutLeft');
						$(parent).html(html);
						$(parent).addClass('animated fadeInLeft');
						setTimeout(function(){
							$(parent).removeClass('animated fadeInLeft');
							//$(parent).show(tmpldata.transitionIn,200,function(){
								template.build();
								if(tmpldata.pageId!="" && tmpldata.pageId!="null" && tmpldata.pageId!=undefined){
									$.scrollTo(  { top:0, left:0}, 250 );
									template.hideLoad();
								}
								javascript.exec(tmpldata);
							//});
						},200);
					},200);
				//});
			break;
			case "wait":
				$(parent).effect(tmpldata.transitionOut,300,function(){
					$(parent).html(html);
					$(parent).show(tmpldata.transitionIn,200,function(){
						template.build();
						javascript.exec(tmpldata);
					});
					if(tmpldata.redirect){
						setTimeout(function(){
							var s = {
								"request":tmpldata.redirect.request,
								"page":tmpldata.redirect.page,
								"class":"Request"
							};
							ws.send(s);
						},tmpldata.sleep);
					}else if(tmpldata.callEvent){
						setTimeout(function(){
							ws.callBack(tmpldata.callEvent);
						},tmpldata.sleep);
					}	
				});
			break;
			case "abot":
				$(parent).append(html);
				template.build();
			break;
			case "atop":
				$(parent).prepend(html);
				template.build();
			break;
		}
	}
}
window.onhashchange = function(){
	if(window.location.hash.split("/").length==3){
		if(window.atob(window.location.hash.split("/")[2])!=template.onPage_request){
			ws.send(jQuery.parseJSON(window.atob(window.location.hash.split("/")[2])));
		}
	}
}