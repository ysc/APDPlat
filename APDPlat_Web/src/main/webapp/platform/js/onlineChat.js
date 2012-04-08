var targetUserId="";
/**
 * 
 * @class HTMLEditor
 * @extends Ext.form.HtmlEditor
 * 
 * 在Ext.form.HtmlEditor上面增加了一些实用方法。
 * 
 * @xtype myhtmleditor
 */
HTMLEditor = Ext.extend(Ext.form.HtmlEditor, {
	// enableFont:false,
	/**
	 * @type String codeStyle 配置的格式化代码样式
	 */
	codeStyle : '<br/><pre style="border-right: #999999 1px dotted; padding-right: 5px; border-top: #999999 1px dotted; padding-left: 5px; font-size: 12px; padding-bottom: 5px; margin-left: 10px; border-left: #999999 1px dotted; margin-right: 10px; padding-top: 5px; border-bottom: #999999 1px dotted; background-color: #eeeeee">{0}</pre><br/>',
	/**
	 * @cfg {Array} keys 在HtmlEditor上面绑定的快捷键
	 */
	onRender : function(ct, position) {
		HTMLEditor.superclass.onRender.call(this, ct, position);
		if (this.keys) {
			if (!this.keys.length) {
				this.keyMap = new Ext.KeyMap(this.getEditorBody(), this.keys);
			} else {
				this.keyMap = new Ext.KeyMap(this.getEditorBody(), this.keys[0]);
				for (var i = 1; i < this.keys.length; i++)
					this.keyMap.addBinding(this.keys[i]);
			}
			this.keyMap.stopEvent = true;
		}
	},
	/**
	 * 选择表情图标
	 */
	showEmoteSelect : function() {
		emoteSelectWin.editor = this;
		emoteSelectWin.show();
	},
	/**
	 * 添加图片方法。点击添加图片按钮，打开图片上传窗口。并能将上传的图片插入到当前编辑行。
	 */
	addImage : function() {
		function insertImage() {
			var editor = this;
			win.upload(function(ret) {
						if (ret) {
							var s = "<br/><img src=" + ret.path;
							if (ret.width)
								s += " width=" + ret.width;
							if (ret.height)
								s += " height=" + ret.height;
							s += " /><br/>";
							editor.insertAtCursor(s);
							win.close();
						}
					});
		};
		var win = new UploadImageWindow({
					modal : true,
					iconCls : "icon-img",
					buttons : [{
								text : "确定",
								handler : insertImage,
								scope : this
							}, {
								text : "取消",
								handler : function() {
									win.close();
								}
							}]
				});
		win.show();
	},
	/**
	 * 添加代码方法。点击添加代码，打开添加代码窗口。并能将添加的代码插入到当前编辑行。
	 */
	addCode : function() {
		function insertCode() {
			var value = win.getComponent("codes").getValue();
			this.insertAtCursor(String.format(this.codeStyle, value));
			win.close();
		};
		var win = new Ext.Window({
					title : "添加代码",
					width : 500,
					height : 300,
					modal : true,
					iconCls : "icon-code",
					layout : "fit",
					items : {
						xtype : "textarea",
						id : "codes"
					},
					buttons : [{
								text : "确定",
								handler : insertCode,
								scope : this
							}, {
								text : "取消",
								handler : function() {
									win.close();
								}
							}]
				});
		win.show();
	},
	/**
	 * 给HtmlEditor添加按钮。默认添加【插入图片】，【插入代码】和【添加表情】三个按钮，顺序为16,17,18
	 * 
	 * @param {}
	 *            editor
	 */
	createToolbar : function(editor) {
		HTMLEditor.superclass.createToolbar.call(this, editor);
		this.tb.insertButton(16, {
					cls : "x-btn-icon",
					icon : "images/qq/img.gif",
					handler : this.addImage,
					scope : this
				});
		this.tb.insertButton(17, {
					cls : "x-btn-icon",
					icon : "images/qq/code.gif",
					handler : this.addCode,
					scope : this
				});
		this.tb.insertButton(18, {
					cls : "x-btn-icon",
					icon : "images/emote/main.png",
					handler : this.showEmoteSelect,
					scope : this
				});

	},
	/**
	 * @cfg {Integer} maxLength 在HtmlEditor中允许输入的最大字数
	 */
	/**
	 * 验证HtmlEditor的值。 如果配置了maxLength，则如果编辑器中的字数大于maxLength，则编辑器不可用。
	 * 
	 * @param {}
	 *            value
	 * @return {Boolean}
	 */
	validateValue : function(value) {
		if (value.length > this.maxLength) {
			var s = String.format(this.maxLengthText, this.maxLength);
			this.markInvalid(s);
			return false;
		}
		return true;
	}
});
Ext.reg('myhtmleditor', HTMLEditor);
UploadImageWindow=Ext.extend(Ext.Window,{
 	title : '上传照片',		
	width : 345,
	autoHeight:true,
	defaults : {			
		border : false
	},
	//clseAction:"hide",
	buttonAlign : 'center',	
	createFormPanel :function() {
		return new Ext.form.FormPanel({			
			defaultType : 'textfield',
			labelAlign : 'right',
			fileUpload:true,
			labelWidth : 75,			
			frame : true,
			defaults : {			
				width : 220		
			},
			items : [{xtype:"hidden",
					  name:"cmd",
					  value:"upload"},
					 {					
					name : 'pathFile',
					fieldLabel : '上传照片',
					inputType : 'file'
					},
					{					
						name : 'title',
						fieldLabel : '照片名称'
					},
					{					
						name : 'path',
						fieldLabel : '照片URL'				
					},
					{
						name : 'width',
						fieldLabel : '照片宽'				
					},
					{
						name : 'height',
						fieldLabel : '照片高'			
					}
				]
		});
	},					
	upload:function(fn) {
			var params={cmd:"upload",ext:true,to:targetUserId};
			this.fp.form.submit({
					waitTitle:"请稍候",
					waitMsg : '正在上传......',
					url : 'onlineUser.ejf',
					params:params,
					success : function(form, action) {
						if(targetUserId){
						this.fp.form.findField("path").setValue("onlineUser.ejf?cmd=loadPic&f="+action.result.data+"&to="+targetUserId+"&s="+OnlineMessageManager.me.id);
						}
						else this.fp.form.findField("path").setValue(action.result.data);
						var obj={title:this.fp.form.findField("title").getValue(),
						   path:this.fp.form.findField("path").getValue(),
						   width:this.fp.form.findField("width").getValue(),
						   height:this.fp.form.findField("height").getValue()
						   }					
						fn(obj);						
					},
					failure : function(form, action) {						
						if (action.failureType == Ext.form.Action.SERVER_INVALID)
							Ext.MessageBox.alert('警告', action.result.errors.msg);
						fn(false);				
					},
					scope:this
				});
		},
	initComponent : function(){
        UploadImageWindow.superclass.initComponent.call(this);       
        this.fp=this.createFormPanel();
        this.add(this.fp);
	 } 	
 }); 
 
 AddFriendWindow=Ext.extend(Ext.Window,{
	id:"addFriendWindow",
	title:"添加好友",
	user:{},
	width:300,
	height:185,
	buttonAlign:"center",
	modal:true,	
	headInfo:" &nbsp;<br/>把用户<font color='blue'><b>{0}</b></font>添加为好友!<br/> &nbsp;",
	addFriend:function(){
		this.fp.form.submit({
			waitTitle:"请稍候",
			waitMsg:"请稍候，正在执行添加好友操作。。。。。。",
			url:"friendship.ejf?cmd=save",
			success:function(req){
				Ext.Msg.alert("提示","好友申请已经发送!",function(){
					this.fp.form.reset();
					this.hide();
				},this)
			},
			scope:this
		});
	},
	initComponent : function(){	
		 this.buttons=[{text:"发送好友申请",handler:this.addFriend,scope:this},{text:"取消",handler:function(){this.hide();},scope:this}]
		 this.fp=new Ext.form.FormPanel({id:"form",xtype:"form",border:false,labelWidth:60,items:[{xtype:"hidden",id:"userId"},{id:"headInfo",border:false},{xtype:"textarea",name:"remark",fieldLabel:"备注信息",width:200}]});
		 AddFriendWindow.superclass.initComponent.call(this);
		 this.add(this.fp);
	},
	show:function(){
		AddFriendWindow.superclass.show.call(this);
		this.findById("headInfo").body.update(String.format(this.headInfo,this.user.name));		
		this.findById("userId").setValue(this.user.id);
		this.fp.form.findField("remark").focus();
	}
});
var addFriendWin=new AddFriendWindow();//全局的添加好友窗口
var findFriendWin;

