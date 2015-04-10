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

package org.apdplat.module.module.service;

import org.apdplat.module.module.model.Command;
import org.apdplat.module.module.model.Module;
import org.apdplat.module.security.model.User;
import org.apdplat.module.security.service.PrivilegeUtils;
import org.apdplat.module.security.service.SpringSecurityService;
import org.apdplat.module.security.service.UserHolder;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.platform.action.ExtJSSimpleAction;
import org.apdplat.platform.criteria.Operator;
import org.apdplat.platform.criteria.PropertyCriteria;
import org.apdplat.platform.criteria.PropertyEditor;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.service.ServiceFacade;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.annotation.Resource;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.stereotype.Service;
/**
 * 模块服务
 * @author 杨尚川
 */
@Service
public class ModuleService {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(ModuleService.class);
    @Resource(name = "serviceFacade")
    private ServiceFacade serviceFacade;

    public Module getRootModule() {
        PropertyEditor propertyEditor = new PropertyEditor("english", Operator.eq, "root");

        PropertyCriteria propertyCriteria = new PropertyCriteria();
        propertyCriteria.addPropertyEditor(propertyEditor);

        List<Module> roots = serviceFacade.query(Module.class, null, propertyCriteria).getModels();
        if(roots!=null && roots.size()==1){
            return roots.get(0);
        }
        LOG.error("有多个根模块!");
        return null;
    }

    public Module getModule(String english) {
        PropertyEditor propertyEditor = new PropertyEditor("english", Operator.eq, english);

        PropertyCriteria propertyCriteria = new PropertyCriteria();
        propertyCriteria.addPropertyEditor(propertyEditor);

        List<Module> page = serviceFacade.query(Module.class, null, propertyCriteria).getModels();
        if (page.isEmpty()) {
            return null;
        }
        return page.get(0);
    }

    public Module getModule(int id) {
        PropertyEditor propertyEditor = new PropertyEditor("id", Operator.eq, Integer.toString(id));

        PropertyCriteria propertyCriteria = new PropertyCriteria();
        propertyCriteria.addPropertyEditor(propertyEditor);

        List<Module> page = serviceFacade.query(Module.class, null, propertyCriteria).getModels();
        if (page.isEmpty()) {
            LOG.error("没有找到ID等于" + id + "的模块");
            return null;
        }
        return page.get(0);
    }

    /**
     * 在建立角色的时候用于选择权限的功能菜单
     * @param module
     * @return 
     */
    public String toJsonForPrivilege(Module module) {
        ModuleFilter filter = new ModuleFilter() {

            @Override
            public void filter(List<Module> subModules) {
            }

            @Override
            public boolean script() {
                return false;
            }

            @Override
            public boolean recursion() {
                return true;
            }

            @Override
            public boolean command() {
                return true;
            }
        };
        return toJson(module, filter);
    }

    /**
     * 在模块维护中用于编辑的功能菜单
     * @param module
     * @return 
     */
    public String toJsonForEdit(Module module) {
        ModuleFilter filter = new ModuleFilter() {

            @Override
            public void filter(List<Module> subModules) {
            }

            @Override
            public boolean script() {
                return false;
            }

            @Override
            public boolean recursion() {
                return false;
            }

            @Override
            public boolean command() {
                return true;
            }
        };
        return toJson(module, filter);
    }
    public String toRootJsonForEdit(){
        Module m=getRootModule();
        if(m==null){
            LOG.error("获取根功能菜单失败！");
            return "";
        }
        StringBuilder json=new StringBuilder();
        json.append("[{'text':'").append(m.getChinese()).append("','id':'module-").append(m.getId()).append("','iconCls':'").append(m.getEnglish()).append("','leaf':false}]");
        
        return json.toString();
    }

    /**
     * 获取用户登录之后的左部功能菜单
     * 只有在启用安全机制的时候才对用户所属的模块进行控制
     * 需要去除隐藏的模块和命令
     * @param module
     * @return
     */
    public String toJsonForUser(Module module, final boolean recursion) {
        ModuleFilter filter = new ModuleFilter() {

            @Override
            public void filter(List<Module> subModules) {
                if (SpringSecurityService.isSecurity()) {
                    securityControl(subModules);
                }
                displayControl(subModules);
            }

            @Override
            public boolean script() {
                return true;
            }

            @Override
            public boolean recursion() {
                return recursion;
            }

            @Override
            public boolean command() {
                return false;
            }
        };
        return toJson(module, filter);
    }

