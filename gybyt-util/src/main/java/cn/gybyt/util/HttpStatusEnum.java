package cn.gybyt.util;

/**
 * HTTP状态码
 *
 * @program: utils
 * @classname: HttpStatusEnum
 * @description: http状态码枚举
 * @author: codetiger
 * @create: 2021/5/19 14:39
 **/
public enum HttpStatusEnum {
    SUCCESS(200, "请求成功"),
    BUSINESS_ERROR(400, "业务异常"),
    UNAUTHORIZED(401, "用户未登录或令牌失效"),
    FORBIDDEN(403, "用户无权访问"),
    NOTFOUND(404, "资源未找到"),
    SERVERERROR(500, "服务异常"),
    USERNOTEXIST(1001, "用户不存在"),
    USERNAMEPASSWORDNOTMATCH(1002, "用户名或密码错误"),
    USERLOGINSUCCESS(1003, "登录成功");


    private final int value;
    private final String reason;

    HttpStatusEnum(int value, String reason) {
        this.value = value;
        this.reason = reason;
    }

    public int value() {
        return this.value;
    }

    public String reason() {
        return this.reason;
    }
}
