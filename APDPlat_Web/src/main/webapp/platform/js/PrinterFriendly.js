
Ext.ns("Ext.ux.plugins");

Ext.ux.plugins.Print = {
    printGrid: function(options) {
        var grid = this.component;
        var store = grid.getStore();
        if((!options || options.browserEvent) && store.lastOptions && store.lastOptions.params && store.lastOptions.params.limit && store.lastOptions.params.limit != 0) {
            // Depending upon app usage, you might want to change this
            // In general, we don't want more than 100 records to be loaded at once. So, we set the limit
            // In our app, we set the limit to 0 to fetch all the records...
            var limit = Math.min(100, store.getTotalCount());
            var lastParams = store.lastOptions.params;
		    var params = Ext.applyIf({limit:limit}, lastParams);
		    store.load({
		        params: params,
		        callback:function() {
    		        this.printGrid({params: lastParams});
		        },
		        scope: this
		    });
		    return;
        }
        var width = grid.getColumnModel().getTotalWidth();
        var params = {
            id: grid.el.id,
            width: width
        }
        window.open(contextPath+'/platform/PrintPreview.jsp?' + Ext.urlEncode(params), "printPreview");
        if(options && options.params) {
            // following delay of 1000 millisec should be enough in most cases
            store.load.defer(1000, store, [{params: options.params}]);
        }
    },
    
    printForm: function() {
        var form = this.component;
        var params = {
            id: form.body.id,
            type: 'form',
            title: form.title || form.ownerCt.title
        }
        window.open(contextPath+'/platform/PrintPreview.jsp?' + Ext.urlEncode(params), "printPreview");
    },
    
    init: function(o) {
        
        var xType = o.xtype || o.getXType();
        
        switch(xType) {
        case 'grid':
            this.component = o;
            if(o.tools) {
                o.tools.push({id: 'print', handler: this.printGrid, scope: this});
            } else {
                var tBar = o.getTopToolbar();
                if(tBar) {
                    var added = false;
                    for(var i=0, len=tBar.length; i<len; i++) {
                        if(tBar[i].text == '打印') {
                            var btn = tBar[i];
                            if(!btn.handler) {
                                Ext.apply(btn, {
                                    handler: this.printGrid,
                                    scope: o
                                });
                                added = true;
                            }
                            break;
                        }
                    }
                    if(!added) {
                        tBar.add({iconCls: 'print', text:'打印', handler: this.printGrid, scope: this});
                    }
                }
            }
            break;
        case 'editorgrid':
            this.component = o;
            if(o.tools) {
                o.tools.push({id: 'print', handler: this.printGrid, scope: this});
            } else {
                var tBar = o.getTopToolbar();
                if(tBar) {
                    var added = false;
                    for(var i=0, len=tBar.length; i<len; i++) {
                        if(tBar[i].text == '打印') {
                            var btn = tBar[i];
                            if(!btn.handler) {
                                Ext.apply(btn, {
                                    handler: this.printGrid,
                                    scope: o
                                });
                                added = true;
                            }
                            break;
                        }
                    }
                    if(!added) {
                        tBar.add({iconCls: 'print', text:'打印', handler: this.printGrid, scope: this});
                    }
                }
            }
            break;
        case 'form':
            this.component = o;
            if(o.tools) {
                o.tools.push({id: 'print', handler: this.printForm, scope: this});
            } else {
                var tBar = o.getTopToolbar();
                if(tBar) {
                    var added = false;
                    for(var i=0, len=tBar.length; i<len; i++) {
                        if(tBar[i].text == 'Print') {
                            var btn = tBar[i];
                            if(!btn.handler) {
                                Ext.apply(btn, {
                                    handler: this.printForm,
                                    scope: o
                                });
                                added = true;
                            }
                            break;
                        }
                    }
                    if(!added) {
                        tBar.push({iconCls: 'print', handler: this.printForm, scope: this});
                    }
                }
            }
            break;
        }
    }
}

Ext.ux.PrintSetup = function() {
    this.init();
}
    
Ext.ux.PrintSetup = Ext.extend(Ext.ux.PrintSetup, {
    setupGrid: function() {
        var width = this.params.width;
        if(!isNaN(width)) {
            width = parseInt(width) + "px";
        } else {
            width = undefined;
        }
        var params = {
            height: null,
            width: width
        };

        this.applyStyles("div[class*='x-grid3-hd-inner']", {"padding-right": null});
        this.applyStyles("div[class*='x-grid3-header-inner']", {"width": null});
        
        this.applyStyles("div[class='x-grid3-scroller']", params);
        this.applyStyles("div[class='x-panel-body']", params);
        this.applyStyles("div[class='x-grid3']", params);
        
        this.setTitle();
    },

    applyStyles: function(selector, styles) {
        var elements = Ext.DomQuery.select(selector);
        if(elements) {
            var dh = Ext.DomHelper;
            for(var i=0, len=elements.length; i<len; i++) {
                 dh.applyStyles(elements[i], styles);
            }
        }
    },
    
    setTitle: function(className) {
        if(!className) {
            className = "x-panel-header-text";
        }
        var elements = Ext.DomQuery.select("span[class='" + className + "']");
        if(elements && elements.length > 0) {
            document.title = elements[0].innerHTML;
        }
    },
    
    setupForm: function() {
        document.title = this.params.title;
    },
    
    init: function() {
        var parentWin = window.opener;
        if(parentWin) {
            var params = Ext.urlDecode(location.href.substring(location.href.indexOf("?")+1));
            this.params = params;
            if(params.id) {
                document.getElementById("print-div").innerHTML = parentWin.document.getElementById(params.id).innerHTML;
                Ext.applyIf(params, {
                    type: 'grid'
                });
                switch(params.type) {
                    case "grid":
                        this.setupGrid();
                        break;
                    case "form":
                        this.setupForm();
                }
                //window.print();
                //window.close();
            }
        }
    }
});
