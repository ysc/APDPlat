package com.apdplat.platform.log;

import java.io.Serializable;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 *日志输出支持多国语言切换解决方案
 * Log output switching solutions support multiple languages
 * @author ysc
 */
public class APDPlatLogger implements Logger,Serializable{
    private static final long serialVersionUID = 1L;

    private static Locale configLanguage = null;
    private Logger log = null;
    
    public APDPlatLogger(Class clazz){
        log = LoggerFactory.getLogger(clazz);
    }
    public static void setConfigLanguage(Locale configLanguage){
        APDPlatLogger.configLanguage=configLanguage;
    }
    /**
     * 是否输出指定语言的日志
     * @param specifyLanguage 要输出的日志使用的语言
     * @return 是或否
     */
    private boolean shouldOutput(Locale specifyLanguage){
        if(configLanguage==null){
            return true;
        }
        return specifyLanguage.getLanguage().equals(configLanguage.getLanguage());
    }

    @Override
    public String getName() {
        return log.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        trace(msg, Locale.CHINA);
    }
    
    public void trace(String msg, Locale locale) {
        if(shouldOutput(locale)){
            log.trace(msg);
        }
    }

    @Override
    public void trace(String format, Object arg) {
        log.trace(format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        log.trace(format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object[] argArray) {
        log.trace(format, argArray);
    }

    @Override
    public void trace(String msg, Throwable t) {
        trace(msg, t, Locale.CHINA);
    }

    public void trace(String msg, Throwable t, Locale locale) {
        if(shouldOutput(locale)){
            log.trace(msg, t);
        }
    }
    
    @Override
    public boolean isTraceEnabled(Marker marker) {
        return log.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        trace(marker, msg, Locale.CHINA);
    }
    
    public void trace(Marker marker, String msg, Locale locale) {
        if(shouldOutput(locale)){
            log.trace(marker, msg);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        log.trace(marker, format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        log.trace(marker, format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object[] argArray) {
        log.trace(marker, format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        trace(marker, msg, t, Locale.CHINA);
    }

    public void trace(Marker marker, String msg, Throwable t, Locale locale) {
        if(shouldOutput(locale)){
            log.trace(marker, msg, t);
        }
    }
    
    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        debug(msg, Locale.CHINA);
    }
    
    public void debug(String msg, Locale locale) {
        if(shouldOutput(locale)){
            log.debug(msg);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        log.debug(format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        log.debug(format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object[] argArray) {
        log.debug(format, argArray);
    }

    @Override
    public void debug(String msg, Throwable t) {
        debug(msg, t, Locale.CHINA);
    }
    
    public void debug(String msg, Throwable t, Locale locale) {
        if(shouldOutput(locale)){
            log.debug(msg, t);
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return log.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        debug(marker, msg, Locale.CHINA);
    }
    
    public void debug(Marker marker, String msg, Locale locale) {
        if(shouldOutput(locale)){
            log.debug(marker, msg);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        log.debug(marker, format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        log.debug(marker, format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object[] argArray) {
        log.debug(marker, format, argArray);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        debug(marker, msg, t, Locale.CHINA);
    }
    
    public void debug(Marker marker, String msg, Throwable t, Locale locale) {
        if(shouldOutput(locale)){
            log.debug(marker, msg, t);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        info(msg, Locale.CHINA);
    }
    
    public void info(String msg, Locale locale) {
        if(shouldOutput(locale)){
            log.info(msg);
        }
    }

    @Override
    public void info(String format, Object arg) {
        log.info(format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        log.info(format, arg1, arg2);
    }

    @Override
    public void info(String format, Object[] argArray) {
        log.info(format, argArray);
    }

    @Override
    public void info(String msg, Throwable t) {
        info(msg, t, Locale.CHINA);
    }
    
    public void info(String msg, Throwable t, Locale locale) {
        if(shouldOutput(locale)){
            log.info(msg, t);
        }
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return log.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String msg) {
        info(marker, msg, Locale.CHINA);
    }
    
    public void info(Marker marker, String msg, Locale locale) {
        if(shouldOutput(locale)){
            log.info(marker, msg);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        log.info(marker, format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        log.info(marker, format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object[] argArray) {
        log.info(marker, format, argArray);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        info(marker, msg, t, Locale.CHINA);
    }
    
    public void info(Marker marker, String msg, Throwable t, Locale locale) {
        if(shouldOutput(locale)){
            log.info(marker, msg, t);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        warn(msg, Locale.CHINA);
    }
    
    public void warn(String msg, Locale locale) {
        if(shouldOutput(locale)){
            log.warn(msg);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        log.warn(format, arg);
    }

    @Override
    public void warn(String format, Object[] argArray) {
        log.warn(format, argArray);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        log.warn(format, arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
        warn(msg, t, Locale.CHINA);
    }
    
    public void warn(String msg, Throwable t, Locale locale) {
        if(shouldOutput(locale)){
            log.warn(msg, t);
        }
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return log.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        warn(marker, msg, Locale.CHINA);
    }
    
    public void warn(Marker marker, String msg, Locale locale) {
        if(shouldOutput(locale)){
            log.warn(marker, msg);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        log.warn(marker, format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        log.warn(marker, format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object[] argArray) {
        log.warn(marker, format, argArray);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        warn(marker, msg, t, Locale.CHINA);
    }
    
    public void warn(Marker marker, String msg, Throwable t, Locale locale) {
        if(shouldOutput(locale)){
            log.warn(marker, msg, t);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        error(msg, Locale.CHINA);
    }
    
    public void error(String msg, Locale locale) {
        if(shouldOutput(locale)){
            log.error(msg);
        }
    }

    @Override
    public void error(String format, Object arg) {
        log.error(format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        log.error(format, arg1, arg2);
    }

    @Override
    public void error(String format, Object[] argArray) {
        log.error(format, argArray);
    }

    @Override
    public void error(String msg, Throwable t) {
        error(msg, t, Locale.CHINA);
    }
    
    public void error(String msg, Throwable t, Locale locale) {
        if(shouldOutput(locale)){
            log.error(msg, t);
        }
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return log.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        error(marker, msg, Locale.CHINA);
    }
    
    public void error(Marker marker, String msg, Locale locale) {
        if(shouldOutput(locale)){
            log.error(marker, msg);
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        log.error(marker, format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        log.error(marker, format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object[] argArray) {
        log.error(marker, format, argArray);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        error(marker, msg, t, Locale.CHINA);
    }
    
    public void error(Marker marker, String msg, Throwable t, Locale locale) {
        if(shouldOutput(locale)){
            log.error(marker, msg, t);
        }
    }
}
