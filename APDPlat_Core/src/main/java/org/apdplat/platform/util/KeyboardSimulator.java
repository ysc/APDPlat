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

package org.apdplat.platform.util;

/**
 * 
 * 传统图书馆借还书的时候是用扫描枪扫描用户的借书证来实现用户认证的
 * 现在的无线城市建设已经分发了市民卡
 * 需要将市民卡和图书馆的现有系统进行集成
 * JAVA程序调用JNI接口读取市民卡中的身份证号码，之后利用本类的功能将身份证号码模拟键盘输入，实现条形码扫描枪的功能
 * 之后图书馆用扫描枪扫描用户的借书卡和在读卡器上刷市民卡的效果是一样的
 * 
 * @author 杨尚川
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