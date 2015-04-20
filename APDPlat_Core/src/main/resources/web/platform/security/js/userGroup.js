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

var namespace='security';
    var action='user-group';
    
    var roleSelector;
    var selectRoleStoreURL=contextPath + '/security/role/store.action?recursion=true';
         
    //添加模型信息
    CreateModel = function() {
        return {
            getItems: function() {
                var roleLoader = new parent.Ext.tree.TreeLoader({
                    dataUrl:selectRoleStoreURL
                });
                roleSelector = new parent.Ext.ux.tree.CheckTreePanel({
                            title : '',
                            id : "roleSelector",
                            bubbleCheck:'none' ,
                            cascadeCheck:'all',
                            deepestOnly:'true',
                            rootVisible : false,
                            loader : roleLoader,
                            root : new Ext.tree.AsyncTreeNode({
                                text:'角色',
                                id : 'root',
                                expanded : true
                            })
                 });
                 roleSelector.reset=function(){
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
                                    name: 'model.userGroupName',
                                    fieldLabel: '用户组名称',
                                    allowBlank: false,
                                    blankText : '用户组名称不能为空'
                                },{
                                    xtype: 'textfield',
                                    name: 'model.des',
                                    fieldLabel: '备注',
                                    allowBlank: true
                                }
                            ]
                        },{
                            xtype: 'fieldset',
                            id:'roleSelectorSet',
                            title: '选择角色',
                            collapsible: true,
                            items: [
                                roleSelector,{
                                xtype: 'textfield',
                                name: 'roles',
                                id:'roles',
                                hidden: true,
                                hideLabel:true
                            }]
                        }
                    ];
                return items;
            },
            
            show: function() {
                CreateBaseModel.prepareSubmit=function(){
                    parent.Ext.getCmp('roles').setValue(roleSelector.getValue());
                };
                CreateBaseModel.show('添加用户组', 'role', 680, 400, this.getItems());
            }
        };
    } ();
    //修改模型信息
    ModifyModel = function() {
        return {
            getItems: function(model) {
                var roleLoader = new parent.Ext.tree.TreeLoader({
                    dataUrl:selectRoleStoreURL
                });
                roleSelector = new parent.Ext.ux.tree.CheckTreePanel({
                            title : '',
                            id : "roleSelector",
                            deepestOnly:'true',
                            rootVisible : false,
                            loader : roleLoader,
                            root : new Ext.tree.AsyncTreeNode({
                                text:'角色',
                                id : 'root',
                                expanded : true
                            })
                 });
                roleSelector.reset=function(){
                    this.clearValue();
                };
                roleLoader.on("load",function(){
                    //在数据装载完成并展开树之后再设值
                    roleSelector.getRootNode().expand(true,true);
                    if(model.roles!=undefined && model.roles.toString().length>1){
                        roleSelector.setValue(model.roles);
                    }
                    roleSelector.bubbleCheck='none';
                    roleSelector.cascadeCheck='all';
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
                                        name: 'model.userGroupName',
                                        value:model.userGroupName,
                                        fieldLabel: '用户组名称',
                                        allowBlank: false,
                                        blankText : '用户组名称不能为空'
                                    },{
                                        xtype: 'textfield',
                                        name: 'model.des',
                                        value:model.des,
                                        fieldLabel: '备注',
                                        allowBlank: true
                                    }
                                ]
                            },{
                                xtype: 'fieldset',
                                id:'roleSelectorSet',
                                title: '选择角色',
                                collapsible: true,
                                items: [roleSelector,{
                                    xtype: 'textfield',
                                    name: 'roles',
                                    id:'roles',
                                    hidden: true,
                                    hideLabel:true
                                }]
                            }
                   ];
                return items;
            },

            show: function(model) {
                ModifyBaseModel.prepareSubmit=function(){
                    parent.Ext.getCmp('roles').setValue(roleSelector.getValue());
                };
                ModifyBaseModel.show('修改用户组', 'role', 680, 400, this.getItems(model),model);
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
				{name: 'userGroupName'},
				{name: 'roles'},
				{name: 'des'}
			];
               return fields;     
            },
            getColumns: function(){
                var columns=[
 				{header: "编号", width: 5, dataIndex: 'id', sortable: true},
 				{header: "版本", width: 5, dataIndex: 'version', sortable: true},
				{header: "用户组名称", width: 20, dataIndex: 'userGroupName', sortable: true,editor:new Ext.form.TextField()},
 				{header: "拥有的角色", width: 20, dataIndex: 'roles', sortable: true},
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