var Observable = require("FuseJS/Observable");

var api = null;
var loggedIn = Observable(false);
var passwort = Observable("");
var selectedServer = Observable();
var username = Observable("");

var clear = function() {
	loggedIn.value = false;
	passwort.value = "";
	username.value = "";
	api = null;
	selectedServer = null;
};

module.exports = {
	api: api,
	clear: clear,
	loggedIn: loggedIn,
	passwort: passwort,
	selectedServer: selectedServer,
	username: username
};