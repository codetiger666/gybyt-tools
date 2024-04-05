package cn.gybyt.config;

import cn.gybyt.config.properties.JwtProperties;
import cn.gybyt.util.BaseException;
import cn.gybyt.util.BaseResponse;
import cn.gybyt.util.BaseUtil;
import cn.gybyt.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.result.method.InvocableHandlerMethod;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

/**
 * @program: gybyt-tools
 * @classname: AuthFluxConfig
 * @author: codetiger
 * @create: 2024/3/20 19:45
 **/
@Configuration
@ConditionalOnClass(InvocableHandlerMethod.class)
@Import(JwtProperties.class)
public class AuthFluxConfig {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Bean
    public WebFilter gybytAuthFilter(JwtProperties jwtProperties) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            String path = request.getPath().toString();
            AntPathMatcher matcher = new AntPathMatcher();
            // 白名单放行
            if (jwtProperties.getWhiteList().stream().anyMatch(pattern -> matcher.match(pattern, path))) {
                return chain.filter(exchange);
            }
            String token = request
                    .getHeaders()
                    .getFirst(jwtProperties.getTokenPrefix());
            if (BaseUtil.isEmpty(token) || BaseUtil.isEmpty(JwtUtil.validateToken(token))) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                try {
                    return response.writeWith(Mono.just(response.bufferFactory().wrap(OBJECT_MAPPER.writeValueAsBytes(
                            BaseResponse.failure(401, "用户未登录")))));
                } catch (JsonProcessingException e) {
                    throw new BaseException("序列化失败");
                }
            }
            return chain.filter(exchange);
        };
    }

}
