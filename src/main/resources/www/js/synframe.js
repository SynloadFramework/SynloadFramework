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

class SynloadFramework{
	constructor(){
		this.loading = false;
		this.encryptEnabled = false;
		this.interval = new Array();
		this.ekey = "";
		this.cache = new Array();
		this.ecsnt = false;
		this.onUnload = null;
		this.socket = null;
		this.storedTemplates = new Array();
		this.triggers = new Array();
		this.onPage = "";
		this.onPage_request = "";
		this.wsAddress = "";
		this.onConnect = null;
		this.wsPath = "";
		this.javascriptLoaded = false;
		this.onCallbacks = new Array();
		this.defaults = [
            {
                "resume":true
            },
            {
                "request": {
                    "data":{
                        "element": "body"
                    },
                    "method": "get",
                    "action": "index",
                    "class": "Request"
                }
            }
        ];
	}
	exec(msg){
		if(msg.javascript.length>0){
			for(var i=0;i<msg.javascript.length;i++){
				eval(Mark.up(
					msg.javascript[i],
					msg
				));
			}
		}
	}
	/*loadDefault(){
		var rSent = false;
		var sf = this;
		$.each( this.defaults , function(key,val){
			if(!rSent){
				if(val.resume){
					if(sf.onPage==""){
						if(window.location.hash.split("/").length==3){
							sf.send($.parseJSON(window.atob(window.location.hash.split("/")[2])));
							rSent = true;
						}
					}
				}else{
					sf.send(val.request);
					rSent = true;
				}
			}
		});
	}*/
	alert(text,extra){
		$.jGrowl(text,extra);
	}
	request(method, action, data){
		var data = {
			"data": data,
			"method":method,
			"action":action,
			"class":"Request"
		}
		this.send(data);
	}
	send(e){
		e.templateCache = this.cache;
		if(this.encryptEnabled){
			this.socket.send(this.encrypt(JSON.stringify(e),serverKey));
		}else{
			this.socket.send(JSON.stringify(e));
		}
	}
	connected(){
	    var sf = this;
	   $("#loadingBar .bar").animate({"width":(390*1.0)+"px"},function(){
    	   $("#loadingBar").fadeOut(100,function(){
    	       if(sf.encryptEnabled){
    	           $("body").after('<img src="/synloadframework/images/technology.png" style="background:#fff;border-radius:4px;padding:4px;position:absolute;left:10px;top:55px;z-index:10000;" />');
    	       }
    	        $("#loadingBar").empty();
                $("#loadingBar").remove();
        		sf.onConnect();
        		setInterval(function(){
        			var data = {
        				"method":"get",
        				"action":"ping",
        				"class":"Request"
        			}
        			sf.send(data);
        		},10000);
    		});
		});
	}
	connect(address,path){ // WebSocket Object
		this.wsAddress = address;
		this.wsPath = path;
		if ((typeof(WebSocket) == 'undefined') && (typeof(MozWebSocket) != 'undefined')) {
			WebSocket = MozWebSocket;
		}
		this.socket =  new WebSocket('ws://'+address+path);
		var sf = this;
		this.addCall(this.messageReceived,"receive");
		this.socket.onopen = function() {

		};
		this.socket.onclose = function() {
			this.call('close');
			this.onPage="";
			this.onPage_request="";
			this.ecsnt = false;
			this.ekey = "";
			this.encryptEnabled = false;
			setTimeout(function(){
				sf.connect(sf.wsAddress,sf.wsPath);
			},5000);
		};
		this.socket.onmessage = function( msg ) {
			if(sf.encryptEnabled){
				var data = jQuery.parseJSON(jQuery.parseJSON(sf.decrypt(msg.data)));
			}else{
				var data = jQuery.parseJSON(msg.data);
			}
			if(data.callEvent!=null && data.callEvent!=""){
				sf.call(data.callEvent,data);
			}else if(data.trigger){
				sf.triggers[data.trigger](data);
			}else{
				sf.call('receive', data);
			}
		};
	}
	requestData(e, func){
		var key = Math.random().toString(36).substring(7);
		e.trigger = key;
		this.triggers[key] = func;
		this.send(e);
	}
	addCall( func, callbackName ){
		if(!this.onCallbacks[callbackName]){
			this.onCallbacks[callbackName] = new Array();
		}
		this.onCallbacks[callbackName].push( func );
	}
	call(callbackName,data){
		if(this.onCallbacks[callbackName]){
		    var sf = this;
			$.each(this.onCallbacks[callbackName],function(key,func){
				try{
					if(data){
						func(sf, sf.socket, data);
					}else{
						func(sf.socket);
					}
				}catch(err){
					console.log(err);
				}
			});
		}
	}
	encrypt(data, key){
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
	}
	decrypt(data){
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
	}
	inject(html,parent,method,tmpldata){
		if($(parent).html()===html){
			return;
		}
		var sf = this;
		switch(method){
			case "cabot":
				$(parent).addClass('animated fadeOutLeft');
				setTimeout(function(){
					$(parent).removeClass('animated fadeOutLeft');
					$(parent).html(html);
					$(parent).addClass('animated fadeInLeft');
					setTimeout(function(){
						$(parent).removeClass('animated fadeInLeft');
						sf.build();
						if(tmpldata.pageId!="" && tmpldata.pageId!="null" && tmpldata.pageId!=undefined){
							//$.scrollTo(  { top:0, left:0}, 250 );
						}
						sf.exec(tmpldata);
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
						sf.build();
						if(tmpldata.pageId!="" && tmpldata.pageId!="null" && tmpldata.pageId!=undefined){
							//$.scrollTo(  { top:0, left:0}, 250 );
						}
						sf.exec(tmpldata);
					},400);
				},200);
			break;
			case "wait":
				$(parent).effect(tmpldata.transitionOut,300,function(){
					$(parent).html(html);
					$(parent).show(tmpldata.transitionIn,200,function(){
						sf.build();
						sf.exec(tmpldata);
					});
					if(tmpldata.redirect){
						setTimeout(function(){
							var s = {
								"method":tmpldata.redirect.request,
								"action":tmpldata.redirect.page,
								"class":"Request"
							};
							sf.send(s);
						},tmpldata.sleep);
					}else if(tmpldata.callEvent){
						setTimeout(function(){
							sf.call(tmpldata.callEvent);
						},tmpldata.sleep);
					}	
				});
			break;
			case "abot":
				$(parent).append(html);
				sf.build();
			break;
			case "cabot":
				$(parent).html("");
				$(parent).append(html);
				sf.build();
			break;
			case "atop":
				$(parent).prepend(html);
				sf.build();
			break;
			default:
				$(parent).html(html);
				sf.build();
			break;
		}
	}
	templateRender(template,tmpldata){
		return Mark.up(template,tmpldata);
	}
	compile(tmpldata){
		if(this.cache.indexOf(tmpldata.templateId) > -1){
			this.inject(
				this.templateRender(this.cache[tmpldata.templateId],tmpldata),
				tmpldata.parent,
				tmpldata.action,
				tmpldata
			);
		}else{
			this.cache[tmpldata.templateId] = tmpldata.template;
			this.cache.push(tmpldata.templateId);
			this.inject(
				this.templateRender(tmpldata.template,tmpldata),
				tmpldata.parent,
				tmpldata.action,
				tmpldata
			);
		}
		if(tmpldata.pageId!="" && tmpldata.pageId!="null" && tmpldata.pageId!=undefined){
			this.onPage_request = JSON.stringify(tmpldata.request);
			window.location.hash = "/"+tmpldata.pageId+"/"+window.btoa(JSON.stringify(tmpldata.request));
			this.onPage = tmpldata.pageId;
		}
	}
	requestParent(sock,parentPage){
		var data = {
			"method":"get",
			"action":parentPage,
			"class":"Request"
		}
		this.send(data);
	}
	build(){
		for(var i=0;i<this.storedTemplates.length;i++){
			if($(this.storedTemplates[i].parent).length){
				this.compile(this.storedTemplates[i]);
				this.storedTemplates.splice(i,1);
			}
		}
	}
	addInterval(interval,pageId){
		if(!this.interval[pageId]){
			this.interval[pageId] = new Array();
		}
		this.interval[pageId].push(interval);
	}
	killInterval(pageId){
		if(this.interval[pageId]){
			for(var i = 0; i < this.interval[pageId].length; i++){
				clearInterval(this.interval[pageId][i]);
			}
		}
	}
	messageReceived(sf, socket,msg){
		if(msg.pageTitle!="" && msg.pageTitle!=null){
			document.title = msg.pageTitle;
		}
		if(msg.templateId!=null && msg.templateId!=""){
			if(msg.pageId!=null && msg.pageId!=""){
				if(sf.onUnload!=null){
					sf.onUnload();
					sf.onUnload = null;
				}
				sf.killInterval(sf.onPage);
			}
			if($(msg.parent).length){
				sf.compile(msg);
			}else{
				if(msg.forceParent){
					sf.requestParent(socket,msg.parentTemplate);
					sf.storedTemplates.push(msg);
				}
			}
		}
		if(msg.javascripts){
			if(!sf.javascriptLoaded){
			       sf.javascriptLoaded=true;
				if(msg.javascripts.length>0){
					var js = sf.templateRender(
						msg.js_template,
						msg
					);
					eval(js);
					//this.connected();
				}
			}else{
				sf.callBack('init');
			}
		}
	}
	reload(){
        if(window.location.hash.split("/").length==3){
            if(window.atob(window.location.hash.split("/")[2])==this.onPage_request){
                this.send($.parseJSON(window.atob(window.location.hash.split("/")[2])));
            }
        }
    }
}
function sendEncryptHandshake(sf){
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
            "method":"synfam",
            "action":"cpk",
            "class":"Request"
        };
        sf.send(s);
        sf.encryptEnabled=true;
    }else{
        setTimeout(function(){
            sendEncryptHandshake(sf);
        },400);
    }
}
var _sf = new SynloadFramework();
_sf.addCall(function(sf, ws, data){

    $("body").append('<div id="loadingBar" style="border-radius:5px;-webkit-box-shadow: 0px 0px 19px -4px rgba(0,0,0,0.74);-moz-box-shadow: 0px 0px 19px -4px rgba(0,0,0,0.74);box-shadow: 0px 0px 19px -4px rgba(0,0,0,0.74);float:left;position:absolute;"><span style="width:400px;text-align:center;float:left;display:block;font-weight:bold;">ENCRYPTING CONNECTION</span><span style="display:block;width:390px;float:left;height:20px;padding:5px;margin-top:10px;margin-bottom:10px;background:#ccc;border-radius:5px;"><span class="bar" style="background:#50C441;border-radius:5px;float:left;width:0px;height:20px;"></span></span></div>');
    $("#loadingBar").css({"background":"#E6F7FC","padding":"20px","top":"40%","left":"50%","marginLeft":"-220px","width":"400px","textAlign":"center"});
    // test data
    $("#loadingBar .bar").animate({"width":(390*.10)+"px"},100);
    serverKey = data.data.spk;
    //console.log(serverKey);
    sendEncryptHandshake(sf);
    
    $("#loadingBar .bar").animate({"width":(390*.40)+"px"},100);
    
}, "encryption_handshake");

_sf.addCall(function(sf, ws, data){
    //console.log(data);
    serverKey = data.data.spk;
    var s = {
        "data":{
            "message":"HELLO"
        },
        "method":"synfam",
        "action":"ack",
        "class":"Request"
    };
    sf.send(s);
    $("#loadingBar .bar").animate({"width":(390*.75)+"px"},100);
}, "encryption_handshake_two");
_sf.addCall(function(ws, data){
    //_sf.loadDefault();
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
