package cn.gybyt.util;

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

    public static String getUsername() {
        HttpServletRequest request = SpringUtil.getServletRequest();
        if (request == null) {
            throw new BaseException("无法获取请求对象");
        }
        String username = (String) request.getAttribute("username");
        if (BaseUtil.isEmpty(username)) {
            throw new BaseException("获取用户名失败");
        }
        return username;
    }

}
