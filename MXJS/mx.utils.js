(function() {

	var utils = {

		/** Function: trace
		 *  Prints a log message
		 *
		 *  Parameters:
		 *    (String) message - Message which should be traced
		 *    (Object) obj - Object which should be traced
		 */
		trace : function(message, obj) {
			message && console.log("Mobilis: " + message);
			obj && console.log(obj);
		},

		/** Function: createMobilisServiceIq
		 *  Create a new Strophe.Builder with an <iq/> element as root. The stanza will have the specified
		 *  attributes and additionally a "to" attribute filled with the FullJid to the service.
		 *
		 *
		 *  Parameters:
		 *    (String) namespace - Namespace of the service's agent
		 *    (String) attrs - Attributes of the <iq/> element (optional)
		 *
		 *  Returns:
		 *    A new Strophe.Builder object
		 */
		createMobilisServiceIq : function(namespace, attrs) {
			( attrs = attrs || {}).to = Mobilis.core.getFullJidFromNamespace(namespace);
			return $iq(attrs);
		},

		/** Function: appendElement
		 *  Appends an element to the Strophe.Builder.
		 *
		 *
		 *  Parameters:
		 *    (Strophe.Builder) builder - Strophe.Builder where to add element
		 *    (Object) element - Element which should be appended
		 */
		appendElement : function(builder, element) {
			if (element) {
				builder.c(element.constructor.name);
				$.each(element, function(k, v) {
					if ( typeof v !== "function")
						if ( typeof v === "object")
							if ($.isArray(v))
								$.each(v, function(i, val) {
									if ( typeof val === "object")
										Mobilis.utils.appendElement(builder, val);
									else
										builder.c(k).t(String(val)).up();
								});
							else
								Mobilis.utils.appendElement(builder, v);
						else
							builder.c(k).t(String(v)).up();
				});
				builder.up();
			}
		},

		/** Function: getUnixTime
		 *  Returns the current unix time (in ms)
		 *
		 *
		 *  Returns:
		 *    The current unix time (in ms)
		 */
		getUnixTime : function() {
			return new Date().getTime();
		}
	}

	Mobilis.extend("utils", utils);

})();
		