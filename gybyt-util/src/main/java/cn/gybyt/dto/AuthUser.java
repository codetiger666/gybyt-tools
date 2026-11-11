package cn.gybyt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * 认证用户
 *
 * @author: codetiger
 **/
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
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
     * 设备类型
     */
    private String deviceType;

    /**
     * 角色列表
     */
    private Set<String> roleKeys;

    /**
     * 角色ID列表
     */
    private Set<String> roleIds;

}
