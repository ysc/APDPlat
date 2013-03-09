package com.apdplat.module.security.service;

/**
 *在Solaris平台上生成机器码
 * @author ysc
 */
public class SolarisSequenceService    extends AbstractSequenceService{
    @Override
    public String getSequence() {
        return getSigarSequence("solaris");
    }

    public static void main(String[] args) {
        SolarisSequenceService s = new SolarisSequenceService();
        String seq = s.getSequence();
        System.out.println(seq);
    }
    
}
