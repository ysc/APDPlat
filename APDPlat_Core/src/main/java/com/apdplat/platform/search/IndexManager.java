package com.apdplat.platform.search;

import com.apdplat.module.monitor.model.IndexLog;
import com.apdplat.module.monitor.model.IndexLogResult;
import com.apdplat.module.security.model.User;
import com.apdplat.module.security.service.UserHolder;
import com.apdplat.module.system.service.LogQueue;
import com.apdplat.module.system.service.PropertyHolder;
import com.apdplat.module.system.service.SystemListener;
import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.model.Model;
import com.apdplat.platform.util.ConvertUtils;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import javax.annotation.Resource;
import org.compass.core.CompassSession;
import org.compass.core.CompassTemplate;
import org.compass.gps.CompassGps;
import org.compass.gps.CompassGpsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 索引管理
 * @author 杨尚川
 *
 */
@Service
public class IndexManager {
    protected static final APDPlatLogger log = new APDPlatLogger(IndexManager.class);
    
    @Resource(name = "compassTemplate")
    private CompassTemplate compassTemplate;
    @Resource(name = "compassGps")
    private CompassGps compassGps;
    private static boolean buiding=false;
    private static final  boolean indexMonitor;
    static{
        indexMonitor=PropertyHolder.getBooleanProperty("monitor.index");        
        if(indexMonitor){
            log.info("启用重建索引日志(Enable rebuilding index log)");
        }else{
            log.info("禁用重建索引日志(Disable rebuilding index log)");
        }
    }

