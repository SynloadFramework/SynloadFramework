$(".menuItem").removeClass("active");
$("#{{data.uname}}").addClass("active");
if($("#{{data.uname}}").hasClass("subMenu")){
	$("#{{data.uname}}").parent().parent().addClass("active");
}