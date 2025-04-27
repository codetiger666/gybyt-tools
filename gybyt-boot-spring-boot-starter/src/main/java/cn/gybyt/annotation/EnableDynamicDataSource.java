package cn.gybyt.annotation;

import cn.gybyt.config.GybytDynamicDataSourceConfig;
import cn.gybyt.config.properties.GybytDynamicProperties;
import cn.gybyt.dynamic.GybytDataSourceAspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启动态数据源
 *
 * @program: ApiClumps
 * @classname: EnableDynamicDataSource
 * @author: codetiger
 * @create: 2023/9/2 16:05
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableAspectJAutoProxy
@Import({GybytDynamicDataSourceConfig.class, GybytDynamicProperties.class, GybytDataSourceAspect.class})
public @interface EnableDynamicDataSource {
}
