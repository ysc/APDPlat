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

package generator;


import org.apdplat.platform.generator.ActionGenerator;
import org.apdplat.platform.generator.MavenRunner;
import org.apdplat.platform.generator.WindowsMavenRunner;
import org.apdplat.platform.generator.Generator;
import org.apdplat.platform.generator.WebGenerator;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.generator.ModelGenerator;
import org.apdplat.platform.generator.ModelGenerator.ModelInfo;
import java.util.ArrayList;
import java.util.List;



/**
 *此文件是用来生成《短信管理系统》的骨架代码，真实文件因客户机密原因没有保留
 * 可参考\generator\template\model_template.xls文件
 * @author 杨尚川
 */
public class SmsGenerator {
    /**
     * 根据类路径下的文件/generator/moduleProjectName/*.xls生成相应的模型
     * 编译模型之后再次生成相应的控制器
     * 生成的文件放置在moduleProjectName指定的项目下
     * @param args 
     */
    public static void main(String[] args){ 
        //待生成的模型位于哪一个模块？物理文件夹名称
        String moduleProjectName="APDPlat_Module_SMS";
        
        //运行此生成器之前确保*.xls已经建立完毕
        //并将*.xls拷贝到/generator/moduleProjectName/下
        //不会强行覆盖MODEL，如果待生成的文件存在则会忽略生成
        //生成model
        List<ModelInfo> modelInfos=ModelGenerator.generate(moduleProjectName);
        
        MavenRunner mavenRunner = new WindowsMavenRunner();
        String workspaceModuleBasePath = ActionGenerator.class.getResource("/").getFile().replace("target/classes/", "")+ "../" + moduleProjectName;
        //在程序生成Action之前先运行mvn install
        mavenRunner.run(workspaceModuleBasePath);
        
        //可选：多个Action对应一个Model
        List<String> actions=new ArrayList<String>();
        actions.add("canLendTip");
        actions.add("lendTip");
        actions.add("notice");
        actions.add("overdueTip");
        actions.add("returnTip");
        actions.add("willReturnTip");
        actions.add("cancelPre");
        actions.add("continueLend");
        actions.add("lendQuery");
        actions.add("preQuery");
        actions.add("lost");
        actions.add("pre");
        
        
        actions.add("cancelPreTip");
        actions.add("lostTip");
        actions.add("preTip");
        actions.add("continueLendTip");
        actions.add("indemnityBookTip");
        actions.add("paymentTip");
        actions.add("compensateTip");
        actions.add("penaltyTip");
        
        actions.add("smsSearch");
        String modelName="Sms";
        modelInfos.forEach(modelInfo -> {
            if(modelInfo.getModelEnglish().equalsIgnoreCase(modelName)){
                String modelClzz=modelInfo.getModelPackage()+"."+modelInfo.getModelEnglish();
                try {
                    Class clazz = Class.forName(modelClzz);
                    Model model=(Model)clazz.newInstance();
                    if(model!=null){
                        Generator.setActionModelMap(actions, model);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("请再重新运行命令一次");
                    return;
                }
            }
        });
        
        //不会强行覆盖ACTION，如果待生成的文件存在则会忽略生成s
        //生成action
        ActionGenerator.generate(modelInfos,workspaceModuleBasePath);
        
        //运行此生成器之前确保module.xml，和相关的model已经建立完毕
        WebGenerator.generate();
    }
}