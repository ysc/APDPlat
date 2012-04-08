
    //已经存在的备份时间点
    var existBackupStore=new Ext.data.Store({
        proxy : new parent.Ext.data.HttpProxy({
            url : contextPath+'/system/backup!store.action?allPage=true'
        }),
        reader: new Ext.data.JsonReader({},
            Ext.data.Record.create([{
                name: 'value'
            },{
                name: 'text'
            },]))
    });
    //索引文件的目录列表
    var indexDirStore=new Ext.data.Store({
        proxy : new parent.Ext.data.HttpProxy({
            url : contextPath+'/index/state!store.action?allPage=true'
        }),
        reader: new Ext.data.JsonReader({},
            Ext.data.Record.create([{
                name: 'value'
            },{
                name: 'text'
            },]))
    });
    //模型信息    
    var modelStore=new Ext.data.Store({
        proxy : new parent.Ext.data.HttpProxy({
            url : contextPath+'/log/operate-log!store.action?allPage=true'
        }),
        reader: new Ext.data.JsonReader({},
            Ext.data.Record.create([{
                name: 'value'
            },{
                name: 'text'
            },]))
    });
    //所有用户    
    var userStore=new Ext.data.Store({
        proxy : new parent.Ext.data.HttpProxy({
            url : contextPath+'/security/user!store.action?allPage=true'
        }),
        reader: new Ext.data.JsonReader({},
            Ext.data.Record.create([{
                name: 'value'
            },{
                name: 'text'
            },]))
    });