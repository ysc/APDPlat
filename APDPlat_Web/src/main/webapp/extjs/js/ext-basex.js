/* global Ext */
/*
 * ext-basex 3.5.1
 * ***********************************************************************************
 *
 * Ext.lib.Ajax enhancements:
 * - adds EventManager Support to Ext.lib.Ajax (if Ext.util.Observable is present in the stack)
 * - adds Synchronous Ajax Support ( options.async =false )
 * - Permits IE to Access Local File Systems using IE's older ActiveX interface via the forceActiveX property
 * - Pluggable Form encoder (encodeURIComponent is still the default encoder)
 * - Corrects the Content-Type Headers for posting JSON (application/json) and XML (text/xml)
 *   data payloads and sets only one value (per RFC)
 * - Adds fullStatus:{ isLocal, proxied, isOK, isError, isTimeout, isAbort, error, status, statusText} object
 *   to the existing Response Object.
 * - Adds standard HTTP Auth support to every request (XHR userId, password config options)
 * - options.method prevails over any method derived by the lib.Ajax stack (DELETE, PUT, HEAD etc).
 * - Adds named-Priority-Queuing for Ajax Requests
 * - adds Script=Tag support for foreign-domains (proxied:true) with configurable callbacks.
 * - Adds final features for $JIT support.
 *
 * - Adds Browser capabilities object reporting on presence of (SVG, Canvas, Flash, Cookies, XPath )
 *    if(Ext.capabilities.hasFlash){ ... }
 * - Adds Ext.overload supported for parameter-based overloading of Function and class methods.
 * - Adds Ext.clone functions for any datatype.
 * - Adds Array prototype features: first, last, clone, forEach, atRandom, include, flatten, compact, unique, filter, map
 * - Connection/response object members : getAllResponseHeaders, getResponseHeader are now functions.
 * - Adds Array.slice support for other browsers (Gecko already supports it)
 *    @example:  Array.slice( someArray, 2 )
 * - Adds Ext[isFunction, isObject, isDocument, isElement, isEvent]  methods.
 * - Adds Ext.isEventSupported('resize'[, forElement]) to determine if the browser supports a specific event.
 * - Adds multiPart Response handling (via onpart callbacks and/or parts Array of response Object)
 * - Adds parsed contentType to response objects
 * - Adds Xdomain request support for modern browsers
 *
 * ***********************************************************************************
 * Author: Doug Hendricks. doug[always-At]theactivegroup.com Copyright
 * 2007-2009, Active Group, Inc. All rights reserved.
 * ***********************************************************************************
 *
 * License: ext-basex is licensed under the terms of : GNU Open Source GPL 3.0
  *
 * Commercial use is prohibited without a Developer License, see:
 * http://licensing.theactivegroup.com.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see < http://www.gnu.org/licenses/gpl.html>.
 *
 * Donations are welcomed: http://donate.theactivegroup.com
 *
 */
 
