package com.apdplat.module.security.ws;

/**
 *
 * @author ysc
 */
import com.apdplat.module.security.model.User;
import javax.jws.WebService;

@WebService
public interface UserService {
    public String login(String username, String password);
    public User getUserInfo(String username, String password);
}
