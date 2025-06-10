package cn.gybyt.config;

import cn.gybyt.config.properties.JwtProperties;
import cn.gybyt.filter.GybytAuthFilter;
import cn.gybyt.service.IGybytUserManageService;
import cn.gybyt.util.BaseException;
import cn.gybyt.util.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

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
public class AuthServletConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private IGybytUserManageService gybytUserManageService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 允许跨域请求和禁用CSRF
        http.cors().and().csrf().disable();
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry = http.authorizeRequests();
        // 放行白名单请求
        jwtProperties.getWhiteList().forEach(path -> urlRegistry.antMatchers(path).permitAll());
        // 其他所有请求添加拦截
        urlRegistry.anyRequest().authenticated();
        // 关闭form登录
        http.formLogin().disable();
        // 关闭session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 添加自定义鉴权过滤器
        http.addFilterBefore(new GybytAuthFilter(jwtProperties, gybytUserManageService), UsernamePasswordAuthenticationFilter.class);
        // 处理异常
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            BaseException authError = (BaseException) request.getAttribute("authError");
            BaseResponse<String> baseResponse = new BaseResponse<>();
            baseResponse.setCode(401);
            response.setStatus(401);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            if (authError == null) {
                baseResponse.setMsg("用户未登录或登录已过期");
            } else {
                baseResponse.setMsg(authError.getMessage());
            }
            response.getWriter().write(new ObjectMapper().writeValueAsString(baseResponse));
        });
    }

}
