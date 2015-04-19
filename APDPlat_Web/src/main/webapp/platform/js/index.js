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

//显示报警信息
var last;
var model;
var tree;
var root;
var id=1;
var tab;
var viewport;
var filter;
var westPanel;
var maxWindows=15;

function show(moduleName){
    if(westPanel.collapsed){
        westPanel.expand(true);
    }
    var i=0;
    var module=westPanel.getComponent(i);
    while(module!=undefined){
        if(module.iconCls==moduleName){
            module.expand();
        }else{
            module.collapse();
        }
        
        i++;
        module=westPanel.getComponent(i);
    }
}
function process(str){
    str=str.replace("A", "-a");
    str=str.replace("B", "-b");
    str=str.replace("C", "-c");
    str=str.replace("D", "-d");
    str=str.replace("E", "-e");
    str=str.replace("F", "-f");
    str=str.replace("G", "-g");
    str=str.replace("H", "-h");
    str=str.replace("I", "-i");
    str=str.replace("J", "-j");
    str=str.replace("K", "-k");
    str=str.replace("L", "-l");
    str=str.replace("M", "-m");
    str=str.replace("N", "-n");
    str=str.replace("O", "-o");
    str=str.replace("P", "-p");
    str=str.replace("Q", "-q");
    str=str.replace("R", "-r");
    str=str.replace("S", "-s");
    str=str.replace("T", "-t");
    str=str.replace("U", "-u");
    str=str.replace("V", "-v");
    str=str.replace("W", "-w");
    str=str.replace("X", "-x");
    str=str.replace("Y", "-y");
    str=str.replace("Z", "-z");
    return str;
}
function openTab(node,event,url){
    event.stopEvent();
    
    openWindow(node.text,node.attributes.iconCls,url);
}
                        
