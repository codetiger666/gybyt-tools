package cn.gybyt.util;

import lombok.NonNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

    private static long currentTimeMillis = 0;
    private static final AtomicInteger KEY_INDEX = new AtomicInteger();

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

    /**
     * 生成随机字符串
     *
     * @param length 生成长度
     */
    public static synchronized String genKey(Integer length) {
        if (length == null) {
            return "";
        }
        if (length <= 13) {
            return BaseUtil.genRandomString(length);
        }
        long current = System.currentTimeMillis();
        if (current == currentTimeMillis) {
            int index = KEY_INDEX.getAndIncrement();
            String indexStr = String.valueOf(index);
            if ((indexStr.length() + 13) > length) {
                return BaseUtil.genRandomString(length);
            }
            return current + BaseUtil.suppleExtentString(index, length - 13);
        }
        KEY_INDEX.set(0);
        currentTimeMillis = current;
        return current + BaseUtil.suppleExtentString(0, length - 13);
    }

    /**
     * 数字转字符串，补全位数
     * @param number
     * @param length
     * @return
     */
    public static String suppleExtentString(Number number, Integer length) {
        if (BaseUtil.isNull(number)) {
            return "";
        }
        String numberStr = String.valueOf(number);
        if (BaseUtil.isNull(length) || numberStr.length() == length) {
            return numberStr;
        }
        if (numberStr.length() > length) {
            throw new BaseException("生成长度大于原数字位数");
        }
        StringBuilder numberStrBuilder = new StringBuilder();
        for (int i = 0; i < length - numberStr.length(); i++) {
            numberStrBuilder.append("0");
        }
        numberStrBuilder.append(numberStr);
        return numberStrBuilder.toString();
    }

    /**
     * 生成随机字符串
     * @param length
     * @return
     */
    public static String genRandomString(@NonNull Integer length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            strBuilder.append(chars.charAt(index));
        }
        return strBuilder.toString();
    }

}
