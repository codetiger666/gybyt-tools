package cn.gybyt.interceptor;

import cn.gybyt.config.properties.GybytMybatisProperties;
import cn.gybyt.util.BaseUtil;
import cn.gybyt.util.ReflectUtil;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于输出每条 SQL 语句及其执行时间
 *
 * @author codetiger
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = Statement.class),
        @Signature(type = StatementHandler.class, method = "batch", args = Statement.class)
})
public class GybytMybatisSqlLogInterceptor implements Interceptor {

    private final Logger log = LoggerFactory.getLogger(GybytMybatisSqlLogInterceptor.class);

    private Pattern sqlPattern;
    private GybytMybatisProperties gybytMybatisProperties;

    public GybytMybatisSqlLogInterceptor(GybytMybatisProperties gybytMybatisProperties) {
        this.gybytMybatisProperties = gybytMybatisProperties;
        this.sqlPattern = Pattern.compile(String.format(".*(%s)", this.gybytMybatisProperties.getSqlPattern()), Pattern.CASE_INSENSITIVE);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object firstArg = invocation.getArgs()[0];
        Statement statement;
        // 代理对象获取代理对象，不是获取原对象
        if (Proxy.isProxyClass(firstArg.getClass())) {
            statement = (Statement) SystemMetaObject.forObject(firstArg).getValue("h.statement");
        } else {
            statement = (Statement) firstArg;
        }
        String sql = statement.toString();
        // 格式化sql语句
        if (BaseUtil.isNotEmpty(sql)) {
            try {
                sql = sql.replaceAll("\\s+", " ");
                Matcher matcher = this.sqlPattern.matcher(sql);
                matcher.find();
                if (matcher.groupCount() > 0) {
                    sql = matcher.group(1);
                }
            } catch (Exception ignored) {
            }
        }
        // 获取实际sql执行方法
        Object delegate = ReflectUtil.getFieldValueByFieldName(invocation.getTarget(), "delegate");
        MappedStatement mappedStatement = ReflectUtil.getFieldValueByFieldName(delegate, "mappedStatement");
        // 计算执行 SQL 耗时
        long start = System.currentTimeMillis();
        Object result = invocation.proceed();
        long end = System.currentTimeMillis();
        if (BaseUtil.isNotEmpty(sql)) {
            log.info(
                    "\n==============  Sql Start  ==============\nExecute ID  ：{}\nExecute SQL ：{}\nExecute Time：{} ms\n==============  Sql  End   ==============\n\n",
                    BaseUtil.isNotEmpty(mappedStatement) ? Objects.requireNonNull(mappedStatement).getId() : "",
                    sql,
                    end - start
            );
        }
        return result;
    }

}