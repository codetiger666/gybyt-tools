package cn.gybyt.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 认证用户
 *
 * @program: gybyt-tools
 * @classname: AuthUser
 * @author: codetiger
 * @create: 2025/6/10 9:35
 **/
public class AuthUser extends User {

    public AuthUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }


}
