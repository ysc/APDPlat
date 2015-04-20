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
var currentName="组织架构";
var propertyCriteriaPre="parent.id:eq:";
var propertyCriteria=propertyCriteriaPre+currentId;

var namespace='security';
var action='org';

var treeDataUrl=contextPath+'/'+namespace+'/'+action+'/store.action';
            
//添加组织架构
CreateModel = function() {
    return {
        getItems: function() {
             var items = [{
                        layout: 'form',
                        defaults: {
                            anchor:"90%"
                        },
                        items:[{
                                    xtype:'textfield',
                                    readOnly:true,
                                    disabled:true,
                                    fieldClass:'detail_field',
                                    value: currentName,
                                    fieldLabel: '上级机构'
                                },{
                                    xtype:'textfield',
                                    cls : 'attr',
                                    name: 'model.orgName',
                                    maxLength:50,
                                    fieldLabel: '机构名称',
                                    allowBlank: false,
                                    blankText : '机构名称不能为空'
                                },{
                                    xtype:'textfield',
                                    cls : 'attr',
                                    name: 'model.chargeMan',
                                    maxLength:50,
                                    fieldLabel: '负责人姓名'
                                },{
                                    xtype:'textfield',
                                    cls : 'attr',
                                    name: 'model.phone',
                                    maxLength:50,
                                    fieldLabel: '联系电话'
                                },{
                                    xtype:'textfield',
                                    cls : 'attr',
                                    name: 'model.address',
                                    maxLength:100,
                                    fieldLabel: '办公地址'
                                },{
                                    xtype:'textarea',
                                    cls : 'attr',
                                    name: 'model.functions',
                                    fieldLabel: '部门职能'
                                }]
                }];
            return items;
        },

        show: function() {
            GridBaseModel.createURLParameter='?model.parent.id='+currentId;
            CreateBaseModel.show('添加组织架构', 'org', 500, 320, this.getItems());
            CreateBaseModel.dlg.on('close',function(){
                    //刷新表格
                    GridBaseModel.refresh();
                    //刷新树
                    TreeModel.refreshTree(false);
                });
        }
    };
} ();
//修改组织架构
ModifyModel = function() {
    return {
        getItems: function(model) {
             var items = [{
                        layout: 'form',
                        defaults: {
                            anchor:"90%"
                        },
                        items:[{
                                    xtype:'textfield',
                                    readOnly:true,
                                    disabled:true,
                                    fieldClass:'detail_field',
                                    value: model.parent_orgName,
                                    fieldLabel: '上级机构'
                                },{
                                    xtype:'textfield',
                                    cls : 'attr',
                                    name: 'model.orgName',
                                    value: model.orgName,
                                    maxLength:50,
                                    fieldLabel: '机构名称',
                                    allowBlank: false,
                                    blankText : '机构名称不能为空'
                                },{
                                    xtype:'textfield',
                                    cls : 'attr',
                                    name: 'model.chargeMan',
                                    value: model.chargeMan,
                                    maxLength:50,
                                    fieldLabel: '负责人姓名'
                                },{
                                    xtype:'textfield',
                                    cls : 'attr',
                                    name: 'model.phone',
                                    value: model.phone,
                                    maxLength:50,
                                    fieldLabel: '联系电话'
                                },{
                                    xtype:'textfield',
                                    cls : 'attr',
                                    name: 'model.address',
                                    value: model.address,
                                    maxLength:100,
                                    fieldLabel: '办公地址'
                                },{
                                    xtype:'textarea',
                                    cls : 'attr',
                                    name: 'model.functions',
                                    value: model.functions,
                                    fieldLabel: '部门职能'
                                }]
                }]
            return items;
        },

        show: function(model,forceRefreshParentNode) {
            ModifyBaseModel.modifySuccess=function(form, action){
                    //刷新表格
                    GridBaseModel.refresh();
                    //刷新树
                    TreeModel.refreshTree(forceRefreshParentNode);
            };
            ModifyBaseModel.show('修改组织架构', 'org', 500, 320, this.getItems(model),model);
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
                            {name: 'orgName'},
                            {name: 'chargeMan'},
                            {name: 'phone'},
                            {name: 'address'},
                            {name: 'functions'}
                    ];
           return fields;     
        },
        getColumns: function(){
            var columns=[
                            {header: "编号", width: 20, dataIndex: 'id', sortable: true},
                            {header: "版本", width: 20, dataIndex: 'version', sortable: true},
                            {header: "机构名称", width: 40, dataIndex: 'orgName', sortable: true,editor:new Ext.form.TextField()},
                            {header: "负责人姓名", width: 40, dataIndex: 'chargeMan', sortable: true,editor:new Ext.form.TextField()},
                            {header: "联系电话", width: 40, dataIndex: 'phone', sortable: true,editor:new Ext.form.TextField()},
                            {header: "办公地址", width: 40, dataIndex: 'address', sortable: true,editor:new Ext.form.TextField()},
                            {header: "部门主要职能", width: 40, dataIndex: 'functions', sortable: true,editor:new Ext.form.TextField()}
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
            var tree = TreeBaseModel.getTreeWithContextMenu(treeDataUrl, '组织架构', 'root', 'org', create, remove, modify);
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
            currentId=node.id;
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
                        //query org detail info
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
OrgForm = function() {
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
    OrgForm.show();
});