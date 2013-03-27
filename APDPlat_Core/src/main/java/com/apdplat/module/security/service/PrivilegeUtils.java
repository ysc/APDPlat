/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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