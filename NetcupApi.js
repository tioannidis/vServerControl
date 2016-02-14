var SOAPClient = require("SOAPClient");
var UserSession = require("UserSession");

function NetcupApi(pUrl){
	this.soapClient = new SOAPClient.SOAPClient();
	this.url = pUrl;
}

NetcupApi.prototype.actionPowerOff = function(pServerName) {
    var lParameters = new SOAPClient.SOAPClientParameters();
    lParameters.add("loginName", UserSession.username.value);
    lParameters.add("password", UserSession.passwort.value);
    lParameters.add("vserverName", pServerName);
    var lSoapCall = this.soapClient.invoke(this.url, "vServerACPIShutdown", lParameters);
    return NetcupApi._checkForSoapError(lSoapCall);
};

NetcupApi.prototype.actionReset = function(pServerName) {
    var lParameters = new SOAPClient.SOAPClientParameters();
    lParameters.add("loginName", UserSession.username.value);
    lParameters.add("password", UserSession.passwort.value);
    lParameters.add("vserverName", pServerName);
    var lSoapCall = this.soapClient.invoke(this.url, "vServerReset", lParameters);
    return NetcupApi._checkForSoapError(lSoapCall);
};

NetcupApi.prototype.actionRestart = function(pServerName) {
    var lParameters = new SOAPClient.SOAPClientParameters();
    lParameters.add("loginName", UserSession.username.value);
    lParameters.add("password", UserSession.passwort.value);
    lParameters.add("vserverName", pServerName);
    var lSoapCall = this.soapClient.invoke(this.url, "vServerACPIReboot", lParameters);
    return NetcupApi._checkForSoapError(lSoapCall);
};

NetcupApi.prototype.actionStart = function(pServerName) {
    var lParameters = new SOAPClient.SOAPClientParameters();
    lParameters.add("loginName", UserSession.username.value);
    lParameters.add("password", UserSession.passwort.value);
    lParameters.add("vserverName", pServerName);
    var lSoapCall = this.soapClient.invoke(this.url, "vServerStart", lParameters);
    return NetcupApi._checkForSoapError(lSoapCall);
};

NetcupApi.prototype.actionStop = function(pServerName) {
    var lParameters = new SOAPClient.SOAPClientParameters();
    lParameters.add("loginName", UserSession.username.value);
    lParameters.add("password", UserSession.passwort.value);
    lParameters.add("vserverName", pServerName);
    var lSoapCall = this.soapClient.invoke(this.url, "vServerPoweroff", lParameters);
    return NetcupApi._checkForSoapError(lSoapCall);
};

NetcupApi.prototype.readServerIps = function(pServerName) {
	var lParameters = new SOAPClient.SOAPClientParameters();
	lParameters.add("loginName", UserSession.username.value);
	lParameters.add("password", UserSession.passwort.value);
	lParameters.add("vserverName", pServerName);
	var lSoapCall = this.soapClient.invoke(this.url, "getVServerIPs", lParameters);
	return NetcupApi._toArray(NetcupApi._checkForSoapError(lSoapCall));
};

NetcupApi.prototype.readServerNames = function() {
    var lParameters = new SOAPClient.SOAPClientParameters();
    lParameters.add("loginName", UserSession.username.value);
    lParameters.add("password", UserSession.passwort.value); 
    var lSoapCall = this.soapClient.invoke(this.url, "getVServers", lParameters);
    return NetcupApi._toArray(NetcupApi._checkForSoapError(lSoapCall));
};

NetcupApi.prototype.readServerNickname = function(pServerName) {
	var lParameters = new SOAPClient.SOAPClientParameters();
	lParameters.add("loginName", UserSession.username.value);
	lParameters.add("password", UserSession.passwort.value);
	lParameters.add("vservername", pServerName);
	var lSoapCall = this.soapClient.invoke(this.url, "getVServerNickname", lParameters);
	return NetcupApi._checkForSoapError(lSoapCall);
};

NetcupApi.prototype.readServerStatus = function(pServerName) {
    var lParameters = new SOAPClient.SOAPClientParameters();
    lParameters.add("loginName", UserSession.username.value);
    lParameters.add("password", UserSession.passwort.value);
    lParameters.add("vserverName", pServerName);
    var lSoapCall = this.soapClient.invoke(this.url, "getVServerState", lParameters);
    return NetcupApi._checkForSoapError(lSoapCall);
};

NetcupApi.prototype.readServerTrafficMonat = function(pServerName) {
    var lParameters = new SOAPClient.SOAPClientParameters();
    lParameters.add("loginName", UserSession.username.value);
    lParameters.add("password", UserSession.passwort.value);
    lParameters.add("vserverName", pServerName);
    var jetzt = new Date();
    lParameters.add("year", jetzt.getFullYear());
    lParameters.add("month", (jetzt.getMonth() + 1));
    var lSoapCall = this.soapClient.invoke(this.url, "getVServerTrafficOfMonth", lParameters);
    return NetcupApi._checkForSoapError(lSoapCall);
};

NetcupApi.prototype.readServerTrafficTag = function(pServerName) {
    var lParameters = new SOAPClient.SOAPClientParameters();
    lParameters.add("loginName", UserSession.username.value);
    lParameters.add("password", UserSession.passwort.value);
    lParameters.add("vserverName", pServerName);
    var jetzt = new Date();
    lParameters.add("year", jetzt.getFullYear());
    lParameters.add("month", (jetzt.getMonth() + 1));
    lParameters.add("day", jetzt.getDate());
    var lSoapCall = this.soapClient.invoke(this.url, "getVServerTrafficOfDay", lParameters);
    return NetcupApi._checkForSoapError(lSoapCall);
};

NetcupApi._checkForSoapError = function(pSoapRequest) {
    return new Promise(function(resolve, reject) {
        pSoapRequest.then(function(response) {
            if(!Array.isArray(response)){
            	if(response) {
            		if(response.toString() == "undefined error"
            			|| response.toString() == "wrong password"
                        || response.toString() == "validation error"
            			|| response.toString().startsWith("couldn't get vserver")){
            			reject(new Error(response.toString()));
        			} else {
        				resolve(pSoapRequest);
        			}
            	} else {
            		resolve(pSoapRequest);
            	}
            } else {
            	resolve(pSoapRequest);
            }	
		}).catch(function(err) {
	        reject(err);
	    });
    });
};

NetcupApi._toArray = function(pSoapRequest) {
    return new Promise(function(resolve, reject) {
        pSoapRequest.then(function(response) {
            if(!Array.isArray(response)){
            	var lArray = new Array();
            	if(response) {
            		lArray.push(response);
            	}
            	resolve(lArray);
            } else {
            	resolve(pSoapRequest);
            }	
		}).catch(function(err) {
	        reject(err);
	    });
    });
};

module.exports = {
  NetcupApi: NetcupApi
};