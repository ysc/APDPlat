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

package org.apdplat.module.system.service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;

/**
 * 目录监控服务
 * @author 杨尚川
 */
public class WatchDirectory {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(WatchDirectory.class);
    
    private static WatchService watchService;
    private static final Map<WatchKey, Path> directories = new HashMap<>();
    private static Thread thread = null;
    /**
     * 开始监控目录（启动一个新的线程）
     * @param dir 绝对路径
     */
    public static void startWatch(final String dir) {
        if(!PropertyHolder.getBooleanProperty("watch.directory.enable")){
            LOG.info("未启用目录监控服务");
            return;
        }
        LOG.info("启用目录监控服务");
        Path start = Paths.get(dir);
        try {
            watchService = FileSystems.getDefault().newWatchService();
            registerTree(start);
        } catch (IOException ex) {
            LOG.error("监控目录失败：" + start.toAbsolutePath(), ex);
            LOG.error("Failed to monitor directory：" + start.toAbsolutePath(), ex, Locale.ENGLISH);
            return;
        }
        LOG.info("开始监控目录：" + start.toAbsolutePath());
        LOG.info("Start to monitor directory：" + start.toAbsolutePath(), Locale.ENGLISH);
        thread = new Thread(new Runnable(){

            @Override
            public void run() {
                watch();
            }
            
        });
        thread.start();
    }
    /**
     * 停止目录监控
     */
    public static void stopWatch(){
        if(thread != null){
            thread.interrupt();
        }
    }
    private static void watch(){
        try {
            while (true) {
                final WatchKey key = watchService.take();
                if(key == null){
                    continue;
                }
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    final WatchEvent.Kind<?> kind = watchEvent.kind();
                    //忽略无效事件
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                    //path是相对路径（相对于监控目录）
                    final Path contextPath = watchEventPath.context();
                    LOG.debug("contextPath:"+contextPath);
                    //获取监控目录
                    final Path directoryPath = directories.get(key);
                    LOG.debug("directoryPath:"+directoryPath);
                    //得到绝对路径
                    final Path absolutePath = directoryPath.resolve(contextPath);
                    LOG.debug("absolutePath:"+absolutePath);
                    LOG.debug("kind:"+kind);
                    //判断事件类别
                    switch (kind.name()) {
                        case "ENTRY_CREATE":
                            if (Files.isDirectory(absolutePath, LinkOption.NOFOLLOW_LINKS)) {
                                LOG.info("新增目录：" + absolutePath);
                                LOG.info("Create directory：" + absolutePath, Locale.ENGLISH);
                                //为新增的目录及其所有子目录注册监控事件
                                registerTree(absolutePath);
                            }else{
                                LOG.info("新增文件：" + absolutePath);
                                LOG.info("Create file：" + absolutePath, Locale.ENGLISH);                                
                            }
                            break;
                        case "ENTRY_DELETE":
                            LOG.info("删除：" + absolutePath);
                            LOG.info("Delete：" + absolutePath, Locale.ENGLISH);
                            break;
                    }
                }
                boolean valid = key.reset();
                if (!valid) {
                    LOG.info("停止监控目录："+directories.get(key));
                    directories.remove(key);
                    if (directories.isEmpty()) {
                        LOG.error("退出监控");
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            LOG.error("监控目录出错", ex);
        } catch (InterruptedException ex) {
            LOG.info("监控目录线程退出");
        } finally{
            try {
                watchService.close();
                LOG.info("关闭监控目录服务");
            } catch (IOException ex) {
                LOG.error("关闭监控目录服务出错", ex);
            }
        }
    }
    /**
     * 为指定目录及其所有子目录注册监控事件
     * @param start 目录
     * @throws IOException 
     */
    private static void registerTree(Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                registerPath(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    /**
     * 为指定目录注册监控事件
     * @param path
     * @throws IOException 
     */
    private static void registerPath(Path path) throws IOException {
        LOG.debug("监控目录:" + path);
        WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE);
        directories.put(key, path);
    }
    public static void main(String[] args) {
        WatchDirectory.startWatch("D:\\Workspaces\\NetBeansProjects\\APDPlat\\APDPlat_Web\\target\\APDPlat_Web-2.5\\");
    }
}
