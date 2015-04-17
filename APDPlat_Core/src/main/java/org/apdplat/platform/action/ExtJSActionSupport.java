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

package org.apdplat.platform.action;

import org.apdplat.platform.criteria.PageCriteria;

/**
 *支持Ext JS的分页请求参数
 * @author 杨尚川
 */
public abstract class ExtJSActionSupport extends ActionSupport{
    private Integer start=null;
    private Integer limit=null;

    public void convert(){
        if(start==null && limit!=null){
            PageCriteria pageCriteria=new PageCriteria();
            pageCriteria.setSize(limit);
            super.setPageCriteria(pageCriteria);
        }
        if(start!=null && limit!=null){
            PageCriteria pageCriteria=new PageCriteria();
            int page=(start+limit)/limit;
            int size=limit;
            pageCriteria.setPage(page);
            pageCriteria.setSize(size);
            super.setPageCriteria(pageCriteria);
        }
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
        convert();
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
        convert();
    }
}