package cn.gybyt.config;

import cn.gybyt.config.properties.GybytDynamicProperties;
import cn.gybyt.config.properties.GybytMybatisProperties;
import cn.gybyt.dynamic.GybytDynamicDataSourceRoute;
import cn.gybyt.util.BaseException;
import cn.gybyt.util.BaseUtil;
import cn.gybyt.util.ReflectUtil;
import cn.gybyt.util.SpringUtil;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 动态数据源配置
 *
 * @program: ApiClumps
 * @classname: GybytDynamicDataSourceConfig
 * @author: codetiger
 * @create: 2023/9/2 16:13
 **/
@Configuration
@Slf4j
public class GybytDynamicDataSourceConfig {

    @Resource
    private GybytDynamicProperties gybytDynamicProperties;
    @Resource
    private GybytMybatisProperties gybytMybatisProperties;
    /**
     * 目标数据源
     */
    private final Map<String, DataSource> targetDataSources = new HashMap<>();

    @PostConstruct
    public void setGybytDynamicDataSource() {
        Map<String, GybytDynamicProperties.DataSourceProperty> dynamic = gybytDynamicProperties.getDataSource();
        Set<String> keySet = dynamic.keySet();
        if (keySet.isEmpty() || !keySet.contains(gybytDynamicProperties.getDynamicMasterDataSource())) {
            throw new BaseException("请设置主数据库");
        }
        dynamic.forEach((k, v) -> {
            if (v.getType() != null && ReflectUtil.isSameType("com.alibaba.druid.pool.DruidDataSource", v.getType())) {
                DruidDataSource druidDataSource = new DruidDataSource();
                druidDataSource.setUrl(v.getUrl());
                druidDataSource.setUsername(v.getUsername());
                druidDataSource.setPassword(v.getPassword());
                druidDataSource.setDriverClassName(v.getDriverClass().getTypeName());
                Map<String, Object> druid = v.getDruid();
                if (druid != null) {
                    druid.forEach((druidK, druidV) -> {
                        ReflectUtil.setFiledValue(druidDataSource, druidK, druidV);
                    });
                }
                String beanName = gybytDynamicProperties.getDynamicBeanNamePrefix() + k;
                targetDataSources.put(beanName, druidDataSource);
                log.info("初始化数据源 {} 成功", k);
                return;
            }
            SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
            dataSource.setDriverClass(v.getDriverClass());
            dataSource.setPassword(v.getPassword());
            dataSource.setUsername(v.getUsername());
            dataSource.setUrl(v.getUrl());
            String beanName = gybytDynamicProperties.getDynamicBeanNamePrefix() + k;
            targetDataSources.put(beanName, dataSource);
            log.info("初始化数据源 {} 成功", k);
        });
    }

    @Bean("dataSource")
    @Primary
    public GybytDynamicDataSourceRoute gybytDynamicDataSourceRoute() {
        return new GybytDynamicDataSourceRoute(targetDataSources, gybytDynamicProperties);
    }

    @ConditionalOnClass({SqlSessionFactory.class, MybatisProperties.class})
    @Primary
    @Bean
    public SqlSessionFactory sqlSessionFactoryBean(GybytDynamicDataSourceRoute gybytDynamicDataSourceRoute, List<Interceptor> interceptors) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(gybytDynamicDataSourceRoute);
        MybatisProperties mybatisProperties = SpringUtil.getBean(MybatisProperties.class);
        try {
            sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(gybytMybatisProperties.getMapperPath()));
        } catch (Exception e) {
            sqlSessionFactoryBean.setMapperLocations();
        }
        if (BaseUtil.isNotEmpty(mybatisProperties.getTypeAliasesPackage())) {
            sqlSessionFactoryBean.setTypeAliasesPackage(mybatisProperties.getTypeAliasesPackage());
        }
        sqlSessionFactoryBean.setConfiguration(mybatisProperties.getConfiguration());
        sqlSessionFactoryBean.setPlugins(interceptors.toArray(new Interceptor[0]));
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 重写事务管理器，管理动态数据源
     */
    @ConditionalOnClass(Transactional.class)
    @Primary
    @Bean
    public PlatformTransactionManager annotationDrivenTransactionManager(GybytDynamicDataSourceRoute gybytDynamicDataSourceRoute) {
        return new DataSourceTransactionManager(gybytDynamicDataSourceRoute);
    }

}
