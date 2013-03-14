package com.apdplat.module.security.service;

import com.apdplat.module.security.model.User;
import com.apdplat.module.security.service.filter.IPAccessControler;
import com.apdplat.platform.criteria.Criteria;
import com.apdplat.platform.criteria.Operator;
import com.apdplat.platform.criteria.PropertyCriteria;
import com.apdplat.platform.criteria.PropertyEditor;
import com.apdplat.platform.filter.OpenEntityManagerInViewFilter;
import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.result.Page;
import com.apdplat.platform.service.ServiceFacade;
import com.apdplat.platform.util.FileUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.util.TextEscapeUtils;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    protected static final APDPlatLogger log = new APDPlatLogger(UserDetailsServiceImpl.class);
    @Resource(name = "serviceFacade")
    private ServiceFacade serviceFacade;
    private static Map<String,String> messages = new HashMap<>();
    private String message;
    private static final IPAccessControler ipAccessControler=new IPAccessControler();

    public synchronized static String getMessage(String username) {
        String result = messages.get(username);
        log.debug("username "+username+" getMessage:"+result);
        messages.clear();
        return result;
    }

    @Override
    public synchronized UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        try{
            if(ipAccessControler.deny(OpenEntityManagerInViewFilter.request)){
                message = "IP访问策略限制";
                throw new UsernameNotFoundException(message);
            }
            return load(username);
        }catch(  UsernameNotFoundException | DataAccessException e){
            throw e;
        }
        finally{
            log.debug("messages put "+username+":"+message);
            messages.put(TextEscapeUtils.escapeEntities(username), message);
        }
    }
    
    public UserDetails load(String username) throws UsernameNotFoundException, DataAccessException {
        message = "密码不正确";

        if(FileUtils.existsFile("/WEB-INF/licence")){
            Collection<String> reqs = FileUtils.getTextFileContent("/WEB-INF/licence");
            message="您还没有购买产品";
            if(reqs!=null && reqs.size()==1){
                message+=":"+reqs.iterator().next().toString();
            }
            log.info(message);
            throw new UsernameNotFoundException(message);
        }
        if (StringUtils.isBlank(username)) {
            log.info("请输入用户名");
            message = "请输入用户名";
            throw new UsernameNotFoundException("请输入用户名");
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
            log.info("用户账号不存在: " + username);
            message = "用户账号不存在";
            throw new UsernameNotFoundException("用户账号不存在");
        }
        if(!page.getModels().get(0).isEnabled()){
            message = "用户账号被禁用";
            throw new UsernameNotFoundException("用户账号被禁用");
        }
        if(!page.getModels().get(0).isAccountNonExpired()){
            message = "用户帐号已过期";
            throw new UsernameNotFoundException("用户帐号已过期");
        }
        if(!page.getModels().get(0).isAccountNonLocked()){
            message = "用户帐号已被锁定";
            throw new UsernameNotFoundException("用户帐号已被锁定");
        }
        if(!page.getModels().get(0).isCredentialsNonExpired()){
            message = "用户凭证已过期";
            throw new UsernameNotFoundException("用户凭证已过期");
        }
        if(page.getModels().get(0).getAuthorities()==null){
            message = "用户帐号未被授予任何权限";
            throw new UsernameNotFoundException("用户帐号未被授予任何权限");
        }

        return page.getModels().get(0);
    }
}
