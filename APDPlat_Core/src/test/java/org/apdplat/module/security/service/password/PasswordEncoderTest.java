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

import org.apdplat.module.security.model.User;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.security.authentication.dao.SaltSource;

/**
 *
 * @author 杨尚川
 */
public class PasswordEncoderTest {
    
    @Test
    public void testEncode() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin");
        
        String expResult = "13f3e796b7333df90192c2e9ec64f92982e56aeecffccb65cc502b777e0b7a25";
        
        SaltSource saltSource = new APDPlatSaltSource();
        PasswordEncoder passwordEncoder = new PasswordEncoder();
        passwordEncoder.setSaltSource(saltSource);
        
        String result = passwordEncoder.encode(user.getPassword(), user);
        
        assertEquals(expResult, result);
    }
}