(function() {
    var A = Ext.lib.Ajax,
        defined = function(test){return typeof test != 'undefined';},
        emptyFn = Ext.emptyFn || function(){},
        OP = Object.prototype;

    Ext.lib.Ajax.Queue = function(config) {

        config = config ? (config.name ? config : { name : config }) : {};
        Ext.apply(this, config, {
            name : 'q-default',
            priority : 5,
            FIFO : true, // false implies LIFO
            callback : null, // optional callback when queue is emptied
            scope : null, // scope of callback
            suspended : false,
            progressive : false // if true, one queue item is dispatched per poll interval

            });
        this.requests = new Array();
        this.pending = false;
        // assert/resolve to 0-9
        this.priority = this.priority > 9 ? 9 : (this.priority < 0 ? 0 : this.priority);
    };

    Ext.extend(Ext.lib.Ajax.Queue, Object, {
       /**
        * Adds Ext.lib.Ajax.request arguments to queue
        * @param {Array} request An array of request method arguments.
        *
        */
        add : function(req) {

            var permit = A.events ? A.fireEvent('beforequeue', this, req) : true;
            if (permit !== false) {
                this.requests.push(req);
                this.pending = true;
                A.pendingRequests++;
                this.manager && this.manager.start();
            }
        },

       /**
        * @property {Boolean} suspended Indicate the suspense state of the queue.
        */
        suspended : false,
        /**
         * @property {Object} activeRequest A reference to current/last active request.
         */
        activeRequest : null,

       /**
        * Selects the next item on the queue stack
        * @param {Boolean} peek If true, the queue item is returned but not removed from the stack.
        * @ default false
        */
        next : function(peek) {
            var req = peek ?
                this.requests[this.FIFO ? 'first' : 'last']()
               :this.requests[this.FIFO ? 'shift' : 'pop']();

            if (this.requests.length == 0) {
                // queue emptied callback
                this.pending = false;
                Ext.isFunction(this.callback) && this.callback.call(this.scope || null, this);
                A.events && A.fireEvent('queueempty', this);
            }
            return req || null;
        },

        /**
        * clear the queue of any remaining (pending) requests
        */
        clear : function() {
            this.suspend();
            A.pendingRequests -= this.requests.length;
            this.requests.length = 0;
            this.pending = false;
            this.resume();
            this.next(); //force the empty callback/event

        },

        /**
        * Suspend queue further queue dispatches of any remaining (pending) requests until the {@link #Ext.ux.ModuleManager-resume} method is called.
        */
        suspend : function() {
            this.suspended = true;
        },

        /** Resume from a suspended state */
        resume : function() {
            this.suspended = false;
        },

        /**
        * Dispatches the next queue item and initiates a Ext.lib.Ajax request on the result.
        * @param {Boolean} peek If true, the queue item is returned but not removed from the stack.
        * @return activeRequest
        */
        requestNext : function(peek) {
            var req;
            this.activeRequest = null;
            if (!this.suspended && (req = this.next(peek))) {
                if(req.active){  //was it aborted
                    this.activeRequest = A.request.apply(A,req);
                    A.pendingRequests--;
                } else {
                    return this.requestNext(peek);
                }
            }
            return this.activeRequest;
        }
    });

    Ext.lib.Ajax.QueueManager = function(config) {

        Ext.apply(this, config || {}, {
                    quantas : 10, // adjustable milliseconds deferred dispatch
                                    // value
                    priorityQueues : new Array(new Array(), new Array(),
                            new Array(), new Array(), new Array(), new Array(),
                            new Array(), new Array(), new Array(), new Array()), // iterable
                                                                                    // array
                                                                                    // (0-9)
                                                                                    // of
                                                                                    // prioritized
                                                                                    // queues:
                    queues : {}
                });
    };

    Ext.extend(Ext.lib.Ajax.QueueManager, Object, {
       /**
        * @cfg {Integer} quantas Adjustable milliseconds deferred dispatch timer interval
        */
        quantas : 10,

       /** Return a named queue reference
        * param {String} name The name of the desired queue.
        * @return Ext.lib.Ajax.Queue
        */
        getQueue : function(name) {
            return this.queues[name];
        },
        
        createQueue : function(config) {
            if (!config) {return null;}

            var q = new A.Queue(config);
            q.manager = this;
            this.queues[q.name] = q;

            var pqa = this.priorityQueues[q.priority];
            pqa && pqa.indexOf(q.name) == -1 && pqa.push(q.name);
            return q;
        },
       /** Remove a Queue by passed name or Queue Object reference
        * @param {String/Ext.lib.Ajax.Queue} queue
        */
        removeQueue : function(q) {
            if (q && (q = this.getQueue(q.name || q))) {
                q.clear(); // purge any pending requests
                this.priorityQueues[q.priority].remove(q.name);
                delete this.queues[q.name];
            }
        },

        /** @private */
        start : function() {
            if (!this.started) {
                this.started = true;
                this.dispatch();
            }
            return this;
        },

        /** Suspends all defined queues */
        suspendAll : function() {
            forEach(this.queues, function(Q) { Q.suspend(); });
        },

        /** Resumes all suspended queues */
        resumeAll : function() {
            forEach(this.queues, function(Q) { Q.resume();  });
            this.start();
        },

        /**
         * @cfg (Boolean) progressive Default Dispatch mode for all defined queues<p>
         * a false value will exhaust a priority queue until empty during dispatch (sequential) <p>
         * true to dispatch a single request from each priority queue until all queues exhausted.<p>This
         * option may be set on the Queue itself as well.
         * @default false
         */
        progressive : false,

        stop : function() {
            this.started = false;
            return this;
        },

        /** private
         * main Request dispatch loop. This keeps the maximum allowed number of
         * requests going at any one time (based on defined queue priority and
         * dispatch mode (see progressive).
         */

        dispatch   : function(){
            var qm = this, qmq = qm.queues;
            var quit=(A.activeRequests > A.maxConcurrentRequests);
            while(A.pendingRequests && !quit){

               var disp = function(qName) {
                    var q = qmq[qName], AR;

                    while (q && !q.suspended && q.pending && q.requestNext()) {

                        quit || (quit = A.activeRequests > A.maxConcurrentRequests);
                        if(quit)break;

                        // progressive, take the first one off each queue only
                        if (q.progressive || qm.progressive) { break;}

                     }
                     // keep going?
                     if(quit)return false;
                };

                forEach(this.priorityQueues, function(pqueue) {
                    // pqueue == array of queue names
                    !!pqueue.length && forEach(pqueue , disp, this);
                    quit || (quit = A.activeRequests > A.maxConcurrentRequests);
                    if(quit)return false;
                }, this);

            }

            if(A.pendingRequests || quit){
                this.dispatch.defer(this.quantas, this);
            } else{
                this.stop();
            }
        }
    });

    Ext.apply(A, {

        headers           : A.headers || {},
        defaultPostHeader : A.defaultPostHeader || 'application/x-www-form-urlencoded; charset=UTF-8',
        defaultHeaders    : A.defaultHeaders || {},
        useDefaultXhrHeader  : !!A.useDefaultXhrHeader,
        defaultXhrHeader  : 'Ext.basex',
        
        poll              : {},

        pollInterval      : A.pollInterval || 50,

        queueManager      : new A.QueueManager(),

        // If true (or queue config object) ALL requests are queued
        queueAll : false,

        // the Current number of active Ajax requests.
        activeRequests : 0,

        // the Current number of pending Queued requests.
        pendingRequests : 0,

        /**
         * @property maxConcurrentRequests
         * Specify the maximum allowed during concurrent Queued browser (XHR) requests
         * Note:   IE8 increases this limit to 6
         */
        maxConcurrentRequests : Ext.isIE ? Ext.value(window.maxConnectionsPerServer, 2) : 4,

        /** set True as needed, to coerce IE to use older ActiveX interface
         */
        forceActiveX : false,

        /**
         *  Global default may be toggled at any time
         */
        async : true,

        /** private */
        createXhrObject : function(transactionId, options) {
            var obj = {
                status : {
                    isError : false
                },
                
                tId   : transactionId
            }, 
            ecode = null;
            
            options || (options = {});
            try {
                options.xdomain && window.XDomainRequest && (obj.conn =  new XDomainRequest());
                
                if (!defined(obj.conn) && window.ActiveXObject && !!Ext.value(options.forceActiveX, this.forceActiveX)) {
                    throw ("IE7forceActiveX");
                }
                obj.conn || (obj.conn = new XMLHttpRequest());
                
            } catch (eo) {
                var actX = window.ActiveXObject ?
                    ( options.multiPart ? this.activeXMultipart : this.activeX ) : null ;
                    
                if(actX){
	                for (var i = 0, l = actX.length; i < l; i++) {
	                    try {
	                        obj.conn = new ActiveXObject(actX[i]);
	                        break;
	                    } catch (e) {ecode = (eo == "IE7forceActiveX"? e: eo);}
	                }
                }
            } finally {
                obj.status.isError = !defined(obj.conn);
                obj.status.error=  ecode;
            }
            return obj;

        },
                
        createExceptionObject: function (tId, callbackArg, isAbort, isTimeout, errObj) {          
            return {
                tId        : tId,
                status     : isAbort ? -1 : 0,
                statusText : isAbort ? 'transaction aborted' : 'communication failure',
                    isAbort: isAbort,
                  isTimeout: isTimeout,
                  argument : callbackArg
            };
        },  

        /* Replaceable Form encoder */

        encoder : encodeURIComponent,

        serializeForm : function(){ 
            var reSelect = /select-(one|multiple)/i,
                reInput = /file|undefined|reset|button/i,
                reChecks = /radio|checkbox/i;
        
	        return function(form) {
	            var fElements = form.elements || (document.forms[form] || Ext.getDom(form)).elements,
	                        hasSubmit = false,
	                        encoder = this.encoder,
	                        element,
	                        options,
	                        name,
	                        val,
	                        data = '',
	                        type;
	            forEach(fElements, function(element) {
	                name = element.name;
	                type = element.type;
	                if (!element.disabled && name){
	                    if(reSelect.test(type)){
	                        forEach(element.options, function(opt) {
	                            if (opt.selected) {
	                                data += String.format("{0}={1}&",
	                                     encoder(name),
	                                     (opt.hasAttribute ? opt.hasAttribute('value') : 
                                           opt.getAttribute('value') !== null) ? opt.value : opt.text);
	                            }
	                        });
	                    } else if(!reInput.test(type)) {
	                        if(!(reChecks.test(type) && !element.checked) && !(type == 'submit' && hasSubmit)){
	                            data += encoder(name) + '=' + encoder(element.value) + '&';
	                            hasSubmit = /submit/i.test(type);
	                        }
	                    }
	                }
	            });
	            return data.substr(0, data.length - 1);
            };
        }(),

        /** private */
        getHttpStatus : function(reqObj) {

            var statObj = {
                status : 0,
                statusText : '',
                isError : false,
                isLocal : false,
                isOK : true,
                error : null,
                isAbort : false,
                isTimeout : false
            };

            try {
                if (!reqObj || !('status' in reqObj)) { throw ('noobj'); }
                statObj.status = reqObj.status;
                statObj.readyState = reqObj.readyState;
                statObj.isLocal = (!reqObj.status && location.protocol == "file:")
                        || (Ext.isSafari && !defined(reqObj.status));

                statObj.isOK = (statObj.isLocal || (statObj.status == 304
                        || statObj.status == 1223 || (statObj.status > 199 && statObj.status < 300)));

                statObj.statusText = reqObj.statusText || '';
            } catch (e) {
            } // status may not avail/valid yet, called too early, or status not support by the transport

            return statObj;

        },
        /**
         * @private
         */
        handleTransactionResponse : function(o, callback, isAbort, isTimeout) {

            callback = callback || {};
            var responseObject = null;
            o.isPart || A.activeRequests--;
            
            if (!o.status.isError) {
                o.status = this.getHttpStatus(o.conn);
                /*
                 * create and enhance the response with proper status and XMLDOM
                 * if necessary
                 */
                responseObject = this.createResponseObject(o, callback.argument, isAbort);
            }
            o.isPart || this.releaseObject(o);

            /*
             * checked again in case exception was raised - ActiveX was
             * disabled during XML-DOM creation? And mixin everything the
             * XHR object had to offer as well
             */
            o.status.isError && (responseObject = Ext.apply({}, responseObject || {},
                        this.createExceptionObject(o.tId, callback.argument,
                          (isAbort? isAbort: false), isTimeout, o.status.error)));

            responseObject.options = o.options;
            responseObject.fullStatus = o.status;

            if (!this.events
                    || this.fireEvent('status:' + o.status.status,
                            o.status.status, o, responseObject, callback,
                            isAbort) !== false) {

                if (o.status.isOK && !o.status.isError) {
                    if (!this.events
                            || this.fireEvent('response', o, responseObject,
                                    callback, isAbort, isTimeout) !== false) {
                        
                        var cb = o.isPart? 'onpart':'success';
                        
                        Ext.isFunction(callback[cb]) && 
                            callback[cb].call(callback.scope || null,responseObject);
                        
                    }
                } else {
                    if (!this.events
                            || this.fireEvent('exception', o, responseObject,
                                    callback, isAbort, isTimeout, responseObject.fullStatus.error) !== false) {
                        Ext.isFunction(callback.failure) &&
                            callback.failure.call(callback.scope || null, responseObject, responseObject.fullStatus.error);
                        
                    }
                }
            }

            return responseObject; 

        },
        /**
         * @private
         * Release the allocated XHR object and reset any timers
         */
        releaseObject:function(o){
            if(o && Ext.value(o.tId,-1)+1){
	            if(this.poll[o.tId]){
	                window.clearInterval(this.poll[o.tId]);
	                delete this.poll[o.tId];
	            }
	            if(this.timeout[o.tId]){
	                window.clearInterval(this.timeout[o.tId]);
		            delete this.timeout[o.tId];
	            }
            }
            o && (o.conn = null) ;
        },

        /**
         *  replace with a custom JSON decoder/validator if required
         */
        decodeJSON : Ext.decode,

        /**
         * @cfg reCtypeJSON
         * regexp test pattern applied to incoming response Content-Type header
         * to identify a potential JSON response. The default pattern handles
         * either text/json or application/json
         */
        reCtypeJSON : /(application|text)\/json/i,
        
        /**
         * @cfg reCtypeXML
         * regexp test pattern applied to incoming response Content-Type header
         * to identify a potential JSON response. The default pattern handles
         * either text/json or application/json
         */
        reCtypeXML : /(application|text)\/xml/i,
        
         /** private */
        createResponseObject : function(o, callbackArg, isAbort, isTimeout) {
            var obj = {
                responseXML : null,
                responseText : '',
                responseStream : null,
                responseJSON : null,
                contentType : null,
                getResponseHeader : emptyFn,
                getAllResponseHeaders : emptyFn
            };

            var headerObj = {}, headerStr = '';

            if (isAbort !== true) {
                try { // to catch bad encoding problems here
                    obj.responseJSON = o.conn.responseJSON || null;
                    obj.responseStream = o.conn.responseStream || null;
                    obj.contentType = o.conn.contentType || null;
                    obj.responseText = o.conn.responseText;
                } catch (e) {
                    o.status.isError = true;
                    o.status.error = e;
                }

                try {
                    obj.responseXML = o.conn.responseXML || null;
                } catch (ex) {
                }

                try {
                    headerStr = ('getAllResponseHeaders' in o.conn ? o.conn.getAllResponseHeaders() : null ) || '';
                    var s;
                    headerStr.split('\n').forEach( function(sHeader){
                        (s = sHeader.split(':')) && s.first() && 
	                        (headerObj[s.first().trim().toLowerCase()] = (s.last()||'').trim());
                    });
	                
                } catch (ex1) {
                    o.status.isError = true; // trigger future exception callback
                    o.status.error = ex1;
                }
                finally{ obj.contentType = obj.contentType || headerObj['content-type'] || ''; }

                if ((o.status.isLocal || o.proxied)
                        && typeof obj.responseText == 'string') {

                    o.status.isOK = !o.status.isError
                            && ((o.status.status = (!!obj.responseText.length)
                                    ? 200 : 404) == 200);

                    if (o.status.isOK
                            && 
                             ( (!obj.responseXML && this.reCtypeXML.test(obj.contentType ))
                             || (obj.responseXML && obj.responseXML.childNodes.length === 0) )
                        ) {

                        var xdoc = null;
                        try { // ActiveX may be disabled
                            if (window.ActiveXObject) {
                                xdoc = new ActiveXObject("MSXML2.DOMDocument.3.0");
                                xdoc.async = false;
                                xdoc.loadXML(obj.responseText);
                            } else {
                                var domParser = null;
                                try { // Opera 9 will fail parsing non-XML content, so trap here.
                                    domParser = new DOMParser();
                                    xdoc = domParser.parseFromString(obj.responseText,'application\/xml');
                                } catch (exP) {
                                } finally {
                                    domParser = null;
                                }
                            }
                        } catch (exd) {
                            o.status.isError = true;
                            o.status.error = exd;
                        }
                        obj.responseXML = xdoc;
                    }
                    if (obj.responseXML) {
                        var parseBad = (obj.responseXML.documentElement && obj.responseXML.documentElement.nodeName == 'parsererror')
                                || (obj.responseXML.parseError || 0) !== 0
                                || obj.responseXML.childNodes.length === 0;
                        parseBad || 
                            (obj.contentType = headerObj['content-type'] = obj.responseXML.contentType || 'text\/xml');
                    }
                }

                if (o.options.isJSON || (this.reCtypeJSON && this.reCtypeJSON.test(headerObj['content-type'] || ""))) {
                    try {
                        Ext.isObject(obj.responseJSON) || 
                            (obj.responseJSON = Ext.isFunction( this.decodeJSON ) && 
                               Ext.isString(obj.responseText)
                                ? this.decodeJSON(obj.responseText)
                                : null);
                    } catch (exJSON) {
                        o.status.isError = true; // trigger future exception callback
                        o.status.error = exJSON;
                    }
                }

            } // isAbort?
            o.status.proxied = !!o.proxied;

            Ext.apply(obj, {
                        tId     : o.tId,
                        status  : o.status.status,
                        statusText : o.status.statusText,
                        contentType : obj.contentType || headerObj['content-type'],
                        getResponseHeader : function(header){return headerObj[(header||'').trim().toLowerCase()];},
                        getAllResponseHeaders : function(){return headerStr;},
                        fullStatus : o.status,
                        isPart : o.isPart || false
                    });
               
            o.parts && !o.isPart && (obj.parts = o.parts);
            defined(callbackArg) && (obj.argument = callbackArg);
            return obj;
        },


        setDefaultPostHeader : function(contentType) {
            this.defaultPostHeader = contentType||'';
        },

        /**
         * Toggle use of the DefaultXhrHeader ('Ext.basex')
         */
        setDefaultXhrHeader : function(bool) {
            this.useDefaultXhrHeader = bool || false;
        },

        request : function(method, uri, cb, data, options) {

            var O = options = Ext.apply({
                        async : this.async || false,
                        headers : false,
                        userId : null,
                        password : null,
                        xmlData : null,
                        jsonData : null,
                        queue : null,
                        proxied : false,
                        multiPart : false,
                        xdomain  : false
                    }, options || {});

            if (!this.events
                    || this.fireEvent('request', method, uri, cb, data, O) !== false) {

                // Named priority queues
                if (!O.queued && (O.queue || (O.queue = this.queueAll || null)) ) {

                    O.queue === true && (O.queue = {name:'q-default'});
                    var oq = O.queue;
                    var qname = oq.name || oq , qm = this.queueManager;

                    var q = qm.getQueue(qname) || qm.createQueue(oq);
                    O.queue = q;
                    O.queued = true;

                    var req = [method, uri, cb, data, O];
                    req.active = true;
                    q.add(req);

                    return {
                        tId : this.transactionId++,
                        queued : true,
                        request : req,
                        options : O
                    };
                }
                
                options.onpart && (cb.onpart || 
                 (cb.onpart = Ext.isFunction(options.onpart) ? 
                    options.onpart.createDelegate(options.scope): null));
                    
                O.headers && forEach(O.headers, 
                    function(value, key) { this.initHeader(key, value, false); },this);

                var cType;
                // The Content-Type specified on options.headers always has priority over 
                // a calculated value.
                if (cType = (this.headers ? this.headers['Content-Type'] || null : null)) {
                    // remove to ensure only ONE is passed later.(per RFC)
                    delete this.headers['Content-Type'];
                }
                if (O.xmlData) {
                    cType || (cType = 'text/xml');
                    method = 'POST';
                    data = O.xmlData;
                } else if (O.jsonData) {
                    cType || (cType = 'application/json; charset=utf-8');
                    method = 'POST';
                    data = Ext.isObject(O.jsonData) ? Ext.encode(O.jsonData) : O.jsonData;
                }
                if (data) {
                    cType || (cType = this.useDefaultHeader
                                    ? this.defaultPostHeader
                                    : null);
                    cType && this.initHeader('Content-Type', cType, false);
                }

                // options.method prevails over any derived method.
                return this.makeRequest(O.method || method, uri, cb, data, O);
            }
            return null;

        },


        /** private */
        getConnectionObject : function(uri, options) {
            var o, f;
            var tId = this.transactionId;
            options || (options = {});
            try {
                if (f = options.proxied) { /* JSONP scriptTag Support */

                    o = {
                        tId : tId,
                        status : {isError : false},
                        proxied : true,
                        // synthesize an XHR object
                        conn : {
                            el : null,
                            send : function() {
                                var doc = (f.target || window).document,
                                head = doc.getElementsByTagName("head")[0];
                                if (head && this.el) {
                                    head.appendChild(this.el.dom);
                                }
                            },
                            abort : function() {
                                this.readyState = 0;
                            },
                            
                            getAllResponseHeaders : emptyFn,
                            getResponseHeader : emptyFn,
                            onreadystatechange : null,
                            onload : null,
                            readyState : 0,
                            status : 0,
                            responseText : null,
                            responseXML : null,
                            responseJSON : null
                        },
                        debug : f.debug,
                        params : options.params || {},
                        cbName : f.callbackName || 'basexCallback' + tId,
                        cbParam : f.callbackParam || null
                    };

                    window[o.cbName] = o.cb = function(content, request) {

                        content && typeof(content)=='object' && (this.responseJSON = content);
                        this.responseText = content || null;

                        this.readyState = 4;
                        this.status = !!content ? 200 : 404;
                        
                        Ext.isFunction(this.onreadystatechange) && this.onreadystatechange();
                        window[o.cbName] = undefined;
                        try {
                            delete window[o.cbName];
                        } catch (ex) {}

                        o.debug || this.el.remove();
                        this.el = null;
                        
                        Ext.isFunction(this.onload) && this.onload();
                        
                        
                    }.createDelegate(o.conn, [o], true);

                    o.conn.open = function() {

                        if (o.cbParam) {
                            o.params[o.cbParam] = o.cbName;
                        }

                        var params = Ext.urlEncode(o.params) || null;

                        this.el = monitoredNode(f.tag || 'script', {
                                    type : "text/javascript",
                                    src : params ? uri
                                            + (uri.indexOf("?") > -1
                                                    ? "&"
                                                    : "?") + params : uri,
                                    charset : f.charset || options.charset
                                            || null
                                },
                                null,
                                f.target, 
                                true); //defer head insertion until send method
                                
                        this.readyState = 1; // show CallInProgress
                        Ext.isFunction(this.onreadystatechange) && this.onreadystatechange();

                    };
                    options.async = true; // force timeout support
                    
                } else {
                    o = this.createXhrObject(tId, options);
                }
                if (o) {
                    this.transactionId++;
                }
            } catch (ex3) { 
                o && (o.status.isError = !!(o.status.error = ex3));
            } finally {
                return o;
            }
        },
        
        /** private */
        makeRequest : function(method, uri, callback, postData, options) {

            var o;
            if (o = this.getConnectionObject(uri, options)) {
                o.options = options;
                var r = o.conn;
                
                try {
                    if(o.status.isError){ throw o.status.error };
                    
                    A.activeRequests++;
                    r.open(method.toUpperCase(), uri, options.async, options.userId, options.password);
                   
                    ('onreadystatechange' in r) && 
                        (r.onreadystatechange = this.onStateChange.createDelegate(this, [o, callback, 'readystate'], 0));
                    
                    ('onload' in r) &&
                        (r.onload = this.onStateChange.createDelegate(this, [o, callback, 'load', 4], 0));
                        
                    ('onprogress' in r) &&
                        (r.onprogress = this.onStateChange.createDelegate(this, [o, callback, 'progress'], 0));
                        
                    //IE8/other? evolving timeout callback support
	                if(callback && callback.timeout){
                        ('timeout' in r) && (r.timeout = callback.timeout);
                        ('ontimeout' in r) && 
                           (r.ontimeout = this.abort.createDelegate(this, [o, callback, true], 0));
                        ('ontimeout' in r) ||
                           // Timers for syncro calls won't work here, as it's a blocking call
                           (options.async && (this.timeout[o.tId] = window.setInterval(
                                function() {A.abort(o, callback, true);
                            }, callback.timeout)));
                    }
                    
                    if (this.useDefaultXhrHeader && !options.xdomain) {
	                    this.defaultHeaders['X-Requested-With'] ||
	                        this.initHeader('X-Requested-With', this.defaultXhrHeader, true);
	                }
	                this.setHeaders(o);
	                
	                if (!this.events
                            || this.fireEvent('beforesend', o, method, uri,
                                    callback, postData, options) !== false) {
                        r.send(postData || null);
                    }
                } catch (exr) {
                    o.status.isError = true;
                    o.status.error = exr;
                }
                if(o.status.isError ) {
                    return Ext.apply(o, this.handleTransactionResponse(o, callback));
                }
                options.async || this.onStateChange(o, callback, 'load'); 
                return o;
            }
        },


        abort : function(o, callback, isTimeout) {

            if (o && o.queued && o.request) {
                o.request.active = o.queued = false;
                this.events && this.fireEvent('abort', o, callback);
                return true;
            } else if (o && this.isCallInProgress(o)) {
                
                if (!this.events || this.fireEvent(isTimeout ? 'timeout' : 'abort', o, callback)!== false){
	                Ext.isFunction(o.conn.abort) && o.conn.abort();
	                o.status.isAbort = !(o.status.isTimeout = isTimeout || false);
                    this.handleTransactionResponse(o, callback, o.status.isAbort, isTimeout);
                }
                return true;
            } else {
                return false;
            }
        },
        
        isCallInProgress : function(o) {
            // if there is a connection and readyState is supported, and not 0 or 4
            if( o && o.conn ){
                if('readyState' in o.conn && {0:true,4:true}[o.conn.readyState]){
                    return false;
                }
                return true;
            }
            return false;
        },

        /**
         * Clears the Browser authentication Cache
         * @param {String} url {optional) reset url for non-IE browsers
         * @return void
         */
        clearAuthenticationCache : function(url) {

            try {

                if (Ext.isIE) {
                    // IE clear HTTP Authentication, (but ALL realms though)
                    document.execCommand("ClearAuthenticationCache");
                } else {
                    // create an xmlhttp object
                    var xmlhttp;
                    if (xmlhttp = new XMLHttpRequest()) {
                        // prepare invalid credentials
                        xmlhttp.open("GET", url || '/@@', true, "logout", "logout");
                        // send the request to the server
                        xmlhttp.send("");
                        // abort the request
                        xmlhttp.abort.defer(100, xmlhttp);
                    }
                }
            } catch (e) {} // There was an error
           
        },

        // private
        initHeader : function(label, value) {
            (this.headers = this.headers || {})[label] = value;
        },
          
        /** @private 
         * General readyStateChange multiPart handler 
         */
        onStateChange : function(o, callback, mode) {
            
            if(!o.conn){ return; }
            
            var C = o.conn, readyState = ('readyState' in C ? C.readyState : 0);
            if(mode === 'load' || readyState > 2){
                var ct;
                try{ct = C.contentType || C.getResponseHeader('Content-Type') || '';}
                catch(exRs){ }
                
                if(ct && /multipart\//i.test(ct)){
                    var r = null, boundary = ct.split('"')[1], kb = '--' + boundary;
                    o.multiPart = true;
                    try{r = C.responseText;}catch(ers){}
                     
                    var p = r ? r.split(kb) : null;
                        
                    if(p){
                         o.parts || (o.parts = []);
	                     p.shift();
	                     p.pop();
	                    
	                     forEach( 
                           Array.slice(p, o.parts.length), //skip parts already parsed 
		                      function(newPart){
		                        var content = newPart.split('\n\n');
		                        var H = (content[0] ? content[0] : '') + '\n';
		                        o.parts.push(this.handleTransactionResponse(
		                          Ext.apply(
                                    Ext.clone(o),{
		                            boundary : boundary,
		                                conn : {  //synthetic conn structure for each part
		                                    status : 200,
		                                    responseText : (content[1]||'').trim(),
		                           getAllResponseHeaders : function(){
		                                        return H.split('\n').filter(
		                                            function(value){return !!value;}).join('\n');
		                                    }
		                                },
		                                isPart : true
		                          }), callback));
		                  },this);
                    }
                    
                }
            }
            (readyState === 4 || mode === 'load') && A.handleTransactionResponse(o, callback);
            this.events && this.fireEvent.apply(this, ['readystatechange'].concat(Array.slice(arguments, 0)));
        },
        
        setHeaders:function(o){

            //Some XDomain implementations (IE8) do not support setting headers
            if(o.conn && 'setRequestHeader' in o.conn){
	            this.defaultHeaders &&
		            forEach(this.defaultHeaders, function(value, key){ o.conn.setRequestHeader(key, value);});
	
	            this.headers &&
		            forEach(this.headers, function(value, key){o.conn.setRequestHeader(key, value);});
            }
            this.headers = {};
            this.hasHeaders = false;

        },

        resetDefaultHeaders:function() {
            delete this.defaultHeaders;
            this.defaultHeaders = {};
            this.hasDefaultHeaders = false;
        },
        
        //These are only current versions of ActiveX XHR that support multipart responses
        activeXMultipart : [
        'MSXML2.XMLHTTP.6.0',
        'MSXML3.XMLHTTP' 
        ],
        
        activeX:[
        'MSXML2.XMLHTTP.3.0',
        'MSXML2.XMLHTTP',
        'Microsoft.XMLHTTP'
        ]

    });
    /**
     * private -- <script and link> tag support
     */
      var monitoredNode = function(tag, attributes, callback, context, deferred) {
        attributes = Ext.apply({}, attributes || {});
        context || (context = window);

        var node = null, doc = context.document,
            head = doc.getElementsByTagName("head")[0];

        if (doc && head && (node = Ext.get(doc.createElement(tag)))) {
            var ndom = Ext.getDom(node);
            
            ndom && forEach(attributes, function(value, attrib) {
                value && (attrib in ndom) && ndom.setAttribute(attrib, value);
            });

            if (callback) {
                var cb = (callback.success || callback).createDelegate(callback.scope || null, [callback], true);
                Ext.capabilities.isEventSupported('load', tag) ? node.on("load", cb) : cb.defer(50);
            }
            deferred || head.appendChild(ndom);
        }
        
        return node;
    };
    
    if (Ext.util.Observable) {

        Ext.apply(A, {

            events : {
                request : true,
                beforesend : true,
                response : true,
                exception : true,
                abort : true,
                timeout : true,
                readystatechange : true,
                beforequeue : true,
                queue : true,
                queueempty : true
            },

            /**
             * onStatus define eventListeners for a single (or array) of
             * HTTP status codes.
             */

            onStatus : function(status, fn, scope, options) {
                var args = Array.slice(arguments, 1);
                status = new Array().concat(status || new Array());
                forEach(status, function(statusCode) {
                            statusCode = parseInt(statusCode, 10);
                            if (!isNaN(statusCode)) {
                                var ev = 'status:' + statusCode;
                                this.events[ev] || (this.events[ev] = true);
                                this.on.apply(this, [ev].concat(args));
                            }
                        }, this);
            },
            
            /**
             * unStatus unSet eventListeners for a single (or array) of
             * HTTP status codes.
             */

            unStatus : function(status, fn, scope, options) {
                var args = Array.slice(arguments, 1);
                status = new Array().concat(status || new Array());
                forEach(status, function(statusCode) {
                            statusCode = parseInt(statusCode, 10);
                            if (!isNaN(statusCode)) {
                                var ev = 'status:' + statusCode;
                                this.un.apply(this, [ev].concat(args));
                            }
                        }, this);
            }

        }, new Ext.util.Observable());

        Ext.hasBasex = true;
    }
        
    // Array, object iteration and clone support
    Ext.stopIteration = { stopIter : true };

    Ext.applyIf(Array.prototype, {

        /*
         * Fix for IE, Opera < 9.5, which does not seem to include the map
         * function on Array's
         */
        map : function(fun, scope) {
            var len = this.length;
            if (typeof fun != "function") {
                throw new TypeError();
            }
            var res = new Array(len);

            for (var i = 0; i < len; i++) {
                if (i in this) {
                    res[i] = fun.call(scope || this, this[i], i, this);
                }
            }
            return res;
        },
        
        /**
         * Return true of the passed Function test true of ANY array elememt.
         * (added for IE)
         */
        some  : function(fn){
          var f= Ext.isFunction(fn) ? fn : function(){};
          var i=0, l=this.length, test=false;
          while(i<l && !(test=!!f(this[i++]))){}
          return test;
        },
        
        /**
         * Return true of the passed Function test true of ALL array elememts.
         * (added for IE)
         */
        every  : function(fn){
          var f= Ext.isFunction(fn) ? fn : function(){};
          var i=0, l=this.length, test=true;
          while(i<l && (test=!!f(this[i++]))){}
          return test;
        },

        include : function(value, deep) { // Boolean: is value present
                                            // in Array
            // use native indexOf if available
            if (!deep && typeof this.indexOf == 'function') {
                return this.indexOf(value) != -1;
            }
            var found = false;
            try {
                this.forEach(function(item, index) {
                    if (found = (deep
                            ? (item.include
                                    ? item.include(value, deep)
                                    : (item === value))
                            : item === value)) {
                        throw Ext.stopIteration;
                    }
                });
            } catch (exc) {
                if (exc != Ext.stopIteration) {
                    throw exc;
                }
            }
            return found;
        },
        // Using iterFn, traverse the array, push the current element
        // value onto the
        // result if the iterFn returns true
        filter : function(iterFn, scope) {
            var a = new Array();
            iterFn || (iterFn = function(value) {
                return value;
            });
            this.forEach(function(value, index) {
                iterFn.call(scope, value, index) && a.push(value);
            });
            return a;
        },

        compact : function(deep) { // Remove null, undefined array
                                    // elements
            var a = new Array();
            this.forEach(function(v) {
                (v === null || v === undefined) || a.push(deep && Ext.isArray(v) ? v.compact() : v);
            }, this);
            return a;
        },

        flatten : function() { // flatten: [1,2,3,[4,5,6]] ->
                                // [1,2,3,4,5,6]
            var a = new Array();
            this.forEach(function(v) {
                Ext.isArray(v) ? (a = a.concat(v)) : a.push(v);
            }, this);
            return a;
        },
        
        indexOf : function(o){
	       for (var i = 0, len = this.length; i < len; i++){
	           if(this[i] == o) return i;
	       }
	       return -1;
	    },

        
        lastIndexOf : function(val){
            var i= this.length-1;
            while(i>-1 && this[i] != val){i--;}
            return i;
        },

        unique : function(sorted /* sort optimization */, exact) { // unique:
                                                                    // [1,3,3,4,4,5]
                                                                    // ->
                                                                    // [1,3,4,5]
            var a = new Array();
            this.forEach(function(value, index) {
                if (0 == index
                        || (sorted ? a.last() != value : !a.include(value, exact))) {
                    a.push(value);
                }
            }, this);
            return a;
        },
        // search array values based on regExpression pattern returning
        // test (and optionally execute function(value,index) on test
        // before returned)
        grep : function(rePattern, iterFn, scope) {
            var a = new Array();
            iterFn || (iterFn = function(value) {
                return value;
            });
            var fn = scope ? iterFn.createDelegate(scope) : iterFn;

            if (typeof rePattern == 'string') {
                rePattern = new RegExp(rePattern);
            }
            rePattern instanceof RegExp && 
             this.forEach(function(value, index) {
                rePattern.test(value) && a.push(fn(value, index));
            });
            return a;
        },
        
        first : function() {
            return this[0];
        },

        last : function() {
            return this[this.length - 1];
        },

        clear : function() {
            this.length = 0;
        },

        // return an array element selected at random
        atRandom : function(defValue) {
            var r = Math.floor(Math.random() * this.length);
            return this[r] || defValue;
        },

        clone : function(deep) {
            if (!deep) {return this.concat();}

            var length = this.length || 0, t = new Array(length);
            while (length--) {
                t[length] = Ext.clone(this[length], true);
            }
            return t;

        },
        
         /*
         * Array forEach Iteration based on previous work by: Dean Edwards
         * (http://dean.edwards.name/weblog/2006/07/enum/) Gecko already
         * supports forEach for Arrays : see
         * http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Objects:Array:forEach
         */
        forEach : function( block, scope) {
            Array.forEach(this, block, scope);
        }            

    });


    // globally resolve forEach enumeration
    window.forEach = function(object, block, context, deep) {
        context = context || object;
        if (object) {
            if (typeof block != "function") {
                throw new TypeError();
            }
            var resolve = Object;
            if (object instanceof Function) {
                // functions have a "length" property
                resolve = Function;
            
            } else if (object.forEach instanceof Function) {
                // the object implements a custom forEach method so use that
                return object.forEach(block, context);
               
            } else if (typeof object == "string") {
                // the object is a string
                resolve = String;
                
            } else if (Ext.isNumber(object.length)) {
                // the object is array-like
                resolve = Array;
            } 
            return resolve.forEach(object, block, context, deep);
        }
    }; 

    /**
     * 
     * Primary clone Function
     */
    Ext.clone = function(obj, deep) {
        if (obj === null || obj === undefined) {return obj;}
        
        if (Ext.isFunction(obj.clone)) { 
            return obj.clone(deep);
        }
        else if(Ext.isFunction(obj.cloneNode)){
            return obj.cloneNode(deep);
        }
        var o={};
        forEach(obj, function(value, name, objAll){
            o[name] = (value === objAll ? // reference to itself?
                o : deep ? Ext.clone(value, true) : value); 
        }, obj, deep);
        return o;
    };
   
    var slice = Array.prototype.slice;
    var filter = Array.prototype.filter;
    Ext.applyIf(Array,{
        // Permits: Array.slice(arguments, 1); // mozilla already supports this
        slice: function(obj) {
            return slice.apply(obj, slice.call(arguments, 1));
            },
        //String filter iteration
        filter: function(obj, fn){
            var t = obj && typeof obj == 'string' ? obj.split('') : [];
            return filter.call(t, fn);
        },
         /*
         * Array forEach Iteration based on previous work by: Dean Edwards
         * (http://dean.edwards.name/weblog/2006/07/enum/) Gecko already
         * supports forEach for Arrays : see
         * http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Objects:Array:forEach
         */
        forEach : function( collection, block, scope) {

            if (typeof block != "function") {
                throw new TypeError();
            }
            for (var i = 0, l = collection.length; i < l; i++) {
                block.call(scope, collection[i], i, collection);
            }
          }
    });
    
    //Add clone function to primitive prototypes
    
    Ext.applyIf(RegExp.prototype,{
        clone : function() {
            return new RegExp(this);
        }        
    });

    Ext.applyIf(Date.prototype, {
        clone  : function(deep){
            return deep? new Date(this.getTime()) : this ;
        }
    });

    Ext.applyIf(Boolean.prototype, {
        clone : function(){
           return this == true; 
        }
    }); 
    
    Ext.applyIf(Number.prototype, {
        times  : function(block, context){
            var total = parseInt(this,10) || 0;
            for (var i=1; i <= total; ){
               block.call(context, i++);
            }
        },
        forEach : function(){
           this.times.apply(this, arguments);
        },
        
        clone : function(){
           return (this)+ 0; 
        }
    });

    // character enumeration
    Ext.applyIf(String.prototype, {
        
        trim : function() {
            var re = /^\s+|\s+$/g;
            return function() {
                return this.replace(re, "");
            };
        }(),
        
        trimRight : function() {
            var re = /^|\s+$/g;
            return function() {
                return this.replace(re, "");
            };
        }(),
        
        trimLeft : function() {
            var re = /^\s+|$/g;
            return function() {
                return this.replace(re, "");
            };
        }(),

        clone : function() { return String(this)+''; },
        
        forEach : function(block, context){
            String.forEach(this, block,context);
        }

    });

    
    var overload = function(pfn, fn ){

           var f = typeof pfn == 'function' ? pfn : function(){};

           var ov = f._ovl; //call signature hash
           if(!ov){
               ov = { base: f};
               ov[f.length|| 0] = f;

               f= function(){  //the proxy stub
                  var o = arguments.callee._ovl;
                  var fn = o[arguments.length] || o.base;
                  //recursion safety
                  return fn && fn != arguments.callee ? fn.apply(this,arguments): undefined;
               };
           }
           var fnA = [].concat(fn);
           for(var i=0,l=fnA.length; i<l; i++){
             //ensures no duplicate call signatures, but last in rules!
             ov[fnA[i].length] = fnA[i];
           }
           f._ovl= ov;
           return f;

       };

    
    Ext.applyIf(Ext,{
        overload : overload( overload,
           [
             function(fn){ return overload(null, fn);},
             function(obj, mname, fn){
                 return obj[mname] = overload(obj[mname],fn);}
          ]),
          
        isIterable : function(obj){
            //check for array or arguments
            if( obj === null || obj === undefined )return false; 
            if(Ext.isArray(obj) || !!obj.callee || Ext.isNumber(obj.length) ) return true;
            
            return !!((/NodeList|HTMLCollection/i).test(OP.toString.call(obj)) || //check for node list type
              //NodeList has an item and length property
              //IXMLDOMNodeList has nextNode method, needs to be checked first.
             obj.nextNode || obj.item || false); 
        },

        isArray : function(obj){
           return OP.toString.apply(obj) == '[object Array]';
        },

        isObject:function(obj){
            return (obj !== null) && typeof obj == 'object';
        },
        
        isNumber: function(obj){
            return typeof obj == 'number' && isFinite(obj);
        },
        
        isBoolean: function(obj){
            return typeof obj == 'boolean';
        },

        isDocument : function(obj){
            return OP.toString.apply(obj) == '[object HTMLDocument]' || (obj && obj.nodeType === 9);
        },

        isElement : function(obj){
            return obj && Ext.type(obj)== 'element';
        },

        isEvent : function(obj){
            return OP.toString.apply(obj) == '[object Event]' || (Ext.isObject(obj) && !Ext.type(obj.constructor) && (window.event && obj.clientX && obj.clientX === window.event.clientX));
        },

        isFunction: function(obj){
            return OP.toString.apply(obj) == '[object Function]';
        },

        isString : function(obj){
            return typeof obj == 'string';
        },
        
        isDefined: defined
        
    });
     /**
      * @class Ext
      * @singleton
      * @constructor
      * @description Ext Adapter extensions
      */
          
    /**
     * @class Ext.capabilities
     * @singleton
     * @desc Describes Detected Browser capabilities.
     */
    Ext.capabilities = {
            /**
             * @property {Boolean} hasActiveX True if the Browser support (and is enabled) ActiveX.
             */
            hasActiveX : !!window.ActiveXObject,
            
            /**
             * @property {Boolean} hasXDR True, if the Browser has Cross-Domain Ajax request capability.
             */
            hasXDR  : function(){
                return (Ext.isIE && defined(window.XDomainRequest)) 
                    || Ext.isSafari4 
                    || (Ext.isGecko && 'withCredentials' in new XMLHttpRequest()) ;
            }(),
            
            /**
             * @property {Boolean} hasFlash True if the Flash Browser plugin is installed.
             */
            hasFlash : (function(){
                //Check for ActiveX first because some versions of IE support navigator.plugins, just not the same as other browsers
                if(window.ActiveXObject){
                    try{
                        //try to create a flash instance
                        new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
                        return true;
                    }catch(e){};
                    //If the try-catch fails, return false
                    return false;
                }else if(navigator.plugins){
                    //Loop through all the plugins
                    for(var i=0, length = navigator.plugins.length; i < length; i++){
                        //test to see if any plugin names contain the word flash, if so it must support it - return true
                        if((/flash/gi).test(navigator.plugins[i].name)){
                            return true;
                        }
                    }
                    //return false if no plugins match
                    return false;
                }
                //Return false if ActiveX and nagivator.plugins are not supported
                return false;
                })(),
            
                /**
             * @property {Boolean} hasCookies True if the browser cookies are enabled/supported.
             */
            hasCookies : !!navigator.cookieEnabled ,
            
            /**
             * @property {Boolean} hasCanvas True if the browser has canvas Element support.
             */
            hasCanvas  : !!document.createElement("canvas").getContext,
            
            /**
             * @property {Boolean} hasSVG True if the browser has SVG support.
             */
            hasSVG     : !!(document.createElementNS && document.createElementNS('http://www.w3.org/2000/svg', 'svg').width),
            
            /**
             * @property {Boolean} hasXpath True if the browser has Xpath query support.
             */
            hasXpath   : !!document.evaluate,
            
            hasBasex   : true,
            
            /**
	         * Determine whether a specified DOMEvent is supported by a given HTMLElement or Object.
	         * @param {String} type The eventName (without the 'on' prefix)
	         * @param {HTMLElement/Object/String} testEl (optional) A specific HTMLElement/Object to test against, otherwise a tagName to test against.
	         * based on the passed eventName is used, or DIV as default. 
	         * @return {Boolean} True if the passed object supports the named event. 
	         */  
	         isEventSupported : function(evName, testEl){
	            var TAGNAMES = {
	              'select':'input','change':'input',
	              'submit':'form','reset':'form',
	              'error':'img','load':'img','abort':'img'
	            },
	            //Cached results
	            cache = {},
	            //Get a tokenized string of the form nodeName:type
	            getKey = function(type, el){
                    
                    var tEl = Ext.getDom(el);
                    
	                return (tEl ?
	                           (Ext.isElement(tEl) || Ext.isDocument(tEl) ?
	                                tEl.nodeName.toLowerCase() :
	                                    el.self ? '#window' : el || '#object')
	                       : el || 'div') + ':' + type;
	            };
	
	            return function (evName, testEl) {
                  var el, isSupported = false;
                  var eventName = 'on' + evName;
                  var tag = (testEl ? testEl : TAGNAMES[evName]) || 'div';
	              var key = getKey(evName, tag);
                  
	              if(key in cache){
	                //Use a previously cached result if available
	                return cache[key];
	              }
	              
	              el = Ext.isString(tag) ? document.createElement(tag): testEl;
	              isSupported = (!!el && (eventName in el));
	              
	              isSupported || (isSupported = window.Event && !!(String(evName).toUpperCase() in window.Event));
                  
	              if (!isSupported && el) {
	                el.setAttribute && el.setAttribute(eventName, 'return;');
	                isSupported = Ext.isFunction(el[eventName]);
	              }
	              //save the cached result for future tests
	              cache[key] = isSupported;
	              el = null;
	              return isSupported;
	            };
	
	        }()
        };

})();

 // enumerate custom class properties (not prototypes unless protos==true)
 // usually only called by the global forEach function
 Ext.applyIf(Function.prototype, {
   forEach : function( object, block, context, protos) {
       if(object){
        var key;
         for (key in object) {
            (!!protos || object.hasOwnProperty(key)) &&
               block.call(context||object, object[key], key, object);
        }
      }
    },

    clone : function(deep){ return this;}
  }); 