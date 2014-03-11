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

Ext.BLANK_IMAGE_URL = '../extjs/images/default/s.gif';
var model;
var loginTitle='登录系统';
var requestCode;
var activeURL="security/active!active.action";
//fieldset的验证
Ext.QuickTips.init();//支持tips提示
Ext.form.Field.prototype.msgTarget='side';//提示的方式，枚举值为"qtip","title","under","side",id(元素id)  
 LoginWindow=function(){
     this.formPanel=function(){
	var form = new Ext.form.FormPanel( {
                bodyStyle : 'padding-top:6px',
                defaultType : 'textfield',
                labelAlign : 'right',
		columnWidth:0.75,
		border:false,
		layout:"form",
                labelWidth : 55,
                buttonAlign: 'center',
                defaults : {
                        allowBlank : false,
                        anchor:"90%"
                },
                items : [{
                                cls : 'j_username',
                                style : 'padding-left:18px',
                                name : 'j_username',
                                id : 'j_username',
                                fieldLabel : '帐 号',
                                blankText : '帐号不能为空'
                        }, {
                                cls : 'j_password',
                                style : 'padding-left:18px',
                                name : 'j_password',
                                id : 'j_password',
                                fieldLabel : '密 码',
                                blankText : '密码不能为空',
                                inputType : 'password'
                        }, {
                                cls : 'j_rand',
                                style : 'padding-left:18px',
                                name : 'j_captcha',
                                id:'j_captcha',
                                fieldLabel : '验证码',
                                blankText : '验证码不能为空'
                        }, {
                                xtype:'panel',
                                layout:'table',
                                hideLabel:true,
                                border:false,
                                layoutConfig:{columns:3},
                                items:[
                                        {
                                                width:55,
                                                xtype:'panel',
                                                border:false,
                                                text:'      '
                                        },{
                                                width:180,
                                                xtype:'panel',
                                                border:false,
                                                id:"codePicture",
                                                html:'<img border="0" height="50" width="180" src="'+contextPath + '/security/jcaptcha.png?rand='+Math.random()+'"/>'
                                        },{
                                                width:55,
                                                xtype:'panel',
                                                border:false,
                                                bodyStyle:'font-size:12px;padding-left:12px',
                                                html:'<a href="javascript:refeshCode()">看不清</a>'
                                        }]
                        }],
                     keys:[{
                         key : Ext.EventObject.ENTER,
                         fn : function() {
                            this.login;
                         },
                         scope : this
                     }]
        });
        return form;
     }(),
     this.show = function(){
     var window=new Ext.Window({
        id:"loginWindow",
        iconCls:"login-icon",
        bodyStyle:"background-color: white",
        border:true,
        closable:false,
        resizable:false,
        buttonAlign:"center",
        
 	title : loginTitle,
	width : 480,		
        height:270,
	collapsible : true,
        layout:{
            type:"vbox",
            align:"stretch"
        },
        items:[{
                xtype:"panel",
                border:false,
                bodyStyle:"padding-left:20px",
                html:'<img src="'+contextPath+'/images/'+logoImage+'" height="55" width="420"/>',
                height:60
            },{
                xtype:"panel",
                border:false,
                layout:"column",
                items:[
                    this.formPanel,
                    {
                        xtype:"panel",
                        border:false,
                        columnWidth:0.25,
                        html:'<img src="'+contextPath+'/images/'+loginImage+'"/>'
                    }
                ]
            }
        ],
        buttons: [{
            text: '登录',
            iconCls:'save',
            scope: this,
            handler: function() {
                this.login();
            }
        },{
            text: '重置',
            iconCls:'reset',
            scope: this,
            handler: function() {
                this.formPanel.form.reset();
            }
        }],
        keys:[{
             key : Ext.EventObject.ENTER,
             fn : function() {
                this.login();
             },
             scope : this
         }]
       });
       window.show();
     },
     this.login=function() {
            var loginTip=Ext.Msg.wait("正在登录......", '请稍候');
            var j_captcha=parent.Ext.getCmp('j_captcha').getValue();
            var j_username=parent.Ext.getCmp('j_username').getValue();
            var j_password=parent.Ext.getCmp('j_password').getValue();
            if(j_username.toString().trim()==""||j_password.toString().trim()==""||j_captcha.toString().trim()==""){
                parent.Ext.getCmp('j_username').validate();
                parent.Ext.getCmp('j_password').validate();
                parent.Ext.getCmp('j_captcha').validate();
                loginTip.hide();
                return false;
            }
            var url = 'j_spring_security_check';
            j_password=hex_sha512(j_password+'{用户信息}');
            Ext.Ajax.request({
                url : url,
                params : {
                    j_captcha  : j_captcha,
                    j_username : j_username,
                    j_password : j_password
                },
                method : 'POST',
                success:function(response, opts){
                    if(response.getResponseHeader('login_success') || response.responseText.length > 20) {
                        Ext.getCmp("loginWindow").hide();
                        //防止用户登录成功之后点击浏览器的后退按钮回到登录页面
                        //在浏览器的历史记录里面不记录登录页面
                        location.replace(contextPath+"/platform/index.jsp");
                        return;
                    }  
                    refeshCode();
                    parent.Ext.getCmp('j_password').setValue("");
                    parent.Ext.getCmp('j_captcha').setValue("");
                    parent.Ext.getCmp('j_password').focus();
                    loginTip.hide();
                    if(response.getResponseHeader('checkCodeError')) {
                        Ext.ux.Toast.msg('登陆失败：','验证码错误，请重新登录!');  
                        return;
                    }  
                    if(response.getResponseHeader('login_error')) {
                        var resp=response.responseText;
                        if(resp.indexOf("您还没有购买产品")!=-1){
                            //购买产品
                            var attr=resp.split(":");
                            if(attr.length==2){
                                var requestCode=attr[1];
                                resp=attr[0];

                                BuyModel.show(requestCode);
                            }                                        
                        }
                        Ext.ux.Toast.msg('登陆失败：',resp);  
                    }  
                },
                failure: function(response, opts) {
                    location.replace("platform/index.jsp");
                }
            });
	}	
    };
/**
 * 更新验证码
 */
function refeshCode(){
	var loginCode = Ext.getCmp('codePicture');
	loginCode.body.update('<img border="0" height="50" width="180" src="'+contextPath + '/security/jcaptcha.png?rand='+Math.random()+'"/>');
        fixPng();
};