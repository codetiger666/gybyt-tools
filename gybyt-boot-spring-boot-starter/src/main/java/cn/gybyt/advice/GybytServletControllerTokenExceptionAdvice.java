package cn.gybyt.advice;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.gybyt.util.BaseResponse;
import cn.gybyt.util.HttpStatusEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * token错误处理
 *
 * @author: sdnnie
 **/
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GybytServletControllerTokenExceptionAdvice {

    /**
     * 权限码异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public ResponseEntity<Object> handleNotPermissionException(NotPermissionException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限码校验失败'{}'", requestURI, e.getMessage());
        return new ResponseEntity<>(BaseResponse.failure(HttpStatus.FORBIDDEN.value(), "没有访问权限，请联系管理员授权"),
                                    HttpStatus.FORBIDDEN);
    }

    /**
     * 角色权限异常
     */
    @ExceptionHandler(NotRoleException.class)
    public ResponseEntity<Object> handleNotRoleException(NotRoleException e, HttpServletRequest request) {
        log.error("请求地址'{}',角色权限校验失败'{}'", request.getRequestURI(), e.getMessage());
        return new ResponseEntity<>(BaseResponse.failure(HttpStatus.FORBIDDEN.value(), "没有访问权限，请联系管理员授权"),
                                    HttpStatus.FORBIDDEN);
    }

    /**
     * 处理token全局异常
     *
     * @param e 异常信息
     * @return 自定义返回信息
     */
    @ExceptionHandler({NotLoginException.class})
    public ResponseEntity<Object> methodArgumentNotValidExceptionHandler(NotLoginException e) {
        // 设置响应为JSON格式
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        String message = "";
        switch (e.getType()) {
            case NotLoginException.NOT_TOKEN:
                message = NotLoginException.NOT_TOKEN_MESSAGE;
                break;
            case NotLoginException.INVALID_TOKEN:
                message = NotLoginException.INVALID_TOKEN_MESSAGE;
                break;
            case NotLoginException.TOKEN_TIMEOUT:
                message = NotLoginException.TOKEN_TIMEOUT_MESSAGE;
                break;
            case NotLoginException.BE_REPLACED:
                message = NotLoginException.BE_REPLACED_MESSAGE;
                break;
            case NotLoginException.KICK_OUT:
                message = NotLoginException.KICK_OUT_MESSAGE;
                break;
            case NotLoginException.TOKEN_FREEZE:
                message = NotLoginException.TOKEN_FREEZE_MESSAGE;
                break;
            case NotLoginException.NO_PREFIX:
                message = NotLoginException.NO_PREFIX_MESSAGE;
                break;
        }
        // 返回异常信息
        return new ResponseEntity<>(BaseResponse.failure(HttpStatusEnum.UNAUTHORIZED.value(), message), headers,
                                    HttpStatus.UNAUTHORIZED);
    }

}
