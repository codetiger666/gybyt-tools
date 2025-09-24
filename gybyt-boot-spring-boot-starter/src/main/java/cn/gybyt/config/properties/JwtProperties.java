package cn.gybyt.config.properties;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * jwt变量提示
 *
 * @program: utils
 * @classname: JWTProperties
 * @author: codetiger
 * @create: 2022/11/14 21:09
 **/
@Configuration
@Component
@Data
@ConditionalOnClass(JWT.class)
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * 请求头变量名
     */
    private String header = "Authorization";

    /**
     * token前缀
     */
    private String tokenPrefix = "Bearer";

    /**
     * 签名密钥
     */
    private String secret = "123456";

    /**
     * 有效期(分钟)
     */
    private Integer expireTime = 30;

    /**
     * 存入redis中的key前缀
     */
    private String keyPrefix = "";
    /**
     * 算法
     */
    private String algorithm = "HMAC256";
    /**
     * 用户名字段
     */
    private String username = "username";

    /**
     * 请求白名单
     */
    private List<String> whiteList = new ArrayList<>();

    public Algorithm getAlgorithm() {
        Algorithm algorithm;
        switch (this.algorithm) {
            case "HMAC256":
                algorithm = Algorithm.HMAC256(this.secret);
                break;
            case "HMAC512":
                algorithm = Algorithm.HMAC512(this.secret);
                break;
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + this.algorithm);
        }
        return algorithm;
    }

}
