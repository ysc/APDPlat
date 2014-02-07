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

package org.apdplat.platform.generator;

import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.util.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apdplat.platform.log.APDPlatLoggerFactory;

/**
 *
 * @author 杨尚川
 */
public class WindowsMavenRunner implements MavenRunner{
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(WindowsMavenRunner.class);

    @Override
    public void run(String workspaceModuleRootPath) {
        LOG.info("workspaceModuleRootPath:"+workspaceModuleRootPath);
        StringBuilder cmd=new StringBuilder();
        cmd.append("cd ")
           .append(workspaceModuleRootPath.substring(1).replace("/", "\\"))
           .append("\n")
           .append(workspaceModuleRootPath.substring(1, 3))
           .append("\n")
           .append("mvn clean install");
        File file=FileUtils.createAndWriteFile("target/install.bat", cmd.toString());
        LOG.info("命令：");
        LOG.info(cmd.toString());
        LOG.info("命令文件："+file.getAbsolutePath());
        LOG.info("开始执行命令：");
        Runtime runtime = Runtime.getRuntime();
        try {
            Process child = runtime.exec(file.getAbsolutePath());
            InputStream in = child.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf8"));
            String line=reader.readLine();
            while (line != null) {
                System.out.println(line);
                line=reader.readLine();
            }
            LOG.info("编译刚才生成的模型文件成功");
        } catch (IOException ex) {
            LOG.info("命令执行失败: "+cmd.toString());
        }
    }
    
}