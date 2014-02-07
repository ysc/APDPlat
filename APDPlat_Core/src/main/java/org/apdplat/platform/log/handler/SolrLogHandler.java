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

package org.apdplat.platform.log.handler;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apdplat.module.log.model.OperateLog;
import org.apdplat.module.monitor.model.MemoryState;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.util.ConvertUtils;
import org.springframework.stereotype.Service;

/**
 * 
 * 日志处理插件:
 * 将日志保存到Solr中
 * 进行高性能实时搜索和分析
 * 支持大规模分布式搜索
 * 
 * @author 杨尚川
 */
@Service
public class SolrLogHandler implements LogHandler{
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(SolrLogHandler.class);
    
    private static final String host = PropertyHolder.getProperty("solr.host");
    private static final String port = PropertyHolder.getProperty("solr.port");
    private static final String core = PropertyHolder.getProperty("solr.core");
    private static final int maxRetries = PropertyHolder.getIntProperty("solr.max.retries");
    private static final int connectionTimeout = PropertyHolder.getIntProperty("solr.connection.timeout");
    private static final boolean allowCompression = PropertyHolder.getBooleanProperty("solr.allow.compression");
    private static final int socketReadTimeout = PropertyHolder.getIntProperty("solr.socket.read.timeout");
    private static final int maxConnectionsPerHost = PropertyHolder.getIntProperty("solr.max.connections.per.host");
    private static final int maxTotalConnections = PropertyHolder.getIntProperty("solr.max.total.connections");   
    private static SolrServer solrServer;
   
    public SolrLogHandler(){
        LOG.info("solr.host: "+host);
        LOG.info("solr.port: "+port);
        LOG.info("solr.core: "+core);
        LOG.info("solr.max.retries: "+maxRetries);
        LOG.info("solr.connection.timeout: "+connectionTimeout);
        LOG.info("solr.allow.compression: "+allowCompression);
        LOG.info("solr.socket.read.timeout: "+socketReadTimeout);
        LOG.info("solr.max.connections.per.host: "+maxConnectionsPerHost);
        LOG.info("solr.max.total.connections: "+maxTotalConnections);
        
        String url = "http://"+host+":"+port+"/solr/"+core+"/";
        LOG.info("初始化Solr服务器连接："+url);
        HttpSolrServer httpSolrServer = new HttpSolrServer(url);
        httpSolrServer.setMaxRetries(maxRetries);
        httpSolrServer.setConnectionTimeout(connectionTimeout);
        httpSolrServer.setAllowCompression(allowCompression);
        httpSolrServer.setSoTimeout(socketReadTimeout);
        httpSolrServer.setDefaultMaxConnectionsPerHost(maxConnectionsPerHost);
        httpSolrServer.setMaxTotalConnections(maxTotalConnections);
        
        solrServer = httpSolrServer;
    }
    
    @Override
    public <T extends Model> void handle(List<T> list) {
        LOG.info("开始将 "+list.size()+" 个日志对象索引到Solr服务器");
        long start = System.currentTimeMillis();
        index(list);
        long cost = System.currentTimeMillis() - start;
        LOG.info("耗时："+ConvertUtils.getTimeDes(cost));
    } 
    /**
     * 批量索引
     * 批量提交
     * 
     * @param <T> 泛型参数
     * @param list 批量模型
     */
    public <T extends Model> void index(List<T> list){
        List<SolrInputDocument> docs = getSolrInputDocuments(list);
        //批量提交索引
        try{
            LOG.info("开始批量提交索引文档");
            solrServer.add(docs);
            UpdateResponse updateResponse = solrServer.commit();     
            int status = updateResponse.getStatus();
            if(status==0){
                LOG.info("成功为Core: "+core+" 提交 "+docs.size()+" 个文档");
            }else{
                LOG.info("索引提交失败，status："+status);
            }
            LOG.info("ResponseHeader:\n"+updateResponse.getResponseHeader().toString());
            LOG.info("Response:\n"+updateResponse.getResponse().toString());
            //加速内存释放
            docs.clear();
        }catch(IOException | SolrServerException e){
            LOG.error("批量提交索引失败", e);
        }
    }
    /**
     * 把对象列表转换为SOLR文档列表
     * @param <T> 对象类型
     * @param list 对象列表
     * @return SOLR文档列表
     */
    public <T extends Model> List<SolrInputDocument> getSolrInputDocuments(List<T> list){        
        int j = 1;
        //构造批量索引请求
        List<SolrInputDocument> docs = new ArrayList<>(list.size());
        LOG.info("开始构造Solr文档");
        for(T model : list){
            try{
                String simpleName = model.getClass().getSimpleName();
                LOG.debug((j++)+"、simpleName: 【"+simpleName+"】");          
                SolrInputDocument doc = new SolrInputDocument();                
                Field[] fields = model.getClass().getDeclaredFields();
                int len = fields.length;
                for(int i = 0; i < len; i++){
                    Field field = fields[i];
                    String name = field.getName();
                    field.setAccessible(true);
                    Object value = field.get(model);
                    //小心空指针异常，LogHandler线程会悄无声息地推出！
                    if(value == null){
                        LOG.debug("忽略空字段："+name);
                        continue;
                    }
                    LOG.debug("name: "+name+"   value: "+value);
                    doc.addField(name, value);
                }
                //日志类型（类名称）
                doc.addField("type", simpleName);
                //增加主键
                UUID uuid = UUID.randomUUID();
                doc.addField("id", uuid.toString());
                docs.add(doc);
            }catch(IllegalAccessException | IllegalArgumentException | SecurityException e){
                LOG.error("构造索引请求失败【"+model.getMetaData()+"】\n"+model, e);
            }
        }
        LOG.info("Solr文档构造完毕");
        return docs;
    }
    
    public static void main(String[] args){
        List<Model> list = new LinkedList<>();
        
        MemoryState m = new MemoryState();
        m.setAppName("杨尚川");
        m.setFreeMemory(1000f);
        m.setMaxMemory(5000f);
        m.setRecordTime(new Date());
        m.setServerIP("127.0.0.1");
        m.setTotalMemory(10000f);
        m.setUsableMemory(8000f);
        
        list.add(m);
        
        OperateLog o = new OperateLog();
        o.setAppName("开发平台");
        o.setLoginIP("192.68.23.12");
        o.setOperatingID(20);
        o.setOperatingModel("用户信息");
        o.setOperatingTime(new Date());
        o.setOperatingType("删除");
        o.setServerIP("192.168.0.1");
        o.setUsername("杨尚川ysc");
        
        list.add(o);
        
        LogHandler h = new SolrLogHandler();
        h.handle(list);
    }
}
