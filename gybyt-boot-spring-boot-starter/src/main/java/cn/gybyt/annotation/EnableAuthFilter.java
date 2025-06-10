package cn.gybyt.annotation;

import cn.gybyt.config.AuthServletConfig;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

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
@Import({AuthServletConfig.class})
@EnableWebSecurity
public @interface EnableAuthFilter {
}
