/** File: mobilis.js
*  Web Client Framework for Mobilis Applications
*
*  This files contains the entire framework.
*  Including: 
*     -  Javascript BOSH client Library Strophe.js
*     -  Mobilis.core  
*/
   
(function ($) {
    if (typeof window.Mobilis === "undefined") {
        
        /** Class: Mobilis
        *  Prototype for the Mobilis object that holds the entire framework.
        *
        */
        Mobilis = function () {
            
            if (typeof $ === "undefined" || $ !== window.jQuery) {
                alert("Please load jQuery library first");
            }
            
            this.fn = {};
            
            /** Function: extend
            *  Extends Mobilis with the provided functionality encapsulated in the obj that is passed.
            *
            *  The plug-in adapter of Mobilis. Methods are available under Mobilis.namespace
            *
            *  Parameters:
            *    (String) namespace - Namespace for the plug-in.
            *    (Object) ojc - Plug-in bbject that holds the functionality.
            */
            this.extend = function (namespace, obj) {
                if (typeof this[namespace] === "undefined") {
                    if (typeof this.fn[namespace] === "undefined" && typeof this[namespace] === "undefined") {
                        $.extend(obj, this.fn.extFN);
                        this.fn[namespace] = obj;
                        this[namespace] = this.fn[namespace];
                        if (typeof this[namespace].init === "function") {
                        this[namespace].init();
                        }
                    } else {
                        alert("The namespace '" + namespace + "' is already taken...");
                    }
                }
            };
            
            this.addHandlers = function () {
                for (a in this.fn) {
                    if (typeof this.fn[a].addHandlers !== 'undefined') {
                        this.fn[a].addHandlers();
                    }
                }
            };
            
            // this.setObjectData = function (object, data) {
            //     var path = (typeof arguments[2] !== "undefined") ? arguments[2] : [];
            //     for (var k in data) {
            //         path.push(k);
            //         if (typeof data[k] === "object") {
            //             this.setObjectData(object, data[k], path);
            //         } else {
            //             var tmpObject = object;
            //             for (var i = 0, l = path.length; i < l - 1; i++) {
            //                 tmpObject = tmpObject[path[i]];
            //             }
            //             tmpObject[k] = data[k];
            //         }
            //     }
            // };
        };
        
        window.Mobilis = new Mobilis();
        
        window.Mobilis.extFN = window.Mobilis.fn.extFN = {
            
            _cache: {},
            
            setConfig: function (settings) {
                window.Mobilis.setObjectData(this.settings, settings);
                return this;
            },
            
            _toString: function () {
                if (typeof this._cache['_toString'] === 'undefined') {
                    this._cache['_toString'] = '';
                    for (var k in window.Mobilis.fn) {
                        if (window.Mobilis.fn[k] === this) {
                            this._cache['_toString'] = 'Mobilis.' + k;
                            break;
                        }
                    }
                }
                return this._cache['_toString'];
            }
        };
    }
})(jQuery);

$(window).unload(function() {
    if (Mobilis.connection){
        if (Mobilis.connection.connected){
            Mobilis.connection.send($pres({type:'unavailable'}));
            Mobilis.core.disconnect('Application Closed');
        };        
    }
});