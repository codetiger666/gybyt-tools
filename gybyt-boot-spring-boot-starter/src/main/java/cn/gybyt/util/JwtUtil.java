package cn.gybyt.util;

import cn.gybyt.config.properties.JwtProperties;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * jwt工具类
 *
 * @program: utils
 * @classname: JWTUtil
 * @author: codetiger
 * @create: 2022/11/12 21:52
 **/
@Slf4j
public class JwtUtil {

    private static JwtProperties jwtProperties;

    public static JwtProperties getJwtProperties() {
        if (jwtProperties == null) {
            jwtProperties = SpringUtil.getBean(JwtProperties.class);
        }
        return jwtProperties;
    }

    /**
     * 创建TOKEN
     *
     * @param map
     * @return
     */
    public static String createToken(Map<String, String> map) {
        String token = getJwtProperties().getTokenPrefix() + " ";
        JWTCreator.Builder builder = JWT.create();
        for (String key : map.keySet()) {
            builder.withClaim(key, map.get(key));
        }
        String tokenContent = builder.withExpiresAt(
                                             new Date(System.currentTimeMillis() + getJwtProperties().getExpireTime() * 1000L * 60))
                                     .sign(Algorithm.HMAC512(getJwtProperties().getSecret()));
        token = token + tokenContent;
        return token;
    }

    /**
     * 验证token，验证成功返回用户名
     *
     * @param token
     */
    public static String validateToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC512(getJwtProperties().getSecret()))
                      .build()
                      .verify(token.replace(getJwtProperties().getTokenPrefix() + " ", ""))
                      .getClaims()
                      .get("username")
                      .asString();
        } catch (TokenExpiredException e) {
            throw new BaseException(HttpStatusEnum.UNAUTHORIZED.value(), "token已经过期");
        } catch (Exception e) {
            throw new BaseException(HttpStatusEnum.UNAUTHORIZED.value(), "token验证失败");
        }
    }

    /**
     * 检查token是否需要更新
     *
     * @param token
     * @return
     */
    public static boolean isNeedUpdate(String token) {
        //获取token过期时间
        Date expiresAt;
        try {
            expiresAt = JWT.require(Algorithm.HMAC512(getJwtProperties().getSecret()))
                           .build()
                           .verify(token.replace(getJwtProperties().getTokenPrefix() + " ", ""))
                           .getExpiresAt();
        } catch (TokenExpiredException e) {
            return true;
        } catch (Exception e) {
            throw new BaseException(HttpStatusEnum.UNAUTHORIZED.value(), "token验证失败");
        }
        //如果剩余过期时间少于过期时常的一般时 需要更新
        return (expiresAt.getTime() - System.currentTimeMillis()) < (getJwtProperties().getExpireTime() * 1000L * 60 >> 1);
    }

    /**
     * 获取当前登录用户
     *
     * @param <T>
     * @return
     */
    public static <T> T getLoginUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader(jwtProperties.getHeader());
        token = token.replace(jwtProperties.getTokenPrefix() + " ", "");
        try {
            DecodedJWT jwt = JWT.decode(token);
            Map<String, Claim> claims = jwt.getClaims();
            Claim usernameClaim = claims.get("username");
            String username = usernameClaim.asString();
            T t = CacheUtil.get(jwtProperties.getKeyPrefix() + username);
            if (BaseUtil.isNull(t)) {
                throw new BaseException(HttpStatusEnum.UNAUTHORIZED.value(), "获取用户信息失败");
            }
            return t;
        } catch (JWTDecodeException exception) {
            log.error("解析token失败", exception);
            throw new BaseException(HttpStatusEnum.UNAUTHORIZED.value(), "解析token失败");
        }
    }

}
