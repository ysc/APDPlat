package com.apdplat.module.security.service;

import com.apdplat.module.module.model.Command;
import com.apdplat.module.module.service.ModuleService;
import com.apdplat.module.system.service.PropertyHolder;
import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.service.ServiceFacade;
import com.apdplat.platform.util.FileUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.access.intercept.RequestKey;
import org.springframework.security.web.util.AntUrlPathMatcher;
import org.springframework.stereotype.Service;

/**
 *
 * @author ysc
 */
@Service
public class SpringSecurityService {
    protected static final APDPlatLogger log = new APDPlatLogger(SpringSecurityService.class);
    @Resource(name = "filterSecurityInterceptor")
    private  FilterSecurityInterceptor filterSecurityInterceptor;
    @Resource(name="serviceFacade")
    protected ServiceFacade serviceFacade;
    /**
     *
     * @return 系统是否启用安全机制
     */
    public static boolean isSecurity(){
        String security=PropertyHolder.getProperty("security");
        if(security!=null && "true".equals(security.trim())){
            return true;
        }
        return false;
    }
    /**
     * 初始化系统安全拦截信息
     */
    @PostConstruct
    public  void initSecurityConfigInfo(){
        String security=PropertyHolder.getProperty("security");
        if(security==null || !"true".equals(security.trim())){
            log.info("当前系统禁用安全机制");
            return ;
        }
        log.info("开始初始化权限子系统...");
        LinkedHashMap<RequestKey, Collection<ConfigAttribute>> requestMap =new LinkedHashMap<>();
        
        SecurityConfig manager=new SecurityConfig("ROLE_MANAGER");
        SecurityConfig superManager=new SecurityConfig("ROLE_SUPERMANAGER");
        Collection<ConfigAttribute> value=new ArrayList<>();
        value.add(manager);
        value.add(superManager);
        Collection<String> urls=new LinkedHashSet<>(); 
        String[] urlFiles=PropertyHolder.getProperty("manager.default.url").split(",");
        for(String urlFile : urlFiles){
            Collection<String> url=FileUtils.getClassPathTextFileContent(urlFile);
            urls.addAll(url);
        }
        for(String url : urls){
            if(url.contains("=")){
                String[] attr=url.split("=");
                url=attr[0];
                String[] roles=attr[1].split(",");
                Collection<ConfigAttribute> v=new ArrayList<>();
                for(String role : roles){
                    v.add(new SecurityConfig(role));
                }
                //POST
                RequestKey key=new RequestKey(url,"POST");
                requestMap.put(key, v);
                //GET
                key=new RequestKey(url,"GET");
                requestMap.put(key, v);
            }else{
                //POST
                RequestKey key=new RequestKey(url,"POST");
                requestMap.put(key, value);
                //GET
                key=new RequestKey(url,"GET");
                requestMap.put(key, value);
            }
        }

        for(Command command : serviceFacade.query(Command.class).getModels()){
            List<String> paths=ModuleService.getCommandPath(command);
            Map<String,String> map=ModuleService.getCommandPathToRole(command);
            for(String path : paths){
                RequestKey key=new RequestKey(path.toString().toLowerCase()+".action*","POST");
                value=new ArrayList<>();
                value.add(new SecurityConfig("ROLE_MANAGER"+map.get(path)));
                value.add(superManager);
                requestMap.put(key, value);
                //GET
                key=new RequestKey(path.toString().toLowerCase()+".action*","GET");
                requestMap.put(key, value);
            }
        }
        RequestKey key=new RequestKey("/**","POST");
        value=new ArrayList<>();
        value.add(superManager);
        requestMap.put(key, value);
        //GET
        key=new RequestKey("/**","GET");
        requestMap.put(key, value);        

        DefaultFilterInvocationSecurityMetadataSource source=new DefaultFilterInvocationSecurityMetadataSource(new AntUrlPathMatcher(),requestMap);
        
        filterSecurityInterceptor.setSecurityMetadataSource(source);

        log.debug("system privilege info:\n");
        for(Map.Entry<RequestKey, Collection<ConfigAttribute>> entry : requestMap.entrySet()){
            log.debug(entry.getKey().toString());
            for(ConfigAttribute att : entry.getValue()){
                log.debug("\t"+att.toString());
            }
        }
        log.info("完成初始化权限子系统...");
    }
}
