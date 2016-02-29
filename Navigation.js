var Aspect = require("Aspect");
var Observable = require("FuseJS/Observable");
var UIBlocker = require("UIBlocker");

var active_page = Observable("Login");
var active_sidebar = Observable(false);

var navigateToLogin = function() {
	if(active_page.value != "Login") {
		debug_log("Navigation-navigateToLogin");
		// Für Login kein Block!
		active_page.value = "Login";
	}
	disableTopMenueSidebar();
};

var enableTopMenueSidebar = function() {
	if(!active_sidebar.value) {
		debug_log("Navigation-enableTopMenueSidebar");
		active_sidebar.value = true;
	}
};

var disableTopMenueSidebar = function() {
	if(active_sidebar.value) {
		debug_log("Navigation-disableTopMenueSidebar");
		active_sidebar.value = false;
	}
};

var navigateToServerdetail = function() {
	if(active_page.value != "Serverdetail") {
		debug_log("Navigation-navigateToServerdetail");
		UIBlocker.block();
		active_page.value = "Serverdetail";
	}
	disableTopMenueSidebar();
};

var navigateToServerliste = function() {
	if(active_page.value != "Serverlist") {
		debug_log("Navigation-navigateToServerliste");
		UIBlocker.block();
		active_page.value = "Serverlist";
	}
	disableTopMenueSidebar();
};

var navigateToServerlisteMitDetail = function() {
	if(active_page.value != "ServerlisteMitDetail") {
		debug_log("Navigation-navigateToServerlisteMitDetail");
		UIBlocker.block();
		active_page.value = "ServerlisteMitDetail";
	}
	disableTopMenueSidebar();
};

var toggleTopMenueSidebar = function() {
	debug_log("Navigation-toggleTopMenueSidebar");
	if(active_sidebar.value){
		disableTopMenueSidebar();
	} else {
		enableTopMenueSidebar();
	}
};

Aspect.landscape.addSubscriber(function() {
	debug_log("Navigation-OrientationChangedListener");
	if(Aspect.landscape.value) {
		if(active_page.value == "Serverdetail"
			|| active_page.value == "Serverlist") {
			navigateToServerlisteMitDetail();
		}
	} else {
		// Portrait
		if(active_page.value == "ServerlisteMitDetail") {
			navigateToServerliste();
		}
	}
});

module.exports = {
	active_page: active_page,
	active_sidebar: active_sidebar,
	enableTopMenueSidebar: enableTopMenueSidebar,
	disableTopMenueSidebar: disableTopMenueSidebar,
	navigateToLogin: navigateToLogin,
	navigateToServerdetail: navigateToServerdetail,
	navigateToServerliste: navigateToServerliste,
	navigateToServerlisteMitDetail: navigateToServerlisteMitDetail,
	toggleTopMenueSidebar: toggleTopMenueSidebar
};