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

//添加模型信息
var currentNode;
var currentId="-1";
var currentName="功能菜单";

var modulePropertyCriteriaPre="parentModule.id:eq:";
var commandPropertyCriteriaPre="module.id:eq:";
var propertyCriteria=modulePropertyCriteriaPre+currentId;
var rootPropertyCriteria="parentModule.english:eq:root";


var namespace='module';
var action='edit-module';

var treeDataUrl=contextPath+'/'+namespace+'/'+action+'/store.action';
            
ModifyModel = function() {
    return {
        show: function(model) {
                if(action=='edit-module'){
                    ModuleModifyModel.show(model,true);
                }
                if(action=='edit-command'){
                    CommandModifyModel.show(model,true);
                }
        }
    };
} ();
//修改模块
ModuleModifyModel = function() {
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
                                    value: model.parentModule,
                                    fieldLabel: '上级模块'
                                },{
                                    xtype:'textfield',
                                    readOnly:true,
                                    disabled:true,
                                    cls : 'attr',
                                    name: 'model.english',
                                    value: model.english,
                                    maxLength:50,
                                    fieldLabel: '模块英文名称'
                                },{
                                    xtype:'textfield',
                                    cls : 'attr',
                                    name: 'model.chinese',
                                    value: model.chinese,
                                    maxLength:50,
                                    fieldLabel: '模块中文名称'
                                },{
                                    xtype:'textfield',
                                    cls : 'attr',
                                    name: 'model.orderNum',
                                    value: model.orderNum,
                                    maxLength:50,
                                    fieldLabel: '模块顺序'
                                }]
                }];
            return items;
        },

        show: function(model) {
            ModifyBaseModel.modifySuccess=function(form, action){
                TreeModel.refreshTree();
                GridBaseModel.refresh();
            };
            ModifyBaseModel.show( '修改模块', 'editModule', 500, 200, this.getItems(model),model);
        }
    };
} ();
//修改命令
CommandModifyModel = function() {
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
                                    value: model.module,
                                    fieldLabel: '父模块'
                                },{
                                    xtype:'textfield',
                                    readOnly:true,
                                    disabled:true,
                                    cls : 'attr',
                                    name: 'model.english',
                                    value: model.english,
                                    maxLength:50,
                                    fieldLabel: '命令英文名称'
                                },{
                                    xtype:'textfield',
                                    cls : 'attr',
                                    name: 'model.chinese',
                                    value: model.chinese,
                                    maxLength:50,
                                    fieldLabel: '命令中文名称'
                                },{
                                    xtype:'textfield',
                                    cls : 'attr',
                                    name: 'model.orderNum',
                                    value: model.orderNum,
                                    maxLength:50,
                                    fieldLabel: '命令顺序'
                                }]
                }];
            return items;
        },

        show: function(model) {
            ModifyBaseModel.modifySuccess=function(form, action){
                TreeModel.refreshTree();
                GridBaseModel.refresh();
            };
            ModifyBaseModel.show('修改命令', 'editCommand', 500, 200, this.getItems(model),model);
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
				{name: 'chinese'},
				{name: 'english'},
				{name: 'orderNum'},
				{name: 'display'},
				{name: 'enable'}
                    ];
           return fields;     
        },
        getColumns: function(){
            var columns=[
 				{header: "编号", width: 20, dataIndex: 'id', sortable: true},
 				{header: "版本", width: 20, dataIndex: 'version', sortable: true},
				{header: "中文名称", width: 20, dataIndex: 'chinese', sortable: true,editor:new Ext.form.TextField()},
				{header: "英文名称", width: 20, dataIndex: 'english', sortable: true},
				{header: "顺序号", width: 20, dataIndex: 'orderNum', sortable: true,editor:new Ext.form.TextField()},
				{header: "是否显示", width: 20, dataIndex: 'display', sortable: true},
				{header: "是否启用", width: 20, dataIndex: 'enable', sortable: true}
                        ];
            return columns;           
        },
        getGrid: function(){
            var pageSize=14;

            //修改单个属性回调
            GridBaseModel.updateAttrSuccess=function(response, opts){
                TreeModel.refreshTree();
                GridBaseModel.refresh();
            };    
            //添加特殊参数
            GridBaseModel.storeURLParameter="?orderCriteria=orderNum:ASC";
            if(currentId==-1){
                GridBaseModel.propertyCriteria=rootPropertyCriteria;
                GridBaseModel.loadStore=function(){
                    //不加载表格
                }
            }else{
                GridBaseModel.propertyCriteria=propertyCriteria;
            }
            GridBaseModel.setStoreBaseParams=function(store){
                store.on('beforeload',function(store){
                   store.baseParams = {propertyCriteria:GridBaseModel.propertyCriteria};
                });
            };
                
            var commands=["updatePart"];
            var tips=['修改(U)'];
            var callbacks=[GridBaseModel.modify];

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
        getTree: function(){
            TreeBaseModel.onClick=this.onClick;
            var tree = TreeBaseModel.getTree(treeDataUrl, currentName, "root", 'module');
            currentNode=TreeBaseModel.root;
            return tree;
        },
        refreshTree: function(){
            //重新加载当前节点
            currentNode.reload();
        },
        select: function(node, event, callback){     
            if(node.id.toString()=='root'){
                GridBaseModel.grid.setTitle("已选中【"+currentName+"】");
                GridBaseModel.propertyCriteria=rootPropertyCriteria;
                GridBaseModel.changeURL(contextPath, namespace, action);
                if(typeof(callback)=='function'){
                    callback();
                }
                return;
            }        
            var type=node.id.toString().split("-")[0];
            var id=node.id.toString().split("-")[1];
            
            //如果当前选择了模块，则需要刷新表格
            //如果当前选中了命令，则忽略刷新表格
            if(type=='module'){
                currentNode=node;                
                currentId=node.id;
                currentName=node.text;
                node.expand(false, true, function(){                    
                    if(currentNode.childNodes[0] && currentNode.childNodes[0].id.toString().split("-")[0]=='command'){
                        //切换到命令
                        action='edit-command';
                        propertyCriteria=commandPropertyCriteriaPre+id;                        
                    }else{
                        //切换到模块
                        action='edit-module';
                        propertyCriteria=modulePropertyCriteriaPre+id;
                    }     
                    GridBaseModel.grid.setTitle("已选中【"+currentName+"】");
                    GridBaseModel.propertyCriteria=propertyCriteria;
                    GridBaseModel.changeURL(contextPath, namespace, action);
                    if(typeof(callback)=='function'){
                        callback();
                    }
                },this);  
            }
        },
        onClick: function(node, event) {   
            TreeModel.select(node, event, function(){
                GridBaseModel.refresh();
            });
        }
    }
}();   
//左边为树右边为表格的编辑视图
EditModuleForm = function() {
    return {
        show: function() {
                 var frm = new Ext.Viewport({
                    layout : 'border',
                    items: [
                        TreeModel.getTree(),
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
    EditModuleForm.show();
});