package cn.gybyt.dto;

import lombok.Data;

import java.util.Set;

/**
 * 认证用户
 *
 * @author: codetiger
 **/
@Data
public class AuthUser {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 角色列表
     */
    private Set<String> roleKeys;

    /**
     * 角色ID列表
     */
    private Set<String> roleIds;

}
