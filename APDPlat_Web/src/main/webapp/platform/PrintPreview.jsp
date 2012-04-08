<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <%@include file="include/common.jsp" %>
        <title>打印</title>

        <script  type="text/javascript">
            Ext.onReady(function() {
                PrintTip.show();
                var printSetup = new Ext.ux.PrintSetup();
                window.print();
            });
            PrintTip = function() {
                return {
                    show: function() {
                         var frm = new Ext.form.FormPanel({
                            applyTo : 'print-tip-div',
                            height:40,
                            autoWidth:true,
                            buttonAlign: 'center',
                            bodyStyle: 'padding:5px',
                            frame: true,//圆角和浅蓝色背景
                            items: [],

                            buttons: [{  
                                        text: '打印',
                                        iconCls:'print',
                                        scope: this,
                                        handler: function() {
                                            window.print();
                                        }
                                },{  
                                        text: '关闭',
                                        iconCls:'cancel',
                                        scope: this,
                                        handler: function() {
                                            window.close();
                                        }
                                }]
                        });
                    }
                };
            } ();
        </script>
        <style type="text/css" media=print>

        #print-tip-div{display : none }

        </style>
</head>
<body>
    <div id='print-tip-div'></div>
    <table align="center">
        <tr><td><div id='print-div' style="width:100%; height:100%;"></div></td></tr>
    </table>
</body>
</html>
