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

//fieldset的验证
Ext.QuickTips.init();//支持tips提示
Ext.form.Field.prototype.msgTarget='side';//提示的方式，枚举值为"qtip","title","under","side",id(元素id)
Ext.BLANK_IMAGE_URL = contextPath+'/extjs/images/default/s.gif';
var keyToFunction=new Map();
var keyToLetter=new Map();

keyToLetter.put("48","0");
keyToLetter.put("49","1");
keyToLetter.put("50","2");
keyToLetter.put("51","3");
keyToLetter.put("52","4");
keyToLetter.put("53","5");
keyToLetter.put("54","6");
keyToLetter.put("55","7");
keyToLetter.put("56","8");
keyToLetter.put("57","9");

keyToLetter.put("65","A");
keyToLetter.put("66","B");
keyToLetter.put("67","C");
keyToLetter.put("68","D");
keyToLetter.put("69","E");
keyToLetter.put("70","F");
keyToLetter.put("71","G");
keyToLetter.put("72","H");
keyToLetter.put("73","I");
keyToLetter.put("74","J");
keyToLetter.put("75","K");
keyToLetter.put("76","L");
keyToLetter.put("77","M");
keyToLetter.put("78","N");
keyToLetter.put("79","O");
keyToLetter.put("80","P");
keyToLetter.put("81","Q");
keyToLetter.put("82","R");
keyToLetter.put("83","S");
keyToLetter.put("84","T");
keyToLetter.put("85","U");
keyToLetter.put("86","V");
keyToLetter.put("87","W");
keyToLetter.put("88","X");
keyToLetter.put("89","Y");
keyToLetter.put("90","Z");

function activeKey(key){
    //alert("activeKey:"+key);
    var letter=keyToLetter.get(key);
    if(letter==undefined){
        return;
    }
   // alert("activeLetter:"+letter)
    var functions=keyToFunction.get(letter);
    //alert(functions);
    functions();
}

