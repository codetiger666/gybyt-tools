package cn.gybyt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 切换数据源
 *
 * @program: ApiClumps
 * @classname: SwitchDataSource
 * @author: codetiger
 * @create: 2023/9/2 15:48
 **/
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SwitchDataSource {

    /**
     * 默认切换的数据源KEY
     */
    String DEFAULT_NAME = "master";

    /**
     * 需要切换到数据的KEY
     */
    String value() default DEFAULT_NAME;

}
