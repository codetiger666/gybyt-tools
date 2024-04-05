package cn.gybyt.config;

import cn.gybyt.filter.GybytRequestFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.DispatcherType;

/**
 * 请求配置
 *
 * @program: utils
 * @classname: RequestConfig
 * @author: codetiger
 * @create: 2023/1/17 19:26
 **/
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(ModelAndView.class)
@ConditionalOnProperty(prefix = "gybyt", name = "enable-request-warpper", havingValue = "true", matchIfMissing = true)
public class RequestServletConfig {

    /**
     * 添加全局过滤器，设置优先级最低，保证最终使用request对象为自定义对象
     * @return
     */
    @Bean
    @ConditionalOnClass(ModelAndView.class)
    public FilterRegistrationBean<GybytRequestFilter> gybytRequestFilterRegistration() {
        FilterRegistrationBean<GybytRequestFilter> registrationBean = new FilterRegistrationBean<>();
        // 只拦截正常请求
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        // 设置拦截器
        registrationBean.setFilter(new GybytRequestFilter());
        // 设置拦截路径
        registrationBean.addUrlPatterns("/*");
        // 设置优先级为最低
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registrationBean;
    }
}