    public void rebuidAll(){
        if(buiding){
            log.info("已经有任务在重建索引，当前请求无效(Rebuilding index in using, invalid request)");
            return;
        }
        buiding=true;
        final IndexLog indexLog=new IndexLog();
        if(indexMonitor){
            User user=UserHolder.getCurrentLoginUser();
            indexLog.setOwnerUser(user);
            indexLog.setLoginIP(UserHolder.getCurrentUserLoginIp());
            try {
                indexLog.setServerIP(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                log.error("保存索引日志出错(Error in saving index log)",e);
            }
            indexLog.setAppName(SystemListener.getContextPath());
            indexLog.setStartTime(new Date());
        }
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    log.info("开始删除索引文件(Start to delete index file)");
                    
                    delDir(getIndexDir());
                    
                    log.info("删除索引文件结束(End to delete index file)");
                    
                    log.info("开始建立索引文件...(Starting to create index file)");
                    long beginTime = System.currentTimeMillis();
                    float max=(float)Runtime.getRuntime().maxMemory()/1000000;
                    float total=(float)Runtime.getRuntime().totalMemory()/1000000;
                    float free=(float)Runtime.getRuntime().freeMemory()/1000000;
                    String pre="执行之前剩余内存(Remain memory before execution):"+max+"-"+total+"+"+free+"="+(max-total+free);
                    
                    compassGps.index();
                    
                    long costTime = System.currentTimeMillis() - beginTime;
                    max=(float)Runtime.getRuntime().maxMemory()/1000000;
                    total=(float)Runtime.getRuntime().totalMemory()/1000000;
                    free=(float)Runtime.getRuntime().freeMemory()/1000000;
                    String post="执行之后剩余内存(Remain memory after execution):"+max+"-"+total+"+"+free+"="+(max-total+free);
                    log.info("索引文件建立完毕.(Finish to build index)");
                    log.info("花费了(this cost) " + ConvertUtils.getTimeDes(costTime));
                    log.info(pre);
                    log.info(post);
                    
                    if(indexMonitor){
                        indexLog.setOperatingResult(IndexLogResult.SUCCESS);
                    }
                }catch(CompassGpsException | IllegalStateException e){
                    log.error("建立索引出错(Error in building index)",e);
                    if(indexMonitor){
                        indexLog.setOperatingResult(IndexLogResult.FAIL);
                    }
                }
                if(indexMonitor){
                    indexLog.setEndTime(new Date());
                    indexLog.setProcessTime(indexLog.getEndTime().getTime()-indexLog.getStartTime().getTime());
                    LogQueue.addLog(indexLog);
                }
                buiding=false;
            }
        }).start();
    }
    private void delDir(File file){
        if(file.isFile()){
            file.delete();
        }else if(file.isDirectory()){
            File[] files=file.listFiles();
            if(files.length==0){
                file.delete();
            }else{
                for(File f : files){
                    delDir(f);
                }
            }
        }
    }
    public static File getIndexDir(){
        String userDir = System.getProperty("user.dir");
        String indexDir=PropertyHolder.getProperty("index.dictionary").replace("/", File.separator);
        File file=new File(userDir,indexDir);
        log.info("获取索引文件目录(Get index file list)："+file.getAbsolutePath());
        return file;
    }
    private CompassSession getCompassSession() {
        return compassTemplate.getCompass().openSession();

    }
    private void closeCompassSession(CompassSession session) {
        session.close();
    }
    @Transactional
    public void createIndex(Model model) {
        try{
            CompassSession session = getCompassSession();
            try {
                session.create(model);
            } catch (Exception e) {
                String info=e.getMessage();
                log.info("创建索引失败,原因是(Failed to create index because): " + info);

                if(info.indexOf("LockObtainFailedException")!=-1){
                    int index=info.indexOf("@");
                    String path=info.substring(index+1);
                    File file=new File(path);
                    file.delete();
                    fixIndex();
                    session.create(model);
                }
            }
            closeCompassSession(session);
        }catch(Exception e){
            log.info("创建索引失败,原因是(Failed to create index because): " + e.getMessage());
        }
    }
    @Transactional
    public void updateIndex(Class<? extends Model> type, Model model) {
        try{
            deleteIndex(type,model.getId());
            CompassSession session = getCompassSession();
            try {
                session.save(model);
            } catch (Exception e) {
                String info=e.getMessage();
                log.info("更新索引失败,原因是(Failed to update index because): " + info);
                if(info.indexOf("LockObtainFailedException")!=-1){
                    int index=info.indexOf("@");
                    String path=info.substring(index+1);
                    File file=new File(path);
                    file.delete();
                    fixIndex();
                    session.save(model);
                }
            }
            closeCompassSession(session);
        }catch(Exception e){
            log.info("更新索引失败,原因是(Failed to update index because): " + e.getMessage());
        }
    }
    @Transactional
    public void deleteIndex(Class<? extends Model> type, Object objectID) {
        try{
            CompassSession session = getCompassSession();
            try {
                session.delete(session.load(type, objectID));
            } catch (Exception e) {
                String info=e.getMessage();
                log.info("删除索引失败,原因是(Failed to delete index because): " + info);
                if(info.indexOf("LockObtainFailedException")!=-1){
                    int index=info.indexOf("@");
                    String path=info.substring(index+1);
                    File file=new File(path);
                    file.delete();
                    fixIndex();
                    session.delete(session.load(type, objectID));
                }
            }
            closeCompassSession(session);
        }catch(Exception e){
            log.info("删除索引失败,原因是(Failed to delete index because): " + e.getMessage());
        }
    }
    private void fixIndex(){
        log.info("开始修复索引(Begin repair index)");
        long beginTime = System.currentTimeMillis();
        File file=IndexManager.getIndexDir();
        clearWriteLock(file);
        long costTime = System.currentTimeMillis() - beginTime;
        log.info("花费了(This cost) " + costTime + " (ms)毫秒");
        log.info("结束修复索引(Finish repair index)");
    }
    private void clearWriteLock(File file){
        if(file.isFile()){
            if(file.getName().equals("write.lock")){
                file.delete();
            }
        }else if(file.isDirectory()){
            File[] files=file.listFiles();
            if(files.length>0){
                for(File f : files){
                    clearWriteLock(f);
                }
            }
        }
    }
}
