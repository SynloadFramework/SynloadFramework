/*
User Pages
*/
function getCookie(cname){
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for(var i=0; i<ca.length; i++){
		var c = ca[i].trim();
		if (c.indexOf(name)==0) return c.substring(name.length,c.length);
	}
	return "";
}
var user = {
	name: "",
	unique: "",
	flags: Array(),
	id:"",
	msg: function(socket, msg){
		// DERP DERP
	},
	event: function(socket, msg){
		if(msg.class=="Failed" && msg.callEvent=="login"){
			$(".loginBoxClass").effect( "bounce", "slow" );
			$("#username").val("");
			$("#username").focus();
		}else if(msg.class=="Failed" && msg.callEvent=="register"){
			$("#username").val("");
			$("#username").focus();
			$(".registerBoxClass").effect( "bounce", "slow" );
		}else if(msg.class=="Success" && msg.callEvent=="login"){
			user.authenticated(msg);
			system.loadDefault();
		}else if(msg.class=="Success" && msg.callEvent=="logout"){
			/*var data = {
				"request":"get",
				"page":"login",
				"class":"Request"
			}
			ws.send(data);*/
			user.name = "";
			user.sessionID = "";
			user.flags = new Array();
			user.id = "";
			system.loadDefault();
		}else if(msg.class=="Success" && msg.callEvent=="register"){
			var data = {
				"request":"get",
				"page":"login",
				"class":"Request"
			}
			ws.send(data);
		}else if(msg.class=="Success" && msg.callEvent=="session"){
			user.authenticated(msg);
			system.loadDefault();
		}else if(msg.class=="Failed" && msg.callEvent=="session"){
			var data = {
				"request":"get",
				"page":"login",
				"class":"Request"
			}
			ws.send(data);
			system.loadDefault();
		}
	},
	showUserSettings: function(){
		ws.send({"request":"get","page":"userSettings","class":"Request"});
	},
	authenticated: function(query){
		user.name = query.data.name;
		user.sessionID = query.data.session;
		if(query.data.flags){
			user.flags = jQuery.parseJSON(query.data.flags);
		}
		document.cookie="sessionid="+user.sessionID;
		user.id = query.data.id;
		user.loggedIn();
		
	},
	loggedIn: function(){
		setInterval(function(){
			var data = {
				"request":"get",
				"page":"ping",
				"class":"Request"
			}
			ws.send(data);
		},30000);
	},
	login: function(user, pass){
		if($(user).val()!="" && $(pass).val()!=""){
			var data = {
				"request":"action",
				"data":{
					"username":$(user).val(),
					"password":$(pass).val(),
				},
				"page":"login",
				"class":"Request"
			}
			ws.send(data);
		}else{
			system.alert("Please enter a username and password!",{ header: 'User System' });
		}
	},
	create: function(user,pass,email){
		if($(user).val()!="" && $(pass).val()!="" && $(email).val()!=""){
			var data = {
				"request":"action",
				"data":{
					"username":$(user).val(),
					"password":$(pass).val(),
					"email":$(email).val()
				},
				"page":"register",
				"class":"Request"
			}
			ws.send(data);
		}else{
			system.alert("Please specify username, password and email address!",{ header: 'User System' });
		}
	},
	logout: function(){
		document.cookie = "sessionid=; expires=Thu, 01 Jan 1970 00:00:00 GMT";
		var data = {
			"request":"get",
			"page":"logout",
			"class":"Request"
		};
		ws.send(data);
	},
	load: function(s){
		if(getCookie("sessionid")!=""){
			console.log("Previously logged in!");
			var data = {
				"request":"get",
				"data": {
					"sessionid":getCookie("sessionid"),
				},
				"page":"sessionlogin",
				"class":"Request"
			}
			ws.send(data);
		}else{
			console.log("Not previously logged in!");
			/*var data = {
				"request":"get",
				"page":"login",
				"class":"Request"
			};
			ws.send(data);*/
			system.loadDefault();
		}
	}
}