package com.apdplat.module.security.ws;

/**
 *
 * @author ysc
 */
import com.apdplat.module.security.model.User;
import com.apdplat.module.security.service.PasswordEncoder;
import com.apdplat.module.security.service.UserDetailsServiceImpl;
import javax.annotation.Resource;
import javax.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@WebService(endpointInterface = "com.apdplat.module.security.ws.UserService")
public class UserServiceImpl implements UserService{
    protected static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Resource(name = "userDetailsServiceImpl")
    private UserDetailsServiceImpl userDetailsServiceImpl;
    
    @Override
    public String login(String username, String password) {
        try{
            User user=(User)userDetailsServiceImpl.loadUserByUsername(username);
            password=PasswordEncoder.encode(password, user);
            if(password.equals(user.getPassword())){
                return "认证成功";
            }else{
                return "密码不正确";
            }
        }catch(Exception e){
            return e.getMessage();
        }
    }

    @Override
    public User getUserInfo(String username, String password) {
        User user=(User)userDetailsServiceImpl.loadUserByUsername(username);
        password=PasswordEncoder.encode(password, user);
        if(password.equals(user.getPassword())){
            return user;
        }
        return null;
    }
    
}
