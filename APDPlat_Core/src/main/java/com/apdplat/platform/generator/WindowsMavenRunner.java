package com.apdplat.platform.generator;

import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.util.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author ysc
 */
public class WindowsMavenRunner implements MavenRunner{
    protected static final APDPlatLogger log = new APDPlatLogger(WindowsMavenRunner.class);

    @Override
    public void run(String workspaceModuleRootPath) {
        log.info("workspaceModuleRootPath:"+workspaceModuleRootPath);
        StringBuilder cmd=new StringBuilder();
        cmd.append("cd ")
           .append(workspaceModuleRootPath.substring(1).replace("/", "\\"))
           .append("\n")
           .append(workspaceModuleRootPath.substring(1, 3))
           .append("\n")
           .append("mvn clean install");
        File file=FileUtils.createAndWriteFile("target/install.bat", cmd.toString());
        log.info("命令：");
        log.info(cmd.toString());
        log.info("命令文件："+file.getAbsolutePath());
        log.info("开始执行命令：");
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
            log.info("编译刚才生成的模型文件成功");
        } catch (IOException ex) {
            log.info("命令执行失败: "+cmd.toString());
        }
    }
    
}
