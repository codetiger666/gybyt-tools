package cn.gybyt.config.properties;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.Map;

/**
 * 多数据源
 *
 * @program: gybyt-tools
 * @classname: GybytDynamicProperties
 * @author: codetiger
 * @create: 2023/9/2 20:33
 **/
@Configuration
@ConfigurationProperties(prefix = "gybyt.dynamic")
public class GybytDynamicProperties {

    private Map<String, DataSourceProperty> dataSource;
    /**
     * 动态数据源Bean前缀
     */
    private String dynamicBeanNamePrefix = "gybytDataSource";
    /**
     * 默认数据源
     */
    private String dynamicMasterDataSource = "master";

    public Map<String, DataSourceProperty> getDataSource() {
        return dataSource;
    }

    public void setDataSource(Map<String, DataSourceProperty> dataSource) {
        this.dataSource = dataSource;
    }

    public void setDynamicBeanNamePrefix(String dynamicBeanNamePrefix) {
        this.dynamicBeanNamePrefix = dynamicBeanNamePrefix;
    }

    public void setDynamicMasterDataSource(String dynamicMasterDataSource) {
        this.dynamicMasterDataSource = dynamicMasterDataSource;
    }

    public String getDynamicBeanNamePrefix() {
        return dynamicBeanNamePrefix;
    }

    public String getDynamicMasterDataSource() {
        return dynamicMasterDataSource;
    }

    public static class DataSourceProperty {
        /**
         * 数据库连接地址
         */
        private String url;
        /**
         * 数据库连接类型
         */
        private Class<? extends Driver> driverClass;
        /**
         * 数据库用户名
         */
        private String username;
        /**
         * 数据库密码
         */
        private String password;
        /**
         * 连接池类型
         */
        private Class<? extends DataSource> type;

        private Map<String, Object> druid;

        public Map<String, Object> getDruid() {
            return druid;
        }

        public void setDruid(Map<String, Object> druid) {
            this.druid = druid;
        }

        public Class<? extends DataSource> getType() {
            return type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Class<? extends Driver> getDriverClass() {
            return driverClass;
        }

        public void setDriverClass(Class<? extends Driver> driverClass) {
            this.driverClass = driverClass;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setType(Class<? extends DataSource> type) {
            this.type = type;
        }
    }

}
