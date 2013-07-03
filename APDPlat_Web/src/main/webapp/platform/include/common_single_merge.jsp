<%--
   APDPlat - Application Product Development Platform
   Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
   
   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   
   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
String contextPath=org.apdplat.module.system.service.SystemListener.getContextPath();
String jsessionid=session.getId();
%>
<script type="text/javascript">
    var contextPath='<%=contextPath%>';
    var jsessionid='<%=jsessionid%>';
</script>
<!--引用合并的css，也可以引用未合并的css-->
<link rel="stylesheet" type="text/css" href="<%=contextPath%>/platform/include/apdplat_merge.css">
<!--引用合并的js，也可以引用未合并的js-->
<script type="text/javascript" src="<%=contextPath%>/platform/include/apdplat_merge.js"></script>

<!--web系统启动时自动生成的js-->
<script type="text/javascript" src="<%=contextPath%>/platform/js/dic.js"></script>
<!--web系统启动时自动生成的css-->
<link rel="stylesheet" type="text/css" href="<%=contextPath%>/platform/css/module.css">
<link rel="stylesheet" type="text/css" href="<%=contextPath%>/platform/css/operation.css">

<script type="text/javascript">
    if(this.parent!=this){
              parent.openingWindows.push(this);
    }
    function refreshTheme(){
              var storeTheme=Ext.util.Cookies.get('theme');
              if(storeTheme==null || storeTheme==''){
                      storeTheme='ext-all';
              }
              Ext.util.CSS.swapStyleSheet("theme", contextPath+"/extjs/css/"+storeTheme+".css");  
    }
    Ext.BLANK_IMAGE_URL = contextPath+'/extjs/images/default/s.gif';
    refreshTheme();
</script>