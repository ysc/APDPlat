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

package org.apdplat.module.security.service.password;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author 杨尚川
 */
public class PasswordComplexityStrategyTest {
    @Test
    public void testCheck() throws Exception {
        PasswordStrategy strategy = new PasswordComplexityStrategy();
        try{
            strategy.check(null);
            fail("未指定用户密码，不应该符合密码长度策略");
        }catch(PasswordInvalidException e){
            Assert.assertEquals("异常应该包含信息：密码不能为空", "密码不能为空", e.getMessage());
        }
    }
    @Test
    public void testCheck1() throws Exception {
        PasswordStrategy strategy = new PasswordComplexityStrategy();
        try{
            strategy.check("");
            fail("未指定用户密码，不应该符合密码长度策略");
        }catch(PasswordInvalidException e){
            Assert.assertEquals("异常应该包含信息：密码不能为空", "密码不能为空", e.getMessage());
        }
    }
    @Test
    public void testCheck2() throws Exception {
        PasswordStrategy strategy = new PasswordComplexityStrategy();
        String password = "123";
        try{
            strategy.check(password);
            fail("密码 123，不应该符合 密码不能全是数字 策略");
        }catch(PasswordInvalidException e){
            Assert.assertEquals("异常应该包含信息：密码不能全是数字", "密码不能全是数字", e.getMessage());
        }
    }
    @Test
    public void testCheck3() throws Exception {
        PasswordStrategy strategy = new PasswordComplexityStrategy();        
        String password = "abc";
        try{
            strategy.check(password);
            fail("密码 abc，不应该符合 密码不能全是字符 策略");
        }catch(PasswordInvalidException e){
            Assert.assertEquals("异常应该包含信息：密码不能全是字符", "密码不能全是字符", e.getMessage());
        }
    }
    @Test
    public void testCheck4() throws Exception {
        PasswordStrategy strategy = new PasswordComplexityStrategy();        
        String password = "abc123";
        try{
            strategy.check(password);            
        }catch(PasswordInvalidException e){
            fail("密码 abc123 应该符合 密码复杂性 策略");
        }
    }
    @Test
    public void testCheck5() throws Exception {
        PasswordStrategy strategy = new PasswordComplexityStrategy();        
        String password = "a3";
        try{
            strategy.check(password);            
        }catch(PasswordInvalidException e){
            fail("密码 a3 应该符合 密码复杂性 策略");
        }        
    }
    @Test
    public void testCheck6() throws Exception {
        PasswordStrategy strategy = new PasswordComplexityStrategy();        
        String password = "杨尚川";
        try{
            strategy.check(password);
            fail("密码 杨尚川，不应该符合 密码不能全是字符 策略");
        }catch(PasswordInvalidException e){
            Assert.assertEquals("异常应该包含信息：密码不能全是字符", "密码不能全是字符", e.getMessage());
        }
    }
    @Test
    public void testCheck7() throws Exception {
        PasswordStrategy strategy = new PasswordComplexityStrategy();        
        String password = "?";
        try{
            strategy.check(password);
        }catch(PasswordInvalidException e){
            fail("密码 ? 应该符合 密码复杂性 策略");
        }            
    }
    @Test
    public void testCheck8() throws Exception {
        PasswordStrategy strategy = new PasswordComplexityStrategy();        
        String password = "?!#";
        try{
            strategy.check(password);            
        }catch(PasswordInvalidException e){
            fail("密码 ?!# 应该符合 密码复杂性 策略");
        }        
    }
    @Test
    public void testCheck9() throws Exception {
        PasswordStrategy strategy = new PasswordComplexityStrategy();        
        String password = "YSCadmin?123!#杨尚川";
        try{
            strategy.check(password);            
        }catch(PasswordInvalidException e){
            fail("密码 YSCadmin?123!#杨尚川 应该符合 密码复杂性 策略");
        }        
    }
    @Test
    public void testCheck10() throws Exception {
        PasswordStrategy strategy = new PasswordComplexityStrategy();        
        String password = "A3";
        try{
            strategy.check(password);            
        }catch(PasswordInvalidException e){
            fail("密码 A3 应该符合 密码复杂性 策略");
        }        
    }
    @Test
    public void testCheck11() throws Exception {
        PasswordStrategy strategy = new PasswordComplexityStrategy();        
        String password = "ab";
        try{
            strategy.check(password);
            fail("密码 ab，不应该符合 密码不能全是字符 策略");
        }catch(PasswordInvalidException e){
            Assert.assertEquals("异常应该包含信息：密码不能全是字符", "密码不能全是字符", e.getMessage());
        }
    }
}