FindFriendWindow=Ext.extend(Ext.Window,{
	id:"findFriendWindow",
	title:"查找好友",
	width:420,
	height:530,
	closeAction:"hide",
	buttonAlign:"center",
	sexs:[["未知",""],["男","man"],["女","women"]],
	findFriend:function(){
		var f=this.fp.form;
		if(!f.findField("name").getValue() && !f.findField("sex").getValue()&& !f.findField("email").getValue()&& !f.findField("im").getValue()&& !f.findField("speciality").getValue() && (!f.findField("cityId").getValue()||f.findField("cityId").getValue()<=0)){
			Ext.Msg.alert("提示","至少要输入一项查询条件",function(){f.findField("name").focus();});
		}
		else{		
		var o=this.fp.form.getValues(false);
		this.userList.store.baseParams=o;
		this.userList.store.baseParams.searched=true;
		this.userList.store.reload();
		}
	},	
	gridContextEvent:function(grid,rowIndex,e){
		e.preventDefault();
		this.menu.showAt(e.getPoint());
		this.userList.grid.getSelectionModel().selectRow(rowIndex);
		this.currentRecord=this.userList.store.getAt(rowIndex);		
	},
	chat:function(){
		var obj={sender:{id:this.currentRecord.get("id"),name:this.currentRecord.get("name")}};
		OnlineMessageManager.openMessage(obj);
	},
	showUserInfo:function(){
		OnlineMessageManager.showUserInfo(this.currentRecord.get("id"));
	},
	addFriend:function(){		
		addFriendWin.user={id:this.currentRecord.get("id"),name:this.currentRecord.get("name")};
		addFriendWin.show();		
	},
	goHomepage:function(){
		window.open("/blog.ejf?userId="+this.currentRecord.get("id"));
	},
	initComponent : function(){	
		var FriendUserList=Ext.extend(BaseGridList,{		
		url:"onlineUser.ejf?cmd=searchUser",	
   		storeMapping:["id","name","sex","score","lastLoginTime"],		
		initComponent : function(){			
		this.tools=	[{id:"refresh",handler:function(){if(this.store.baseParams.searched)this.store.reload();},scope:this}];
		this.cm=new Ext.grid.ColumnModel([
		{header: "用户名", sortable:true,width: 80, dataIndex:"name"},
		{header: "性别", sortable:true,width: 60, dataIndex:"sex"},
		{header: "积分", sortable:true,width: 60, dataIndex:"score"},
		{header: "上次登录时间", sortable:true,width: 150, dataIndex:"lastLoginTime",renderer:this.dateRender()}
	        ]);		
		FriendUserList.superclass.initComponent.call(this);
		}		
		});
		
		this.menu=new Ext.menu.Menu({items:[{text:"发送消息",handler:this.chat,scope:this},
		 		{text:"查看资料",handler:this.showUserInfo,scope:this},
		 		//{text:"加为好友",hidden:true,handler:this.addFriend,scope:this},
		 		{text:"查看动态",hidden:true},
		 		{text:"用户主页",hidden:true,handler:this.goHomepage,scope:this}
		 		]});
		//this.buttons=[{text:"搜索",handler:this.findFriend,scope:this},{text:"取消",handler:function(){this.hide();},scope:this}];
		this.fp=new Ext.form.FormPanel({defaultType:"textfield",
			labelWidth:60,
			defaults:{width:280},
			bodyStyle : 'padding-left:20px',			
			items:[
			{xtype:"panel",border:false,html:"<b>请输入下面的查询条件来查询你所需要的用户:</b><br/>&nbsp;",width:300},
			{xtype:"panel",width:400,border:false,layout:"column",defaults:{layout:"form",border:false},items:[
			{columnWidth:.45,items:{xtype:"textfield",fieldLabel:"用户名",name:"name",width:100}},
			{columnWidth:.55,items:{
			xtype:"combo",
			name:"sex",
			hiddenName:"sex",
			fieldLabel:"性别",
			displayField:"title",
			valueField:"value",
			width:100,
			store: new Ext.data.SimpleStore({
			        fields: ['title', 'value'],
			        data : this.sexs
			    }),
			editable:false,
        	mode: 'local',
        	triggerAction: 'all',
        	emptyText:'选择...'
			}}]},
			{xtype:"treecombo",
			 fieldLabel:"城市",
			 name:"cityId",
			 hiddenName:"cityId",
			 tree:new Ext.tree.TreePanel({
 				root:new Ext.tree.AsyncTreeNode({
 				id:"root",
   				text:"选择所在城市",   	
   				expanded:true,
   				loader:Global.systemRegionLoader
   				})
 			})},			
			{fieldLabel:"电子邮件",name:"email"},
			{fieldLabel:"IM",name:"im"},			
			{fieldLabel:"专长",name:"speciality"}			
			],
			buttons:[{text:"搜索",handler:this.findFriend,scope:this},{text:"取消",handler:function(){this.hide();},scope:this}]});
		
		FindFriendWindow.superclass.initComponent.call(this);
		this.userList=new FriendUserList();		
		this.add(this.fp,{
				title:"查询结果",
				border:false,
				height:300,layout:"fit",
				items:this.userList,
				tools:[{id:"refresh",handler:this.findFriend,scope:this}],
				tbar:["->",{
					text:"发送消息",handler:this.chat,scope:this},
					{text:"查看资料",handler:this.showUserInfo,scope:this},
					//{text:"加为好友",handler:this.addFriend,scope:this},
					{text:"查看动态"},
					{text:"用户主页",handler:this.goHomepage,scope:this}]});
		this.on("render",function(){
			this.userList.grid.on("rowcontextmenu",this.gridContextEvent,this); 
			this.userList.grid.on("rowclick",function(g,rowIndex,e){this.currentRecord=this.userList.store.getAt(rowIndex);},this)
		},this)
	}
});


