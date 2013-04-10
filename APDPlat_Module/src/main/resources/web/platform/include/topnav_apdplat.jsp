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
<style type="text/css">
    .menu-desktop{
        background-image:url('images/desktop.gif')!important;
        background-repeat:no-repeat;
        background-position:10% 50%;
    }
</style>

                                <li class="commonli"   onmouseover="selectSwitch(this); "><a href="#" onclick="show('security');" class="menu-desktop">安全管理</a></li>
                                <li class="commonli"   onmouseover="selectSwitch(this); "><a href="#" onclick="show('system');" class="menu-desktop">系统管理</a></li>
                                <li class="commonli"   onmouseover="selectSwitch(this); "><a href="#" onclick="show('monitor');" class="menu-desktop">系统监控</a></li>