package cn.gybyt.util;

/**
 * 公共异常
 *
 * @program: utils
 * @classname: BaseException
 * @author: codetiger
 * @create: 2021/5/19 11:39
 **/

public class BaseException extends RuntimeException {

    /**
     * 状态码
     */
    private int code = HttpStatusEnum.BUSINESS_ERROR.value();
    /**
     * http状态码
     */
    private int httpStatus = HttpStatusEnum.BUSINESS_ERROR.value();
    /**
     * 错误信息
     */
    private String msg;

    /**
     * 默认构造方法
     */
    public BaseException() {
        super("业务异常");
        this.msg = "业务异常";
    }

    /**
     * 错误信息，默认400错误
     * @param msg
     */
    public BaseException(String  msg){
        super(msg);
        this.msg = msg;
    }
    /**
     * @param msg 错误信息
     * @param cause
     */
    public BaseException(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
    }

    /**
     * @param code 错误码
     * @param msg 错误信息
     *
     **/
    public BaseException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    /**
     * @param code 错误码
     * @param msg 错误信息
     * @param httpStatus http状态码
     *
     **/
    public BaseException(int code, String msg, int httpStatus) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.httpStatus = httpStatus;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

}
