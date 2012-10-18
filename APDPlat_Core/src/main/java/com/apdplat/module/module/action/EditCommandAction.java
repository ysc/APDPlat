package com.apdplat.module.module.action;

import com.apdplat.module.module.model.Command;
import com.apdplat.platform.action.ExtJSSimpleAction;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
/**
* 维护树形模块，对应于module.xml文件
 * 在module.xml中的数据未导入到数据库之前，可以通过修改module.xml文件的形式修改树形模块
 * 在module.xml中的数据导入到数据库之后，就只能在浏览器网页中对树形模块进行修改
 *
 * 修改命令
* @author 杨尚川
*/
@Controller
@Scope("prototype")
@Namespace("/module")
public class EditCommandAction extends ExtJSSimpleAction<Command> {

}
