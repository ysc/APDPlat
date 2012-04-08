package com.apdplat.module.security.service;

/**
 *生成机器码的接口，不同平台有不同实现
 * @author ysc
 */
public interface SequenceService {

    /**
     * 
     * @return  机器码
     */
    public String getSequence();
    
}
