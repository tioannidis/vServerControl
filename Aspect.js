var Observable = require("FuseJS/Observable");

var landscape = Observable(false);
var portrait = Observable(true);

var setLandscape = function() {
	landscape.value = true;
	portrait.value = false;
};

var setPortrait = function() {
	landscape.value = false;
	portrait.value = true;
};

module.exports = {
	landscape: landscape,
	portrait: portrait,
	setLandscape: setLandscape,
	setPortrait: setPortrait
};