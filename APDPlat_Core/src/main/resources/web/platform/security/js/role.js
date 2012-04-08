
    var namespace='security';
    var action='role';
    
    var privilegeSelector;
         
    //添加模型信息
    CreateModel = function() {
        return {
            getItems: function() {
                var loader = new parent.Ext.tree.TreeLoader({
                    dataUrl:contextPath + '/module/module!query.action?privilege=true'
                });
                privilegeSelector = new parent.Ext.ux.tree.CheckTreePanel({
                            title : '',
                            id : "privilegeSelector",
                            bubbleCheck:'none' ,
                            cascadeCheck:'all',
                            deepestOnly:'true',
                            rootVisible : false,
                            loader : loader,
                            root : new Ext.tree.AsyncTreeNode({
                                text:'功能菜单',
                                id : 'root',
                                expanded : true
                            })
                 });
                 privilegeSelector.reset=function(){
                    this.clearValue();
                };
                 var items = [{
                            xtype: 'fieldset',
                            id:'baseInfo',
                            title: '基本信息',
                            collapsible: false,
                            defaults: {
                                anchor: '95%'
                            },
                            items: [{
                                    xtype: 'textfield',
                                    name: 'model.roleName',
                                    fieldLabel: '角色名称',
                                    allowBlank: false,
                                    blankText : '角色名称不能为空'
                                },{
                                    xtype: 'textfield',
                                    name: 'model.superManager',
                                    id:'superManager',
                                    hidden: true,
                                    hideLabel:true
                                },{
                                    xtype: 'textfield',
                                    name: 'model.des',
                                    fieldLabel: '备注',
                                    allowBlank: true
                                },{
                                    xtype: 'checkbox',
                                    fieldLabel: '超级权限',
                                    boxLabel: '',
                                    listeners : {"check" : function(obj,ischecked){
                                            if(ischecked){
                                                parent.Ext.getCmp('privilegeSelectorSet').hide();
                                                parent.Ext.getCmp('superManager').setValue("true");
                                            }else{
                                                parent.Ext.getCmp('privilegeSelectorSet').show();
                                                parent.Ext.getCmp('superManager').setValue("false");
                                            }
                                    }}  
                                }
                            ]
                        },{
                            xtype: 'fieldset',
                            id:'privilegeSelectorSet',
                            title: '普通权限',
                            collapsible: true,
                            items: [privilegeSelector,{
                                xtype: 'textfield',
                                name: 'privileges',
                                id:'privileges',
                                hidden: true,
                                hideLabel:true
                            }]
                        }
                    ];
                return items;
            },
            
            show: function() {
                CreateBaseModel.prepareSubmit=function(){
                    parent.Ext.getCmp('privileges').setValue(privilegeSelector.getValue());
                };
                CreateBaseModel.show('添加角色', 'role', 680, 400, this.getItems());
            }
        };
    } ();
    //修改模型信息
    ModifyModel = function() {
        return {
            getItems: function(model) {
                var loader = new parent.Ext.tree.TreeLoader({
                    dataUrl:contextPath + '/module/module!query.action?privilege=true'
                });
                privilegeSelector = new parent.Ext.ux.tree.CheckTreePanel({
                            title : '',
                            id : "privilegeSelector",
                            deepestOnly:'true',
                            rootVisible : false,
                            loader : loader,
                            root : new Ext.tree.AsyncTreeNode({
                                text:'功能菜单',
                                id : 'root',
                                expanded : true
                            })
                 });
                privilegeSelector.reset=function(){
                    this.clearValue();
                };
                loader.on("load",function(){
                    //在数据装载完成并展开树之后再设值
                    privilegeSelector.getRootNode().expand(true,true);
                    if(model.privileges!=undefined && model.privileges.toString().length>1){
                        privilegeSelector.setValue(model.privileges);
                    }
                    privilegeSelector.bubbleCheck='none';
                    privilegeSelector.cascadeCheck='all';
                });
                var items = [{
                            xtype: 'fieldset',
                            id:'baseInfo',
                            title: '基本信息',
                            collapsible: false,
                            defaults: {
                                anchor: '95%'
                            },
                            items: [{
                                    xtype: 'textfield',
                                    name: 'model.roleName',
                                    value:model.roleName,
                                    fieldLabel: '角色名称',
                                    allowBlank: false,
                                    blankText : '角色名称不能为空'
                                },{
                                    xtype: 'textfield',
                                    name: 'model.des',
                                    value:model.des,
                                    fieldLabel: '备注',
                                    allowBlank: true
                                },{
                                    xtype: 'textfield',
                                    name: 'model.superManager',
                                    value:model.superManager,
                                    id:'superManager',
                                    hidden: true,
                                    hideLabel:true
                                },{
                                    xtype: 'checkbox',
                                    fieldLabel: '超级权限',
                                    checked:model.superManager,
                                    boxLabel: '',
                                    listeners : {"check" : function(obj,ischecked){
                                            if(ischecked){
                                                parent.Ext.getCmp('privilegeSelectorSet').hide();
                                                parent.Ext.getCmp('superManager').setValue("true");
                                            }else{
                                                parent.Ext.getCmp('privilegeSelectorSet').show();
                                                parent.Ext.getCmp('superManager').setValue("false");
                                            }
                                    }}  
                                }
                            ]
                        },{
                            xtype: 'fieldset',
                            id:'privilegeSelectorSet',
                            title: '普通权限',
                            collapsible: true,
                            items: [privilegeSelector,{
                                    xtype: 'textfield',
                                    name: 'privileges',
                                    id:'privileges',
                                    hidden: true,
                                    hideLabel:true
                            }]
                        }
                   ];
                return items;
            },

            show: function(model) {
                ModifyBaseModel.prepareSubmit=function(){
                    parent.Ext.getCmp('privileges').setValue(privilegeSelector.getValue());
                };
                ModifyBaseModel.show('修改角色', 'role', 680, 400, this.getItems(model),model);
                if(model.superManager){
                    parent.Ext.getCmp('privilegeSelectorSet').hide();
                }
            }
        };
    } ();
    //表格
    GridModel = function() {
        return {
            getFields: function(){
                var fields=[
 				{name: 'id'},
 				{name: 'version'},
				{name: 'roleName'},
				{name: 'des'}
			];
               return fields;     
            },
            getColumns: function(){
                var columns=[
 				{header: "编号", width: 5, dataIndex: 'id', sortable: true},
 				{header: "版本", width: 5, dataIndex: 'version', sortable: true},
				{header: "角色名", width: 20, dataIndex: 'roleName', sortable: true,editor:new Ext.form.TextField()},
				{header: "描述", width: 20, dataIndex: 'des', sortable: true,editor:new Ext.form.TextField()}
                            ];
                return columns;           
            },
            show: function(){
                var pageSize=17;
                
                var commands=["create","delete","updatePart"];
                var tips=['增加(C)','删除(R)','修改(U)'];
                var callbacks=[GridBaseModel.create,GridBaseModel.remove,GridBaseModel.modify];
            
                GridBaseModel.show(contextPath, namespace, action, pageSize, this.getFields(), this.getColumns(), commands, tips, callbacks);
            }
        }
    } ();
    Ext.onReady(function(){
        GridModel.show();
    });
