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
    var action='runing-time';
    
    var category="runingRate";
    var chartDataURL=contextPath+'/'+namespace+'/'+action+'/chart.action?category=';
    
    RuningTimeChart=function(){
        return{
            show : function(url,category,queryString,type){
                var dataTypeLabel='数据类型'
                var dataTypeItems=[
                                    {boxLabel: '运行效率', name : "dataType", inputValue: 'runingRate',checked:category=='runingRate'},
                                    {boxLabel: '运行序列', name : "dataType", inputValue: 'runingSequence',checked:category=='runingSequence'}
                              ];
                var dataTypeColumns=10;
                var dataTypeHeight=20;

                SingleSeriesChartBaseModel.show("系统运行时间统计","runingTime",100,url, category, queryString, type, dataTypeLabel, dataTypeItems, dataTypeColumns, dataTypeHeight);
            }
        }
    }();
    //统计图
    ChartModel= function(){
        return{
            show: function(){
                RuningTimeChart.show(chartDataURL,category,GridBaseModel.queryString,"Column3D"); 
            }
        }
    }();
    //高级搜索
    AdvancedSearchModel = function() {
        return {
            //搜索表单
            getItems : function(){
                var items=[{
                        xtype:'datefield',
                        format:"Y-m-d",
                        id:'search_startStartupTime',
                        editable:false,
                        fieldLabel: '启动开始日期',
                        vtype:"daterange",
                        endDateField:"search_endStartupTime"
                    },{
                        xtype:'datefield',
                        format:"Y-m-d",
                        id:'search_endStartupTime',
                        editable:false,
                        fieldLabel: '启动结束日期',
                        vtype:"daterange",
                        startDateField:"search_startStartupTime"
                    }];
                return items;
            },
            //点击搜索之后的回调方法
            callback : function(){               
                    var data=[];

                    var search_startStartupTime=parent.Ext.getCmp('search_startStartupTime').value;
                    var search_endStartupTime=parent.Ext.getCmp('search_endStartupTime').value;
                    var search_startupTime="";
                    if(search_startStartupTime!=undefined  && search_startStartupTime!=""  && search_endStartupTime!=undefined   && search_endStartupTime!="" ){
                        search_startupTime=' +startupTime:['+search_startStartupTime+' TO '+search_endStartupTime+']';
                        data.push(search_startupTime);
                    }

                    AdvancedSearchBaseModel.search(data, "RuningTime");
            },
            
            show: function() {
                AdvancedSearchBaseModel.show('高级搜索',"runingTime", 420, 180, this.getItems(), this.callback);
            }
        };
    } ();
    //表格
    GridModel = function() {
        return {
            getFields: function(){
                var fields=[
 				{name: 'id'},
				{name: 'serverIP'},
				{name: 'startupTime'},
				{name: 'shutdownTime'},
				{name: 'runingTime'}
			];
               return fields;     
            },
            getColumns: function(){
                var columns=[
 				{header: "编号", width: 10, dataIndex: 'id', sortable: true},
				{header: "服务器IP地址", width: 20, dataIndex: 'serverIP', sortable: true},
				{header: "系统启动时间", width: 20, dataIndex: 'startupTime', sortable: true},
				{header: "系统关闭时间", width: 20, dataIndex: 'shutdownTime', sortable: true},
				{header: "持续运行时间", width: 20, dataIndex: 'runingTime', sortable: true}
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