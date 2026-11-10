package cn.gybyt.config.properties;

import cn.dev33.satoken.config.SaTokenConfig;
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
@ConditionalOnClass(SaTokenConfig.class)
@ConfigurationProperties(prefix = "gybyt.auth")
public class AuthProperty {

    /**
     * 请求头变量名
     */
    private String header = "Authorization";

    /**
     * 有效期(分钟)
     * 默认7天
     */
    private Integer expireTime = 10080;

    /**
     * 冻结时间(分钟)
     * 默认-1 不冻结
     */
    private Integer freezeTime = -1;

    /**
     * jwt密钥
     */
    private String secretKey = "123456";

    /**
     * 请求白名单
     */
    private List<String> whiteList = new ArrayList<>();


}
