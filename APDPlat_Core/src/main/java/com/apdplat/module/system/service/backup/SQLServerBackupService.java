package com.apdplat.module.system.service.backup;

import com.apdplat.module.system.service.Lock;
import com.apdplat.module.system.service.PropertyHolder;
import com.apdplat.platform.action.converter.DateTypeConverter;
import com.apdplat.platform.search.IndexManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import javax.annotation.Resource;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.stereotype.Service;
/**
 * SQLServer备份恢复实现
 * @author ysc
 */
@Service("SQL_SERVER")
public class SQLServerBackupService extends BackupService{
    @Resource(name="dataSource")
    private BasicDataSource dataSource;
    @Resource(name="indexManager")
    private IndexManager indexManager;
    /**
     * SQLServer备份数据库实现
     * @return 
     */
    @Override
    public boolean backupImpl(){
        Connection con = null;
        PreparedStatement bps = null;
        try {
            con = dataSource.getConnection();
            String path=getPath()+DateTypeConverter.toFileName(new Date())+".bak";
            String bakSQL=PropertyHolder.getProperty("db.backup.sql");
            bps=con.prepareStatement(bakSQL);
            bps.setString(1,path);
            if(!bps.execute()){
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {
            log.error("备份出错",e);
            return false;
        }finally{
            if(bps!=null){
                try {
                    bps.close();
                } catch (SQLException e) {
                    log.error("备份出错",e);
                }
            }
            if(con!=null){
                try {
                    con.close();
                } catch (SQLException e) {
                    log.error("备份出错",e);
                }
            }
        }
    }
    /**
     * SQLServer恢复数据库实现
     * @return 
     */
    @Override
    public boolean restoreImpl(String date){
        Lock.setRestore(true);
        Connection con = null;
        PreparedStatement rps = null;
        try {
            con= DriverManager.getConnection(PropertyHolder.getProperty("db.restore.url"),username,password);
            String path=getPath()+date+".bak";
            String restoreSQL=PropertyHolder.getProperty("db.restore.sql");
            rps=con.prepareStatement(restoreSQL);
            rps.setString(1,path);
            dataSource.close();
        
            if(!rps.execute()){
                indexManager.rebuidAll();
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {
            log.error("恢复出错",e);
            return false;
        } finally{
            Lock.setRestore(false);
            if(rps!=null){
                try {
                    rps.close();
                } catch (SQLException e) {
                    log.error("恢复出错",e);
                }
            }
            if(con!=null){
                try {
                    con.close();
                } catch (SQLException e) {
                    log.error("恢复出错",e);
                }
            }
        }
    }
}
