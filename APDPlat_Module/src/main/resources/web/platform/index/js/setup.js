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

var namespace='index'; 
    var action='setup';
    
    var storeURL=contextPath+'/'+namespace+'/'+action+'/query.action';
    var rebuildAllURL=contextPath+'/'+namespace+'/'+action+'/rebuidAll.action';
    var clearTaskURL=contextPath+'/'+namespace+'/'+action+'/clearTask.action';
    var setTaskURL=contextPath+'/'+namespace+'/'+action+'/setTask.action';

    var  hour=-1;
    var  minute=-1;
    SetupForm = function() {
        return {
            show: function(model) {
                 var frm = new Ext.form.FormPanel({
                    applyTo : 'grid-div',
                    height:240,
                    autoWidth:true,
                    buttonAlign: 'center',
                    bodyStyle: 'padding:5px',
                    frame: true,//圆角和浅蓝色背景
                    labelWidth: 140,
                    items: [{
                            xtype: 'fieldset',
                            title: '定时重建状态',
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
                            title: '定时重建时间（24小时制）',
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
        if(parent.isGranted(namespace,action,"rebuidAll")){     
             toolbar.add(new Ext.Button({  
                        text: '手动重建索引',
                        iconCls:'setup',
                        scope: this,
                        handler: function() {
                            parent.Ext.Ajax.request({
                                    url : rebuildAllURL,
                                    waitTitle: '请稍等',
                                    waitMsg: '正在重建索引……',
                                    method : 'POST',
                                    success : function(response,options){
                                            var res=response.responseText;
                                            parent.Ext.ux.Toast.msg('操作提示：',res);  
                                    }
                            });
                        }
                }));  
        }  
        if(parent.isGranted(namespace,action,"clearTask")){     
             toolbar.add(new Ext.Button({  
                        text: '取消定时重建任务',
                        iconCls:'cancel',
                        scope: this,
                        handler: function() {
                            parent.Ext.Ajax.request({
                                    url : clearTaskURL,
                                    waitTitle: '请稍等',
                                    waitMsg: '取消定时重建任务……',
                                    method : 'POST',
                                    success : function(response,options){
                                            var res=response.responseText;
                                            parent.Ext.ux.Toast.msg('操作提示：',res);  
                                            Ext.getCmp('state').setValue("无定时重建任务");
                                            Ext.getCmp('hour').reset();
                                            Ext.getCmp('minute').reset();
                                    }
                            });
                        }
                }));  
        }  
        if(parent.isGranted(namespace,action,"setTask")){     
             toolbar.add(new Ext.Button({  
                        text: '设置定时重建任务',
                        iconCls:'create',
                        scope: this,
                        handler: function() {
                            
                            hour=Ext.getCmp('hour').getValue();
                            minute=Ext.getCmp('minute').getValue();
                            if(hour.toString().trim()==""||minute.toString().trim()==""){
                                parent.Ext.ux.Toast.msg('操作提示：',"定时重建时间不能为空");
                                return false;
                            }
                            parent.Ext.Ajax.request({
                                    url : setTaskURL,
                                    waitTitle: '请稍等',
                                    waitMsg: '设置定时重建任务……',
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
                        SetupForm.show(model);
                    }
                });
    });