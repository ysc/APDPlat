package com.apdplat.module.security.service;

/**
 *在Mac OS X平台上生成机器码
 * @author ysc
 */
public class MacSequenceService    extends AbstractSequenceService{
    @Override
    public String getSequence() {
        return getSigarSequence("mac");
    }
   
    public static void main(String[] args) {
        MacSequenceService s = new MacSequenceService();
        String seq = s.getSequence();
        System.out.println(seq);
    }
}
