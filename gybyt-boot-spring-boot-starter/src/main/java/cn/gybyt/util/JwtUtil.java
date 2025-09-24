package cn.gybyt.util;

import cn.gybyt.config.properties.JwtProperties;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
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

    private static final JwtProperties JWT_PROPERTIES = SpringUtil.getBean(JwtProperties.class);

    /**
     * 创建TOKEN
     *
     * @param o 对象
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String createToken(Object o) {
        String token = JWT_PROPERTIES.getTokenPrefix() + " ";
        JWTCreator.Builder builder = JWT.create();
        if (BaseUtil.isEmpty(o)) {
            throw new BaseException(HttpStatusEnum.UNAUTHORIZED.value(), "创建token失败，参数不能为空");
        }
        if (o instanceof Map) {
            Map<String, String> map = (Map<String, String>) o;
            if (BaseUtil.isEmpty(map.get(JWT_PROPERTIES.getUsername()))) {
                throw new BaseException(HttpStatusEnum.UNAUTHORIZED.value(), "创建token失败，用户名不能为空");
            }
            for (String key : map.keySet()) {
                builder.withClaim(key, map.get(key));
            }
        } else if (o instanceof String) {
            builder.withClaim(JWT_PROPERTIES.getUsername(), (String) o);
        } else {
            Map<String, Object> jsonMap = JsonUtil.parseObject(JsonUtil.toJson(o),
                                                               new TypeUtil<Map<String, Object>>() {
                                                               });
            if (BaseUtil.isEmpty(jsonMap.get(JWT_PROPERTIES.getUsername()))) {
                throw new BaseException(HttpStatusEnum.UNAUTHORIZED.value(), "创建token失败，用户名不能为空");
            }
            for (String key : jsonMap.keySet()) {
                builder.withClaim(key, jsonMap.get(key).toString());
            }
        }

        String tokenContent = builder.withExpiresAt(
                                             new Date(System.currentTimeMillis() + JWT_PROPERTIES.getExpireTime() * 1000L * 60))
                                     .sign(JWT_PROPERTIES.getAlgorithm());
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
            return JWT.require(JWT_PROPERTIES.getAlgorithm())
                      .build()
                      .verify(token.replace(JWT_PROPERTIES.getTokenPrefix() + " ", ""))
                      .getClaims()
                      .get(JWT_PROPERTIES.getUsername())
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
            expiresAt = JWT.require(JWT_PROPERTIES.getAlgorithm())
                           .build()
                           .verify(token.replace(JWT_PROPERTIES.getTokenPrefix() + " ", ""))
                           .getExpiresAt();
        } catch (TokenExpiredException e) {
            return true;
        } catch (Exception e) {
            throw new BaseException(HttpStatusEnum.UNAUTHORIZED.value(), "token验证失败");
        }
        //如果剩余过期时间少于过期时常的一般时 需要更新
        return (expiresAt.getTime() - System.currentTimeMillis()) < (JWT_PROPERTIES.getExpireTime() * 1000L * 60 >> 1);
    }

    /**
     * 获取token信息
     *
     * @return token信息
     */
    public static Map<String, String> getClaims() {
        String token = SpringUtil.getRequestHeader(JWT_PROPERTIES.getHeader());
        token = token.replace(JWT_PROPERTIES.getTokenPrefix() + " ", "");
        try {
            DecodedJWT jwt = JWT.decode(token);
            Map<String, Claim> claims = jwt.getClaims();
            Map<String, String> dataMap = new HashMap<>();
            claims.forEach((claimName, claimValue) -> {
                dataMap.put(claimName, claimValue.asString());
            });
            return dataMap;
        } catch (JWTDecodeException exception) {
            log.error("解析token失败", exception);
            throw new BaseException(HttpStatusEnum.UNAUTHORIZED.value(), "解析token失败");
        }
    }

}
