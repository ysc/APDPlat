package com.apdplat.module.module.service;

import com.apdplat.module.module.model.Module;
import java.util.List;

/**
 *
 * @author ysc
 */
public interface ModuleFilter {
    public void filter(List<Module> subModules);
    /**
     * 生成的JSON是否包含JS脚本，此脚本用于打开模块对应的页面
     * @return 
     */
    public boolean script();
     /**
     * 是否生成一颗完整的树形功能菜单
     * @return 
     */
    public boolean recursion();
    /**
     * 生成的功能菜单树中是否包含命令
     * @return 
     */
    public boolean command();
}
