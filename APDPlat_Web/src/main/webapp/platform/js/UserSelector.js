/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

/**
 * 用户选择组件
 */
var store;
var userSelectGrid;
var userSelectStoreURL=contextPath+'/security/user!store.action?select=true';
var orgURL=contextPath+'/security/org!store.action';
var roleURL=contextPath+'/security/role!store.action';
var roleId=0;
var orgId=0;
var roleText="";
var orgText="";
var pageSize=8;

var propertyCriteriaPre="collection:roles,object:role,role.id:eq:";
var propertyCriteria="";
parent.Ext.useShims=true;

var UserSelector = {
	getView : function() {
		var panel=this.getPanel();
		this.window = new parent.Ext.Window({
			title : '选择用户',
                        maximizable:true,
                        iconCls:'onlineUser',
			width : 900,
			height : 475,
			layout:'fit',
			items : [panel],
			modal:true,
			buttonAlign : 'center',
			buttons : [{
                                            text : '确定',
                                            iconCls:'create',
                                            handler : function() {
                                                UserSelector.select();
                                            }
                                    },
                                    {
                                            text : '关闭',
                                            iconCls:'cancel',
                                            handler : function() {
                                                    UserSelector.window.close();
                                            }
                                    }
                     ]
		});
		return this.window;
	},
        select: function(){    
            var selectedUsers=parent.Ext.getCmp("selectedUsers");
            if(selectedUsers==undefined){
                selectedUsers=Ext.getCmp("selectedUsers");
            }
            var users=selectedUsers.getValue();     
                                   
            if(users.indexOf(";")==-1){
                parent.Ext.MessageBox.alert('提示','请至少选择一个用户！');
                return;
            }
            var receiver=parent.Ext.getCmp(UserSelector.receiver);
            if(receiver==undefined){
                receiver=Ext.getCmp(UserSelector.receiver);
            }
            receiver.setValue(users);
            UserSelector.window.close();
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
				{name: 'realName'},
				{name: 'enabled'},
				{name: 'roles'},
				{name: 'orgName'},
				{name: 'des'},
			])
			),
			proxy : new parent.Ext.data.HttpProxy({
				url : userSelectStoreURL
			})
		});
		//创建工具栏组件
		var toolbar = new parent.Ext.Toolbar();  
		//创建Grid表格组件
		var cb = new parent.Ext.grid.CheckboxSelectionModel();
                store.on('beforeload',function(store){
                   store.baseParams = {limit:pageSize,propertyCriteria:propertyCriteria,orgId:orgId};
                });
                var bbar=new parent.Ext.PagingToolbar({
                    rowComboSelect : true,
                    pageSize : pageSize,
                    store : store,
                    displayInfo : true
                });
		userSelectGrid = new parent.Ext.grid.GridPanel({
                        title:'所有用户',
                        autoHeight: true,
			frame:true,
			tbar : toolbar,
			store: store,
                        bbar: bbar,
			stripeRows : true,
			autoScroll : true,
			viewConfig : {
				autoFill : true,
                                forceFit:true
			},
			sm : cb,
			columns: [//配置表格列
				new parent.Ext.grid.RowNumberer({
					header : '行号',
					width : 40
				}),//表格行号组件
				cb,
 				{header: "编号", width: 10, dataIndex: 'id', sortable: true},
				{header: "用户名", width: 20, dataIndex: 'username', sortable: true},
				{header: "用户姓名", width: 20, dataIndex: 'realName', sortable: true},
				{header: "状态", width: 20, dataIndex: 'enabled', sortable: true},
				{header: "拥有角色", width: 40, dataIndex: 'roles', sortable: true},
				{header: "组织架构", width: 40, dataIndex: 'orgName', sortable: true},
				{header: "描述", width: 40, dataIndex: 'des', sortable: true}
			]
		});

		var orgPanel = new parent.Ext.tree.TreePanel({
                                        autoScroll:true,
					id : 'orgPanel',
					title : '组织架构 ',
					iconCls:'org',
                                        rootVisible:true,
					loader : new Ext.tree.TreeLoader({
								url : orgURL
							}),
					root : new Ext.tree.AsyncTreeNode({
                                                                text: '组织架构',
                                                                draggable:false, // disable root node dragging
                                                                id:'1',
								expanded : true
							}),
					listeners : {
						'click' : this.clickOrgNode
					}
				});

		var rolePanel = new parent.Ext.tree.TreePanel({
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
		
		var allPanel = new parent.Ext.tree.TreePanel({
					id : 'onlinePanel',
					iconCls:'onlineUser',
					title : '所有用户',
					rootVisible : true,
					root : new Ext.tree.AsyncTreeNode({
                                                                iconCls:'onlineUser',
                                                                text:'所有用户',
								leaf : true
							}),
					listeners : {
						'click' : this.clickUserPanel
					}
				});
				

		var userPanel = new parent.Ext.Panel({
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
                                                items : [userSelectGrid,
                                                    new parent.Ext.form.FormPanel({
                                                            labelAlign: 'left',
                                                            buttonAlign: 'center',
                                                            bodyStyle: 'padding:5px',
                                                            frame: true,//圆角和浅蓝色背景
                                                            labelWidth: 60,
                                                            autoScroll:true,

                                                            defaults: {
                                                                anchor: '96%'
                                                            },

                                                            items: [{
                                                                        xtype:'button',
                                                                        text : '增加',
                                                                        iconCls:'create',
                                                                        handler : function() {                                                                            
                                                                                var result=UserSelector.getSelectUserNames();
                                                                                if(result.length < 1){
                                                                                    //parent.Ext.MessageBox.alert('提示','请至少选择一个用户！');
                                                                                    return;
                                                                                }
                                                                                var selectedUsers=parent.Ext.getCmp("selectedUsers");
                                                                                if(selectedUsers==undefined){
                                                                                    selectedUsers=Ext.getCmp("selectedUsers");
                                                                                }
                                                                                var old=selectedUsers.getValue();                    
                                                                                if(selectedUsers!=undefined && old!=undefined && old!=''){
                                                                                    old+=";";
                                                                                }else{
                                                                                    old="";
                                                                                }
                                                                                for(var i=0;i<result.length;i++){
                                                                                    var username=result[i];
                                                                                    if(old.indexOf(username+";")==-1){
                                                                                        old=old+username+";";
                                                                                    }
                                                                                }
                                                                                old=old.replace(";;",";");
                                                                                selectedUsers.setValue(old);
                                                                        }
                                                                    },
                                                                    {
                                                                        xtype:'textarea',
                                                                        height : 100,
                                                                        width:650,
                                                                        cls : 'attr',
                                                                        name: 'selectedUsers',
                                                                        id: 'selectedUsers',
                                                                        fieldLabel: '已选用户'
                                                                    }]
                                                        })]
                                        }]
				});
		return userPanel;
	},
        show : function(receiver){
            this.receiver=receiver;
            this.getView().show();
            this.clickUserPanel();
        },
	clickOrgNode : function(node) {
		if (node != null) {
                        orgId=node.id;
                        orgText=node.text;
                        if(roleId>0){
                            propertyCriteria=propertyCriteriaPre+roleId;
                        }else{
                            propertyCriteria="";
                        }
                        store.load({
                            params:{
                                limit:pageSize,
                                orgId:orgId,
                                propertyCriteria:propertyCriteria
                            }
                        });
                        var title="";
                        if(roleText!=""){
                            title="角色：【"+roleText+"】 ";
                        }
                        title+="组织架构：【"+orgText+"】";
                        userSelectGrid.getView().refresh();
                        userSelectGrid.setTitle(title);
		}
	},
	clickRoleNode : function(node) {
		if (node != null) {
                        roleId=node.id.toString().split("-")[1];
                        if(roleId==undefined){
                            roleId=0;
                        }
                        roleText=node.text;
                        if(roleId>0){
                            propertyCriteria=propertyCriteriaPre+roleId;
                        }else{
                            propertyCriteria="";
                        }
                        store.load({
                            params:{
                                limit:pageSize,
                                orgId:orgId,
                                propertyCriteria:propertyCriteria
                            }
                        });
                        var title = "角色：【"+roleText+"】";
                        if(orgText!=""){
                            title+=" 组织架构：【"+orgText+"】";
                        }
                        userSelectGrid.getView().refresh();
                        userSelectGrid.setTitle(title);
		}
	},
	clickUserPanel: function(){
                roleId=0;
                orgId=0;
                propertyCriteria="";
                store.load({
                    params:{
                        limit:pageSize,
                        orgId:orgId,
                        propertyCriteria:propertyCriteria
                    }
                });
                userSelectGrid.getView().refresh();
                userSelectGrid.setTitle("所有用户");
	},
        //取得所选
        getSelectUserNames: function(){
                var result=[];
                var recs = userSelectGrid.getSelectionModel().getSelections();        
                   
                for(var i=0;i<recs.length;i++){
                    var rec = recs[i];
                    var username=rec.get("username");
                    result.push(username);
                }
                
                //var users = result.join(";");
                
                return result;
        }
};