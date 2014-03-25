var form = {
	send: function(keyID, request, page){
		var data = new Object();
		$.each(keyID,function(key,val){
			data[key]=$(val).val();
		});
		var data = {
			"request":request,
			"page":page,
			"data":data,
			"class":"Request"
		};
		ws.send(data);
	}
}