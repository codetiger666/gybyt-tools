package cn.gybyt.config.properties;

import lombok.Data;
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
    private String sqlPattern = ".*?(insert.*)|.*?(update.*)|.*?(select.*)|.*?(delete.*)|.*?(create.*)|.*?(drop.*)|.*?(truncate.*)";
    /**
     * 驼峰转换
     */
    private boolean mapUnderscoreToCamelCase;
    /**
     * 别名包
     */
    private String typeAliasesPackage;
    private Integer defaultFetchSize;
    private Integer defaultStatementTimeout;

}
