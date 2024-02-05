package cn.gybyt.util;

/**
 * @program: utils
 * @classname: BaseResponse
 * @description: 公共返回体
 * @author: codetiger
 * @create: 2021/5/19 9:49
 **/
public class BaseResponse<T> {
    /**
     * 响应码
     */
    Integer code;
    /**
     * 提示信息
     */
    String msg;
    /**
     * 响应数据
     */
    T data;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public void setCode(Integer code) {
        this.code = code;
    }


    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    public BaseResponse(Integer code, String message, T data) {
        this.code = code;
        this.msg = message;
        this.data = data;
    }

    public BaseResponse(Integer code, String message) {
        this.code = code;
        this.msg = message;
    }

    public BaseResponse() {
    }

    public static <T> BaseResponse<T> success(T content) {
        return new BaseResponse<>(HttpStatusEnum.SUCCESS.value(), HttpStatusEnum.SUCCESS.reason(), content);
    }


    public static BaseResponse<String> failure(Integer code, String reason) {
        return new BaseResponse<>(code, reason);
    }


}
