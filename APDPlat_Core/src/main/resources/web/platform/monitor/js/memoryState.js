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
    var action='memory-state';
    
    var category="sequenceDD";
    var chartDataURL=contextPath+'/'+namespace+'/'+action+'/chart.action?category=';
    
    MemoryStateChart=function(){
        return{
            show : function(url,category,queryString,type){
                var dataTypeLabel='数据类型'
                var dataTypeItems=[
                                    {boxLabel: '耗时序列', name : "dataType", inputValue: 'sequence',checked:category=='sequence'},
                                    {boxLabel: '耗时序列(小时)', name : "dataType", inputValue: 'sequenceHH',checked:category=='sequenceHH'},
                                    {boxLabel: '耗时序列(天)', name : "dataType", inputValue: 'sequenceDD',checked:category=='sequenceDD'},
                                    {boxLabel: '耗时序列(月)', name : "dataType", inputValue: 'sequenceMonth',checked:category=='sequenceMonth'}
                              ];
                var dataTypeColumns=10;
                var dataTypeHeight=20;

                MultiSeriesChartBaseModel.show("系统内存使用统计","memoryState",100,url, category, queryString, type, dataTypeLabel, dataTypeItems, dataTypeColumns, dataTypeHeight);
            }
        }
    }();
    //统计图
    ChartModel= function(){
        return{
            show: function(){
                MemoryStateChart.show(chartDataURL,category,GridBaseModel.queryString,"MSColumn3D"); 
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
                        id:'search_startRecordTime',
                        editable:false,
                        fieldLabel: '记录开始日期',
                        vtype:"daterange",
                        endDateField:"search_endRecordTime"
                    },{
                        xtype:'datefield',
                        format:"Y-m-d",
                        id:'search_endRecordTime',
                        editable:false,
                        fieldLabel: '记录结束日期',
                        vtype:"daterange",
                        startDateField:"search_startRecordTime"
                    }];
                return items;
            },
            //点击搜索之后的回调方法
            callback : function(){        
                    var data=[];

                    var search_startRecordTime=parent.Ext.getCmp('search_startRecordTime').value;
                    var search_endRecordTime=parent.Ext.getCmp('search_endRecordTime').value;
                    var search_recordTime="";
                    if(search_startRecordTime!=undefined  && search_startRecordTime!=""  && search_endRecordTime!=undefined   && search_endRecordTime!="" ){
                        search_recordTime=' +recordTime:['+search_startRecordTime+' TO '+search_endRecordTime+']';
                        data.push(search_recordTime);
                    }

                    AdvancedSearchBaseModel.search(data, "MemoryState");
            },
            
            show: function() {
                AdvancedSearchBaseModel.show('高级搜索',"memoryState", 420, 180, this.getItems(), this.callback);
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
				{name: 'recordTime'},
				{name: 'maxMemory'},
				{name: 'totalMemory'},
				{name: 'freeMemory'},
				{name: 'usableMemory'},
				{name: 'usingMemory'}
			];
               return fields;     
            },
            getColumns: function(){
                var columns=[
 				{header: "编号", width: 10, dataIndex: 'id', sortable: true},
				{header: "服务器IP地址", width: 20, dataIndex: 'serverIP', sortable: true},
				{header: "最大可用内存（MB)", width: 20, dataIndex: 'maxMemory', sortable: true},
				{header: "已分配内存（MB)", width: 20, dataIndex: 'totalMemory', sortable: true},
				{header: "已释放内存（MB)", width: 20, dataIndex: 'freeMemory', sortable: true},
				{header: "可用内存（MB)", width: 20, dataIndex: 'usableMemory', sortable: true},
				{header: "已用内存（MB)", width: 20, dataIndex: 'usingMemory', sortable: true},
				{header: "记录时间", width: 20, dataIndex: 'recordTime', sortable: true}
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