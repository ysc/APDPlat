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
<link rel="stylesheet" type="text/css" href="<%=contextPath%>/extjs/css/extjs_css_merge.css">
<link rel="stylesheet" type="text/css" href="<%=contextPath%>/extjs/ux/css/extjs_ux_css_merge.css">
<link rel="stylesheet" type="text/css" href="<%=contextPath%>/DateTime/DateTime_merge.css">
<link rel="stylesheet" type="text/css" href="<%=contextPath%>/platform/css/platform_css_merge.css">
<!--引用合并的js，也可以引用未合并的js-->
<script type="text/javascript" src="<%=contextPath%>/extjs/js/extjs_js_merge.js"></script>
<script type="text/javascript" src="<%=contextPath%>/extjs/ux/extjs_ux_merge.js"></script>
<script type="text/javascript" src="<%=contextPath%>/FusionCharts/FusionCharts_merge.js"></script>
<script type="text/javascript" src="<%=contextPath%>/ckeditor/ckeditor_merge.js"></script>
<script type="text/javascript" src="<%=contextPath%>/ckfinder/ckfinder_merge.js"></script>
<script type="text/javascript" src="<%=contextPath%>/DateTime/DateTime_merge.js"></script>
<script type="text/javascript" src="<%=contextPath%>/js/js_merge.js"></script>
<script type="text/javascript" src="<%=contextPath%>/platform/js/platform_js_merge.js"></script>

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