OnlineMessageManager={
	me:{id:"",name:""},	
	wins:[],	
	period:5000,
	stopRecive:false,
	popMessage:true,
	audioMessage:false,
	picMessage:false,	
	config:function(){
		if(!this.configWin)
			this.configWin=new Ext.Window({
				title:"对话信息设置",
				width:200,
				height:200,
				iconCls:"icon-oMsgset",
				closeAction:"hide",
				modal:true,
				items:{			
				layout:"form",	
				frame:true,	
				items:[{xtype:"numberfield",width:50,minValue:5000,fieldLabel:"刷新周期",id:"period",value:this.period},
					   {xtype:"checkbox",fieldLabel:"暂停接收信息",id:"pause",checked:this.stopRecive},
					   {xtype:"checkbox",fieldLabel:"自动弹出",id:"pop",checked:this.popMessage,disabled:true},
					   {xtype:"checkbox",fieldLabel:"声音提示",id:"audio",checked:this.audioMessage,disabled:true},
					   {xtype:"checkbox",fieldLabel:"闪动图标",id:"pic",checked:this.picMessage,disabled:true}
				]},
				buttons:[{text:"确定",handler:this.saveConfig,scope:this},{text:"取消",handler:function(){this.configWin.hide();},scope:this}]
			});
		this.configWin.show();
	},	
	saveConfig:function(){
		this.popMessage=this.configWin.findById("pop").getValue();
		this.audioMessage=this.configWin.findById("audio").getValue();
		this.picMessage=this.configWin.findById("pic").getValue();
		this.period=this.configWin.findById("period").getValue();
		if(this.period<5000){	
			this.period=5000;
			this.configWin.findById("period").setValue(5000);
		}
		var st=this.configWin.findById("pause").getValue();		
		if(st!=this.stopRecive)
		{
			this.stopRecive=st;
			if(this.stopRecive)this.stop();
			else this.start();
		}
		this.configWin.hide();
	},
	openMessage : function(message) {
		try {
			if ((!message.sender && !message.sender.id) || message.status > 0) {
				if (message.id)
					Ext.Ajax.request({
						url : "onlineUser.ejf?cmd=readMessage",
						params : {
							mulitId : message.id
						}
					});
				return;
			}			
			
			var winId = "messageWin_" + message.sender.id;
			var msgWin = Ext.getCmp(winId);
			if (msgWin) {
				msgWin.show();
			} else {
				msgWin = new MessageWindow({
					id : winId,
					reciver : message.sender
				});
				if (this.wins.length > 10) {
					this.wins[0].close();
					this.wins.remove(this.wins[0]);
				}
				this.wins.push(msgWin);
				msgWin.show();
			}
			if (message.content) {
				var obj = {
					msg : message.content,
					date : message.inputTime,
					user : message.sender,
					cls : "yourMsg"
				};
				msgWin.addMessage(obj);
				if (message.id)
					Ext.Ajax.request({
						url : "onlineUser.ejf?cmd=readMessage",
						params : {
							mulitId : message.id
						}
					});
			}
		} catch (e) {
			//alert(e);
		}
	},	
   loadMessage : function() {
		if (this.me.id && !this.stopRecive) {
			try {
				Ext.Ajax.request({
					url : "onlineUser.ejf?cmd=loadMessage",
					callback : function(options, success, response) {
						if (success) {
							var pageList = Ext.decode(response.responseText);
							if (pageList && pageList.rowCount > 0
									&& pageList.result) {
								var list = pageList.result;
								for (var i = 0; i < list.length; i++) {
									if (list[i].announce) {
										if(list[i].inputTime){
										list[i].content="<font color=blue><u>"+list[i].inputTime.format("Y-m-d H:i:s")+"</u></font><br/>"+list[i].content;
										}
										
										this.showSystemMessage(list[i]);
										break;
									} else {
										this.openMessage(list[i]);
									}
								}
							}
						}
						if (!this.stopRecive)
							this.loadMessage.defer(this.period, this);
					},
					scope : this
				});
			} catch (e) {
				// alert(e);
			}
		}
	},
   start : function() {
		this.stopRecive = false;
		if (this.me.id)
			this.loadMessage.defer(this.period, this);
		else
			this.start.defer(this.period, this);
	},
	stop : function() {
		this.stopRecive = true;
	},
	showUserInfo : function(id, name) {
		if (!this.userInfoWin) {
			this.userInfoWin = new Ext.Window({
				title : "查看用户资料",
				width : 250,
				iconCls : "icon-zoom",
				closeAction : "hide",
				autoScroll : true,
				manager : OnlineMessageManager.winMgr,
				height : 200,
				layout:"fit",
				items:[{
					xtype:"form",
					frame:true,
					labelWidth:70,
					items:[
						{xtype:"labelfield",fieldLabel:"用户名",name:"name"},
						{xtype:"labelfield",fieldLabel:"Email",name:"email"},
						{xtype:"labelfield",fieldLabel:"电话",name:"tel"},
						{xtype:"labelfield",fieldLabel:"部门",name:"dept"},
						{xtype:"labelfield",fieldLabel:"登录次数",name:"loginTimes"}
					]
				}],
				buttons : [{
					text : "更新",
					handler : function() {
						this.refreshUserInfo(id, name);
					},
					scope : this
				}, {
					text : "关闭",
					handler : function() {
						this.userInfoWin.hide()
					},
					scope : this
				}]
			});
		}
		this.userInfoWin.show();
		this.userInfoWin.getComponent(0).el.mask("正在加载用户信息...");
		this.refreshUserInfo(id, name);
	},
    refreshUserInfo:function(id,name){
    	var params=id?{id:id}:{name:name};    	
    	Ext.Ajax.request({
    		url:"onlineUser.ejf?cmd=readUser",
    		params:params,
    		success:function(response){
	    		var ret=Ext.decode(response.responseText);
	    		this.userInfoWin.getComponent(0).form.setValues(ret);
	    		this.userInfoWin.getComponent(0).el.unmask();
    		},
    		scope:this
    	});
    },
   findFriend:function(){
	   	return false;
	   	if(!findFriendWin)findFriendWin=new FindFriendWindow();
	   	findFriendWin.show();
   },
   sendAttach:function(reciveUser){
   	if(!this.attachWin){
   		this.attachWin=new Ext.Window({
   			width:300,
   			autoHeight:true,
   			title:"上传附件",
   			closeAction:"hide",
   			layout:"fit",
   			items:{
   					xtype:"form",defaults:{anchor:'-20'},
   					autoHeight:true,fileUpload:true,labelWidth : 75,
   					items:[{
	   					xtype:"textfield",				
						name :"pathFile",
						width:200,
						fieldLabel :"上传照片",
						inputType :"file"
					},{		
						xtype:"textfield",
						width:200,			
						name : 'path',
						fieldLabel : '附件URL'				
					}]},
			buttons:[{text:"确定",handler:function(){
				var form=this.attachWin.getComponent(0).form
				var params={cmd:"upload",ext:true,to:reciveUser};
				form.submit({
					waitTitle:"请稍候",
					waitMsg : '正在上传......',
					url : 'onlineUser.ejf',
					params:params,
					success : function(form, action) {
						var path="onlineUser.ejf?cmd=download&f="+action.result.data+"&to="+reciveUser+"&s="+OnlineMessageManager.me.id;
						var msg="<a href='"+path+"' target='_blank'><font color='blue'>附件</font></a>";
						Ext.Ajax.request({
    						url:"onlineUser.ejf?cmd=sendMessage",
    						params:{reciver:reciveUser,content:msg}
    						});		
    					form.reset();
    					this.attachWin.hide();	
					},
					failure : function(form, action) {						
						if (action.failureType == Ext.form.Action.SERVER_INVALID)
							Ext.MessageBox.alert('警告', action.result.errors.msg);
						fn(false);				
					},
					scope:this
				});
				
				},scope:this},
				{text:"取消",handler:function(){this.attachWin.hide();},scope:this}]
					});
   	}
   this.attachWin.show();
   },
   winMgr:null,
   showSystemMessage:function(message){
   	var bx=Ext.getBody().getViewSize().width,by=Ext.getBody().getViewSize().height;
   	if(!this.sysWin){
   		this.sysWin=new Ext.Window({x:bx,y:by,width:300,height:200,title:"系统消息",html:"SomeThing",iconCls:"erpwin-announce-icon",closeAction:"hide"});
   		this.sysWin.on("show",function(win){win.hide.defer(60000,win,[true])});
   	}
   	else {this.sysWin.el.moveTo(bx,by);}
   	var win=this.sysWin;
   	win.show();
   	win.body.update(message.content);
	win.el.moveTo(bx-win.getBox().width,by-win.getBox().height,{endOpacity: 1, duration:2});
	win.el.highlight.defer(2000,win.el,["ffff9c", {
	    attr: "background-color",
	    endColor:"ffff00",
	    easing: 'easeIn',
	    duration: 1
	}]);
	if (message.id)
					Ext.Ajax.request({
						url : "onlineUser.ejf?cmd=readMessage",
						params : {
							mulitId : message.id
						}
					});
   }
};

