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

var currentNode;
var currentId="-1";
var currentName="角色";
var propertyCriteriaPre="parent.id:eq:";
var propertyCriteria=propertyCriteriaPre+currentId;

var namespace='security';
var action='role';
var privilegeSelector;

var treeDataUrl=contextPath+'/'+namespace+'/'+action+'/store.action';

//添加角色
CreateModel = function() {
    return {
        getItems: function() {
            var privilegeLoader = new parent.Ext.tree.TreeLoader({
                dataUrl:contextPath + '/module/module/store.action?privilege=true'
            });
            privilegeSelector = new parent.Ext.ux.tree.CheckTreePanel({
                        title : '',
                        id : "privilegeSelector",
                        bubbleCheck:'none' ,
                        cascadeCheck:'all',
                        deepestOnly:'true',
                        rootVisible : false,
                        loader : privilegeLoader,
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
                                    xtype:'textfield',
                                    readOnly:true,
                                    disabled:true,
                                    fieldClass:'detail_field',
                                    value: currentName,
                                    fieldLabel: '上级角色'
                                },{
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
            GridBaseModel.createURLParameter='?model.parent.id='+currentId;
            CreateBaseModel.show('添加角色', 'role', 500, 420, this.getItems());
            CreateBaseModel.dlg.on('close',function(){
                    //刷新表格
                    GridBaseModel.refresh();
                    //刷新树
                    TreeModel.refreshTree(false);
                });
        }
    };
} ();
//修改角色
ModifyModel = function() {
    return {
        getItems: function(model) {
            var privilegeLoader = new parent.Ext.tree.TreeLoader({
                dataUrl:contextPath + '/module/module/store.action?privilege=true'
            });
            privilegeSelector = new parent.Ext.ux.tree.CheckTreePanel({
                        title : '',
                        id : "privilegeSelector",
                        deepestOnly:'true',
                        rootVisible : false,
                        loader : privilegeLoader,
                        root : new Ext.tree.AsyncTreeNode({
                            text:'功能菜单',
                            id : 'root',
                            expanded : true
                        })
             });
            privilegeSelector.reset=function(){
                this.clearValue();
            };
            privilegeLoader.on("load",function(){
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
                                    xtype:'textfield',
                                    readOnly:true,
                                    disabled:true,
                                    fieldClass:'detail_field',
                                    value: model.parent_roleName,
                                    fieldLabel: '上级角色'
                                },{
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

        show: function(model,forceRefreshParentNode) {
            ModifyBaseModel.prepareSubmit=function(){
                parent.Ext.getCmp('privileges').setValue(privilegeSelector.getValue());
            };
            ModifyBaseModel.modifySuccess=function(form, action){
                    //刷新表格
                    GridBaseModel.refresh();
                    //刷新树
                    TreeModel.refreshTree(forceRefreshParentNode);
            };
            ModifyBaseModel.show('修改角色', 'role', 500, 420, this.getItems(model),model);
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
                            {header: "编号", width: 20, dataIndex: 'id', sortable: true},
                            {header: "版本", width: 20, dataIndex: 'version', sortable: true},
                            {header: "角色名称", width: 40, dataIndex: 'roleName', sortable: true,editor:new Ext.form.TextField()},
                            {header: "描述", width: 20, dataIndex: 'des', sortable: true,editor:new Ext.form.TextField()}
                        ];
            return columns;           
        },
        getGrid: function(){
            var pageSize=14;

            //删除数据命令回调
            GridBaseModel.removeSuccess=function(response,opts){
                GridBaseModel.refresh();
                TreeModel.refreshTree(false);
            };
            //修改单个属性回调
            GridBaseModel.updateAttrSuccess=function(response, opts){
                GridBaseModel.refresh();
                TreeModel.refreshTree(false);
            };    
            //添加特殊参数
            if(currentId!=-1){
                GridBaseModel.propertyCriteria=propertyCriteria;
            }
            if(currentId==-1){
                GridBaseModel.loadStore=function(){
                    //不加载表格
                }
            }
            GridBaseModel.setStoreBaseParams=function(store){
                store.on('beforeload',function(store){
                   store.baseParams = {propertyCriteria:GridBaseModel.propertyCriteria};
                });
            };
            var commands=["create","delete","updatePart"];
            var tips=['增加(C)','删除(R)','修改(U)'];
            var callbacks=[GridBaseModel.create,GridBaseModel.remove,GridBaseModel.modify];

            var grid=GridBaseModel.getGrid(contextPath, namespace, action, pageSize, this.getFields(), this.getColumns(), commands, tips, callbacks);

            //设置标题
            grid.setTitle(" ");

            return grid;
        }
    }
} ();
//左部树
TreeModel = function(){
    return{
        getTreeWithContextMenu: function(){
            TreeBaseModel.onClick=this.onClick;
            TreeBaseModel.remove=this.remove;
            TreeBaseModel.modify=this.modify;    

            var create=true;
            var remove=true;
            var modify=true;
            var tree = TreeBaseModel.getTreeWithContextMenu(treeDataUrl, '角色', 'root', 'role', create, remove, modify);
            currentNode=TreeBaseModel.root;
            return tree;
        },
        //当forceRefreshParentNode为true时表示需要强制刷新父节点
        refreshTree: function(forceRefreshParentNode){
            if(currentNode.parentNode && forceRefreshParentNode){
                currentNode.parentNode.reload(
                    // node loading is asynchronous, use a load listener or callback to handle results
                    function(){
                        currentNode=TreeBaseModel.tree.getNodeById(currentId);
                        TreeModel.select(currentNode);
                    },
                this);
            }else{
                if(!currentNode.isExpandable()){
                    //当前节点是叶子节点（新添加的节点是当前节点的第一个子节点）
                    if(currentNode.parentNode==null){
                        TreeBaseModel.root.reload();
                        TreeBaseModel.root.expand(false, true);
                    }else{
                        //重新加载当前节点的父节点，这样才能把新添加的节点装载进来
                        currentNode.parentNode.reload(
                            // node loading is asynchronous, use a load listener or callback to handle results
                            function(){
                                //重新查找当前节点，因为已经重新加载过数据
                                currentNode=TreeBaseModel.tree.getNodeById(currentId);
                                //展开当前节点
                                currentNode.expand(false, true);
                            },
                        this);
                    }
                }else{
                    //重新加载当前节点
                    currentNode.reload(
                        // node loading is asynchronous, use a load listener or callback to handle results
                        function(){
                            //展开当前节点
                            currentNode.expand(false, true);
                        },
                    this);
                }
            }
        },
        select: function(node,event){
            node.expand(false, true);
            currentNode=node;
            currentId=node.id.split("-")[1];
            currentName=node.text;
            GridBaseModel.grid.setTitle("已选中【"+currentName+"】");
            propertyCriteria=propertyCriteriaPre+currentId;
            GridBaseModel.propertyCriteria=propertyCriteria;
        },
        onClick: function(node, event) {
            TreeModel.select(node, event);
            GridBaseModel.refresh();
        },
        remove: function() {
                if(currentNode.parentNode==TreeBaseModel.tree.getRootNode()){
                    parent.Ext.ux.Toast.msg('操作提示：','不能删除根节点');  
                    return;
                }
                //在删除当前节点之前记住父节点
                var parentNode=currentNode.parentNode;
                Ext.MessageBox.confirm("请确认","确实要删除【"+currentName+"】吗？",function(button,text){
                    if(button == "yes"){
                        parent.Ext.Ajax.request({
                            url : GridBaseModel.deleteURL+'?time='+new Date().toString(),
                            params : {
                                ids : currentId
                            },
                            method : 'POST',
                            success: function(response,options){
                                var data=response.responseText;
                                if("删除成功"==data){
                                    TreeModel.select(parentNode);
                                    GridBaseModel.refresh();
                                    TreeModel.refreshTree(false);
                                }else{
                                    parent.Ext.MessageBox.alert('提示', "删除失败！");
                                }
                            },
                            failure: function() {
                                alert("删除失败！");
                            }
                        });
                    }
                });
            },
        modify: function() {
                if(currentNode.parentNode==TreeBaseModel.tree.getRootNode()){
                    parent.Ext.ux.Toast.msg('操作提示：','不能修改根节点');  
                    return;
                }
                Ext.MessageBox.confirm("请确认","确实要修改【"+currentNode.text+"】吗？",function(button,text){
                    if(button == "yes"){
                        //query role detail info
                        parent.Ext.Ajax.request({
                                url : GridBaseModel.retrieveURL+currentId+'&time='+new Date().toString(),
                                method : 'POST',
                                success : function(response,options){
                                    var data=response.responseText;
                                    //返回的数据是对象，在外层加个括号才能正确执行eval
                                    var model=eval('(' + data + ')');
                                    ModifyModel.show(model,true);
                                }
                        });
                    }
                });
            }
    }
}();
//左边为树右边为表格的编辑视图
RoleForm = function() {
    return {
        show: function() {
                 var frm = new Ext.Viewport({
                    layout : 'border',
                    items: [
                        TreeModel.getTreeWithContextMenu(),
                        {
                            region:'center',
                            autoScroll:true,
                            layout: 'fit',
                            items:[GridModel.getGrid()]
                        }
                    ]
                });
        }
    };
} ();

Ext.onReady(function(){
    RoleForm.show();
});