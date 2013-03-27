/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package generator;


import com.apdplat.platform.generator.ActionGenerator;
import com.apdplat.platform.generator.MavenRunner;
import com.apdplat.platform.generator.WindowsMavenRunner;
import com.apdplat.platform.generator.WebGenerator;
import com.apdplat.platform.generator.ModelGenerator;
import com.apdplat.platform.generator.ModelGenerator.ModelInfo;
import java.util.List;



/**
 *
 * @author ysc
 */
public class InvoicingGenerator {
    /**
     * 根据类路径下的文件/generator/*.xls生成相应的模型
     * 编译模型之后再次生成相应的控制器
     * 生成的文件放置在moduleProjectName指定的项目下
     * @param args 
     */
    public static void main(String[] args){ 
        //待生成的模型位于哪一个模块？物理文件夹名称
        String moduleProjectName="Apdplat_Module_Invoicing";
        
        //运行此生成器之前确保*.xls已经建立完毕
        //并将*.xls拷贝到/generator/下
        //不会强行覆盖MODEL，如果待生成的文件存在则会忽略生成s
        //生成model
        List<ModelInfo> modelInfos=ModelGenerator.generate(moduleProjectName);
        
        MavenRunner mavenRunner = new WindowsMavenRunner();
        String workspaceModuleBasePath = ActionGenerator.class.getResource("/").getFile().replace("target/classes/", "")+ "../" + moduleProjectName;
        //在程序生成Action之前先运行mvn install
        mavenRunner.run(workspaceModuleBasePath);
        
        //不会强行覆盖ACTION，如果待生成的文件存在则会忽略生成s
        //生成action
        //如果在linux平台，则需要改变第三个参数
        ActionGenerator.generate(modelInfos,workspaceModuleBasePath,mavenRunner);
        
        //运行此生成器之前确保module.xml，和相关的model已经建立完毕
        WebGenerator.generate();
    }
}