////decode begin

EmoteSelect = Ext.extend(Ext.Panel,{
	baseUrl:"images/emote/",
	layout:"table",
	layoutConfig:{columns:14},
	select:function(img){
		emoteSelectWin.editor.insertAtCursor("<img src='"+img+"'>");
		emoteSelectWin.hide();
	},
	clickEvent:function(e){		
        if(e.getTarget().tagName=="IMG"||e.getTarget().tagName=="img"){
        		this.select(e.getTarget().src);
        }
    },
	initComponent : function(){	
		 var s=[];	 	 
	 	 for(var i=0;i<95;i++)
	 	 	s.push({src:this.baseUrl+i+'.gif'});
	 	 this.tpl = new Ext.XTemplate('<tpl for="."><img style ="margin:2px 2px 2px 2px;" src= "{src}"></img></tpl>');
	 	 EmoteSelect.superclass.initComponent.call(this);
	 	 this.on("render",function(){
	 	 	this.tpl.overwrite(this.body,s);
	 	 	this.body.on("click",this.clickEvent,this);
	 	},this)
	 }
});

var emoteSelectWin=new Ext.Window({title:"选择表情",
	width:380,
	height:220,
	modal:true,
	layout:"fit",
	closable:true,
	closeAction:"hide",
	items:new EmoteSelect(),
	manager:OnlineMessageManager.winMgr
});
	