function openWindow(text,iconCls,url){
    var exist=false;
    tab.items.each(function(item){
        if(item.title==text && item.iconCls==iconCls){
            tab.setActiveTab(item);
            exist=true;
        }
    });
    if(exist){
        return;
    }
    if(id>maxWindows){
        if(tab.getItem("windows"+(id-maxWindows))){
            Ext.get("windows"+(id-maxWindows)).src = false;
            tab.remove("windows"+(id-maxWindows));
        }
    }
    
    Ext.Msg.wait('页面加载中...','请稍后');

    if(url == null || url == "null" || url == ""){
        url = "working.jsp";
    }
    n = tab.add({
        'id':  "windows"+id,
        'iconCls':iconCls,
        'title': text,
        closable: true,  //通过html载入目标页
        scripts:true,
        html: '<iframe id="'+"windows"+id+'" name="'+"windows"+id+'" scrolling="auto" frameborder="0" width="100%" height="100%" onload="Ext.Msg.hide()" src="'+url+'"></iframe>'
    });
    tab.setActiveTab(n);
    id++;
}
function closeWindow(text){
    tab.items.each(function(item){
        if(item.closable&&item.title==text)
        {
            tab.remove(item);
        }
    });
}
function createTab(){
//生成标签页
 tab = new Ext.TabPanel({
    region:'center',
    deferredRender:false,
    activeTab:0,
    resizeTabs:false,
    minTabWidth: 115,
    enableTabScroll:true,
    //下面是右键菜单代码
    listeners:{
        //传进去的三个参数分别为:这个tabpanel,当前标签页,事件对象e
        "contextmenu":function(tabPanel,myitem,e){
            menu=new Ext.menu.Menu([{
                iconCls:'close',
                text:"关闭当前窗口",
                handler:function(){
                    if(myitem.closable){
                        tabPanel.remove(myitem);
                    }
                }
            },{
                iconCls:'closeOther',
                text:"关闭其他所有窗口",
                handler:function(){
                    //循环遍历
                    tabPanel.items.each(function(item){
                        if(item.closable&&item!=myitem)
                        {
                            //可以关闭的其他所有标签页全部关掉
                            tabPanel.remove(item);
                        }
                    });
                }
            }]);
            //显示在当前位置
            menu.showAt(e.getPoint());
        }
    },
    items:[indexPage]
});
}
function createViewport(){
    westPanel = new Ext.Panel({
        region : 'west',
        id : 'west-panel', 
        title : '功能菜单',
        iconCls : 'menu',
        split : true,
        width : 200,
        autoScroll : true,
        layout : 'accordion',
        collapsible : true,
        margins : '0 0 0 2',
        layoutConfig: {
            animate: true
        },
        items : []
    });
    // 构建左边的菜单
    Ext.Ajax.request({
        url : contextPath + '/module/module/store.action?node=root&recursion=false',
        success : function(response, options) {
                var arr = eval(response.responseText);
                var activedPanelId = Ext.util.Cookies.get("activedPanelId");
                for (var i = 0; i < arr.length; i++) {
                        var panel = new Ext.tree.TreePanel({
                                id : arr[i].id,
                                title : arr[i].text,
                                iconCls : arr[i].iconCls,
                                autoScroll : true,
                                border : false,
                                loader : new Ext.tree.TreeLoader({
                                        dataUrl : contextPath + '/module/module/store.action?recursion=false'
                                }),
                                root : new Ext.tree.AsyncTreeNode({
                                        text:arr[i].text,
                                        id : arr[i].id,
                                        iconCls : arr[i].iconCls,
                                        expanded : true
                                }),
                                listeners : {
                                        'click' : function(node, event){
                                            node.toggle();
                                        }
                                },
                                rootVisible : false
                        });
                        westPanel.add(panel);
                        // 记住上次点击的panel
                        panel.on('expand', function(p) {
                                Ext.util.Cookies.set('activedPanelId', p.id, new Date(new Date().getTime()+(1000*60*60*24*7)), contextPath);
                        });
                        // 激活上次点击的panel
                        if (arr[i].id == activedPanelId) {
                                westPanel.layout.activeItem = panel;
                        }
                }
                westPanel.doLayout();
        }
    });
      var storeTheme=Ext.util.Cookies.get('theme');
      if(storeTheme==null || storeTheme==''){
              storeTheme='ext-all';
      }
   viewport = new Ext.Viewport({
        layout:'border',
        items:[
        new Ext.Panel({
            region:"north",
            id:"northPanel",
            contentEl:"north",
            height:60})
        ,
        new Ext.Panel({
            region:"south",
            height:28,
            border:false,
            bbar:[{
                    text:"退出系统",
                    iconCls:"logout",
                    handler:function(){
                        logout();
                    }
                },"-",{
                    text:"在线用户",
                    iconCls:"onlineUser",
                    handler:function(){
                        //new OnlineChatWindow().show();
                        OnlineUser.show();
                    }
                },"-",{
                    text:"修改密码",
                    iconCls:"updatePart",
                    handler:function(){
                        ModifyWindow.show();
                    }
                },"-",{
                    id:"messageTip",
                    xtype:"button",
                    hidden:true,
                    handler:function(){
                        
                    }
                },"->",{
                    xtype:"tbfill"
                },{
                    xtype:"tbtext",
                    text:appName,
                    id:"toolbarCompanyName"
                },{
                    xtype:"tbseparator"
                },
                new Ext.Toolbar.TextItem(support),
                {
                    xtype:"tbseparator"
                },
                {
                    pressed:false,
                    iconCls:"contact",
                    text:"与我联系",
                    handler:function(){
                        Ext.ux.Toast.msg("联系我",contact);
                    }
                },"-",{
                    text:"收缩顶部",
                    iconCls:"expand",
                    handler:function(){
                        trigger(this);
                    }
                },"-",{            
                    xtype:'iconcombo',
                    mode:"local",
                    editable:false,
                    textAlign:'right',
                    style:'text-align: right;',
                    value:storeTheme,
                    width:105,
                    triggerAction:"all",
                    store : new Ext.data.SimpleStore({
                            fields : ['id','text','icon'],
                            data :[
                                        ["ext-all","缺省浅蓝","default"],
                                        ["xtheme-midnight","深蓝风格","midnight"],
                                        ["xtheme-blue","紫色风格","blue"],
                                        ["xtheme-tp","灰绿风格","tp"],
                                        ["xtheme-pink","粉红风格","pink"],
                                        ["xtheme-access","黑白风格","access"],
                                        ["xtheme-orange","橙色风格","orange"],
                                        ["xtheme-red5","红色风格","red5"]
                                  ]
                    }),
                    valueField: 'id',
                    displayField: 'text',
                    iconClsField: 'icon',
                    listeners:{
                        scope:this,
                        "select":function(d,b,c){
                            if(d.value!=""){
                                Ext.util.Cookies.set('theme', d.value, new Date(new Date().getTime()+(1000*60*60*24*7)), contextPath);
                                refreshAll();
                            }
                        }
                    }
                }]})
        ,westPanel,
        tab//初始标签页
        ]
    });
}

