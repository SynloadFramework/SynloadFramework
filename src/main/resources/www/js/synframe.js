
var js = new Array();
var clientRSA;
var clientKey;
var serverKey;
var key;
function loadedEncrypt(){
    crypt = new JSEncrypt({default_key_size: 1024});
    key = crypt.getKey();
    clientRSA=key.getPrivateKey();
    //console.log(clientRSA);
    clientKey=key.getPublicKey();
    //console.log(clientKey);
}
var loadedJSEncrypt = false;
$.getScript("/synloadframework/js/JSEncrypt.js",function(){
    loadedJSEncrypt=true;
});

var _sf = {
	loading: false,
	encryptEnabled: false,
	interval: new Array(),
	ekey:"",
	cache: new Array(),
	cacheTemplate: new Array(),
	ecsnt: false,
	onUnload: null,
	socket: null,
	storedTemplates: new Array(),
	triggers: new Array(),
	onPage: "",
	onPage_request: "",
	wsAddress: "",
	onConnect: null,
	wsPath: "",
	javascriptLoaded: false,
	onCallbacks: new Array(),
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
	loadDefault: function(){
		var rSent = false;
		$.each( _sf.defaults , function(key,val){
			if(!rSent){
				if(val.resume){
					if(_sf.onPage==""){
						if(window.location.hash.split("/").length==3){
							_sf.send($.parseJSON(window.atob(window.location.hash.split("/")[2])));
							rSent = true;
						}
					}
				}else{
					_sf.send(val.request);
					rSent = true;
				}
			}
		});
	},
	alert: function(text,extra){
		$.jGrowl(text,extra);
	},
	request: function(method, page, data){
		var data = {
			"data": data,
			"request":method,
			"page":page,
			"class":"Request"
		}
		_sf.send(data);
	},
	send: function(e){
		e.templateCache = _sf.cache;
		if(_sf.encryptEnabled){
			_sf.socket.send(_sf.encrypt(JSON.stringify(e),serverKey));
		}else{
			_sf.socket.send(JSON.stringify(e));
		}
	},
	connected: function(){
	   $("#loadingBar .bar").animate({"width":(390*1.0)+"px"},function(){
    	   $("#loadingBar").fadeOut(100,function(){
    	       if(_sf.encryptEnabled){
    	           $("body").after('<img src="/synloadframework/images/technology.png" style="background:#fff;border-radius:4px;padding:4px;position:absolute;left:10px;top:55px;z-index:10000;" />');
    	       }
    	        $("#loadingBar").empty();
                $("#loadingBar").remove();
        		_sf.onConnect();
        		setInterval(function(){
        			var data = {
        				"request":"get",
        				"page":"ping",
        				"class":"Request"
        			}
        			_sf.send(data);
        		},10000);
    		});
		});
	},
	connect: function(address,path){
		_sf.wsAddress = address;
		_sf.wsPath = path;
		if ((typeof(WebSocket) == 'undefined') && (typeof(MozWebSocket) != 'undefined')) {
			WebSocket = MozWebSocket;
		}
		_sf.socket =  new WebSocket('ws://'+address+path);
		_sf.socket.onopen = function() {
			//_sf.alert("Connected to server!",{ header: 'Server Connection' });
			//_sf.loadDefault();
			//_sf.connected();
			_sf.addCallback(_sf.msg,"recieve");
		};
		_sf.socket.onclose = function() {
			//_sf.alert("connection lost to server!",{ header: 'Server Connection' });
			_sf.callBack('close');
			_sf.onPage="";
			_sf.onPage_request="";
			_sf.ecsnt = false;
			_sf.ekey = "";
			_sf.encryptEnabled = false;
			setTimeout(function(){
				_sf.connect(_sf.wsAddress,_sf.wsPath);
			},5000);
		};
		_sf.socket.onmessage = function( msg ) {
			if(_sf.encryptEnabled){
                //console.log(msg);
				var data = jQuery.parseJSON(jQuery.parseJSON(_sf.decrypt(msg.data)));
				//console.log(data);
			}else{
				var data = jQuery.parseJSON(msg.data);
			}
			if(data.callEvent!=null && data.callEvent!=""){
				_sf.callBack(data.callEvent,data);
			}else if(data.trigger){
				_sf.triggers[data.trigger](data);
			}else{
				_sf.callBack('recieve', data);
			}
		};
	},
	requestData: function(e, func){
		var key = Math.random().toString(36).substring(7);
		e.trigger = key;
		_sf.triggers[key] = func;
		_sf.send(e);
	},
	addCallback: function( func, callbackName ){
		if(!_sf.onCallbacks[callbackName]){
			_sf.onCallbacks[callbackName] = new Array();
		}
		_sf.onCallbacks[callbackName].push( func );
	},
	callBack: function (callbackName,data){
		if(_sf.onCallbacks[callbackName]){
			$.each(_sf.onCallbacks[callbackName],function(key,func){
				try{
					if(data){
						func(_sf.socket, data);
					}else{
						func(_sf.socket);
					}
				}catch(err){
					console.log(err);
				}
			});
		}
	},
	encrypt: function(data, key){
        var enc = new JSEncrypt();
        enc.setPublicKey(key);
        var partials = new Array();
        var myregexp = /((.|[\r\n]){1,50})/g;
        var match = myregexp.exec(data);
        while (match != null) {
            partials.push(match[1]);
            match = myregexp.exec(data);
        }
	   var renc = "";
	   for(var i=0;i<partials.length;i++){
	       if(renc==""){
	           renc = enc.encrypt(partials[i]);
           }else{
	           renc += "&"+enc.encrypt(partials[i]);
           }
	   }
	   return renc;
	},
	decrypt: function(data){
	    var partials = data.split(/&/);
	    var renc = "";
	    for(var i=0;i<partials.length;i++){
	       var unenc = atob(crypt.decrypt(partials[i]));
	       if(unenc==null){
	           console.log("decrypt error!");
	           break;
	       }else{
               if(renc==""){
                    renc = unenc;
               }else{
                    renc = renc+unenc;
               }
           }
        }
		return renc;
	},
	showLoad: function(){
		_sf.loading = true;
		//NProgress.start();
	},
	hideLoad: function(){
		_sf.loading = false;
		//NProgress.done();
	},
	inject: function(html,parent,method,tmpldata){
		if($(parent).html()===html){
			return;
		}
		switch(method){
			case "cabot":
				$(parent).addClass('animated fadeOutLeft');
				setTimeout(function(){
					$(parent).removeClass('animated fadeOutLeft');
					$(parent).html(html);
					$(parent).addClass('animated fadeInLeft');
					setTimeout(function(){
						$(parent).removeClass('animated fadeInLeft');
						_sf.build();
						if(tmpldata.pageId!="" && tmpldata.pageId!="null" && tmpldata.pageId!=undefined){
							//$.scrollTo(  { top:0, left:0}, 250 );
						}
						_sf.exec(tmpldata);
					},400);
				},200);
			break;
			case "alone":
				$(parent).addClass('animated fadeOutLeft');
				setTimeout(function(){
					$(parent).removeClass('animated fadeOutLeft');
					$(parent).html(html);
					$(parent).addClass('animated fadeInLeft');
					setTimeout(function(){
						$(parent).removeClass('animated fadeInLeft');
						_sf.build();
						if(tmpldata.pageId!="" && tmpldata.pageId!="null" && tmpldata.pageId!=undefined){
							//$.scrollTo(  { top:0, left:0}, 250 );
						}
						_sf.exec(tmpldata);
					},400);
				},200);
			break;
			case "wait":
				$(parent).effect(tmpldata.transitionOut,300,function(){
					$(parent).html(html);
					$(parent).show(tmpldata.transitionIn,200,function(){
						_sf.build();
						_sf.exec(tmpldata);
					});
					if(tmpldata.redirect){
						setTimeout(function(){
							var s = {
								"request":tmpldata.redirect.request,
								"page":tmpldata.redirect.page,
								"class":"Request"
							};
							_sf.send(s);
						},tmpldata.sleep);
					}else if(tmpldata.callEvent){
						setTimeout(function(){
							_sf.callBack(tmpldata.callEvent);
						},tmpldata.sleep);
					}	
				});
			break;
			case "abot":
				$(parent).append(html);
				_sf.build();
			break;
			case "cabot":
				$(parent).html("");
				$(parent).append(html);
				_sf.build();
			break;
			case "atop":
				$(parent).prepend(html);
				_sf.build();
			break;
			default:
				$(parent).html(html);
				_sf.build();
			break;
		}
	},
	templateRender: function(template,tmpldata){
		return Mark.up(template,tmpldata);
	},
	compile: function(tmpldata){
		if(_sf.cache.indexOf(tmpldata.templateId) > -1){
			_sf.inject(
				_sf.templateRender(_sf.cacheTemplate[tmpldata.templateId],tmpldata),
				tmpldata.parent,
				tmpldata.action,
				tmpldata
			);
		}else{
			_sf.cacheTemplate[tmpldata.templateId] = tmpldata.template;
			_sf.cache.push(tmpldata.templateId);
			_sf.inject(
				_sf.templateRender(tmpldata.template,tmpldata),
				tmpldata.parent,
				tmpldata.action,
				tmpldata
			);
		}
		if(tmpldata.pageId!="" && tmpldata.pageId!="null" && tmpldata.pageId!=undefined){
			_sf.onPage_request = JSON.stringify(tmpldata.request);
			_sf.hideLoad();
			window.location.hash = "/"+tmpldata.pageId+"/"+window.btoa(JSON.stringify(tmpldata.request));
			_sf.onPage = tmpldata.pageId;
		}
	},
	requestParent: function(sock,parentPage){
		var data = {
			"request":"get",
			"page":parentPage,
			"class":"Request"
		}
		_sf.send(data);
	},
	build: function(){
		for(var i=0;i<_sf.storedTemplates.length;i++){
			if($(_sf.storedTemplates[i].parent).length){
				_sf.compile(_sf.storedTemplates[i]);
				_sf.storedTemplates.splice(i,1);
			}
		}
	},
	addInterval: function(interval,pageId){
		if(!_sf.interval[pageId]){
			_sf.interval[pageId] = new Array();
		}
		_sf.interval[pageId].push(interval);
	},
	killInterval: function(pageId){
		if(_sf.interval[pageId]){
			for(var i = 0; i < _sf.interval[pageId].length; i++){
				clearInterval(_sf.interval[pageId][i]);
			}
		}
	},
	msg: function(socket,msg){
		if(msg.pageTitle!="" && msg.pageTitle!=null){
			document.title = msg.pageTitle;
		}
		if(msg.templateId!=null && msg.templateId!=""){
			if(msg.pageId!=null && msg.pageId!=""){
				if(_sf.onUnload!=null){
					_sf.onUnload();
					_sf.onUnload = null;
				}
				_sf.killInterval(_sf.onPage);
			}
			if($(msg.parent).length){
				_sf.compile(msg);
			}else{
				if(msg.forceParent){
					_sf.requestParent(socket,msg.parentTemplate);
					_sf.storedTemplates.push(msg);
				}
			}
		}
		if(msg.javascripts){
			if(!_sf.javascriptLoaded){
				if(msg.javascripts.length>0){
					var js = _sf.templateRender(
						msg.js_template,
						msg
					);
					eval(js);
					_sf.javascriptLoaded=true;
					//_sf.connected();
				}
			}else{
				_sf.callBack('init');
			}
		}
	},
	reload: function(){
		if(window.location.hash.split("/").length==3){
			if(window.atob(window.location.hash.split("/")[2])==_sf.onPage_request){
				_sf.send($.parseJSON(window.atob(window.location.hash.split("/")[2])));
			}
		}
	},
	defaults: [
		{
			"resume":true
		},
		{
			"request": {
				"data":{
					"element": "body"
				},
				"request": "get",
				"page": "index",
				"class": "Request"
			}
		}
	],
}
function sendEncryptHandshake(){
    if(loadedJSEncrypt){
        loadedEncrypt();
        var eKey = _sf.encrypt(
            clientKey.replace(/-----BEGIN PUBLIC KEY-----/g, "").replace(/-----END PUBLIC KEY-----/g, ""), 
            serverKey
        );
        var s = {
            "data":{
                "cpk":eKey
            },
            "request":"synfam",
            "page":"cpk",
            "class":"Request"
        };
        _sf.send(s);
        _sf.encryptEnabled=true;
    }else{
        setTimeout(function(){
            sendEncryptHandshake();
        },400);
    }
}
_sf.addCallback(function(ws, data){

    $("body").append('<div id="loadingBar" style="border-radius:5px;-webkit-box-shadow: 0px 0px 19px -4px rgba(0,0,0,0.74);-moz-box-shadow: 0px 0px 19px -4px rgba(0,0,0,0.74);box-shadow: 0px 0px 19px -4px rgba(0,0,0,0.74);float:left;position:absolute;"><span style="width:400px;text-align:center;float:left;display:block;font-weight:bold;">ENCRYPTING CONNECTION</span><span style="display:block;width:390px;float:left;height:20px;padding:5px;margin-top:10px;margin-bottom:10px;background:#ccc;border-radius:5px;"><span class="bar" style="background:#50C441;border-radius:5px;float:left;width:0px;height:20px;"></span></span></div>');
    $("#loadingBar").css({"background":"#E6F7FC","padding":"20px","top":"40%","left":"50%","marginLeft":"-220px","width":"400px","textAlign":"center"});
    // test data
    $("#loadingBar .bar").animate({"width":(390*.10)+"px"},100);
    serverKey = data.data.spk;
    //console.log(serverKey);
    sendEncryptHandshake();
    
    $("#loadingBar .bar").animate({"width":(390*.40)+"px"},100);
    
}, "encryption_handshake");

_sf.addCallback(function(ws, data){
    //console.log(data);
    var d = data.data.spk;
    serverKey = d;
    var s = {
        "data":{
            "message":"HELLO"
        },
        "request":"synfam",
        "page":"ack",
        "class":"Request"
    };
    _sf.send(s);
    $("#loadingBar .bar").animate({"width":(390*.75)+"px"},100);
}, "encryption_handshake_two");
_sf.addCallback(function(ws, data){
    _sf.loadDefault();
    _sf.connected();
}, "conn_est");


window.onhashchange = function(){
	if(window.location.hash.split("/").length==3){
		if(window.atob(window.location.hash.split("/")[2])!=_sf.onPage_request){
			_sf.showLoad();
			_sf.send($.parseJSON(window.atob(window.location.hash.split("/")[2])));
		}
	}
}
function connect(domain,func){
	$("._sf_connect_hideme").hide();
    _sf.connect(domain,"/ws/");
    _sf.onConnect = func;
}
