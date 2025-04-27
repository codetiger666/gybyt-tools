package cn.gybyt.config;

import cn.gybyt.config.properties.JwtProperties;
import cn.gybyt.filter.GybytAuthFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.ModelAndView;

/**
 * 请求配置
 *
 * @program: utils
 * @classname: RequestConfig
 * @author: codetiger
 * @create: 2023/1/17 19:26
 **/
@Configuration
@ConditionalOnClass(ModelAndView.class)
public class AuthServletConfig {

    /**
     * 添加全局过滤器，设置优先级最高，保证token验证在最前
     * @return
     */
    @Bean
    public FilterRegistrationBean<GybytAuthFilter> gybytAuthFilterRegistration(JwtProperties jwtProperties) {
        FilterRegistrationBean<GybytAuthFilter> registrationBean = new FilterRegistrationBean<>();
        // 设置拦截器
        registrationBean.setFilter(new GybytAuthFilter(jwtProperties));
        // 设置拦截路径
        registrationBean.addUrlPatterns("/*");
        // 设置优先级为最高
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

}
