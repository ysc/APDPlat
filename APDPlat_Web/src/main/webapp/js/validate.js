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

// 购买产品
    BuyModel = function() {
        return {
            getForm: function(requestCode) {
                 var frm = new parent.Ext.form.FormPanel({
                    labelAlign: 'left',
                    buttonAlign: 'center',
                    bodyStyle: 'padding:5px',
                    frame: true,//圆角和浅蓝色背景
                    labelWidth: 100,
                    items: [{
                            xtype: 'fieldset',
                            title: '机器码',
                            collapsible: false,

                            items: [{
                                xtype:'textfield',
                                anchor: '95%',
                                cls : 'attr',
                                name: 'model.name',
                                value: requestCode,
                                fieldLabel: '您的机器码',
                                readOnly:true
                            }]
                        },{
                            xtype: 'fieldset',
                            title: '在线购买',
                            collapsible: false,

                            items: [{
                                    xtype:'textfield',
                                    anchor: '95%',
                                    cls : 'attr',
                                    name: 'model.name',
                                    fieldLabel: '您的姓名',
                                    allowBlank: false,
                                    blankText : '您的姓名不能为空'
                                },
                                {
                                    xtype:'textfield',
                                    anchor: '95%',
                                    cls : 'attr',
                                    name: 'model.unit',
                                    fieldLabel: '您的单位',
                                    allowBlank: false,
                                    blankText : '您的单位不能为空'
                                },
                                {
                                    xtype:'numberfield',
                                    anchor: '95%',
                                    cls : 'attr',
                                    name: 'model.phone',
                                    fieldLabel: '您的手机',
                                    allowBlank: false,
                                    blankText : '您的手机不能为空'
                                }]
                        },{
                            xtype: 'fieldset',
                            title: '离线购买',
                            collapsible: false,

                            items: [{
                                    xtype:'panel',
                                    anchor: '95%',
                                html:'请将您的姓名、单位以及机器码发送到邮箱：ysc@apdplat.org'
                                }]
                    },{
                            xtype: 'fieldset',
                            title: '激活产品',
                            collapsible: false,

                            items: [{
                                    xtype:'textfield',
                                    anchor: '95%',
                                    cls : 'attr',
                                    id: 'licence',
                                    fieldLabel: '注册码'
                                }]
                    }],

                    buttons: [{
                        text: '购买',
                        iconCls:'save',
                        scope: this,
                        handler: function() {
                            this.buy();
                        }
                    },{
                        text: '激活',
                        iconCls:'save',
                        scope: this,
                        handler: function() {
                            this.active();
                        }
                    },{
                        text: '重置',
                        iconCls:'reset',
                        scope: this,
                        handler: function() {
                            this.frm.form.reset();
                        }
                    }]
                });
                return frm;
            },

            getDialog: function(requestCode) {
                this.frm = this.getForm(requestCode);
                var dlg = new parent.Ext.Window({
                    title: '购买激活产品',
                    iconCls:'active',
                    height:400,
                    width:750,
                    plain: true,
                    closable: false,
                    draggable: false,
                    resizable:false,
                    frame: true,
                    layout: 'fit',
                    border: false,
                    modal: true,
                    items: [this.frm]
                });
                return dlg;
            },

            show: function(requestCode) {
                this.dlg = this.getDialog(requestCode);
                this.dlg.show();
            },

            reset: function(){
                this.frm.form.reset();
            },
            close: function(){
                this.dlg.close();
            },
            
            active : function() {
                var licence=parent.Ext.getCmp('licence').getValue();
                if(licence.toString().trim()==""){
                    Ext.ux.Toast.msg('操作提示：','请输入激活码'); 
                    return ;
                }
                var loginTip=Ext.Msg.wait("正在激活......", '请稍候');
                
                Ext.Ajax.request({
                    url : activeURL,
                    params : {
                        licence  : licence
                    },
                    method : 'POST',
                    success:function(response, opts){
                        var resp=response.responseText;
                        loginTip.hide();
                        Ext.ux.Toast.msg('操作提示：',resp); 
                        if(resp.indexOf("激活成功")!=-1){
                            BuyModel.close();
                        }
                    },
                    failure: function(response, opts) {
                        loginTip.hide();
                        location.replace("platform/index.jsp");
                    }
                });
            },
            
            buy : function() {
                //购买产品，将购买信息提交给官网
                if (this.frm.getForm().isValid()) {
                    
                }
            }
        };
    } ();