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

package org.apdplat.platform.filter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
/**
 *JPA事务开启和关闭过滤器
 * @author 杨尚川
 */
public class OpenEntityManagerInViewFilter extends org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter {
    public static HttpServletRequest request;
    public static final String EXCLUDE_SUFFIXS_NAME = "excludeSuffixs";
    public static final String ENTITY_MANAGER_FACTORY_BEAN_NAME = "entityManagerFactoryBeanName";

    private static final String[] DEFAULT_EXCLUDE_SUFFIXS = { ".js", ".css", ".jpg", ".gif" };

    private String[] excludeSuffixs = DEFAULT_EXCLUDE_SUFFIXS;

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
        OpenEntityManagerInViewFilter.request=request;
        String path = request.getServletPath();

        for (String suffix : excludeSuffixs) {
            if (path.endsWith(suffix)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void initFilterBean() throws ServletException {
        String entityManagerFactoryBeanName = getFilterConfig().getInitParameter(ENTITY_MANAGER_FACTORY_BEAN_NAME);
        if (StringUtils.isNotBlank(entityManagerFactoryBeanName)) {
            setEntityManagerFactoryBeanName(entityManagerFactoryBeanName);
        }

        String excludeSuffixStr = getFilterConfig().getInitParameter(EXCLUDE_SUFFIXS_NAME);
        if (StringUtils.isNotBlank(excludeSuffixStr)) {
            excludeSuffixs = excludeSuffixStr.split(",");
            //处理匹配字符串为".后缀名"
            for (int i = 0; i < excludeSuffixs.length; i++) {
                excludeSuffixs[i] = "." + excludeSuffixs[i];
            }
        }
    }
}