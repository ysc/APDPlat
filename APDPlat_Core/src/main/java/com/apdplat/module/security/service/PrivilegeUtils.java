package com.apdplat.module.security.service;

/**
 *
 * @author ysc
 */
public class PrivilegeUtils {
    public static String process(String str){
        str=str.replace("A", "-a");
        str=str.replace("B", "-b");
        str=str.replace("C", "-c");
        str=str.replace("D", "-d");
        str=str.replace("E", "-e");
        str=str.replace("F", "-f");
        str=str.replace("G", "-g");
        str=str.replace("H", "-h");
        str=str.replace("I", "-i");
        str=str.replace("J", "-j");
        str=str.replace("K", "-k");
        str=str.replace("L", "-l");
        str=str.replace("M", "-m");
        str=str.replace("N", "-n");
        str=str.replace("O", "-o");
        str=str.replace("P", "-p");
        str=str.replace("Q", "-q");
        str=str.replace("R", "-r");
        str=str.replace("S", "-s");
        str=str.replace("T", "-t");
        str=str.replace("U", "-u");
        str=str.replace("V", "-v");
        str=str.replace("W", "-w");
        str=str.replace("X", "-x");
        str=str.replace("Y", "-y");
        str=str.replace("Z", "-z");
        return str;
    }
}
