package cn.gybyt.util;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 公共工具类
 *
 * @program: utils
 * @classname: BaseUtil
 * @author: codetiger
 * @create: 2022/11/18 19:07
 **/
public class BaseUtil {

    /**
     * 解决空指针问题,为空指定默认值
     *
     * @param t
     * @param function
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> R executeByFunction(T t, Function<? super T, ? extends R> function, R... defaultResult) {
        if (BaseUtil.isNotNull(t)) {
            return function.apply(t);
        }
        // 判断是否有为空时默认返回
        if (defaultResult.length > 0) {
            return defaultResult[0];
        }
        return null;
    }

    /**
     * 判断是null
     *
     * @param o
     * @return
     */
    public static Boolean isNull(Object o) {
        return Objects.isNull(o);
    }

    /**
     * 判断对象是否有值
     *
     * @param o
     * @return
     */
    public static Boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    /**
     * 判断对象是否无值
     * @param o
     * @return
     */
    public static Boolean isEmpty(Object o) {
        if (Objects.isNull(o)) {
            return true;
        } else if (o instanceof CharSequence) {
            return ((CharSequence) o).length() == 0;
        } else if (Objects.requireNonNull(ReflectUtil.getClass(o)).isArray()) {
            return Array.getLength(o) == 0;
        } else if (o instanceof Collection) {
            return ((Collection<?>) o).isEmpty();
        } else if (o instanceof Map){
            return ((Map<?, ?>) o).isEmpty();
        } else {
            return false;
        }
    }

    /**
     * 判断不是null
     *
     * @param o
     * @return
     */
    public static Boolean isNotNull(Object o) {
        return Objects.nonNull(o);
    }

    /**
     * 集合转map
     * @param dataList
     * @param keyFun
     * @return
     * @param <T>
     */
    public static <T, R> Map<R, T> listToMap(Collection<T> dataList, Function<T, R> keyFun) {
        if (BaseUtil.isEmpty(dataList)) {
            return new HashMap<>(0);
        }
        return dataList.stream().collect(Collectors.toMap(keyFun, Function.identity(), (key1, key2) -> key2));
    }

    /**
     * 字符串转列表
     * @param str 原始字符串
     * @param regex 分隔符
     * @return
     * @param <T>
     */
    public static <T> List<T> toList(CharSequence str, String regex) {
        List<T> dataList = new ArrayList<>();
        if (isEmpty(str)) {
            return dataList;
        }
        if (str instanceof String) {
            for (String s : ((String) str).split(regex)) {
                dataList.add((T) s);
            }
        }
        return dataList;
    }

}
