package cn.gybyt.annotation;

import cn.gybyt.advice.LogAdvice;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启错误日志处理
 *
 * @program: gybyt-tools
 * @classname: EnableHandleAdvice
 * @author: codetiger
 * @create: 2023/5/28 20:09
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({LogAdvice.class})
public @interface EnableHandleAdvice {
}
