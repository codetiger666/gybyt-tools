package cn.gybyt.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 *
 * @program: utils
 * @classname: GybytMybatisProperties
 * @author: codetiger
 * @create: 2023/3/4 11:27
 **/
@Configuration
@Component
@ConfigurationProperties(prefix = "gybyt.cloud")
public class GybytCloudProperties {
    /**
     * 开启feign请求头处理
     */
    private Boolean enableFeignHeaderHandle;

    /**
     * 是否开启nacos注册中心
     */
    private Boolean enableNacosDiscovery;

    public Boolean getEnableFeignHeaderHandle() {
        return enableFeignHeaderHandle;
    }

    public void setEnableFeignHeaderHandle(Boolean enableFeignHeaderHandle) {
        this.enableFeignHeaderHandle = enableFeignHeaderHandle;
    }

    public Boolean getEnableNacosDiscovery() {
        return enableNacosDiscovery;
    }

    public void setEnableNacosDiscovery(Boolean enableNacosDiscovery) {
        this.enableNacosDiscovery = enableNacosDiscovery;
    }
}
