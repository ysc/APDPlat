package com.apdplat.module.security.service;

/**
 *在Linux平台上生成机器码
 * @author ysc
 */
public class LinuxSequenceService  implements SequenceService{

    @Override
    public String getSequence() {
        return "linux-machine-code";
    }
    
}
