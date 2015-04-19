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
    var action='backup-log';
    
    var category="rate";
    var chartDataURL=contextPath+'/'+namespace+'/'+action+'/chart.action?category=';
   
    BackupLogChart=function(){
        return{
            show : function(url,category,queryString,type){
                var dataTypeLabel='数据类型'
                var dataTypeItems=[
                                    {boxLabel: '成功率', name : "dataType", inputValue: 'rate',checked:category=='rate'},
                                    {boxLabel: '耗时序列', name : "dataType", inputValue: 'sequence',checked:category=='sequence'}
                                ];
                var dataTypeColumns=10;
                var dataTypeHeight=20;

                SingleSeriesChartBaseModel.show("备份恢复统计","backupLog",100,url, category, queryString, type, dataTypeLabel, dataTypeItems, dataTypeColumns, dataTypeHeight);
            }
        }
    }(); 
    //统计图
    ChartModel= function(){
        return{
            show: function(){
                BackupLogChart.show(chartDataURL,category,GridBaseModel.queryString,"Column3D"); 
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
                        id:'search_startOperatingTime',                        
                        editable:false,
                        fieldLabel: '操作开始日期',
                        vtype:"daterange",
                        endDateField:"search_endOperatingTime"
                    },{
                        xtype:'datefield',
                        format:"Y-m-d",
                        id:'search_endOperatingTime',
                        editable:false,
                        fieldLabel: '操作结束日期',
                        vtype:"daterange",
                        startDateField:"search_startOperatingTime"
                    },{
                        xtype: 'combo',
                        id:'search_operatingType',
                        store : new Ext.data.SimpleStore({
                           fields : ['value', 'text'],
                           data:[['备份','备份'],['恢复','恢复']]
                        }),
                        emptyText:'请选择',
                        mode:'local',
                        valueField:'value',
                        displayField:'text',
                        triggerAction:'all',
                        forceSelection: true,
                        editable:       false,
                        fieldLabel: '操作类型'
                    },{
                        xtype: 'combo',
                        id:'search_operatingResult',
                        store : new Ext.data.SimpleStore({
                           fields : ['value', 'text'],
                           data:[['成功','成功'],['失败','失败']]
                        }),
                        emptyText:'请选择',
                        mode:'local',
                        valueField:'value',
                        displayField:'text',
                        triggerAction:'all',
                        forceSelection: true,
                        editable:       false,
                        fieldLabel: '操作结果'
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

                    var search_startOperatingTime=parent.Ext.getCmp('search_startOperatingTime').value;
                    var search_endOperatingTime=parent.Ext.getCmp('search_endOperatingTime').value;
                    var search_operatingTime="";
                    if(search_startOperatingTime!=undefined  && search_startOperatingTime!=""  && search_endOperatingTime!=undefined   && search_endOperatingTime!=""){
                        search_operatingTime=' +operatingTime:['+search_startOperatingTime+' TO '+search_endOperatingTime+']';
                        data.push(search_operatingTime);
                    }

                    var search_operatingType=parent.Ext.getCmp('search_operatingType').getValue();
                    if(search_operatingType!=""){
                        search_operatingType=' +operatingType:'+search_operatingType;
                        data.push(search_operatingType);
                    }

                    var search_operatingResult=parent.Ext.getCmp('search_operatingResult').getValue();
                    if(search_operatingResult!=""){
                        search_operatingResult=' +operatingResult:'+search_operatingResult;
                        data.push(search_operatingResult);
                    }

                    AdvancedSearchBaseModel.search(data, "BackupLog");
            },
            
            show: function() {
                AdvancedSearchBaseModel.show('高级搜索', 'backupLog', 420, 270, this.getItems(), this.callback);
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
				{name: 'startTime'},
				{name: 'endTime'},
				{name: 'processTime'},
				{name: 'operatingType'},
				{name: 'operatingResult'}
			];
               return fields;     
            },
            getColumns: function(){
                var columns=[
                                {header: "编号", width: 10, dataIndex: 'id', sortable: true},
 				{header: "用户名称", width: 10, dataIndex: 'username', sortable: true},
				{header: "登录IP地址", width: 20, dataIndex: 'loginIP', sortable: true},
				{header: "服务器IP地址", width: 20, dataIndex: 'serverIP', sortable: true},
				{header: "开始处理时间", width: 20, dataIndex: 'startTime', sortable: true},
				{header: "处理完成时间", width: 20, dataIndex: 'endTime', sortable: true},
				{header: "操作类型", width: 10, dataIndex: 'operatingType', sortable: true},
				{header: "操作结果", width: 10, dataIndex: 'operatingResult', sortable: true},
 				{header: "操作耗时", width: 20, dataIndex: 'processTime', sortable: true}
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