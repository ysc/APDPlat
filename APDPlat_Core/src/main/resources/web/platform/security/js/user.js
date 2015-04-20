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

//orgId==-1或orgId<0代表为根节点，不加过滤条件
    var orgId="-1";
    var rootNodeID="root";
    var rootNodeText="组织架构";

    var namespace='security';
    var action='user';

    var roleSelector;
    var positionSelector;

    //本页面特殊URL
    var selectOrgStoreURL=contextPath+'/security/org/store.action';
    var selectRoleStoreURL=contextPath + '/security/role/store.action?recursion=true';
    var selectPositionURL=contextPath + '/security/position/store.action?recursion=true';
    var selectUserGroupURL=contextPath + '/security/user-group/store.action';
    var resetURL=contextPath+'/'+namespace+'/'+action+'/reset.action';
    var reportURL=contextPath+'/'+namespace+'/'+action+'/report.action';
     
    //高级搜索
    AdvancedSearchModel = function() {
        return {
            //搜索表单
            getItems : function(){
                var items=[{
                                xtype: 'textfield',
                                id:'search_username',
                                fieldLabel: '账号'
                            },
                            {
                                xtype: 'textfield',
                                id:'search_realName',
                                fieldLabel: '姓名'
                            },
                            new TreeSelector('search_orgName','',selectOrgStoreURL,rootNodeID,rootNodeText,"组织架构名称",'model.org.id','95%'),
                            {
                                xtype:'textfield',
                                name: 'model.org.id',
                                id:'model.org.id',
                                hidden: true,
                                hideLabel:true
                            }
                        ];
                return items;
            },
            //点击搜索之后的回调方法
            callback : function(){               
                    var data=[];

                    var search_username=parent.Ext.getCmp('search_username').getValue();
                    if(search_username!=""){
                        search_username=' +username:'+search_username;
                        data.push(search_username);
                    }

                    var search_realName=parent.Ext.getCmp('search_realName').getValue();
                    if(search_realName!=""){
                        search_realName=' +realName:'+search_realName;
                        data.push(search_realName);
                    }

                    var search_orgName=parent.Ext.getCmp('search_orgName').getValue();
                    if(search_orgName!=""){
                        search_orgName=' +org_orgName:'+search_orgName;
                        data.push(search_orgName);
                    }

                    AdvancedSearchBaseModel.search(data, "User");
            },
            
            show: function() {
                AdvancedSearchBaseModel.show('高级搜索',"user", 420, 170, this.getItems(), this.callback);
            }
        };
    } ();
    //添加模型信息
    CreateModel = function() {
        return {
            getItems: function() {
                orgSelector=new TreeSelector('model.org.orgName','',selectOrgStoreURL,rootNodeID,rootNodeText,"组织架构",'model.org.id','95%');
               
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
                 
                var positionLoader = new parent.Ext.tree.TreeLoader({
                    dataUrl:selectPositionURL
                });
                positionSelector = new parent.Ext.ux.tree.CheckTreePanel({
                            title : '',
                            id : "positionSelector",
                            bubbleCheck:'none' ,
                            cascadeCheck:'all',
                            deepestOnly:'true',
                            rootVisible : false,
                            loader : positionLoader,
                            root : new Ext.tree.AsyncTreeNode({
                                text:'岗位',
                                id : 'root',
                                expanded : true
                            })
                 });
                 positionSelector.reset=function(){
                     this.clearValue();
                 };
                 var userGroupLoader = new parent.Ext.tree.TreeLoader({
                    dataUrl:selectUserGroupURL
                });
                userGroupSelector = new parent.Ext.ux.tree.CheckTreePanel({
                            title : '',
                            id : "userGroupSelector",
                            rootVisible : false,
                            loader : userGroupLoader,
                            root : new Ext.tree.AsyncTreeNode({
                                text:'用户组',
                                id : 'root',
                                expanded : true
                            })
                 });
                 userGroupSelector.reset=function(){
                     this.clearValue();
                 };
                 var items=[{
                                layout: 'form',
                                items:[{
                                            xtype: 'fieldset',
                                            id:'baseInfo',
                                            title: '基本信息',
                                            collapsible: true,
                                            defaults: {
                                                allowBlank: false,
                                                anchor: '95%'
                                            },
                                            items: [
                                                {
                                                layout:'column',
                                                defaults: {width: 250},
                                                items:[{
                                                    columnWidth:.5,
                                                    layout: 'form',
                                                    defaultType: 'textfield',
                                                    defaults: {
                                                        allowBlank: false,
                                                        anchor:"90%"
                                                    },

                                                     items: [{
                                                                cls : 'attr',
                                                                name: 'model.username',
                                                                fieldLabel: '账号',
                                                                blankText : '账号不能为空'
                                                            },
                                                            {
                                                                cls : 'attr',
                                                                name: 'model.realName',
                                                                fieldLabel: '姓名',
                                                                blankText : '姓名不能为空'
                                                            },
                                                            {
                                                                xtype: 'combo',
                                                                store:userStateStore,
                                                                emptyText:'请选择',
                                                                mode:'remote',
                                                                valueField:'value',
                                                                displayField:'text',
                                                                triggerAction:'all',
                                                                forceSelection: true,
                                                                editable:       false,
                                                                cls : 'attr',
                                                                hiddenName: 'model.enabled',
                                                                fieldLabel: '状态',
                                                                allowBlank: false,
                                                                blankText : '状态不能为空'
                                                            }]
                                                },{
                                                    columnWidth:.5,
                                                    layout: 'form',
                                                    defaultType: 'textfield',
                                                    defaults: {
                                                        allowBlank: false,
                                                        anchor:"90%"
                                                    },

                                                    items: [{
                                                                cls : 'attr',
                                                                id:'password',
                                                                name: 'model.password',
                                                                fieldLabel: '密码',
                                                                blankText : '密码不能为空',
                                                                inputType : 'password'
                                                            },
                                                            {
                                                                cls : 'attr',
                                                                name: 'confirmPassword',
                                                                id: 'confirmPassword',
                                                                fieldLabel: '确认密码',
                                                                blankText : '确认密码不能为空',
                                                                inputType : 'password'
                                                            },
                                                            orgSelector,
                                                            {
                                                                xtype:'textfield',
                                                                name: 'model.org.id',
                                                                id:'model.org.id',
                                                                hidden: true,
                                                                hideLabel:true
                                                            }]
                                                        }]
                                            },
                                            {
                                                xtype:'textfield',
                                                allowBlank: true,
                                                name: 'model.des',
                                                fieldLabel: '备注',
                                                anchor:"95%"
                                        }]
                                    },{
                                        xtype: 'fieldset',
                                        id:'userGroupSelectorSet',
                                        title: '选择用户组',
                                        collapsible: true,
                                        items: [
                                            userGroupSelector,{
                                            xtype: 'textfield',
                                            name: 'userGroups',
                                            id:'userGroups',
                                            hidden: true,
                                            hideLabel:true
                                        }]
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
                                    },{
                                        xtype: 'fieldset',
                                        id:'positionSelectorSet',
                                        title: '选择岗位',
                                        collapsible: true,
                                        items: [
                                            positionSelector,{
                                            xtype: 'textfield',
                                            name: 'positions',
                                            id:'positions',
                                            hidden: true,
                                            hideLabel:true
                                        }]
                                    }]
                    }];
                return items;
            },

            show: function() {
                //指定是否应该提交数据的规则
                CreateBaseModel.shouldSubmit=function(){
                    var password=parent.Ext.getCmp('password').getValue();
                    var confirmPassword=parent.Ext.getCmp('confirmPassword').getValue();
                    if(confirmPassword!=password){
                        parent.Ext.MessageBox.alert('提示', "密码输入不一致");
                        return false;
                    }else{
                        parent.Ext.getCmp('roles').setValue(roleSelector.getValue());
                        parent.Ext.getCmp('positions').setValue(positionSelector.getValue());
                        parent.Ext.getCmp('userGroups').setValue(userGroupSelector.getValue());
                        return true;
                    }
                };
                CreateBaseModel.show('添加用户', 'user', 800, 460, this.getItems());
            }
        };
    } ();
    //修改模型信息
    ModifyModel = function() {
        return {
            getItems: function(model) {
                var orgSelector=new TreeSelector('model.org.orgName',model.orgName,selectOrgStoreURL,rootNodeID,rootNodeText,"组织架构",'model.org.id','95%');
                
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
                
                var positionLoader = new parent.Ext.tree.TreeLoader({
                    dataUrl:selectPositionURL
                });
                positionSelector = new parent.Ext.ux.tree.CheckTreePanel({
                            title : '',
                            id : "positionSelector",
                            deepestOnly:'true',
                            rootVisible : false,
                            loader : positionLoader,
                            root : new Ext.tree.AsyncTreeNode({
                                text:'岗位',
                                id : 'root',
                                expanded : true
                            })
                 });
                positionSelector.reset=function(){
                    this.clearValue();
                };
                positionLoader.on("load",function(){
                    //在数据装载完成并展开树之后再设值
                    positionSelector.getRootNode().expand(true,true);
                    if(model.positions!=undefined && model.positions.toString().length>1){
                        positionSelector.setValue(model.positions);
                    }
                    positionSelector.bubbleCheck='none';
                    positionSelector.cascadeCheck='all';
                });
                
                 var userGroupLoader = new parent.Ext.tree.TreeLoader({
                    dataUrl:selectUserGroupURL
                });
                userGroupSelector = new parent.Ext.ux.tree.CheckTreePanel({
                            title : '',
                            id : "userGroupSelector",
                            rootVisible : false,
                            loader : userGroupLoader,
                            root : new Ext.tree.AsyncTreeNode({
                                text:'用户组',
                                id : 'root',
                                expanded : true
                            })
                 });
                 userGroupSelector.reset=function(){
                     this.clearValue();
                 };                 
                userGroupLoader.on("load",function(){
                    userGroupSelector.setValue(model.userGroups);
                });
                 var items=[{
                                layout: 'form',
                                items:[{
                                            xtype: 'fieldset',
                                            id:'baseInfo',
                                            title: '基本信息',
                                            collapsible: true,
                                            defaults: {
                                                allowBlank: false,
                                                anchor: '95%'
                                            },
                                            items: [{
                                                    layout:'column',
                                                    defaults: {width: 250},
                                                    items:[{
                                                        columnWidth:.5,
                                                        layout: 'form',
                                                        defaultType: 'textfield',
                                                        defaults: {
                                                            allowBlank: false,
                                                            anchor:"90%"
                                                        },

                                                         items: [{
                                                                    readOnly:true,
                                                                    fieldClass:'detail_field',
                                                                    name: 'model.username',
                                                                    value: model.username,
                                                                    fieldLabel: '账号'
                                                                },{
                                                                    xtype:'textfield',
                                                                    name: 'model.realName',
                                                                    value: model.realName,
                                                                    fieldLabel: '姓名',
                                                                    allowBlank: false,
                                                                    blankText : '姓名不能为空'
                                                                }]
                                                    },{
                                                        columnWidth:.5,
                                                        layout: 'form',
                                                        defaultType: 'textfield',
                                                        defaults: {
                                                            allowBlank: false,
                                                            anchor:"90%"
                                                        },

                                                        items: [orgSelector,
                                                                {
                                                                    xtype:'textfield',
                                                                    value: model.orgId,
                                                                    name: 'model.org.id',
                                                                    id:'model.org.id',
                                                                    hidden: true,
                                                                    hideLabel:true
                                                                },{
                                                                    id:'state',
                                                                    xtype: 'combo',
                                                                    store:userStateStore,
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
                                                                    fieldLabel: '状态',
                                                                    allowBlank: false,
                                                                    blankText : '状态不能为空'
                                                                }]
                                                    }]
                                                },
                                                {
                                                    xtype:'textfield',
                                                    allowBlank: true,
                                                    name: 'model.des',
                                                    value: model.des,
                                                    fieldLabel: '备注',
                                                    anchor:"95%"
                                                }
                                            ]
                                        },{
                                            xtype: 'fieldset',
                                            id:'userGroupSelectorSet',
                                            title: '选择用户组',
                                            collapsible: true,
                                            items: [userGroupSelector,{
                                                xtype: 'textfield',
                                                name: 'userGroups',
                                                id:'userGroups',
                                                hidden: true,
                                                hideLabel:true
                                            }]
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
                                        },{
                                            xtype: 'fieldset',
                                            id:'positionSelectorSet',
                                            title: '选择岗位',
                                            collapsible: true,
                                            items: [positionSelector,{
                                                xtype: 'textfield',
                                                name: 'positions',
                                                id:'positions',
                                                hidden: true,
                                                hideLabel:true
                                            }]
                                        }]
                    }];
                return items;
            },

            show: function(model) {
                ModifyBaseModel.prepareSubmit=function() {
                    parent.Ext.getCmp('roles').setValue(roleSelector.getValue());
                    parent.Ext.getCmp('positions').setValue(positionSelector.getValue());
                    parent.Ext.getCmp('userGroups').setValue(userGroupSelector.getValue());
                    if("启用"==parent.Ext.getCmp('state').getValue()){
                        parent.Ext.getCmp('state').setValue("true");
                    }
                    if("停用"==parent.Ext.getCmp('state').getValue()){
                        parent.Ext.getCmp('state').setValue("false");
                    }
                }
                ModifyBaseModel.show('修改用户', 'user', 800, 410, this.getItems(model),model);
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
				{name: 'username'},
				{name: 'realName'},
				{name: 'enabled'},
				{name: 'roles'},
				{name: 'positions'},
				{name: 'orgName'},
				{name: 'des'}
			];
               return fields;     
            },
            getColumns: function(){
                var columns=[
 				{header: "编号", width: 10, dataIndex: 'id', sortable: true},
 				{header: "版本", width: 10, dataIndex: 'version', sortable: true},
				{header: "账号", width: 20, dataIndex: 'username', sortable: true},
				{header: "姓名", width: 20, dataIndex: 'realName', sortable: true,editor:new Ext.form.TextField()},
				{header: "状态", width: 20, dataIndex: 'enabled', sortable: true,editor:{
                                                                    xtype: 'combo',
                                                                    store:userStateStore,
                                                                    emptyText:'请选择',
                                                                    mode:'remote',
                                                                    valueField:'value',
                                                                    displayField:'text',
                                                                    triggerAction:'all',
                                                                    forceSelection: true,
                                                                    editable:       false
                                                                }},
				{header: "拥有角色", width: 40, dataIndex: 'roles', sortable: true},
				{header: "拥有岗位", width: 40, dataIndex: 'positions', sortable: true},
				{header: "组织架构", width: 40, dataIndex: 'orgName', sortable: true},
				{header: "描述", width: 40, dataIndex: 'des', sortable: true,editor:new Ext.form.TextField()}
                            ];
                return columns;           
            },
            getGrid: function(){
                var pageSize=17;
                
                //添加特殊参数
                GridBaseModel.orgId=orgId;
                GridBaseModel.setStoreBaseParams=function(store){
                    store.on('beforeload',function(store){
                       store.baseParams = {queryString:GridBaseModel.queryString,search:GridBaseModel.search,orgId:GridBaseModel.orgId};
                    });
                };
                
                var commands=["create","delete","updatePart","search","query","export","reset","report"];
                var tips=['增加(C)','删除(R)','修改(U)','高级搜索(S)','显示全部(A)','导出(E)',"重置密码(Z)","图形报表"];
                var callbacks=[GridBaseModel.create,GridBaseModel.remove,GridBaseModel.modify,GridBaseModel.advancedsearch,GridBaseModel.showall,GridBaseModel.exportData,GridModel.reset,GridModel.report];
                
                var grid=GridBaseModel.getGrid(contextPath, namespace, action, pageSize, this.getFields(), this.getColumns(), commands,tips,callbacks);   
         
                //设置标题
                grid.setTitle("已选中【"+rootNodeText+"】");
                
                return grid;
            },
            report: function(){
                var win = new parent.Ext.Window({
                    title: "用户报表",
                    maximizable:true,
                    width:800,
                    height:600,
                    plain: true,
                    closable: true,
                    frame: true,
                    layout: 'fit',
                    border: false,
                    modal: true,
                    items:[new parent.Ext.form.FormPanel({                    
                            labelAlign: 'left',
                            buttonAlign: 'center',
                            bodyStyle: 'padding:5px',
                            frame: true,//圆角和浅蓝色背景
                            autoScroll:true,

                            autoLoad: reportURL,

                            buttons: [{
                                text: '关闭',
                                iconCls:'cancel',
                                scope: this,
                                handler: function() {
                                    win.close();
                                }
                            }],
                             keys:[{
                                 key : Ext.EventObject.ENTER,
                                 fn : function() {
                                    win.close();
                                 },
                                 scope : this
                             }]
                        })]
                });
                win.show();
            },
            reset: function(){
                var idList=GridBaseModel.getIdList();
                if(idList.length<1){
                    parent.Ext.ux.Toast.msg('操作提示：','请选择要进行操作的记录');  
                    return ;
                }
                parent.Ext.MessageBox.confirm("操作提示：","确实要对所选的用户执行密码重置操作吗？",function(button,text){
                    if(button == "yes"){
                        parent.Ext.Msg.prompt('操作提示', '请输入重置密码:', function(btn, text){
                            if (btn == 'ok'){
                                    if(text.toString()==null||text.toString().trim()==""){
                                        parent.Ext.ux.Toast.msg('操作提示：','密码不能为空'); 
                                    }else{
                                         GridModel.resetPassword(idList.join(','),text);
                                    }
                            };
                        });
                    }
                });
            },
            resetPassword: function(ids,password){
                parent.Ext.Ajax.request({
                    url : resetURL+'?time='+new Date().toString(),
                    waitTitle: '请稍等',
                    waitMsg: '正在重置密码……',
                    params : {
                        ids : ids,
                        password : password
                    },
                    method : 'POST',
                    success : function(response,opts){
                        var data=response.responseText;
                        parent.Ext.ux.Toast.msg('操作提示：','{0}',data);  
                    }
                });
            }
        }
    } ();
    //左部树
    TreeModel = function(){
        return{
            getTree: function(){
                TreeBaseModel.onClick=this.onClick;
                return TreeBaseModel.getTree(selectOrgStoreURL,rootNodeText,'root','user');
            },
            onClick: function(node, event) {
                node.expand(false, true);
                var id=node.id;
                var name=node.text;
                TreeModel.change(id,name);
                GridBaseModel.refresh();
            },
            change: function(id,name) {
                orgId=id;
                rootNodeID=id;
                rootNodeText=name;
                GridBaseModel.grid.setTitle('已选中【'+rootNodeText+'】');
                GridBaseModel.orgId=orgId;
                //只要点击左边的树就自动退出搜索模式
                GridBaseModel.search=false;
            }  
        }
    }();
    //树和表格
    UserPanel = function() {
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
        UserPanel.show();
    });
