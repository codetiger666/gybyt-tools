package cn.gybyt.interceptor;

import cn.gybyt.config.properties.GybytMybatisProperties;
import cn.gybyt.util.BaseUtil;
import cn.gybyt.util.ReflectUtil;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于输出每条 SQL 语句及其执行时间
 *
 * @author codetiger
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class,
                                                                                 ResultHandler.class}),
             @Signature(type = StatementHandler.class, method = "update", args = Statement.class),
             @Signature(type = StatementHandler.class, method = "batch", args = Statement.class)})
public class GybytMybatisSqlLogInterceptor implements Interceptor {

    private final Logger log = LoggerFactory.getLogger(GybytMybatisSqlLogInterceptor.class);
    private final Pattern sqlPattern;
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    private GybytMybatisProperties gybytMybatisProperties;
    private final String databaseType;
    private final static Map<String, Pattern> PATTERN_MAP = new ConcurrentHashMap<>();

    public GybytMybatisSqlLogInterceptor(GybytMybatisProperties gybytMybatisProperties) {
        this.gybytMybatisProperties = gybytMybatisProperties;
        this.databaseType = gybytMybatisProperties.getDatabaseType() != null ? gybytMybatisProperties.getDatabaseType()
                .toLowerCase() : "mysql";
        this.sqlPattern = Pattern.compile("^.*?((?:" + gybytMybatisProperties.getSqlPattern() + ").*$)",
                                          Pattern.CASE_INSENSITIVE);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 关闭 sql 日志时直接放行，不做任何处理
        if (Boolean.FALSE.equals(gybytMybatisProperties.getSqlLog())) {
            return invocation.proceed();
        }
        // 日志级别未开启时直接放行，不做任何处理
        if (!log.isInfoEnabled()) {
            return invocation.proceed();
        }
        Object target = invocation.getTarget();
        StatementHandler statementHandler = (StatementHandler) target;
        // 提前获取 MappedStatement 用于跳过包匹配，避免后续无用处理
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = null;
        if (metaObject.hasGetter("delegate.mappedStatement")) {
            mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        } else if (metaObject.hasGetter("mappedStatement")) {
            mappedStatement = (MappedStatement) metaObject.getValue("mappedStatement");
        }
        String executeId = Objects.nonNull(mappedStatement) ? mappedStatement.getId() : "";
        // 提前匹配跳过的包，匹配成功则直接放行
        if (BaseUtil.isNotEmpty(executeId)) {
            for (String skipPackage : gybytMybatisProperties.getSkipPackages()) {
                if (getPattern(skipPackage).matcher(executeId)
                        .find()) {
                    return invocation.proceed();
                }
            }
        }
        // 执行 SQL 并计时
        long start = System.currentTimeMillis();
        Object result = invocation.proceed();
        long end = System.currentTimeMillis();
        // 判断是否为批量操作
        boolean isBatch = "batch".equals(invocation.getMethod()
                                                 .getName());
        // 拼装完整 SQL（批量操作跳过参数替换，仅输出 SQL 模板以规避反射开销）
        String sql = isBatch ? buildBatchSql(statementHandler) : buildSql(statementHandler);
        log.info(
                "\n\n==============  Sql Start  ==============\nExecute ID  ：{}\nExecute SQL ：{}\nExecute Time：{} ms\n==============  Sql  End   ==============\n",
                executeId, sql, end - start);
        return result;
    }

    /**
     * 构建批量操作的 SQL 日志，仅输出 SQL 模板，不做参数替换（避免大量反射开销）
     */
    private String buildBatchSql(StatementHandler statementHandler) {
        try {
            BoundSql boundSql = statementHandler.getBoundSql();
            return compactSql(boundSql.getSql()) + " [batch]";
        } catch (Exception e) {
            log.error("sql处理失败", e);
            return "";
        }
    }

    /**
     * 构建可执行 SQL 语句，将参数占位符替换为实际值
     */
    private String buildSql(StatementHandler statementHandler) {
        try {
            BoundSql boundSql = statementHandler.getBoundSql();
            String sql = boundSql.getSql();
            Object parameterObject = boundSql.getParameterObject();
            if (Objects.isNull(parameterObject)) {
                return compactSql(sql);
            }
            if (BaseUtil.isSimpleType(parameterObject)) {
                sql = replaceValues(sql, Collections.singletonList(parameterObject));
            } else {
                sql = replaceParameters(sql, boundSql, parameterObject);
            }
            return compactSql(sql);
        } catch (Exception e) {
            log.error("sql处理失败", e);
            return "";
        }
    }

