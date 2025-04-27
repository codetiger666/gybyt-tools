package cn.gybyt.config;

import cn.gybyt.interceptor.GybytFeignRequestHeaderInterceptor;
import feign.Feign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * openfeign配置类
 * @program: utils
 * @classname: OpenfeignConfig
 * @author: codetiger
 * @create: 2023/3/5 17:59
 **/
@Configuration
@ConditionalOnClass(Feign.class)
@ConditionalOnProperty(prefix = "gybyt.cloud", name = "enable-feign-header-handle", havingValue = "true", matchIfMissing = true)
public class GybytOpenfeignConfig {

    @Bean
    public GybytFeignRequestHeaderInterceptor gybytFeignRequestHeaderInterceptor() {
        return new GybytFeignRequestHeaderInterceptor();
    }
}