MessageWindow=Ext.extend(Ext.Window,{
	reciver:null,	
	autoClose:true,
	title:"发送消息",
	width:520,
	height:470,
	minWidth:300, 
	closeAction:"hide",
	iconCls:"icon-im",
	layout:"anchor",	
	maximizable:true,
	shim:false,
    animCollapse:false,
    manager:OnlineMessageManager.winMgr,
    constrainHeader:true,	
    addFriend:function(){
    	addFriendWin.user=this.reciver;
    	addFriendWin.show();
    },
    loadHistory:function(){
    	this.cleanHistory();
    	Ext.Ajax.request({
    		waitMsg:"正在加载,请稍候...",
    		url:"onlineUser.ejf?cmd=loadHistory&id="+this.reciver.id,
    		success:function(req){
    		var list=Ext.decode(req.responseText);
    		if(list&&list.length){
    		for(var i=list.length-1;i>=0;i--){
    			var o=list[i];    			
    			var obj={msg:o.content,date:o.inputTime,user:o.sender,cls:(OnlineMessageManager.me.id==o.sender.id?"myMsg":"yourMsg")};
    			this.addMessage(obj);
    		}
    		}
    		},
    		scope:this
    	}); 
    },    
    cleanHistory:function(){
    	var msgPanel=this.findById("msgArea"+this.reciver.id);
    	msgPanel.body.update("");	
    },
    addMessage:function(obj){
    	var msgPanel=this.findById("msgArea"+this.reciver.id);
    	var m="<div><div class='"+obj.cls+"'><b>"+obj.user.name+"</b>("+obj.user.id+")　"+obj.date.format("Y-m-d H:i:s")+"</div><div>　";
    	m+=obj.msg+"</div></div>";
    	msgPanel.body.insertHtml("beforeEnd",m);
    	msgPanel.body.scroll("bottom",100);
    },
    sendMessage:function(){    	    	
    	var msg=this.findById("editor"+this.reciver.id);
    	var m=msg.getValue();    	
    	if(m!=""){    	
    	if(!msg.isValid())return;
    	var obj={msg:m,date:new Date(),user:OnlineMessageManager.me,cls:"myMsg"};
    	Ext.Ajax.request({
    		url:"onlineUser.ejf?cmd=sendMessage",
    		params:{reciver:this.reciver.id,content:m},
    		success:function(){},
    		failure:function(){}//给出信息没发送成功的提示
    		});
    	this.addMessage(obj);
    	this.lastMsg=m;
    	msg.setValue("");
    	if(this.autoClose)this.hide();
    	}
    	msg.focus();
    },  
    clickEvent:function(e){
        	if(e.getTarget().tagName=="A"||e.getTarget().tagName=="a"){
        		e.getTarget().target="_blank";
        	}
        	},
    initComponent : function(){
		 this.tbar=[this.reciver.name,
		 		{text:"查看资料",cls:"x-btn-text-icon",icon:"images/qq/zoom.gif",handler:OnlineMessageManager.showUserInfo.createDelegate(OnlineMessageManager,[this.reciver.id])}
		 			//{text:"加为好友",cls:"x-btn-text-icon",icon:"images/qq/adding.gif",handler:this.addFriend,scope:this}
		 			];
		 this.title="与"+this.reciver.name+"对话";
		 if(this.reciver){
		 	this.id="message"+this.reciver.id;
		 }
		 MessageWindow.superclass.initComponent.call(this);
		 this.add({
			layout:"fit",
			border : false,
			anchor:"100% 60%",
			items:{
				border : false,
				id:"msgArea"+this.reciver.id,
				autoScroll:true,
				tbar:this.tbar
			}
		});
		this.editor=new ChatEditor({id:"editor"+this.reciver.id,				   
				   name:"editor"+this.reciver.id,	
				   maxLength:2000,			   
				   listeners:{"activate":function(){
				   	//alert(111);
				   targetUserId=this.reciver.id;
					Ext.get(this.editor.iframe).on("focus",function(){
						targetUserId=this.reciver.id;
						},this);
				   	},scope:this},
				   keys:[{
						    key:Ext.EventObject.ENTER,
						    ctrl: true,
						    fn:this.sendMessage,
						    scope: this
						},
						{
						    key:'s',
						    alt: true,
						    fn:this.sendMessage,
						    scope: this
						},
						{
						    key:'cx',
						    alt: true,
						    fn:function(){this.hide()},
						    scope: this
						}]   
				   }
		);
		
		this.add({
			border : false,
			anchor:"100% 40%",
			tbar:[{text:"聊天记录",cls:"x-btn-text-icon",icon:"images/qq/loadHistory.gif",handler:this.loadHistory,scope:this},
				  {text:"清空记录",cls:"x-btn-text-icon",icon:"images/qq/cleanHistory.gif",handler:this.cleanHistory,scope:this},
				  {text:"传递附件",cls:"x-btn-text-icon",icon:"images/qq/updown.gif",handler:OnlineMessageManager.sendAttach.createDelegate(this,[this.reciver.id])},"是否自动关闭",
				  {
				 	 xtype:"checkbox",
				 	 checked:this.autoClose,
					 listeners:{
				 		"check":function(c,chk){
				 			this.autoClose=chk;			 	
				 	 },
					 scope:this}
					}],
			layout:"fit",
			items:this.editor,
			buttons:[{text:"发送",handler:this.sendMessage,scope:this},{text:"关闭",handler:function(){this.hide();},scope:this}]		
			})	
    },
	listeners:{
		"show":function(win){
			var c=Ext.getCmp("editor"+this.reciver.id);
			c.getEditorBody().focus();
			Ext.get(c.getEditorBody()).on("focus",function(){this.show();},this);
			var msgPanel=this.findById("msgArea"+this.reciver.id);
			if(!this.loadClickEvent){
        	msgPanel.body.on("click",this.clickEvent);
        	this.loadClickEvent=true;
			}        
		}
	}	
});


ChatEditor=Ext.extend(HTMLEditor,{
	 enableFont:false,
	 maxLength:1000,
	 maxLengthText : "The maximum length for this field is {0}",
	 onRender : function(ct, position){	 	
	 	 ChatEditor.superclass.onRender.call(this, ct, position);
	 	 if(this.keys){
	 	 	if(!this.keys.length){
	 	 	this.keyMap = new Ext.KeyMap(this.getEditorBody(), this.keys);
	 	 	}
	 	 	else{	 	 		
	 	 		this.keyMap = new Ext.KeyMap(this.getEditorBody(),this.keys[0]);
	 	 		for(var i=1;i<this.keys.length;i++)this.keyMap.addBinding(this.keys[i]);
	 	 	}
	 	 	this.keyMap.stopEvent=true;
	 	 }
	 },
	validateValue : function(value){
		 if(value.length > this.maxLength){
		 	var s=String.format(this.maxLengthText, this.maxLength);
            this.markInvalid(s);
            return false;
        }
		 return true;
	}
});

