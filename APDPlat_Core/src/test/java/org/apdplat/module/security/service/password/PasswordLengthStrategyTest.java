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
public class PasswordLengthStrategyTest {
    @Test
    public void testCheck() throws Exception {
        PasswordStrategy strategy = new PasswordLengthStrategy();        
        try{
            strategy.check(null);
            fail("未指定用户密码，不应该符合密码长度策略");
        }catch(PasswordInvalidException e){
            Assert.assertEquals("异常应该包含信息：密码长度必须大于等于6", "密码长度必须大于等于6", e.getMessage());
        }
    }
    @Test
    public void testCheck1() throws Exception {
        PasswordStrategy strategy = new PasswordLengthStrategy();        
        try{
            strategy.check("");
            fail("未指定用户密码，不应该符合密码长度策略");
        }catch(PasswordInvalidException e){
            Assert.assertEquals("异常应该包含信息：密码长度必须大于等于6", "密码长度必须大于等于6", e.getMessage());
        }
    }
    @Test
    public void testCheck2() throws Exception {
        PasswordStrategy strategy = new PasswordLengthStrategy();        
        String password = "123";
        try{
            strategy.check(password);
            fail("123 不应该符合密码长度策略");
        }catch(PasswordInvalidException e){
            Assert.assertEquals("异常应该包含信息：密码长度必须大于等于6", "密码长度必须大于等于6", e.getMessage());
        }
    }
    @Test
    public void testCheck3() throws Exception {
        PasswordStrategy strategy = new PasswordLengthStrategy();        
        String password = "12345";
        try{
            strategy.check(password);
            fail("12345 不应该符合密码长度策略");
        }catch(PasswordInvalidException e){
            Assert.assertEquals("异常应该包含信息：密码长度必须大于等于6", "密码长度必须大于等于6", e.getMessage());
        }
    }
    @Test
    public void testCheck4() throws Exception {
        PasswordStrategy strategy = new PasswordLengthStrategy();        
        String password = "123456";
        try{
            strategy.check(password);
        }catch(PasswordInvalidException e){
            fail("123456 应该符合密码长度策略");
        }
    }
    @Test
    public void testCheck5() throws Exception {
        PasswordStrategy strategy = new PasswordLengthStrategy();        
        String password = "1234567";
        try{
            strategy.check(password);
        }catch(PasswordInvalidException e){
            fail("1234567 应该符合密码长度策略");
        }
    }
    @Test
    public void testCheck6() throws Exception {
        PasswordStrategy strategy = new PasswordLengthStrategy();        
        String password = "abc123";
        try{
            strategy.check(password);
        }catch(PasswordInvalidException e){
            fail("abc123 应该符合密码长度策略");
        }
    }
    @Test
    public void testCheck7() throws Exception {
        PasswordStrategy strategy = new PasswordLengthStrategy();        
        String password = "111111";
        try{
            strategy.check(password);
        }catch(PasswordInvalidException e){
            fail("111111 应该符合密码长度策略");
        }
    }
    @Test
    public void testCheck8() throws Exception {
        PasswordStrategy strategy = new PasswordLengthStrategy();        
        String password = "aaaaaa";
        try{
            strategy.check(password);
        }catch(PasswordInvalidException e){
            fail("aaaaaa 应该符合密码长度策略");
        }
    }    
}