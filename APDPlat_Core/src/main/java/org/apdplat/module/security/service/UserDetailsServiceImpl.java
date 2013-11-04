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

package org.apdplat.module.security.service;

import org.apdplat.module.security.model.User;
import org.apdplat.module.security.service.filter.IPAccessControler;
import org.apdplat.platform.criteria.Criteria;
import org.apdplat.platform.criteria.Operator;
import org.apdplat.platform.criteria.PropertyCriteria;
import org.apdplat.platform.criteria.PropertyEditor;
import org.apdplat.platform.filter.OpenEntityManagerInViewFilter;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.result.Page;
import org.apdplat.platform.service.ServiceFacade;
import org.apdplat.platform.util.FileUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apdplat.module.system.service.PropertyHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.util.TextEscapeUtils;
import org.springframework.stereotype.Service;

/**
 * 用户登录认证服务实现类
 * 实现接口org.springframework.security.core.userdetails.UserDetailsService
 * 定义的方法UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
 * @author 杨尚川
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final APDPlatLogger LOG = new APDPlatLogger(UserDetailsServiceImpl.class);
    @Resource(name = "serviceFacade")
    private ServiceFacade serviceFacade;
    public static String SPRING_SECURITY_LAST_USERNAME = null;
    private static Map<String,String> messages = new HashMap<>();
    private String message;
    private static final IPAccessControler ipAccessControler=new IPAccessControler();
    
    /**
     * 在登录的JSP页面中，如果用户登录失败，可调用此方法返回登录失败的原因
     * @param username 登录失败的用户名
     * @return 登录失败的原因
     */
    public synchronized static String getMessage(String username) {
        String result = messages.get(TextEscapeUtils.escapeEntities(username));
        LOG.debug("获取用户登录失败原因，用户名： "+username+" 原因:"+result);
        messages.remove(TextEscapeUtils.escapeEntities(username));
        return result;
    }

    /**
     * 用户登录认证实现细节
     * @param username 用户名
     * @return 用户信息
     * @throws UsernameNotFoundException 如果没有相应的用户或是用户没有登录权限则抛出异常
     */
    @Override
    public synchronized UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //spring security最新版本不保存上一次登录的用户名，所以在这里自己保存
        SPRING_SECURITY_LAST_USERNAME = username;
        //加try catch的目的是为了能执行finally的代码，在登录失败的情况下保存失败原因
        try{
            if(ipAccessControler.deny(OpenEntityManagerInViewFilter.request)){
                message = "IP访问策略限制";
                LOG.info(message);
                throw new UsernameNotFoundException(message);
            }
            return load(username);
        }catch(  UsernameNotFoundException e){
            throw e;
        }
        finally{
            LOG.debug("保存用户登录失败原因，用户名： "+username+" 原因："+message);
            messages.put(TextEscapeUtils.escapeEntities(username), message);
        }
    }
    
    private UserDetails load(String username) throws UsernameNotFoundException {        
        if(FileUtils.existsFile("/WEB-INF/licence") && PropertyHolder.getBooleanProperty("security")){
            Collection<String> reqs = FileUtils.getTextFileContent("/WEB-INF/licence");
            message="您还没有购买产品";
            if(reqs!=null && reqs.size()==1){
                message+=":"+reqs.iterator().next().toString();
            }
            LOG.info(message);
            throw new UsernameNotFoundException(message);
        }
        if (StringUtils.isBlank(username)) {
            message = "请输入用户名";
            LOG.info(message);
            throw new UsernameNotFoundException(message);
        }
        /* 取得用户 */
        PropertyCriteria propertyCriteria = new PropertyCriteria(Criteria.or);
        propertyCriteria.addPropertyEditor(new PropertyEditor("username", Operator.eq, "String",username));

        //PropertyEditor sub1=new PropertyEditor(Criteria.or);
        //sub1.addSubPropertyEditor(new PropertyEditor("id", Operator.eq, 1));
        //sub1.addSubPropertyEditor(new PropertyEditor("id", Operator.eq, 2));

        //PropertyEditor sub=new PropertyEditor(Criteria.and);
        //sub.addSubPropertyEditor(new PropertyEditor("id", Operator.ne, 6));
        //sub.addSubPropertyEditor(new PropertyEditor("id", Operator.ne, 7));
        //sub.addSubPropertyEditor(new PropertyEditor("id", Operator.ne, 8));
        //sub.addSubPropertyEditor(sub1);

        //propertyCriteria.addPropertyEditor(sub);

        Page<User> page = serviceFacade.query(User.class, null, propertyCriteria);
        
        
        if (page.getTotalRecords() != 1) {
            message = "用户账号不存在";
            LOG.info(message+": " + username);
            throw new UsernameNotFoundException(message);
        }
        User user = page.getModels().get(0);        
        message = user.loginValidate();
        if(message != null){
            LOG.info(message);
            throw new UsernameNotFoundException(message);
        }
        //到了这里，如果用户还是不能登录，那么只有一种情况就是：密码不正确
        message = "密码不正确";

        return user;
    }
}