OnlineChatWindow=Ext.extend(Ext.Window,{
	title:"即时交流WebIM",
	width:200,
	height:500,
	minSize:100,
	closeAction:"hide",
	iconCls:"icon-im",
	maximizable:true,
	minimizable:true,
	shim:false,
    animCollapse:false,
    constrainHeader:true,
    layout:"accordion",
	layoutConfig:{hideCollapseTool:true},
    show:function(){
    	if(!this.isVisible()){
    	OnlineChatWindow.superclass.show.call(this);    	
    	this.refreshUser();
    	}
    },	
    minimize:function(){
    	this.hide();
    },
	render : function(){
		var width = Ext.getBody().getWidth();
		this.x =Math.max(width-this.width-20,0) ;
		this.y = 30;
		this.title = "即时交流("+OnlineMessageManager.me.name+")";
	    OnlineChatWindow.superclass.render.apply(this, arguments);
	},	
	refreshUser:function(){
		this.findById("userList0").root.reload();
		this.findById("userList1").root.reload();
		this.findById("resentUser").root.reload();
	},
	chat:function(node)	{
		if(node.id.indexOf("ext")<0){
			if(node.attributes.login && node.id!=OnlineMessageManager.me.id){
				var obj={sender:{id:node.id,name:node.text}};
				OnlineMessageManager.openMessage(obj);
			}
		}
	},
	addFriend:function(node){
		if(node.id.indexOf("ext")<0){
		if(node.attributes.login && node.id!=OnlineMessageManager.me.id){
			addFriendWin.user={id:node.id,name:node.text};
			addFriendWin.show();
		}}
	},
	showMenu:function(menu){
		return function(node,e){
		if(node.id.indexOf("ext")<0){
		if(node.attributes.login){//如果不是匿名用户，则显示相关菜单
			this.currentNode=node;
			menu.showAt(e.getPoint());
		}}
		}
	},	
	initComponent : function(){
		 this.tbar=[
		 		{text:"刷新",cls:"x-btn-text-icon",icon:"images/operation/refresh.gif",handler:this.refreshUser,scope:this},
		 		"-",
		 		{text:"查找",cls:"x-btn-text-icon",hidden:true,icon:"images/qq/zoom.gif",handler:OnlineMessageManager.findFriend},
		 		{text:"设置",cls:"x-btn-text-icon",icon:"images/qq/zoom.gif",handler:OnlineMessageManager.config,scope:OnlineMessageManager}
		 ];
		 this.menu=new Ext.menu.Menu({items:[{text:"发送消息",handler:function(){this.chat(this.currentNode);},scope:this},
		 		{text:"查看资料",handler:function(){OnlineMessageManager.showUserInfo(this.currentNode.id)},scope:this},
		 		{text:"加为好友",hidden:true,handler:function(){this.addFriend(this.currentNode)},scope:this},
		 		{text:"查看动态",hidden:true,handler:function(){this.addFriend(this.currentNode)},scope:this},
		 		{text:"用户主页",hidden:true,handler:function(){this.closeAll(this.getActiveTab());},scope:this}
		 		]});
		 OnlineChatWindow.superclass.initComponent.call(this);
	this.add({title:"在线用户",iconCls:"icon-user",
			//hidden:true,
			layout:"fit",
			collapsed:true,	
			border : false,
			items:{
				id:"userList0",
				xtype:"treepanel",
				autoScroll:true,
				clearOnLoad:false,
				rootVisible:false,		
				lines:false,
				loader:new Ext.tree.TreeLoader({url:"onlineUser.ejf?cmd=loadOnlineUser&ext=true"}),	
				root:new Ext.tree.AsyncTreeNode({text:"根"}),
				listeners:{
				"render":function(tree){
					tree.root.expand();},
				"dblclick":this.chat,
				"contextmenu":this.showMenu(this.menu),
				scope:this
				}
				}});	
	this.userLoad=new Ext.tree.TreeLoader({
				url : "onlineUser.ejf?cmd=deptUser&pageSize=-1",
				listeners : {
					'beforeload' : function(treeLoader, node) {
						treeLoader.baseParams.id = (node.id.indexOf('root') < 0
								? node.id
								: "");
					}
				}
			});			
	this.add({title:"员工信息",iconCls:"icon-online",
			layout:"fit",
			//collapsed:true,	
			border : false,
			items:{
				id:"userList1",
				xtype:"treepanel",
				autoScroll:true,
				clearOnLoad:false,
				rootVisible:false,		
				border : false,
				loader:this.userLoad,	
				root:new Ext.tree.AsyncTreeNode({id:"root",text:"根"}),
				listeners:{
					"render":function(tree){tree.root.expand();},
					"dblclick":this.chat,
					"contextmenu":this.showMenu(this.menu),
					 scope:this
					}
	}});
	this.add({title:"最近联系人",iconCls:"icon-timelist",
			layout:"fit",
			collapsed:true,	
			border : false,
			items:{
				id:"resentUser",
				xtype:"treepanel",
				autoScroll:true,
				clearOnLoad:false,
				rootVisible:false,		
				lines:false,
				border : false,
				loader:new Ext.tree.TreeLoader({url:"onlineUser.ejf?cmd=recentChatUser"}),	
				root:new Ext.tree.AsyncTreeNode({text:"根"}),
				listeners:{
					"render":function(tree){tree.root.expand();},
					"dblclick":this.chat,
					"contextmenu":this.showMenu(this.menu),
					scope:this
				}
		}});
	} 
});


MettingManager={};
Ext.apply(MettingManager,OnlineMessageManager);
Ext.apply(MettingManager,{
	currentWin:null,
	removeMetting:function(id){
		Ext.Ajax.request({
		url:"chatRoom.ejf?cmd=remove&id="+id,
		success:function(response,options){
			var obj=Ext.decode(response.responseText);
			if(!obj){				
				Ext.Msg.alert("提示!","您没有删除在线课堂的权限!");
			}
			else Ext.Msg.alert("提示!","删除成功!");
		},
		scope:this	
		});	
	},
	stopMetting:function(id){
		Ext.Ajax.request({
		url:"chatRoom.ejf?cmd=close&id="+id,
		success:function(response,options){
			var obj=Ext.decode(response.responseText);
			if(!obj){				
				Ext.Msg.alert("提示!","您没有关闭在线课堂的权限!");
			}
			else Ext.Msg.alert("提示!","操作成功!");
		},
		scope:this	
		});	
	},
	startMetting:function(id){
	Ext.Ajax.request({
		url:"chatRoom.ejf?cmd=start&id="+id,
		success:function(response,options){
			var obj=Ext.decode(response.responseText);
			if(!obj){				
				Ext.Msg.alert("提示!","您没有启动在线课堂的权限!");
			}
			else Ext.Msg.alert("提示!","操作成功!");
		},
		scope:this	
		});	
	},
	joinMeeting:function(id){
		var winId="messageWin_"+id;
		var msgWin=Ext.getCmp(winId);
		if(msgWin){
			msgWin.show();
			return;
		}
		Ext.Ajax.request({
		url:"chat.ejf?cmd=main&id="+id,
		success:function(response,options){
			var obj=Ext.decode(response.responseText);
			if(obj.success){
				this.openMessage.call(MettingManager,{room:obj.data});
			}else{
				Ext.Msg.alert("提示",obj.errors.msg);
			}
		},
		scope:this	
		});
	},	
	openMessage:function(message){
		var winId="messageWin_"+message.room.id;
		var msgWin=Ext.getCmp(winId);
		if(msgWin){
			msgWin.show();
		}else{
		msgWin=new MettingMessageWindow({id:winId,room:message.room});
		if(this.wins.length>10){
			this.wins[0].close();
			this.wins.remove(this.wins[0]);			
		}
		this.wins.push(msgWin);
		msgWin.show();
		//如果是第一次加载，则启动任务
		if(msgWin.firstLoad){
			msgWin.loadMessage.call(msgWin);
			msgWin.announce.body.update(message.room.announce);
			if(message.room.beginTime)
			msgWin.beginTime.el.innerHTML="<font color=blue>"+message.room.beginTime+"</font>";
			if(message.room.endTime)
			msgWin.endTime.el.innerHTML="<font color=blue>"+message.room.endTime+"</font>";
    		if(message.room.teacher)
			msgWin.teacher.el.innerHTML="<font color=blue>"+message.room.teacher+"</font>";    	   
			msgWin.firstLoad=false;
		}
		}
		if(message.content){
		var obj={msg:message.content,date:message.vdate,reciver:message.reciver,user:{name:message.sender,id:message.sender},cls:message.sender==OnlineMessageManager.me.name?"myMsg":"yourMsg"};
		msgWin.addMessage(obj);
		msgWin.lastReadId=message.id;
		}
	},
   talkTo:function(name){
   	if(this.currentWin){
   	this.currentWin.msgTarget.el.innerHTML="<font color=blue>"+name+"</font>";
	this.currentWin.allPerson.setDisabled(false);
	this.currentWin.reciver=name;
   	}
   },
   showUserInfo:function(name){
   	var fn=OnlineMessageManager.showUserInfo.createDelegate(OnlineMessageManager,[null,name]);
   	fn();
   },
   winMgr:new Ext.WindowGroup()  
});

