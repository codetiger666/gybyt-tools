package cn.gybyt.util;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.gybyt.dto.AuthUser;
import cn.hutool.core.util.ObjectUtil;

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

    public static AuthUser getLoginUser() {
        AuthUser authUser = (AuthUser) SaHolder.getStorage().get(LOGIN_USER_KEY);
        if (ObjectUtil.isNotNull(authUser)) {
            return authUser;
        }
        SaSession session = StpUtil.getTokenSession();
        if (ObjectUtil.isNull(session)) {
            return authUser;
        }
        authUser = (AuthUser) session.get(LOGIN_USER_KEY);
        SaHolder.getStorage().set(LOGIN_USER_KEY, authUser);
        return authUser;
    }

    public static void login(AuthUser loginUser) {
        SaStorage storage = SaHolder.getStorage();
        storage.set(LOGIN_USER_KEY, loginUser);
        storage.set(USER_KEY, loginUser.getUserId());
        SaLoginParameter model = new SaLoginParameter();
        model.setDeviceType(loginUser.getDeviceType());
        StpUtil.login(loginUser.getUserId(), model.setExtra(USER_KEY, loginUser.getUserId()));
        StpUtil.getTokenSession()
                .set(LOGIN_USER_KEY, loginUser);
    }

    public static String getLoginToken() {
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return tokenInfo.getTokenValue();
    }

}
