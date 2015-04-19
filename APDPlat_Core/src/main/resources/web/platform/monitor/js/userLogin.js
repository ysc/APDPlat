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

var namespace='monitor';
    var action='user-login';
    
    var category="loginTimes";
    var chartDataURL=contextPath+'/'+namespace+'/'+action+'/chart.action?category=';
    
    UserLoginChart=function(){
        return{
            show : function(url,category,queryString,type){
                var dataTypeLabel='数据类型'
                var dataTypeItems=[
                                    {boxLabel: '登录次数', name : "dataType", inputValue: 'loginTimes',checked:category=='loginTimes'},
                                    {boxLabel: '在线时间', name : "dataType", inputValue: 'onlineTime',checked:category=='onlineTime'}
                              ];
                var dataTypeColumns=10;
                var dataTypeHeight=20;

                SingleSeriesChartBaseModel.show("用户登录统计","userLogin",100,url, category, queryString, type, dataTypeLabel, dataTypeItems, dataTypeColumns, dataTypeHeight);
            }
        }
    }();
    //统计图
    ChartModel= function(){
        return{
            show: function(){
                UserLoginChart.show(chartDataURL,category,GridBaseModel.queryString,"Column3D"); 
            }
        }
    }();
    //高级搜索
    AdvancedSearchModel = function() {
        return {
            //搜索表单
            getItems : function(){
                var items=[{
                        xtype: 'combo',
                        id:'search_username',
                        store:userStore,
                        emptyText:'请选择',
                        mode:'remote',
                        valueField:'value',
                        displayField:'text',
                        triggerAction:'all',
                        forceSelection: true,
                        editable:       false,
                        fieldLabel: '用户名'
                    },{
                        xtype:'datefield',
                        format:"Y-m-d",
                        id:'search_startLoginTime',
                        editable:false,
                        fieldLabel: '登录开始日期',
                        vtype:"daterange",
                        endDateField:"search_endLoginTime"
                    },{
                        xtype:'datefield',
                        format:"Y-m-d",
                        id:'search_endLoginTime',
                        editable:false,
                        fieldLabel: '登录结束日期',
                        vtype:"daterange",
                        startDateField:"search_startLoginTime"
                    }];
                return items;
            },
            //点击搜索之后的回调方法
            callback : function(){               
                    var data=[];

                    var search_username=parent.Ext.getCmp('search_username').getValue();
                    if(search_username!=""){
                        search_username=' +username:'+search_username;
                        data.push(search_username);
                    }

                    var search_startLoginTime=parent.Ext.getCmp('search_startLoginTime').value;
                    var search_endLoginTime=parent.Ext.getCmp('search_endLoginTime').value;
                    var search_loginTime="";
                    if(search_startLoginTime!=undefined  && search_startLoginTime!=""  && search_endLoginTime!=undefined   && search_endLoginTime!="" ){
                       search_loginTime=' +loginTime:['+search_startLoginTime+' TO '+search_endLoginTime+']';
                        data.push(search_loginTime);
                    }
                                
                    AdvancedSearchBaseModel.search(data, "UserLogin");
            },
            
            show: function() {
                AdvancedSearchBaseModel.show('高级搜索',"userLogin", 420, 180, this.getItems(), this.callback);
            }
        };
    } ();
    //表格
    GridModel = function() {
        return {
            getFields: function(){
                var fields=[
 				{name: 'id'},
				{name: 'username'},
				{name: 'loginIP'},
				{name: 'serverIP'},
				{name: 'loginTime'},
				{name: 'logoutTime'},
				{name: 'onlineTime'}
			];
               return fields;     
            },
            getColumns: function(){
                var columns=[
 				{header: "编号", width: 10, dataIndex: 'id', sortable: true},
 				{header: "用户名称", width: 10, dataIndex: 'username', sortable: true},
				{header: "登录IP地址", width: 20, dataIndex: 'loginIP', sortable: true},
				{header: "服务器IP地址", width: 20, dataIndex: 'serverIP', sortable: true},
				{header: "登录时间", width: 20, dataIndex: 'loginTime', sortable: true},
				{header: "注销时间", width: 20, dataIndex: 'logoutTime', sortable: true},
				{header: "用户在线时间", width: 20, dataIndex: 'onlineTime', sortable: true}
                            ];
                return columns;           
            },
            show: function(){
                var pageSize=17;
                
                var commands=["search","query","chart"];
                var tips=['高级搜索(S)','显示全部(A)','图表(T)'];
                var callbacks=[GridBaseModel.advancedsearch,GridBaseModel.showall,GridBaseModel.chart];
            
                GridBaseModel.show(contextPath, namespace, action, pageSize, this.getFields(), this.getColumns(), commands,tips,callbacks);
                GridBaseModel.onRowDblClick=function(){};
            }
        }
    } ();

Ext.onReady(function(){
        GridModel.show();
});