var Observable = require("FuseJS/Observable");

var counter = 0;
var ui_blocked = Observable(false);

var block = function() {
	if(!ui_blocked.value){
		ui_blocked.value = true;
	}
	counter++;
	debug_log("UIBlocker-block: " + counter);
};

var unblock = function() {
	if(counter > 0){
		counter--;
		if(counter == 0 && ui_blocked.value){
			ui_blocked.value = false;
		}
	}
	debug_log("UIBlocker-unblock: " + counter);
};

module.exports = {
	block: block,
	ui_blocked: ui_blocked,
	unblock: unblock
};