var Observable = require("FuseJS/Observable");
var UIBlocker = require("UIBlocker");

var active_page = Observable("Login");
var active_sidebar = Observable(null);

var navigateToLogin = function() {
	if(active_page.value != "Login") {
		// Für Login kein Block!
		active_page.value = "Login";
		active_sidebar.value = null;
	}
};

var navigateToServerdetail = function() {
	if(active_page.value != "Serverdetail") {
		UIBlocker.block();
		active_page.value = "Serverdetail";
		active_sidebar.value = null;
	}
};

var navigateToServerlist = function() {
	if(active_page.value != "Serverlist") {
		UIBlocker.block();
		active_page.value = "Serverlist";
		active_sidebar.value = null;
	}
};

var toggleTopMenueSidebar = function() {
	if(active_sidebar.value == null){
		active_sidebar.value = "menu";
	} else {
		active_sidebar.value = null;
	}
};

module.exports = {
	active_page: active_page,
	active_sidebar: active_sidebar,
	navigateToLogin: navigateToLogin,
	navigateToServerdetail: navigateToServerdetail,
	navigateToServerlist: navigateToServerlist,
	toggleTopMenueSidebar: toggleTopMenueSidebar
};