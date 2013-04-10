/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

var tools= [{
        id:'refresh',  
        handler: function(e, target, panel){  

        } 
    }, {
        id : 'close',
        handler : function(e, target, panel) {
            panel.ownerCt.remove(panel, true);
        }
    }];
var indexPage={
        title:"我的桌面",
        closable : false,
        iconCls:'computer',
        style : 'padding:4px 4px 4px 4px;',
        xtype : 'portal',
        region : 'center',
        margins : '5 5 5 0',
        layout:'fit',
        items : [{
            style : 'padding:0 0 10px 0',
            items : [{
                title : '系统运行情况',
                tools : tools,
                autoScroll:false,
                autoWidth:true,
                height:470,
                scripts:true,
                html: '<iframe id="'+"windows"+id+'" name="'+"windows"+id+'" scrolling="auto" frameborder="0" width="100%" height="100%" onload="Ext.Msg.hide()" src="'+contextPath + '/platform/status.jsp'+'"></iframe>'
            }]
        }]
    };