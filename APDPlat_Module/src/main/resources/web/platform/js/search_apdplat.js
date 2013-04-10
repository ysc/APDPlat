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