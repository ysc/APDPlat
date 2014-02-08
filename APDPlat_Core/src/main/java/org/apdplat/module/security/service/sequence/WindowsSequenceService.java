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

package org.apdplat.module.security.service.sequence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

/**
 *在Windows平台上生成机器码
 * @author 杨尚川
 */
public final class WindowsSequenceService extends AbstractSequenceService{    
    @Override
    public String getSequence() {        
        String cpuID=getCPUSerial();
        String hdID=getHDSerial("C");
        if(cpuID==null || hdID==null){
            return null;
        }
        String machineCode = getMD5(cpuID+hdID);
                
        return machineCode;
    }
    
    /**
     *
     * @param drive 硬盘驱动器分区 如C,D
     * @return 该分区的卷标
     */
    private String getHDSerial(String drive) {
        StringBuilder result = new StringBuilder();
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            try (FileWriter fw = new java.io.FileWriter(file)) {
                String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
                        + "Set colDrives = objFSO.Drives\n" + "Set objDrive = colDrives.item(\"" + drive + "\")\n"
                        + "Wscript.Echo objDrive.SerialNumber";
                fw.write(vbs);
            }
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    result.append(line);
                }
            }
            file.delete();
        } catch (Throwable e) {
            LOG.error("生成HDSerial失败", e);
        }
        if (result.length() < 1) {
            LOG.info("无磁盘ID被读取");
        }

        return result.toString();
    }

    /**
     * 获取CPU号,多CPU时,只取第一个
     * @return
     */
    private String getCPUSerial() {
        StringBuilder result = new StringBuilder();
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            try (FileWriter fw = new FileWriter(file)) {
                String vbs = "On Error Resume Next \r\n\r\n" + "strComputer = \".\"  \r\n"
                        + "Set objWMIService = GetObject(\"winmgmts:\" _ \r\n"
                        + "    & \"{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\root\\cimv2\") \r\n"
                        + "Set colItems = objWMIService.ExecQuery(\"Select * from Win32_Processor\")  \r\n "
                        + "For Each objItem in colItems\r\n " + "    Wscript.Echo objItem.ProcessorId  \r\n "
                        + "    exit for  ' do the first cpu only! \r\n" + "Next                    ";

                fw.write(vbs);
            }
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    result.append(line);
                }
            }
            file.delete();
        } catch (Throwable e) {
            LOG.error("生成CPUSerial失败", e);
        }
        if (result.length() < 1) {
            LOG.info("无CPU_ID被读取");
        }
        return result.toString();
    }
    
    public static void main(String[] args) {        
        SequenceService s = new WindowsSequenceService();
        String seq = s.getSequence();
        System.out.println(seq);
    }
}