    public String toJson(Module module, ModuleFilter filter) {
        StringBuilder json = new StringBuilder();
        List<Module> subModules = module.getSubModules();

        if (filter != null) {
            //是否对模块及命令进行移除交给模块控制器来做
            filter.filter(subModules);
        }

        if (subModules.size() > 0) {
            json.append("[");
            for (Module m : subModules) {
                json.append("{'text':'").append(m.getChinese()).append("','id':'module-").append(m.getId()).append("','iconCls':'").append(m.getEnglish()).append("'");
                if (filter.recursion()) {
                    if(m.getSubModules().size()>0 || (filter.command() && m.getCommands().size()>0)){
                        json.append(",children:").append(toJson(m, filter));
                    }
                }
                if (m.getSubModules().size() > 0) {
                    json.append(",'leaf':false");
                } else {
                    if (filter.command()) {
                        json.append(",'leaf':false");
                    } else {
                        json.append(",'leaf':true");
                    }
                    if (filter.script()) {
                        json.append(",listeners:{'click':function(node,event){openTab(node,event,'").append(m.getUrl()).append("')}}");
                    }
                }
                json.append("},");
            }
            json = json.deleteCharAt(json.length() - 1);
            json.append("]");
        } else {
            if (filter.command()) {
                List<Command> commands = module.getCommands();
                if (commands.size() > 0) {
                    json.append("[");
                    for (Command c : commands) {
                        json.append("{'text':'").append(c.getChinese()).append("','id':'command-").append(c.getId()).append("','iconCls':'").append(c.getEnglish()).append("','leaf':true").append("},");
                    }
                    json = json.deleteCharAt(json.length() - 1);
                    json.append("]");
                }
            }
        }

        return json.toString();
    }

    /**
     * 去除没有分配给用户的模块
     * @param modules
     * @param commands
     */
    public void securityControl(List<Module> modules) {
        User user = UserHolder.getCurrentLoginUser();
        //重新装载用户，消除由于不同会话间延迟加载的问题
        user = serviceFacade.retrieve(User.class, user.getId());
        List<Module> userModules = user.getModule();
        Iterator<Module> moduleIterator = modules.iterator();
        while (moduleIterator.hasNext()) {
            Module m = moduleIterator.next();
            //把没有分配给用户的模块去掉
            boolean contains = false;
            for (Module userModule : userModules) {
                if (m.getId() == userModule.getId()) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                moduleIterator.remove();
            }
        }
    }

    /**
     * 去除隐藏的模块
     * @param modules
     * @param commands
     */
    public void displayControl(List<Module> modules) {
        Iterator<Module> moduleIterator = modules.iterator();
        while (moduleIterator.hasNext()) {
            Module m = moduleIterator.next();
            //去掉隐藏的模块
            if (!m.isDisplay()) {
                moduleIterator.remove();
                continue;
            }
        }
    }
    /**
     * 获取命令访问路径到角色名称的映射
     * 规则：
     * 1、去掉开头的/**
     * 2、把/转换为_
     * 3、把!转换为_
     * 3、全部字母转换为大写
     * @param command
     * @return 
     */
    public static Map<String, String> getCommandPathToRole(Command command) {
        Map<String, String> result = new HashMap<>();
        //命令路径：/**/security/user!query
        //映射角色：_SECURITY_USER_QUERY
        for (String path : getCommandPath(command)) {
            String role = path.toString().substring(3).replace("/", "_").replace("!", "_").toUpperCase();
            result.put(path, role);
        }

        return result;
    }
    /**
     * 获取浏览器要访问一个命令的完全路径
     * @param command 命令
     * @return 因为命令的依赖关系，可能会返回多个路径
     */
    public static List<String> getCommandPath(Command command) {
        List<String> result = new ArrayList<>();
        //command.update=updatePart,updateWhole 表示把update权限分配给用户的时候，用户自动获得updatePart,updateWhole的权限
        //update只是一个逻辑的命令，用于统一指定一组命令，方便授权
        String dependency = PropertyHolder.getProperty("command." + command.getEnglish());
        String[] commands = null;
        if (dependency != null && !"".equals(dependency.trim())) {
            commands = dependency.split(",");
        } else {
            commands = new String[]{command.getEnglish()};
        }
        
        //APDPlat是支持任意多级的模块嵌套的，虽然目前的DEMO中只有两级
        //对于以下的路径来说：
        //http://apdplat.net/security/user!query.action
        //query是命令
        //user是模块
        //security也是模块
        
        //倒数第一级模块，对以上例子来说，即为user模块
        Module module = command.getModule();
        //获取除了倒数第一级模块之外的模块访问路径表示，对以上例子来说，即为security/
        String modulePath = getModulePath(module.getParentModule());
        //对模块名称进行处理，如：updatePart要转换为update-part，以符合struts2的规则，对以上例子来说，即为user
        String moduleName = PrivilegeUtils.process(module.getEnglish());
        for (String cmd : commands) {
            StringBuilder path = new StringBuilder();
            //模块访问路径+模块名称+"!"+命令 即为浏览器要访问一个命令的完全路径
            //如: /**/security/user!query
            path.append("/**/").append(modulePath).append(moduleName).append("!").append(cmd);
            result.add(path.toString());
        }
        return result;
    }
    /**
     * 获取该模块在浏览器中的访问路径表示
     * @param module
     * @return 
     */
    public static String getModulePath(Module module) {
        StringBuilder str = new StringBuilder();
        Stack<Module> stack = new Stack<>();
        getModules(module, stack);
        int len = stack.size();
        for (int i = 0; i < len; i++) {
            str.append(stack.pop().getEnglish()).append("/");
        }
        return str.toString();
    }
    /**
     * 用栈来表示模块的层级关系，从栈顶到栈底就像模块的依赖树一样
     * @param module
     * @param stack 
     */
    private static void getModules(Module module, Stack<Module> stack) {
        //将当前模块加入堆栈
        stack.push(module);
        Module parent = module.getParentModule();
        //当还有父模块并且父模块不为根模块的时候，把父模块也加入堆栈
        if (parent != null && !"root".equals(parent.getEnglish())) {
            getModules(parent, stack);
        }
    }

