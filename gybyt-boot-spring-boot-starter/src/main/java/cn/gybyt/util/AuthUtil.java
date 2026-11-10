package cn.gybyt.util;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaStorage;
import cn.gybyt.config.properties.AuthProperty;
import cn.gybyt.dto.AuthUser;

/**
 * 验证工具类
 *
 * @program: gybyt-tools
 * @classname: AuthUtil
 * @author: codetiger
 * @create: 2024/4/21 8:32
 **/
public class AuthUtil {

    public static final String LOGIN_USER_KEY = "loginUser";
    public static final String USER_KEY = "userId";
    public static final String CLIENT_KEY = "clientId";
    private static AuthProperty JWT_PROPERTIES;

    static {
        JWT_PROPERTIES = SpringUtil.getBean(AuthProperty.class);
    }

    public static AuthUser getLoginUser() {
        SaStorage storage = SaHolder.getStorage();
        return (AuthUser) storage.get(LOGIN_USER_KEY);
    }

}
