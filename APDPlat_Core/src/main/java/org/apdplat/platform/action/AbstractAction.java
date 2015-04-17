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

import org.apdplat.platform.model.Model;
import org.apdplat.platform.result.Page;
import org.apdplat.platform.service.Service;
import org.apdplat.platform.util.SpringContextUtils;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 *
 *控制器接口的抽象实现类
 *
 * @author 杨尚川
 */
public abstract class AbstractAction<T extends Model, S extends Service<T>>  extends ActionSupport implements Action {

    protected S service = null;
    protected T model = null;
    protected Page<T> page = new Page<>();
    @Resource(name = "springContextUtils")
    protected SpringContextUtils springContextUtils;

    @PostConstruct
    private void initService() {
        if (this.service == null) {
            this.service = (S) springContextUtils.getBean(getDefaultServiceName());
        }
        if (this.model == null) {
            this.model = (T) springContextUtils.getBean(getDefaultModelName());
        }
    }

    private String getDefaultServiceName(){
        return getDefaultModelName()+"Service";
    }

    private String getDefaultModelName(){
        return getDefaultModelName(getClass());
    }

    /**
     * 在添加及更新一个特定的完整的Model之前对Model的组装，以确保组装之后的Model是一个语义完整的模型
     * @return
     */
    public T assemblyModel(T model) {
        return model;
    }

    @Override
    public String create() {
        service.create(assemblyModel(model));

        super.setFeedback(new Feedback(model.getId(), "添加成功"));

        return "";
    }


    @Override
    public String retrieve() {
        this.setModel(service.retrieve(model.getId()));

        return "";
    }


    @Override
    public String updatePart() {
        service.update(model.getId(), getPartProperties(model));

        super.setFeedback(new Feedback(model.getId(), "添加成功"));

        return "";
    }

    @Override
    public String updateWhole() {
        service.update(assemblyModel(model));

        super.setFeedback(new Feedback(model.getId(), "更新成功"));

        return "";
    }

    @Override
    public String delete() {
        service.delete(super.getIds());

        return "";
    }

    @Override
    public String query() {
        this.setPage(service.query(super.getPageCriteria(), super.buildPropertyCriteria(), super.buildOrderCriteria()));
        return "";
    }

    @Override
    public String search() {
        return null;
    }

    public T getModel() {
        return this.model;
    }

    public void setModel(T model) {
        this.model = model;
    }

    public Page<T> getPage() {
        return page;
    }

    public void setPage(Page<T> page) {
        this.page = page;
    }
}