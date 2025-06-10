package cn.gybyt.service;

import cn.gybyt.entity.AuthUser;

/**
 * 用户管理服务
 *
 * @program: gybyt-tools
 * @classname: IGybytUserManageService
 * @author: codetiger
 * @create: 2025/6/10 8:20
 **/
public interface IGybytUserManageService {

    /**
     * 加载用户信息
     * @param username
     * @return
     */
    AuthUser loadUserByUsername(String username);

}
