package cn.gybyt.dynamic;

import cn.gybyt.annotation.SwitchDataSource;
import cn.gybyt.config.properties.GybytDynamicProperties;
import cn.gybyt.util.BaseException;
import cn.gybyt.util.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态数据源切面
 *
 * @program: ApiClumps
 * @classname: GybytDataSourceAspect
 * @author: codetiger
 * @create: 2023/9/2 17:27
 **/
@Aspect
//优先级要设置在事务切面执行之前
@Order(1)
@Component
@ConditionalOnClass(Aspect.class)
@Slf4j
public class GybytDataSourceAspect {

    @Resource
    private GybytDynamicProperties gybytDynamicProperties;
    private final Map<String, String> DATASOURCE_KEY_MAP = new ConcurrentHashMap<>();

    /**
     * 在方法执行之前切换到指定的数据源
     * @param joinPoint
     */
    @SuppressWarnings("unchecked")
    @Before(value = "@annotation(cn.gybyt.annotation.SwitchDataSource) || @within(cn.gybyt.annotation.SwitchDataSource)")
    public void beforeSwitchDataSource(JoinPoint joinPoint) {
        // 因为是对注解进行切面，所以这边无需做过多判定，直接获取注解的值，进行环绕，将数据源设置成远方，然后结束后，清楚当前线程数据源
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        SwitchDataSource switchSource = method.getAnnotation(SwitchDataSource.class);
        if (switchSource == null) {
            switchSource = joinPoint.getTarget().getClass().getAnnotation(SwitchDataSource.class);
            if (switchSource == null) {
                // 获取接口上的注解
                for (Class<?> cls : joinPoint.getClass().getInterfaces()) {
                    switchSource = cls.getAnnotation(SwitchDataSource.class);
                    if (switchSource != null) {
                        break;
                    }
                }
            }
        }
        if (switchSource == null) {
            switchSource = (SwitchDataSource) joinPoint.getSignature().getDeclaringType().getAnnotation(SwitchDataSource.class);
        }
        if (switchSource == null) {
            throw new BaseException("获取动态数据源注解失败");
        }
        DATASOURCE_KEY_MAP.put(this.getKeyName(joinPoint), GybytDataSourceHolder.getDataSource());
        GybytDataSourceHolder.setDataSource(gybytDynamicProperties.getDynamicBeanNamePrefix() + switchSource.value());
    }


    /**
     * 方法执行之后清除掉ThreadLocal中存储的KEY，这样动态数据源会使用默认的数据源
     */
    @After(value = "@annotation(cn.gybyt.annotation.SwitchDataSource) || @within(cn.gybyt.annotation.SwitchDataSource)")
    public void afterSwitchDataSource(JoinPoint joinPoint) {
        String datasourceKey = DATASOURCE_KEY_MAP.get(this.getKeyName(joinPoint));
        if (BaseUtil.isEmpty(datasourceKey)) {
            GybytDataSourceHolder.clearDataSource();
            return;
        }
        GybytDataSourceHolder.setDataSource(datasourceKey);
    }

    /**
     * 生成缓存key
     * @param joinPoint
     * @return
     */
    private String getKeyName(JoinPoint joinPoint) {
        return Thread.currentThread().getName() + joinPoint.toString();
    }

}
