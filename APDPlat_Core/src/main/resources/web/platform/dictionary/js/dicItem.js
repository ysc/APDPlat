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

var dicId="1";
    var dicName="数据字典";
    
    var namespace='dictionary';
    var action='dic-item';

    var propertyCriteriaPre="dic.id:eq:";
    var propertyCriteria=propertyCriteriaPre+dicId;
    
    //本页面特殊URL
    var treeStoreUrl=contextPath+'/'+namespace+'/'+action+'/store.action';
    //添加模型信息
    CreateModel = function() {
        return {
            getItems: function() {
                 var items = [{
                        xtype:'textfield',
                        cls : 'attr',
                        name: 'model.code',
                        fieldLabel: '编码',
                        allowBlank: false,
                        blankText : '编码不能为空'
                    },
                    {
                        xtype:'textfield',
                        cls : 'attr',
                        name: 'model.name',
                        fieldLabel: '名称',
                        allowBlank: false,
                        blankText : '名称不能为空'
                    },{
                        xtype:'textfield',
                        cls : 'attr',
                        name: 'model.orderNum',
                        fieldLabel: '顺序'
                    }];
                return items;
            },
            
            show: function() {
                CreateBaseModel.prepareSubmit=function() {
                    GridBaseModel.createURLParameter='?model.dic.id='+dicId;
                };                
                CreateBaseModel.show('添加'+dicName, 'dicItem', 550, 170, this.getItems());
            }
        };
    } ();
    //修改模型信息
    ModifyModel = function() {
        return {
            getItems: function(model) {
                 var items = [{
                        xtype:'textfield',
                        cls : 'attr',
                        name: 'model.code',
                        fieldLabel: '编码',
                        allowBlank: false,
                        blankText : '编码不能为空',
                        value: model.code
                    },
                    {
                        xtype:'textfield',
                        cls : 'attr',
                        name: 'model.name',
                        fieldLabel: '名称',
                        allowBlank: false,
                        blankText : '名称不能为空',
                        value:model.name
                    },{
                        xtype:'textfield',
                        cls : 'attr',
                        name: 'model.orderNum',
                        fieldLabel: '顺序',
                        value:model.orderNum
                    }];
                return items;
            },

            show: function(model) {
                ModifyBaseModel.show('修改'+dicName, 'dicItem', 550, 170, this.getItems(model),model);
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
                                    {name: 'code'},
                                    {name: 'name'},
                                    {name: 'orderNum'}
                        ];
               return fields;     
            },
            getColumns: function(){
                var columns=[
                                    {header: "编号", width: 20, dataIndex: 'id', sortable: true},
                                    {header: "版本", width: 20, dataIndex: 'version', sortable: true},
                                    {header: "编码", width: 40, dataIndex: 'code', sortable: true,editor:new Ext.form.TextField()},
                                    {header: "名称", width: 40, dataIndex: 'name', sortable: true,editor:new Ext.form.TextField()},
                                    {header: "顺序", width: 20, dataIndex: 'orderNum', sortable: true,editor:new Ext.form.TextField()}
                            ];
                return columns;           
            },
            getGrid: function(){
                var pageSize=17;

                //添加特殊参数
                GridBaseModel.storeURLParameter="?orderCriteria=orderNum:ASC";
                GridBaseModel.propertyCriteria=propertyCriteria;
                GridBaseModel.setStoreBaseParams=function(store){
                    store.on('beforeload',function(store){
                       store.baseParams = {propertyCriteria:GridBaseModel.propertyCriteria};
                    });
                };
                
                var commands=["create","delete","updatePart"];
                var tips=['增加(C)','删除(R)','修改(U)'];
                var callbacks=[GridBaseModel.create,GridBaseModel.remove,GridBaseModel.modify];
            
                var grid=GridBaseModel.getGrid(contextPath, namespace, action, pageSize, this.getFields(), this.getColumns(), commands, tips, callbacks);

                return grid;
            }
        }
    } ();
    //左部树
    TreeModel = function(){
        return{
            getTree: function(){
                TreeBaseModel.onClick=this.onClick;
                return TreeBaseModel.getTree(treeStoreUrl,dicName,'root','dicItem');
            },
            onClick: function(node, event) {
                node.expand(false, true);
                var id=node.id;
                var name=node.text;
                TreeModel.change(id,name);
                //只有在选择叶子节点时才刷新表格
                if(node.isLeaf()){
                    GridBaseModel.refresh();
                }
            },
            change: function(id,name) {
                dicId=id;
                dicName=name;
                GridBaseModel.grid.setTitle('已选中【'+dicName+'】');
                GridBaseModel.propertyCriteria=propertyCriteriaPre+dicId;
            }  
        }
    }();
    //树和表格
    DicPanel = function() {
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
    DicPanel.show();
});