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

var infoTypeId="-1";
    var rootNodeID="root";
    var rootNodeText="新闻类别";
    
    var namespace='info';
    var action='news';
    
    var lang='zh';
    //本页面特殊URL
    var selectInfoTypeStoreUrl=contextPath+'/info/info-type/store.action?lang=';
    
    //高级搜索
    AdvancedSearchModel = function() {
        return {
            //搜索表单
            getItems: function() {
                 var items = [
                    {
                        xtype: 'textfield',
                        id:'search_title',
                        fieldLabel: '标题'
                    },
                    {
                        xtype: 'textfield',
                        id:'search_content',
                        fieldLabel: '内容'
                    },
                    {
                        xtype: 'textfield',
                        id:'search_infoTypeName',
                        fieldLabel: '类别'
                    }];
                return items;
            },
            //点击搜索之后的回调方法
            callback : function(){               
                    var data=[];

                    var search_title=parent.Ext.getCmp('search_title').getValue();
                    if(search_title!=""){
                        search_title=' +title:'+search_title;
                        data.push(search_title);
                    }

                    var search_content=parent.Ext.getCmp('search_content').getValue();
                    if(search_content!=""){
                        search_content=' +content:'+search_content;
                        data.push(search_content);
                    }

                    var search_infoTypeName=parent.Ext.getCmp('search_infoTypeName').getValue();
                    if(search_infoTypeName!=""){
                        search_infoTypeName=' +infoTypeName:'+search_infoTypeName;
                        data.push(search_infoTypeName);
                    }
                    
                    AdvancedSearchBaseModel.search(data, "News");
            },
            show: function() {
                AdvancedSearchBaseModel.show('高级搜索', 'news', 420, 180, this.getItems(), this.callback);
            }
        };
    } ();
    //添加模型信息
    CreateModel = function() {
        return {
            getItems: function() {
                 var infoTypeSelector=new TreeSelector('model.infoType.infoTypeName','',selectInfoTypeStoreUrl+lang,rootNodeID,rootNodeText,"类别",'model.infoType.id','95%');
                 var items = [{
                            layout: 'form',
                            defaults: {
                                allowBlank: false,
                                anchor:"95%"
                            },
                            items:[{
                                    xtype:'textfield',
                                    cls : 'attr',
                                    name: 'model.title',
                                    fieldLabel: '标题',
                                    allowBlank: false,
                                    blankText : '标题不能为空'
                                 },{
                                    xtype: 'combo',
                                    store:choiceStore,
                                    emptyText:'请选择',
                                    mode:'remote',
                                    valueField:'value',
                                    displayField:'text',
                                    triggerAction:'all',
                                    forceSelection: true,
                                    editable:       false,
                                    cls : 'attr',
                                    hiddenName: 'model.enabled',
                                    fieldLabel: '启用',
                                    allowBlank: false,
                                    blankText : '启用不能为空'
                                },infoTypeSelector,
                                {
                                    xtype:'textfield',
                                    name: 'model.infoType.id',
                                    id:'model.infoType.id',
                                    hidden: true,
                                    hideLabel:true
                                },
                                {
                                    xtype: 'ckeditor',
                                    id: 'ckeditor',
                                    allowBlank: true,
                                    fieldLabel: '内容',
                                    name: 'model.content',
                                    CKConfig: {
                                            height : 250,
                                            width: 800
                                    }
                                }]
                    }];
                return items;
            },

            show: function() {
                CreateBaseModel.show('添加新闻', 'news', 980, 580, this.getItems());
            }
        };
    } ();
    //修改模型信息
    ModifyModel = function() {
        return {
            getItems: function(model) {
                 var infoTypeSelector=new TreeSelector('model.infoType.infoTypeName',model.infoTypeName,selectInfoTypeStoreUrl+lang,rootNodeID,rootNodeText,"类别",'model.infoType.id','95%');
                 var items = [{
                            layout: 'form',
                            defaults: {
                                allowBlank: false,
                                anchor:"95%"
                            },
                            items:[{
                                    xtype:'textfield',
                                    cls : 'attr',
                                    name: 'model.title',
                                    value: model.title,
                                    fieldLabel: '标题',
                                    allowBlank: false,
                                    blankText : '标题不能为空'
                                 },{
                                    xtype: 'combo',
                                    id:'enabled',
                                    store:choiceStore,
                                    emptyText:'请选择',
                                    mode:'remote',
                                    valueField:'value',
                                    displayField:'text',
                                    triggerAction:'all',
                                    forceSelection: true,
                                    editable:       false,
                                    cls : 'attr',
                                    hiddenName: 'model.enabled',
                                    value: model.enabled,
                                    fieldLabel: '启用',
                                    allowBlank: false,
                                    blankText : '启用不能为空'
                                },infoTypeSelector,
                                {
                                    xtype:'textfield',
                                    name: 'model.infoType.id',
                                    value: model.infoTypeId,
                                    id:'model.infoType.id',
                                    hidden: true,
                                    hideLabel:true
                                },
                                {
                                    xtype: 'ckeditor',
                                    allowBlank: true,
                                    fieldLabel: '内容',
                                    name: 'model.content',
                                    value: model.content,
                                    CKConfig: {	
                                            height : 250,
                                            width: 800
                                    }
                                }]
                    }];
                return items;
            },
            show: function(model) {
                ModifyBaseModel.prepareSubmit=function() {
                    var enabled=parent.Ext.getCmp('enabled').getValue();
                    if(enabled=="是"){
                        parent.Ext.getCmp('enabled').setValue("true");
                    }
                    if(enabled=="否"){
                        parent.Ext.getCmp('enabled').setValue("false");
                    }
                }
                ModifyBaseModel.show('修改新闻', 'news', 980, 580, this.getItems(model),model);
            }
        };
    } ();
    //显示模型详细信息
    DisplayModel = function() {
        return {
            getItems: function(model) {
                 var items = [{
                            layout: 'form',
                            defaults: {
                                readOnly:true,
                                fieldClass:'detail_field',
                                xtype:'textfield',
                                anchor:"95%"
                            },
                            items:[{
                                    value: model.title,
                                    fieldLabel: '标题'
                                 },
                                {
                                    value: model.enabled,
                                    fieldLabel: '启用'
                                },
                                {
                                    value: model.infoTypeName,
                                    fieldLabel: '类别'
                                },
                                {
                                    xtype: 'ckeditor',
                                    fieldLabel: '内容',
                                    value: model.content,
                                    CKConfig: {
                                            customConfig : contextPath+'/ckeditor/config.js',		
                                            toolbar: "Detail",
                                            height : 350,
                                            width: 800
                                    }
                                }]
                    }];
                return items;
            },
            show: function(model) {
                DisplayBaseModel.show('新闻详细信息', 'news', 980, 580, this.getItems(model));
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
				{name: 'title'},
				{name: 'username'},
				{name: 'orgname'},
				{name: 'infoTypeName'},
				{name: 'enabled'},
				{name: 'createTime'},
				{name: 'updateTime'}
			];
               return fields;     
            },
            getColumns: function(){
                var columns=[
 				{header: "编号", width: 10, dataIndex: 'id', sortable: true},
 				{header: "版本", width: 10, dataIndex: 'version', sortable: true},
				{header: "标题", width: 40, dataIndex: 'title', sortable: true,editor:new Ext.form.TextField()},
				{header: "类别", width: 20, dataIndex: 'infoTypeName', sortable: true},
				{header: "启用", width: 20, dataIndex: 'enabled', sortable: true},
				{header: "作者", width: 20, dataIndex: 'username', sortable: true},
				{header: "组织架构", width: 20, dataIndex: 'orgname', sortable: true},
				{header: "发布日期", width: 20, dataIndex: 'createTime', sortable: true},
				{header: "更新日期", width: 20, dataIndex: 'updateTime', sortable: true}
                            ];
                return columns;           
            },
            getGrid: function(){
                var pageSize=17;
                
                //添加特殊参数
                GridBaseModel.extraModifyParameters=function(){
                    return "&lang="+lang;
                };
                GridBaseModel.extraDetailParameters=function(){
                    return "&lang="+lang;
                };
                GridBaseModel.extraCreateParameters=function(){
                    return "?lang="+lang;
                };
                GridBaseModel.infoTypeId=infoTypeId;
                GridBaseModel.setStoreBaseParams=function(store){
                    store.on('beforeload',function(store){
                       store.baseParams = {queryString:GridBaseModel.queryString,search:GridBaseModel.search,infoTypeId:GridBaseModel.infoTypeId,lang:lang};
                    });
                };
                
                var commands=["create","delete","updatePart","retrieve","search","query","export"];
                var tips=['增加(C)','删除(R)','修改(U)','详细(D)','高级搜索(S)','显示全部(A)','导出(E)'];
                var callbacks=[GridBaseModel.create,GridBaseModel.remove,GridBaseModel.modify,GridBaseModel.detail,GridBaseModel.advancedsearch,GridBaseModel.showall,GridBaseModel.exportData];
            
                var grid=GridBaseModel.getGrid(contextPath, namespace, action, pageSize, this.getFields(), this.getColumns(), commands,tips,callbacks);   
                
                //设置标题
                grid.setTitle("已选中【"+rootNodeText+"】");
                
                return grid;
            }
        }
    } ();
    //左部树
    TreeModel = function(){
        return{
            getTree: function(){
                TreeBaseModel.onClick=this.onClick;
                return TreeBaseModel.getTree(selectInfoTypeStoreUrl+lang,rootNodeText,'root','infoType');
            },
            onClick: function(node, event) {
                node.expand(false, true);
                var id=node.id;
                var name=node.text;
                TreeModel.change(id,name);
                GridBaseModel.refresh();
            },
            change: function(id,name) {
                infoTypeId=id;
                rootNodeID=id;
                rootNodeText=name;
                GridBaseModel.grid.setTitle('已选中【'+rootNodeText+'】');
                GridBaseModel.infoTypeId=infoTypeId;
                //只要点击左边的树就自动退出搜索模式
                GridBaseModel.search=false;
            }  
        }
    }();
    //树和表格
    NewsPanel = function() {
        return {
            show: function() {
                 var tree=TreeModel.getTree();
                 var frm = new Ext.Viewport({
                    layout : 'border',
                    items: [{
                            region:'west',
                            width : 200,
                            labelWidth : 40,
                            labelAlign : 'right',
                            layout : 'form',
                            items:[
                                    {
                                        xtype: 'combo',
                                        width : 150,
                                        store:langStore,
                                        emptyText:'请选择',
                                        mode:'remote',
                                        valueField:'value',
                                        displayField:'text',
                                        triggerAction:'all',
                                        forceSelection: true,
                                        editable:       false,
                                        fieldLabel: '语言',
                                        listeners: {
                                                select: function(combo,record,number){								
                                                        lang=combo.getValue();
                                                        tree.loader.dataUrl=selectInfoTypeStoreUrl+lang;
                                                        tree.root.reload(
                                                            function(){
                                                                tree.root.expand(false, true);
                                                                TreeModel.onClick(tree.root.childNodes[0]);
                                                            },
                                                        tree);
                                                }
                                        }
                                    },
                                    tree
                            ]
                        },
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
    NewsPanel.show();
});