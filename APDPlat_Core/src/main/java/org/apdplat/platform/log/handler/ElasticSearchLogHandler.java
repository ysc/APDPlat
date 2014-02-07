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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apdplat.module.monitor.model.MemoryState;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.platform.action.converter.DateTypeConverter;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.util.ConvertUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * 
 * 日志处理实现:
 * 将日志保存到ElasticSearch中
 * 进行高性能实时搜索和分析
 * 支持大规模分布式搜索
 * 
 * The bulk API makes it possible to perform 
 * many index/delete operations in a single API call. 
 * This can greatly increase the indexing speed. 
 * The REST API endpoint is /_bulk, and it 
 * expects the following JSON structure:
 * action_and_meta_data\n
 * optional_source\n
 * action_and_meta_data\n
 * optional_source\n
 * ....
 * action_and_meta_data\n
 * optional_source\n
 * 
 * NOTE: the final line of data must end with a newline character \n.
 * 
 * The possible actions are index, create, delete and since version 0.90.1 also update. 
 * index and create expect a source on the next line, 
 * and have the same semantics as the op_type parameter to the standard index API 
 * (i.e. create will fail if a document with the same index and type exists already, 
 * whereas index will add or replace a document as necessary). 
 * delete does not expect a source on the following line, 
 * and has the same semantics as the standard delete API. 
 * update expects that the partial doc, upsert and script 
 * and its options are specified on the next line.
 * 
 * @author 杨尚川
 */
@Service
public class ElasticSearchLogHandler implements LogHandler{
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(ElasticSearchLogHandler.class);
    
    private static final String INDEX_NAME = PropertyHolder.getProperty("elasticsearch.log.index.name");
    private static final String HOST = PropertyHolder.getProperty("elasticsearch.host");
    private static final String PORT = PropertyHolder.getProperty("elasticsearch.port");
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static URL URL;
    
    private int success;
   
    public ElasticSearchLogHandler(){
        LOG.info("elasticsearch.log.index.name: "+INDEX_NAME);
        LOG.info("elasticsearch.host: "+HOST);
        LOG.info("elasticsearch.port: "+PORT);
        try {
            URL = new URL("http://"+HOST+":"+PORT+"/_bulk");
        } catch (MalformedURLException ex) {
            LOG.error("构造URL失败",ex);
        }
    }
    
    /**
     * 批量提交索引JSON文档
     * 
     * @param <T> 泛型参数
     * @param list 批量模型
     */
    public <T extends Model> void index(List<T> list){
        success = 0;
        String json = getJsonDocuments(list);
        try{
            LOG.debug("开始批量提交索引JSON文档");
            HttpURLConnection conn = (HttpURLConnection) URL.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"));    
            writer.write(json.toString());
            writer.flush();
            StringBuilder result = new StringBuilder();
            try (BufferedReader reader = new BufferedReader (new InputStreamReader (conn.getInputStream()))) {
                String line = reader.readLine();
                while(line != null){
                    result.append(line);
                    line = reader.readLine();
                }
            }
            String resultStr = result.toString();
            LOG.debug(resultStr);          
            //使用Jackson解析返回的JSON
            JsonNode node = MAPPER.readTree(resultStr);
            for(JsonNode item : node.get("items")){
                JsonNode createJsonNode = item.get("create");
                JsonNode okJsonNode = createJsonNode.get("ok");
                if(okJsonNode != null){
                    boolean r = okJsonNode.getBooleanValue();
                    if(r){
                        success++;
                    }
                }else{
                    JsonNode errorJsonNode = createJsonNode.get("error");
                    if(errorJsonNode != null){
                        String errorMessage = errorJsonNode.getTextValue();
                        LOG.error("索引失败："+errorMessage);
                    }
                }
            }
            LOG.debug("批量提交索引JSON文档完毕");
        }catch(IOException e){
            LOG.error("批量提交索引失败", e);
        }
    }
    /**
     * 将待索引的日志对象列表按照ElasticSearch的要求
     * 构造成合适的JSON文档
     * @param <T> 泛型参数
     * @param list 待索引的日志对象列表
     * @return 符合ElasticSearch要求的JSON文档
     */
    public <T extends Model> String getJsonDocuments(List<T> list){        
        StringBuilder json = new StringBuilder();
        int j = 1;
        LOG.debug("开始构造JSON文档");
        for(T model : list){
            try{
                String simpleName = model.getClass().getSimpleName();
                LOG.debug((j++)+"、simpleName: 【"+simpleName+"】");
                json.append("{\"index\":{\"_index\":\"")
                        .append(INDEX_NAME)
                        .append("\",\"_type\":\"")
                        .append(simpleName)
                        .append("\"}}")
                        .append("\n");
                json.append("{");
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
                    if(i>0){
                        json.append(",");
                    }
                    String valueClass=value.getClass().getSimpleName();
                    LOG.debug("name: "+name+"   type: "+valueClass);
                    if("Timestamp".equals(valueClass) || "Date".equals(valueClass)){
                        //提交给ES的日期时间值要为"2014-01-31T13:53:54"这样的形式
                        value=DateTypeConverter.toDefaultDateTime((Date)value).replace(" ", "T");
                    }
                    String prefix = "\"";
                    String suffix = "\"";
                    //提交给ES的数字和布尔值不要加双引号
                    if("Float".equals(valueClass)
                            || "Double".equals(valueClass) 
                            || "Long".equals(valueClass) 
                            || "Integer".equals(valueClass)
                            || "Short".equals(valueClass)
                            || "Boolean".equals(valueClass)){
                        prefix="";
                        suffix="";
                    }
                    json.append("\"")
                            .append(name)
                            .append("\":")
                            .append(prefix)
                            .append(value)
                            .append(suffix);
                }
                json.append("}\n");
            }catch(SecurityException | IllegalArgumentException | IllegalAccessException e){
                LOG.error("构造索引请求失败【"+model.getMetaData()+"】\n"+model, e);
            }
        }
        LOG.debug("JSON文档构造完毕：\n"+json.toString());
        return json.toString();
    }
    
    @Override
    public <T extends Model> void handle(List<T> list) {
        LOG.info("开始将 "+list.size()+" 个日志对象索引到ElasticSearch服务器");
        long start = System.currentTimeMillis();
        index(list);
        long cost = System.currentTimeMillis() - start;
        
        if(success != list.size()){
            LOG.info("索引失败： "+(list.size()-success)+" 个");            
        }
        if(success > 0){
            LOG.info("索引成功： "+success+" 个");
        }
        LOG.info("耗时："+ConvertUtils.getTimeDes(cost));
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
        
        m = new MemoryState();
        m.setAppName("APDPlat");
        m.setFreeMemory(2000f);
        m.setMaxMemory(6000f);
        m.setRecordTime(new Date());
        m.setServerIP("127.0.0.1");
        m.setTotalMemory(11000f);
        m.setUsableMemory(9000f);
        
        list.add(m);
        
        LogHandler h = new ElasticSearchLogHandler();
        h.handle(list);
        
        //可使用以下命令查看自动生成的结构：
        //curl  -XGET http://localhost:9200/_mapping?pretty=true
        //curl  -XGET http://localhost:9200/apdplat_for_log/_search?pretty=true&q=ysc
    }
}
