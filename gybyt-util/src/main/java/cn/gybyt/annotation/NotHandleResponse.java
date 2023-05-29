package cn.gybyt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 不处理响应
 * @program: utils
 * @classname: NotControllerResponseAdvice
 * @author: codetiger
 * @create: 2022/7/20 20:22
 **/

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotHandleResponse {
}
