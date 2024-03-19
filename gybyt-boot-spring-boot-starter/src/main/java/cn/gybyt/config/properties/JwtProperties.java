package cn.gybyt.config.properties;

import com.auth0.jwt.JWT;
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
     * 请求白名单
     */
    private List<String> whiteList = new ArrayList<>();

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Integer getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Integer expireTime) {
        this.expireTime = expireTime;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }
}
