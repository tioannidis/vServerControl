var Observable = require("FuseJS/Observable");
var UserSession = require("UserSession");

var aktualisiereSelectedServer = function(pServerliste) {
	if(pServerliste && pServerliste.length > 0) {
		// Es wurden neue Server gelesen
		if(!UserSession.selectedServer.value) {
			// Aktuell war noch kein Server ausgewählt. Wir setzen einfach den ersten.
			UserSession.selectedServer.value = pServerliste[0];
		} else {
			// Aktuell ist bereits ein Server ausgewählt
			var lServer = UserSession.selectedServer.value;
			var lServerName = lServer.name.value;
			var lServerFromSession = UserSession.serverliste.where(function(server){
				return server.name.value == UserSession.selectedServer.value.name.value;
			});
			if(lServerFromSession.length === 0) {
				// Der aktuell ausgewählte Server existiert in der Session nicht mehr. Wir setzen einfach erneut den ersten.
				UserSession.selectedServer.value = pServerliste[0];
			}
		}
	} else {
		// Es wurden keine Server gefunden. Wir löschen den ggf. bereits ausgewählten Server!
		UserSession.selectedServer.clear();
	}
}

var buildStringFromArray = function(pArray) {
	var lResult = "";
	for (var i = 0; i < pArray.length; i++) {
		if(i > 0) {
			lResult = lResult + "\n";
		}
        lResult = lResult + pArray[i];
    };
    return lResult;
}

var buildTrafficString = function(pTraffic) {
	return "In: " + bytesToString(pTraffic.in) + "\nOut: " + bytesToString(pTraffic.out) + "\nTotal: " + bytesToString(pTraffic.total);
}

var bytesToString = function(pBytes) {
    var kiloBytes = parseInt(pBytes / 1024);
    var megaBytes = parseInt(kiloBytes / 1024);
    var gigaBytes = parseInt(megaBytes / 1024);
    if (gigaBytes > 0) {
        return gigaBytes + " GB";
    }
    if (megaBytes > 0) {
        return megaBytes + " MB";
    }
    if (kiloBytes > 0) {
        return kiloBytes + " KB";
    }
    return pBytes + " B";
}

var createEmptyServer = function(pServerName) {
    return new Promise(function(resolve, reject) {
		var lServer = new Object();
		lServer.name = Observable(pServerName);
		lServer.nameWithNickname = Observable(pServerName);
		//
		var lReadNickname = UserSession.api.readServerNickname(pServerName);
		//
		Promise.all([lReadNickname]).then(function(response) {
			var lNickname = response[0];
			lServer.nickname = Observable();
			if(lNickname && lNickname != pServerName) {
				lServer.nickname.value = lNickname;
				lServer.nameWithNickname.value = pServerName + " [" + lNickname + "]";
			}
			//
			lServer.gelesen = false;
			lServer.ips = Observable();
			lServer.ipsString = Observable();
			lServer.status = Observable();
			lServer.traffic = new Object();
			lServer.traffic.monat = Observable();
			lServer.traffic.monatString = Observable();
			lServer.traffic.tag = Observable();
			lServer.traffic.tagString = Observable();
			//
			resolve(lServer);
		}).catch(function(err) {
       		reject(err);
        });
    });
}

var getServerliste = function(pRefresh) {
	if(!pRefresh && UserSession.serverliste.length > 0) {
		var lSessionListe = UserSession.serverliste.value;
		if(!Array.isArray(lSessionListe)){
        	var lArray = new Array();
        	if(lSessionListe) {
        		lArray.push(lSessionListe);
        	}
			return Promise.resolve(lArray);
        }
		return Promise.resolve(lSessionListe);
	}
    return new Promise(function(resolve, reject) {
		UserSession.api.readServerNames().then(function(response) {
			var lServerPromises = new Array();
			for (var i = 0; i < response.length; i++) {
				lServerPromises.push(createEmptyServer(response[i]));
			}
			Promise.all(lServerPromises).then(function(response) {
				var lServerliste = kombiniereServerlisteMitSession(response);
				UserSession.serverliste.replaceAll(lServerliste);
				aktualisiereSelectedServer(lServerliste);
				resolve(lServerliste);
			}).catch(function(err) {
				reject(err);
	        });
		}).catch(function(err) {
			reject(err);
	    });
	});
};

var kombiniereServerlisteMitSession = function(pServerliste) {
	var lServerliste = new Array();
	for (var i = pServerliste.length - 1; i >= 0; i--) {
		var lServer = pServerliste[i];
		var lServerName = lServer.name.value;
		var lOldServer = UserSession.serverliste.where(function(server){
			return server.name.value == lServerName;
		});
		if(lOldServer.length > 0){
			lServer = lOldServer.value;
		}
		lServerliste.push(lServer);
	}
	return lServerliste;
}

