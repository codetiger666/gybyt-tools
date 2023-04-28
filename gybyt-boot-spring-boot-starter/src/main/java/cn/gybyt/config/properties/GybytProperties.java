package cn.gybyt.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 工具提示
 *
 * @program: utils
 * @classname: WarpperProperties
 * @author: codetiger
 * @create: 2022/11/14 20:22
 **/
@Configuration
@Component
@ConfigurationProperties(prefix = "gybyt")
public class GybytProperties {
    /**
     * 是否启用包装
     */
    private Boolean warpper;
    /**
     * 是否开启请求处理
     */
    private Boolean enableRequestWarpper = true;
    /**
     * 是否开启缓存支持
     */
    private Boolean enableCache;

    public Boolean getWarpper() {
        return warpper;
    }

    public void setWarpper(Boolean warpper) {
        this.warpper = warpper;
    }

    public Boolean getEnableRequestWarpper() {
        return enableRequestWarpper;
    }

    public void setEnableRequestWarpper(Boolean enableRequestWarpper) {
        this.enableRequestWarpper = enableRequestWarpper;
    }

    public Boolean getEnableCache() {
        return enableCache;
    }

    public void setEnableCache(Boolean enableCache) {
        this.enableCache = enableCache;
    }

}
