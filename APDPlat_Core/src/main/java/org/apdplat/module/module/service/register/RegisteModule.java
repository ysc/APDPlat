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

package org.apdplat.module.module.service.register;

import org.apdplat.module.module.model.Command;
import org.apdplat.module.module.model.Module;
import org.apdplat.module.module.service.ModuleParser;
import org.apdplat.module.module.service.ModuleService;
import org.apdplat.module.security.service.SpringSecurityService;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.module.system.service.RegisterService;
import org.apdplat.platform.criteria.Order;
import org.apdplat.platform.criteria.OrderCriteria;
import org.apdplat.platform.criteria.Sequence;
import org.apdplat.platform.result.Page;
import org.apdplat.platform.util.FileUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 *模块注册服务
 * @author 杨尚川
 */
@Service
public class RegisteModule extends RegisterService<Module>{
    private List<Module> data;
    @Resource(name = "springSecurityService")
    private  SpringSecurityService springSecurityService;
    @Resource(name = "moduleService")
    private  ModuleService moduleService;
    private boolean registed=false;

    private Module rootModule;
    
    @Override
    protected void registeSuccess() {
        if(registed){
            LOG.info("模块、命令数据注册完毕，重新初始化权限信息");
            springSecurityService.initSecurityConfigInfo();
        }else{
            LOG.info("模块、命令数据没有变化");
        }
        LOG.info("重新生成/platform/css/module.css");
        reGenerateModuleCss();
        LOG.info("重新生成/platform/css/operation.css");
        reGenerateCommandCss();
    }
    @PostConstruct
    private void reGenerateModuleCss(){
        OrderCriteria orderCriteria = new OrderCriteria();
        orderCriteria.addOrder(new Order("id", Sequence.ASC));
        //查出所有模块
        List<Module> modules=serviceFacade.query(Module.class,null,null,orderCriteria).getModels();
        StringBuilder css=new StringBuilder();
        css.append("/* 自动生成的文件，请不要修改 */");
        modules.forEach(module -> {
            if ("root".equals(module.getEnglish().trim())) {
                //忽略根模块
                return;
            }
            String path = ModuleService.getModulePath(module);
            path = path.substring(0, path.length() - 1);
            css.append(".")
                    .append(module.getEnglish())
                    .append("{")
                    .append("background-image: url(../images/module/")
                    .append(path)
                    .append(".png) !important;")
                    .append("}");
        });
        FileUtils.createAndWriteFile("/platform/css/module.css", css.toString());
        LOG.info("module css:"+css);
    }

    @PostConstruct
    private void reGenerateCommandCss() {
        OrderCriteria orderCriteria = new OrderCriteria();
        orderCriteria.addOrder(new Order("id", Sequence.ASC));
        //查出所有模块
        List<Command> list=serviceFacade.query(Command.class,null,null,orderCriteria).getModels();
        Set<String> commandCsses=new HashSet<>();
        list.forEach(command -> {
            //在module.xml中配置的命令可能会对于前台的几个按钮
            String dependency = PropertyHolder.getProperty("command." + command.getEnglish());
            String[] commands = null;
            if (StringUtils.isNotBlank(dependency)) {
                commands = dependency.split(",");
            } else {
                commands = new String[]{command.getEnglish()};
            }
            commandCsses.addAll(Arrays.asList(commands));
        });
        StringBuilder css=new StringBuilder();
        css.append("/* 自动生成的文件，请不要修改 */");
        commandCsses.forEach(commandCss -> {
            css.append(".")
                    .append(commandCss)
                    .append("{")
                    .append("background-image: url(../images/operation/")
                    .append(commandCss)
                    .append(".png) !important;")
                    .append("}");
        });
        FileUtils.createAndWriteFile("/platform/css/operation.css", css.toString());
        LOG.info("operation css:"+css);
    }
    /**
     * 每一次启动的时候都要检查所有的模块是否已经注册
     * @return 
     */
    @Override
    protected boolean shouldRegister() {
        return true;
    }
    @Override
    public void registe() {
        data=new ArrayList<>();
        List<Module> modules=ModuleParser.getRootModules();
        modules.forEach(module -> {
            registeModule(module);
        });
    }
    private void registeModule(Module module){
        Page<Module> page=serviceFacade.query(Module.class);
        if(page.getTotalRecords()==0){
            LOG.info("第一次注册第一个模块: "+module.getChinese());            
            serviceFacade.create(module);
            registed=true;
            //保存根模块
            rootModule=module;
            data.add(module);
        }else{
            LOG.info("以前已经注册过模块");
            LOG.info("查找出根模块");
            Module root=null;
            Module existRoot=moduleService.getRootModule();
            if(existRoot!=null){
                root=existRoot;
                LOG.info("找到以前导入的根模块: "+root.getChinese());
            }else{
                LOG.info("没有找到以前导入的根模块");
                if(rootModule!=null){
                    LOG.info("使用本次导入的根模块");
                    root=rootModule;
                }
            }
            if(root!=null){
                Module parentModule=root;
                LOG.info("将第一次以后的模块的根模块设置为第一次注册的根模块");
                module.getSubModules().forEach(subModule -> {
                    if(hasRegisteModule(subModule)){
                        LOG.info("模块 "+subModule.getChinese()+" 在此前已经被注册过，此次忽略，检查其子模块");                        
                        registeSubModule(subModule);
                        return;
                    }
                    //确保后续注册的模块的顺序在最后
                    subModule.setOrderNum(subModule.getOrderNum()+(int)page.getTotalRecords());
                    subModule.setParentModule(parentModule);
                    LOG.info("注册后续模块: "+subModule.getChinese());
                    serviceFacade.create(subModule);
                    registed=true;
                    data.add(subModule);
                });
            }else{
                LOG.info("没有找到根模块，注册失败！");
            }
        }
    }
    private void registeSubModule(Module module){        
        LOG.info("模块 "+module.getChinese()+" 在此前已经被注册过，检查其命令");
        chechCommand(module);
        module.getSubModules().forEach(sub -> {
            if(hasRegisteModule(sub)){
                LOG.info("模块 "+sub.getChinese()+" 在此前已经被注册过，此次忽略，检查其子模块");
                registeSubModule(sub);
            }else{
                LOG.info("注册后续模块: "+sub.getChinese());
                //重新从数据库中查询出模块，参数中的模块是从XML中解析出来的
                sub.setParentModule(moduleService.getModule(module.getEnglish()));
                serviceFacade.create(sub);
                registed=true;
                data.add(sub);
            }
        });
    }
    
    private void chechCommand(Module module){
        Set<String> existCommands=new HashSet<>();
        Module existsModule=moduleService.getModule(module.getEnglish());
        if(existsModule!=null){
            existsModule.getCommands().forEach(command -> {
                existCommands.add(command.getEnglish());
            });
            module.getCommands().forEach(command -> {
                if(!existCommands.contains(command.getEnglish())){
                    command.setModule(existsModule);
                    serviceFacade.create(command);
                    LOG.info("注册新增命令: "+command.getChinese()+" ,模块："+existsModule.getChinese());
                }
            });
        }
    }
    private boolean hasRegisteModule(Module module){
        Module existsModule=moduleService.getModule(module.getEnglish());
        if(existsModule==null){
            return false;
        }
        return true;
    }
    @Override
    public List<Module> getRegisteData() {
        return data;
    }
}