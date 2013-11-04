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
<%@page  import="org.apdplat.module.security.service.OnlineUserService"%>
<%@page  import="org.apdplat.module.security.service.SpringSecurityService"%>
<%@page  import="org.apdplat.module.security.service.UserDetailsServiceImpl"%>
<%@page  import="org.apdplat.module.system.service.PropertyHolder"%>
<%@page  import="java.util.Collection"%>
<%@page  import="org.apdplat.platform.util.FileUtils"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<%
response.addHeader("login","true");  
//供记录用户登录日志使用
String userAgent=request.getHeader("User-Agent");
request.getSession().setAttribute("userAgent", userAgent);
if(!SpringSecurityService.isSecurity()){
    //如果没有启用安全机制则直接进入主界面
    response.sendRedirect("platform/index.jsp");
    return;
}
String name=OnlineUserService.getUsername(request.getSession(true).getId());
if(!"匿名用户".equals(name)){
    //用户已经等登录直接进入主界面
    response.sendRedirect("platform/index.jsp");
    return;
}

String message="";
String state=request.getParameter("state");
if(state!=null){
    response.addHeader("state",state);  
}
if("checkCodeError".equals(state)){
    response.addHeader("checkCodeError","true");  
    message="验证码错误";
    response.getWriter().write(message);
    response.getWriter().flush();
    response.getWriter().close();
    return;
}

String SPRING_SECURITY_LAST_USERNAME=UserDetailsServiceImpl.SPRING_SECURITY_LAST_USERNAME;

String lastUsername="";
if(SPRING_SECURITY_LAST_USERNAME!=null){
    lastUsername=SPRING_SECURITY_LAST_USERNAME;
    if(request.getParameter("login_error")!=null){
        String tip=UserDetailsServiceImpl.getMessage(lastUsername);
        if(tip!=null){
            message=tip;
            response.addHeader("login_error","true");  
            response.getWriter().write(message);
            response.getWriter().flush();
            response.getWriter().close();
            return;
        }
    }
 }
String contextPath=org.apdplat.module.system.service.SystemListener.getContextPath();
String appName=PropertyHolder.getProperty("app.name");
String requestCode="";
if(FileUtils.existsFile("/WEB-INF/licence")){
    Collection<String> reqs = FileUtils.getTextFileContent("/WEB-INF/licence");
    if(reqs!=null && reqs.size()==1){
        requestCode=reqs.iterator().next().toString();
    }
}
String shortcut=PropertyHolder.getProperty("module.short.name");
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><%=appName%></title>
    <link rel="shortcut icon" href="images/<%= shortcut %>.ico" />
    <link rel="stylesheet" type="text/css" href="css/login_merge.css"/>
    <script type="text/javascript" src="js/login_merge.js"></script>
    <script type="text/javascript">
        //解决Ext在ie9报错：不支持extjs对象的“createContextualFragment属性或方法”
        if ((typeof Range !== "undefined") && !Range.prototype.createContextualFragment) {
            Range.prototype.createContextualFragment = function(html) {
                var frag = document.createDocumentFragment(),div = document.createElement("div");
                frag.appendChild(div);
                div.outerHTML = html;
                return frag;
            };
        }

        var contextPath='<%=contextPath%>';
        var requestCode='<%=requestCode%>';
        var loginImage='<%=PropertyHolder.getProperty("login.image")%>';
        var logoImage='<%=PropertyHolder.getProperty("logo.image")%>';
        
        //判断当前登录窗口有没有被嵌在其他窗口内部
        function is_toplevel(w){
               return (w.parent == w);
        }
        function autoFit() {
            if(!is_toplevel(this)){
                parent.location.href=this.location.href;
            }

            window.moveTo(0, 0);
            window.resizeTo(window.screen.availWidth,window.screen.availHeight);
        }
        function refreshTheme(){
                  var storeTheme=Ext.util.Cookies.get('theme');
                  if(storeTheme==null || storeTheme==''){
                          storeTheme='ext-all';
                  }
                  Ext.util.CSS.swapStyleSheet("theme", contextPath+"/extjs/css/"+storeTheme+".css");  
        }
        var lastUsername="<%=lastUsername%>";
        var message="<%=message%>";
        Ext.onReady(function()
        {
            autoFit();
            refreshTheme();
            if("<%=state%>"=="checkCodeError"){
                Ext.ux.Toast.msg('登录提示：','验证码错误，请重新登录!');  
            }
            if("<%=state%>"=="session-invalid" || "<%=state%>"=="session-authentication-error"){
                Ext.ux.Toast.msg('操作提示：','操作已经超时，请重新登录!');  
            }
            if("<%=state%>"=="session-expired"){
                Ext.ux.Toast.msg('操作提示：','您已被踢下线，请重新登录!');  
            }
            if(message!=""){
                Ext.ux.Toast.msg('登录提示：',message); 
            }
            var win=new LoginWindow();
            win.show();
            if(lastUsername!=""){
                parent.Ext.getCmp('j_username').setValue(lastUsername);
            }
            Ext.get('loading-mask').fadeOut( {
                    remove : true
            });
            fixPng();
            if(""!=requestCode){
                //购买产品
                BuyModel.show(requestCode);
            }
        })
    </script>

</head>
<body>

<div id="loading-mask">
	<div id="loading">
            <div style="text-align:center;padding-top:26%"><img alt="Loading..."  src="images/extanim32.gif" width="32" height="32" style="margin-right:8px;"/>Loading...</div>
	</div>
</div>

</body>
</html>