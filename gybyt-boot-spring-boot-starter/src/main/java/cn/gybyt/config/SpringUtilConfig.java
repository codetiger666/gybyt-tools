package cn.gybyt.config;

import cn.gybyt.util.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * springUtil配置类
 *
 * @program: utils
 * @classname: SpringUtilConfig
 * @author: codetiger
 * @create: 2023/4/13 19:13
 **/
@Configuration
@Import(SpringUtil.class)
@ConditionalOnWebApplication
public class SpringUtilConfig {

}
