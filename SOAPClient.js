var Marknote = require("Marknote");

function SOAPClientParameters() {
    this._pl = new Array();
    this._sl = new Array();
}

SOAPClientParameters.prototype.add = function(name, value) {
    this._pl[name] = value;
    return this;
};

SOAPClientParameters.prototype.addSchema = function(prefix, uri) {
    this._sl[prefix] = uri;
    return this;
};
    
SOAPClientParameters.prototype.toXml = function() {
    var xml = "";
    for(var p in this._pl)
    {
        switch(typeof(this._pl[p]))
        {
            case "string":
            case "number":
            case "boolean":
            case "object":
                xml += SOAPClientParameters._serialize(p, this._pl[p]);
                break;
            default:
                break;
        }
    }
    return xml;
};
    
SOAPClientParameters.prototype.printSchemaList = function() {
    var list = [];

    for (var prefix in this._sl) {
        if (this._sl.hasOwnProperty(prefix)) {
            list.push('xmlns:' + prefix + '="' + this._sl[prefix] + '"');
        }
    }

    return list.join(' ');
}

SOAPClientParameters._serialize = function(t, o)
{
    var s = "";
    switch(typeof(o))
    {
        case "string":
            s += "<" + t + ">";
            s += o.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
            s += "</" + t + ">";
            break;
        case "number":
        case "boolean":
            s += "<" + t + ">";
            s += o.toString(); 
            s += "</" + t + ">";
            break;
        case "object":
            // Date
            if(o.constructor.toString().indexOf("function Date()") > -1)
            {
        
                var year = o.getFullYear().toString();
                var month = (o.getMonth() + 1).toString();
                month = (month.length == 1) ? "0" + month : month;
                var date = o.getDate().toString();
                date = (date.length == 1) ? "0" + date : date;
                var hours = o.getHours().toString();
                hours = (hours.length == 1) ? "0" + hours : hours;
                var minutes = o.getMinutes().toString();
                minutes = (minutes.length == 1) ? "0" + minutes : minutes;
                var seconds = o.getSeconds().toString();
                seconds = (seconds.length == 1) ? "0" + seconds : seconds;
                var milliseconds = o.getMilliseconds().toString();
                var tzminutes = Math.abs(o.getTimezoneOffset());
                var tzhours = 0;
                while(tzminutes >= 60)
                {
                    tzhours++;
                    tzminutes -= 60;
                }
                tzminutes = (tzminutes.toString().length == 1) ? "0" + tzminutes.toString() : tzminutes.toString();
                tzhours = (tzhours.toString().length == 1) ? "0" + tzhours.toString() : tzhours.toString();
                var timezone = ((o.getTimezoneOffset() < 0) ? "+" : "-") + tzhours + ":" + tzminutes;
                s += "<" + t + ">";
                s += year + "-" + month + "-" + date + "T" + hours + ":" + minutes + ":" + seconds + "." + milliseconds + timezone;
                s += "</" + t + ">";
            }
            // Array
            else if(o.constructor.toString().indexOf("function Array()") > -1)
            {
                
                s += "<" + t + " SOAP-ENC:arrayType=\"SOAP-ENC:Array[" + o.length + "]\" xsi:type=\"SOAP-ENC:Array\">";
                for(var p in o)
                {
                    if(!isNaN(p))   // linear array
                    {
                        (/function\s+(\w*)\s*\(/ig).exec(o[p].constructor.toString());
                        var type = RegExp.$1;
                        switch(type)
                        {
                            case "":
                                type = typeof(o[p]);
                            case "String":
                                type = "string";
                                break;
                            case "Number":
                                type = "int";
                                break;
                            case "Boolean":
                                type = "bool";
                                break;
                            case "Date":
                                type = "DateTime";
                                break;
                        }
                        s += SOAPClientParameters._serialize("item", o[p]);
                    }
                    else    // associative array
                    {
                        SOAPClientParameters._serialize("item", o[p]);
                    }
                }
                s += "</" + t + ">";
            }
            // Object or custom function
            else
                for(var p in o)
                {
                    s += "<" + t + ">";
                    s += SOAPClientParameters._serialize(p, o[p]);
                    s += "</" + t + ">";
                }
            break;
        default:
            throw new Error(500, "SOAPClientParameters: type '" + typeof(o) + "' is not supported");
    }
    return s;
}

function SOAPClient() {}
 
SOAPClient.prototype.invoke = function(url, method, parameters) {
    debug_log("SOAPClient-invoke: " + url + " - " + method);
    return new Promise(function(resolve, reject) {
        SOAPClient._getWsdl(url).then(function(response) {
            resolve(SOAPClient._sendSoapRequest(url, method, parameters, response));
        }).catch(function(err) {
            reject(err);
        });
    });
}

SOAPClient_cacheWsdl = new Array();

SOAPClient._getNamespace = function(wsdl) {
    // Namespace ermitteln
    var lRootElement = wsdl.getRootElement();
    var ns = lRootElement.getAttributeValue("targetNamespace");
    if(typeof ns == "undefined"){
        var nsNode = lRootElement.getChildElements("targetNamespace");
        if(typeof nsNode != "undefined"){
            ns = nsNode.nodeValue;
        }
    }
    return ns;
}

SOAPClient._getSoapRequest = function(pNamespace, pMethod, pParameters) {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
    + "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\""
    + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\""
    + " xmlns:wsp=\"http://www.w3.org/ns/ws-policy\""
    + " xmlns:wsp1_2=\"http://schemas.xmlsoap.org/ws/2004/09/policy\""
    + " xmlns:wsam=\"http://www.w3.org/2007/05/addressing/metadata\""
    + " xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\""
    + (pNamespace ? " xmlns:tns=\"" + pNamespace + "\"" : "")
    + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >"
    + "<SOAP-ENV:Body>"
    + "<tns:" + pMethod + ">"
    + pParameters.toXml()
    + "</tns:" + pMethod + ">"
    + "</SOAP-ENV:Body>"
    + "</SOAP-ENV:Envelope>";
}

SOAPClient._getWsdl = function(url) {
    return new Promise(function(resolve, reject) {
        // WSDL bereits im Cache?
        var wsdl = SOAPClient_cacheWsdl[url];
        if(wsdl) {
            resolve(wsdl);
        } else {
            // WSDL neu einlesen und in den Cache stellen
            debug_log("SOAPClient-Read Wsdl: " + url);
            fetch(url + "?wsdl", {
                method: 'GET',
                headers: { "Content-type": "text/xml; charset=utf-8" }
            }).then(function(response) {
                if(response.ok) {
                    return response.text();
                } else {
                    reject(new Error("Lese WSDL: " + response.statusText + " (" + response.status + ")"));;
                }
            }).then(function(responseText) {
                if (!responseText || responseText == ""){
                    reject(new Error("SOAP-Call: Leere Antwort erhalten."));
                } else {
                    if (!responseText || responseText == ""){
                        reject(new Error("Lese WSDL: Leere Antwort erhalten."));
                    } else {
                        var parser = new Marknote.Parser();
                        wsdl = parser.parse(responseText);
                        SOAPClient_cacheWsdl[url] = wsdl
                        resolve(wsdl);
                    }
                }
            }).catch(function(err) {
                reject(err);
            });
        }
    });
}

SOAPClient._sendSoapRequest = function(url, method, parameters, wsdl) {
    return new Promise(function(resolve, reject) {
        // Namespace ermitteln
        var ns = SOAPClient._getNamespace(wsdl);
        
        // SOAP-Request erstellen
        var lRequestBody = SOAPClient._getSoapRequest(ns, method, parameters);

        // SoapAction ermitteln
        var lSoapAction = ((ns.lastIndexOf("/") != ns.length - 1) ? ns + "/" : ns) + method;

        // Request absetzen
        fetch(url, {
            method: 'POST',
            headers: { "Content-type": "text/xml; charset=utf-8", "SOAPAction": lSoapAction },
            body: lRequestBody
        }).then(function(response) {
            if(response.ok) {
                return response.text();
            } else {
                reject(new Error("SOAP-Call: " + response.statusText + " (" + response.status + ")"));
            }
        }).then(function(responseText) {
            if (!responseText || responseText == ""){
                debug_log("SOAPClient-Result-Error: Leere Antwort");
                reject(new Error("SOAP-Call: Leere Antwort erhalten."));
            } else {
                var parser = new Marknote.Parser();
                var lResponseXml = parser.parse(responseText);
                var lRootElement = lResponseXml.getRootElement();
                var lFaultElements = SOAPClient._getChildElements(lRootElement, "faultstring");
                if(lFaultElements.length > 0) {
                    debug_log("SOAPClient-Result-Error: " + lFaultElements[0]);
                    reject(new Error(lFaultElements[0]));
                } else {
                    var lReturnElements = SOAPClient._getChildElements(lRootElement, "return");
                    if(lReturnElements.length == 0) {
                        debug_log("SOAPClient-Result-Error: Kein Ergebnis");
                        reject(new Error("SOAP-Call: Kein Ergebnis gefunden."));
                    } else if(lReturnElements.length == 1) {
                        debug_log("SOAPClient-SingleResult: " + lReturnElements[0]);
                        var lResult = SOAPClient._toObject(lReturnElements[0]);
                        resolve(lResult);
                    } else {
                        var lResult = new Array();
                        for (var i = 0; i < lReturnElements.length; i++) {
                            lResult.push(SOAPClient._toObject(lReturnElements[i]));
                        };
                        debug_log("SOAPClient-ArrayResult: " + lResult.length + " Elemente");
                        resolve(lResult);
                    }
                }   
            }
        }).catch(function(err) {
            reject(err);
        });
    });
} 
 
SOAPClient._getChildElements = function(pXmlElement, pName) {
    var lElements = new Array();
    try{
        if(pXmlElement.getQName().getLocalPart() == pName) {
            lElements.push(pXmlElement);
        }
        if(pXmlElement.hasContents()) {
            for (var i = 0; i < pXmlElement.getContents().length; i++) {
                var lContent = pXmlElement.getContentAt(i);
                var lChildElements = SOAPClient._getChildElements(lContent, pName);
                lElements = lElements.concat(lChildElements);
            };
        }
    }catch(err){
        // Nichts tun,...
    }
    return lElements;
} 
 
SOAPClient._toObject = function(pXmlElement) {
    try{
        var lElementName = pXmlElement.getQName().getLocalPart();
        if(pXmlElement.hasContents() && pXmlElement.getContents().length > 0) {
            var lResult = new Object();
            for (var i = 0; i < pXmlElement.getContents().length; i++) {
                var lSubElement = pXmlElement.getContentAt(i);
                var lSubElementName = lSubElement.getQName().getLocalPart();
                var lSubResult = SOAPClient._toObject(lSubElement);
                if(lResult[lSubElementName]) {
                    if(!Array.isArray(lResult[lSubElementName])) {
                        var lSubArray = new Array();
                        lSubArray.push(lResult[lSubElementName]);
                        lResult[lSubElementName] = lSubArray;
                    }
                    lResult[lSubElementName].push(lSubResult);
                } else {
                    lResult[lSubElementName] = lSubResult;
                }
            }
            return lResult;
        } else {
            // Es sind keine Content-Elemente enthalten
            return null;
        }
    }catch(err){
        try{
            // Dann haben wir hier ggf. nur noch ein Text-Element
            return pXmlElement.getText();
        }catch(err){
            // Dann haben wir hier kein strukturiertes XML-Element, sondern einen primitiven Typen
            return pXmlElement;
        }
    }
}

module.exports = {
    SOAPClient: SOAPClient,
    SOAPClientParameters: SOAPClientParameters
};