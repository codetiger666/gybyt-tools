package cn.gybyt.advice;

import cn.gybyt.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常拦截
 *
 * @program: utils
 * @classname: ControllerExceptionAdvice
 * @author: codetiger
 * @create: 2022/7/20 19:29
 **/
@RestControllerAdvice
public class GybytControllerExceptionAdvice {

    private final Logger log = LoggerFactory.getLogger(GybytControllerExceptionAdvice.class);

    /**
     * 处理web全局异常
     *
     * @param e 异常信息
     * @return 自定义返回信息
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> methodArgumentNotValidExceptionHandler(Exception e) {
        // 设置响应为JSON格式
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        LoggerUtil.handleException(log, e);
        log.error(e.getMessage(), e);
        // 返回异常信息
        return new ResponseEntity<>(BaseResponse.failure(HttpStatusEnum.SERVERERROR.value(), e.getMessage()), headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理自定义异常
     *
     * @param e 异常信息
     * @return 自定义返回信息
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> apiExceptionHandler(BaseException e) {
        // 设置响应为JSON格式
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        LoggerUtil.handleException(log, e);
        // 打印错误信息
        log.error(e.getMsg(), e);
        // 转换状态码为HttpStatus对象
        HttpStatus httpStatus = HttpStatus.resolve(e.getHttpStatus());
        // 返回自定义状态码返回体(默认400错误)
        if (BaseUtil.isNull(httpStatus)) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(BaseResponse.failure(e.getCode(), e.getMsg()), headers, httpStatus);
    }
}
