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

var updatePartURL=contextPath+'/security/user!modifyPassword.action';
    ModifyWindow = function() {
        return {
            getForm: function() {
                 var frm = new Ext.form.FormPanel({
                    labelAlign: 'right',
                    buttonAlign: 'center',
                    bodyStyle: 'padding:5px',
                    labelWidth: 80,
                    defaultType: 'textfield',
                    defaults: {
                        allowBlank: false,
                        anchor: '90%'
                    },
                    items: [{
                            cls : 'j_password',
                            style : 'padding-left:18px',
                            id:'oldPassword',
                            name: 'oldPassword',
                            fieldLabel: '旧密码',
                            blankText : '旧密码不能为空',
                            inputType : 'password'
                        },
                        {
                            cls : 'j_password',
                            style : 'padding-left:18px',
                            name: 'newPassword',
                            id: 'newPassword',
                            fieldLabel: '新密码',
                            blankText : '新密码不能为空',
                            inputType : 'password'
                        },
                        {
                            cls : 'j_password',
                            style : 'padding-left:18px',
                            name: 'confirmPassword',
                            id: 'confirmPassword',
                            fieldLabel: '确认新密码',
                            blankText : '确认新密码不能为空',
                            inputType : 'password'
                    }],

                    buttons: [{
                        text: '修改',
                        iconCls:'save',
                        scope: this,
                        handler: function() {
                            this.submit();
                        }
                    },
                    {
                        text: '取消',
                        iconCls:'cancel',
                        scope: this,
                        handler: function() {
                            this.close();
                        }
                    }],
                     keys:[{
                         key : Ext.EventObject.ENTER,
                         fn : function() {
                            this.submit();
                         },
                         scope : this
                     }]
                });
                return frm;
            },

            reset: function(){
                this.frm.form.reset();
            },

            getDialog: function() {
                this.frm = this.getForm();
                var dlg = new Ext.Window({
                    title: '修改密码',
                    iconCls:'security',
                    height:180,
                    width:400,
                    plain: true,
                    closable: true,
                    frame: true,
                    layout: 'fit',
                    border: false,
                    modal: true,
                    items: [this.frm]
                });
                return dlg;
            },

            show: function() {
                this.dlg = this.getDialog();
                this.dlg.show();
            },

            close: function(){
                this.dlg.close();
            },

            submit: function() {
                var newPassword=parent.Ext.getCmp('newPassword').getValue();
                var confirmPassword=parent.Ext.getCmp('confirmPassword').getValue();
                if(confirmPassword!=newPassword){
                    Ext.MessageBox.alert('提示', "两次密码输入不一致");
                }else{
                    if (this.frm.getForm().isValid()) {
                        this.frm.form.submit({
                                waitTitle: '请稍等',
                                waitMsg: '正在修改……',
                                timeout:60,
                                url : updatePartURL,

                                success : function(form, action) {
                                        Ext.ux.Toast.msg('修改成功：',action.result.message); 
                                        ModifyWindow.close();
                                },
                                failure : function(form, action) {
                                        if (action.failureType === Ext.form.Action.CONNECT_FAILURE) {
                                            Ext.ux.Toast.msg('错误：',"网络已断开或后台服务已停止"); 
                                            ModifyWindow.close();
                                        }
                                        if (action.failureType === Ext.form.Action.SERVER_INVALID){
                                            // server responded with success = false
                                            Ext.ux.Toast.msg('修改失败：',action.result.message); 
                                            ModifyWindow.reset();
                                        }
                                }
                        });
                    }
                }
            }

        };
    } ();