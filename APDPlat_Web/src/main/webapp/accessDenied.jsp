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
<%@page  import="org.apdplat.module.security.service.SpringSecurityService"%>
<%   
 response.addHeader("accessDenied","true");  
 if(SpringSecurityService.isSecurity()){
    return; 
 }
 String basePath=request.getContextPath();  
%>  
<html>  
 <head>  
  <title>访问拒绝</title>
   <style type="text/css">  
   <!--  
   .STYLE10 {  
    font-family: "黑体";
    font-size: 36px;  
   }  
   -->    
   </style>  
 </head>  
 <body>  
  <table width="510" border="0" align="center" cellpadding="0" cellspacing="0">  
   <tr>  
     <td><img src="<%=basePath%>/images/error_top.jpg" width="510" height="80" /></td>  
     </tr>  
   <tr>  
     <td height="200" align="center" valign="top" background="<%=basePath%>/images/error_bg.jpg">  
      <table width="80%" border="0" cellspacing="0" cellpadding="0">  
         <tr>  
           <td width="34%" align="right"><img src="<%=basePath%>/images/error.gif" width="128" height="128"></td>  
           <td width="66%" valign="bottom" align="center">  
            <span class="STYLE10">访问被拒绝</span>
            <div style="text-align: left;line-height: 22px;">  
             <font size="2">对不起，您的当前角色没有查看此页面的权限。请联系您的系统管理员，以获得相应的权限。点击这里返回主页。�</font>
          </div>  
          <a href="#" onclick="javascript:document.location.href='<%=basePath%>/j_spring_security_logout';">重新登录</a>
        </td>  
       </table>  
       </td>  
   </tr>        
   <tr>  
     <td><img src="<%=basePath%>/images/error_bootom.jpg" width="510" height="32" /></td>  
      </tr>  
 </table>  
 </body>  
</html>  