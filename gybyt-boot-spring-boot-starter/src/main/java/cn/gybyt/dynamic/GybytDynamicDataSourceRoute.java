package cn.gybyt.dynamic;

import cn.gybyt.config.properties.GybytDynamicProperties;
import cn.gybyt.config.properties.GybytProperties;
import cn.gybyt.util.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * 动态数据源路由
 *
 * @program: ApiClumps
 * @classname: GybytDynamicDataSourceConfig
 * @author: codetiger
 * @create: 2023/9/2 15:47
 **/
@Slf4j
public class GybytDynamicDataSourceRoute extends AbstractDataSource {

    /**
     * 目标数据源
     */
    private final Map<String, DataSource> targetDataSources;
    /**
     * 动态数据源配置
     */
    private final GybytDynamicProperties gybytDynamicProperties;

    /**
     * 返回需要使用的数据源的key，将会按照这个KEY从Map获取对应的数据源（切换）
     *
     * @return
     */
    protected String determineCurrentLookupKey() {
        //从ThreadLocal中取出KEY
        return GybytDataSourceHolder.getDataSource();
    }

    /**
     * 构造方法填充Map，构建多数据源
     */
    public GybytDynamicDataSourceRoute(Map<String, DataSource> targetDataSources, GybytDynamicProperties gybytDynamicProperties) {
        this.targetDataSources = targetDataSources;
        this.gybytDynamicProperties = gybytDynamicProperties;
    }

    public DataSource getDataSource() {
        String dataSourceKey = BaseUtil.isEmpty(this.determineCurrentLookupKey()) ? gybytDynamicProperties.getDynamicBeanNamePrefix() + gybytDynamicProperties.getDynamicMasterDataSource() : this.determineCurrentLookupKey();
        DataSource dataSource = targetDataSources.get(dataSourceKey);
        if (BaseUtil.isNull(dataSource)) {
            dataSourceKey = gybytDynamicProperties.getDynamicBeanNamePrefix() + gybytDynamicProperties.getDynamicMasterDataSource();
            dataSource = targetDataSources.get(dataSourceKey);
        }
        log.debug("使用数据源{}", dataSourceKey.replace(gybytDynamicProperties.getDynamicBeanNamePrefix(), ""));
        return dataSource;
    }


    @Override
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getDataSource().getConnection(username, password);
    }
}
