package cn.gybyt.annotation;

import cn.gybyt.advice.ControllerFluxResponseAdvice;
import cn.gybyt.advice.ControllerServletResponseAdvice;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启返回处理
 *
 * @program: gybyt-tools
 * @classname: EnableHandleResponse
 * @author: codetiger
 * @create: 2023/5/28 20:20
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({ControllerServletResponseAdvice.class, ControllerFluxResponseAdvice.class})
public @interface EnableHandleResponse {
}
