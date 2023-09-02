package cn.gybyt.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${mybatis.configuration.mapUnderscoreToCamelCase:false}")
    private boolean mapUnderscoreToCamelCase;
    @Value("${mybatis.configuration.defaultFetchSize:100}")
    private Integer defaultFetchSize;
    @Value("${mybatis.configuration.defaultStatementTimeout:30}")
    private Integer defaultStatementTimeout;

    public Boolean getSqlLog() {
        return sqlLog;
    }

    public boolean getMapUnderscoreToCamelCase() {
        return mapUnderscoreToCamelCase;
    }

    public Integer getDefaultFetchSize() {
        return defaultFetchSize;
    }

    public Integer getDefaultStatementTimeout() {
        return defaultStatementTimeout;
    }

    public void setDefaultFetchSize(Integer defaultFetchSize) {
        this.defaultFetchSize = defaultFetchSize;
    }

    public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
        this.defaultStatementTimeout = defaultStatementTimeout;
    }

    public void setSqlLog(Boolean sqlLog) {
        this.sqlLog = sqlLog;
    }

    public String getSqlPattern() {
        return sqlPattern;
    }

    public void setSqlPattern(String sqlPattern) {
        this.sqlPattern = sqlPattern;
    }

    public Boolean getEnableRefresh() {
        return enableRefresh;
    }

    public void setEnableRefresh(Boolean enableRefresh) {
        this.enableRefresh = enableRefresh;
    }

    public Long getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(Long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public String getMapperPath() {
        return mapperPath;
    }

    public void setMapperPath(String mapperPath) {
        this.mapperPath = mapperPath;
    }
}
