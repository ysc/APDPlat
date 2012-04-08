    var dir="";
    
    var namespace='index';
    var action='state';

    //表格
    GridModel = function() {
        return {
            getFields: function(){
                var fields=[
				{name: 'name'},
 				{name: 'length'},
				{name: 'lastModified'}
			];
               return fields;     
            },
            getColumns: function(){
                var columns=[
 				{header: "索引文件", width: 60, dataIndex: 'name', sortable: true},
				{header: "文件大小（KB）", width: 40, dataIndex: 'length', sortable: true},
				{header: "修改时间", width: 40, dataIndex: 'lastModified', sortable: true}
                            ];
                return columns;           
            },
            getGrid: function(){
                var pageSize=17;
                
                //添加特殊参数
                GridBaseModel.setStoreBaseParams=function(store){
                    store.on('beforeload',function(store){
                       store.baseParams = {dir:GridBaseModel.dir};
                    });
                };
                
                var grid=GridBaseModel.getGrid(contextPath, namespace, action, pageSize, this.getFields(), this.getColumns());
         
                return grid;
            }
        }
    } ();
    ConfigForm = function() {
        return {
            show: function() {
                 var frm = new Ext.form.FormPanel({
                    applyTo : 'grid-div',
                    height:494,
                    autoWidth:true,
                    buttonAlign: 'left',
                    bodyStyle: 'padding:5px',
                    frame: true,//圆角和浅蓝色背景
                    items: [{
                                layout:'column',
                                defaults: {width: 250},
                                items:[{
                                    columnWidth:.2,
                                    layout: 'form',

                                     items: [{
                                                xtype: 'multiselect',
                                                hideLabel:true,
                                                width: 180,
                                                height: 475,
                                                store: indexDirStore,
                                                displayField: 'text',
                                                valueField: 'value',
                                                ddReorder: true,
                                                listeners : {
                                                    "click" : function(obj,event) {
                                                                var name="";
                                                                var len=indexDirStore.totalLength;
                                                                for(var i=0;i<len;i++){
                                                                    if(obj.getValue()==indexDirStore.getAt(i).json.value){
                                                                        name=indexDirStore.getAt(i).json.text;
                                                                    }
                                                                }
                                                                onClick(obj.getValue(),name);
                                                            }
                                                     }
                                            }]
                                },{
                                    columnWidth:.8,
                                    layout: 'form',
                                    items: [GridModel.getGrid()]
                                }]
                    }]
                });
            }
        };
    } ();
    function onClick(type,name) {
            dir=type;
            GridBaseModel.grid.setTitle("已选中【"+name+"】");
            GridBaseModel.dir=dir;
            GridBaseModel.refresh();
    }
    Ext.onReady(function(){ 
                ConfigForm.show();
                
                indexDirStore.reload();
                indexDirStore.on("load",function(){
                    var dir=indexDirStore.getAt(0).json.value;
                    var dirName=indexDirStore.getAt(0).json.text;
                    GridBaseModel.grid.setTitle("已选中【"+dirName+"】");
                    GridBaseModel.dir=dir;
                    GridBaseModel.refresh();
                },this);
    });