var Observable = require("FuseJS/Observable");

var api = null;
var loggedIn = Observable(false);
var passwort = Observable("");
var selectedServer = Observable();
var serverliste = Observable();
var username = Observable("");

var clear = function() {
	debug_log("UserSession-Clear");
	loggedIn.value = false;
	passwort.value = "";
	username.value = "";
	api = null;
	selectedServer.clear();
	serverliste.clear();
};

module.exports = {
	api: api,
	clear: clear,
	loggedIn: loggedIn,
	passwort: passwort,
	selectedServer: selectedServer,
	serverliste: serverliste,
	username: username
};