package com.apdplat.module.security.service;

import com.apdplat.module.security.model.Role;
import com.apdplat.platform.criteria.Criteria;
import com.apdplat.platform.criteria.Operator;
import com.apdplat.platform.criteria.PropertyCriteria;
import com.apdplat.platform.criteria.PropertyEditor;
import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.result.Page;
import com.apdplat.platform.service.ServiceFacade;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    protected static final APDPlatLogger log = new APDPlatLogger(RoleService.class);
    @Resource(name="serviceFacade")
    private ServiceFacade serviceFacade;

    public static List<String> getChildNames(Role role){
        List<String> names=new ArrayList<>();
        List<Role> child=role.getChild();
        for(Role item : child){
            names.add(item.getRoleName());
            names.addAll(getChildNames(item));
        }
        return names;
    }
    public static List<Integer> getChildIds(Role role){
        List<Integer> ids=new ArrayList<>();
        List<Role> child=role.getChild();
        for(Role item : child){
            ids.add(item.getId());
            ids.addAll(getChildIds(item));
        }
        return ids;
    }
    public static boolean isParentOf(Role parent,Role child){
        Role role=child.getParent();
        while(role!=null){
            if(role.getId()==parent.getId()){
                return true;
            }
            role=role.getParent();
        }
        return false;
    }
    
    public String toRootJson(boolean recursion){
        Role rootRole=getRootRole();
        if(rootRole==null){
            log.error("获取根角色失败！");
            return "";
        }
        StringBuilder json=new StringBuilder();
        json.append("[");

        json.append("{'text':'")
            .append(rootRole.getRoleName())
            .append("','id':'role-")
            .append(rootRole.getId());
            if(rootRole.getChild().isEmpty()){
                json.append("','leaf':true,'cls':'file'");
            }else{
                json.append("','leaf':false,'cls':'folder'");
                
                if (recursion) {
                    for(Role item : rootRole.getChild()){
                        json.append(",children:").append(toJson(item.getId(), recursion));
                    }
                }
            }
        json.append("}");
        json.append("]");
        
        return json.toString();
    }
    public String toJson(int roleId, boolean recursion){
        Role role=serviceFacade.retrieve(Role.class, roleId);
        if(role==null){
            log.error("获取ID为 "+roleId+" 的角色失败！");
            return "";
        }
        List<Role> child=role.getChild();
        if(child.isEmpty()){
            return "";
        }
        StringBuilder json=new StringBuilder();
        json.append("[");

        
        for(Role item : child){
            json.append("{'text':'")
                .append(item.getRoleName())
                .append("','id':'role-")
                .append(item.getId());
                if(item.getChild().isEmpty()){
                    json.append("','leaf':true,'cls':'file'");
                }else{
                    json.append("','leaf':false,'cls':'folder'");
                    if (recursion) {
                        json.append(",children:").append(toJson(item.getId(), recursion));
                    }
                }
           json .append("},");
        }
        //删除最后一个,号，添加一个]号
        json=json.deleteCharAt(json.length()-1);
        json.append("]");

        return json.toString();
    }
    public Role getRootRole(){
        PropertyCriteria propertyCriteria = new PropertyCriteria(Criteria.or);
        propertyCriteria.addPropertyEditor(new PropertyEditor("roleName", Operator.eq, "String","角色"));
        Page<Role> page = serviceFacade.query(Role.class, null, propertyCriteria);
        if (page.getTotalRecords() == 1) {
            return page.getModels().get(0);
        }
        return null;
    }
}
