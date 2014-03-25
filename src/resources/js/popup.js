var popup = {
	arr: new Array(), 
	show: function(e){
		if(e.element in popup.arr){
			popup.arr[e.element]++;
		}else{
			popup.arr[e.element] = 0;
			var windowWidth = $(window).width();
			var pos = "right";
			var x = $("."+e.element).parent().offset().left+$("."+e.element).parent().outerWidth();
			if(x+400>windowWidth){
				x = $("."+e.element).parent().offset().left-400;
				pos = "left";
			}
			var y = $("."+e.element).offset().top;
			var htmlAttrs = "";
			$.each(e.r.data,function(ke,va){
				htmlAttrs += (htmlAttrs!="") ? " " : "" ;
				htmlAttrs += ke+'="'+va+'"';
			});
			$("body").append(
				'<div class="'+e.element+"_p"+' popupshow" '+htmlAttrs+' style="background:#fff;border:#e0e0e0 1px solid;'
				+((pos=="right")?"-webkit-border-top-right-radius: 10px;-webkit-border-bottom-right-radius: 10px;-moz-border-radius-topright: 10px;-moz-border-radius-bottomright: 10px;border-top-right-radius: 10px;border-bottom-right-radius: 10px;":"-webkit-border-top-left-radius: 10px;-webkit-border-bottom-left-radius: 10px;-moz-border-radius-topleft: 10px;-moz-border-radius-bottomleft: 10px;border-top-left-radius: 10px;border-bottom-left-radius: 10px;")+
				'padding:4px;top:'+y+'px;left:'+x+'px;position:absolute;width:400px;"><center><img src="http://thumb.animecap.com/loadingsmall.gif" /></center></div>'
			);
			$("."+e.element+"_p").hover(function(){
				if(e.element in popup.arr){
					popup.arr[e.element]++;
				}
			},function(){
				popup.hide(e);
			});
			ws.send(e.r);
		}
	},
	hide: function(e){
		var elemId = popup.arr[e.element]; 
		setTimeout(function(){
			if(elemId==popup.arr[e.element]){
				$("."+e.element+"_p").fadeOut();
				$("."+e.element+"_p").empty();
				$("."+e.element+"_p").remove();
				delete popup.arr[e.element];
			}
		},100);
	},
	forceHide: function(e){
		$("."+e.element+"_p").fadeOut();
		$("."+e.element+"_p").empty();
		$("."+e.element+"_p").remove();
		delete popup.arr[e.element];
	}
}