package cn.gybyt.advice;

import cn.gybyt.util.BaseResponse;
import cn.gybyt.util.HttpStatusEnum;
import cn.gybyt.util.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * security异常处理
 *
 * @program: utils
 * @classname: GybytControllerSecurityExceptionAdvice
 * @author: codetiger
 * @create: 2022/11/21 20:01
 **/
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@ConditionalOnClass(SecurityExpressionHandler.class)
public class GybytControllerSecurityExceptionAdvice {

    @ExceptionHandler({AuthenticationException.class})
    public BaseResponse<Object> MethodArgumentNotValidExceptionHandler(BadCredentialsException e) {
        SpringUtil.getServletResponse().setStatus(HttpStatusEnum.UNAUTHORIZED.value());
        return new BaseResponse<>(HttpStatusEnum.UNAUTHORIZED.value() , e.getMessage());
    }
    @ExceptionHandler({AccessDeniedException.class})
    public BaseResponse<Object> MethodArgumentNotValidExceptionHandler(AccessDeniedException e) {
        SpringUtil.getServletResponse().setStatus(HttpStatusEnum.UNAUTHORIZED.value());
        return new BaseResponse<>(HttpStatusEnum.UNAUTHORIZED.value(), "用户无权访问");
    }

    @ExceptionHandler({LockedException.class})
    public BaseResponse<Object> LockedException(LockedException e) {
        SpringUtil.getServletResponse().setStatus(HttpStatusEnum.UNAUTHORIZED.value());
        return new BaseResponse<>(HttpStatusEnum.UNAUTHORIZED.value(), "账号被锁定");
    }

    @ExceptionHandler({CredentialsExpiredException.class})
    public BaseResponse<Object> CredentialsExpiredException(CredentialsExpiredException e) {
        SpringUtil.getServletResponse().setStatus(HttpStatusEnum.UNAUTHORIZED.value());
        return new BaseResponse<>(HttpStatusEnum.UNAUTHORIZED.value(), "密码过期");
    }

    @ExceptionHandler({AccountExpiredException.class})
    public BaseResponse<Object> AccountExpiredException(AccountExpiredException e) {
        SpringUtil.getServletResponse().setStatus(HttpStatusEnum.UNAUTHORIZED.value());
        return new BaseResponse<>(HttpStatusEnum.UNAUTHORIZED.value(), "账号过期");
    }

    @ExceptionHandler({DisabledException.class})
    public BaseResponse<Object> DisabledException(DisabledException e) {
        SpringUtil.getServletResponse().setStatus(HttpStatusEnum.UNAUTHORIZED.value());
        return new BaseResponse<>(HttpStatusEnum.UNAUTHORIZED.value(), "账号被禁用");
    }

    @ExceptionHandler({BadCredentialsException.class})
    public BaseResponse<Object> BadCredentialsException(BadCredentialsException e) {
        SpringUtil.getServletResponse().setStatus(HttpStatusEnum.UNAUTHORIZED.value());
        return new BaseResponse<>(HttpStatusEnum.UNAUTHORIZED.value(), "用户名或密码错误");
    }

    @ExceptionHandler({InternalAuthenticationServiceException.class})
    public BaseResponse<Object> InternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        SpringUtil.getServletResponse().setStatus(HttpStatusEnum.UNAUTHORIZED.value());
        return new BaseResponse<>(HttpStatusEnum.UNAUTHORIZED.value(), e.getMessage());
    }

}
