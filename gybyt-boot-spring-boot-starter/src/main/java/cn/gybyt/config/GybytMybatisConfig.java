package cn.gybyt.config;

import cn.gybyt.config.properties.GybytMybatisProperties;
import cn.gybyt.interceptor.GybytMybatisSqlLogInterceptor;
import cn.gybyt.plugins.GybytMybatisMapperRefreshPlugin;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * mybatis配置类
 * @program: utils
 * @classname: GybytMybatisConfig
 * @author: codetiger
 * @create: 2023/3/4 11:30
 **/
@Configuration
@ConditionalOnClass(Select.class)
@ConditionalOnWebApplication
public class GybytMybatisConfig {

    @Bean
    @ConditionalOnProperty(prefix = "gybyt.mybatis", name = "sql-log", havingValue = "true")
    public GybytMybatisSqlLogInterceptor gybytMybatisSqlLogInterceptor(GybytMybatisProperties gybytMybatisProperties) {
        return new GybytMybatisSqlLogInterceptor(gybytMybatisProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "gybyt.mybatis", name = "enable-refresh", havingValue = "true")
    public GybytMybatisMapperRefreshPlugin gybytMybatisMapperRefreshPlugin(GybytMybatisProperties gybytMybatisProperties, SqlSessionFactory sqlSessionFactory) {
        GybytMybatisMapperRefreshPlugin gybytMybatisMapperRefreshPlugin = new GybytMybatisMapperRefreshPlugin(gybytMybatisProperties.getMapperPath(), gybytMybatisProperties.getRefreshInterval() == null ? 3L : gybytMybatisProperties.getRefreshInterval(), sqlSessionFactory);
        gybytMybatisMapperRefreshPlugin.init();
        return gybytMybatisMapperRefreshPlugin;
    }
}
