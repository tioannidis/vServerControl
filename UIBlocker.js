var Observable = require("FuseJS/Observable");

var counter = 0;
var ui_blocked = Observable(false);

var block = function() {
	if(!ui_blocked.value){
		ui_blocked.value = true;
	}
	counter++;
};

var unblock = function() {
	if(counter <= 0){
		throw new Error("Die UI ist aktuell nicht geblockt!");
	} else {
		counter--;
		if(counter == 0 && ui_blocked.value){
			ui_blocked.value = false;
		}
	}
};

module.exports = {
	block: block,
	ui_blocked: ui_blocked,
	unblock: unblock
};