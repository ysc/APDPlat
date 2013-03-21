package com.apdplat.module.security.ws;

/**
 *
 * @author ysc
 */
import com.apdplat.module.security.model.User;
import com.apdplat.module.security.service.PasswordEncoder;
import com.apdplat.module.security.service.UserDetailsServiceImpl;
import com.apdplat.platform.log.APDPlatLogger;
import javax.annotation.Resource;
import javax.jws.WebService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/**
 * 用户认证服务实现
 * @author ysc
 */
@Service
@WebService(endpointInterface = "com.apdplat.module.security.ws.UserService")
public class UserServiceImpl implements UserService{
    protected static final APDPlatLogger log = new APDPlatLogger(UserServiceImpl.class);
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
        }catch(UsernameNotFoundException | DataAccessException e){
            return e.getMessage();
        }
    }

    @Override
    public User getUserInfo(String username, String password) {
        try{
            User user=(User)userDetailsServiceImpl.loadUserByUsername(username);
            if(user!=null){
                password=PasswordEncoder.encode(password, user);
                if(password.equals(user.getPassword())){
                    return user;
                }
            }
        }catch(UsernameNotFoundException | DataAccessException e){
            log.info("没有获取到用户信息："+username);
        }
        return null;
    }
    
}
