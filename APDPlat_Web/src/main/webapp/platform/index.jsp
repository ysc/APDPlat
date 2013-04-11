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
<%@page  import="org.apdplat.module.security.model.User"%>
<%@page  import="org.apdplat.module.security.service.UserHolder"%>
<%@page  import="org.apdplat.module.system.service.PropertyHolder"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<%
response.addHeader("login_success","true");  
User loginUser=UserHolder.getCurrentLoginUser();
String username="匿名用户";
String realName="";
Integer userId=0;
String orgName="匿名组织架构";
int orgId=0;
String userPath="";
if(loginUser!=null){
    //设置用户的数据上传主目录
    userPath=request.getContextPath() + "/userfiles/"+loginUser.getId()+"/";
    request.getSession().setAttribute("userPath", userPath);
    orgName=loginUser.getOrg()==null?"匿名组织架构":loginUser.getOrg().getOrgName();
    orgId=loginUser.getOrg()==null?0:loginUser.getOrg().getId();
    username=loginUser.getUsername();
    realName=loginUser.getRealName();
    if(realName==null){
        realName=username;
    }
    userId=loginUser.getId();
}
String appName=PropertyHolder.getProperty("app.name").replace("\"", "'");
String appCopyright=PropertyHolder.getProperty("app.copyright").replace("\"", "'");
String appVersion=PropertyHolder.getProperty("app.version").replace("\"", "'");
String contact=PropertyHolder.getProperty("app.contact").replace("\"", "'");
String support=PropertyHolder.getProperty("app.support").replace("\"", "'");
String topnavPage="include/"+PropertyHolder.getProperty("topnav.page");
String searchForm="js/"+PropertyHolder.getProperty("search.js");
String indexPage="js/"+PropertyHolder.getProperty("index.page.js");
String shortcut=PropertyHolder.getProperty("module.short.name");
%>
<html  xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title><%=appName%></title>
        <link rel="shortcut icon" href="../images/<%= shortcut %>.ico" />
        <%@include file="include/common.jsp" %>
        <link rel="stylesheet" type="text/css" href="css/qq.css"/>
        <script type="text/javascript" src="js/onlineUser.js"></script>
        <script type="text/javascript" src="js/onlineChat.js"></script>
        <script type="text/javascript" src="<%= indexPage %>"></script>
        <script type="text/javascript" src="<%= searchForm %>"></script>
        <script type="text/javascript" src="js/index.js"></script>
        <script type="text/javascript" src="js/modfiyPassword.js"></script>
        <script type="text/javascript">            
            var userPath="<%=userPath%>";
            var appCopyright="<%=appCopyright%>";
            var appVersion="<%=appVersion%>";
            var appName="<%=appName%>";
            var contact="<%=contact%>";
            var support="<%=support%>";
            var userId="<%=userId%>";
            var username="<%=username%>";
            var realName="<%=realName%>";
            var orgName="<%=orgName%>";
            var orgId="<%=orgId%>";
            
            var privileges='<%=loginUser.getAuthoritiesStr()%>';
            function isGranted(namespace,action,command){
                if(privileges.toString().indexOf("ROLE_SUPERMANAGER")!=-1){
                    return true;
                }
                var role="ROLE_MANAGER_"+namespace.toUpperCase().replace("/", "_")+"_"+process(action).toUpperCase()+"_"+command.toUpperCase();
                if(privileges.toString().indexOf(role)==-1){
                    return false;
                }
                return true;
            }
            //用来保存在tab页面中打开的窗口
            var openingWindows=new Array();
            function refreshAll(){
                for(var i=0;i<openingWindows.length;i++){
                    if(openingWindows[i]!=undefined && openingWindows[i].closed==false){
                        openingWindows[i].refreshTheme();
                    }
                }
                refreshTheme();
            }
            
            function changeTime(){
                     document.getElementById("time").innerHTML = new Date().format('Y年n月j日  H:i:s');
            }
            //setInterval("changeTime()",1000);
            function selectSwitch(current){
                var lis=document.getElementsByTagName("li")
                for(var i=0;i<lis.length;i++){
                    if(lis[i].className=="activeli"){
                        lis[i].className="commonli";
                    }
                };
                current.className="activeli";
            }
	</script>
    </head>
    <body id="apdplat_main">
        <div id="loading-mask"></div>
        <div id="loading">
            <div class="loading-indicator"></div>
        </div>
        <div id="north">
            <div id="app-header">
                <div id="header-left">
                        <img id ="logo" src="../images/<%=PropertyHolder.getProperty("logo.image")%>" height="50" style="max-width:300px;"/>
                </div>
                <div id="header-main">
                        <div id="topInfoPanel" style="float:left;padding-bottom: 4px">
                                <div id="welcomeMsg">欢迎[ <%=realName%> | <%=orgName%> ]登录系统</div>
                                
                        </div>
                        <div class="clear"></div>
                        <ul id="header-topnav">
                            <jsp:include page="<%= topnavPage %>"></jsp:include>
                        </ul>
                </div>
                <div id="header-right">
                        <div id="currentTime"><span id="time"></span></div>
                        <div id="setting">
                                <a href="#" onclick='triggerHeader();'><img id="trigger-image" src="images/trigger-up.png"/></a>
                        </div>
                        <div id="search" style="width:260px;float:right;padding-top:8px;">&nbsp;</div>
                </div>
            </div>
        </div>

        <div id="west"></div>
        <div id="south"></div>
        <div id="main"></div>
    </body>
</html>