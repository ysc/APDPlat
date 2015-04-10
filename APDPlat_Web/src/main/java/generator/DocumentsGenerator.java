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
 *
 * @author 杨尚川
 */
public class DocumentsGenerator {
    /**
     * 根据类路径下的文件/generator/moduleProjectName/*.xls生成相应的模型
     * 编译模型之后再次生成相应的控制器
     * 生成的文件放置在moduleProjectName指定的项目下
     * @param args 
     */
    public static void main(String[] args){ 
        //待生成的模型位于哪一个模块？物理文件夹名称
        String moduleProjectName="APDPlat_Module_Documents";
        
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
        actions.add("latestMessages");
        actions.add("needReceiveDocuments");
        actions.add("needVerificationDocuments");
        actions.add("needRevisionDocuments");
        actions.add("needApprovalDocuments");
        actions.add("needDispatchDocuments");
        String modelName="TodoTask";
        
        List<String> actions1=new ArrayList<String>();
        actions1.add("publishedDocuments");
        actions1.add("verifiedDocuments");
        actions1.add("revisedDocuments");
        actions1.add("approvedDocuments");
        actions1.add("receivedDocuments");
        actions1.add("dispatchedDocuments");
        String modelName1="DoneTask";
        
        List<String> actions2=new ArrayList<String>();
        actions2.add("latestPreviewDocuments");
        actions2.add("latestDownloadDocuments");
        actions2.add("latestPrintDocuments");
        actions2.add("latestFavoriteDocuments");
        String modelName2="OperationHistory";
        
        List<String> actions3=new ArrayList<String>();
        actions3.add("personalFavoriteDirectory");
        actions3.add("personalDocumentsDirectory");
        actions3.add("publicDirectory");
        String modelName3="Directory";
        
        List<String> actions4=new ArrayList<String>();
        actions4.add("personalFavorite");
        actions4.add("personalDocuments");
        actions4.add("latestDocumentsDirectory");
        actions4.add("latestPublishOrg");
        actions4.add("olderDocumentsDirectory");
        actions4.add("olderPublishOrg");
        actions4.add("departmentDocuments");
        actions4.add("documentsSearch");
        String modelName4="Document";
        modelInfos.forEach(modelInfo -> {
            if(modelInfo.getModelEnglish().toUpperCase().equals(modelName.toUpperCase())){
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
            if(modelInfo.getModelEnglish().toUpperCase().equals(modelName1.toUpperCase())){
                String modelClzz=modelInfo.getModelPackage()+"."+modelInfo.getModelEnglish();
                try {
                    Class clazz = Class.forName(modelClzz);
                    Model model=(Model)clazz.newInstance();
                    if(model!=null){
                        Generator.setActionModelMap(actions1, model);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("请再重新运行命令一次");
                    return;
                }
            }
            if(modelInfo.getModelEnglish().toUpperCase().equals(modelName2.toUpperCase())){
                String modelClzz=modelInfo.getModelPackage()+"."+modelInfo.getModelEnglish();
                try {
                    Class clazz = Class.forName(modelClzz);
                    Model model=(Model)clazz.newInstance();
                    if(model!=null){
                        Generator.setActionModelMap(actions2, model);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("请再重新运行命令一次");
                    return;
                }
            }
            if(modelInfo.getModelEnglish().toUpperCase().equals(modelName3.toUpperCase())){
                String modelClzz=modelInfo.getModelPackage()+"."+modelInfo.getModelEnglish();
                try {
                    Class clazz = Class.forName(modelClzz);
                    Model model=(Model)clazz.newInstance();
                    if(model!=null){
                        Generator.setActionModelMap(actions3, model);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("请再重新运行命令一次");
                    return;
                }
            }
            if(modelInfo.getModelEnglish().toUpperCase().equals(modelName4.toUpperCase())){
                String modelClzz=modelInfo.getModelPackage()+"."+modelInfo.getModelEnglish();
                try {
                    Class clazz = Class.forName(modelClzz);
                    Model model=(Model)clazz.newInstance();
                    if(model!=null){
                        Generator.setActionModelMap(actions4, model);
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