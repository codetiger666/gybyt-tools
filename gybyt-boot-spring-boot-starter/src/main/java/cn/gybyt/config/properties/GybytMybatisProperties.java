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
    private String mapperPath = "classpath*:mapper/**/*.xml";
    /**
     * SQL语句匹配规则
     */
    private String sqlPattern = "select|insert|update|delete|replace|merge|with|explain|analyze|create|alter|drop|truncate|rename|comment|grant|revoke|call|exec|execute|set|use|show|describe|desc|commit|rollback|savepoint|release|lock|unlock|start|begin|end|declare|fetch|open|close|cursor|if|while|loop|for|exit|return|optimize|repair|check|load|import|export|copy|into|values|do|signal|resignal|deallocate|flush|reset|kill|help|listen|notify|unlisten|vacuum|attach|detach|purge|connect|disconnect|backup|restore|reindex|cluster";

}
