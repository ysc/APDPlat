package com.apdplat.module.monitor.filter;

import com.apdplat.module.monitor.model.ProcessTime;
import com.apdplat.module.security.model.User;
import com.apdplat.module.security.service.OnlineUserService;
import com.apdplat.module.system.service.LogQueue;
import com.apdplat.module.system.service.PropertyHolder;
import com.apdplat.module.system.service.SystemListener;
import com.apdplat.platform.log.APDPlatLogger;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author ysc
 */
public class PerformanceFilter implements Filter {
    protected static final APDPlatLogger log = new APDPlatLogger(PerformanceFilter.class);
    private boolean enabled = false;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest req=(HttpServletRequest)request;
        
        long start=0;
        if (enabled && filter(req)) {            
		start=System.currentTimeMillis();
        }
        chain.doFilter(request, response);
        if (enabled && filter(req)) {
		long end=System.currentTimeMillis();
                User user=OnlineUserService.getUser(req.getSession().getId());
                ProcessTime logger=new ProcessTime();
                logger.setOwnerUser(user);
                logger.setUserIP(req.getRemoteAddr());
                try {
                    logger.setServerIP(InetAddress.getLocalHost().getHostAddress());
                } catch (UnknownHostException ex) {
                    log.error("保存日志出错(Error in saving log)",ex);
                }
                logger.setAppName(SystemListener.getContextPath());
                String resource=req.getRequestURI().replace(logger.getAppName(), "");
                logger.setResource(resource);
                logger.setStartTime(new Date(start));
                logger.setEndTime(new Date(end));
                logger.setProcessTime(end-start);
                LogQueue.addLog(logger);
        }
    }

    @Override
    public void init(FilterConfig fc) throws ServletException {
        log.info("初始化性能过滤器(Initialize the filter performance)");
        enabled = PropertyHolder.getBooleanProperty("monitor.performance");
        if(enabled){
            log.info("启用性能分析日志(Enable performance analyzing log)");
        }else{            
            log.info("禁用性能分析日志(Disable performance analyzing log)");
        }
    }

    @Override
    public void destroy() {
        log.info("销毁性能过滤器(Destroy the filter performance)");
    }

    private boolean filter(HttpServletRequest req) {
        String path=req.getRequestURI();
        if(path.contains("/log/")){
            log.info("路径包含/log/,不执行性能分析(/log/ in path, not execute performance analysis) "+path);
            return false;
        }
        if(path.contains("/monitor/")){
            log.info("路径包含/monitor/,不执行性能分析(/log/ in path, not execute performance analysis) "+path);
            return false;
        }
        return true;
    }
}
