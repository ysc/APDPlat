
//统计报表
ChartBaseModel=function(){
    return {

            getChartPanel : function(chartData,type){
                var chartPanel = new parent.Ext.ux.Chart.Fusion.Panel({ 
                      autoWidth:true,
                      chartURL : contextPath+"/FusionCharts/"+type+".swf", 
                      chartData : chartData, 
                      mediaCfg : { 
                         params : { 
                             scale : "exactfit" 
                         } 
                      }
                  }); 
                  return chartPanel;
            },
            //同步方法，返回请求得到的数据
            getChartData : function(url,category,queryString){ 
                var tip=parent.Ext.Msg.wait("正在查询数据......", '请稍候');
                var xml="";
                parent.Ext.Ajax.request({
                    url : url+category,
                    method : 'POST',
                    //同步方法
                    async: false,
                    params : {
                        queryString : queryString
                    },
                    success:function(response, opts){
                        xml=response.responseText;
                    }
                });
                tip.hide();
                return xml;
            },
            getChartWindow : function(windowTitle,windowIcon){
                var window = new parent.Ext.Window({ 
                        title : windowTitle, 
                        maximized:true,
                        buttonAlign: 'center',
                        iconCls:windowIcon,
                        plain: true,
                        closable: true,
                        frame: true,
                        layout: 'form',
                        border: false,
                        modal: true,
                        buttons: [{
                            text: '关闭',
                            iconCls:'cancel',
                            scope: this,
                            handler: function() {
                                window.close();
                            }
                        }],
                        keys:[{
                             key : Ext.EventObject.ENTER,
                             fn : function(){
                                 window.close();
                             },
                             scope : this
                        }]
                  }); 
                  return window;
            },
            show : function(windowTitle,windowIcon,topHeight,url,category,queryString,type,dataTypeLabel,dataTypeItems,dataTypeColumns,dataTypeHeight,chartTypeLabel,chartTypeItems,chartTypeColumns,chartTypeHeight){                 
                  var window = this.getChartWindow(windowTitle,windowIcon);
                  //显示窗口
                  window.show();
                  //获取数据
                  var chartData=this.getChartData(url,category,queryString);
                  //获取图表
                  var chartPanel = this.getChartPanel(chartData,type); 
                  var dataType;
                  if(dataTypeLabel && dataTypeItems && dataTypeColumns && dataTypeHeight){
                      dataType={
                            xtype: 'radiogroup',
                            fieldLabel: dataTypeLabel,   
                            autoWidth:true,
                            columns : dataTypeColumns,             
                            height:dataTypeHeight,
                            items: dataTypeItems,
                            listeners :{
                                'change':function(radioGroup,oldValue){
                                        //category已经改变
                                       category=radioGroup.getValue().inputValue;
                                       //重新获取数据
                                       chartData=ChartBaseModel.getChartData(url,category,queryString);
                                       //删除原来的图表
                                       window.remove(chartPanel);
                                       //重新获取图表
                                       chartPanel = ChartBaseModel.getChartPanel(chartData,type); 
                                       //加入图表
                                       window.add(chartPanel);
                                       //设置图表的高度
                                       var height=window.getHeight()-topHeight;
                                       chartPanel.setHeight(height); 
                                       //显示图表
                                       window.doLayout();
                                }
                            }
                      };    
                  }
                      
                  var chartType={
                        xtype: 'radiogroup',
                        fieldLabel: chartTypeLabel,   
                        autoWidth:true,
                        columns : chartTypeColumns,             
                        height:chartTypeHeight,
                        items: chartTypeItems,
                        listeners :{
                            'change':function(radioGroup,oldValue){
                                   //图表类型已经改变
                                   type=radioGroup.getValue().inputValue;
                                   //删除原来的图表
                                   window.remove(chartPanel);
                                   //重新获取图表
                                   chartPanel = ChartBaseModel.getChartPanel(chartData,type); 
                                   //加入图表
                                   window.add(chartPanel);
                                   //设置图表的高度
                                   var height=window.getHeight()-topHeight;
                                   chartPanel.setHeight(height); 
                                   //显示图表
                                   window.doLayout();
                            }
                        }
                  };    
                  
                  //设置图表的高度
                  var height=window.getHeight()-topHeight;
                  chartPanel.setHeight(height);
                  
                  if(dataType){
                    window.add(dataType);
                  }
                  window.add(chartType);
                  window.add(chartPanel);
                  
                  window.on("resize",function(){
                      var height=window.getHeight()-topHeight;
                      chartPanel.setHeight(height);
                  })
            }
    };
}();
//单数据集统计报表
SingleSeriesChartBaseModel=function(){
    return{
        show : function(windowTitle,windowIcon,topHeight,url,category,queryString,type, dataTypeLabel, dataTypeItems, dataTypeColumns, dataTypeHeight){
            var chartTypeLabel='图表类型'
            var chartTypeItems=[
                                {boxLabel: '',id:'Column2D', name : "chartType", inputValue: 'Column2D',checked:type=='Column2D'},
                                {boxLabel: '',id:'Column3D', name : "chartType", inputValue: 'Column3D',checked:type=='Column3D'},
                                {boxLabel: '',id:'Line', name : "chartType", inputValue: 'Line',checked:type=='Line'},
                                {boxLabel: '',id:'Area2D', name : "chartType", inputValue: 'Area2D',checked:type=='Area2D'},
                                {boxLabel: '',id:'Bar2D', name : "chartType", inputValue: 'Bar2D',checked:type=='Bar2D'},
                                {boxLabel: '',id:'Pie2D', name : "chartType", inputValue: 'Pie2D',checked:type=='Pie2D'},
                                {boxLabel: '',id:'Pie3D', name : "chartType", inputValue: 'Pie3D',checked:type=='Pie3D'},
                                {boxLabel: '',id:'Pareto2D', name : "chartType", inputValue: 'Pareto2D',checked:type=='Pareto2D'},
                                {boxLabel: '',id:'Pareto3D', name : "chartType", inputValue: 'Pareto3D',checked:type=='Pareto3D'},
                                {boxLabel: '',id:'Doughnut2D', name : "chartType", inputValue: 'Doughnut2D',checked:type=='Doughnut2D'},
                                {boxLabel: '',id:'Doughnut3D', name : "chartType", inputValue: 'Doughnut3D',checked:type=='Doughnut3D'}
                            ];
            var chartTypeColumns=15;
            var chartTypeHeight=20;
            ChartBaseModel.show(windowTitle,windowIcon,topHeight,url, category, queryString, type, dataTypeLabel, dataTypeItems, dataTypeColumns, dataTypeHeight, chartTypeLabel, chartTypeItems, chartTypeColumns, chartTypeHeight);
        }
    }
}();
//多数据集统计报表
MultiSeriesChartBaseModel=function(){
    return{
        show : function(windowTitle,windowIcon,topHeight,url,category,queryString,type, dataTypeLabel, dataTypeItems, dataTypeColumns, dataTypeHeight){
            var chartTypeLabel='图表类型'
            var chartTypeItems=[
                                {boxLabel: '',id:"MSColumn2D",name : "chartType", inputValue: 'MSColumn2D',checked:type=='MSColumn2D'},
                                {boxLabel: '',id:"MSColumn3D", name : "chartType", inputValue: 'MSColumn3D',checked:type=='MSColumn3D'},
                                {boxLabel: '',id:"MSBar2D", name : "chartType", inputValue: 'MSBar2D',checked:type=='MSBar2D'},
                                {boxLabel: '',id:"MSBar3D", name : "chartType", inputValue: 'MSBar3D',checked:type=='MSBar3D'},
                                {boxLabel: '',id:"MSArea", name : "chartType", inputValue: 'MSArea',checked:type=='MSArea'},
                                {boxLabel: '',id:"StackedArea2D", name : "chartType", inputValue: 'StackedArea2D',checked:type=='StackedArea2D'},
                                {boxLabel: '',id:"StackedColumn3D", name : "chartType", inputValue: 'StackedColumn3D',checked:type=='StackedColumn3D'},
                                {boxLabel: '',id:"StackedColumn2D", name : "chartType", inputValue: 'StackedColumn2D',checked:type=='StackedColumn2D'},
                                {boxLabel: '',id:"StackedBar2D", name : "chartType", inputValue: 'StackedBar2D',checked:type=='StackedBar2D'},
                                {boxLabel: '',id:"StackedBar3D", name : "chartType", inputValue: 'StackedBar3D',checked:type=='StackedBar3D'},
                                {boxLabel: '',id:"Marimekko", name : "chartType", inputValue: 'Marimekko',checked:type=='Marimekko'},
                                {boxLabel: '',id:"ScrollStackedColumn2D", name : "chartType", inputValue: 'ScrollStackedColumn2D',checked:type=='ScrollStackedColumn2D'},
                                {boxLabel: '',id:"ScrollColumn2D", name : "chartType", inputValue: 'ScrollColumn2D',checked:type=='ScrollColumn2D'},
                                {boxLabel: '',id:"ScrollArea2D", name : "chartType", inputValue: 'ScrollArea2D',checked:type=='ScrollArea2D'},
                                {boxLabel: '',id:"ScrollCombi2D", name : "chartType", inputValue: 'ScrollCombi2D',checked:type=='ScrollCombi2D'},
                                {boxLabel: '',id:"ScrollLine2D", name : "chartType", inputValue: 'ScrollLine2D',checked:type=='ScrollLine2D'},
                                {boxLabel: '',id:"MSLine", name : "chartType", inputValue: 'MSLine',checked:type=='MSLine'},
                                {boxLabel: '',id:"ZoomLine", name : "chartType", inputValue: 'ZoomLine',checked:type=='ZoomLine'}
                            ];
            var chartTypeColumns=20;
            var chartTypeHeight=20;
            ChartBaseModel.show(windowTitle,windowIcon,topHeight,url, category, queryString, type, dataTypeLabel, dataTypeItems, dataTypeColumns, dataTypeHeight, chartTypeLabel, chartTypeItems, chartTypeColumns, chartTypeHeight);
        }
    }
}();