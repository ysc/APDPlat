
var SearchForm=new Ext.Panel({
	id:"searchForm",
	layout:"hbox",
	border:false,
	bodyStyle:"background-color: transparent;",
	style:"margin-top:-15px",
	layoutConfig:{
		align:"top",
		pack:"center"
	},
	defaults:{
		margins:{
			top:0,
			left:2,
			bottom:0,
			right:0
		}
	},
	items:[{
		id:"searchContent",
		xtype:"textfield",
                width:100
            },{
		id:"searchType",
		width:80,
		xtype:"combo",
		mode:"local",
		editable:false,
		triggerAction:"all",
		store:[
                    ["title","标题"],["content","内容"]
                ],
                value:"title"
            },{
                xtype:"button",
                text:"搜索",
                iconCls:"search-bg",
                handler:function(){
                    var searchContent=Ext.getCmp("searchContent").getValue();
                    var searchType=Ext.getCmp("searchType").value;
                    var searchText=Ext.getCmp("searchType").lastSelectionText;
                    if(searchContent!=""){
                        closeWindow(searchText);
                        openWindow(searchText,searchType,"../platform/search.jsp?q="+searchContent+"&t="+searchType);
                    }
                }
            }]
    });