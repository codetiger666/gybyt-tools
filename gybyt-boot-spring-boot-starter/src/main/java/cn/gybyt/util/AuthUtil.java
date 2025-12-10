package cn.gybyt.util;

import cn.gybyt.config.properties.JwtProperties;

import javax.servlet.http.HttpServletRequest;

/**
 * 验证工具类
 *
 * @program: gybyt-tools
 * @classname: AuthUtil
 * @author: codetiger
 * @create: 2024/4/21 8:32
 **/
public class AuthUtil {

    private static JwtProperties JWT_PROPERTIES;

    static {
        JWT_PROPERTIES = SpringUtil.getBean(JwtProperties.class);
    }

    public static String getUsername() {
        String token = SpringUtil.getRequestHeader(JWT_PROPERTIES.getHeader());
        if (BaseUtil.isEmpty(token)) {
            throw new BaseException("获取认证信息失败");
        }
        String username = JwtUtil.validateToken(token);
        if (BaseUtil.isEmpty(username)) {
            throw new BaseException("获取用户名失败");
        }
        return username;
    }

}
