package cn.gybyt.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.gybyt.config.properties.AuthProperty;
import cn.gybyt.satoken.GybytTokenDao;
import cn.gybyt.satoken.GybytTokenPermission;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 认证配置
 *
 * @author: codetiger
 **/
@ConditionalOnClass(SaTokenConfig.class)
@Slf4j
public class AuthConfig implements WebMvcConfigurer {

    @Resource
    private AuthProperty authProperty;

    @Bean
    @Primary
    public SaTokenConfig saTokenConfig() {
        SaTokenConfig config = new SaTokenConfig();
        config.setTokenName(authProperty.getHeader());
        config.setTimeout(authProperty.getExpireTime() * 60L);
        if (-1 != authProperty.getFreezeTime()) {
            config.setActiveTimeout(authProperty.getFreezeTime() * 60L);
        }
        config.setIsConcurrent(true);
        config.setIsShare(false);
        config.setTokenStyle("uuid");
        config.setIsReadCookie(false);
        config.setIsPrint(false);
        config.setIsLog(false);
        config.setTokenPrefix(authProperty.getPrefix());
        config.setJwtSecretKey(authProperty.getSecretKey());
        return config;
    }

    /**
     * 注册 Sa-Token 拦截器打开注解鉴权功能
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> SaRouter.match("/**")
                        .check(r -> StpUtil.checkLogin())))
                .addPathPatterns("/**")
                // 不进行拦截的路径
                .excludePathPatterns(authProperty.getWhiteList());
    }

    /**
     * 注册 [Sa-Token 全局过滤器]
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 指定 [拦截路由] 与 [放行路由]
                .addInclude("/**")
                .setExcludeList(authProperty.getWhiteList())
                // 认证函数: 每次请求执行
                .setAuth(obj -> {
                })
                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> {
                    log.error("sa-token全局异常，异常信息：{}", e.getMessage());
                    return SaResult.error(e.getMessage());
                })
                // 前置函数：在每次认证函数之前执行 （BeforeAuth不受 includeList 与 excludeList 的限制，所有请求都会进入）
                .setBeforeAuth(r -> {
                    SaHolder.getResponse()
                            // ---------- 设置跨域响应头 ----------
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", SaHolder.getRequest()
                                    .getHeader("origin"))
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "*")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600");
                    // 如果是预检请求，则立即返回到前端
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            .back();
                });
    }

    /**
     * 注册 [Sa-Token 自定义dao]
     * @return
     */
    @Bean
    @Primary
    public SaTokenDao getGybytTokenDao() {
        return new GybytTokenDao();
    }

    /**
     * 注册 [Sa-Token 自定义权限获取]
     * @return
     */
    @Bean
    @Primary
    public StpInterface getStpInterface() {
        return new GybytTokenPermission();
    }

    /**
     * 注册 [Sa-Token 自定义jwt逻辑]
     * @return
     */
    @Bean
    @Primary
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

}
