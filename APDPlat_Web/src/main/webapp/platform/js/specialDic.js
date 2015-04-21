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

//已经存在的备份时间点
    var existBackupStore=new Ext.data.Store({
        proxy : new parent.Ext.data.HttpProxy({
            url : contextPath+'/system/backup/store.action'
        }),
        reader: new Ext.data.JsonReader({},
            Ext.data.Record.create([{
                name: 'value'
            },{
                name: 'text'
            },]))
    });
    //索引文件的目录列表
    var indexDirStore=new Ext.data.Store({
        proxy : new parent.Ext.data.HttpProxy({
            url : contextPath+'/index/state/store.action'
        }),
        reader: new Ext.data.JsonReader({},
            Ext.data.Record.create([{
                name: 'value'
            },{
                name: 'text'
            },]))
    });
    //模型信息    
    var modelStore=new Ext.data.Store({
        proxy : new parent.Ext.data.HttpProxy({
            url : contextPath+'/log/operate-log/store.action'
        }),
        reader: new Ext.data.JsonReader({},
            Ext.data.Record.create([{
                name: 'value'
            },{
                name: 'text'
            },]))
    });
    //所有用户    
    var userStore=new Ext.data.Store({
        proxy : new parent.Ext.data.HttpProxy({
            url : contextPath+'/security/user/store.action'
        }),
        reader: new Ext.data.JsonReader({},
            Ext.data.Record.create([{
                name: 'value'
            },{
                name: 'text'
            },]))
    });