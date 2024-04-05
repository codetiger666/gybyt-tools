package cn.gybyt.annotation;

import cn.gybyt.config.AuthFluxConfig;
import cn.gybyt.config.AuthServletConfig;
import cn.gybyt.controller.GybytAuthError;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启认证拦截
 *
 * @program: gybyt-tools
 * @classname: EnableAuthFilter
 * @author: codetiger
 * @create: 2023/5/28 20:09
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({AuthServletConfig.class, AuthFluxConfig.class, GybytAuthError.class})
public @interface EnableAuthFilter {
}
