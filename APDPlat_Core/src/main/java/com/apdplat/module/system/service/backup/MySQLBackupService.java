package com.apdplat.module.system.service.backup;

import com.apdplat.module.system.service.PropertyHolder;
import com.apdplat.platform.action.converter.DateTypeConverter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import org.springframework.stereotype.Service;

/**
 *MySQL备份恢复实现
 * @author ysc
 */
@Service("MYSQL")
public class MySQLBackupService extends BackupService{
 
    /**
     * MySQL备份数据库实现
     * @return 
     */
    @Override
    public boolean backupImpl() {
        try {
            String path=getPath()+DateTypeConverter.toFileName(new Date())+".bak";
            String command=PropertyHolder.getProperty("db.backup.command");
            command=command.replace("${db.username}", username);
            command=command.replace("${db.password}", password);
            command=command.replace("${module.short.name}", PropertyHolder.getProperty("module.short.name"));

            Runtime runtime = Runtime.getRuntime();
            Process child = runtime.exec(command);
            InputStream in = child.getInputStream();

            try(OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path), "utf8");BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf8"))){
                String line=reader.readLine();
                while (line != null) {
                    writer.write(line+"\n");
                    line=reader.readLine();
                }
                writer.flush();
            }
            log.debug("备份到："+path);
            return true;
        } catch (Exception e) {
            log.error("备份出错",e);
        }
        return false;
    }

    /**
     * MySQL恢复数据库实现
     * @return 
     */
    @Override
    public boolean restoreImpl(String date) {
        try {
            String path=getPath()+date+".bak";
            String command=PropertyHolder.getProperty("db.restore.command");
            command=command.replace("${db.username}", username);
            command=command.replace("${db.password}", password);
            command=command.replace("${module.short.name}", PropertyHolder.getProperty("module.short.name"));
            
            Runtime runtime = Runtime.getRuntime();
            Process child = runtime.exec(command);
            try(OutputStreamWriter writer = new OutputStreamWriter(child.getOutputStream(), "utf8");BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf8"))){
                String line=reader.readLine();
                while (line != null) {
                    writer.write(line+"\n");
                    line=reader.readLine();
                }
                writer.flush();
            }
            log.debug("从 "+path+" 恢复");
            return true;
        } catch (Exception e) {
            log.error("恢复出错",e);
        }
        return false;
    }
}
