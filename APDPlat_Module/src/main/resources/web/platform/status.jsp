<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html>
<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>系统运行情况</title>
        <%@include file="include/common.jsp" %>

        <%
            String osName=System.getProperty("os.name");
            String osVersion=System.getProperty("os.version");
            String osArch=System.getProperty("os.arch");
            String jvmName=System.getProperty("java.vm.name");
            String jvmVersion=System.getProperty("java.vm.version");
            String jvmVendor=System.getProperty("java.vm.vendor");

            float max=(float)Runtime.getRuntime().maxMemory()/1000000;
            float total=(float)Runtime.getRuntime().totalMemory()/1000000;
            float free=(float)Runtime.getRuntime().freeMemory()/1000000;
            float usable=max-total+free;
            
            StringBuilder xml=new StringBuilder();
            xml.append("<?xml version=\"1.0\"?>")
               .append("<chart caption=\"系统运行情况\">")
               .append("<set label=\"最大可分配内存(MB)\" value=\"").append(max).append("\"/>")
               .append("<set label=\"已分配内存(MB)\" value=\"").append(total).append("\"/>")
               .append("<set label=\"可用内存(MB)\" value=\"").append(usable).append("\"/>")
               .append("</chart>");
        %>
        <script type="text/javascript">
            Ext.onReady(function(){
                new Ext.ux.Chart.Fusion.Panel({ 
                                  applyTo:'memChart',
                                  width:'90%',
                                  height:360,
                                  chartURL : contextPath+"/FusionCharts/Column3D.swf", 
                                  chartData : '<%=xml%>', 
                                  mediaCfg : { 
                                     params : { 
                                         scale : "exactfit" 
                                     } 
                                  }
                              });           
            });
        </script>
</head>
<body>
    <center><h1><a href="#" onclick="location.reload();">刷新</a></h1></center>
        <div id='memChart'>
        </div>
    操作系统信息：<%=osName%>   <%=osVersion%>    <%=osArch%><br/>
    Java虚拟机信息：<%=jvmName%>    <%=jvmVersion%> <%=jvmVendor%><br/>
</body>
</html>