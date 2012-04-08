package com.apdplat.module.security.service;

import com.apdplat.module.security.model.Role;
import com.apdplat.module.security.model.User;
import com.apdplat.platform.service.ServiceFacade;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    @Resource(name="serviceFacade")
    private ServiceFacade serviceFacade;

    
    public String toUserRoleJson(String userId){
        int id=Integer.parseInt(userId);
        User user=serviceFacade.retrieve(User.class, id);
        List<Role> roles=user.getRoles();
        return toJson(roles);
    }
    public String toAllRoleJson(){
        List<Role> roles=serviceFacade.query(Role.class, null).getModels();
        return toJson(roles);
    }
    
    public String toJson(List<Role> roles){        
        if(roles==null || roles.isEmpty()){
            return "";
        }
        
        StringBuilder json=new StringBuilder();
        
        json.append("[");
        for(Role role : roles){
            json.append("{'text':'")
                .append(role.getRoleName())
                .append("','id':'role-")
                .append(role.getId())
                .append("','iconCls':'")
                .append("role")
                .append("'")
                .append(",'leaf':true")
                .append("},");
        }
        json=json.deleteCharAt(json.length()-1);
        json.append("]");
            
        return json.toString();
    }
}