function triggerHeader(){
    var northPanel=Ext.getCmp("northPanel");
    var triggerImage=document.getElementById("trigger-image");
    if(northPanel.collapsed){
        northPanel.expand(true);
        triggerImage.src="images/trigger-up.png";
    }else{
        northPanel.collapse(true);
        triggerImage.src="images/trigger-down.png";
    }
}
function trigger(tip){
    var northPanel=Ext.getCmp("northPanel");
    if(northPanel.collapsed){
        tip.setIconClass("collapse");
        tip.setText("收缩顶部");
        northPanel.expand(true);
    }else{
        tip.setIconClass("expand");
        tip.setText("展开顶部");
        northPanel.collapse(true);
    }
}
function logout(){
    window.location.href="../j_spring_security_logout";
}
var tip;
function beforerequest(conn, options){
    if (options.waitMsg) {
            tip=Ext.Msg.wait(options.waitMsg, options.waitTitle || '请稍候');
    }
}
var reLogining=false;
function requestcomplete(conn, resp,options){
    if(tip){
        tip.hide();
    }
    if (resp && resp.getResponseHeader){  
        if(resp.getResponseHeader('login')) {  
            //window.location.href=contextPath+'/login.jsp?state='+resp.getResponseHeader('state');  
            if(!reLogining){
                reLogining=true;
                ReLoginWindow.show();
            }
        }  
        if(resp.getResponseHeader('accessDenied')){  
            Ext.ux.Toast.msg('系统访问权限提示：','你目前没有权限访问：{0}',options.url);  
        }  
        if("communication failure"==resp.statusText){
                //断网或服务器停机
                parent.Ext.ux.Toast.msg('访问错误：','无法连接到服务器，可能网络出现故障，或后台服务已停止');  
                setInterval(function(){
                    parent.window.location.href=contextPath+'/login.jsp?state=error';  
                },2000);
        }
    } 
}
function requestexception(conn, resp,options){
        if(tip){
            tip.hide();
        }
        if(resp && resp.getResponseHeader){
            requestcomplete(conn, resp,options);
        }
        else{
            var code = resp.status || 0;
            switch (code) {
                    case 0 :
                    case 12002 :
                    case 12029 :
                    case 12030 :
                    case 12031 :
                    case 12152 :
                    case 13030 :
                            parent.Ext.ux.Toast.msg("异常提示：", "您的网络连接发生中断,通讯异常!");
                            return false;
                            break;
                    case -1 :
                            parent.Ext.ux.Toast.msg("异常提示：","您的请求已经被自动取消,通讯超时!");
                            return false;
                            break;
                    case 403 :
                            parent.Ext.ux.Toast.msg('系统访问权限提示：','你目前没有权限访问：{0}',options.url);  
                            return false;
                            break;
                    default :
                            if (code < 200 || code >= 300) {
                                    var data = Ext.decode(resp.responseText);
                                    var errMsg = "发生了其它通讯异常，异常状态编码为" + code + "警告!"; 
                                    if(Ext.getObjVal(data,'err')){
                                            errMsg = Ext.getObjVal(data,'err');
                                    }
                                    Ext.ux.Toast.msg("异常提示：",errMsg);
                                    return false;
                            }
            }
        } 
        return false;
}
function watch(){
    Ext.util.Observable.observeClass(Ext.data.Connection); 
    Ext.data.Connection.on("beforerequest", function(conn, options) {
        beforerequest(conn, options);
    });
    Ext.data.Connection.on('requestcomplete', function(conn, resp,options ){  
        requestcomplete(conn, resp,options);
    });  
    Ext.data.Connection.on('requestexception', function(conn, resp,options ){  
        requestexception(conn, resp,options);
    }); 
}

var barcode="";
Ext.onReady(function(){
    createTab();
    createViewport();
    //当用户会话超时退出到登陆页面
    //当权限拒绝的时候提示用户
    watch();
    Ext.get('loading-mask').fadeOut( {
            remove : true
    });
    //搜索表单，可以在这里进行权限控制
    Ext.getCmp("searchForm").render("search");

    /**
     * 屏蔽退格键
     */
    new Ext.KeyMap(document.documentElement, [{
            key : Ext.EventObject.BACKSPACE,
            fn : function(key, e) {
                    var regExp = /(?:INPUT|TEXTAREA)/;
                    if (!regExp.test(e.getTarget().nodeName)) {
                            e.stopEvent();
                    }
            }
    },{
            key : 'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
            shift:true,
            fn : function(key, e) {
                //alert(key);
                var iframeId=tab.getActiveTab().id;
                //alert(iframeId);
                var subWindow=window.frames[iframeId];
                if(subWindow!=undefined){
                    subWindow.activeKey(key);
                }
            }
    },{
            key : '1234567890',
            fn : function(key, e) {
                barcode+=keyToLetter.get(key);
                return;
            }
    },{
        key: [10,13],
        fn : function(key, e) {
            e.stopEvent();
            if(barcode.length!=13){
                //没有扫描到合适的条码
                barcode="";
                return;
            }
            
            var iframeId=tab.getActiveTab().id;
            //alert(iframeId);
            var subWindow=window.frames[iframeId];
            if(subWindow!=undefined){
                if((typeof subWindow.scanBarcode) == "function"){
                    subWindow.scanBarcode(barcode);
                }
            }
            //清空缓存
            barcode="";
        }
    },{
        key: [32],
        fn : function(key, e) {
            e.stopEvent();
            
            var iframeId=tab.getActiveTab().id;
            //alert(iframeId);
            var subWindow=window.frames[iframeId];
            if(subWindow!=undefined){
                if((typeof subWindow.blank) == "function"){
                    subWindow.blank();
                }
            }
        }
    }]);

    fixPng();
});