    /**
     * 替换 SQL 中的参数占位符为实际参数值
     */
    private String replaceParameters(String sql, BoundSql boundSql, Object parameterObject) {
        List<Object> values = new ArrayList<>(boundSql.getParameterMappings()
                .size());
        for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
            try {
                String property = parameterMapping.getProperty();
                Object value;
                if (BaseUtil.isEmpty(property)) {
                    value = boundSql.getParameterObject();
                } else {
                    String[] propertyArray = property.split("\\.");
                    value = parameterMapping;
                    for (int i = 0; i < propertyArray.length; i++) {
                        String key = propertyArray[i];
                        if (i == 0) {
                            if (boundSql.hasAdditionalParameter(key)) {
                                value = boundSql.getAdditionalParameter(key);
                            } else {
                                value = getData(parameterObject, key);
                            }
                        } else {
                            value = getData(value, key);
                        }
                    }
                }
                values.add(value);
            } catch (Exception ignore) {
                values.add(null);
            }
        }
        return replaceValues(sql, values);
    }

    /**
     * 按占位符在原始 SQL 中的位置依次替换参数值，避免参数值本身包含 ? 时被误认为占位符
     */
    private String replaceValues(String sql, List<Object> values) {
        String[] parts = sql.split("\\?", -1);
        StringBuilder sb = new StringBuilder(sql.length());
        for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i]);
            if (i < parts.length - 1) {
                sb.append(i < values.size() ? toStr(values.get(i)) : "?");
            }
        }
        return sb.toString();
    }

    /**
     * 压缩 SQL 中的空白字符，并提取有效 SQL 片段
     */
    private String compactSql(String sql) {
        String compacted = WHITESPACE_PATTERN.matcher(sql)
                .replaceAll(" ");
        Matcher matcher = sqlPattern.matcher(compacted);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return compacted;
    }

    private static Pattern getPattern(String str) {
        return PATTERN_MAP.computeIfAbsent(str, key -> {
            StringBuilder regex = new StringBuilder("^");
            for (int i = 0; i < key.length(); i++) {
                char c = key.charAt(i);
                if (c == '*') {
                    if (i + 1 < key.length() && key.charAt(i + 1) == '*') {
                        regex.append(".*");
                        i++;
                    } else {
                        regex.append("[^.]+");
                    }
                } else if (c == '.') {
                    regex.append("\\.");
                } else {
                    if ("\\[]{}()+-^$|".indexOf(c) >= 0) {
                        regex.append("\\");
                    }
                    regex.append(c);
                }
            }
            regex.append("$");
            return Pattern.compile(regex.toString());
        });
    }

    private Object getData(Object o, String key) {
        if (o instanceof Map) {
            return ((Map<?, ?>) o).get(key);
        }
        return ReflectUtil.getFieldValueByFieldName(o, key);
    }

    private String toStr(Object o) {
        if (o == null) {
            return "null";
        }
        String simpleName = o.getClass()
                .getSimpleName();
        switch (simpleName) {
            case "String":
                return BaseUtil.format("'{}'", o);
            case "Date":
                return formatDateValue(o, "date");
            case "DateTime":
                return formatDateValue(o, "timestamp");
            case "LocalDate":
                return formatDateValue(o, "date");
            case "LocalDateTime":
                return formatDateValue(o, "timestamp");
            default:
                return BaseUtil.format("'{}'", BaseUtil.toStr(o));
        }
    }

    private String formatDateValue(Object o, String type) {
        switch (databaseType) {
            case "mysql":
                return BaseUtil.format("'{}'", o);
            case "oracle":
                return BaseUtil.format("{} '{}'", type.toUpperCase(), o);
            case "postgresql":
            default:
                return BaseUtil.format("{} '{}'", type, o);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 兼容 MyBatis 插件配置入口，当前不依赖外部属性
        if (properties == null) {
            return;
        }
        if (gybytMybatisProperties == null) {
            gybytMybatisProperties = new GybytMybatisProperties();
        }
    }

}