//通用表格
GridBaseModel = function() {
    return {
        setStoreBaseParams: function(store){
            store.on('beforeload',function(store){
                   store.baseParams = {queryString:GridBaseModel.queryString,search:GridBaseModel.search};
            });
        },
        //数据源
        getStore: function(fields,pageSize){
            if(undefined==this.storeURLParameter){
                this.storeURLParameter="";
            }
            //定义数据集对象
            var store = new Ext.data.Store({
                    reader: new Ext.data.JsonReader({
                        totalProperty: 'totalProperty',
                        root: 'root'
                    },
                    Ext.data.Record.create(fields)
                    ),
                    proxy : new parent.Ext.data.HttpProxy({
                            url : this.storeURL+this.storeURLParameter
                    })
            });
            this.setStoreBaseParams(store);
            this.loadStore(store,pageSize);
            return store;
        },   
        loadStoreSuccess: function(store){
            
        },
        loadStore: function(store,pageSize){
            //第一次装载的时候指定页面大小
            store.load({params:{limit:pageSize},callback:this.loadStoreSuccess(store)});
        },
        //底部工具条
        getBBar: function(pageSize,store){
            return new Ext.ux.PageSizePlugin({
                rowComboSelect : true,
                pageSize : pageSize,
                store : store,
                displayInfo : true
            });
        },
        //添加
        create: function(){
            CreateModel.show();
        },
        //删除
        remove: function (){
            var idList=GridBaseModel.getIdList();
            if(idList.length<1){
                parent.Ext.ux.Toast.msg('操作提示：','请选择要进行操作的记录');  
                return ;
            }
            parent.Ext.MessageBox.confirm("请确认","确实要删除吗？",function(button,text){
                if(button == "yes"){
                    GridBaseModel.deleteData(idList.join(','));
                }
            });
        },
        //删除数据
        deleteData: function(ids){
            parent.Ext.Ajax.request({
                url : GridBaseModel.deleteURL+'?time='+new Date().toString(),
                waitTitle: '请稍等',
                waitMsg: '正在删除数据……',
                params : {
                    ids : ids
                },
                method : 'POST',
                success : function(response,opts){
                    GridBaseModel.removeSuccess(response,opts);
                }
            });
        },
        //删除数据成功后的回调
        removeSuccess: function(response,opts){
                var data=response.responseText;
                parent.Ext.ux.Toast.msg('操作提示：','{0}',data);  
                GridBaseModel.refresh();
        },
        //取得所选的数据的特定字段
        getFieldList: function(field){
            var recs = GridBaseModel.grid.getSelectionModel().getSelections();
            var list = [];
            if(recs.length > 0){
                for(var i = 0 ; i < recs.length ; i++){
                    var rec = recs[i];
                    list.push(rec.get(field))
                }
            }
            return list;
        },
        //取得所选
        getIdList: function(){
            return this.getFieldList('id');
        },
        //修改
        modify: function(){
            var idList=GridBaseModel.getIdList();
            if(idList.length<1){
                parent.Ext.ux.Toast.msg('操作提示：','请选择要进行操作的记录');  
                return ;
            }
            if(idList.length==1){
                var id=idList[0];

                parent.Ext.Ajax.request({
                    url : GridBaseModel.retrieveURL+id+GridBaseModel.extraModifyParameters()+'&time='+new Date().toString(),
                    waitTitle: '请稍等',
                    waitMsg: '正在检索数据……',
                    method : 'POST',
                    success : function(response,options){
                        var data=response.responseText;
                        //返回的数据是对象，在外层加个括号才能正确执行eval
                        var model=eval('(' + data + ')');
                        ModifyModel.show(model);
                    }
                });
            }else{
                parent.Ext.ux.Toast.msg('操作提示：','只能选择一个要进行操作的记录！');  
            }
        },
        //详细
        detail: function(){
            var idList=GridBaseModel.getIdList();
            if(idList.length<1){
                parent.Ext.ux.Toast.msg('操作提示：','请选择要进行操作的记录');  
                return ;
            }
            if(idList.length==1){
                var id=idList[0];

                parent.Ext.Ajax.request({
                    url : GridBaseModel.retrieveURL+id+GridBaseModel.extraDetailParameters()+'&time='+new Date().toString(),
                    waitTitle: '请稍等',
                    waitMsg: '正在检索数据……',
                    method : 'POST',
                    success : function(response,opts){
                        var data=response.responseText;
                        //返回的数据是对象，在外层加个括号才能正确执行eval
                        var model=eval('(' + data + ')');
                        DisplayModel.show(model);
                    }
                });
            }else{
                parent.Ext.ux.Toast.msg('操作提示：','只能选择一个要进行操作的记录！');  
            }
        },
        //高级搜索
        advancedsearch: function(){
            AdvancedSearchModel.show();
        },
        //显示全部
        showall: function(){
            GridBaseModel.search=false;
            GridBaseModel.queryString="";
            GridBaseModel.refresh();
        },
        //导出
        exportData: function(){
            parent.Ext.Ajax.request({
                url : GridBaseModel.exportURL+'?time='+new Date().toString(),
                waitTitle: '请稍等',
                waitMsg: '正在导出数据……',
                params : {
                    queryString : GridBaseModel.queryString,
                    propertyCriteria : GridBaseModel.propertyCriteria,
                    search : GridBaseModel.search
                },
                method : 'POST',
                success:function(response, opts){
                    var path = response.responseText;
                    //contextPath定义在引用了此JS的页面中
                    path=this.contextPath+path;
                    window.open(path,'_blank','width=1,height=1,toolbar=no,menubar=no,location=no');
                },
                failure : function(response,options){
                    parent.Ext.ux.Toast.msg('操作提示：', "导出失败");
                }
            });
        },
        //报表
        chart: function(){
            ChartModel.show();
        },
        //右键菜单
        getContextMenu: function(commands,tips,callbacks){
            //右键菜单
            var contextmenu=new Ext.menu.Menu({
                id:'theContextMenu',
                items:[]
            });
            this.addCommands(contextmenu,false,commands,tips,callbacks);
            return contextmenu;
        },
        //顶部工具条
        getToolbar: function(commands,tips,callbacks){
            //工具栏组件
            var toolbar = new Ext.Toolbar();  
            this.addCommands(toolbar,true,commands,tips,callbacks);
            return toolbar;
        },
        refreshStoreSuccess: function(store){
            
        },
        //刷新表格
        refresh: function (){
            this.store.load({
                params:{
                    limit:this.bbar.pageSize,
                    queryString:this.queryString,
                    search:this.search
                },
                callback:function(){
                    GridBaseModel.refreshStoreSuccess(this.store);
                }
            });
            this.grid.getView().refresh();
        },
        //控制右键菜单及顶部工具条中的命令是否是用户有权限拥有的
        //obj工具条 或 菜单条
        //button 为true表示工具条 false表示菜单条
        addCommands: function(obj,button,commands,tips,callbacks){
            if(commands==undefined || tips==undefined || callbacks==undefined){
                return;
            }
            for(var i=0;i<commands.length;i++){
                var command=commands[i];
                var tip=tips[i];
                var callback=callbacks[i];
                if(button){
                    if(command && parent.isGranted(namespace,action,command)){  
                         obj.add(new Ext.Button({  
                                    iconCls : command,  
                                    text : tip,  
                                    handler : callback
                                }));  
                    }  
                }else{
                    if(command && parent.isGranted(namespace,action,command)){  
                         obj.add(new Ext.menu.Item({  
                                    iconCls : command,  
                                    text : tip,  
                                    handler : callback
                                }));  
                    }  
                }
            }  
        },    
        //修改单个字段
        afterEdit: function(obj){
            var key=obj.field;
            var value=obj.value;
            var r=obj.record;
            var id=r.get("id");
            var version=r.get("version");
            this.updateAttr(id,key,value,version);
        },
        //提交单个属性修改数据
        updateAttr: function(id,key,value,version){
            //在此要利用encodeURI对传递的值进行编码
            //因为ie不会对传递的参数编码，只会对路径进行编码
            //firefox会对参数和路径都编码
            //配合后台    <Connector port="8080" protocol="HTTP/1.1" 
            //            connectionTimeout="20000" 
            //            redirectPort="8443"  URIEncoding="utf-8"/>
            //就可以实现对中文参数的正确使用

            parent.Ext.Ajax.request({
                url : this.updatePartURL+id+"&model."+key+"="+encodeURI(value)+"&model.version="+version+GridBaseModel.extraModifyParameters(),
                method : 'POST',
                success:function(response, opts){
                    GridBaseModel.updateAttrSuccess(response, opts);
                    var data=response.responseText;
                    var tip=eval('(' + data + ')');
                    parent.Ext.ux.Toast.msg('操作提示：','{0}',tip.message);  
                }
            });
        },
        extraModifyParameters: function(){
            return "";
        },        
        extraDetailParameters: function(){
            return "";
        },      
        extraCreateParameters: function(){
            return "";
        },
        //单个属性更新成功后的回调
        updateAttrSuccess: function(response, opts){
            GridBaseModel.refresh();
        },
        changeURL: function(contextPath,namespace,action){
            this.initURL(contextPath,namespace,action);
            if(this.store){
                this.store.proxy.setUrl(this.storeURL+this.storeURLParameter,true); 
            }
        },
        initURL: function(contextPath,namespace,action){
            this.contextPath=contextPath;
            this.namespace=namespace;
            this.action=action;
            //批量查询
            this.storeURL=contextPath+'/'+namespace+'/'+action+'/query.action';
            //单条查询
            this.retrieveURL=contextPath+'/'+namespace+'/'+action+'/retrieve.action?model.id=';
            //批量删除
            this.deleteURL=contextPath+'/'+namespace+'/'+action+'/delete.action';
            //导出数据
            this.exportURL=contextPath+'/'+namespace+'/'+action+'/export.action';
            //添加一条数据
            this.createURL=contextPath+'/'+namespace+'/'+action+'/create.action';
            //修改部分数据
            this.updatePartURL=contextPath+'/'+namespace+'/'+action+'/updatePart.action?model.id=';
        },
        showBefore: function(){
            
        },
        getGrid: function(contextPath,namespace,action,pageSize, fields, columns, commands,tips,callbacks){            
            if(tips!=undefined && callbacks!=undefined){
                var keyMaps=[];
                for(var i=0;i<tips.length;i++){
                    var tip=tips[i];
                    var start=tip.indexOf("(");
                    var end=tip.indexOf(")");
                    if(start!=-1 && end!=-1){
                        var key=tip.toString().substring(start+1, end);
                        keyToFunction.put(key, callbacks[i]);
                        var keyMap={
                         key: key,
                         fn: callbacks[i]
                        };
                        keyMaps.push(keyMap);
                    }
                }
                this.extKeyMap = new Ext.KeyMap(document.documentElement, keyMaps);
            }
         
            this.initURL(contextPath,namespace,action);            
            
            this.store = this.getStore(fields,pageSize);
            this.toolbar=this.getToolbar(commands,tips,callbacks);
            this.contextmenu=this.getContextMenu(commands,tips,callbacks);
            this.bbar=this.getBBar(pageSize, this.store);
            var cb = new Ext.grid.CheckboxSelectionModel();
            var preColumns=[//配置表格列
                            new Ext.grid.RowNumberer({
                                    header : '行号',
                                    width : 40
                            }),//表格行号组件
                            cb
                    ];
            columns=preColumns.concat(columns);  
            this.grid = new Ext.grid.EditorGridPanel({
                    title:' ',
                    autoHeight: true,
                    frame:true,
                    store: this.store,
                    tbar : this.toolbar,
                    bbar: this.bbar,
                    stripeRows : true,
                    autoScroll : true,
                    viewConfig : {
                        loadingText : '数据加载中,请稍等...',
                        emptyText : '无对应信息',
                        deferEmptyText : true,
                        autoFill : true,
                        forceFit:true  
                    },
                    sm : cb,
                    columns: columns,
                    clicksToEdit:1,
                    plugins: this.getPlugins(),
                    keys:this.getKeys()
            });    
            this.grid.on("rowcontextmenu",function(grid,rowIndex,e){
                    e.preventDefault();
                    grid.getSelectionModel().selectRow(rowIndex);
                    GridBaseModel.contextmenu.showAt(e.getXY());
            });
            this.grid.on("afteredit",function(obj) {
                    GridBaseModel.afterEdit(obj);
                }
            );
            this.grid.on('rowdblclick',function(grid,index,e){
                GridBaseModel.onRowDblClick(namespace,action);
            });
            this.showBefore();
            return this.grid;
        },
        getPlugins: function(){
            //return [Ext.ux.plugins.Print];
            return [];
        },
        getKeys: function(){
            return [];
        },
        onRowDblClick : function(namespace,action){
            if(parent.isGranted(namespace,action,"updatePart")){     
                GridBaseModel.modify();
            }
        },
        getViewport: function(){
             this.viewport = new Ext.Viewport({            
                layout:'border',         
                items:[
                    {
                        region:'center',
                        autoScroll:true,
                        layout: 'fit',
                        items:[this.grid]
                    }
                ]
            });    
            return this.viewport;
        },
        show : function(contextPath,namespace,action,pageSize, fields, columns, commands,tips,callbacks){
            this.grid=this.getGrid(contextPath,namespace,action,pageSize, fields, columns, commands,tips,callbacks);
            this.getViewport();    
        }
    };
} ();