    /**
     * 比较module.xml中定义的命令和ExtJSSimpleAction中默认提供的命令
     * 返回在在module.xml中指定而在ExtJSSimpleAction中没有默认提供的命令
     * @param module
     * @return 
     */
    public static List<Command> getSpecialCommand(Module module) {
        List<Command> special = new ArrayList<>();
        List<String> commons = new ArrayList<>();
        for (Method method : ExtJSSimpleAction.class.getMethods()) {
            commons.add(method.getName());
            LOG.info("common method: " + method.getName());
        }
        for (Command command : module.getCommands()) {
            String dependency = PropertyHolder.getProperty("command." + command.getEnglish());
            if (dependency != null && !"".equals(dependency.trim())) {
                for (String c : dependency.split(",")) {
                    if (!commons.contains(c)) {
                        LOG.info("被依赖的方法不存在，通常情况下，被依赖的方法应该是通用方法，应该存在");
                        LOG.info("不存在通用方法: " + c);
                    }
                }
            } else {
                if (!commons.contains(command.getEnglish())) {
                    special.add(command);
                    LOG.info("special method:"+command.getEnglish());
                }
            }
        }
        return special;
    }
    
    /**
     * 将根模块转换为所有的模块的集合
     * @return 
     */
    public static List<Module> getAllModule(Module rootModule){
        List<Module> list=new ArrayList<>();
        list.add(rootModule);
        return getAllModule(list);
    }
    public static List<Module> getAllModule(List<Module> rootModules){
        List<Module> modules=new ArrayList<>();
        rootModules.forEach(module -> {
            moduleWalk(modules,module);
        });
        return modules;
    }
    private static void moduleWalk(List<Module> modules, Module module){
        modules.add(module);
        module.getSubModules().forEach(m -> {
            moduleWalk(modules,m);
        });
    }
    /**
     * 根据英文名称获取模型，从XML文件中获取模块信息，不从数据库中获取，英文此方法主要供代码辅助生成使用
     * @param moduleEnglishName
     * @return 
     */
    public static Module getModuleFromXml(String moduleEnglishName){
        for(Module module : getAllModule(ModuleParser.getRootModules())){
            if(moduleEnglishName.equals(module.getEnglish())){
                return module;
            }
        }
        return null;
    }
    /**
     * 获取叶子模块，每一个叶子模块都对应一个Action
     * @return 
     */
    public static List<Module> getLeafModule(Module rootModule){
        List<Module> leaf = new ArrayList<>();
        getAllModule(rootModule).forEach(module -> {
            if(module.getCommands().isEmpty()){
                LOG.info(module.getChinese()+" 模块不是叶子模块");
            }else{
                leaf.add(module);
            }
        });
        return leaf;
    }
}