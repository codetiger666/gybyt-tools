package cn.gybyt.config;

import cn.gybyt.config.properties.JwtProperties;
import cn.gybyt.filter.GybytAuthFilter;
import cn.gybyt.service.IGybytUserManageService;
import cn.gybyt.util.BaseException;
import cn.gybyt.util.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
@EnableWebSecurity
@ConditionalOnClass(ModelAndView.class)
public class AuthServletConfig {

    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private IGybytUserManageService gybytUserManageService;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 允许跨域请求和禁用CSRF
        return http.csrf(AbstractHttpConfigurer::disable)
                   .authorizeHttpRequests(auth -> {
                       // 放行白名单请求
                       jwtProperties.getWhiteList()
                                    .forEach(path -> auth.requestMatchers(path)
                                                         .permitAll());
                       auth.anyRequest()
                           .authenticated();
                   })
                   // 关闭form登录
                   .formLogin(AbstractHttpConfigurer::disable)
                   // 关闭session
                   .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                   // 添加自定义鉴权过滤器
                   .addFilterBefore(new GybytAuthFilter(jwtProperties, gybytUserManageService),
                                    UsernamePasswordAuthenticationFilter.class)
                   // 处理异常
                   .exceptionHandling(
                           exception -> exception.authenticationEntryPoint((request, response, authException) -> {

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

                               response.getWriter()
                                       .write(OBJECT_MAPPER.writeValueAsString(baseResponse));
                           }))
                   .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用 BCrypt 加密密码
        return new BCryptPasswordEncoder();
    }

}
