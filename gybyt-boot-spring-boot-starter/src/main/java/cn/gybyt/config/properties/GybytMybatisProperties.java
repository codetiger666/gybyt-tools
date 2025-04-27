package cn.gybyt.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @program: utils
 * @classname: GybytMybatisProperties
 * @author: codetiger
 * @create: 2023/3/4 11:27
 **/
@Configuration
@Data
@ConfigurationProperties(prefix = "gybyt.mybatis")
public class GybytMybatisProperties {
    /**
     * 开启sql打印
     */
    private Boolean sqlLog;
    /**
     * 开启刷新mapper缓存
     */
    private Boolean enableRefresh;
    /**
     * 刷新间隔
     */
    private Long refreshInterval;
    /**
     * mapper文件位置
     */
    private String mapperPath = "classpath:mapper/*.xml";
    /**
     * SQL语句匹配规则
     */
    private String sqlPattern = "insert|update|select|delete|create|drop|truncate|call|alter";

}
