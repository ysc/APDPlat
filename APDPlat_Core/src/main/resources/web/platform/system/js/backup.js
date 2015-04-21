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

var namespace='system';
    var action='backup';

    var storeURL=contextPath+'/'+namespace+'/'+action+'/query.action';
    var restoreURL=contextPath+'/'+namespace+'/'+action+'/restore.action';
    var backupURL=contextPath+'/'+namespace+'/'+action+'/backup.action';
    var clearTaskURL=contextPath+'/'+namespace+'/'+action+'/clearTask.action';
    var setTaskURL=contextPath+'/'+namespace+'/'+action+'/setTask.action';
    var downloadURL=contextPath+'/'+namespace+'/'+action+'/download.action';

    Backup = function() {
        return {
            show: function(model) {
                var frm = new Ext.form.FormPanel({
                    applyTo : 'backup-div',
                    labelAlign: 'left',
                    buttonAlign: 'center',
                    bodyStyle: 'padding:5px',
                    frame: true,//圆角和浅蓝色背景
                    labelWidth: 130,
                    defaultType: 'textfield',
                    defaults: {
                        anchor: '95%'
                    },
                    items:[{
                        xtype: 'fieldset',
                        title: '已有备份',
                        collapsible: false,
                        items: [{
                                layout:'column',
                                defaults: {width: 250},
                                items:[{
                                    columnWidth:.5,
                                    layout: 'form',

                                     items: [{
                                                xtype: 'combo',
                                                store:existBackupStore,
                                                emptyText:'请选择',
                                                mode:'remote',
                                                triggerAction:'all',
                                                forceSelection: true,
                                                editable:       false,
                                                valueField:'value',
                                                displayField:'text',
                                                cls : 'attr',
                                                id: 'date',
                                                fieldLabel: '存在的备份时间点'
                                            }]
                                },{
                                    columnWidth:.5,
                                    layout: 'form',

                                    items: [new Ext.Button({  
                                                text: '下载备份文件',
                                                iconCls:'download',
                                                scope: this,
                                                handler: function() {
                                                    var date=Ext.getCmp('date').getValue();
                                                    if(""==date){
                                                        parent.Ext.MessageBox.alert('提示', "请选择需要下载的备份文件的时间点");
                                                        return;
                                                    }
                                                    parent.Ext.Ajax.request({
                                                            url : downloadURL,
                                                            waitTitle: '请稍等',
                                                            waitMsg: '正在下载备份文件……',
                                                            params : {date : date},
                                                            method : 'POST',
                                                            success : function(response,opts){
                                                                    var path = response.responseText;
                                                                    //contextPath定义在引用了此JS的页面中
                                                                    path=this.contextPath+path;
                                                                    window.open(path,'_blank','width=1,height=1,toolbar=no,menubar=no,location=no');
                                                            },
                                                            failure : function(response,options){
                                                                    parent.Ext.ux.Toast.msg('操作提示：', "下载失败");
                                                            }
                                                    });
                                                }
                                        })]
                                }]
                            }]
                    },{
                            xtype: 'fieldset',
                            title: '定时备份状态',
                            collapsible: false,
                            defaults: {
                                anchor:"95%"
                            },
                            items: [{
                                xtype:'textfield',
                                value:model.state,
                                id:'state',
                                readOnly:true,
                                fieldLabel:'任务状态'
                            }]
                    },{
                            xtype: 'fieldset',
                            title: '定时备份时间（24小时制）',
                            collapsible: false,
                            items: [{
                                        xtype:'numberfield',
                                        id:'hour',
                                        value:model.hour,
                                        maxValue: 23,
                                        minValue: 0,
                                        fieldLabel:'小时（0-23）'
                                },{
                                        xtype:'numberfield',
                                        id:'minute',
                                        value:model.minute,
                                        maxValue: 59,
                                        minValue: 0,
                                        fieldLabel:'分钟（0-59）'
                                }]
                    }],

                    buttons: [getCommand()]
                });
            }

        };
    } ();

    function getCommand(){
        var toolbar = new Ext.Toolbar();  
        if(parent.isGranted(namespace,action,"backup")){     
             toolbar.add(new Ext.Button({  
                        text: '手动备份',
                        iconCls:'backup',
                        scope: this,
                        handler: function() {
                            parent.Ext.Ajax.request({
                                    url : backupURL,
                                    waitTitle: '请稍等',
                                    waitMsg: '正在备份数据库……',
                                    method : 'POST',
                                    success : function(response,options){
                                            var res=response.responseText;
                                            if(res=="true"){
                                                existBackupStore.reload();
                                                parent.Ext.ux.Toast.msg('操作提示：', "备份成功");
                                            }else{
                                                parent.Ext.ux.Toast.msg('操作提示：', "备份失败");
                                            }
                                    },
                                    failure : function(response,options){
                                            parent.Ext.ux.Toast.msg('操作提示：', "备份失败");
                                    }
                            });
                        }
                }));  
        }  
        if(parent.isGranted(namespace,action,"restore")){     
             toolbar.add(new Ext.Button({  
                        text: '手动恢复',
                        iconCls:'restore',
                        scope: this,
                        handler: function() {
                            var date=Ext.getCmp('date').getValue();
                            if(""==date){
                                parent.Ext.MessageBox.alert('提示', "请选择需要恢复的时间点");
                                return;
                            }
                            parent.Ext.Ajax.request({
                                    url : restoreURL,
                                    waitTitle: '请稍等',
                                    waitMsg: '正在恢复数据库……',
                                    params : {date : date},
                                    method : 'POST',
                                    success : function(response,options){
                                            var res=response.responseText;
                                            if(res=="true"){
                                                parent.Ext.ux.Toast.msg('操作提示：', "恢复成功");
                                            }else{
                                                parent.Ext.ux.Toast.msg('操作提示：', "恢复失败");
                                            }
                                    },
                                    failure : function(response,options){
                                            parent.Ext.ux.Toast.msg('操作提示：', "恢复失败");
                                    }
                            });
                        }
                }));  
        }  
        if(parent.isGranted(namespace,action,"clearTask")){     
             toolbar.add(new Ext.Button({  
                        text: '取消定时备份任务',
                        iconCls:'cancel',
                        scope: this,
                        handler: function() {
                            parent.Ext.Ajax.request({
                                    url : clearTaskURL,
                                    waitTitle: '请稍等',
                                    waitMsg: '取消定时备份任务……',
                                    method : 'POST',
                                    success : function(response,options){
                                            var res=response.responseText;
                                            parent.Ext.ux.Toast.msg('操作提示：',res);  
                                            Ext.getCmp('state').setValue("无定时备份任务");
                                            Ext.getCmp('hour').reset();
                                            Ext.getCmp('minute').reset();
                                    }
                            });
                        }
                }));  
        }  
        if(parent.isGranted(namespace,action,"setTask")){     
             toolbar.add(new Ext.Button({  
                        text: '设置定时备份任务',
                        iconCls:'create',
                        scope: this,
                        handler: function() {
                            
                            hour=Ext.getCmp('hour').getValue();
                            minute=Ext.getCmp('minute').getValue();
                            if(hour.toString().trim()==""||minute.toString().trim()==""){
                                parent.Ext.ux.Toast.msg('操作提示：',"定时备份时间不能为空");
                                return false;
                            }
                            parent.Ext.Ajax.request({
                                    url : setTaskURL,
                                    waitTitle: '请稍等',
                                    waitMsg: '设置定时备份任务……',
                                    params : {
                                        hour : hour,
                                        minute : minute
                                    },
                                    method : 'POST',
                                    success : function(response,options){
                                            var res=response.responseText;
                                            parent.Ext.ux.Toast.msg('操作提示：',res);  
                                            Ext.getCmp('state').setValue(res);
                                    }
                            });
                        }
                }));  
        }  
        return toolbar;
    }
    
Ext.onReady(function(){
    parent.Ext.Ajax.request({
        url : storeURL,
        waitTitle: '请稍等',
        waitMsg: '正在获取数据……',
        method : 'POST',
        success : function(response,options){
            var data=response.responseText;
            //返回的数据是对象，在外层加个括号才能正确执行eval
            var model=eval('(' + data + ')');
            Backup.show(model);
        }
    });
});