var readServer = function(pServer, pRefresh) {
	if(!pServer) {
		return Promise.resolve(pServer);
	}
	if(!pRefresh && pServer.gelesen) {
		return Promise.resolve(pServer);
	}
    return new Promise(function(resolve, reject) {
		var lPromises = new Array();
		var lServername = pServer.name.value;
		lPromises.push(UserSession.api.readServerNickname(lServername));
		lPromises.push(UserSession.api.readServerIps(lServername));
		lPromises.push(UserSession.api.readServerStatus(lServername));
		lPromises.push(UserSession.api.readServerTrafficMonat(lServername));
		lPromises.push(UserSession.api.readServerTrafficTag(lServername));
		Promise.all(lPromises).then(function(response) {
			var lNickname = response[0];
			if(lNickname && lNickname != lServername) {
				pServer.nickname.value = lNickname;
				pServer.nameWithNickname.value = lServername + " [" + lNickname + "]";
			}
			pServer.gelesen = true;
			pServer.ips.value = response[1];
			pServer.ipsString.value = buildStringFromArray(response[1]);
			pServer.status.value = response[2];
			pServer.traffic.monat.value = response[3];
			pServer.traffic.monatString.value = buildTrafficString(response[3]);
			pServer.traffic.tag.value = response[4];
			pServer.traffic.tagString.value = buildTrafficString(response[4]);
			//
			resolve(pServer);
		}).catch(function(err) {
			reject(err);
        });
	});
};

var showOKMessageAndUnblock = function(pTitle, pMessage) {
	UIBlocker.unblock();
	ModalJS.showModal(
		"Server gestartet.",
		"Bitte die Ansicht in einigen Sekunden aktualisieren, um den neuen Status zu berücksichtigen.",
		["OK"],
		function (action) {
			// Nichts tun,...
		});
}

var startServer = function(pServer) {
	UIBlocker.block();
	var lServername = pServer.name.value;
	UserSession.api.actionStart(lServername).then(function(response) {
		if(response == "true"){
			showOKMessageAndUnblock("Server gestartet.",
				"Bitte die Ansicht in einigen Sekunden aktualisieren, um den neuen Status zu berücksichtigen.");
		} else {
			showOKMessageAndUnblock("Fehler beim Server-Start.", JSON.stringify(response));	
		}
	}).catch(function(err) {
		showOKMessageAndUnblock("Fehler beim Server-Start.", err.toString());
    });
}

var restartServer = function(pServer) {
	UIBlocker.block();
	var lServername = pServer.name.value;
	UserSession.api.actionRestart(lServername).then(function(response) {
		if(response == "true"){ 
			showOKMessageAndUnblock("Server neugestartet.",
				"Bitte die Ansicht in einigen Sekunden aktualisieren, um den neuen Status zu berücksichtigen.");
		} else {
			showOKMessageAndUnblock("Fehler beim Server-Neustart.", JSON.stringify(response));	
		}
	}).catch(function(err) {
		showOKMessageAndUnblock("Fehler beim Server-Neustart.", err.toString());
    });
}

var stopServer = function(pServer) {
	UIBlocker.block();
	var lServername = pServer.name.value;
	UserSession.api.actionStop(lServername).then(function(response) {
		if(response == "true"){
			showOKMessageAndUnblock("Server gestoppt.",
				"Bitte die Ansicht in einigen Sekunden aktualisieren, um den neuen Status zu berücksichtigen.");
		} else {
			showOKMessageAndUnblock("Fehler beim Server-Stop.", JSON.stringify(response));	
		}
	}).catch(function(err) {
		showOKMessageAndUnblock("Fehler beim Server-Stop.", err.toString());
    });
}

var resetServer = function(pServer) {
	UIBlocker.block();
	var lServername = pServer.name.value;
	UserSession.api.actionReset(lServername).then(function(response) {
		if(response == "true"){
			showOKMessageAndUnblock("Server resettet.",
				"Bitte die Ansicht in einigen Sekunden aktualisieren, um den neuen Status zu berücksichtigen.");
		} else {
			showOKMessageAndUnblock("Fehler beim Server-Reset.", JSON.stringify(response));	
		}
	}).catch(function(err) {
		showOKMessageAndUnblock("Fehler beim Server-Reset.", err.toString());
    });
}

var powerOffServer = function(pServer) {
	UIBlocker.block();
	var lServername = pServer.name.value;
	UserSession.api.actionStart(lServername).then(function(response) {
		if(response == "true"){
			showOKMessageAndUnblock("Server herungerfahren.",
				"Bitte die Ansicht in einigen Sekunden aktualisieren, um den neuen Status zu berücksichtigen.");
		} else {
			showOKMessageAndUnblock("Fehler beim Server-PowerOff.", JSON.stringify(response));	
		}
	}).catch(function(err) {
		showOKMessageAndUnblock("Fehler beim Server-PowerOff.", err.toString());
    });
}

module.exports = {
	getServerliste: getServerliste,
	readServer: readServer,
	powerOffServer: powerOffServer,
	resetServer: resetServer,
	restartServer: restartServer,
	startServer: startServer,
	stopServer: stopServer
};