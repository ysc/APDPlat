//自动生成的文件，请不要修改
<#list dics as dic>
    //${dic.chinese}
    var ${dic.english}Store=new Ext.data.Store({
        proxy : new parent.Ext.data.HttpProxy({
            <#if dic.justCode>
            url : contextPath+'/dictionary/dic/store.action?dic=${dic.english}&justCode=true'
            <#else>
            url : contextPath+'/dictionary/dic/store.action?dic=${dic.english}'
            </#if>
        }),
        reader: new Ext.data.JsonReader({},
            Ext.data.Record.create([{
                name: 'value'
            },{
                name: 'text'
            }]))
    });
</#list>