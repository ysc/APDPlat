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

package com.apdplat.platform.util;

/**
 * 
 * 传统图书馆借还书的时候是用扫描枪扫描用户的借书证来实现用户认证的
 * 现在的无线城市建设已经分发了市民卡
 * 需要将市民卡和图书馆的现有系统进行集成
 * JAVA程序调用JNI接口读取市民卡中的身份证号码，之后利用本类的功能将身份证号码模拟键盘输入，实现条形码扫描枪的功能
 * 之后图书馆用扫描枪扫描用户的借书卡和在读卡器上刷市民卡的效果是一样的
 * 
 * @author ysc
 */
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class KeyboardSimulator {

    private static Robot robot = null;

    static {
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            throw new RuntimeException(ex);
        }
    }

    private KeyboardSimulator() {
        //不可实例化
    }
    
    public static void input(String str){
        if(str!=null){
            for(char c : str.toCharArray()){
                pressKey(c);
            }
            robot.keyPress(KeyEvent.VK_ENTER);
        }
    }
    /**
     * 模拟按下按键
     * @param keyvalue
     */
    private static void pressKey(int keyvalue) {
        robot.keyPress(keyvalue); // 按下按键
        robot.keyRelease(keyvalue); // 释放按键
    }

    public static void main(String[] args) throws IOException {
        input("533001198510124839");
    }
}