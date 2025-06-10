package cn.gybyt.filter;

import cn.gybyt.config.properties.JwtProperties;
import cn.gybyt.entity.AuthUser;
import cn.gybyt.service.IGybytUserManageService;
import cn.gybyt.util.BaseException;
import cn.gybyt.util.BaseUtil;
import cn.gybyt.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 鉴权拦截器
 *
 * @program: gybyt-tools
 * @classname: GybytAuthFilter
 * @author: codetiger
 * @create: 2024/3/19 18:57
 **/
@Slf4j
public class GybytAuthFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final IGybytUserManageService gtybytUserManageService;

    public GybytAuthFilter(JwtProperties jwtProperties, IGybytUserManageService gtybytUserManageService) {
        this.jwtProperties = jwtProperties;
        this.gtybytUserManageService = gtybytUserManageService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String path = request.getServletPath();
        String token = request.getHeader(jwtProperties.getHeader());
        // 放行没有认证信息的请求到请求链
        if (BaseUtil.isEmpty(token)) {
            log.info("请求路径：{}，未携带认证信息，放行", path);
            chain.doFilter(request, response);
            return;
        }
        if (!token.startsWith(jwtProperties.getTokenPrefix())) {
            log.info("请求路径：{}，认证信息解析错误，放行", path);
            request.setAttribute("authError", new BaseException(401, "用户未登录"));
            chain.doFilter(request, response);
            return;
        }
        try {
            String username = JwtUtil.validateToken(token);
            // 设置认证信息到SecurityContextHolder
            AuthUser authUser = gtybytUserManageService.loadUserByUsername(username);
            request.setAttribute("user", authUser);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authUser,
                                                                                                         null,
                                                                                                         authUser.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
            log.info("请求路径：{}，请求用户：{}", path, username);
        } catch (Exception e) {
            request.setAttribute("authError", new BaseException(401, "用户信息验证失败"));
            chain.doFilter(request, response);
            log.info("请求路径：{}，认证信息解析失败，放行", path);
            return;
        }
        chain.doFilter(request, response);
    }

}
