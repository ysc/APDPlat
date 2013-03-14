package com.apdplat.module.security.service;

import com.apdplat.module.security.model.Org;
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

/**
 *
 * @author ysc
 */
@Service
public class OrgService {
    protected static final APDPlatLogger log = new APDPlatLogger(OrgService.class);
    @Resource(name="serviceFacade")
    private ServiceFacade serviceFacade;

    public static List<String> getChildNames(Org org){
        List<String> names=new ArrayList<>();
        List<Org> child=org.getChild();
        for(Org item : child){
            names.add(item.getOrgName());
            names.addAll(getChildNames(item));
        }
        return names;
    }
    public static List<Integer> getChildIds(Org org){
        List<Integer> ids=new ArrayList<>();
        List<Org> child=org.getChild();
        for(Org item : child){
            ids.add(item.getId());
            ids.addAll(getChildIds(item));
        }
        return ids;
    }
    public static boolean isParentOf(Org parent,Org child){
        Org org=child.getParent();
        while(org!=null){
            if(org.getId()==parent.getId()){
                return true;
            }
            org=org.getParent();
        }
        return false;
    }
    
    public String toRootJson(){
        Org rootOrg=getRootOrg();
        if(rootOrg==null){
            log.error("获取根组织架构失败！");
            return "";
        }
        StringBuilder json=new StringBuilder();
        json.append("[");

        json.append("{'text':'")
            .append(rootOrg.getOrgName())
            .append("','id':'")
            .append(rootOrg.getId());
            if(rootOrg.getChild().isEmpty()){
                json.append("','leaf':true,'cls':'file'");
            }else{
                json.append("','leaf':false,'cls':'folder'");
            }
        json.append("}");
        json.append("]");
        
        return json.toString();
    }
    public String toJson(int orgId){
        Org org=serviceFacade.retrieve(Org.class, orgId);
        if(org==null){
            log.error("获取ID为 "+orgId+" 的组织架构失败！");
            return "";
        }
        List<Org> child=org.getChild();
        if(child.isEmpty()){
            return "";
        }
        StringBuilder json=new StringBuilder();
        json.append("[");

        
        for(Org item : child){
            json.append("{'text':'")
                .append(item.getOrgName())
                .append("','id':'")
                .append(item.getId());
                if(item.getChild().isEmpty()){
                    json.append("','leaf':true,'cls':'file'");
                }else{
                    json.append("','leaf':false,'cls':'folder'");
                }
           json .append("},");
        }
        //删除最后一个,号，添加一个]号
        json=json.deleteCharAt(json.length()-1);
        json.append("]");

        return json.toString();
    }
    public Org getRootOrg(){
        PropertyCriteria propertyCriteria = new PropertyCriteria(Criteria.or);
        propertyCriteria.addPropertyEditor(new PropertyEditor("orgName", Operator.eq, "String","组织架构"));
        Page<Org> page = serviceFacade.query(Org.class, null, propertyCriteria);
        if (page.getTotalRecords() == 1) {
            return page.getModels().get(0);
        }
        return null;
    }
}