MettingMessageWindow=Ext.extend(Ext.Window,{
	reciver:"所有人",
	firstLoad:true,
	room:{},
	autoClose:false,
	haveExit:false,
	lastReadId:-1,
	manager:MettingManager.winMgr,
	title:"会议室",
	width:780,
	height:540,
	layout:"border",
	minWidth:300, 
	closeAction:"hide",
	iconCls:"icon-im",	
	maximizable:true,
	minimizable:true,
	shim:false,
    animCollapse:false,
    constrainHeader:true,	
    showMessage:function(ret){
    var msgList=ret.msgList;
   	if(msgList && msgList.length>0){
    	for(var i=0;i<msgList.length;i++){
    		var m=msgList[i];
    		Ext.apply(m,{room:{id:m.roomId}});
    		MettingManager.openMessage(m);
    		if(i==msgList.length-1)this.lastReadId=m.id;
    	}
    }
   var userList=ret.userList;   
   if(userList&&userList.length>0){
   	while(this.onlineList.root.firstChild)this.onlineList.root.firstChild.remove();
   	for(var i=0;i<userList.length;i++){
   		this.onlineList.root.appendChild(new Ext.tree.TreeNode({
   			text:userList[i].userName,
   			icon:"images/user.gif"
   		}));
   	}
   }
   },
   loadMessage:function(){
   if(OnlineMessageManager.me.id && !OnlineMessageManager.stopRecive && !this.haveExit){
   	try{
   	Ext.Ajax.request({
   		url:"chat.ejf?cmd=recive",
   		params:{id:this.room.id,lastReadId:this.lastReadId},
   		callback:function(options,success,response)
   		{
   			if(success){
   				var ret=Ext.decode(response.responseText);
   				//if(ret && ret.msgList&&ret.msgList.length>0){
   				if(ret){
   					this.showMessage(ret);				
   				}
   			}
   			if(!OnlineMessageManager.stopRecive)this.loadMessage.defer(OnlineMessageManager.period,this);
   		},
   		scope:this
   	}); 
   	}catch(e){
   		//alert(e);
   	}  	
   	
   }
   },
   loadHistory:function(){
    	   	
    },  
    exit:function(){
    	Ext.Msg.confirm("请确认","真的要退出当前课堂吗？",function(btn){
    		if(btn=="yes"){
    	Ext.Ajax.request({
   		url:"chat.ejf?cmd=exit&id="+this.room.id,
   		success:function(response)
   		{
   			this.haveExit=true;
   			this.close();
   			MettingManager.wins.remove(this);
   		},
   		scope:this
   	});   	
	 }
	 },this);
    },
    cleanHistory:function(){
    	var msgPanel=this.findById("msgArea"+this.room.id);
    	msgPanel.body.update("");	
    },
    addMessage:function(obj){
    	var msgPanel=this.findById("msgArea"+this.room.id);
    	var m="<div><div class='"+obj.cls+"'><span onclick='MettingManager.talkTo(\""+obj.user.name+"\")' ondblclick='MettingManager.showUserInfo(\""+obj.user.name+"\")' style='cursor:pointer'><b>"+obj.user.name+"</b></span> 对 <span onclick='MettingManager.talkTo(\""+obj.reciver+"\")' ondblclick='MettingManager.showUserInfo(\""+obj.reciver+"\")' style='cursor:pointer'><b>"+obj.reciver+"</b></span>　"+obj.date.format("Y-m-d H:i:s")+"</div><div>　";
    	m+=obj.msg+"</div></div>";
    	msgPanel.body.insertHtml("beforeEnd",m);
    	msgPanel.body.scroll("bottom",100);
    },
    sendMessage:function(){    	    	
    	var msg=this.findById("editor"+this.room.id);
    	var m=msg.getValue();    	
    	if(m!=""){    	
    	if(!msg.isValid())return;
    	var obj={msg:m,date:new Date(),user:OnlineMessageManager.me,cls:"myMsg"};
    	try{
    	Ext.Ajax.request({
    		url:"chat.ejf?cmd=send",
    		params:{id:this.room.id,reciver:this.reciver,lastReadId:this.lastReadId,content:m},
    		scope:this,
    		success:function(response,options){
    			var ret=Ext.decode(response.responseText);
    			this.showMessage(ret);
    			},
    		failure:function(){}//给出信息没发送成功的提示
    		});
    	}catch(e){
    		//alert(e);
    	}
    	//this.addMessage(obj);
    	this.lastMsg=m;
    	msg.setValue("");
    	if(this.autoClose)this.hide();
    	}
    	msg.focus();
    },   
    clickEvent:function(e){
        	if(e.getTarget().tagName=="A"||e.getTarget().tagName=="a"){
        		e.getTarget().target="_blank";
        	}
        	},
    initComponent : function(){
    	 this.beginTime=new Ext.Toolbar.TextItem("<font color=blue>未知</font>");
    	 this.endTime=new Ext.Toolbar.TextItem("<font color=blue>未知</font>");
    	 this.teacher=new Ext.Toolbar.TextItem("<font color=blue>Vifir顾问</font>");
		 this.tbar=["<img src='images/qq/techer.gif'/>","主讲:",this.teacher,{text:"查看详情",cls:"x-btn-text-icon",icon:"images/qq/zoom.gif"},"-","<img src='images/core/start.gif'/>","开始时间:",this.beginTime,"-","<img src='images/core/stop.gif'/>","结束时间:",this.endTime,"->",{text:"申请发言",cls:"x-btn-text-icon",icon:"images/qq/ren1.gif",disabled:true},{text:"退出课堂",cls:"x-btn-text-icon",icon:"images/qq/exit.gif",handler:this.exit,scope:this}];
		 this.title="Vifir在线课堂："+this.room.title;
		 if(this.room){
		 	this.id="message"+this.room.id;
		 }		 
		 MettingMessageWindow.superclass.initComponent.call(this);
		 this.onlineList=new Ext.tree.TreePanel({
		 	title:"参与人员",
		 	height:"100%",
		 	autoScroll:true,
		 	root:new Ext.tree.TreeNode(),
		 	rootVisible:false,
		 	lines:false});
		 this.onlineList.on("click",function(node){
		 	this.msgTarget.el.innerHTML="<font color=blue>"+node.text+"</font>";
			this.allPerson.setDisabled(false);
			this.reciver=node.text;
		 },this);	
		 this.announce=new Ext.Panel({title:"课堂公告",
		 		html:"",
		 		anchor:"100% 30%"});	 	
		 this.historyList=new Ext.tree.TreePanel({
		 				title:"会议记录",
		 				height:"100%",
		 				autoScroll:true,
		 				root:new Ext.tree.AsyncTreeNode(),
		 				loader:new Ext.tree.TreeLoader({url:"chatRoom.ejf?cmd=listHistory&id="+this.room.id}),
		 				rootVisible:false});
		 this.historyList.on("click",function(node){
		 	if(!node.attributes.dir){
		 		window.open("chatRoom.ejf?cmd=showHistory&id="+this.room.id+"&fileName="+node.parentNode.text+"/"+node.text);
		 		}
		 	},this);		 
		 this.left=new Ext.Panel({
		 	region:"east",
		 	width:180,
		 	layout:"anchor",
		 	items:[this.announce,
		 		{xtype:"tabpanel",
		 		anchor:"100% 70%",
		 		activeTab:0,
		 		items:[
		 		this.onlineList,
		 		this.historyList
		 		]}
		 		]});
		 this.center=new Ext.Panel({region:"center",layout:"anchor"});
		 this.center.add({
			anchor:"100% 60%",
			layout:"fit",
			border : false,
			items:{
				id:"msgArea"+this.room.id,
				autoScroll:true,
				tbar:this.tbar
			}
		});
		this.editor=new ChatEditor({id:"editor"+this.room.id,				   
				   name:"editor"+this.room.id,	
				   maxLength:2000,			   
				   listeners:{"activate":function(){}},
				   keys:[{
						    key:Ext.EventObject.ENTER,
						    ctrl: true,
						    fn:this.sendMessage,
						    scope: this
						},
						{
						    key:'s',
						    alt: true,
						    fn:this.sendMessage,
						    scope: this
						},
						{
						    key:'cx',
						    alt: true,
						    fn:function(){this.hide()},
						    scope: this
						}]   
				   }
		);
		this.msgTarget=new Ext.Toolbar.TextItem("<font color=blue>所有人</font>");	
		this.allPerson=new Ext.Toolbar.Button({text:"所有人",disabled:true,handler:function(){
			this.msgTarget.el.innerHTML="<font color=blue>所有人</font>";
			this.allPerson.setDisabled(true);
			this.reciver="所有人";
		},scope:this});	
		this.center.add({	
			frame:true,
			border : false,
			anchor:"100% 40%",
			hideBorders : true,
			tbar:[{text:"清空记录",cls:"x-btn-text-icon",icon:"images/qq/cleanHistory.gif",handler:this.cleanHistory,scope:this},"-",
				  {text:"传递附件",cls:"x-btn-text-icon",icon:"images/qq/updown.gif",disabled:true},"是否自动关闭",
				  {
				 	xtype:"checkbox",
				 	checked:this.autoClose,
				 	listeners:{
					 	"check":function(c,chk){
					 		this.autoClose=chk;			 	
					 	},
						scope:this
					}
				   },"-","发言对象：",this.msgTarget,this.allPerson
				],
			layout:"fit",
			items:this.editor,
			buttons:[{text:"发送",handler:this.sendMessage,scope:this},{text:"关闭",handler:function(){this.hide();},scope:this}]		
			});
		this.add(this.center);
		this.add(this.left);	
    	},
	listeners:{
		"show":function(win){
			var c=Ext.getCmp("editor"+this.room.id);
			c.getEditorBody().focus();
			MettingManager.currentWin=this;
			Ext.get(c.getEditorBody()).on("focus",function(){this.show();},this);
			var msgPanel=this.findById("msgArea"+this.room.id);
			if(!this.loadClickEvent){
        	msgPanel.body.on("click",this.clickEvent);
        	this.loadClickEvent=true;
			}  
		}
	}	
});

Ext.data.DWRProxy = function(fn){
    Ext.data.DWRProxy.superclass.constructor.call(this);
    this.fn = fn;
};
Ext.extend(Ext.data.DWRProxy, Ext.data.DataProxy, {  
    load : function(params, reader, callback, scope, arg){
        params = params || {};       
        if(this.fireEvent("beforeload", this, params) !== false){
        	var proxy=this;
	        this.fn(params,function(ret){
	        var result;
	        try {
	            result = reader.readRecords(ret);
	        }catch(e){
	            this.fireEvent("loadexception", this, arg, null, e);
	            callback.call(scope, null, arg, false);
	            return;
	        }
	        callback.call(scope, result, arg, true);
	      	//fireEvent("load", scope,arg, result);
	        }); 
        }   
    }  
});

Ext.data.DWRStore = function(c){
    Ext.data.DWRStore.superclass.constructor.call(this, Ext.apply(c, {
        proxy: c.fn ? new Ext.data.DWRProxy(c.fn): undefined,
        reader: new Ext.data.JsonReader(c, c.fields)
    }));
};
Ext.extend(Ext.data.DWRStore, Ext.data.Store);


OnlineMessageManager.me={id:'1',name:'admin'};

