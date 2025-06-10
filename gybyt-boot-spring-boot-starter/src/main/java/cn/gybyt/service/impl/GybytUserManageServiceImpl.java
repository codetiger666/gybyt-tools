package cn.gybyt.service.impl;

import cn.gybyt.entity.AuthUser;
import cn.gybyt.service.IGybytUserManageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * 用户管理服务
 *
 * @program: gybyt-tools
 * @classname: UserManageService
 * @author: codetiger
 * @create: 2025/6/9 16:25
 **/
@Service
@ConditionalOnMissingBean(IGybytUserManageService.class)
@ConditionalOnClass(UsernamePasswordAuthenticationToken.class)
public class GybytUserManageServiceImpl implements IGybytUserManageService {

    @Override
    public AuthUser loadUserByUsername(String username) {
        return new AuthUser(username, "", new ArrayList<>());
    }

}
