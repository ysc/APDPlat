/**
 * 在线用户
 */
var store;
var onlineGrid;
var onlineUserStoreURL=contextPath+'/security/user!online.action';
var orgURL=contextPath+'/security/org!store.action';
var roleURL=contextPath+'/security/role!store.action';
var role="";
var org="";
var roleText="";
var orgText="";
var pageSize=10;

var OnlineUser = {
	getView : function() {
		var panel=this.getPanel();
		var window = new Ext.Window({
			title : '当前在线用户',
                        maximizable:true,
                        iconCls:'onlineUser',
			width : 900,
			height : 450,
			layout:'fit',
			items : [panel],
			modal:true,
			buttonAlign : 'center',
			buttons : [{
                                    text : '关闭',
                                    iconCls:'cancel',
                                    handler : function() {
                                            window.close();
                                    }
                            }]
		});
		return window;
	},

	getPanel : function() {
		//定义数据集对象
		store = new Ext.data.Store({
			reader: new Ext.data.JsonReader({
                            totalProperty: 'totalProperty',
                            root: 'root'
			},
			Ext.data.Record.create([
 				{name: 'id'},
				{name: 'username'},
				{name: 'enabled'},
				{name: 'roles'},
				{name: 'orgName'},
				{name: 'des'},
			])
			),
			proxy : new parent.Ext.data.HttpProxy({
				url : onlineUserStoreURL
			})
		});
		//创建工具栏组件
		var toolbar = new Ext.Toolbar();  
		//创建Grid表格组件
		var cb = new Ext.grid.CheckboxSelectionModel();
                store.on('beforeload',function(store){
                   store.baseParams = {limit:pageSize,role:role,org:org};
                });
                
		onlineGrid = new Ext.grid.GridPanel({
                        title:'所有在线用户',
                        autoHeight: true,
			frame:true,
			tbar : toolbar,
			store: store,
                        bbar: GridBaseModel.getBBar(pageSize,store),
			stripeRows : true,
			autoScroll : true,
			viewConfig : {
				autoFill : true,
                                forceFit:true
			},
			sm : cb,
			columns: [//配置表格列
				new Ext.grid.RowNumberer({
					header : '行号',
					width : 40
				}),//表格行号组件
				cb,
 				{header: "编号", width: 10, dataIndex: 'id', sortable: true},
				{header: "用户名", width: 20, dataIndex: 'username', sortable: true},
				{header: "状态", width: 20, dataIndex: 'enabled', sortable: true},
				{header: "拥有角色", width: 40, dataIndex: 'roles', sortable: true},
				{header: "组织机构", width: 40, dataIndex: 'orgName', sortable: true},
				{header: "描述", width: 40, dataIndex: 'des', sortable: true}
			]
		});

		var orgPanel = new Ext.tree.TreePanel({
                                        autoScroll:true,
					id : 'orgPanel',
					title : '组织机构 ',
					iconCls:'org',
                                        rootVisible:true,
					loader : new Ext.tree.TreeLoader({
								url : orgURL
							}),
					root : new Ext.tree.AsyncTreeNode({
                                                                text: '组织机构',
                                                                draggable:false, // disable root node dragging
                                                                id:'1',
								expanded : true
							}),
					listeners : {
						'click' : this.clickOrgNode
					}
				});

		var rolePanel = new Ext.tree.TreePanel({
                                        autoScroll:true,
					id : 'rolePanel',
					iconCls:'role',
					title : '角色',
					rootVisible : true,
					loader : new Ext.tree.TreeLoader({
								url : roleURL
							}),
					root : new Ext.tree.AsyncTreeNode({
                                                                text:'角色',
                                                                id : 'root',
								expanded : true
							}),
					listeners : {
						'click' : this.clickRoleNode
					}
				});
		
		var allPanel = new Ext.tree.TreePanel({
					id : 'onlinePanel',
					iconCls:'onlineUser',
					title : '所有在线用户',
					rootVisible : true,
					root : new Ext.tree.AsyncTreeNode({
                                                                iconCls:'onlineUser',
                                                                text:'在线用户',
								leaf : true
							}),
					listeners : {
						'click' : this.clickOnlinePanel
					}
				});
				

		var onlinePanel = new Ext.Panel({
					id : 'contactPanel',
					layout : 'border',
					border : false,
					items : [{
                                                region : 'west',
                                                split : true,
                                                collapsible : true,
                                                width : 230,
                                                margins : '5 0 5 5',
                                                layout : 'accordion',
                                                items : [allPanel,orgPanel, rolePanel]
                                        }, {
                                                region : 'center',
                                                margins : '5 0 5 5',
                                                layout: 'fit',
                                                items : [onlineGrid]
                                        }]
				});
		return onlinePanel;
	},
        show : function(){
            this.getView().show();
            this.clickOnlinePanel();
        },
	clickOrgNode : function(node) {
		if (node != null) {
                        org=node.id;
                        orgText=node.text;
                        store.load({
                            params:{
                                limit:pageSize,
                                org:org,
                                role:role
                            }
                        });
                        var title="";
                        if(roleText!=""){
                            title="角色：【"+roleText+"】 ";
                        }
                        title+="组织机构：【"+orgText+"】";
                        onlineGrid.getView().refresh();
                        onlineGrid.setTitle(title);
		}
	},
	clickRoleNode : function(node) {
		if (node != null) {
                        role=node.id.toString().split("-")[1];
                        if(role==undefined){
                            role="";
                        }
                        roleText=node.text;
                        store.load({
                            params:{
                                limit:pageSize,
                                org:org,
                                role:role
                            }
                        });
                        var title = "角色：【"+roleText+"】";
                        if(orgText!=""){
                            title+=" 组织机构：【"+orgText+"】";
                        }
                        onlineGrid.getView().refresh();
                        onlineGrid.setTitle(title);
		}
	},
	clickOnlinePanel:function(){
                role="";
                org="";
                store.load({
                    params:{
                        limit:pageSize,
                        org:org,
                        role:role
                    }
                });
                onlineGrid.getView().refresh();
                onlineGrid.setTitle("所有在线用户");
	}
};
