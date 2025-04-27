package cn.gybyt.filter;

import cn.gybyt.config.properties.JwtProperties;
import cn.gybyt.util.BaseException;
import cn.gybyt.util.JwtUtil;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 鉴权拦截器
 *
 * @program: gybyt-tools
 * @classname: GybytAuthFilter
 * @author: codetiger
 * @create: 2024/3/19 18:57
 **/
public class GybytAuthFilter implements Filter {

    private final JwtProperties jwtProperties;

    public GybytAuthFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String path = httpServletRequest.getRequestURI();
        AntPathMatcher matcher = new AntPathMatcher();
        // 白名单放行
        if (jwtProperties.getWhiteList().stream().anyMatch(pattern -> matcher.match(pattern, path))) {
            chain.doFilter(request, response);
            return;
        }
        // 验证token
        String token = httpServletRequest.getHeader(jwtProperties.getHeader());
        if (token == null || !token.startsWith(jwtProperties.getTokenPrefix())) {
            request.setAttribute("authError", new BaseException(401, "未登录"));
            request.getRequestDispatcher("/error/authError").forward(request, response);
            return;
        }
        try {
            String username = JwtUtil.validateToken(token);
            request.setAttribute("username", username);
        } catch (Exception e) {
            request.setAttribute("authError", new BaseException(401, "未登录"));
            request.getRequestDispatcher("/error/authError").forward(request, response);
            return;
        }
        chain.doFilter(request, response);
    }

}
