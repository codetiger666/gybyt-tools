package cn.gybyt.satoken;

import cn.dev33.satoken.stp.StpInterface;
import cn.gybyt.dto.AuthUser;
import cn.gybyt.util.AuthUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * satoken权限获取
 *
 * @author: sdnnie
 **/
public class GybytTokenPermission implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        AuthUser loginUser = AuthUtil.getLoginUser();
        return new ArrayList<>(loginUser.getRoleKeys());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        AuthUser loginUser = AuthUtil.getLoginUser();
        return new ArrayList<>(loginUser.getRoleKeys());
    }

}