//高级搜索
AdvancedSearchBaseModel = function() {
    return {
        getLabelWidth: function(){
            return 120;
        },
        getForm: function(items) {
             var labelWidth=this.getLabelWidth();
             var frm = new parent.Ext.form.FormPanel({
                labelAlign: 'left',
                buttonAlign: 'center',
                bodyStyle: 'padding:5px',
                frame: true,//圆角和浅蓝色背景
                labelWidth: labelWidth,
                autoScroll:true,
                
                defaults: {
                    anchor: '95%'
                },
                items: items,

                buttons: [{
                    text: '搜索',
                    iconCls:'search',
                    scope: this,
                    handler: function() {
                        this.sure();
                    }
                },
                {
                    text: '重置',
                    iconCls:'reset',
                    scope: this,
                    handler: function() {
                        this.frm.form.reset();
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
                        this.sure();
                     },
                     scope : this
                 }]
            });
            return frm;
        },

        getDialog: function(title,iconCls,width,height,items) {
            this.frm = this.getForm(items);
            var dlg = new parent.Ext.Window({
                maximizable:true,
                title: title,
                iconCls:iconCls,
                width:width,
                height:height,
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

        show: function(title,iconCls,width,height,items,callback) {
            //注册函数，指定处理方式
            this.sure=callback;
            this.dlg = this.getDialog(title,iconCls,width,height,items);
            this.dlg.show();
        },

        reset: function(){
            this.frm.form.reset();
        },

        close: function(){
            if(this.dlg!=undefined){
                this.dlg.close();
            }
        },

        sure: function() {
            //由具体的使用者指定处理方式
        },
        startSearch: function(){
            
        },
        silentSearch : function(data,alias){
            var queryString="";
            for(var i=0;i<data.length;i++){
                if(data[i]!=""){
                        queryString+=data[i];
                        queryString+=" AND ";
                }
            }
            if(queryString!=""){
                queryString+=' +alias:'+alias;
            }
            if(queryString!=""){
                this.startSearch();
                GridBaseModel.queryString=queryString;
                GridBaseModel.search=true;
                GridBaseModel.refresh();
                return true;
            }
            return false;
        },
        search: function(data,alias){
            if(this.silentSearch(data,alias)){
                AdvancedSearchBaseModel.close();
            }else{
                parent.Ext.MessageBox.alert('提示', "请输入查询条件！");
            } 
        }
    };
} ();

//添加模型信息
CreateBaseModel = function() {
    return {
        getLabelWidth: function(){
            return 80;
        },
        getForm: function(items) {
             var labelWidth=this.getLabelWidth();
             var frm = new parent.Ext.form.FormPanel({
                labelAlign: 'left',
                buttonAlign: 'center',
                bodyStyle: 'padding:5px',
                frame: true,//圆角和浅蓝色背景
                labelWidth: labelWidth,
                autoScroll:true,
                
                defaults: {
                    anchor: '95%'
                },
                
                items: items,

                buttons: this.getButtons(),
                keys:this.getKeys()
            });
            return frm;
        },
        getButtons: function(){
            var buttons=[
                {
                    text: '保存',
                    iconCls:'save',
                    scope: this,
                    handler: function() {
                        this.submit();
                    }
                },
                {
                    text: '重置',
                    iconCls:'reset',
                    scope: this,
                    handler: function() {
                        this.frm.form.reset();
                    }
                },
                {
                    text: '取消',
                    iconCls:'cancel',
                    scope: this,
                    handler: function() {
                        this.close();
                    }
                }
            ];
            return buttons;
        },
        getKeys: function(){
            var keys=[
                {
                     key : Ext.EventObject.ENTER,
                     fn : function() {
                        this.submit();
                     },
                     scope : this
                 }
            ];
            return keys;
        },

        getDialog: function(title,iconCls,width,height,items) {
            this.frm = this.getForm(items);
            var dlg = new parent.Ext.Window({
                title: title,
                iconCls:iconCls,
                width:width,
                height:height,
                maximizable:true,
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

        show: function(title,iconCls,width,height,items) {
            this.dlg = this.getDialog(title,iconCls,width,height,items);
            this.dlg.show();
            this.reset();
            this.dlg.on('close',function(){
                    //刷新表格
                    GridBaseModel.refresh();
                });
        },

        reset: function(){
            this.frm.form.reset();
        },
        
        close: function(){
            this.dlg.close();
        },
        
        formIsValid: function(){
            if (this.frm.getForm().isValid()) {
                return true;
            }
            return false;
        },
        
        shouldSubmit: function(){
            return true;
        },
        
        prepareSubmit: function(){
            
        },
        
        submit: function() {
            if (this.formIsValid()) {
                if(this.shouldSubmit()){
                    this.prepareSubmit();
                    this.submitCreate(this.frm.form);
                }
            }
        },    
        
        //提交添加数据
        submitCreate: function(form){
            if(undefined==GridBaseModel.createURLParameter){
                GridBaseModel.createURLParameter="";
            }
            form.submit({
                    waitTitle: '请稍等',
                    waitMsg: '正在'+CreateBaseModel.dlg.title+'……',
                    url : GridBaseModel.createURL+GridBaseModel.createURLParameter+GridBaseModel.extraCreateParameters(),

                    success : function(form, action) {
                        GridBaseModel.search=false;
                        CreateBaseModel.createSuccess(form, action);
                    },
                    failure : function(form, action) {
                        CreateBaseModel.reset();
                        if (action.failureType === Ext.form.Action.SERVER_INVALID){
                            parent.Ext.ux.Toast.msg('操作提示：',action.result.message);  
                        }
                    }
            });
        },
        createSuccess: function(form, action){
            //回调，留给使用者实现
            parent.Ext.ux.Toast.msg('操作提示：',action.result.message); 
            parent.Ext.MessageBox.confirm(CreateBaseModel.dlg.title+"成功","是否接着"+CreateBaseModel.dlg.title+"？",function(button){
                if(button == "yes"){
                    form.reset();
                }else{
                    CreateBaseModel.close();
                }
            },this); 
        }
    };
} ();

//修改模型信息
ModifyBaseModel = function() {
    return {
        getLabelWidth: function(){
            return 80;
        },
        getForm: function(items) {
            var labelWidth=this.getLabelWidth();
            var frm = new parent.Ext.form.FormPanel({
                labelAlign: 'left',
                buttonAlign: 'center',
                bodyStyle: 'padding:5px',
                frame: true,//圆角和浅蓝色背景
                labelWidth: labelWidth,
                autoScroll:true,
                
                defaults: {
                    anchor: '95%'
                },

                items: items,

                buttons: [{
                    text: '保存',
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

        getDialog: function(title,iconCls,width,height,items) {
            this.frm = this.getForm(items);
            var dlg = new parent.Ext.Window({
                title: title,
                maximizable:true,
                iconCls:iconCls,
                width:width,
                height:height,
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

        show: function(title,iconCls,width,height,items,model) {
            this.model=model;
            this.dlg = this.getDialog(title,iconCls,width,height,items);
            this.dlg.show();
        },

        reset: function(){
            this.frm.form.reset();
        },

        close: function(){
            this.dlg.close();
        },
        
        formIsValid: function(){
            if (this.frm.getForm().isValid()) {
                return true;
            }
            return false;
        },
        
        shouldSubmit: function(){
            return true;
        },
        
        prepareSubmit: function(){
            
        },

        submit: function() {
            if (this.formIsValid()) {
                if(this.shouldSubmit()){
                    this.prepareSubmit();
                    this.submitModify(this.frm.form);
                }                
            }
        },
        success:function(form, action){
            
        },
        //提交修改数据
        submitModify: function(form){
            form.submit({
                    waitTitle: '请稍等',
                    waitMsg: '正在修改……',
                    url : GridBaseModel.updatePartURL+this.model.id+'&model.version='+this.model.version+GridBaseModel.extraModifyParameters(),

                    success : function(form, action) {              
                        parent.Ext.ux.Toast.msg('操作提示：','修改成功');  
                        ModifyBaseModel.close();
                        ModifyBaseModel.modifySuccess(form, action); 
                    },
                    failure : function(form, action) {
                        if (action.failureType === Ext.form.Action.SERVER_INVALID){
                            parent.Ext.ux.Toast.msg('操作提示：',action.result.message);  
                            form.reset();
                        }
                        ModifyBaseModel.close();
                    }
            });
        },
        modifySuccess: function(form, action){
            //回调，留给使用者实现
            GridBaseModel.refresh();         
        }
    };
} ();

//显示模型详细信息
DisplayBaseModel = function() {
    return {
        getLabelWidth: function(){
            return 80;
        },
        getForm: function(items) {
             var labelWidth=this.getLabelWidth();
             var frm = new parent.Ext.form.FormPanel({                    
                labelAlign: 'left',
                buttonAlign: 'center',
                bodyStyle: 'padding:5px',
                frame: true,//圆角和浅蓝色背景
                labelWidth: labelWidth,
                autoScroll:true,
                
                defaults: {
                    readOnly:true,
                    fieldClass:'detail_field',
                    anchor: '95%'
                },

                items: items,


                buttons: [{
                    text: '关闭',
                    iconCls:'cancel',
                    scope: this,
                    handler: function() {
                        this.close();
                    }
                }],
                 keys:[{
                     key : Ext.EventObject.ENTER,
                     fn : function() {
                        this.close();
                     },
                     scope : this
                 }]
            });
            return frm;
        },

        getDialog: function(title,iconCls,width,height,items) {
            this.frm = this.getForm(items);
            var dlg = new parent.Ext.Window({
                title: title,
                maximizable:true,
                iconCls:iconCls,
                width:width,
                height:height,
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
        
        show: function(title,iconCls,width,height,items) {
            this.dlg = this.getDialog(title,iconCls,width,height,items);
            this.dlg.show();
        },

        close: function(){
            this.dlg.close();
        }
    };
} ();

//树模型
TreeBaseModel = function(){
    return{        
        getTreeWithContextMenu: function(dataUrl, rootText, rootId, icon, create, remove, modify){            
            var tree = this.getTree(dataUrl, rootText, rootId,icon);                
            var contextMenu=this.getContextMenu(create, remove, modify);
            document.oncontextmenu = function() {return false;} //屏蔽右键 ，IE6下会有问题
            tree.on('contextmenu', function(node, event) {
                event.preventDefault(); //关闭默认的菜单，以避免弹出两个菜单
                TreeBaseModel.onClick(node, event);
                contextMenu.showAt(event.getXY()); //取得鼠标点击坐标，展示菜单
            });
            return tree;
        },
        //右键菜单
        getContextMenu: function(create,remove,modify){
            //右键菜单
            var contextmenu=new Ext.menu.Menu({
                id:'theContextMenu',
                items:[]
            });
            this.commandPrivilegeControl(contextmenu,create,remove,modify);
            return contextmenu;
        },
        //控制右键菜单中的命令是否是用户有权限拥有的
        commandPrivilegeControl: function(obj,create,remove,modify){
                if(create && parent.isGranted(namespace,action,"create")){     
                        obj.add(new Ext.menu.Item({  
                                iconCls : 'create',  
                                text : '添加',  
                                handler : this.create
                            }));  
                }  
                if(remove && parent.isGranted(namespace,action,"delete")){
                        obj.add(new Ext.menu.Item({  
                                iconCls : 'delete',  
                                text : '删除',  
                                handler : this.remove
                            }));  
                }  
                if(modify && parent.isGranted(namespace,action,"updatePart")){     
                        obj.add(new Ext.menu.Item({  
                                iconCls : 'updatePart',  
                                text : '修改',  
                                handler : this.modify
                            }));  
                }
        },
        //添加
        create: function(){
            CreateModel.show();
        },
        //删除
        remove: function (){
            //留给使用者实现
        },
        //修改
        modify: function(){
            //留给使用者实现
        },
        getTree: function(dataUrl,rootText,rootId,icon){
                var Tree = Ext.tree;
                this.tree = new Tree.TreePanel({
                    frame : true,// 美化界面
                    split : true, //分隔条
                    animate:true,
                    autoScroll:true,
                    collapsible : true,
                    region:'west',
                    width:200,
                    loader: new Tree.TreeLoader({
                        dataUrl:dataUrl
                    }),
                    containerScroll: false,
                    border: false,
                    rootVisible: rootId!="root"
                });

                // set the root node
                this.root = new Tree.AsyncTreeNode({
                    text: rootText,
                    iconCls : icon,
                    draggable:false, // disable root node dragging
                    id:rootId
                });
                this.tree.setRootNode(this.root);
                this.tree.on('click',function(node, event){
                    TreeBaseModel.onClick(node, event);                    
                });
                //根节点装载完毕后，自动选择刚装载的节点
                this.root.reload(
                        function(){
                            this.root.expand(false, true);
                            this.onClick(this.root.childNodes[0]);
                        },
                    this);
                return this.tree;
        },
        //选择节点
        onClick: function(node, event){
            
        }
    }
}();

/**下拉树
 * 用法: new TreeSelector(_id,_nodeText,_url,_rootNodeID,_rooNodeText,_label,_field,_anchor,_justLeaf); 返回类型为Ext.form.ComboBox
 * @param {} _id:下拉树的ID，用于提交nodeText的表单字段
 * @param {} _nodeText:下拉树初始显示内容
 * @param {} _url:读取下拉树JSON数据的URL,JSON数据的格式是Ext.tree.TreeLoader能接收的数据格式
 * @param {} _rootNodeID:树根节点ID，当_rootNodeID!="root"时显示根节点
 * @param {} _rooNodeText:树根节点文本
 * @param {} _label:是该下拉树的fieldLabel
 * @param {} _field:修改域的ID,根据此ID把其树结点的id的值赋给该域
 * @param {} _anchor:anchor
 * @param {} _justLeaf:为true表示下拉树只能选择叶子节点，其他值表示可选所有节点
 */
var TreeSelector = function(_id,_nodeText,_url,_rootNodeID,_rooNodeText,_label,_field,_anchor,_justLeaf) {

	var config={
		id:_id,
		store : new parent.Ext.data.SimpleStore({
					fields : [],
					data : [[]]
				}),
		editable : false,
		mode : 'local',
		fieldLabel:_label,
		emptyText : "请选择",
		triggerAction : 'all',
                anchor: _anchor,
		maxHeight : 280,
                tpl : "<tpl for='.'><div style='height:280px'><div id='tree_"+_field+"'></div></div></tpl>",
		selectedClass : '',
		onSelect : parent.Ext.emptyFn,
                //以下方法用于修正点击树的加号后，下拉框被隐藏
                onViewClick : function(doFocus) {     
                    var index = this.view.getSelectedIndexes()[0], s = this.store, r = s.getAt(index);     
                    if (r) {     
                      this.onSelect(r, index);     
                    } else if (s.getCount() === 0) {     
                      this.collapse();     
                    }     
                    if (doFocus !== false) {     
                      this.el.focus();     
                    }     
                }     
	};
	var comboxWithTree = new parent.Ext.form.ComboBox(config);

	var tree = new parent.Ext.tree.TreePanel({
		id:'selectTree',
		height:280,
		autoScroll: true,
		split: true,
		loader: new parent.Ext.tree.TreeLoader({url:_url}),
                root:
                    new parent.Ext.tree.AsyncTreeNode({
                    id:_rootNodeID,
                    text:_rooNodeText,
                    expanded: true
                }),
                rootVisible: _rootNodeID!="root"
	});
        
	tree.on('click', function(node,e) {
                var editField = parent.Ext.getCmp(_field);//根据要修改的域的ID取得该域
                if(node.id!=null && node.id!=''){
                    if(_justLeaf && !node.isLeaf()){
                        //如果指定只能选叶子节点，当在点击了非叶子节点时没有反应
                    }else{
                        comboxWithTree.setValue(node.text);
                        comboxWithTree.id=node.id;
                        comboxWithTree.collapse();
                        if(editField){
                            editField.setValue(node.id); //把树结点的值赋给要修改的域
                        }
                    }
                }
                
	});
	comboxWithTree.on('expand', function() {
                tree.render('tree_'+_field);
                tree.getRootNode().expand(false,true);
	});
        comboxWithTree.setValue(_nodeText);
	return comboxWithTree
};


var IframeTreeSelector = function(_id,_nodeText,_url,_rootNodeID,_rooNodeText,_label,_field,_anchor,_justLeaf) {

    var config={
            id:_id,
            store : new Ext.data.SimpleStore({
                                    fields : [],
                                    data : [[]]
                            }),
            editable : false,
            mode : 'local',
            fieldLabel:_label,
            emptyText : "请选择",
            triggerAction : 'all',
            anchor: _anchor,
            maxHeight : 280,
            tpl : "<tpl for='.'><div style='height:280px'><div id='tree_"+_field+"'></div></div></tpl>",
            selectedClass : '',
            onSelect : Ext.emptyFn,
            //以下方法用于修正点击树的加号后，下拉框被隐藏
            onViewClick : function(doFocus) {     
                var index = this.view.getSelectedIndexes()[0], s = this.store, r = s.getAt(index);     
                if (r) {     
                    this.onSelect(r, index);     
                } else if (s.getCount() === 0) {     
                    this.collapse();     
                }     
                if (doFocus !== false) {     
                    this.el.focus();     
                }     
            }     
    };
    var comboxWithTree = new Ext.form.ComboBox(config);

    var tree = new Ext.tree.TreePanel({
            id:'selectTree',
            height:280,
            autoScroll: true,
            split: true,
            loader: new Ext.tree.TreeLoader({url:_url}),
            root:
                new Ext.tree.AsyncTreeNode({
                id:_rootNodeID,
                text:_rooNodeText,
                expanded: true
            }),
            rootVisible: _rootNodeID!="root"
    });

    tree.on('click', function(node,e) {
            var editField = Ext.getCmp(_field);//根据要修改的域的ID取得该域
            if(node.id!=null && node.id!=''){
                if(_justLeaf && !node.isLeaf()){
                    //如果指定只能选叶子节点，当在点击了非叶子节点时没有反应
                }else{
                    comboxWithTree.setValue(node.text);
                    comboxWithTree.id=node.id;
                    comboxWithTree.collapse();
                    if(editField){
                        editField.setValue(node.id); //把树结点的值赋给要修改的域
                    }
                }
            }

    });
    comboxWithTree.on('expand', function() {
            tree.render('tree_'+_field);
            tree.getRootNode().expand(false,true);
    });
    comboxWithTree.setValue(_nodeText);
    return comboxWithTree
};
//通用模型，主要用来提交表单数据
CommonModel = function() {
    return {
        getLabelWidth: function(){
            return 80;
        },
        getForm: function(items,buttons,keys) {
             var labelWidth=this.getLabelWidth();
             var frm = new parent.Ext.form.FormPanel({
                labelAlign: 'left',
                buttonAlign: 'center',
                bodyStyle: 'padding:5px',
                frame: true,//圆角和浅蓝色背景
                labelWidth: labelWidth,
                autoScroll:true,
                
                defaults: {
                    anchor: '95%'
                },
                
                items: items,

                buttons: buttons,
                keys:keys
            });
            return frm;
        },

        getDialog: function(title,iconCls,width,height,items,buttons,keys) {
            this.frm = this.getForm(items,buttons,keys);
            var dlg = new parent.Ext.Window({
                title: title,
                iconCls:iconCls,
                width:width,
                height:height,
                maximizable:true,
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

        show: function(title,iconCls,width,height,items,buttons,keys) {
            this.dlg = this.getDialog(title,iconCls,width,height,items,buttons,keys);
            this.dlg.show();
            this.reset();
        },

        reset: function(){
            this.frm.form.reset();
        },
        
        close: function(){
            this.dlg.close();
        },
        
        formIsValid: function(){
            if (this.frm.getForm().isValid()) {
                return true;
            }
            return false;
        },
        
        shouldSubmit: function(){
            return true;
        },
        
        prepareSubmit: function(){
            
        },
        
        submit: function() {
            if (this.formIsValid()) {
                if(this.shouldSubmit()){
                    this.prepareSubmit();
                    this.submitCreate(this.frm.form);
                }
            }
        },    
        
        //提交数据
        submitCreate: function(form){
            form.submit({
                    waitTitle: '请稍等',
                    waitMsg: '正在'+CommonModel.dlg.title+'……',
                    url : this.submitUrl,

                    success : function(form, action) {
                        GridBaseModel.search=false;
                        CommonModel.createSuccess(form, action);
                        
                        parent.Ext.ux.Toast.msg('操作提示：',action.result.message);  
                        parent.Ext.MessageBox.confirm(CommonModel.dlg.title+"成功","是否接着"+CommonModel.dlg.title+"？",function(button){
                            if(button == "yes"){
                                form.reset();
                            }else{
                                CommonModel.close();
                            }
                        },this);
                    },
                    failure : function(form, action) {
                        CommonModel.reset();
                        if (action.failureType === Ext.form.Action.SERVER_INVALID){
                            parent.Ext.ux.Toast.msg('操作提示：',action.result.message);  
                        }
                    }
            });
        },
        createSuccess: function(form, action){
            //回调，留给使用者实现
            GridBaseModel.refresh();
        }
    };
} ();