package com.apdplat.module.system.service.backup;

/**
 *
 * @author ysc
 */
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

@Service("MYSQL")
public class MySQLBackupService extends BackupService{
 
    @Override
    public boolean backupImpl() {
        try {
            String path=getPath()+DateTypeConverter.toFileName(new Date())+".bak";
            String command=PropertyHolder.getProperty("db.backup.command");
            command=command.replace("${db.username}", username);
            command=command.replace("${db.password}", password);

            Runtime runtime = Runtime.getRuntime();
            Process child = runtime.exec(command);
            InputStream in = child.getInputStream();

            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path), "utf8");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf8"));
            String line=reader.readLine();
            while (line != null) {
                writer.write(line+"\n");
                line=reader.readLine();
            }
            writer.flush();
            writer.close();
            reader.close();
            log.debug("备份到："+path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean restoreImpl(String date) {
        try {
            String path=getPath()+date+".bak";
            String command=PropertyHolder.getProperty("db.restore.command");
            command=command.replace("${db.username}", username);
            command=command.replace("${db.password}", password);
            
            Runtime runtime = Runtime.getRuntime();
            Process child = runtime.exec(command);
            OutputStreamWriter writer = new OutputStreamWriter(child.getOutputStream(), "utf8");
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf8"));
            String line=reader.readLine();
            while (line != null) {
                writer.write(line+"\n");
                line=reader.readLine();
            }
            writer.flush();
            writer.close();
            reader.close();
            log.debug("从 "+path+" 恢复");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
