package cn.gybyt.annotation;

import cn.gybyt.advice.GybytServletControllerExceptionAdvice;
import cn.gybyt.advice.GybytControllerSecurityExceptionAdvice;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启异常处理
 *
 * @program: gybyt-tools
 * @classname: Enable
 * @author: codetiger
 * @create: 2023/5/28 20:05
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({GybytServletControllerExceptionAdvice.class, GybytControllerSecurityExceptionAdvice.class})
public @interface EnableHandleException {
}
